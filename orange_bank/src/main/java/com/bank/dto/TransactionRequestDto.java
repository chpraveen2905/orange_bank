package com.bank.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransactionRequestDto {
	private int userId;

	private int bankId;

	private BigDecimal amount;

	private int sourceBankAccountId;

	private String transactionType;

	private String toBankAccount; // for account transfer

	private String toBankIfsc; // for account transfer

	private String accountTransferPurpose;
}
