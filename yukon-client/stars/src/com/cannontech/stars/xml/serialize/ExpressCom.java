package com.cannontech.stars.xml.serialize;

public class ExpressCom {
    private int serviceProvider;
    private boolean hasServiceProvider;
    private int GEO;
    private boolean hasGEO;
    private int substation;
    private boolean hasSubstation;
    private int feeder;
    private boolean hasFeeder;
    private int zip;
    private boolean hasZip;
    private int userAddress;
    private boolean hasUserAddress;
    private String program;
    private String splinter;

    public ExpressCom() {

    }

    public int getFeeder() {
        return this.feeder;
    } 

    public int getGEO() {
        return this.GEO;
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

    public boolean hasFeeder() {
        return this.hasFeeder;
    }

    public boolean hasGEO() {
        return this.hasGEO;
    }

    public boolean hasServiceProvider() {
        return this.hasServiceProvider;
    }

    public boolean hasSubstation() {
        return this.hasSubstation;
    }

    public boolean hasUserAddress() {
        return this.hasUserAddress;
    }

    public boolean hasZip() {
        return this.hasZip;
    }

    public void setFeeder(int feeder) {
        this.feeder = feeder;
        this.hasFeeder = true;
    }

    public void setGEO(int GEO) {
        this.GEO = GEO;
        this.hasGEO = true;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setServiceProvider(int serviceProvider) {
        this.serviceProvider = serviceProvider;
        this.hasServiceProvider = true;
    }

    public void setSplinter(String splinter) {
        this.splinter = splinter;
    } 

    public void setSubstation(int substation) {
        this.substation = substation;
        this.hasSubstation = true;
    }

    public void setUserAddress(int userAddress) {
        this.userAddress = userAddress;
        this.hasUserAddress = true;
    }

    public void setZip(int zip) {
        this.zip = zip;
        this.hasZip = true;
    } 

}
