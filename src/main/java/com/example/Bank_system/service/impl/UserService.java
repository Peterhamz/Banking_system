package com.example.Bank_system.service.impl;

import com.example.Bank_system.dto.BankResponse;
import com.example.Bank_system.dto.CreditDebitRequest;
import com.example.Bank_system.dto.EnquiryRequest;
import com.example.Bank_system.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);

    String nameEnquiry(EnquiryRequest enquiryRequest);

    BankResponse creditAccount(CreditDebitRequest creditDebitRequest);
    BankResponse debitAccount(CreditDebitRequest creditDebitRequest);

}
