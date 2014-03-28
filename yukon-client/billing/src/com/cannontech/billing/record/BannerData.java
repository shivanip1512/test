package com.cannontech.billing.record;

public class BannerData {
    private String premiseNumber;
    private String serviceNumber;
    private String meterNumber;
    private String routeNumber;
    public BannerData(String premiseNumber, String serviceNumber,
            String meterNumber, String routeNumber) {
        super();
        this.premiseNumber = premiseNumber;
        this.serviceNumber = serviceNumber;
        this.meterNumber = meterNumber;
        this.routeNumber = routeNumber;
    }
    public String getPremiseNumber() {
        return premiseNumber;
    }
    public String getServiceNumber() {
        return serviceNumber;
    }
    public String getMeterNumber() {
        return meterNumber;
    }
    public String getRouteNumber() {
        return routeNumber;
    }
}