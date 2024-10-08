package com.bank.resources;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.bank.dto.CommonApiResponse;
import com.bank.dto.TransactionRequestDto;
import com.bank.dto.TransactionResponseDto;
import com.bank.entity.Account;
import com.bank.entity.Bank;
import com.bank.entity.Transaction;
import com.bank.entity.User;
import com.bank.exception.TransactionException;
import com.bank.service.AccountService;
import com.bank.service.BankService;
import com.bank.service.TransactionService;
import com.bank.service.UserService;
import com.bank.utility.BankStatementDownloader;
import com.bank.utility.Constants.BankAccountStatus;
import com.bank.utility.Constants.TransactionNarration;
import com.bank.utility.Constants.TransactionType;
import com.bank.utility.Constants.UserStatus;
import com.bank.utility.DateTimeUtils;
import com.bank.utility.TransactionIdGenerator;
import com.lowagie.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Component
public class TransactionResource {

	private final Logger LOG = LoggerFactory.getLogger(TransactionResource.class);
	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;
	@Autowired
	private BankService bankService;

	@Autowired
	private TransactionService transactionService;

	@Transactional(rollbackOn = TransactionException.class)
	public ResponseEntity<CommonApiResponse> depositAmountTxn(TransactionRequestDto request) throws Exception {
		LOG.info("Request Received to Deposit amount into Customer Account");
		CommonApiResponse response = new CommonApiResponse();
		if (request == null || request.getAmount() == null || request.getUserId() <= 0
				|| request.getSourceBankAccountId() <= 0) {
			response.setResponseMessage("Invalid Data - Bad Request");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			response.setResponseMessage("Failed to Deposit Amount, Please Select valid Amount");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Account account = this.accountService.getAccountById(request.getSourceBankAccountId());
		if (account == null) {
			response.setResponseMessage("Account not found, Invalid Account Id!!!");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		if (!account.getStatus().equals(BankAccountStatus.OPEN.value())) {
			response.setResponseMessage("Account is Locked, Cannot Deposit Amount");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		User user = account.getUser();
		if (!user.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.value())) {
			System.out.println("user infor inside loop");
			response.setResponseMessage("User is not ACTIVE");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		account.setBalance(account.getBalance().add(request.getAmount()));
		Account updatedAccount = this.accountService.updateAccount(account);
		if (updatedAccount == null) {
			response.setResponseMessage("Failed to Deposit Amount");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Bank bank = account.getBank();

		Transaction transaction = new Transaction();
		transaction.setSourceAccount(updatedAccount);
		transaction.setType(TransactionType.DEPOSIT.value());
		transaction.setTransactionId(TransactionIdGenerator.generate());
		transaction.setAmount(request.getAmount());
		transaction.setTransactionTime(
				String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		transaction.setNarration(TransactionNarration.BANK_DEPOSIT.value());
		transaction.setUser(user);
		transaction.setBank(bank);

		Transaction addedTxn = this.transactionService.addTransaction(transaction);
		if (addedTxn == null) {
			throw new TransactionException("Failed to Deposit Amount");
		} else {
			response.setResponseMessage("Amount Deposited Succesfully");
			response.setSuccess(true);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.CREATED);
		}

	}

	@Transactional(rollbackOn = TransactionException.class)
	public ResponseEntity<CommonApiResponse> withdrawTxn(TransactionRequestDto request) {
		// TODO Auto-generated method stub
		LOG.info("Request Received to withdraw Transaction");
		CommonApiResponse response = new CommonApiResponse();
		if (request == null || request.getAmount() == null || request.getUserId() <= 0
				|| request.getSourceBankAccountId() <= 0) {
			response.setResponseMessage("Invalid Data - Bad Request");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			response.setResponseMessage("Failed to Withdraw Amount, Please Select valid Amount");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		Account account = this.accountService.getAccountById(request.getSourceBankAccountId());
		if (account == null) {
			response.setResponseMessage("Account not found, Invalid Account Id!!!");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		if (!account.getStatus().equals(BankAccountStatus.OPEN.value())) {
			response.setResponseMessage("Account is Locked, Cannot Deposit Amount");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		if (account.getBalance().compareTo(request.getAmount()) < 0) {
			response.setResponseMessage("Insufficient amount in the account");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		User user = account.getUser();
		if (!user.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.value())) {
			System.out.println("user infor inside loop");
			response.setResponseMessage("User is not ACTIVE");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		account.setBalance(account.getBalance().subtract(request.getAmount()));
		Account updatedAccount = this.accountService.updateAccount(account);
		if (updatedAccount == null) {
			response.setResponseMessage("Failed to Withdraw Amount");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Bank bank = account.getBank();

		Transaction transaction = new Transaction();
		transaction.setSourceAccount(updatedAccount);
		transaction.setType(TransactionType.WITHDRAW.value());
		transaction.setTransactionId(TransactionIdGenerator.generate());
		transaction.setAmount(request.getAmount());
		transaction.setTransactionTime(
				String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		transaction.setNarration(TransactionNarration.BANK_WITHDRAW.value());
		transaction.setUser(user);
		transaction.setBank(bank);

		Transaction addedTxn = this.transactionService.addTransaction(transaction);
		if (addedTxn == null) {
			throw new TransactionException("Failed to Withdraw Amount");
		} else {
			response.setResponseMessage("Amount Withdraw Succesfully");
			response.setSuccess(true);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.CREATED);
		}
	}

	@Transactional(rollbackOn = TransactionException.class)
	public ResponseEntity<CommonApiResponse> accountTransfer(TransactionRequestDto request) {
		// TODO Auto-generated method stub
		LOG.info("Request Received for Account Transfer");
		CommonApiResponse response = new CommonApiResponse();
		if (request == null || request.getAmount() == null || request.getUserId() <= 0
				|| request.getSourceBankAccountId() <= 0 || request.getToBankIfsc() == null
				|| request.getToBankAccount() == null) {
			response.setResponseMessage("Invalid Data - Bad Request");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
			response.setResponseMessage("Failed to Transfer Amount, Please Select valid Amount");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = this.userService.getUserById(request.getUserId());
		if (user == null || !user.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.value())) {
			response.setResponseMessage("User not found or User is IN-ACTIVE, please check user details");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		Account senderAccount = this.accountService.getByUserAndStatus(request.getUserId(),
				BankAccountStatus.OPEN.value());

		if (senderAccount == null) {
			response.setResponseMessage("Sender Account not found, please check Account details");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (!senderAccount.getStatus().equalsIgnoreCase(BankAccountStatus.OPEN.value())) {
			response.setResponseMessage("Sender Account not in ACTIVE State, please check Account details");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (senderAccount.getBalance().compareTo(request.getAmount()) < 0) {
			response.setResponseMessage("Insufficient Fund, Failed to transfer the amount");
			response.setSuccess(true);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Account receipientAccount = this.accountService.getAccountByNumberAndIfscCodeAndStatus(
				request.getToBankAccount(), request.getToBankIfsc(), BankAccountStatus.OPEN.value());

		if (receipientAccount == null) {
			response.setResponseMessage("Receipient Account not found, please check Account details");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		senderAccount.setBalance(senderAccount.getBalance().subtract(request.getAmount()));
		Account updatedSenderAccount = this.accountService.updateAccount(senderAccount);

		if (updatedSenderAccount == null) {
			response.setResponseMessage("Failed to Transfer Amount");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		receipientAccount.setBalance(receipientAccount.getBalance().add(request.getAmount()));
		Account updatedReceipientAccount = this.accountService.updateAccount(receipientAccount);

		if (updatedReceipientAccount == null) {
			response.setResponseMessage("Failed to Deposit Amount");
			response.setSuccess(false);
			throw new TransactionException("Failed to Transfer Amount");
		}

		Transaction transaction = new Transaction();
		transaction.setSourceAccount(updatedSenderAccount);
		transaction.setType(TransactionType.ACCOUNT_TRANSFER.value());
		transaction.setTransactionId(TransactionIdGenerator.generate());
		transaction.setAmount(request.getAmount());
		transaction.setDestinationAccount(updatedReceipientAccount);
		transaction.setTransactionTime(
				String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		transaction.setNarration(request.getAccountTransferPurpose());
		transaction.setUser(user);
		transaction.setBank(senderAccount.getBank());

		Transaction newTransaction = this.transactionService.addTransaction(transaction);
		if (newTransaction == null) {
			throw new TransactionException("Failed to Transfer Amount");
		} else {
			response.setResponseMessage("Account Transfer Successfull");
			response.setSuccess(true);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.CREATED);
		}

	}

	public ResponseEntity<TransactionResponseDto> bankTransactionHistory(int userId) {
		// TODO Auto-generated method stub
		LOG.info("request received for Transaction history using userId");
		TransactionResponseDto response = new TransactionResponseDto();
		if (userId <= 0) {
			response.setResponseMessage("Invalid User Id");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		User user = this.userService.getUserById(userId);
		System.out.println(user.getStatus());
		if (user == null || user.getStatus().equals(UserStatus.ACTIVE.value())) {
			response.setResponseMessage("User is Inactive or user not found. Please check user details");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Transaction> transactions = this.transactionService.getAllTransctionsByUserId(userId);

		if (transactions.isEmpty()) {
			response.setResponseMessage("No Transactions found");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
		}
		response.setTransactions(transactions);
		response.setResponseMessage("Transactions fetched successfully");
		response.setSuccess(true);
		return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<TransactionResponseDto> allBankCustomerTransactions() {
		LOG.info("Request Received for Fetching all Bank Customers Transactions");
		TransactionResponseDto response = new TransactionResponseDto();
		List<Transaction> transactions = this.transactionService.getAllTransactions();
		if (transactions.isEmpty()) {
			response.setResponseMessage("No Transactions found");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
		}
		response.setResponseMessage("All Bank Transactions fetched successfully");
		response.setSuccess(true);
		return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<TransactionResponseDto> getBankCustomerTransaction(int bankId, String accountNo) {
		LOG.info("Request Received for Fetching Transactions using Bank Id and account number");
		TransactionResponseDto response = new TransactionResponseDto();
		if (bankId <= 0 || accountNo == null) {
			response.setResponseMessage("Invalid Bank Id or account number");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		Bank bank = this.bankService.getBankById(bankId);
		if (bank == null) {
			response.setResponseMessage("Bank Not Found, Please check bank Id");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		List<Account> accounts = this.accountService.getAccountByNumberContainingIgnoreCaseAndBankId(accountNo, bankId);
		if (accounts.isEmpty()) {
			response.setResponseMessage("No Account found, Please check the account number");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Account account = accounts.get(0);
		List<Transaction> transactions = this.transactionService.getAllTransctionsByUserId(account.getUser().getId());
		if (transactions.isEmpty()) {
			response.setResponseMessage("No Transactions found");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
		}
		response.setTransactions(transactions);
		response.setResponseMessage("All Bank Transactions fetched successfully using bank Id and account number");
		response.setSuccess(true);
		return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<TransactionResponseDto> getBankCustomerTransactionByTimeRange(int bankId, String accountNo,
			String startTime, String endTime) {
		LOG.info("Request Received for Fetching Transactions using Time Range");
		TransactionResponseDto response = new TransactionResponseDto();
		if (bankId <= 0 || accountNo == null) {
			response.setResponseMessage("Invaid Bank Id or account number, Please check inputs");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
		}
		Bank bank = this.bankService.getBankById(bankId);
		if (bank == null) {
			response.setResponseMessage("Bank Not Found, Please check bank Id");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Account> accounts = this.accountService.getAccountByNumberContainingIgnoreCaseAndBankId(accountNo, bankId);
		if (accounts.isEmpty()) {
			response.setResponseMessage("No Account found, Please check the account number");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		Account account = accounts.get(0);

		List<Transaction> transactions = this.transactionService
				.getAllTransactionsByTransactionTimeAndAccountId(startTime, endTime, account.getId());
		if (transactions.isEmpty()) {
			response.setResponseMessage("No Transactions found");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
		}

		response.setTransactions(transactions);
		response.setResponseMessage("All Bank Transactions fetched successfully using Time range");
		response.setSuccess(true);
		return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);

	}

	public ResponseEntity<TransactionResponseDto> getBankAllCustomerTransactionByTimeRange(int bankId, String startTime,
			String endTime) {
		LOG.info("Request Received for Fetching Transactions using Time Range using Bank Id");
		TransactionResponseDto response = new TransactionResponseDto();
		if (bankId <= 0) {
			response.setResponseMessage("Invaid Bank Id , Please check inputs");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		Bank bank = this.bankService.getBankById(bankId);
		if (bank == null) {
			response.setResponseMessage("Bank Not Found");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		
		List<Transaction> transactions = 
				this.transactionService.getAllTransactionsByTransactionTimeAndBankId(startTime, endTime, bankId);
		if (transactions.isEmpty()) {
			response.setResponseMessage("No Transactions found");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
		}
		response.setTransactions(transactions);
		response.setResponseMessage("AllTransactions fetched successfully using Bank Id");
		response.setSuccess(true);
		return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
	}

	public void downloadBankStatement(int accountId, String startTime, String endTime, HttpServletResponse response) 
	throws DocumentException, IOException
	{
		if(accountId <= 0 || startTime == null || endTime == null) {
			return;
		}
		List<Transaction> transactions = this.transactionService.
				getAllTransactionsByTransactionTimeAndAccountId(startTime, endTime, accountId);
		if(transactions.isEmpty()) {
			return;
		}
		response.setContentType("application/pdf");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + transactions.get(0).getSourceAccount().getNumber()
				+ "_Statement.pdf";
		response.setHeader(headerKey, headerValue);

		BankStatementDownloader exporter = new BankStatementDownloader(transactions,
				DateTimeUtils.getProperDateTimeFormatFromEpochTime(startTime),
				DateTimeUtils.getProperDateTimeFormatFromEpochTime(endTime));
		exporter.export(response);

		return;
	}

	public ResponseEntity<TransactionResponseDto> bankTransactionHistoryByTimeRange(int userId, String startTime,
			String endTime) {
		LOG.info("Request Received for Fetching Transactions using Time Range using User Id");
		TransactionResponseDto response = new TransactionResponseDto();
		if (userId <= 0) {
			response.setResponseMessage("Invaid User Id , Please check inputs");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		User user = this.userService.getUserById(userId);
		if (user == null) {
			response.setResponseMessage("User Not Found");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		
		
		List<Transaction> transactions = 
				this.transactionService.
				getTransactionsByUserIdAndTransactionTimeOrderByIdDesc(userId, startTime, endTime);
		
		
		if (transactions.isEmpty()) {
			response.setResponseMessage("No Transactions found");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
		}
		response.setTransactions(transactions);
		response.setResponseMessage("AllTransactions fetched successfully using Bank Id");
		response.setSuccess(true);
		return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<TransactionResponseDto> getBankAllCustomerTransaction(int bankId) {
		LOG.info("Request Received to fetch customer Transactions");
		TransactionResponseDto response = new TransactionResponseDto();
		if (bankId <= 0) {
			response.setResponseMessage("Invaid Bank Id , Please check inputs");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		Bank bank = this.bankService.getBankById(bankId);
		if (bank == null) {
			response.setResponseMessage("Bank Not Found");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		List<Transaction> transactions = this.transactionService.getAllTransactionOrderByBankIdDesc(bankId);
		if (transactions.isEmpty()) {
			response.setResponseMessage("No Transactions found");
			response.setSuccess(false);
			return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
		}
		response.setTransactions(transactions);
		response.setSuccess(true);
		return new ResponseEntity<TransactionResponseDto>(response, HttpStatus.OK);
	}

}
