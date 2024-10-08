package com.bank.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.dao.TransactionDao;
import com.bank.entity.Transaction;
import com.bank.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionDao transactionDao;

	@Override
	public Transaction addTransaction(Transaction transaction) {
		// TODO Auto-generated method stub
		return transactionDao.save(transaction);
	}

	@Override
	public Transaction getTansactionById(int id) {
		// TODO Auto-generated method stub
		return transactionDao.findById(id).get();
	}

	@Override
	public Transaction getTransactionByTransactionId(String transactionId) {
		// TODO Auto-generated method stub
		return transactionDao.findByTransactionId(transactionId);
	}

	@Override
	public List<Transaction> getTransactionsByAccountId(int accountId) {
		// TODO Auto-generated method stub
		return transactionDao.findBySourceAccount_id(accountId);
	}

	@Override
	public List<Transaction> getAllTransactions() {
		// TODO Auto-generated method stub
		return transactionDao.findAllByOrderByIdDesc();
	}

	@Override
	public List<Transaction> getAllTransactionsByTransactionTime(String startDate, String endDate) {
		// TODO Auto-generated method stub
		return transactionDao.findByTransactionTimeBetweenOrderByIdDesc(startDate, endDate);
	}

	@Override
	public List<Transaction> getAllTransactionsByTransactionTimeAndBankId(String startDate, String endDate,
			int bankId) {
		// TODO Auto-generated method stub
		return transactionDao.findByTransactionTimeBetweenAndBank_idOrderByIdDesc(startDate, endDate, bankId);
	}

	@Override
	public List<Transaction> getAllTransactionsByTransactionTimeAndAccountId(String startDate, String endDate,
			int accountId) {
		// TODO Auto-generated method stub
		return transactionDao.findByTransactionTimeBetweenAndSourceAccount_idOrderByIdDesc(startDate, endDate, accountId);
	}

	@Override
	public List<Transaction> getAllTransctionsByUserId(int userId) {
		// TODO Auto-generated method stub
		return transactionDao.findByUser_idOrderByIdDesc(userId);
	}

	@Override
	public List<Transaction> getAllTransactionOrderByBankIdDesc(int bankId) {
		// TODO Auto-generated method stub
		return transactionDao.findByBank_idOrderByIdDesc(bankId);
	}

	@Override
	public List<Transaction> getTransactionsByUserIdAndTransactionTimeOrderByIdDesc(int userId, String startDate,
			String endDate) {
		// TODO Auto-generated method stub
		return transactionDao.findByUser_idAndTransactionTimeBetweenOrderByIdDesc(userId, startDate, endDate);
	}

}
