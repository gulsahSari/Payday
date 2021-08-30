package com.softtech.kafka.stock.tick.consumer.avro.redis;


import com.softtech.kafka.stock.tick.consumer.avro.model.RedisStock;

import java.util.Map;

public interface RedisRepository {

    Map<Object, Object> findAllStocks();

    void add(RedisStock stock);

    void delete(String stockName);

    RedisStock findStock(String stockName);
}