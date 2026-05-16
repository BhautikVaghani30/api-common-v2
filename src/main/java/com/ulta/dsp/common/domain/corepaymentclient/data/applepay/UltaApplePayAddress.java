/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.applepay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.data.UltaField;
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
public class UltaApplePayAddress {
    /** firstName for user account */
    private UltaField firstName;

    /** lastName for user account */
    private UltaField lastName;

    /** city for address */
    private UltaField city;

    /** country for code */
    private UltaField countryCode;

    /** addressLine1 for user account */
    private UltaField addressLine1;

    /** addressLine2 for user account */
    private UltaField addressLine2;

    /** postalCode for address */
    private UltaField postalCode;

    /** state for address */
    private UltaField state;

    /** phoneNumber for user account */
    private UltaField phoneNumber;

}