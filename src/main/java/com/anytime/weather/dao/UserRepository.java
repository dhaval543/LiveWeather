package com.anytime.weather.dao;

import com.anytime.weather.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	User findByUsername(String username);
}