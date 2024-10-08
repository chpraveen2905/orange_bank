package com.bank.dto;

import lombok.Data;

@Data
public class AccountStatusUpdateRequestDto {

    private int accountId;
    private String status;
}
