package com.ulta.dsp.common.domain.corepaymentclient.data;

public interface UltaBasePaymentWithOrderId extends UltaBasePayment {
    String getOrderId();
}