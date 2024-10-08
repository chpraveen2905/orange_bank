package com.bank.dto;

import java.util.ArrayList;
import java.util.List;

import com.bank.entity.User;

import lombok.Data;

@Data
public class UserListResponseDto extends CommonApiResponse{
	private List<User> users = new ArrayList<>();
}
