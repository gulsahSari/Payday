package com.softtech.kafka.stock.tick.consumer.avro.repository;



import com.softtech.kafka.stock.tick.consumer.avro.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM placeorder WHERE symbol = ?1 and ((order_type = 'BUY' and target_amount <= ?2) or (order_type = 'SELL' and target_amount >= ?2))", nativeQuery = true)
    List<Order> getAllEligibleStockList(String symbol, BigDecimal amount);

    @Query(value = "SELECT * FROM placeorder WHERE target_amount<(SELECT MAX(target_amount) FROM placeorder WHERE symbol = ?1) and  symbol = ?1  LIMIT 1 ", nativeQuery = true)
    Order getBorderMaxAmount(String symbol);

    @Query(value = "SELECT * FROM placeorder WHERE target_amount>(SELECT MIN(target_amount) FROM placeorder WHERE symbol = ?1) and  symbol = ?1  LIMIT 1", nativeQuery = true)
    Order getBorderMinAmount(String symbol);

    @Override
    void delete(Order entity);
}
