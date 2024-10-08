package com.bank.dto;

import com.bank.entity.User;

import lombok.Data;

@Data
public class UserLoginResponse extends CommonApiResponse {
	private User user;
	private String jwtToken;
}
