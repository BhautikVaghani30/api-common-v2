package com.ulta.dsp.common.domain.corepaymentclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class UltaProfilePaymentDTO {
    private String profileId;
    private String tokenizedCard;
    private String cardType;
    private String expirationYear;
    private String expirationMonth;
    private String _id;
    private String nickName;
    private String name;
    private String maskedPAN;
    private String last4CardNo;
    private String firstName;
    private String lastName;
    private String address1;
    private String phone;
    private String postalCode;
    private String city;
    private String state;
    private String country;
    private boolean primary;
}