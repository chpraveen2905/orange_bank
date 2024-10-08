package com.bank.resources;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.bank.dto.BankDetailsResponseDto;
import com.bank.dto.CommonApiResponse;
import com.bank.dto.RegisterBankRequestDto;
import com.bank.entity.Bank;
import com.bank.entity.User;
import com.bank.service.BankService;
import com.bank.service.UserService;
import com.bank.utility.Constants.UserRole;

import jakarta.transaction.Transactional;

@Component
public class BankResource {
	private final Logger LOG = LoggerFactory.getLogger(BankResource.class);
	@Autowired
	private BankService bankService;

	@Autowired
	private UserService userService;

	@Transactional
	public ResponseEntity<CommonApiResponse> registerBank(RegisterBankRequestDto request) {
		LOG.info("Received Request for Register bank");
		CommonApiResponse response = new CommonApiResponse();
		if (request == null) {
			response.setResponseMessage("Bad Request - Missing Data");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		if (request.getUserId() <= 0) {
			response.setResponseMessage("Bad Request - User Not Selected");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		User bankUser = this.userService.getUserById(request.getUserId());
		if (bankUser == null || !bankUser.getRoles().equals(UserRole.ROLE_BANK.value())) {
			response.setResponseMessage("Bad Request, Selected bank is Not Bank User");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		Bank bank = RegisterBankRequestDto.toBankEntity(request);
		Bank registeredBank = this.bankService.addBank(bank);
		if (registeredBank == null) {
			response.setResponseMessage("Failed to Register Bank");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		bankUser.setBank(registeredBank);
		User updatedBankUser = this.userService.updateUser(bankUser);
		if (updatedBankUser == null) {
			response.setResponseMessage("Failed to update Bank detail to USER DB");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.setResponseMessage("Bank registered successfully");
		response.setSuccess(true);
		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.CREATED);
	}

	public ResponseEntity<BankDetailsResponseDto> fetchAllBanks() {
		LOG.info("request received for fetching all banks");
		BankDetailsResponseDto response = new BankDetailsResponseDto();
		List<Bank> banks = this.bankService.getAllBank();
		response.setBanks(banks);
		response.setResponseMessage("Successfully fetched Banks");
		response.setSuccess(true);
		return new ResponseEntity<BankDetailsResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<BankDetailsResponseDto> fetchBankById(int id) {
		BankDetailsResponseDto response = new BankDetailsResponseDto();
		if (id <= 0) {
			response.setResponseMessage("Invalid Data");
			response.setSuccess(false);
			return new ResponseEntity<BankDetailsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		Bank bank = this.bankService.getBankById(id);
		List<Bank> banks = new ArrayList<>();
		banks.add(bank);
		response.setBanks(banks);
		response.setResponseMessage("Bank Details Fetch Successfully");
		response.setSuccess(true);
		return new ResponseEntity<BankDetailsResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<BankDetailsResponseDto> fetchBankByUserId(int userId) {
		LOG.info("Request Received for fetching bank using UserID");
		BankDetailsResponseDto response = new BankDetailsResponseDto();

		if (userId <= 0) {
			response.setResponseMessage("Invalid Data");
			response.setSuccess(false);
			return new ResponseEntity<BankDetailsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		User user = this.userService.getUserById(userId);

		if (user == null || !user.getRoles().equals(UserRole.ROLE_BANK.value())) {
			response.setResponseMessage("Bad Request, user null or Not bank user");
			response.setSuccess(false);
			return new ResponseEntity<BankDetailsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		Bank bank = this.bankService.getBankById(user.getBank().getId());
		if (bank == null) {
			response.setResponseMessage("Bank Not Found in DB");
			response.setSuccess(false);
			return new ResponseEntity<BankDetailsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		List<Bank> banks = new ArrayList<>();
		banks.add(bank);
		response.setBanks(banks);
		response.setResponseMessage("banks fetch successfully");
		response.setSuccess(true);
		return new ResponseEntity<BankDetailsResponseDto>(response, HttpStatus.OK);
	}
}
