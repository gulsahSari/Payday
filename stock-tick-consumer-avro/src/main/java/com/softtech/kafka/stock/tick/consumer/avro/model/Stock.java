package com.softtech.kafka.stock.tick.consumer.avro.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;
    private String symbol;
    private BigDecimal price;

    public Stock(String symbol, BigDecimal price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


}