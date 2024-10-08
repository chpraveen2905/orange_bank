package com.bank.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.dao.BankDao;
import com.bank.entity.Bank;
import com.bank.service.BankService;

@Service
public class BankServiceImpl implements BankService{
	
	@Autowired
	private BankDao bankDao;

	@Override
	public Bank getBankById(int bankId) {
		// TODO Auto-generated method stub
		return this.bankDao.findById(bankId).get();
	}

	@Override
	public Bank addBank(Bank bank) {
		// TODO Auto-generated method stub
		return this.bankDao.save(bank);
	}

	@Override
	public Bank updateBank(Bank bank) {
		// TODO Auto-generated method stub
		return this.bankDao.save(bank);
	}

	@Override
	public List<Bank> getAllBank() {
		// TODO Auto-generated method stub
		return this.bankDao.findAll();
	}

}
