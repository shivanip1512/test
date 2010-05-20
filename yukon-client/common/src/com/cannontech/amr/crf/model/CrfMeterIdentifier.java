package com.cannontech.amr.crf.model;

public class CrfMeterIdentifier {
    private String sensorManufacturer;
    private String sensorModel;
    private String sensorSerialNumber;
    
    public CrfMeterIdentifier(String sensorSerialNumber, String sensorManufacturer,
            String sensorModel) {
        this.sensorSerialNumber = sensorSerialNumber;
        this.sensorManufacturer = sensorManufacturer;
        this.sensorModel = sensorModel;
    }

    public String getSensorManufacturer() {
        return sensorManufacturer;
    }

    public void setSensorManufacturer(String sensorManufacturer) {
        this.sensorManufacturer = sensorManufacturer;
    }

    public String getSensorModel() {
        return sensorModel;
    }

    public void setSensorModel(String sensorModel) {
        this.sensorModel = sensorModel;
    }

    public String getSensorSerialNumber() {
        return sensorSerialNumber;
    }

    public void setSensorSerialNumber(String sensorSerialNumber) {
        this.sensorSerialNumber = sensorSerialNumber;
    }
    
    public String getCombinedIdentifier() {
        return String.format("%s_%s_%s", sensorManufacturer, sensorModel, sensorSerialNumber);
    }
}
