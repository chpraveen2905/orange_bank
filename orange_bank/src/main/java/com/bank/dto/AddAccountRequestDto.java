package com.bank.dto;

import com.bank.entity.Account;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AddAccountRequestDto {
    private String number;
    private String ifscCode;
    private String type;
    private int bankId;
    private int userId;

    public static Account toAccountEntity(AddAccountRequestDto requestDto) {
        Account account = new Account();
        BeanUtils.copyProperties(requestDto, account);
        return account;
    }
}
