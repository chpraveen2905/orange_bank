package com.bank.service;

import java.util.List;

import com.bank.entity.Bank;

public interface BankService {

	Bank getBankById(int bankId);
	Bank addBank(Bank bank);
	Bank updateBank(Bank bank);
	List<Bank> getAllBank();

}
