package com.cannontech.capcontrol;

public class BankLocation
{
    private String name;
    private long serialNumber;
    private String address;
    private String directions;

    public BankLocation(String name, long serialNumber, String address, String directions) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.address = address;
        this.directions = directions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }
}
