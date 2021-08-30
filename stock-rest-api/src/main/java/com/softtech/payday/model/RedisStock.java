package com.softtech.payday.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class RedisStock implements Serializable {
    private static final long serialVersionUID = 1L;
    private String symbol;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public RedisStock(String symbol, BigDecimal minPrice, BigDecimal maxPrice) {
        this.symbol = symbol;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public RedisStock() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }
}