package com.cannontech.common.bulk.model;

import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.types.StrictBoolean;
import com.cannontech.common.pao.ImportPaoType;
import com.cannontech.common.point.AccumulatorType;
import com.cannontech.common.util.PositiveDouble;
import com.cannontech.common.util.PositiveInteger;
import com.cannontech.database.data.point.ControlType;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.StateControlType;

/**
 * ImportFileFormats for each type of point import.
 */
public class PointImportFormats {
    private static ImportFileFormat basePointImportFormat;
    private static ImportFileFormat analogAccumulatorSharedImportFormat;
    public static ImportFileFormat ANALOG_POINT_FORMAT;
    public static ImportFileFormat STATUS_POINT_FORMAT;
    public static ImportFileFormat ACCUMULATOR_POINT_FORMAT;
    public static ImportFileFormat CALC_ANALOG_POINT_FORMAT;
    public static ImportFileFormat CALC_STATUS_POINT_FORMAT;
    public static ImportFileFormat CALCULATION_FORMAT;
    
    static {
        basePointImportFormat = new ImportFileFormat();
        //Required Columns
        basePointImportFormat.addRequiredColumn("ACTION", ImportAction.class, false, true);
        basePointImportFormat.addRequiredColumn("DEVICE NAME", String.class);
        basePointImportFormat.addRequiredColumn("DEVICE TYPE", ImportPaoType.class);
        basePointImportFormat.addRequiredColumn("POINT NAME", String.class);
        //Optional Columns
        basePointImportFormat.addOptionalColumn("DISABLED", StrictBoolean.class);
        //Grouped Columns
        basePointImportFormat.addGroupedColumn("STALE DATA TIME", PositiveInteger.class, "STALE DATA");
        basePointImportFormat.addGroupedColumn("STALE DATA UPDATE", StaleDataUpdateStyle.class, "STALE DATA", false, true);
        //Column Descriptions
        basePointImportFormat.setDescriptionKey("ACTION", "yukon.web.modules.amr.pointImport.column.action");
        basePointImportFormat.setValidValuesKey("ACTION", "yukon.web.modules.amr.pointImport.validValues.action");
        basePointImportFormat.setDescriptionKey("DEVICE NAME", "yukon.web.modules.amr.pointImport.column.deviceName");
        basePointImportFormat.setDescriptionKey("DEVICE TYPE", "yukon.web.modules.amr.pointImport.column.deviceType");
        basePointImportFormat.setValidValuesKey("DEVICE TYPE", "yukon.web.modules.amr.pointImport.validValues.deviceType");
        basePointImportFormat.setDescriptionKey("POINT NAME", "yukon.web.modules.amr.pointImport.column.pointName");
        basePointImportFormat.setDescriptionKey("DISABLED", "yukon.web.modules.amr.pointImport.column.disabled");
        basePointImportFormat.setDescriptionKey("STALE DATA TIME", "yukon.web.modules.amr.pointImport.column.staleDataTime");
        basePointImportFormat.setDescriptionKey("STALE DATA UPDATE", "yukon.web.modules.amr.pointImport.column.staleDataUpdate");
        
        analogAccumulatorSharedImportFormat = basePointImportFormat.clone();
        //Optional Columns
        analogAccumulatorSharedImportFormat.addOptionalColumn("ARCHIVE TYPE", PointArchiveType.class, false, true);
        analogAccumulatorSharedImportFormat.addOptionalColumn("HIGH REASONABILITY", Double.class);
        analogAccumulatorSharedImportFormat.addOptionalColumn("LOW REASONABILITY", Double.class);
        //Grouped Columns
        analogAccumulatorSharedImportFormat.addGroupedColumn("HIGH LIMIT 1", Double.class, "LIMIT1");
        analogAccumulatorSharedImportFormat.addGroupedColumn("LOW LIMIT 1", Double.class, "LIMIT1");
        analogAccumulatorSharedImportFormat.addGroupedColumn("LIMIT DURATION 1", PositiveInteger.class, "LIMIT1");
        analogAccumulatorSharedImportFormat.addGroupedColumn("HIGH LIMIT 2", Double.class, "LIMIT2");
        analogAccumulatorSharedImportFormat.addGroupedColumn("LOW LIMIT 2", Double.class, "LIMIT2");
        analogAccumulatorSharedImportFormat.addGroupedColumn("LIMIT DURATION 2", PositiveInteger.class, "LIMIT2");
        //ARCHIVE TYPE dependent
        analogAccumulatorSharedImportFormat.addValueDependentColumn("ARCHIVE INTERVAL", ImportPointArchiveInterval.class, false, true, "ARCHIVE TYPE", PointArchiveType.ON_TIMER, PointArchiveType.ON_TIMER_OR_UPDATE);
        //ACTION dependent
        analogAccumulatorSharedImportFormat.addValueDependentColumn("UNIT OF MEASURE", ImportUnitOfMeasure.class, false, true, "ACTION", ImportAction.ADD);
        analogAccumulatorSharedImportFormat.addValueDependentColumn("DECIMAL PLACES", PositiveInteger.class, false, true, "ACTION", ImportAction.ADD);
        //Column Descriptions
        analogAccumulatorSharedImportFormat.setDescriptionKey("ARCHIVE TYPE", "yukon.web.modules.amr.pointImport.column.archiveType");
        analogAccumulatorSharedImportFormat.setDescriptionKey("HIGH REASONABILITY", "yukon.web.modules.amr.pointImport.column.highReasonability");
        analogAccumulatorSharedImportFormat.setDescriptionKey("LOW REASONABILITY", "yukon.web.modules.amr.pointImport.column.lowReasonability");
        analogAccumulatorSharedImportFormat.setDescriptionKey("HIGH LIMIT 1", "yukon.web.modules.amr.pointImport.column.highLimit1");
        analogAccumulatorSharedImportFormat.setDescriptionKey("LOW LIMIT 1", "yukon.web.modules.amr.pointImport.column.lowLimit1");
        analogAccumulatorSharedImportFormat.setDescriptionKey("LIMIT DURATION 1", "yukon.web.modules.amr.pointImport.column.limitDuration1");
        analogAccumulatorSharedImportFormat.setDescriptionKey("HIGH LIMIT 2", "yukon.web.modules.amr.pointImport.column.highLimit2");
        analogAccumulatorSharedImportFormat.setDescriptionKey("LOW LIMIT 2", "yukon.web.modules.amr.pointImport.column.lowLimit2");
        analogAccumulatorSharedImportFormat.setDescriptionKey("LIMIT DURATION 2", "yukon.web.modules.amr.pointImport.column.limitDuration2");
        analogAccumulatorSharedImportFormat.setDescriptionKey("ARCHIVE INTERVAL", "yukon.web.modules.amr.pointImport.column.archiveInterval");
        analogAccumulatorSharedImportFormat.setValidValuesKey("ARCHIVE INTERVAL", "yukon.web.modules.amr.pointImport.validValues.archiveInterval");
        analogAccumulatorSharedImportFormat.setDescriptionKey("UNIT OF MEASURE", "yukon.web.modules.amr.pointImport.column.unitOfMeasure");
        analogAccumulatorSharedImportFormat.setValidValuesKey("UNIT OF MEASURE", "yukon.web.modules.amr.pointImport.validValues.unitOfMeasure");
        analogAccumulatorSharedImportFormat.setDescriptionKey("DECIMAL PLACES", "yukon.web.modules.amr.pointImport.column.decimalPlaces");
        
        ANALOG_POINT_FORMAT = analogAccumulatorSharedImportFormat.clone();
        //Optional Columns
        ANALOG_POINT_FORMAT.addOptionalColumn("POINT OFFSET", PositiveInteger.class);
        ANALOG_POINT_FORMAT.addOptionalColumn("DEADBAND", PositiveDouble.class);
        //ACTION dependent
        ANALOG_POINT_FORMAT.addValueDependentColumn("MULTIPLIER", PositiveDouble.class, "ACTION", ImportAction.ADD);
        ANALOG_POINT_FORMAT.addValueDependentColumn("DATA OFFSET", Double.class, "ACTION", ImportAction.ADD);
        ANALOG_POINT_FORMAT.addValueDependentColumn("METER DIALS", PositiveInteger.class, "ACTION", ImportAction.ADD);        
        //Column descriptions
        ANALOG_POINT_FORMAT.setDescriptionKey("POINT OFFSET", "yukon.web.modules.amr.pointImport.column.pointOffset");
        ANALOG_POINT_FORMAT.setDescriptionKey("DEADBAND", "yukon.web.modules.amr.pointImport.column.deadband");
        ANALOG_POINT_FORMAT.setDescriptionKey("MULTIPLIER", "yukon.web.modules.amr.pointImport.column.multiplier");
        ANALOG_POINT_FORMAT.setDescriptionKey("DATA OFFSET", "yukon.web.modules.amr.pointImport.column.dataOffset");
        ANALOG_POINT_FORMAT.setDescriptionKey("METER DIALS", "yukon.web.modules.amr.pointImport.column.meterDials");
        
        STATUS_POINT_FORMAT = basePointImportFormat.clone();
        //Optional Columns
        STATUS_POINT_FORMAT.addOptionalColumn("POINT OFFSET", PositiveInteger.class);
        STATUS_POINT_FORMAT.addOptionalColumn("CONTROL TYPE", ControlType.class, false, true);
        STATUS_POINT_FORMAT.addOptionalColumn("ARCHIVE DATA", StrictBoolean.class);
        STATUS_POINT_FORMAT.addOptionalColumn("CONTROL INHIBIT", StrictBoolean.class);
        //ACTION dependent
        STATUS_POINT_FORMAT.addValueDependentColumn("STATE GROUP", String.class, "ACTION", ImportAction.ADD);
        STATUS_POINT_FORMAT.addValueDependentColumn("INITIAL STATE", String.class, "ACTION", ImportAction.ADD);
        //CONTROL TYPE dependent
        Object[] dependentTypes = {ControlType.NORMAL, ControlType.LATCH, ControlType.PSEUDO, ControlType.SBOLATCH, ControlType.SBOPULSE};
        STATUS_POINT_FORMAT.addValueDependentColumn("CONTROL POINT OFFSET", Integer.class, "CONTROL TYPE", dependentTypes);
        STATUS_POINT_FORMAT.addValueDependentColumn("CLOSE TIME 1", Integer.class, "CONTROL TYPE", dependentTypes);
        STATUS_POINT_FORMAT.addValueDependentColumn("CLOSE TIME 2", Integer.class, "CONTROL TYPE", dependentTypes);
        STATUS_POINT_FORMAT.addValueDependentColumn("STATE 1 COMMAND", StateControlType.class, false, true, "CONTROL TYPE", dependentTypes);
        STATUS_POINT_FORMAT.addValueDependentColumn("STATE 2 COMMAND", StateControlType.class, false, true, "CONTROL TYPE", dependentTypes);
        STATUS_POINT_FORMAT.addValueDependentColumn("COMMAND TIMEOUT", PositiveInteger.class, "CONTROL TYPE", dependentTypes);
        //Column descriptions
        STATUS_POINT_FORMAT.setDescriptionKey("POINT OFFSET", "yukon.web.modules.amr.pointImport.column.pointOffset");
        STATUS_POINT_FORMAT.setDescriptionKey("CONTROL TYPE", "yukon.web.modules.amr.pointImport.column.controlType");
        STATUS_POINT_FORMAT.setDescriptionKey("ARCHIVE DATA", "yukon.web.modules.amr.pointImport.column.archiveData");
        STATUS_POINT_FORMAT.setDescriptionKey("CONTROL INHIBIT", "yukon.web.modules.amr.pointImport.column.controlInhibit");
        STATUS_POINT_FORMAT.setDescriptionKey("STATE GROUP", "yukon.web.modules.amr.pointImport.column.stateGroup");
        STATUS_POINT_FORMAT.setValidValuesKey("STATE GROUP", "yukon.web.modules.amr.pointImport.validValues.stateGroup");
        STATUS_POINT_FORMAT.setDescriptionKey("INITIAL STATE", "yukon.web.modules.amr.pointImport.column.initialState");
        STATUS_POINT_FORMAT.setValidValuesKey("INITIAL STATE", "yukon.web.modules.amr.pointImport.validValues.initialState");
        STATUS_POINT_FORMAT.setDescriptionKey("CONTROL POINT OFFSET", "yukon.web.modules.amr.pointImport.column.controlPointOffset");
        STATUS_POINT_FORMAT.setDescriptionKey("CLOSE TIME 1", "yukon.web.modules.amr.pointImport.column.closeTime1");
        STATUS_POINT_FORMAT.setDescriptionKey("CLOSE TIME 2", "yukon.web.modules.amr.pointImport.column.closeTime2");
        STATUS_POINT_FORMAT.setDescriptionKey("STATE 1 COMMAND", "yukon.web.modules.amr.pointImport.column.state1Command");
        STATUS_POINT_FORMAT.setDescriptionKey("STATE 2 COMMAND", "yukon.web.modules.amr.pointImport.column.state2Command");
        STATUS_POINT_FORMAT.setDescriptionKey("COMMAND TIMEOUT", "yukon.web.modules.amr.pointImport.column.commandTimeout");
        
        ACCUMULATOR_POINT_FORMAT = analogAccumulatorSharedImportFormat.clone();
        //Required Columns
        ACCUMULATOR_POINT_FORMAT.addRequiredColumn("ACCUMULATOR TYPE", AccumulatorType.class, false, true);
        //Optional Columns
        ACCUMULATOR_POINT_FORMAT.addOptionalColumn("POINT OFFSET", PositiveInteger.class);
        //ACTION dependent
        ACCUMULATOR_POINT_FORMAT.addValueDependentColumn("MULTIPLIER", Double.class, "ACTION", ImportAction.ADD);
        ACCUMULATOR_POINT_FORMAT.addValueDependentColumn("DATA OFFSET", Double.class, "ACTION", ImportAction.ADD);
        ACCUMULATOR_POINT_FORMAT.addValueDependentColumn("METER DIALS", PositiveInteger.class, "ACTION", ImportAction.ADD);
        //Column descriptions
        ACCUMULATOR_POINT_FORMAT.setDescriptionKey("ACCUMULATOR TYPE", "yukon.web.modules.amr.pointImport.column.accumulatorType");
        ACCUMULATOR_POINT_FORMAT.setDescriptionKey("POINT OFFSET", "yukon.web.modules.amr.pointImport.column.pointOffset");
        ACCUMULATOR_POINT_FORMAT.setDescriptionKey("MULTIPLIER", "yukon.web.modules.amr.pointImport.column.multiplier");
        ACCUMULATOR_POINT_FORMAT.setDescriptionKey("DATA OFFSET", "yukon.web.modules.amr.pointImport.column.dataOffset");
        ACCUMULATOR_POINT_FORMAT.setDescriptionKey("METER DIALS", "yukon.web.modules.amr.pointImport.column.meterDials");
        
        CALC_ANALOG_POINT_FORMAT = analogAccumulatorSharedImportFormat.clone();
        //ACTION dependent
        CALC_ANALOG_POINT_FORMAT.addValueDependentColumn("UPDATE TYPE", AnalogPointUpdateType.class, false, true, "ACTION", ImportAction.ADD);
        CALC_ANALOG_POINT_FORMAT.addValueDependentColumn("CALCULATION", String.class, "ACTION", ImportAction.ADD);
        CALC_ANALOG_POINT_FORMAT.addValueDependentColumn("FORCE QUALITY NORMAL", StrictBoolean.class, "ACTION", ImportAction.ADD);
        //UPDATE TYPE dependent
        CALC_ANALOG_POINT_FORMAT.addValueDependentColumn("UPDATE RATE", PointPeriodicRate.class, false, true, "UPDATE TYPE", AnalogPointUpdateType.ON_TIMER, AnalogPointUpdateType.ON_TIMER_AND_CHANGE);
        //Column descriptions
        CALC_ANALOG_POINT_FORMAT.setDescriptionKey("UPDATE TYPE", "yukon.web.modules.amr.pointImport.column.updateType");
        CALC_ANALOG_POINT_FORMAT.setDescriptionKey("CALCULATION", "yukon.web.modules.amr.pointImport.column.calculation");
        CALC_ANALOG_POINT_FORMAT.setDescriptionKey("FORCE QUALITY NORMAL", "yukon.web.modules.amr.pointImport.column.forceQualityNormal");
        CALC_ANALOG_POINT_FORMAT.setDescriptionKey("UPDATE RATE", "yukon.web.modules.amr.pointImport.column.updateRate");
        
        CALC_STATUS_POINT_FORMAT = basePointImportFormat.clone();
        //Optional Columns
        CALC_STATUS_POINT_FORMAT.addOptionalColumn("ARCHIVE DATA", StrictBoolean.class);
        //ACTION dependent
        CALC_STATUS_POINT_FORMAT.addValueDependentColumn("UPDATE TYPE", StatusPointUpdateType.class, false, true, "ACTION", ImportAction.ADD);
        CALC_STATUS_POINT_FORMAT.addValueDependentColumn("INITIAL STATE", String.class, "ACTION", ImportAction.ADD);
        CALC_STATUS_POINT_FORMAT.addValueDependentColumn("STATE GROUP", String.class, "ACTION", ImportAction.ADD);
        CALC_STATUS_POINT_FORMAT.addValueDependentColumn("CALCULATION", String.class, "ACTION", ImportAction.ADD);
        //UPDATE TYPE dependent
        CALC_STATUS_POINT_FORMAT.addValueDependentColumn("UPDATE RATE", PointPeriodicRate.class, false, true, "UPDATE TYPE", StatusPointUpdateType.ON_TIMER, StatusPointUpdateType.ON_TIMER_AND_CHANGE);
        //Column descriptions
        CALC_STATUS_POINT_FORMAT.setDescriptionKey("ARCHIVE DATA", "yukon.web.modules.amr.pointImport.column.archiveData");
        CALC_STATUS_POINT_FORMAT.setDescriptionKey("UPDATE TYPE", "yukon.web.modules.amr.pointImport.column.updateType");
        CALC_STATUS_POINT_FORMAT.setDescriptionKey("INITIAL STATE", "yukon.web.modules.amr.pointImport.column.initialState");
        CALC_STATUS_POINT_FORMAT.setDescriptionKey("STATE GROUP", "yukon.web.modules.amr.pointImport.column.stateGroup");
        CALC_STATUS_POINT_FORMAT.setDescriptionKey("CALCULATION", "yukon.web.modules.amr.pointImport.column.calculation");
        CALC_STATUS_POINT_FORMAT.setDescriptionKey("UPDATE RATE", "yukon.web.modules.amr.pointImport.column.updateRate");
    }
}
