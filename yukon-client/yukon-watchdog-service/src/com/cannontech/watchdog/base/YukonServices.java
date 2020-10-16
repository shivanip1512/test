package com.cannontech.watchdog.base;

public enum YukonServices {
    MESSAGEBROKER(2),
    SERVICEMANAGER(2),
    NOTIFICATIONSERVICE(2),
    WEBSERVER(2),
    DISPATCH(2),
    REALTIMESCANNER(2),
    PORTER(2),
    MACS(3),
    LOADMANAGEMENT(2),
    FDR(2),
    CAPCONTROL(3),
    CALCLOGIC(2),
    NETWORKMANAGER(2),
    ITRON(2),
    DATABASE(0);

    private Integer failedPollThreshold;

    private YukonServices(Integer failedPollThreshold) {
        this.failedPollThreshold = failedPollThreshold;
    }

    public Integer getFailedPollThreshold() {
        return this.failedPollThreshold;
    }
}
