package com.bank.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Transaction;

@Repository
public interface TransactionDao extends JpaRepository<Transaction, Integer> {
	Transaction findByTransactionId(String transactionId);

	List<Transaction> findBySourceAccount_id(int accountId);

	List<Transaction> findAllByOrderByIdDesc();

	List<Transaction> findByTransactionTimeBetweenOrderByIdDesc(String startDate, String endDate);

	List<Transaction> findByTransactionTimeBetweenAndBank_idOrderByIdDesc(String startDate, String endDate, int bankId);

	List<Transaction> findByTransactionTimeBetweenAndSourceAccount_idOrderByIdDesc(String startDate, String endDate,
			int accountId);

	List<Transaction> findByUser_idOrderByIdDesc(int userId);

	List<Transaction> findByBank_idOrderByIdDesc(int bankId);

	List<Transaction> findByUser_idAndTransactionTimeBetweenOrderByIdDesc(int userId, String startDate, String endDate);
}
