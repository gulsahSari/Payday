package com.softtech.kafka.stock.tick.consumer.avro.service;

import com.softtech.kafka.stock.tick.consumer.avro.model.Order;
import com.softtech.kafka.stock.tick.consumer.avro.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private OrderRepository orderRepository;


    @Override
    public List<Order> getAllEligibleStockList(String symbol, BigDecimal amount) {
        return orderRepository.getAllEligibleStockList(symbol,amount);
    }

    @Override
    public Order getBorderMaxAmount(String symbol) {
        return orderRepository.getBorderMaxAmount(symbol);
    }

    @Override
    public Order getBorderMinAmount(String symbol) {
        return orderRepository.getBorderMinAmount(symbol);
    }

    @Override
    public void delete(Order entity) {
         orderRepository.delete(entity);
    }
}
