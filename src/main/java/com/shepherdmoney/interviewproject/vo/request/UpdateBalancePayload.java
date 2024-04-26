package com.shepherdmoney.interviewproject.vo.request;

import java.time.LocalDate;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class UpdateBalancePayload {

    private String creditCardNumber;
    
    private LocalDate balanceDate;

    private double balanceAmount;
}
