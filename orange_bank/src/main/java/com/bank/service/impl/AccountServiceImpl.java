package com.bank.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.dao.AccountDao;
import com.bank.entity.Account;
import com.bank.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService{

	@Autowired
	private AccountDao accountDao;
	@Override
	public Account addAccount(Account account) {
		// TODO Auto-generated method stub
		return accountDao.save(account);
	}

	@Override
	public Account updateAccount(Account account) {
		// TODO Auto-generated method stub
		return accountDao.save(account);
	}

	@Override
	public Account getAccountById(int accountId) {
		// TODO Auto-generated method stub
		return accountDao.findById(accountId).get();
	}

	@Override
	public List<Account> getAllAccounts() {
		// TODO Auto-generated method stub
		return accountDao.findAll();
	}

	@Override
	public Account getByUserAndStatus(int userId, String status) {
		// TODO Auto-generated method stub
		return accountDao.findByUser_IdAndStatus(userId, status);
	}

	@Override
	public List<Account> getByBankId(int bankId) {
		// TODO Auto-generated method stub
		return accountDao.findByBank_Id(bankId);
	}

	@Override
	public List<Account> getByBankIdAndStatus(int bankId, String status) {
		// TODO Auto-generated method stub
		return accountDao.findByBank_IdAndStatus(bankId, status);
	}

	@Override
	public List<Account> getByStatus(String status) {
		// TODO Auto-generated method stub
		return accountDao.findByStatus(status);
	}

	@Override
	public Account getByNumberAndIfscCodeAndBankIdAndStatus(String accNumber, String ifscCode, int bankId,
			String status) {
		// TODO Auto-generated method stub
		return accountDao.findByNumberAndIfscCodeAndBank_IdAndStatus(accNumber, ifscCode, bankId, status);
	}

	@Override
	public List<Account> getAccountByNumberContainingIgnoreCaseAndBankId(String accNumber, int bankId) {
		// TODO Auto-generated method stub
		return accountDao.findByNumberContainingIgnoreCaseAndBank_Id(accNumber, bankId);
	}

	@Override
	public Account getAccountByUserId(int userId) {
		// TODO Auto-generated method stub
		return accountDao.findByUser_Id(userId);
	}

	@Override
	public Account getAccountByNumberAndIfscCodeAndStatus(String accNumber, String ifscCode, String status) {
		// TODO Auto-generated method stub
		return accountDao.findByNumberAndIfscCodeAndStatus(accNumber, ifscCode, status);
	}

	@Override
	public List<Account> getAccountByNumberContainingIgnoreCase(String accountNumber) {
		// TODO Auto-generated method stub
		return accountDao.findByNumberContainingIgnoreCase(accountNumber);
	}

}
