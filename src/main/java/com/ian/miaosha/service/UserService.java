package com.ian.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ian.miaosha.dao.UserDao;
import com.ian.miaosha.domain.User;

@Service
public class UserService {

	@Autowired
	UserDao userDao;
	
	public User getUserById(int id) {
		return userDao.getUserById(id);
	}
	
	@Transactional
	public boolean tx() {
		User user1 = new User();
		user1.setId(4);
		user1.setName("user4");
		userDao.insert(user1);
		
		User user2 = new User();
		user2.setId(3);
		user2.setName("user3");
		userDao.insert(user2);
		
		return true;
		
	}
}
