package com.cannontech.billing.format.itron;

public interface ItronClient {
    Object invoke(Object... objects) throws Exception;
}
