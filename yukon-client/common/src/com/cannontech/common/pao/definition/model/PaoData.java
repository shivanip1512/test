package com.cannontech.common.pao.definition.model;

import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.google.common.collect.ImmutableSet;

/**
 * This class represents various generic PAO data where some or all of it might not be required.
 */
public class PaoData {
    public static enum OptionalField {
        NAME,
        ENABLED,
        METER_NUMBER,
        CARRIER_ADDRESS,
        ROUTE_NAME,
        TYPE,
        /**
         * A field which will be the address for PLC devices or the serial number for RFN devices.
         */
        ADDRESS_OR_SERIAL_NUMBER,
        LATITUDE,
        LONGITUDE,
        ;

        public final static Set<OptionalField> SET_OF_ALL = ImmutableSet.copyOf(values());
    }

    private Set<OptionalField> responseFields;

    private PaoIdentifier paoIdentifier;
    private String name;
    private String type;
    private boolean enabled;
    private String meterNumber;
    private Integer carrierAddress;
    private String routeName;
    private String addressOrSerialNumber;
    private String latitude;
    private String longitude;

    /**
     * Create an instance of this class which will have at a minimum the fields specified by
     * responseFields populated.  This Set is used by this class to help error check--if a caller
     * requests a field not in responseFields, an exception will be thrown.
     */
    public PaoData(Set<OptionalField> responseFields, PaoIdentifier paoIdentifier) {
        this.responseFields = responseFields;
        this.paoIdentifier = paoIdentifier;
    }

    public Set<OptionalField> getRequestedFields() {
        return responseFields;
    }

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public String getName() {
        if (responseFields.contains(OptionalField.NAME)) {
            return name;
        }
        throw new UnsupportedOperationException("name not populated for this object");
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        if (responseFields.contains(OptionalField.TYPE)) {
            return type;
        }
        throw new UnsupportedOperationException("name not populated for this object");
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnabled() {
        if (responseFields.contains(OptionalField.ENABLED)) {
            return enabled;
        }
        throw new UnsupportedOperationException("enabled not populated for this object");
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMeterNumber() {
        if (responseFields.contains(OptionalField.METER_NUMBER)) {
            return meterNumber;
        }
        throw new UnsupportedOperationException("meterNumber not populated for this object");
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public Integer getCarrierAddress() {
        if (responseFields.contains(OptionalField.CARRIER_ADDRESS)) {
            return carrierAddress;
        }
        throw new UnsupportedOperationException("carrierAddress not populated for this object");
    }

    public void setCarrierAddress(Integer carrierAddress) {
        this.carrierAddress = carrierAddress;
    }

    public String getRouteName() {
        if (responseFields.contains(OptionalField.ROUTE_NAME)) {
            return routeName;
        }
        throw new UnsupportedOperationException("routeName not populated for this object");
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getAddressOrSerialNumber() {
        if (responseFields.contains(OptionalField.ADDRESS_OR_SERIAL_NUMBER)) {
            return addressOrSerialNumber;
        }
        throw new UnsupportedOperationException("addressOrSerialNumber not populated for this object");
    }

    public void setAddressOrSerialNumber(String addressOrSerialNumber) {
        this.addressOrSerialNumber = addressOrSerialNumber;
    }

    public String getLatitude() {
        if (responseFields.contains(OptionalField.LATITUDE)) {
            return latitude;
        }
        throw new UnsupportedOperationException("latitude not populated for this object");
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        if (responseFields.contains(OptionalField.LONGITUDE)) {
            return longitude;
        }
        throw new UnsupportedOperationException("longitude not populated for this object");
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
