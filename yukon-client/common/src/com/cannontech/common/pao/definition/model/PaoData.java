package com.cannontech.common.pao.definition.model;

import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * This class represents various generic PAO data where some or all of it might not be required.
 */
public class PaoData {
    public static enum OptionalField {
        NAME,
        ENABLED,
        METER_NUMBER,
        CARRIER_ADDRESS;
    }

    public final static Function<PaoData, PaoIdentifier> paoIdFunction =
        new Function<PaoData, PaoIdentifier>() {
            @Override
            public PaoIdentifier apply(PaoData from) {
                return from.getPaoIdentifier();
            }
        };

    private ImmutableSet<OptionalField> responseFields;

    private PaoIdentifier paoIdentifier;
    private String name;
    private boolean enabled;
    private String meterNumber;
    private Integer carrierAddress;

    /**
     * Create an instance of this class which will have at a minimum the fields specified by
     * responseFields populated.  This Set is used by this class to help error check--if a caller
     * requests a field not in responseFields, an exception will be thrown.
     */
    public PaoData(ImmutableSet<OptionalField> responseFields, PaoIdentifier paoIdentifier) {
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
}
