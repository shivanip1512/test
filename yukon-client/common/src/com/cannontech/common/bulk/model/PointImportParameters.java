package com.cannontech.common.bulk.model;

import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.csvImport.types.StrictBoolean;
import com.cannontech.common.pao.ImportPaoType;
import com.cannontech.common.point.AccumulatorType;
import com.cannontech.common.util.PositiveDouble;
import com.cannontech.common.util.PositiveInteger;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.ControlStateType;
import com.cannontech.database.data.point.StatusControlType;

public enum PointImportParameters {
    ACTION("ACTION", ImportAction.class, "yukon.web.modules.tools.bulk.pointImport.column.action", "yukon.web.modules.tools.bulk.pointImport.validValues.action"),
    DEVICE_NAME("DEVICE NAME", String.class, "yukon.web.modules.tools.bulk.pointImport.column.deviceName"),
    DEVICE_TYPE("DEVICE TYPE", ImportPaoType.class, "yukon.web.modules.tools.bulk.pointImport.column.deviceType", "yukon.web.modules.tools.bulk.pointImport.validValues.deviceType"),
    POINT_NAME("POINT NAME", String.class, "yukon.web.modules.tools.bulk.pointImport.column.pointName"),
    DISABLED("DISABLED", StrictBoolean.class, "yukon.web.modules.tools.bulk.pointImport.column.disabled"),
    STALE_DATA_TIME("STALE DATA TIME", PositiveInteger.class, "yukon.web.modules.tools.bulk.pointImport.column.staleDataTime"),
    STALE_DATA_UPDATE("STALE DATA UPDATE", StaleDataUpdateStyle.class, "yukon.web.modules.tools.bulk.pointImport.column.staleDataUpdate"),
    ARCHIVE_TYPE("ARCHIVE TYPE", PointArchiveType.class, "yukon.web.modules.tools.bulk.pointImport.column.archiveType"),
    HIGH_REASONABILITY("HIGH REASONABILITY", Double.class, "yukon.web.modules.tools.bulk.pointImport.column.highReasonability"),
    LOW_REASONABILITY("LOW REASONABILITY", Double.class, "yukon.web.modules.tools.bulk.pointImport.column.lowReasonability"),
    HIGH_LIMIT_1("HIGH LIMIT 1", Double.class, "yukon.web.modules.tools.bulk.pointImport.column.highLimit1"),
    LOW_LIMIT_1("LOW LIMIT 1", Double.class, "yukon.web.modules.tools.bulk.pointImport.column.lowLimit1"),
    LIMIT_DURATION_1("LIMIT DURATION 1", PositiveInteger.class, "yukon.web.modules.tools.bulk.pointImport.column.limitDuration1"),
    HIGH_LIMIT_2("HIGH LIMIT 2", Double.class, "yukon.web.modules.tools.bulk.pointImport.column.highLimit2"),
    LOW_LIMIT_2("LOW LIMIT 2", Double.class, "yukon.web.modules.tools.bulk.pointImport.column.lowLimit2"),
    LIMIT_DURATION_2("LIMIT DURATION 2", PositiveInteger.class, "yukon.web.modules.tools.bulk.pointImport.column.limitDuration2"),
    ARCHIVE_INTERVAL("ARCHIVE INTERVAL", ImportPointArchiveInterval.class, "yukon.web.modules.tools.bulk.pointImport.column.archiveInterval", "yukon.web.modules.tools.bulk.pointImport.validValues.archiveInterval"),
    UNIT_OF_MEASURE("UNIT OF MEASURE", ImportUnitOfMeasure.class, "yukon.web.modules.tools.bulk.pointImport.column.unitOfMeasure", "yukon.web.modules.tools.bulk.pointImport.validValues.unitOfMeasure"),
    DECIMAL_PLACES("DECIMAL PLACES", PositiveInteger.class, "yukon.web.modules.tools.bulk.pointImport.column.decimalPlaces"),
    POINT_OFFSET("POINT OFFSET", PositiveInteger.class, "yukon.web.modules.tools.bulk.pointImport.column.pointOffset"),
    DEADBAND("DEADBAND", PositiveDouble.class, "yukon.web.modules.tools.bulk.pointImport.column.deadband"),
    MULTIPLIER("MULTIPLIER", PositiveDouble.class, "yukon.web.modules.tools.bulk.pointImport.column.multiplier"),
    DATA_OFFSET("DATA OFFSET", Double.class, "yukon.web.modules.tools.bulk.pointImport.column.dataOffset"),
    METER_DIALS("METER DIALS", PositiveInteger.class, "yukon.web.modules.tools.bulk.pointImport.column.meterDials"),
    CONTROL_TYPE("CONTROL TYPE", StatusControlType.class, "yukon.web.modules.tools.bulk.pointImport.column.controlType"),
    ARCHIVE_DATA("ARCHIVE DATA", StrictBoolean.class, "yukon.web.modules.tools.bulk.pointImport.column.archiveData"),
    CONTROL_INHIBIT("CONTROL INHIBIT", StrictBoolean.class, "yukon.web.modules.tools.bulk.pointImport.column.controlInhibit"),
    STATE_GROUP("STATE GROUP", String.class, "yukon.web.modules.tools.bulk.pointImport.column.stateGroup", "yukon.web.modules.tools.bulk.pointImport.validValues.stateGroup"),
    INITIAL_STATE("INITIAL STATE", String.class, "yukon.web.modules.tools.bulk.pointImport.column.initialState", "yukon.web.modules.tools.bulk.pointImport.validValues.initialState"),
    CONTROL_POINT_OFFSET("CONTROL POINT OFFSET", Integer.class, "yukon.web.modules.tools.bulk.pointImport.column.controlPointOffset"),
    CLOSE_TIME_1("CLOSE TIME 1", Integer.class, "yukon.web.modules.tools.bulk.pointImport.column.closeTime1"),
    CLOSE_TIME_2("CLOSE TIME 2", Integer.class, "yukon.web.modules.tools.bulk.pointImport.column.closeTime2"),
    STATE_1_COMMAND("STATE 1 COMMAND", ControlStateType.class, "yukon.web.modules.tools.bulk.pointImport.column.state1Command"),
    STATE_2_COMMAND("STATE 2 COMMAND", ControlStateType.class, "yukon.web.modules.tools.bulk.pointImport.column.state2Command"),
    COMMAND_TIMEOUT("COMMAND TIMEOUT", PositiveInteger.class, "yukon.web.modules.tools.bulk.pointImport.column.commandTimeout"),
    ACCUMULATOR_TYPE("ACCUMULATOR TYPE", AccumulatorType.class, "yukon.web.modules.tools.bulk.pointImport.column.accumulatorType"),
    ANALOG_UPDATE_TYPE("UPDATE TYPE", AnalogPointUpdateType.class, "yukon.web.modules.tools.bulk.pointImport.column.updateType"),
    STATUS_UPDATE_TYPE("UPDATE TYPE", StatusPointUpdateType.class, "yukon.web.modules.tools.bulk.pointImport.column.updateType"),
    CALCULATION("CALCULATION", String.class, "yukon.web.modules.tools.bulk.pointImport.column.calculation"),
    FORCE_QUALITY_NORMAL("FORCE QUALITY NORMAL", StrictBoolean.class, "yukon.web.modules.tools.bulk.pointImport.column.forceQualityNormal"),
    UPDATE_RATE("UPDATE RATE", PointPeriodicRate.class, "yukon.web.modules.tools.bulk.pointImport.column.updateRate"),
    ;
    
    public final String NAME;
    public final Class<?> CLASS;
    public final String DESCRIPTION;
    public final String VALUES;
    
    private PointImportParameters(String name, Class<?> typeClass, String description) {
        this.NAME = name;
        this.CLASS = typeClass;
        this.DESCRIPTION = description;
        this.VALUES = null;
    }
    
    private PointImportParameters(String name, Class<?> typeClass, String description, String validValues) {
        this.NAME = name;
        this.CLASS = typeClass;
        this.DESCRIPTION = description;
        this.VALUES = validValues;
    }
}
