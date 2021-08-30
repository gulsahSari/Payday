package com.softtech.payday.controller;

import com.softtech.payday.model.Stock;
import com.softtech.payday.payload.ApiResponse;
import com.softtech.payday.payload.OrderRequest;
import com.softtech.payday.payload.PagedResponse;
import com.softtech.payday.security.CurrentUser;
import com.softtech.payday.security.UserPrincipal;
import com.softtech.payday.service.StockService;
import com.softtech.payday.utils.AppConstants;
import com.softtech.payday.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/stock")
public class StockController {

	@Autowired
	private StockService stockService;

	@GetMapping("/list")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<PagedResponse<Stock>> listStock(@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
														  @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
														  @CurrentUser UserPrincipal currentUser) {
		AppUtils.validatePageNumberAndSize(page, size);

		PagedResponse<Stock> stockServiceResponse = stockService.listAllStock(page,size);
		return new ResponseEntity< >(stockServiceResponse, HttpStatus.OK);
	}

	@PostMapping("/order")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse> placeOrder(@Valid @RequestBody OrderRequest orderRequest,
												  @CurrentUser UserPrincipal currentUser) {
		ApiResponse response = stockService.orderPlace(currentUser, orderRequest);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
