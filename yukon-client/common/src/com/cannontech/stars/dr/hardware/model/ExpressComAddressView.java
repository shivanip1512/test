package com.cannontech.stars.dr.hardware.model;

public class ExpressComAddressView {

    private int groupId;
    private String usage;
    private String serialNumber;
    private int routeId;
    private int spid;
    private int geo;
    private int substation;
    private int feeder;
    private int zip;
    private int user;
    private int program;
    private int splinter;
    private String relay;
    private int priority;
    
    public int getGroupId() {
        return groupId;
    }
    
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    public String getUsage() {
        return usage;
    }
    
    public void setUsage(String usage) {
        this.usage = usage;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public int getRouteId() {
        return routeId;
    }
    
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
    
    public int getSpid() {
        return spid;
    }
    
    public void setSpid(int spid) {
        this.spid = spid;
    }
    
    public int getGeo() {
        return geo;
    }
    
    public void setGeo(int geo) {
        this.geo = geo;
    }
    
    public int getSubstation() {
        return substation;
    }
    
    public void setSubstation(int substation) {
        this.substation = substation;
    }
    
    public int getFeeder() {
        return feeder;
    }
    
    public void setFeeder(int feeder) {
        this.feeder = feeder;
    }
    
    public int getZip() {
        return zip;
    }
    
    public void setZip(int zip) {
        this.zip = zip;
    }
    
    public int getUser() {
        return user;
    }
    
    public void setUser(int user) {
        this.user = user;
    }
    
    public int getProgram() {
        return program;
    }
    
    public void setProgram(int program) {
        this.program = program;
    }
    
    public int getSplinter() {
        return splinter;
    }
    
    public void setSplinter(int splinter) {
        this.splinter = splinter;
    }
    
    public String getRelay() {
        return relay;
    }
    
    public void setRelay(String relay) {
        this.relay = relay;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return String
            .format("ExpressComAddressView [groupId=%s, usage=%s, serialNumber=%s, routeId=%s, spid=%s, geo=%s, substation=%s, feeder=%s, zip=%s, user=%s, program=%s, splinter=%s, relay=%s, priority=%s]",
                    groupId,
                    usage,
                    serialNumber,
                    routeId,
                    spid,
                    geo,
                    substation,
                    feeder,
                    zip,
                    user,
                    program,
                    splinter,
                    relay,
                    priority);
    }
    
    
    
}