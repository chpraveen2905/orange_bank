package com.bank.service;

import java.util.List;

import com.bank.entity.Transaction;

public interface TransactionService {

	Transaction addTransaction(Transaction transaction);

	Transaction getTansactionById(int id);

	Transaction getTransactionByTransactionId(String transactionId);
	
	List<Transaction> getTransactionsByAccountId(int accountId);

	List<Transaction> getAllTransactions();

	List<Transaction> getAllTransactionsByTransactionTime(String startDate, String endDate);

	List<Transaction> getAllTransactionsByTransactionTimeAndBankId(String startDate, String endDate, int bankId);

	List<Transaction> getAllTransactionsByTransactionTimeAndAccountId(String startDate, String endDate, int accountId);

	List<Transaction> getAllTransctionsByUserId(int userId);

	List<Transaction> getAllTransactionOrderByBankIdDesc(int bankId);

	List<Transaction> getTransactionsByUserIdAndTransactionTimeOrderByIdDesc(int userId, String startDate,
			String endDate);

}
