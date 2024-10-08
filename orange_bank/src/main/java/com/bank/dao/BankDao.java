package com.bank.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Bank;

@Repository
public interface BankDao extends JpaRepository<Bank, Integer> {

}
