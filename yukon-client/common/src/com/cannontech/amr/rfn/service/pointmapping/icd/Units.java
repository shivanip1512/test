package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.cannontech.common.stream.StreamUtils;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Units {
    WATT_HOURS          ("Wh",                  UnitOfMeasure.KWH,   0.001),
    VAR_HOURS           ("Varh",                UnitOfMeasure.KVARH, 0.001),
    Q_HOURS             ("Qh"),
    VOLT_AMP_HOURS      ("VAh",                 UnitOfMeasure.KVAH,  0.001),
    SECONDS             ("s"),
    VOLTS               ("V",                   UnitOfMeasure.VOLTS),
    AMPS                ("A",                   UnitOfMeasure.AMPS),
    POWER_FACTOR        ("PF",                  UnitOfMeasure.PF),
    VOLTAGE_ANGLE       ("V degree",            UnitOfMeasure.DEGREES, 0.1),
    CURRENT_ANGLE       ("A degree",            UnitOfMeasure.DEGREES, 0.1),
    POWER_FACTOR_ANGLE  ("PF degree",           UnitOfMeasure.DEGREES, 0.1),
    GALLONS             ("gal",                 UnitOfMeasure.GALLONS),
    CUBIC_FEET          ("ft^3",                UnitOfMeasure.CUBIC_FEET),
    CUBIC_METERS        ("m^3",                 UnitOfMeasure.CUBIC_METERS),
    NONE                ("none"),
    WATTS               ("W",                   UnitOfMeasure.KW,   0.001),
    VARS                ("Var",                 UnitOfMeasure.KVAR, 0.001),
    Q                   ("Q",                   UnitOfMeasure.KQ,   0.001),
    VOLT_AMPS           ("VA",                  UnitOfMeasure.KVA,  0.001),
    DEGREES_CELSIUS     ("°C",                  UnitOfMeasure.TEMP_C),
    UNKNOWN             ("-"),
    OUTAGE_COUNT        ("Outage Count",        UnitOfMeasure.COUNTS),
    RESTORE_COUNT       ("Restore Count",       UnitOfMeasure.COUNTS),
    OUTAGE_BLINK_COUNT  ("Outage Blink Count",  UnitOfMeasure.COUNTS),
    RESTORE_BLINK_COUNT ("Restore Blink Count", UnitOfMeasure.COUNTS),
    DEMAND_RESET        ("Demand Reset");  //  seemingly only in rfnPointMapping.xml 
    
    private static final Map<String, Units> nameLookup;
    
    static {
        nameLookup = Stream.of(values())
            .collect(StreamUtils.mapToSelf(e -> e.commonName));
    }

    String commonName;
    UnitOfMeasure yukonUom;
    double multiplier;

    Units(String commonName) {
        this(commonName, UnitOfMeasure.UNDEF);
    }

    Units(String commonName, UnitOfMeasure uom) {
        this(commonName, uom, 1.0);
    }

    Units(String commonName, UnitOfMeasure uom, double multiplier) {
        this.commonName = commonName;
        this.yukonUom = uom;
        this.multiplier = multiplier;
    }
    
    @JsonCreator
    public static Units getByCommonName(String commonName) {
        return Optional.ofNullable(nameLookup.get(commonName))
                .orElseThrow(() -> new RuntimeException("Unknown unit name " + commonName));
    }
    
    public String getCommonName() {
        return commonName;
    }

    public UnitOfMeasure getYukonUom() {
        return yukonUom;
    }

    public double getMultiplier() {
        return multiplier;
    }
    
    public boolean isMetric() {
        return this != SECONDS;
    }
}