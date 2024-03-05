package com.anytime.weather.service;

import com.anytime.weather.controller.request.UserRequest;
import com.anytime.weather.dao.UserRepository;
import com.anytime.weather.exception.EntityType;
import com.anytime.weather.exception.ExceptionType;
import com.anytime.weather.exception.TWMException;
import com.anytime.weather.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


/**
 * Repository for User Table.
 *
 */

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.anytime.weather.model.User user = findByUsername(username);
		if (user == null) {
			throw exception(EntityType.USER, ExceptionType.ENTITY_NOT_FOUND, username + " doesn't exists");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}

	public com.anytime.weather.model.User findByUsername(String username) {
		com.anytime.weather.model.User user = userRepo.findByUsername(username);
		return user;
	}

	public com.anytime.weather.model.User save(UserRequest user) {
		com.anytime.weather.model.User newUser = new com.anytime.weather.model.User();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userRepo.save(newUser);
	}
	
	public void updatePassword(UserDTO user) {
		com.anytime.weather.model.User daoUser = userRepo.findByUsername(user.getUsername());
		daoUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		daoUser.setUsername(user.getUsername());
		userRepo.save(daoUser);
	}

	/**
	 * Returns a new RuntimeException
	 *
	 * @param entityType
	 * @param exceptionType
	 * @param args
	 * @return
	 */
	private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
		return TWMException.throwException(entityType, exceptionType, args);
	}
}