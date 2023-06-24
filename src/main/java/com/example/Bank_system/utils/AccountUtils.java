package com.example.Bank_system.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtils {

    public static String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10);
            accountNumber.append(digit);
        }
        return accountNumber.toString();
    }



    public static String generateAccountNo(){
        /**
         * 2023 + randomSixDigit (100,000 - 999,000)
         *  for random ten digits randomTenDigit (100,000,000,000 - 999,000,000,000)
         */
        Year currentYear = Year.now();

        int min = 100000;
        int max = 999999;

        int randNumber = (int) Math.floor(Math.random() * (max-min + 1) + min);

        // convert the current year and randNumber to string and then concatenate them together

        String year = String.valueOf(currentYear);
        String randomNum = String.valueOf(randNumber);

        StringBuilder accountNumber = new StringBuilder();
        accountNumber.append(year).append(randomNum);

        return accountNumber.toString();

    }

}
