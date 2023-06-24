package com.example.Bank_system.service.impl;

import com.example.Bank_system.dto.BankResponse;
import com.example.Bank_system.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
}
