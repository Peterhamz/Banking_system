package com.example.Bank_system.service.impl;

import com.example.Bank_system.dto.*;
import com.example.Bank_system.entity.User;
import com.example.Bank_system.repository.UserRepository;
import com.example.Bank_system.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;


    @Autowired
    EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        /**
         * create an account and save in the database
         * check if a user already exist
         */

        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode("001")
                    .responseMessage("Account already exist")
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .email(userRequest.getEmail())
                .accountBalance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNumber())
                .alternative(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();


        User savedUser = userRepository.save(newUser);

        //Send email Alert

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Creation")
                .messageBody("Congrats your Account has been created. \n Your Account Details: \n" +  "\n " +
                                "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + "\n" + "Account Number: "
                                + savedUser.getAccountNumber())
                .build();
                 emailService.sendEmail(emailDetails);
        return BankResponse.builder()
                .responseCode("002")
                .responseMessage("Account has been created")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName())
                        .accountNumber(savedUser.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());

        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode("004")
                    .responseMessage("Account do not exist")
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode("005")
                .responseMessage("Account found successfully")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(enquiryRequest.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getOtherName() + " " + foundUser.getLastName())
                        .accountBalance(foundUser.getAccountBalance())
                        .build())
                .build();
    }
    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());

        if(!isAccountExist){
            return "Account do not exist";
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());


        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode("004")
                    .responseMessage("Account do not exist")
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));

        userRepository.save(userToCredit);

        return BankResponse.builder()
                .responseCode("005")
                .responseMessage("Account found successfully")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(creditDebitRequest.getAccountNumber())
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getOtherName() + " " + userToCredit.getLastName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());


        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode("004")
                    .responseMessage("Account do not exist")
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = creditDebitRequest.getAmount().toBigInteger();
        if (availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode("006")
                    .responseMessage("Insufficient Balance")
                    .accountInfo(null)
                    .build();
        }
        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
            userRepository.save(userToDebit);
            return BankResponse.builder()
                    .responseCode("007")
                    .responseMessage("Account debited Successfully")
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(creditDebitRequest.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getOtherName() + " " + userToDebit.getLastName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }
}
