package com.bank.dto;

import com.bank.entity.Account;
import lombok.Data;

import java.util.List;

@Data
public class AccountResponseDto extends CommonApiResponse {

    private List<Account> accounts;
}
