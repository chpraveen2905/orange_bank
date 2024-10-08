package com.bank.dto;


import lombok.Data;
import com.bank.entity.User;
import org.springframework.beans.BeanUtils;

@Data
public class RegisterUserRequestDto {
    private Integer id;
    private String name;
    private String email;
    private String password;
    private String roles;
    private String gender;
    private String contact;
    private String street;
    private String city;
    private String pinCode;
    private int bankId;

    public static User toUserEntity(RegisterUserRequestDto registerUserRequestDto) {
        User user = new User();
        BeanUtils.copyProperties(registerUserRequestDto, user);
        return user;
    }
}
