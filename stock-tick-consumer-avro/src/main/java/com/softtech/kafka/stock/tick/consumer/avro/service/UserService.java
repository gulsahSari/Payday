package com.softtech.kafka.stock.tick.consumer.avro.service;


import com.softtech.kafka.stock.tick.consumer.avro.model.User;

public interface UserService {

    void updateBalance(User user);

    User getUserDetail(Long user);

}