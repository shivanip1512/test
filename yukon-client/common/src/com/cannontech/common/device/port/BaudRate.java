package com.cannontech.common.device.port;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.ControlPriority;
import com.google.common.collect.ImmutableMap;

public enum BaudRate {

    BAUD_300(300),
    BAUD_1200(1200),
    BAUD_2400(2400),
    BAUD_4800(4800),
    BAUD_9600(9600),
    BAUD_14400(14400),
    BAUD_28800(28000),
    BAUD_38400(38400),
    BAUD_57600(57600),
    BAUD_115200(115200);

    private Integer baudRate;

    private final static Logger log = YukonLogManager.getLogger(BaudRate.class);
    private final static ImmutableMap<Integer, BaudRate> lookupByRate;

    static {
        try {
            ImmutableMap.Builder<Integer, BaudRate> priorityBuilder = ImmutableMap.builder();
            for (BaudRate rate : values()) {
                priorityBuilder.put(rate.baudRate, rate);
            }
            lookupByRate = priorityBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps for baud rate.", e);
            throw e;
        }
    }

    public static BaudRate getForRate(Integer rate) {
        BaudRate baudRate = lookupByRate.get(rate);
        checkArgument(baudRate != null, baudRate);
        return baudRate;
    }

    public Integer getBaudRateValue() {
        return baudRate;
    }

    BaudRate(Integer baudRate) {
        this.baudRate = baudRate;
    }
}
