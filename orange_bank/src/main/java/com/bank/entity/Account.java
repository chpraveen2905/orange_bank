package com.bank.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String number;
    private String ifscCode;
    private String type;
    private BigDecimal balance;
    private String creationDate;
    private String status; // Open, Closed, Deleted, In operative

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
