package com.example.Bank_system.service.impl;

import com.example.Bank_system.dto.AccountInfo;
import com.example.Bank_system.dto.BankResponse;
import com.example.Bank_system.dto.EmailDetails;
import com.example.Bank_system.dto.UserRequest;
import com.example.Bank_system.entity.User;
import com.example.Bank_system.repository.UserRepository;
import com.example.Bank_system.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

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
}
