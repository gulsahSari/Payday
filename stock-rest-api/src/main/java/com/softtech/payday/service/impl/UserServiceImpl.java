package com.softtech.payday.service.impl;

import com.softtech.payday.model.user.User;
import com.softtech.payday.payload.ApiResponse;
import com.softtech.payday.repository.UserRepository;
import com.softtech.payday.security.UserPrincipal;
import com.softtech.payday.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;


	@Override
	@Transactional
	public ApiResponse updateBalance(UserPrincipal userinfo, BigDecimal amount) {
		User user = userRepository.getUserByName(userinfo.getUsername());

		BigDecimal currentBalance = user.getBalance();
		currentBalance = currentBalance.add(amount);

		user.setBalance(currentBalance);
		userRepository.save(user);
		return new ApiResponse(Boolean.TRUE, "Your current balance is : " + currentBalance);
	}

}
