package com.cannontech.amr.crf.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class CrfMeterIdentifier implements Serializable{
    
    private static final long serialVersionUID = 2L;
    
    private String sensorManufacturer;
    private String sensorModel;
    private String sensorSerialNumber;
    
    public CrfMeterIdentifier(String sensorSerialNumber, String sensorManufacturer, String sensorModel) {
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
        /* TODO Temporary: for now we only send manufacturer and serial number 
        return String.format("%s_%s_%s", sensorManufacturer, sensorModel, sensorSerialNumber); */
        return String.format("%s_%s", sensorManufacturer, sensorSerialNumber); 
    }

    
    public boolean isBlank() {
        return StringUtils.isBlank(sensorManufacturer) 
            && StringUtils.isBlank(sensorModel)
            && StringUtils.isBlank(sensorSerialNumber);
    }
    
    public static CrfMeterIdentifier createBlank() {
        return new CrfMeterIdentifier(null, null, null);
    }

    @Override
    public String toString() {
        return String.format("CrfMeterIdentifier [sensorManufacturer=%s, sensorModel=%s, sensorSerialNumber=%s]",
                             sensorManufacturer,
                             sensorModel,
                             sensorSerialNumber);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sensorManufacturer == null) ? 0 : sensorManufacturer.hashCode());
        result = prime * result + ((sensorModel == null) ? 0 : sensorModel.hashCode());
        result = prime * result + ((sensorSerialNumber == null) ? 0 : sensorSerialNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CrfMeterIdentifier other = (CrfMeterIdentifier) obj;
        if (sensorManufacturer == null) {
            if (other.sensorManufacturer != null)
                return false;
        } else if (!sensorManufacturer.equals(other.sensorManufacturer))
            return false;
        if (sensorModel == null) {
            if (other.sensorModel != null)
                return false;
        } else if (!sensorModel.equals(other.sensorModel))
            return false;
        if (sensorSerialNumber == null) {
            if (other.sensorSerialNumber != null)
                return false;
        } else if (!sensorSerialNumber.equals(other.sensorSerialNumber))
            return false;
        return true;
    }
    
    
}