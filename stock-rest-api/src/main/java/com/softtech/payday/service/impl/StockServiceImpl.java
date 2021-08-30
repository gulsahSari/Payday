package com.softtech.payday.service.impl;

import com.softtech.payday.exception.ResourceNotFoundException;
import com.softtech.payday.model.RedisStock;
import com.softtech.payday.model.Stock;
import com.softtech.payday.model.order.Order;
import com.softtech.payday.model.user.User;
import com.softtech.payday.payload.ApiResponse;
import com.softtech.payday.payload.OrderRequest;
import com.softtech.payday.payload.PagedResponse;
import com.softtech.payday.repository.OrderRepository;
import com.softtech.payday.repository.UserRepository;
import com.softtech.payday.repository.redis.RedisRepository;
import com.softtech.payday.security.UserPrincipal;
import com.softtech.payday.service.StockService;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.softtech.payday.utils.AppConstants.ID;
import static com.softtech.payday.utils.AppConstants.USER;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisRepository redisRepository;


    public List<Stock> randomStockListGenerator() {
        List<Stock> stocks = Arrays.asList(new Stock("AAPL", generateRandomPrice()),
                new Stock("AMZN", generateRandomPrice()),
                new Stock("GOOGL", generateRandomPrice()),
                new Stock("NFLX", generateRandomPrice()),
                new Stock("INGA", generateRandomPrice()),
                new Stock("AD", generateRandomPrice()),
                new Stock("RDSA", generateRandomPrice()));

        return stocks;
    }

    private BigDecimal generateRandomPrice() {
        double leftLimit = 1.000D;
        double rightLimit = 3000.000D;

        BigDecimal randomPrice = BigDecimal.valueOf(new RandomDataGenerator().nextUniform(leftLimit, rightLimit));
        randomPrice = randomPrice.setScale(3, RoundingMode.HALF_UP);
        return randomPrice;
    }

    @Override
    public PagedResponse<Stock> listAllStock(int page, int size) {
        List<Stock> stock = randomStockListGenerator();

        return new PagedResponse<>(stock, page, size, stock.size(),
                page, true);
    }

    Map<String, String> findAll() {
        Map<Object, Object> aa = redisRepository.findAllStocks();
        Map<String, String> map = new HashMap<String, String>();
        for (Map.Entry<Object, Object> entry : aa.entrySet()) {
            String key = (String) entry.getKey();
            map.put(key, aa.get(key).toString());
        }
        return map;
    }

    public void updateRedisRecord(OrderRequest orderRequest) {
        String stockSymbol = orderRequest.getSymbol();
        RedisStock tempStock = redisRepository.findStock(stockSymbol);

        if (tempStock == null) {
            RedisStock redisStock = new RedisStock(stockSymbol, orderRequest.getTargetAmount(), orderRequest.getTargetAmount());
            redisRepository.add(redisStock);
        } else {
            BigDecimal minAmount = tempStock.getMinPrice();
            BigDecimal maxAmount = tempStock.getMaxPrice();
            if (orderRequest.getTargetAmount().compareTo(minAmount) < 0) {
                tempStock.setMinPrice(orderRequest.getTargetAmount());
            }

            if (orderRequest.getTargetAmount().compareTo(maxAmount) > 0) {
                tempStock.setMaxPrice(orderRequest.getTargetAmount());
            }
            redisRepository.add(tempStock);
        }
    }

    @Override
    @Transactional
    public ApiResponse orderPlace(UserPrincipal currentUser, OrderRequest orderRequest) {

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER, ID, 1L));

        Order order = new Order();
        order.setSymbol(orderRequest.getSymbol());
        order.setOrderType(orderRequest.getOrderType());
        order.setTargetAmount(orderRequest.getTargetAmount());
        order.setCount(orderRequest.getCount());
        order.setUser(user);

        Order result = orderRepository.save(order);

        updateRedisRecord(orderRequest);

        return new ApiResponse(Boolean.TRUE, "You successfully create order");
    }

}
