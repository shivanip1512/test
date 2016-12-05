package com.cannontech.common.rfn.message;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public final class RfnIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public final static RfnIdentifier BLANK = new RfnIdentifier(null,  null, null);
    
    private final String sensorManufacturer;
    private final String sensorModel;
    private final String sensorSerialNumber;
    
    public RfnIdentifier(String sensorSerialNumber, String sensorManufacturer, String sensorModel) {
        this.sensorSerialNumber = sensorSerialNumber;
        this.sensorManufacturer = sensorManufacturer;
        this.sensorModel = sensorModel;
    }
    
    public String getSensorManufacturer() {
        return sensorManufacturer;
    }

    public String getSensorModel() {
        return sensorModel;
    }

    public String getSensorSerialNumber() {
        return sensorSerialNumber;
    }

    public String getCombinedIdentifier() {
        return String.format("%s_%s_%s", sensorSerialNumber, sensorManufacturer, sensorModel);
    }

    /**
     * Returns true if NONE of the fields are blank.
     */
    public boolean isNotBlank() {
        return StringUtils.isNotBlank(sensorManufacturer) 
            && StringUtils.isNotBlank(sensorModel)
            && StringUtils.isNotBlank(sensorSerialNumber);
    }

    /**
     * Returns true if ALL of the fields are blank.
     */
    public boolean isBlank() {
        return StringUtils.isBlank(sensorManufacturer) 
            && StringUtils.isBlank(sensorModel)
            && StringUtils.isBlank(sensorSerialNumber);
    }
    
    /**
     * Returns true if ALL of the fields are blank or ALL fields are non-blank.
     */
    public boolean isValid() {
        return (isBlank() || isNotBlank());
    }

    public static RfnIdentifier createBlank() {
        return new RfnIdentifier(null, null, null);
    }

    @Override
    public String toString() {
        return String.format("RfnIdentifier [sensorManufacturer=%s, sensorModel=%s, sensorSerialNumber=%s]",
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RfnIdentifier other = (RfnIdentifier) obj;
        if (sensorManufacturer == null) {
            if (other.sensorManufacturer != null) {
                return false;
            }
        } else if (!sensorManufacturer.equals(other.sensorManufacturer)) {
            return false;
        }
        if (sensorModel == null) {
            if (other.sensorModel != null) {
                return false;
            }
        } else if (!sensorModel.equals(other.sensorModel)) {
            return false;
        }
        if (sensorSerialNumber == null) {
            if (other.sensorSerialNumber != null) {
                return false;
            }
        } else if (!sensorSerialNumber.equals(other.sensorSerialNumber)) {
            return false;
        }
        return true;
    }
    
}