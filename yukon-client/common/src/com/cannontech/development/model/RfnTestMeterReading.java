package com.cannontech.development.model;

import java.util.Map;

import org.apache.commons.collections4.map.DefaultedMap;
import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;

public class RfnTestMeterReading {
    private int serialFrom = 1000;
    private int serialTo = 1000;
    private String manufacturer;
    private String model;
    private Double value;
    private boolean random;
    private String uom; // V
    private Map<String, Boolean> uomModifiers = new DefaultedMap<>(false);
    private RfnMeterReadingType type;
    private Instant timestamp = new Instant();
    
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
    public int getSerialTo() {
        return serialTo;
    }
    public void setSerialTo(int serialTo) {
        this.serialTo = serialTo;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
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

}
