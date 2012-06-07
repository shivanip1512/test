package com.cannontech.stars.xml.serialize;

public class Generator {
    private TransferSwitchType transferSwitchType;
    private TransferSwitchManufacturer transferSwitchManufacturer;
    private int peakKWCapacity;
    private boolean hasPeakKWCapacity;
    private int fuelCapGallons;
    private boolean hasFuelCapGallons;
    private int startDelaySeconds;
    private boolean hasStartDelaySeconds;

    public Generator() {
        this.transferSwitchType = new TransferSwitchType();
        this.transferSwitchManufacturer = new TransferSwitchManufacturer();
    }

    public void deleteFuelCapGallons() {
        this.hasFuelCapGallons = false;
    }

    public void deletePeakKWCapacity() {
        this.hasPeakKWCapacity = false;
    }

    public void deleteStartDelaySeconds() {
        this.hasStartDelaySeconds = false;
    } 

    public int getFuelCapGallons() {
        return this.fuelCapGallons;
    }

    public int getPeakKWCapacity() {
        return this.peakKWCapacity;
    }

    public int getStartDelaySeconds() {
        return this.startDelaySeconds;
    }

    public TransferSwitchManufacturer getTransferSwitchManufacturer() {
        return this.transferSwitchManufacturer;
    }

    public TransferSwitchType getTransferSwitchType() {
        return this.transferSwitchType;
    }

    public boolean hasFuelCapGallons() {
        return this.hasFuelCapGallons;
    }

    public boolean hasPeakKWCapacity() {
        return this.hasPeakKWCapacity;
    }
    
    public boolean hasStartDelaySeconds() {
        return this.hasStartDelaySeconds;
    }

    public void setFuelCapGallons(int fuelCapGallons) {
        this.fuelCapGallons = fuelCapGallons;
        this.hasFuelCapGallons = true;
    }

    public void setPeakKWCapacity(int peakKWCapacity) {
        this.peakKWCapacity = peakKWCapacity;
        this.hasPeakKWCapacity = true;
    }

    public void setStartDelaySeconds(int startDelaySeconds) {
        this.startDelaySeconds = startDelaySeconds;
        this.hasStartDelaySeconds = true;
    }

    public void setTransferSwitchManufacturer(TransferSwitchManufacturer transferSwitchManufacturer) {
        this.transferSwitchManufacturer = transferSwitchManufacturer;
    }

    public void setTransferSwitchType(TransferSwitchType transferSwitchType) {
        this.transferSwitchType = transferSwitchType;
    }

}
