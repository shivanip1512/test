package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author aaron
 */
public interface MeteringRole {
    public static final int ROLEID = OperatorRoleDefs.METERING_ROLEID;

    public static final int IMPORTER_ENABLED = OperatorRoleDefs.METERING_PROPERTYID_BASE - 3; // -20203

    /** Controls ability to collect profile data (in the past) from the meter. */
    public static final int PROFILE_COLLECTION = OperatorRoleDefs.METERING_PROPERTYID_BASE - 6; // -20206
    /** Controls Move In/Out auto archiving (versus manual (or none?)*/
    public static final int MOVE_IN_MOVE_OUT_AUTO_ARCHIVING = OperatorRoleDefs.METERING_PROPERTYID_BASE - 7; // -20207
    /** Controls access to process a move in/out */
    public static final int MOVE_IN_MOVE_OUT = OperatorRoleDefs.METERING_PROPERTYID_BASE - 8; // -20208
    /** Controls ability to collect profile data (now) by scanning the meter. */
    public static final int PROFILE_COLLECTION_SCANNING = OperatorRoleDefs.METERING_PROPERTYID_BASE - 9; // -20209
    /** Controls access to process a Hight bill complaint */
    public static final int HIGH_BILL_COMPLAINT = OperatorRoleDefs.METERING_PROPERTYID_BASE - 10; // -20210
    /** Control ability to view the CIS Information widget. */
    public static final int CIS_DETAIL_WIDGET_ENABLED = OperatorRoleDefs.METERING_PROPERTYID_BASE - 11; // -20211
    /** Defines what type is CIS Detail widget is to be displayed (i.e. NONE, MULTISPEAK, CAYENTA, etc). See {@link CisDetailRolePropertyEnum}. */
    public static final int CIS_DETAIL_TYPE = OperatorRoleDefs.METERING_PROPERTYID_BASE - 12; // -20212
    /** Access to Outage Processing */
    public static final int OUTAGE_PROCESSING = OperatorRoleDefs.METERING_PROPERTYID_BASE - 13; // -20213
    /** Access to Tamper Flag Processing */
    public static final int TAMPER_FLAG_PROCESSING = OperatorRoleDefs.METERING_PROPERTYID_BASE - 14; // -20214
    /** Access to the Phase Detect link on the System Actions widget on metering start page */
    public static final int PHASE_DETECT = OperatorRoleDefs.METERING_PROPERTYID_BASE - 15; // -20215
    /** Access to the Validation Engine setup and processing widget on metering start page */
    public static final int VALIDATION_ENGINE = OperatorRoleDefs.METERING_PROPERTYID_BASE - 16; // -20216
    /** Access to Status Point Monitoring */
    public static final int STATUS_POINT_MONITORING = OperatorRoleDefs.METERING_PROPERTYID_BASE - 17; // -20217
    /** Access to Porter Response Processing Monitoring */
    public static final int PORTER_RESPONSE_MONITORING = OperatorRoleDefs.METERING_PROPERTYID_BASE - 18; // -20218
    /** Access to Meter Events features */
    public static final int METER_EVENTS = OperatorRoleDefs.METERING_PROPERTYID_BASE - 19; // -20219
}
