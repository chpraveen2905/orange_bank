package com.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.CommonApiResponse;
import com.bank.dto.RegisterUserRequestDto;
import com.bank.dto.UserListResponseDto;
import com.bank.dto.UserLoginRequest;
import com.bank.dto.UserLoginResponse;
import com.bank.dto.UserStatusUpdateRequestDto;
import com.bank.resources.UserResource;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

	@Autowired
	public UserResource userResource;
	
	@Operation(summary = "API to Register ADMIN")
	@PostMapping("/admin/register")
	public ResponseEntity<CommonApiResponse> registerAdmin(@RequestBody RegisterUserRequestDto request){
		return this.userResource.registerAdmin(request);
	}
	
	@Operation(summary = "login")
	@PostMapping("/login")
	public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest){
		return userResource.login(userLoginRequest);
	}
	
	@Operation(summary = "API to Register User")
	@PostMapping("/register")
	public ResponseEntity<CommonApiResponse> registerUser(@RequestBody RegisterUserRequestDto request){
		return this.userResource.registerUser(request);
	}
	@GetMapping("/fetch/role")
	@Operation(summary =  "Api to get Users By Role")
	public ResponseEntity<UserListResponseDto> fetchAllBankUsers(@RequestParam("role") String role) {
		return userResource.getUsersByRole(role);
	}
	
	@GetMapping("/fetch/bank/managers")
	@Operation(summary =  "Api to get Bank Managers who is not assigned to any other Bank")
	public ResponseEntity<UserListResponseDto> fetchBankManagers() {
		return userResource.fetchBankManagers();
	}
	
	@PostMapping("update/status")
	@Operation(summary =  "Api to update the user status")
	public ResponseEntity<CommonApiResponse> updateUserStatus(@RequestBody UserStatusUpdateRequestDto request) {
		return userResource.updateUserStatus(request);
	}
	
	@GetMapping("/bank/customers")
	@Operation(summary =  "Api to get Bank Customers by bank id")
	public ResponseEntity<UserListResponseDto> fetchAllBankCustomersByBankId(@RequestParam("bankId") int bankId) {
		return userResource.fetchBankCustomerByBankId(bankId);
	}
	
	@GetMapping("/bank/customer/search")
	@Operation(summary =  "Api to get Bank Customers by bank id")
	public ResponseEntity<UserListResponseDto> searchBankCustomer(@RequestParam("bankId") int bankId, @RequestParam("customerName") String customerName) {
		return userResource.searchBankCustomer(bankId, customerName);
	}
	
	@GetMapping("/all/customer/search")
	@Operation(summary =  "Api to get all Bank Customers by customer name")
	public ResponseEntity<UserListResponseDto> searchBankCustomer(@RequestParam("customerName") String customerName) {
		return userResource.searchCustomer(customerName);
	}
}
