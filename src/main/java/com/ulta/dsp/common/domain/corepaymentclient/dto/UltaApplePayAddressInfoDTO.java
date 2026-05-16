package com.ulta.dsp.common.domain.corepaymentclient.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaApplePayAddressInfoDTO {
    /** firstName for user account */
    private String firstName;

    /** lastName for user account */
    private String lastName;

    /** city for address */
    private String city;

    /** country for code */
    private String countryCode;

    /** addressLine1 for user account */
    private String addressLine1;

    /** addressLine2 for user account */
    private String addressLine2;

    /** postalCode for address */
    private String postalCode;

    /** state for address */
    private String state;

    /** phoneNumber for user account */
    private String phoneNumber;

}