package com.softtech.payday.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashRequest {

	private BigDecimal amount;

}
