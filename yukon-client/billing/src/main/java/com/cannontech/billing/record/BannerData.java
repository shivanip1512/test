package com.cannontech.billing.record;

public final class BannerData {
    private final String premiseNumber;
    private final String serviceNumber;
    private final String meterNumber;
    private final String routeNumber;
    private final int scatNumber;
    
    public BannerData(String premiseNumber, String serviceNumber,
            String meterNumber, String routeNumber, int scatNumber) {
        this.premiseNumber = premiseNumber;
        this.serviceNumber = serviceNumber;
        this.meterNumber = meterNumber;
        this.routeNumber = routeNumber;
        this.scatNumber = scatNumber;
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
    public int getScatNumber() {
        return scatNumber;
    }
}