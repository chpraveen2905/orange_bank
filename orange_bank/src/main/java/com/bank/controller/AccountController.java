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

import com.bank.dto.AccountResponseDto;
import com.bank.dto.AccountStatusUpdateRequestDto;
import com.bank.dto.AddAccountRequestDto;
import com.bank.dto.CommonApiResponse;
import com.bank.resources.AccountResource;


import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/bank/account")
@CrossOrigin
public class AccountController {

	@Autowired
	private AccountResource accountResource;

	@PostMapping("add")
	@Operation(summary = "API to Add Account")
	public ResponseEntity<CommonApiResponse> addAccount(@RequestBody AddAccountRequestDto request) {
		return this.accountResource.addAccount(request);
	}

	@GetMapping("fetch/all")
	@Operation(summary = "API to fetch all Accounts")
	public ResponseEntity<AccountResponseDto> fetchAllAccounts() {
		return this.accountResource.fetchAllAccounts();
	}

	@GetMapping("fetch/bankwise")
	@Operation(summary = "API to fetch Account using Bank Id")
	public ResponseEntity<AccountResponseDto> fetchAccountByBankId(@RequestParam("bankId") int bankId) {
		return this.accountResource.fetchAccountsByBankId(bankId);
	}

	@GetMapping("fetch/id")
	@Operation(summary = "API to fetch Account using Account Id")
	public ResponseEntity<AccountResponseDto> fetchAccountByAccountId(@RequestParam("accountId") int accountId) {
		return this.accountResource.fetchAccountByAccountId(accountId);
	}

	@GetMapping("fetch/user")
	@Operation(summary = "API to fetch Account using User Id")
	public ResponseEntity<AccountResponseDto> fetchAccountByUserId(@RequestParam("userId") int userId) {
		return this.accountResource.fetchAccountByUserId(userId);
	}

	@GetMapping("search")
	@Operation(summary = "Api to search bank accounts by bankId and account number")
	public ResponseEntity<AccountResponseDto> searchBankBy(@RequestParam("bankId") int bankId,
			@RequestParam("accountNumber") String accountNumber) {
		return this.accountResource.searchAccounts(accountNumber, bankId);
	}

	@PostMapping("update/status")
	@Operation(summary = "Api to update the bank account status")
	public ResponseEntity<CommonApiResponse> updateAccountStatus(@RequestBody AccountStatusUpdateRequestDto request) {
		return this.accountResource.updateAccountStatus(request);
	}

	@GetMapping("search/all")
	@Operation(summary = "Api to search bank accounts by account no")
	public ResponseEntity<AccountResponseDto> searchBankBy(@RequestParam("accountNumber") String accountNumber) {
		return this.accountResource.searchAccounts(accountNumber);
	}
}
