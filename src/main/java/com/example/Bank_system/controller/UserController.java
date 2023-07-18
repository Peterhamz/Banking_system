package com.example.Bank_system.controller;

import com.example.Bank_system.dto.BankResponse;
import com.example.Bank_system.dto.CreditDebitRequest;
import com.example.Bank_system.dto.EnquiryRequest;
import com.example.Bank_system.dto.UserRequest;
import com.example.Bank_system.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/createAccount")
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
       return userService.balanceEnquiry(request);
    }
    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }


    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest userRequest){
        return userService.creditAccount(userRequest);
    }
    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }
}
