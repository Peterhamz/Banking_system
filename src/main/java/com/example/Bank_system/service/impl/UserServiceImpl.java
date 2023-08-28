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

    @Autowired
    TransactionService transactionService;

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

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(creditDebitRequest.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode("005")
                .responseMessage("Account Credited successfully")
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
        // Check if the account exists
        // check if the amount you tend to withdraw is not greater than the current account balance
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

            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(creditDebitRequest.getAmount())
                    .build();

            transactionService.saveTransaction(transactionDto);

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

    @Override
    public BankResponse transfer(TransferRequest transferRequest) {
        // Get the account to debit
        // check if the amount i'm debiting is not more that the current balance
        // debit the account
        // get the account to credit and credit the account

        boolean isDestinationAccount = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());

        if (!isDestinationAccount){
            return BankResponse.builder()
                    .responseCode("004")
                    .responseMessage("Account do not exist")
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountNumber = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());

        if (transferRequest.getAmount().compareTo(sourceAccountNumber.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode("006")
                    .responseMessage("Insufficient Balance")
                    .accountInfo(null)
                    .build();
        }
        sourceAccountNumber.setAccountBalance(sourceAccountNumber.getAccountBalance().subtract(transferRequest.getAmount()));
        String sourceAccount = sourceAccountNumber.getLastName() + " " + sourceAccountNumber.getFirstName() + " " + sourceAccountNumber.getOtherName();
        userRepository.save(sourceAccountNumber);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountNumber.getEmail())
                .messageBody("The sum of " + transferRequest.getAmount() + " has been deducted from your account! Your account balance is " + sourceAccountNumber.getAccountBalance())
                .build();
        emailService.sendEmail(debitAlert);

        User destinationAccountUser = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(transferRequest.getAmount()));
        //String destination = destinationAccountUser.getLastName() + " " + destinationAccountUser.getFirstName() + " " + destinationAccountUser.getOtherName();
        userRepository.save(destinationAccountUser);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The sum of " + transferRequest.getAmount() + " Has been sent to your account! from " + sourceAccount + " Your account balance is " + destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmail(creditAlert);


        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(transferRequest.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode("009")
                .responseMessage("Transaction successful")
                .accountInfo(null)
                .build();
    }
}
