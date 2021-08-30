package com.softtech.payday.service;

import com.softtech.payday.model.Stock;
import com.softtech.payday.payload.ApiResponse;
import com.softtech.payday.payload.OrderRequest;
import com.softtech.payday.payload.PagedResponse;
import com.softtech.payday.security.UserPrincipal;

public interface StockService {

	PagedResponse<Stock> listAllStock(int page, int size);

	ApiResponse orderPlace(UserPrincipal userPrincipal, OrderRequest orderRequest);



}
