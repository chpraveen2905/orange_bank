package com.bank.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Account;

@Repository
public interface AccountDao extends JpaRepository<Account, Integer> {

	Account findByUser_IdAndStatus(int userId, String status);

	List<Account> findByBank_Id(int bankId);

	List<Account> findByBank_IdAndStatus(int bankId, String status);

	List<Account> findByStatus(String status);

	Account findByNumberAndIfscCodeAndBank_IdAndStatus(String accNumber, String ifscCode, int bankId, String status);

	List<Account> findByNumberContainingIgnoreCaseAndBank_Id(String accNumber, int bankId);

	Account findByUser_Id(int userId);

	Account findByNumberAndIfscCodeAndStatus(String accNumber, String ifscCode, String status);

	List<Account> findByNumberContainingIgnoreCase(String accountNumber);
}
