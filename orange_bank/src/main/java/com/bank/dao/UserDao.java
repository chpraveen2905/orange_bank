package com.bank.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bank.entity.User;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    User findByEmailAndPassword(String email, String password);
    
    User findByEmailAndPasswordAndRoles(String email, String password, String role);

    User findByEmailAndRoles(String email, String roles);
    User findByEmail(String email);
    List<User> findByRoles(String role);
    List<User> findByRolesAndStatus(String role, String status);
    List<User> findByRolesAndStatusAndBank_Id(String role, String status, int bankId);
    List<User> findByRolesAndStatusAndBankIsNull(String role, String status);
    List<User> findByRolesAndBank_Id(String role, int bankId);
    List<User> findByNameContainingIgnoreCaseAndBank_IdAndRoles(String customerName, int bankId, String role);
    List<User> findByNameContainingIgnoreCaseAndRoles(String customerName, String role);


}
