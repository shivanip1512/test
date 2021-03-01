package com.cannontech.common.bulk.model;

import static com.cannontech.common.bulk.model.PointImportParameters.*;

import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.StatusControlType;

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
    
    private static final String STALE_DATA_GROUP = "STALE DATA";
    private static final String LIMIT_1_GROUP = "LIMIT1";
    private static final String LIMIT_2_GROUP = "LIMIT2";
    
    static {
        basePointImportFormat = new ImportFileFormat();
        //Required Columns
        basePointImportFormat.addRequiredColumn(ACTION.NAME, ACTION.CLASS, false, true);
        basePointImportFormat.addRequiredColumn(DEVICE_NAME.NAME, DEVICE_NAME.CLASS);
        basePointImportFormat.addRequiredColumn(DEVICE_TYPE.NAME, DEVICE_TYPE.CLASS);
        basePointImportFormat.addRequiredColumn(POINT_NAME.NAME, POINT_NAME.CLASS);
        //Optional Columns
        basePointImportFormat.addOptionalColumn(DISABLED.NAME, DISABLED.CLASS);
        basePointImportFormat.addOptionalColumn(POINT_OFFSET.NAME, POINT_OFFSET.CLASS);
        //Grouped Columns
        basePointImportFormat.addGroupedColumn(STALE_DATA_TIME.NAME, STALE_DATA_TIME.CLASS, STALE_DATA_GROUP);
        basePointImportFormat.addGroupedColumn(STALE_DATA_UPDATE.NAME, STALE_DATA_UPDATE.CLASS, STALE_DATA_GROUP, false, true);
        //Column Descriptions
        basePointImportFormat.setDescriptionKeys(ACTION.NAME, ACTION.DESCRIPTION, ACTION.VALUES);
        basePointImportFormat.setDescriptionKey(DEVICE_NAME.NAME, DEVICE_NAME.DESCRIPTION);
        basePointImportFormat.setDescriptionKeys(DEVICE_TYPE.NAME, DEVICE_TYPE.DESCRIPTION, DEVICE_TYPE.VALUES);
        basePointImportFormat.setDescriptionKey(POINT_NAME.NAME, POINT_NAME.DESCRIPTION);
        basePointImportFormat.setDescriptionKey(DISABLED.NAME, DISABLED.DESCRIPTION);
        basePointImportFormat.setDescriptionKey(POINT_OFFSET.NAME, POINT_OFFSET.DESCRIPTION);
        basePointImportFormat.setDescriptionKey(STALE_DATA_TIME.NAME, STALE_DATA_TIME.DESCRIPTION);
        basePointImportFormat.setDescriptionKey(STALE_DATA_UPDATE.NAME, STALE_DATA_UPDATE.DESCRIPTION);
        
        analogAccumulatorSharedImportFormat = basePointImportFormat.clone();
        //Optional Columns
        analogAccumulatorSharedImportFormat.addOptionalColumn(ARCHIVE_TYPE.NAME, ARCHIVE_TYPE.CLASS, false, true);
        analogAccumulatorSharedImportFormat.addOptionalColumn(HIGH_REASONABILITY.NAME, HIGH_REASONABILITY.CLASS);
        analogAccumulatorSharedImportFormat.addOptionalColumn(LOW_REASONABILITY.NAME, LOW_REASONABILITY.CLASS);
        //Grouped Columns
        analogAccumulatorSharedImportFormat.addGroupedColumn(HIGH_LIMIT_1.NAME, HIGH_LIMIT_1.CLASS, LIMIT_1_GROUP);
        analogAccumulatorSharedImportFormat.addGroupedColumn(LOW_LIMIT_1.NAME, LOW_LIMIT_1.CLASS, LIMIT_1_GROUP);
        analogAccumulatorSharedImportFormat.addGroupedColumn(LIMIT_DURATION_1.NAME, LIMIT_DURATION_1.CLASS, LIMIT_1_GROUP);
        analogAccumulatorSharedImportFormat.addGroupedColumn(HIGH_LIMIT_2.NAME, HIGH_LIMIT_2.CLASS, LIMIT_2_GROUP);
        analogAccumulatorSharedImportFormat.addGroupedColumn(LOW_LIMIT_2.NAME, LOW_LIMIT_2.CLASS, LIMIT_2_GROUP);
        analogAccumulatorSharedImportFormat.addGroupedColumn(LIMIT_DURATION_2.NAME, LIMIT_DURATION_2.CLASS, LIMIT_2_GROUP);
        //ARCHIVE TYPE dependent
        analogAccumulatorSharedImportFormat.addValueDependentColumn(ARCHIVE_INTERVAL.NAME, ARCHIVE_INTERVAL.CLASS, false, true, ARCHIVE_TYPE.NAME, PointArchiveType.ON_TIMER, PointArchiveType.ON_TIMER_OR_UPDATE);
        //ACTION dependent
        analogAccumulatorSharedImportFormat.addValueDependentColumn(UNIT_OF_MEASURE.NAME, UNIT_OF_MEASURE.CLASS, false, true, ACTION.NAME, ImportAction.ADD);
        analogAccumulatorSharedImportFormat.addValueDependentColumn(DECIMAL_PLACES.NAME, DECIMAL_PLACES.CLASS, false, true, ACTION.NAME, ImportAction.ADD);
        //Column Descriptions
        analogAccumulatorSharedImportFormat.setDescriptionKey(ARCHIVE_TYPE.NAME, ARCHIVE_TYPE.DESCRIPTION);
        analogAccumulatorSharedImportFormat.setDescriptionKey(HIGH_REASONABILITY.NAME, HIGH_REASONABILITY.DESCRIPTION);
        analogAccumulatorSharedImportFormat.setDescriptionKey(LOW_REASONABILITY.NAME, LOW_REASONABILITY.DESCRIPTION);
        analogAccumulatorSharedImportFormat.setDescriptionKey(HIGH_LIMIT_1.NAME, HIGH_LIMIT_1.DESCRIPTION);
        analogAccumulatorSharedImportFormat.setDescriptionKey(LOW_LIMIT_1.NAME, LOW_LIMIT_1.DESCRIPTION);
        analogAccumulatorSharedImportFormat.setDescriptionKey(LIMIT_DURATION_1.NAME, LIMIT_DURATION_1.DESCRIPTION);
        analogAccumulatorSharedImportFormat.setDescriptionKey(HIGH_LIMIT_2.NAME, HIGH_LIMIT_2.DESCRIPTION);
        analogAccumulatorSharedImportFormat.setDescriptionKey(LOW_LIMIT_2.NAME, LOW_LIMIT_2.DESCRIPTION);
        analogAccumulatorSharedImportFormat.setDescriptionKey(LIMIT_DURATION_2.NAME, LIMIT_DURATION_2.DESCRIPTION);
        analogAccumulatorSharedImportFormat.setDescriptionKeys(ARCHIVE_INTERVAL.NAME, ARCHIVE_INTERVAL.DESCRIPTION, ARCHIVE_INTERVAL.VALUES);
        analogAccumulatorSharedImportFormat.setDescriptionKeys(UNIT_OF_MEASURE.NAME, UNIT_OF_MEASURE.DESCRIPTION, UNIT_OF_MEASURE.VALUES);
        analogAccumulatorSharedImportFormat.setDescriptionKey(DECIMAL_PLACES.NAME, DECIMAL_PLACES.DESCRIPTION);
        
        ANALOG_POINT_FORMAT = analogAccumulatorSharedImportFormat.clone();
        //Optional Columns
        ANALOG_POINT_FORMAT.addOptionalColumn(DEADBAND.NAME, DEADBAND.CLASS);
        //ACTION dependent
        ANALOG_POINT_FORMAT.addValueDependentColumn(MULTIPLIER.NAME, MULTIPLIER.CLASS, ACTION.NAME, ImportAction.ADD);
        ANALOG_POINT_FORMAT.addValueDependentColumn(DATA_OFFSET.NAME, DATA_OFFSET.CLASS, ACTION.NAME, ImportAction.ADD);
        ANALOG_POINT_FORMAT.addValueDependentColumn(METER_DIALS.NAME, METER_DIALS.CLASS, ACTION.NAME, ImportAction.ADD);        
        //Column descriptions
        ANALOG_POINT_FORMAT.setDescriptionKey(DEADBAND.NAME, DEADBAND.DESCRIPTION);
        ANALOG_POINT_FORMAT.setDescriptionKey(MULTIPLIER.NAME, MULTIPLIER.DESCRIPTION);
        ANALOG_POINT_FORMAT.setDescriptionKey(DATA_OFFSET.NAME, DATA_OFFSET.DESCRIPTION);
        ANALOG_POINT_FORMAT.setDescriptionKey(METER_DIALS.NAME, METER_DIALS.DESCRIPTION);
        
        STATUS_POINT_FORMAT = basePointImportFormat.clone();
        //Optional Columns
        STATUS_POINT_FORMAT.addOptionalColumn(CONTROL_TYPE.NAME, CONTROL_TYPE.CLASS, false, true);
        STATUS_POINT_FORMAT.addOptionalColumn(ARCHIVE_DATA.NAME, ARCHIVE_DATA.CLASS);
        STATUS_POINT_FORMAT.addOptionalColumn(CONTROL_INHIBIT.NAME, CONTROL_INHIBIT.CLASS);
        //ACTION dependent
        STATUS_POINT_FORMAT.addValueDependentColumn(STATE_GROUP.NAME, STATE_GROUP.CLASS, ACTION.NAME, ImportAction.ADD);
        STATUS_POINT_FORMAT.addValueDependentColumn(INITIAL_STATE.NAME, INITIAL_STATE.CLASS, ACTION.NAME, ImportAction.ADD);
        //CONTROL TYPE dependent
        Object[] dependentTypes = {StatusControlType.NORMAL, StatusControlType.LATCH, StatusControlType.PSEUDO, StatusControlType.SBOLATCH, StatusControlType.SBOPULSE};
        STATUS_POINT_FORMAT.addValueDependentColumn(CONTROL_POINT_OFFSET.NAME, CONTROL_POINT_OFFSET.CLASS, CONTROL_TYPE.NAME, dependentTypes);
        STATUS_POINT_FORMAT.addValueDependentColumn(CLOSE_TIME_1.NAME, CLOSE_TIME_1.CLASS, CONTROL_TYPE.NAME, dependentTypes);
        STATUS_POINT_FORMAT.addValueDependentColumn(CLOSE_TIME_2.NAME, CLOSE_TIME_2.CLASS, CONTROL_TYPE.NAME, dependentTypes);
        STATUS_POINT_FORMAT.addValueDependentColumn(STATE_1_COMMAND.NAME, STATE_1_COMMAND.CLASS, false, true, CONTROL_TYPE.NAME, dependentTypes);
        STATUS_POINT_FORMAT.addValueDependentColumn(STATE_2_COMMAND.NAME, STATE_2_COMMAND.CLASS, false, true, CONTROL_TYPE.NAME, dependentTypes);
        STATUS_POINT_FORMAT.addValueDependentColumn(COMMAND_TIMEOUT.NAME, COMMAND_TIMEOUT.CLASS, CONTROL_TYPE.NAME, dependentTypes);
        //Column descriptions
        STATUS_POINT_FORMAT.setDescriptionKey(CONTROL_TYPE.NAME, CONTROL_TYPE.DESCRIPTION);
        STATUS_POINT_FORMAT.setDescriptionKey(ARCHIVE_DATA.NAME, ARCHIVE_DATA.DESCRIPTION);
        STATUS_POINT_FORMAT.setDescriptionKey(CONTROL_INHIBIT.NAME, CONTROL_INHIBIT.DESCRIPTION);
        STATUS_POINT_FORMAT.setDescriptionKeys(STATE_GROUP.NAME, STATE_GROUP.DESCRIPTION, STATE_GROUP.VALUES);
        STATUS_POINT_FORMAT.setDescriptionKeys(INITIAL_STATE.NAME, INITIAL_STATE.DESCRIPTION, INITIAL_STATE.VALUES);
        STATUS_POINT_FORMAT.setDescriptionKey(CONTROL_POINT_OFFSET.NAME, CONTROL_POINT_OFFSET.DESCRIPTION);
        STATUS_POINT_FORMAT.setDescriptionKey(CLOSE_TIME_1.NAME, CLOSE_TIME_1.DESCRIPTION);
        STATUS_POINT_FORMAT.setDescriptionKey(CLOSE_TIME_2.NAME, CLOSE_TIME_2.DESCRIPTION);
        STATUS_POINT_FORMAT.setDescriptionKey(STATE_1_COMMAND.NAME, STATE_1_COMMAND.DESCRIPTION);
        STATUS_POINT_FORMAT.setDescriptionKey(STATE_2_COMMAND.NAME, STATE_2_COMMAND.DESCRIPTION);
        STATUS_POINT_FORMAT.setDescriptionKey(COMMAND_TIMEOUT.NAME, COMMAND_TIMEOUT.DESCRIPTION);
        
        ACCUMULATOR_POINT_FORMAT = analogAccumulatorSharedImportFormat.clone();
        //Required Columns
        ACCUMULATOR_POINT_FORMAT.addRequiredColumn(ACCUMULATOR_TYPE.NAME, ACCUMULATOR_TYPE.CLASS, false, true);
        //Optional Columns
        //ACTION dependent
        ACCUMULATOR_POINT_FORMAT.addValueDependentColumn(MULTIPLIER.NAME, MULTIPLIER.CLASS, ACTION.NAME, ImportAction.ADD);
        ACCUMULATOR_POINT_FORMAT.addValueDependentColumn(DATA_OFFSET.NAME, DATA_OFFSET.CLASS, ACTION.NAME, ImportAction.ADD);
        ACCUMULATOR_POINT_FORMAT.addValueDependentColumn(METER_DIALS.NAME, METER_DIALS.CLASS, ACTION.NAME, ImportAction.ADD);
        //Column descriptions
        ACCUMULATOR_POINT_FORMAT.setDescriptionKey(ACCUMULATOR_TYPE.NAME, ACCUMULATOR_TYPE.DESCRIPTION);
        ACCUMULATOR_POINT_FORMAT.setDescriptionKey(MULTIPLIER.NAME, MULTIPLIER.DESCRIPTION);
        ACCUMULATOR_POINT_FORMAT.setDescriptionKey(DATA_OFFSET.NAME, DATA_OFFSET.DESCRIPTION);
        ACCUMULATOR_POINT_FORMAT.setDescriptionKey(METER_DIALS.NAME, METER_DIALS.DESCRIPTION);
        
        CALC_ANALOG_POINT_FORMAT = analogAccumulatorSharedImportFormat.clone();
        //ACTION dependent
        CALC_ANALOG_POINT_FORMAT.addValueDependentColumn(ANALOG_UPDATE_TYPE.NAME, ANALOG_UPDATE_TYPE.CLASS, false, true, ACTION.NAME, ImportAction.ADD);
        CALC_ANALOG_POINT_FORMAT.addValueDependentColumn(CALCULATION.NAME, CALCULATION.CLASS, ACTION.NAME, ImportAction.ADD);
        CALC_ANALOG_POINT_FORMAT.addValueDependentColumn(FORCE_QUALITY_NORMAL.NAME, FORCE_QUALITY_NORMAL.CLASS, ACTION.NAME, ImportAction.ADD);
        //UPDATE TYPE dependent
        CALC_ANALOG_POINT_FORMAT.addValueDependentColumn(UPDATE_RATE.NAME, UPDATE_RATE.CLASS, false, true, ANALOG_UPDATE_TYPE.NAME, AnalogPointUpdateType.ON_TIMER, AnalogPointUpdateType.ON_TIMER_AND_CHANGE);
        //Column descriptions
        CALC_ANALOG_POINT_FORMAT.setDescriptionKey(ANALOG_UPDATE_TYPE.NAME, ANALOG_UPDATE_TYPE.DESCRIPTION);
        CALC_ANALOG_POINT_FORMAT.setDescriptionKey(CALCULATION.NAME, CALCULATION.DESCRIPTION);
        CALC_ANALOG_POINT_FORMAT.setDescriptionKey(FORCE_QUALITY_NORMAL.NAME, FORCE_QUALITY_NORMAL.DESCRIPTION);
        CALC_ANALOG_POINT_FORMAT.setDescriptionKey(UPDATE_RATE.NAME, UPDATE_RATE.DESCRIPTION);
        
        CALC_STATUS_POINT_FORMAT = basePointImportFormat.clone();
        //Optional Columns
        CALC_STATUS_POINT_FORMAT.addOptionalColumn(ARCHIVE_DATA.NAME, ARCHIVE_DATA.CLASS);
        //ACTION dependent
        CALC_STATUS_POINT_FORMAT.addValueDependentColumn(STATUS_UPDATE_TYPE.NAME, STATUS_UPDATE_TYPE.CLASS, false, true, ACTION.NAME, ImportAction.ADD);
        CALC_STATUS_POINT_FORMAT.addValueDependentColumn(INITIAL_STATE.NAME, INITIAL_STATE.CLASS, ACTION.NAME, ImportAction.ADD);
        CALC_STATUS_POINT_FORMAT.addValueDependentColumn(STATE_GROUP.NAME, STATE_GROUP.CLASS, ACTION.NAME, ImportAction.ADD);
        CALC_STATUS_POINT_FORMAT.addValueDependentColumn(CALCULATION.NAME, CALCULATION.CLASS, ACTION.NAME, ImportAction.ADD);
        //UPDATE TYPE dependent
        CALC_STATUS_POINT_FORMAT.addValueDependentColumn(UPDATE_RATE.NAME, UPDATE_RATE.CLASS, false, true, STATUS_UPDATE_TYPE.NAME, StatusPointUpdateType.ON_TIMER, StatusPointUpdateType.ON_TIMER_AND_CHANGE);
        //Column descriptions
        CALC_STATUS_POINT_FORMAT.setDescriptionKey(ARCHIVE_DATA.NAME, ARCHIVE_DATA.DESCRIPTION);
        CALC_STATUS_POINT_FORMAT.setDescriptionKey(STATUS_UPDATE_TYPE.NAME, STATUS_UPDATE_TYPE.DESCRIPTION);
        CALC_STATUS_POINT_FORMAT.setDescriptionKey(INITIAL_STATE.NAME, INITIAL_STATE.DESCRIPTION);
        CALC_STATUS_POINT_FORMAT.setDescriptionKey(STATE_GROUP.NAME, STATE_GROUP.DESCRIPTION);
        CALC_STATUS_POINT_FORMAT.setDescriptionKey(CALCULATION.NAME, CALCULATION.DESCRIPTION);
        CALC_STATUS_POINT_FORMAT.setDescriptionKey(UPDATE_RATE.NAME, UPDATE_RATE.DESCRIPTION);
    }
}
