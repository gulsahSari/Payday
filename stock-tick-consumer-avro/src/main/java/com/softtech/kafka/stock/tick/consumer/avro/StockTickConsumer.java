package com.softtech.kafka.stock.tick.consumer.avro;

import com.softtech.kafka.stock.tick.consumer.avro.model.Order;
import com.softtech.kafka.stock.tick.consumer.avro.model.RedisStock;
import com.softtech.kafka.stock.tick.consumer.avro.model.User;
import com.softtech.kafka.stock.tick.consumer.avro.redis.RedisRepository;
import com.softtech.kafka.stock.tick.consumer.avro.service.StockService;
import com.softtech.kafka.stock.tick.consumer.avro.service.UserService;
import com.softtech.stock.tick.avro.StockTick;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.softtech.kafka.stock.tick.consumer.avro.StockTickConsumerAvroApplication.TOPIC_NAME;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION_ID;

@Component
@Slf4j
public class StockTickConsumer {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private StockService stockService;


    @Autowired
    private JavaMailSender sender;

    @Autowired
    private UserService userService;

    @KafkaListener(topics = TOPIC_NAME)
    public void listen(StockTick stockTick, @Header(RECEIVED_PARTITION_ID) Integer partitionId) {

        String stockSymbol = stockTick.getSymbol();
        BigDecimal stockAmount = new BigDecimal(stockTick.getTradeValue());

        RedisStock tempStock = redisRepository.findStock(stockSymbol);

        if (tempStock != null) {
            if (stockAmount.compareTo(tempStock.getMinPrice()) >= 0 && stockAmount.compareTo(tempStock.getMaxPrice()) <= 0) {
                List<Order> stockList = stockService.getAllEligibleStockList(stockSymbol,stockAmount);
                for(Order order: stockList){
                    BigDecimal depositAmount = stockAmount.multiply(new BigDecimal(order.getCount()));
                    User userInfo = userService.getUserDetail(order.getUser().getId());
                    try{
                        doingPlaceOrder(stockSymbol, tempStock, order, userInfo,depositAmount);
                    }catch(Exception e){
                        log.info("PlaceOrder not done for: {}", order.getId());
                    }
                    log.info("PlaceOrder done for: {}", order.getId());
                }
            }
        }
        log.info("Consumed: {}, {} {} from partition: {}", stockTick.getSymbol(), stockTick.getTradeValue());
    }

    @Transactional
    public void doingPlaceOrder(String stockSymbol, RedisStock tempStock, Order order, User userInfo,BigDecimal depositAmount) {
        if(order.getOrderType().equals("BUY")){
            BigDecimal userNewBalance = userInfo.getBalance().add(depositAmount);
            userInfo.setBalance(userNewBalance);
            userService.updateBalance(userInfo);
            sendMail(userInfo.getEmail(),"your order is filled");
        }else if(order.getOrderType().equals("SELL") && (userInfo.getBalance().compareTo(depositAmount)>=0) ){
            BigDecimal userNewBalance = userInfo.getBalance().subtract(depositAmount);
            userInfo.setBalance(userNewBalance);
            userService.updateBalance(userInfo);
            sendMail(userInfo.getEmail(),"your order is filled");
        }
        if(order.getTargetAmount().compareTo(tempStock.getMaxPrice()) ==0 ){
            Order borderNewMaxAmount =  stockService.getBorderMaxAmount(stockSymbol);
            if(borderNewMaxAmount!= null){
                RedisStock stock = new RedisStock(stockSymbol, tempStock.getMinPrice(),borderNewMaxAmount.getTargetAmount());
                redisRepository.add(stock);
            }else{
                redisRepository.delete(stockSymbol);
            }
        }else if(order.getTargetAmount().compareTo(tempStock.getMinPrice())==0){
            Order borderNewMinAmount =  stockService.getBorderMinAmount(stockSymbol);
            if(borderNewMinAmount!= null){
                RedisStock stock = new RedisStock(stockSymbol, borderNewMinAmount.getTargetAmount(), tempStock.getMaxPrice());
                redisRepository.add(stock);
            }else{
                redisRepository.delete(stockSymbol);
            }
        }

        stockService.delete(order);

    }

    public String sendMail(String email,String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("paydaytradetrade@gmail.com");
        message.setTo(email);
        message.setSubject("Information of your order");
        message.setText(text);
        sender.send(message);

        return "success";

    }

}
