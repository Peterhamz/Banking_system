package com.example.Bank_system.service.impl;

import com.example.Bank_system.dto.TransactionDto;
import com.example.Bank_system.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
