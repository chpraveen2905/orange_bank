package com.bank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String code;
    private String address;

    private String phoneNumber;
    private String email;
    private String website;
    private String country;
    private String currency;
    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Account> accountList = new ArrayList<>();
}
