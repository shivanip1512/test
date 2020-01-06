package com.cannontech.multispeak.block.syntax.v5;

import com.cannontech.msp.beans.v5.enumerations.FieldNameKind;
import com.cannontech.msp.beans.v5.enumerations.UomKind;
import com.cannontech.multispeak.data.v5.MspUom;

public enum SyntaxItem {
    METER_NUMBER("meterNo", null, FieldNameKind.METER_NUMBER),
    PHYSICAL_ADDRESS("transponderID", null, FieldNameKind.COMMUNICATIONS_ADDRESS),
    KWH("posKWh", MspUom.kWh, FieldNameKind.POS_K_WH),
    KWH_DATETIME("posKWhDateTime", MspUom.DateTime, FieldNameKind.POS_K_WH_DATE_TIME),
    LAST_INTERVAL_DEMAND("kW", MspUom.kW, FieldNameKind.CUMULATIVE_DEMAND),
    LAST_INTERVAL_DEMAND_DATETIME("kWDateTime", MspUom.DateTime, FieldNameKind.CUMULATIVE_DEMAND_DATE_TIME),
    PEAK_DEMAND("peakDemand", MspUom.kW, FieldNameKind.MAX_DEMAND),
    PEAK_DEMAND_DATETIME("peakDemandDateTime", MspUom.DateTime, FieldNameKind.MAX_DEMAND_DATE_TIME),
    LOAD_PROFILE_DEMAND("loadProfileDemand", MspUom.kW, FieldNameKind.OTHER),
    LOAD_PROFILE_DEMAND_DATETIME("loadProfileDemandDateTime", MspUom.DateTime, FieldNameKind.OTHER),
    KVAR("kVAR", MspUom.kVAR, FieldNameKind.OTHER),
    KVAR_DATETIME("kVARDateTime", MspUom.DateTime, FieldNameKind.OTHER),
    BLINK_COUNT("outageCount", MspUom.NumberPowerOutage, FieldNameKind.SUSTAINED_OUTAGE),
    BLINK_COUNT_DATETIME("outageCountDateTime", MspUom.DateTime, FieldNameKind.OTHER),
    VOLTAGE("voltage", MspUom.Volts, FieldNameKind.VOLTAGE),
    VOLTAGE_DATETIME("voltageDateTime", MspUom.DateTime, FieldNameKind.OTHER),
    MIN_VOLTAGE("minVoltage", MspUom.Volts, FieldNameKind.MIN_VOLTAGE),
    MIN_VOLTAGE_DATETIME("minVoltageDateTime", MspUom.DateTime, FieldNameKind.OTHER),
    MAX_VOLTAGE("maxVoltage", MspUom.Volts, FieldNameKind.MAX_VOLTAGE),
    MAX_VOLTAGE_DATETIME("maxVoltageDateTime", MspUom.DateTime, FieldNameKind.OTHER),
    MULTIPLIER("multiplier", null, FieldNameKind.OTHER),
    VOLTAGE_PROFILE("voltageProfile", MspUom.Volts, FieldNameKind.OTHER),
    VOLTAGE_PROFILE_DATETIME("voltageProfileDateTime", MspUom.DateTime, FieldNameKind.OTHER),
    PROGRAM_NAME("programName", null, FieldNameKind.OTHER),
    CURRENT_STATUS("currentStatus", null, FieldNameKind.OTHER),
    START_DATETIME("startDateTime", MspUom.DateTime, FieldNameKind.OTHER),
    STOP_DATETIME("stopDateTime", MspUom.DateTime, FieldNameKind.OTHER),
    GEAR_CHANGETIME("gearChangeTime", MspUom.DateTime, FieldNameKind.OTHER),
    GEAR_NAME("gearName", null, FieldNameKind.OTHER),
    CORRELATION_ID("correlationId", null, FieldNameKind.OTHER);

    private String mspFieldName;
    private UomKind mspUom;
    private FieldNameKind mspFieldNameKind;

    private SyntaxItem(String mspFieldName, UomKind mspUom, FieldNameKind mspFieldNameKind) {
        this.mspFieldName = mspFieldName;
        this.mspUom = mspUom;
        this.mspFieldNameKind = mspFieldNameKind;
    }

    public String getMspFieldName() {
        return mspFieldName;
    }

    public UomKind getMspUom() {
        return mspUom;
    }

    public FieldNameKind getMspFieldNameKind() {
        return mspFieldNameKind;
    }

}
