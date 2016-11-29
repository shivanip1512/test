package com.cannontech.broker.message.request;

import java.io.Serializable;

import com.cannontech.broker.model.BrokerSystemMetricsAttribute;

/**
 * Request for broker system metrics.
 */
public class BrokerSystemMetricsRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private BrokerSystemMetricsAttribute systemMetricsAttribute;

    public BrokerSystemMetricsRequest(BrokerSystemMetricsAttribute systemMetricsAttribute) {
        this.systemMetricsAttribute = systemMetricsAttribute;
    }

    public BrokerSystemMetricsAttribute getSystemMetricsAttribute() {
        return systemMetricsAttribute;
    }
}
