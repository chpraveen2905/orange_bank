package com.bank.service.impl;

import com.bank.dao.UserDao;
import com.bank.entity.User;
import com.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

	@Override
	public User registerUser(User user) {
		// TODO Auto-generated method stub
		return userDao.save(user);
	}

	@Override
	public User updateUser(User user) {
		// TODO Auto-generated method stub
		return userDao.save(user);
	}

	@Override
	public User getUserById(int userId) {
		// TODO Auto-generated method stub
		return userDao.findById(userId).get();
	}

	@Override
	public User getUserByEmailAndPassword(String email, String password) {
		// TODO Auto-generated method stub
		return userDao.findByEmailAndPassword(email, password);
	}

	@Override
	public User getUserByEmailAndPasswordAndRoles(String email, String password, String role) {
		// TODO Auto-generated method stub
		return userDao.findByEmailAndPasswordAndRoles(email, password, role);
	}

	@Override
	public User getUserByEmail(String email) {
		// TODO Auto-generated method stub
		return userDao.findByEmail(email);
	}

	@Override
	public User getUserByEmailAndRoles(String email, String roles) {
		// TODO Auto-generated method stub
		return userDao.findByEmailAndRoles(email, roles);
	}

	@Override
	public List<User> getUsersByRolesAndStatus(String role, String status) {
		// TODO Auto-generated method stub
		return userDao.findByRolesAndStatus(role, status);
	}

	@Override
	public List<User> getUsersByRolesAndStatusAndBank(String role, String status, int bankId) {
		// TODO Auto-generated method stub
		return userDao.findByRolesAndStatusAndBank_Id(role, status, bankId);
	}

	@Override
	public List<User> getUserByRoles(String role) {
		// TODO Auto-generated method stub
		return userDao.findByRoles(role);
	}

	@Override
	public List<User> getUsersByRolesAndStatusAndBankIsNull(String role, String status) {
		// TODO Auto-generated method stub
		return userDao.findByRolesAndStatusAndBankIsNull(role, status);
	}

	@Override
	public List<User> getUserByRolesAndBank(String role, int bankId) {
		// TODO Auto-generated method stub
		return userDao.findByRolesAndBank_Id(role, bankId);
	}

	@Override
	public List<User> searchBankCustomerByNameAndBankAndRole(String customerName, int bankId, String role) {
		// TODO Auto-generated method stub
		return userDao.findByNameContainingIgnoreCaseAndBank_IdAndRoles(customerName, bankId, role);
	}

	@Override
	public List<User> searchBankCustomerByNameAndRole(String customerName, String role) {
		// TODO Auto-generated method stub
		return userDao.findByNameContainingIgnoreCaseAndRoles(customerName, role);
	}

  
}
