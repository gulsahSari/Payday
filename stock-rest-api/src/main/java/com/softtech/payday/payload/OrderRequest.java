package com.softtech.payday.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class OrderRequest {

	@NotBlank
	@Size(min = 1)
	private String symbol;

	@NotBlank
	@Size(min = 1)
	private String orderType;

	private BigDecimal targetAmount;

	private int count;
}
