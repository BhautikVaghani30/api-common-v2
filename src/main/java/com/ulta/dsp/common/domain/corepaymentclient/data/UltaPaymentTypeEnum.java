/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.corepaymentclient.data;

/** Declaring type of Payment */

/** Enum for payment types. */
public enum UltaPaymentTypeEnum {
    CREDITCARD("creditCard"),
    PAYPAL("payPal"),
    APPLEPAY("applePay"),
    AFTERPAY("afterPay"),
    VENMO("venmo"),
    FIPAY("fiPay"),
    KLARNA("klarna");
    private String value;

    UltaPaymentTypeEnum(String value) {
        this.value = value;
    }
    /** Override to return value for Enum constant */
    @Override
    public String toString() {
        return this.value;
    }
}