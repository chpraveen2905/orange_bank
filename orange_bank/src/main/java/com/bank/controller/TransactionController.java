package com.bank.controller;

import java.io.IOException;

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
import com.bank.dto.TransactionRequestDto;
import com.bank.dto.TransactionResponseDto;
import com.bank.resources.TransactionResource;
import com.lowagie.text.DocumentException;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/bank/transaction")
@CrossOrigin
public class TransactionController {

	@Autowired
	private TransactionResource transactionResource;

	@PostMapping("deposit")
	@Operation(summary = "API for Transaction Deposit")
	public ResponseEntity<CommonApiResponse> depositTransaction(
			@RequestBody TransactionRequestDto request)throws Exception {
		return this.transactionResource.depositAmountTxn(request);
	}
	
	@PostMapping("withdraw")
	@Operation(summary = "API for WithDraw Transaction")
	public ResponseEntity<CommonApiResponse> withDrawTransaction(@RequestBody TransactionRequestDto request){
		return this.transactionResource.withdrawTxn(request);
	}
	
	@PostMapping("account/transfer")
	@Operation(summary = "API for Account Transfer")
	public ResponseEntity<CommonApiResponse> accountTransferTransaction(@RequestBody TransactionRequestDto request){
		return this.transactionResource.accountTransfer(request);
	}


	@GetMapping("history")
	@Operation(summary =  "Api for fetch bank transaction history")
	public ResponseEntity<TransactionResponseDto> getTransactionHistory(
			@RequestParam("userId") int userId) {
		return this.transactionResource.bankTransactionHistory(userId);
	}

	@GetMapping("all")
	@Operation(summary =  "Api for fetch bank transaction history")
	public ResponseEntity<TransactionResponseDto> getAllBankCustomerTransactions() {
		return this.transactionResource.allBankCustomerTransactions();
	}

	@GetMapping("customer/fetch")
	@Operation(summary =  "Api for fetch bank transaction history")
	public ResponseEntity<TransactionResponseDto> getBankCustomerTransaction(@RequestParam("bankId") int bankId,
			@RequestParam("accountNo") String accountNo) {
		return this.transactionResource.getBankCustomerTransaction(bankId, accountNo);
	}

	@GetMapping("customer/fetch/timerange")
	@Operation(summary =  "Api for fetch bank customer transaction history by time range")
	public ResponseEntity<TransactionResponseDto> getBankCustomerTransactionByTimeRange(
			@RequestParam("bankId") int bankId, @RequestParam("accountNo") String accountNo,
			@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
		return this.transactionResource.getBankCustomerTransactionByTimeRange(bankId, accountNo, startTime,
				endTime);
	}

	@GetMapping("all/customer/fetch/timerange")
	@Operation(summary =  "Api for fetch bank all customer transaction history")
	public ResponseEntity<TransactionResponseDto> getBankAllCustomerTransactionsByTimeRange(
			@RequestParam("bankId") int bankId, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime) {
		return this.transactionResource.getBankAllCustomerTransactionByTimeRange(bankId, startTime, endTime);
	}

	@GetMapping("all/customer/fetch")
	@Operation(summary =  "Api for fetch bank all customer tranctions")
	public ResponseEntity<TransactionResponseDto> getBankAllCustomerTransaction(
			@RequestParam("bankId") int bankId) {
		return this.transactionResource.getBankAllCustomerTransaction(bankId);
	}

	@GetMapping("history/timerange")
	@Operation(summary =  "Api for fetch customer transactions by time range")
	public ResponseEntity<TransactionResponseDto> getCustomerTransactionsByTimeRange(
			@RequestParam("userId") int userId, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime) {
		return this.transactionResource.bankTransactionHistoryByTimeRange(userId, startTime, endTime);
	}

	@GetMapping("statement/download")
	@Operation(summary =  "Api for downloading the Bank Statement using account Id")
	public void downloadBankStatement(@RequestParam("accountId") int accountId,
			@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
			HttpServletResponse response) throws DocumentException, IOException {
		this.transactionResource.downloadBankStatement(accountId, startTime, endTime, response);
	}

}
