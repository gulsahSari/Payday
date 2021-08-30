package com.softtech.payday.repository.redis;

import com.softtech.payday.model.RedisStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;


@Repository
public class RedisRepositoryImpl implements RedisRepository {
    private static final String KEY = "Stock";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    public RedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public void add(final RedisStock stock) {
        hashOperations.put(KEY, stock.getSymbol(), stock.getMinPrice() + "-" + stock.getMaxPrice());
    }

    public void delete(final String stockName) {
        hashOperations.delete(KEY, stockName);
    }

    public RedisStock findStock(final String stockName) {
        Object value = hashOperations.get(KEY, stockName);
        if (value == null) return null;

        RedisStock redisStock = new RedisStock();
        redisStock.setSymbol(stockName);
        redisStock.setMinPrice(new BigDecimal(value.toString().split("-")[0]));
        redisStock.setMaxPrice(new BigDecimal(value.toString().split("-")[1]));
        return redisStock;
    }

    public Map<Object, Object> findAllStocks() {
        return hashOperations.entries(KEY);
    }
}