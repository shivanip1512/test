package com.cannontech.database.data.point;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.ImmutableSet;

public enum UnitOfMeasure implements DisplayableEnum {
    
    INVALID(-1, "", ""),
    KW(0, "kW", "kW"),
    KWH(1, "kWH", "kWH"),
    KVA(2, "kVA", "kVA"),
    KVAR(3, "kVAr", "kVAr"),
    KVAH(4, "kVAh", "kVAh"),
    KVARH(5, "kVArh", "kVArh"),
    KVOLTS(6, "kVolts", "kVolts"),
    KQ(7, "kQ", "kQ"),
    AMPS(8, "Amps", "Amps"),
    COUNTS(9, "Counts", "Counts"),
    DEGREES(10, "Degrees", "Degrees"),
    DOLLARS(11, "Dollars", "Dollars"),
    DOLLAR_CHAR(12, "$", "Dollar Char"),
    FEET(13, "Feet", "Feet"),
    GALLONS(14, "Gallons", "Gallons"),
    GAL_PM(15, "Gal/PM", "Gal/PM"),
    GAS_CFT(16, "GAS-CFT", "GAS-CFT"),
    HOURS(17, "Hours", "Hours"),
    LEVELS(18, "Level", "Level"),
    MINUTES(19, "Minutes", "Minutes"),
    MW(20, "MW", "MW"),
    MWH(21, "MWh", "MWh"),
    MVA(22, "MVA", "MVA"),
    MVAR(23, "MVAr", "MVAr"),
    MVAH(24, "MVAh", "MVAh"),
    MVARH(25, "MVArh", "MVArh"),
    OPS(26, "Ops", "Ops"),
    PF (27, "PF", "PF"),
    PERCENT(28, "Percent", "Percent"),
    PERCENT_CHAR(29, "%", "Percent Char"),
    PSI(30, "PSI", "PSI"),
    SECONDS(31, "Seconds", "Seconds"),
    TEMP_F(32, "Temp-F", "Temp-F"),
    TEMP_C(33, "Temp-C", "Temp-C"),
    VARS(34, "Vars", "Vars"),
    VOLTS(35, "Volts", "Volts"),
    VOLTAMPS(36, "VoltAmps", "VoltAmps"),
    VA(37, "VA", "VA"),
    CUBIC_FEET(38, "ft^3", "Cubic Feet"),
    WATTS(39, "Watts", "Watts"),
    HZ(40, "Hz", "Hertz"),
    VOLTS_V2H(41, "Volts", "Volts from V2H"),
    AMPS_V2H(42, "Amps", "Amps from A2H"),
    TAP(43, "Tap", "LTC Tap Position"),
    MILES(44, "Miles", "Miles"),
    MS(45, "Ms", "Milliseconds"),
    PPM(46, "PPM", "Parts Per Million"),
    MPH(47, "MPH", "Miles Per Hour"),
    INCHES(48, "Inches", "Inches"),
    KPH(49, "KPH", "Kilometers Per Hour"),
    MILIBARS(50, "Milibars", "Milibars"),
    KH_H(51, "km/h", "Kilometers Per Hour"),
    M_S(52, "m/s", "Meters Per Second"),
    KV(53, "KV", "KVolts"),
    UNDEF(54, "UNDEF", "Undefined"),
    CUBIC_METERS(55, "m^3", "Cubic Meters"),
    MEGABYTES(56, "MB", "Megabytes"),
    DBM(57, "dBm", "Decibel-Milliwatts"),
    THERMS(58, "Therms", "Therms"),
    DB(59, "dB", "Decibels"),
    CCF(60, "CCF", "Centum Cubic Feet");
    // When adding a new UoM, remember to update displayableEnums.xml and point.xsd and creation scripts and update scripts
    
    private static final ImmutableSet<UnitOfMeasure> CAP_CONTROL_VAR_UOM = ImmutableSet.of(KVAR, VARS, MVAR, KQ);
    private static final ImmutableSet<UnitOfMeasure> CAP_CONTROL_WATTS_UOM = ImmutableSet.of(KW, MW, WATTS);
    private static final ImmutableSet<UnitOfMeasure> CAP_CONTROL_VOLTS_UOM = ImmutableSet.of(KVOLTS, VOLTS, VOLTS_V2H, KV);
    private static final ImmutableSet<UnitOfMeasure> DURATION = ImmutableSet.of(HOURS, MINUTES, SECONDS, MS);
    private static final String keyBase = "yukon.common.unitOfMeasure.";
    
    private static List<UnitOfMeasure> values;
    private int id;
    private String abbreviation;
    private String longName;
     
    private UnitOfMeasure(int id, String abbreviation, String longName) {
        this.id = id;
        this.abbreviation = abbreviation;
        this.longName = longName;
    }
    
    @Override
    public String getFormatKey() {
        return keyBase + name();
    }
    
    public int getId() {
        return id;
    }
    
    public String getAbbreviation() {
        return abbreviation;
    }
    
    public String getLongName() {
        return longName;
    }
    
    public boolean isCapControlVar() {
        return CAP_CONTROL_VAR_UOM.contains(this);
    }
    
    public boolean isCapControlWatt() {
        return CAP_CONTROL_WATTS_UOM.contains(this);
    }
    
    public boolean isCapControlVolt() {
        return CAP_CONTROL_VOLTS_UOM.contains(this);
    }
    
    public boolean isDuration() {
        return DURATION.contains(this);
    }
    
    public static UnitOfMeasure getForId(int id) {
        for (UnitOfMeasure uom : values()) {
            if (uom.getId() == id) {
                return uom;
            }
        }
        throw new NotFoundException("No UoM found for id: " + id);
    }
    
    public static ImmutableSet<UnitOfMeasure> getCapControlVarUom() {
        return CAP_CONTROL_VAR_UOM;
    }
    
    public static ImmutableSet<UnitOfMeasure> getCapControlVoltsUom() {
        return CAP_CONTROL_VOLTS_UOM;
    }
    
    public static ImmutableSet<UnitOfMeasure> getCapControlWattsUom() {
        return CAP_CONTROL_WATTS_UOM;
    }
    
    @Override
    public String toString() {
        return longName;
    }
    
    public static List<UnitOfMeasure> allValidValues() {
        if (values == null) {
            values = Arrays.stream(values())
                           .filter(uom -> uom != UnitOfMeasure.INVALID)
                           .collect(Collectors.toList());
        }
        return values;
    }
}