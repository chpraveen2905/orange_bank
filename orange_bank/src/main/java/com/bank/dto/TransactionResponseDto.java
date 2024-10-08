package com.bank.dto;

import java.util.ArrayList;
import java.util.List;

import com.bank.entity.Transaction;

import lombok.Data;

@Data
public class TransactionResponseDto extends CommonApiResponse {
	private List<Transaction> transactions = new ArrayList<>();
}
