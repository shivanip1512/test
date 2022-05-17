package com.cannontech.multispeak.block.syntax.v4;

import com.cannontech.multispeak.data.v4.MspUom;
import com.cannontech.msp.beans.v4.Uom;

public enum SyntaxItem {
    METER_NUMBER("meterNo", null),
    PHYSICAL_ADDRESS("transponderID", null),
    KWH("posKWh", MspUom.kWh),
    WATER_VOLUME ("WaterVolume", MspUom.gallon),
    GAS_VOLUME ("GasVolume", MspUom.ccf),
    KWH_DATETIME("posKWhDateTime", MspUom.DateTime),
    LAST_INTERVAL_DEMAND("kW", MspUom.kW),
    LAST_INTERVAL_DEMAND_DATETIME("kWDateTime", MspUom.DateTime),
    PEAK_DEMAND("maxDemand", MspUom.kW),
    PEAK_DEMAND_DATETIME("peakDemandDateTime", MspUom.DateTime),
    LOAD_PROFILE_DEMAND("loadProfileDemand", MspUom.kW),
    LOAD_PROFILE_DEMAND_DATETIME("loadProfileDemandDateTime", MspUom.DateTime),
    KVAR("kVAR", MspUom.kVAR),
    KVAR_DATETIME("kVARDateTime", MspUom.DateTime),
    BLINK_COUNT("outageCount", MspUom.NumberPowerOutage),
    BLINK_COUNT_DATETIME("outageCountDateTime", MspUom.DateTime),
    VOLTAGE("voltage", MspUom.Volts),
    VOLTAGE_DATETIME("voltageDateTime", MspUom.DateTime),
    MIN_VOLTAGE("minVoltage", MspUom.Volts),
    MIN_VOLTAGE_DATETIME("minVoltageDateTime", MspUom.DateTime),
    MAX_VOLTAGE("maxVoltage", MspUom.Volts),
    MAX_VOLTAGE_DATETIME("maxVoltageDateTime", MspUom.DateTime),
    MULTIPLIER("multiplier", null),
    VOLTAGE_PROFILE("voltageProfile", MspUom.Volts),
    VOLTAGE_PROFILE_DATETIME("voltageProfileDateTime", MspUom.DateTime);

    private String mspFieldName;
    private Uom mspUom;

    private SyntaxItem(String mspFieldName, Uom mspUom) {
        this.mspFieldName = mspFieldName;
        this.mspUom = mspUom;
    }

    public String getMspFieldName() {
        return mspFieldName;
    }

    public Uom getMspUom() {
        return mspUom;
    }
}
