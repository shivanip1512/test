package com.cannontech.development.model;

import java.util.Map;

import org.apache.commons.collections4.map.DefaultedMap;
import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.common.rfn.model.RfnManufacturerModel;

public class RfnTestMeterReading {
    private int serialFrom = 1000;
    private Integer serialTo;
    private RfnManufacturerModel manufacturerModel = RfnManufacturerModel.RFN_420CL;
    private String manufacturerOverride;
    private String modelOverride;
    private Double value = 271828.18;
    private boolean random;
    private String uom; // V
    private Map<String, Boolean> uomModifiers = new DefaultedMap<>(false);
    private RfnMeterReadingType type;
    private Instant timestamp = new Instant();
    private boolean now = true;
    
    public RfnTestMeterReading() {
        uomModifiers.put("quad1", true);
        uomModifiers.put("quad4", true);
    }
    
    public int getSerialFrom() {
        return serialFrom;
    }
    public void setSerialFrom(int serialFrom) {
        this.serialFrom = serialFrom;
    }
    public Integer getSerialTo() {
        return serialTo;
    }
    public void setSerialTo(Integer serialTo) {
        this.serialTo = serialTo;
    }
    public RfnManufacturerModel getManufacturerModel() {
        return manufacturerModel;
    }
    public void setManufacturerModel(RfnManufacturerModel manufacturerModel) {
        this.manufacturerModel = manufacturerModel;
    }
    public String getManufacturerOverride() {
        return manufacturerOverride;
    }
    public void setManufacturerOverride(String manufacturerOverride) {
        this.manufacturerOverride = manufacturerOverride;
    }
    public String getModelOverride() {
        return modelOverride;
    }
    public void setModelOverride(String model) {
        this.modelOverride = model;
    }
    public String getUom() {
        return uom;
    }
    public void setUom(String uom) {
        this.uom = uom;
    }
    public Map<String, Boolean> getModifiers() {
        return uomModifiers;
    }
    public void setModifiers(Map<String, Boolean> uomModifiers) {
        this.uomModifiers = uomModifiers;
    }
    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }
    public boolean isRandom() {
        return random;
    }
    public void setRandom(boolean random) {
        this.random = random;
    }
    public RfnMeterReadingType getType() {
        return type;
    }
    public void setType(RfnMeterReadingType type) {
        this.type = type;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    public Long getTimestampAsMillis() {
        return timestamp.getMillis();
    }
    public boolean isNow() {
        return now;
    }
    public void setNow(boolean now) {
        this.now = now;
    }
}
