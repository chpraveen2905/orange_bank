package com.bank.service;

import java.util.List;

import com.bank.entity.Account;

public interface AccountService {
	Account addAccount(Account account);

	Account updateAccount(Account account);

	Account getAccountById(int accountId);

	List<Account> getAllAccounts();

	Account getByUserAndStatus(int userId, String status);

	List<Account> getByBankId(int bankId);

	List<Account> getByBankIdAndStatus(int bankId, String status);

	List<Account> getByStatus(String status);

	Account getByNumberAndIfscCodeAndBankIdAndStatus(String accNumber, String ifscCode, int bankId, String status);

	List<Account> getAccountByNumberContainingIgnoreCaseAndBankId(String accNumber, int bankId);

	Account getAccountByUserId(int userId);

	Account getAccountByNumberAndIfscCodeAndStatus(String accNumber, String ifscCode, String status);

	List<Account> getAccountByNumberContainingIgnoreCase(String accountNumber);
}
