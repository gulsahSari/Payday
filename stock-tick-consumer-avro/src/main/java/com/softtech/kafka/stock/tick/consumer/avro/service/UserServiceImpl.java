package com.softtech.kafka.stock.tick.consumer.avro.service;


import com.softtech.kafka.stock.tick.consumer.avro.model.User;
import com.softtech.kafka.stock.tick.consumer.avro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public void updateBalance(User user) {
		User userInfo = userRepository.save(user);
	}

	@Override
	public User getUserDetail(Long userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("Not found or already confirmed")));

		return user;
	}

}
