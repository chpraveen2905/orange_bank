package com.bank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import jakarta.persistence.Entity;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;

    @JsonIgnore
    private String password;
    private String roles;
    private String gender;
    private String contact;
    private String street;
    private String city;
    private String pinCode;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;
    private String isAccountLinked; // Yes or No
    private String status; // User Status Active or Deactivated

}
