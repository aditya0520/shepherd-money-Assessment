package com.shepherdmoney.interviewproject.vo.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class AddCreditCardToUserPayload {

    private int userId;

    private String cardIssuanceBank;

    private String cardNumber;


}
