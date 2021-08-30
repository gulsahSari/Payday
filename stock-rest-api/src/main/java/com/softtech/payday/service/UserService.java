package com.softtech.payday.service;

import com.softtech.payday.payload.ApiResponse;
import com.softtech.payday.security.UserPrincipal;

import java.math.BigDecimal;

public interface UserService {

    ApiResponse updateBalance(UserPrincipal user, BigDecimal amount);

}