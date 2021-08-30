package com.softtech.payday.repository.redis;

import com.softtech.payday.model.RedisStock;

import java.util.Map;

public interface RedisRepository {

    Map<Object, Object> findAllStocks();

    void add(RedisStock stock);

    void delete(String stockName);

    RedisStock findStock(String stockName);
}