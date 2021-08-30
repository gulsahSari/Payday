package com.softtech.payday.controller;

import com.softtech.payday.payload.ApiResponse;
import com.softtech.payday.payload.CashRequest;
import com.softtech.payday.security.CurrentUser;
import com.softtech.payday.security.UserPrincipal;
import com.softtech.payday.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/cash")
public class CashController {

	@Autowired
	private UserService userService;

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse> updateBalance(@Valid @RequestBody CashRequest cashRequest,
													 @CurrentUser UserPrincipal currentUser) {
		ApiResponse userServiceResponse = userService.updateBalance(currentUser,cashRequest.getAmount());
		return new ResponseEntity<>(userServiceResponse, HttpStatus.OK);
	}
}
