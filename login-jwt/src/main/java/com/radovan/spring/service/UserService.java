package com.radovan.spring.service;

import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.UserEntity;

public interface UserService {

	UserDto storeUser(UserDto user);
	
	UserEntity getUserByEmail(String email);
}
