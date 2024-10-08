package com.bank.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.bank.dto.AccountResponseDto;
import com.bank.dto.AccountStatusUpdateRequestDto;
import com.bank.dto.AddAccountRequestDto;
import com.bank.dto.CommonApiResponse;
import com.bank.entity.Account;
import com.bank.entity.Bank;
import com.bank.entity.User;
import com.bank.service.AccountService;
import com.bank.service.BankService;
import com.bank.service.UserService;
import com.bank.utility.Constants.BankAccountStatus;
import com.bank.utility.Constants.IsAccountLinked;

import io.jsonwebtoken.lang.Collections;

@Component
public class AccountResource {

	private final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

	@Autowired
	private UserService userService;
	@Autowired
	private BankService bankService;
	@Autowired
	private AccountService accountService;

	public ResponseEntity<CommonApiResponse> addAccount(AddAccountRequestDto request) {
		LOG.info("Received request to Add Account");
		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getUserId() <= 0 || request.getBankId() <= 0) {
			response.setResponseMessage("invalid Data - Bad Request");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		Account account = AddAccountRequestDto.toAccountEntity(request);
		account.setBalance(BigDecimal.ZERO);
		account.setCreationDate(
				String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		account.setStatus(BankAccountStatus.OPEN.value());

		Bank bank = this.bankService.getBankById(request.getBankId());
		if (bank == null) {
			response.setResponseMessage("Hey, bank Not Found with provided " + request.getBankId());
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		account.setBank(bank);

		User user = this.userService.getUserById(request.getUserId());
		if (user == null) {
			response.setResponseMessage("Hey, User Not Found with provided " + request.getUserId());
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		account.setUser(user);

		Account createdAccount = this.accountService.addAccount(account);
		if (createdAccount != null) {
			user.setIsAccountLinked(IsAccountLinked.YES.value());
			this.userService.updateUser(user);
			response.setResponseMessage("Account Created Successfully");
			response.setSuccess(true);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.CREATED);
		} else {
			response.setResponseMessage("Failed to create Account");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.CREATED);
		}

	}

	public ResponseEntity<AccountResponseDto> fetchAllAccounts() {
		LOG.info("Request Recieved to Fetch All Accounts");
		AccountResponseDto response = new AccountResponseDto();
		response.setAccounts(this.accountService.getAllAccounts());
		response.setResponseMessage("Accounts Fetched successfully");
		response.setSuccess(true);
		return new ResponseEntity<AccountResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<AccountResponseDto> fetchAccountsByBankId(int bankId) {
		LOG.info("Request Received to fetch account using Bank Id");
		AccountResponseDto response = new AccountResponseDto();
		if (bankId <= 0) {
			response.setResponseMessage("Invalid Data");
			response.setSuccess(true);
			return new ResponseEntity<AccountResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		List<Account> accounts = this.accountService.getByBankId(bankId);
		if (Collections.isEmpty(accounts)) {
			response.setResponseMessage("Accounts Not found with provided Bank Id");
			response.setSuccess(true);
			return new ResponseEntity<AccountResponseDto>(response, HttpStatus.OK);
		}
		response.setAccounts(accounts);
		response.setResponseMessage("Accounts Fetched Successfully by using Bank Id");
		response.setSuccess(true);
		return new ResponseEntity<AccountResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<AccountResponseDto> fetchAccountByAccountId(int accountId) {
		LOG.info("Request Received to fetch account using Account Id");
		AccountResponseDto response = new AccountResponseDto();
		if (accountId <= 0) {
			response.setResponseMessage("Invalid Account Id");
			response.setSuccess(true);
			return new ResponseEntity<AccountResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		Account account = this.accountService.getAccountById(accountId);
		if (account == null) {
			response.setResponseMessage("Account Not Found");
			response.setSuccess(false);
			return new ResponseEntity<AccountResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		List<Account> accounts = new ArrayList<>();
		accounts.add(account);
		response.setAccounts(accounts);
		response.setResponseMessage("Accounts Fetched Successfully by using Bank Id");
		response.setSuccess(true);
		return new ResponseEntity<AccountResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<AccountResponseDto> fetchAccountByUserId(int userId) {
		AccountResponseDto response = new AccountResponseDto();
		if (userId <= 0) {
			response.setResponseMessage("Invalid User Id");
			response.setSuccess(false);
			return new ResponseEntity<AccountResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		Account account = this.accountService.getAccountByUserId(userId);
		if (account == null) {
			response.setResponseMessage("Account Not Found with user Id");
			response.setSuccess(false);
			return new ResponseEntity<AccountResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		List<Account> accounts = new ArrayList<>();
		accounts.add(account);
		response.setAccounts(accounts);
		response.setResponseMessage("Account fetched succesfully using userId");
		response.setSuccess(true);
		return new ResponseEntity<AccountResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<AccountResponseDto> searchAccounts(String accNumber, int bankId) {
		LOG.info("Request Received to search Accounts");
		AccountResponseDto response = new AccountResponseDto();
		if (accNumber == null || bankId <= 0) {
			response.setResponseMessage("Invalid Data");
			response.setSuccess(false);
			return new ResponseEntity<AccountResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		List<Account> accounts = accountService.getAccountByNumberContainingIgnoreCaseAndBankId(accNumber, bankId);
		if (Collections.isEmpty(accounts)) {
			response.setResponseMessage("Account Not found");
			response.setSuccess(false);
			return new ResponseEntity<AccountResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		response.setAccounts(accounts);
		response.setResponseMessage("Accounts Fetched Successfully using account number and Bank Id");
		response.setSuccess(true);
		return new ResponseEntity<AccountResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateAccountStatus(AccountStatusUpdateRequestDto request) {
		LOG.info("Request Received for Update Account Status");
		CommonApiResponse response = new CommonApiResponse();
		if (request == null || request.getAccountId() <= 0 || request.getStatus() == null) {
			response.setResponseMessage("Invalid Bad Request");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		Account account = this.accountService.getAccountById(request.getAccountId());
		if (account == null) {
			response.setResponseMessage("Account Not Found");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		account.setStatus(request.getStatus());
		Account updatedAccount = this.accountService.updateAccount(account);
		if (updatedAccount != null) {
			response.setResponseMessage("Account updated Successfully");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
		} else {
			response.setResponseMessage("Failed to update the account");
			response.setSuccess(true);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<AccountResponseDto> searchAccounts(String accNumber) {
		LOG.info("Request Received to Search Accounts using account number");
		AccountResponseDto response = new AccountResponseDto();
		if (accNumber == null) {
			response.setResponseMessage("Invalid Bad Request");
			response.setSuccess(false);
			return new ResponseEntity<AccountResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Account> accounts = this.accountService.getAccountByNumberContainingIgnoreCase(accNumber);
		if (Collections.isEmpty(accounts)) {
			response.setResponseMessage("Accounts Not Found");
			response.setSuccess(false);
			return new ResponseEntity<AccountResponseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.setAccounts(accounts);
		response.setResponseMessage("Accounts Fetched Successfully using account number");
		response.setSuccess(true);
		return new ResponseEntity<AccountResponseDto>(response, HttpStatus.OK);
	}
}
