package com.cannontech.simulators.message.request;

import org.joda.time.Instant;

import com.cannontech.simulators.message.response.SimulatorResponseBase;

/**
 * Request to simulator to get statistics
 */
public class EatonCloudSimulatorStatisticsResponse extends SimulatorResponseBase {
    private static final long serialVersionUID = 1L;

    private String token1;
    private String token2;

    public EatonCloudSimulatorStatisticsResponse(String token1, String token2) {
        this.token1 = token1;
        this.token2 = token2;
    }

    public String getToken1() {
        return token1;
    }
    
    public String getToken2() {
        return token2;
    }
}
