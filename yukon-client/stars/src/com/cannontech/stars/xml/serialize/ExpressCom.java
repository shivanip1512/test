package com.cannontech.stars.xml.serialize;

public class ExpressCom {
    private int serviceProvider = 0;
    private int geo = 0;
    private int substation = 0;
    private int feeder = 0;
    private int zip = 0;
    private int userAddress = 0;
    private String program = null;
    private String splinter = null;

    public int getFeeder() {
        return this.feeder;
    } 

    public int getGEO() {
        return this.geo;
    }

    public String getProgram() {
        return this.program;
    }

    public int getServiceProvider() {
        return this.serviceProvider;
    }

    public String getSplinter() {
        return this.splinter;
    } 

    public int getSubstation() {
        return this.substation;
    }

    public int getUserAddress() {
        return this.userAddress;
    }

    public int getZip() {
        return this.zip;
    }

    public void setFeeder(int feeder) {
        this.feeder = feeder;
    }

    public void setGEO(int geo) {
        this.geo = geo;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setServiceProvider(int serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public void setSplinter(String splinter) {
        this.splinter = splinter;
    } 

    public void setSubstation(int substation) {
        this.substation = substation;
    }

    public void setUserAddress(int userAddress) {
        this.userAddress = userAddress;
    }

    public void setZip(int zip) {
        this.zip = zip;
    } 
}
