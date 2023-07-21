package com.example.Bank_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {

    @Schema(
            name = "User account name"
    )
    private String accountName;
    private BigDecimal accountBalance;
    private String accountNumber;

}
