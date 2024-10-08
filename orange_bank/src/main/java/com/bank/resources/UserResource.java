package com.bank.resources;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bank.config.CustomUserDetailsService;
import com.bank.dto.CommonApiResponse;
import com.bank.dto.RegisterUserRequestDto;
import com.bank.dto.UserListResponseDto;
import com.bank.dto.UserLoginRequest;
import com.bank.dto.UserLoginResponse;
import com.bank.dto.UserStatusUpdateRequestDto;
import com.bank.entity.Bank;
import com.bank.entity.User;
import com.bank.service.BankService;
import com.bank.service.UserService;
import com.bank.service.impl.JwtService;
import com.bank.utility.Constants.IsAccountLinked;
import com.bank.utility.Constants.UserRole;
import com.bank.utility.Constants.UserStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UserResource {
	private final Logger LOG = LoggerFactory.getLogger(UserResource.class);

	@Autowired
	private UserService userService;
	@Autowired
	private BankService bankService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private JwtService jwtService;

	private ObjectMapper objectMapper = new ObjectMapper();

	public ResponseEntity<CommonApiResponse> registerAdmin(RegisterUserRequestDto request) {
		CommonApiResponse response = new CommonApiResponse();
		if (request == null) {
			response.setResponseMessage("Bad Input");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		if (request.getEmail() == null || request.getPassword() == null) {
			response.setResponseMessage("Bad Input");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		User existingUser = this.userService.getUserByEmail(request.getEmail());
		if (existingUser != null) {
			response.setResponseMessage("User Already Registered with this Email Id");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		User newUser = new User();
		newUser.setEmail(request.getEmail());
		newUser.setPassword(passwordEncoder.encode(request.getPassword()));

		newUser.setRoles(UserRole.ROLE_ADMIN.value());
		newUser.setStatus(UserStatus.ACTIVE.value());
		existingUser = this.userService.registerUser(newUser);
		if (existingUser == null) {
			response.setResponseMessage("Failed to Register as ADMIN");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		response.setResponseMessage("Admin Registered Successfully");
		response.setSuccess(true);
		// Convert the object to a JSON string
		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(jsonString);
		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.CREATED);
	}

	public ResponseEntity<UserLoginResponse> login(UserLoginRequest loginRequest) {
		UserLoginResponse response = new UserLoginResponse();
		if (loginRequest == null) {
			response.setResponseMessage("Missing Input");
			response.setSuccess(false);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String jwtToken = null;
		User user = null;

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmailId(), loginRequest.getPassword()));
		} catch (Exception ex) {
			response.setResponseMessage("Invalid email or password.");
			response.setSuccess(true);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmailId());
		user = this.userService.getUserByEmail(loginRequest.getEmailId());

		if (!user.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.value())) {
			response.setResponseMessage("Failed to LOGIN");
			response.setSuccess(false);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}
		for (GrantedAuthority grantedAuthory : userDetails.getAuthorities()) {
			if (grantedAuthory.getAuthority().equals(loginRequest.getRole())) {
				jwtToken = jwtService.generateToken(userDetails.getUsername());
			}
		}

		// user is authenticated
		if (jwtToken != null) {
			response.setUser(user);
			response.setResponseMessage("Logged in sucessful");
			response.setSuccess(true);
			response.setJwtToken(jwtToken);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.OK);
		}

		else {
			response.setResponseMessage("Failed to login");
			response.setSuccess(true);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

	}

	public ResponseEntity<CommonApiResponse> registerUser(RegisterUserRequestDto request) {
		LOG.info("Received request for register user");
		CommonApiResponse response = new CommonApiResponse();
		if (request == null) {
			response.setResponseMessage("Bad Input - USER is NULL");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		User existingUser = this.userService.getUserByEmail(request.getEmail());
		if (existingUser != null) {
			response.setResponseMessage("User Already Registered with this Email Id");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		if (request.getRoles() == null) {
			response.setResponseMessage("Bad Request - User Role is Missing");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User newUser = RegisterUserRequestDto.toUserEntity(request);
		Bank bank = null;
		if (request.getRoles().equals(UserRole.ROLE_CUSTOMER.value())) {
			if (request.getBankId() <= 0) {
				response.setResponseMessage("Bad Request - Bank Id is Missing");
				response.setSuccess(false);
				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
			}
			bank = this.bankService.getBankById(request.getBankId());
			if (bank == null) {
				response.setResponseMessage("Bad Request - Invalid Bank Id");
				response.setSuccess(false);
				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
			}
			newUser.setBank(bank);
			newUser.setIsAccountLinked(IsAccountLinked.NO.value());
		}
		newUser.setStatus(UserStatus.ACTIVE.value());
		newUser.setPassword(passwordEncoder.encode(request.getPassword()));
		existingUser = this.userService.registerUser(newUser);
		if (existingUser == null) {
			response.setResponseMessage("Failed to Register as USER");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		response.setResponseMessage("User Registered Successfully");
		response.setSuccess(true);
		// Convert the object to a JSON string
		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(jsonString);
		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.CREATED);

	}

	public ResponseEntity<UserListResponseDto> getUsersByRole(String role) {
		UserListResponseDto response = new UserListResponseDto();
		List<User> users = this.userService.getUserByRoles(role);
		if (!users.isEmpty()) {
			response.setUsers(users);
		}
		response.setResponseMessage("Users Fetched Successfully");
		response.setSuccess(true);
		// Convert the object to a JSON string
		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<UserListResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserListResponseDto> fetchBankManagers() {
		UserListResponseDto response = new UserListResponseDto();
		List<User> bankManagers = this.userService.getUsersByRolesAndStatusAndBankIsNull(UserRole.ROLE_BANK.value(),
				UserStatus.ACTIVE.value());
		if (!bankManagers.isEmpty()) {
			response.setUsers(bankManagers);
		}
		response.setResponseMessage("Bank Managers Fetched Successfully");
		response.setSuccess(true);
		return new ResponseEntity<UserListResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateUserStatus(UserStatusUpdateRequestDto request) {
		LOG.info("Request Received for updating the status");
		CommonApiResponse response = new CommonApiResponse();
		if (request == null || request.getUserId() <= 0) {
			response.setResponseMessage("Bad Request - Input data is Missing");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		User existingUser = this.userService.getUserById(request.getUserId());
		existingUser.setStatus(request.getStatus());
		User updatedUser = this.userService.updateUser(existingUser);
		if (updatedUser != null) {
			response.setSuccess(true);
			response.setResponseMessage("User updated successfully - " + request.getUserId());
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
		} else {
			response.setSuccess(false);
			response.setResponseMessage("Failed to Update the " + request.getUserId() + "the User");
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<UserListResponseDto> fetchBankCustomerByBankId(int bankId) {
		UserListResponseDto response = new UserListResponseDto();
		List<User> customers = this.userService.getUserByRolesAndBank(UserRole.ROLE_CUSTOMER.value(), bankId);
		if (!customers.isEmpty()) {
			response.setUsers(customers);
		}
		response.setResponseMessage("Customers Fetched Successfully");
		response.setSuccess(true);
		return new ResponseEntity<UserListResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserListResponseDto> searchBankCustomer(int bankId, String customerName) {
		UserListResponseDto response = new UserListResponseDto();
		List<User> users = this.userService.searchBankCustomerByNameAndBankAndRole(customerName, bankId,
				UserRole.ROLE_CUSTOMER.value());
		if (!users.isEmpty()) {
			response.setUsers(users);
		}
		response.setResponseMessage("Customers Fetched Successfully");
		response.setSuccess(true);
		return new ResponseEntity<UserListResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserListResponseDto> searchCustomer(String customerName) {
		UserListResponseDto response = new UserListResponseDto();
		List<User> customers = this.userService.searchBankCustomerByNameAndRole(customerName, UserRole.ROLE_CUSTOMER.value());
		if(!customers.isEmpty()) {
			response.setUsers(customers);
		}
		response.setResponseMessage("Customers Fetched Successfully");
		response.setSuccess(true);
		return new ResponseEntity<UserListResponseDto>(response, HttpStatus.OK);
	}
}
