package com.softtech.kafka.stock.tick.consumer.avro.service;


import com.softtech.kafka.stock.tick.consumer.avro.model.Order;

import java.math.BigDecimal;
import java.util.List;

public interface StockService {

	List<Order> getAllEligibleStockList(String symbol, BigDecimal amount);

	Order getBorderMaxAmount(String symbol);

	Order getBorderMinAmount(String symbol);

	void delete(Order entity);

}
