package com.cannontech.billing;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.billing.mainprograms.BillingFileDefaults;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.spring.YukonSpringHook;

public class SQLStringBuilder {
    public SqlStatementBuilder buildSQLStatement(String[] columns, String[] tables,
            BillingFileDefaults billingDefaults, int[] analogOffsets, int[] pulseAccOffsets, Date date) {
        return buildSQLStatement(columns, tables, billingDefaults, analogOffsets, pulseAccOffsets, null, date);
    }

    public SqlStatementBuilder buildSQLStatement(String[] columns, String[] tables,
            BillingFileDefaults billingDefaults, int[] analogOffsets, int[] pulseAccOffsets, int[] demandAccOffsets,
            Date date) {
        if (columns == null || tables == null) {
            return null;
        }

        SqlStatementBuilder sqlBuffer = buildSelectClause(columns);

        sqlBuffer.append(buildFromClause(tables));
        sqlBuffer.append(buildWhereClause(billingDefaults.getDeviceGroups(), analogOffsets, pulseAccOffsets,
            demandAccOffsets, date));

        CTILogger.info(" SQL Statement: " + sqlBuffer);
        return sqlBuffer;
    }

    public static final String PAO_PAONAME = "YUKONPAOBJECT.PAONAME";
    public static final String PAO_PAOBJECTID = "YUKONPAOBJECT.PAOBJECTID";

    public static final String RPH_VALUE = "RAWPOINTHISTORY.VALUE";
    public static final String RPH_TIMESTAMP = "RAWPOINTHISTORY.TIMESTAMP";

    public static final String PT_POINTID = "POINT.POINTID";
    public static final String PT_POINTOFFSET = "POINT.POINTOFFSET";

    public static final String DMG_DEVICEID = "DEVICEMETERGROUP.DEVICEID";
    public static final String DMG_METERNUMBER = "DEVICEMETERGROUP.METERNUMBER";

    // true when table is used in sql string.
    private boolean deviceMeterGroup_from = false;
    // boolean deviceScanRate_from = false;
    private boolean yukonPAObjectTable_from = false;
    private boolean rawPointHistoryTable_from = false;
    private boolean point_from = false;
    private boolean device_from = false;
    private boolean pointUnit_from = false;
    private boolean unitMeasure_from = false;
    private boolean deviceCarrierSettings_from = false;

    private SqlStatementBuilder buildFromClause(String[] tables) {
        SqlStatementBuilder fromString = new SqlStatementBuilder(" FROM " + tables[0]);
        for (int i = 1; i < tables.length; i++) {
            fromString.append(", " + tables[i]);
        }

        // set the boolean values of the tables that exist.
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].equalsIgnoreCase(com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME)) {
                yukonPAObjectTable_from = true;
            } else if (tables[i].equalsIgnoreCase(com.cannontech.database.db.device.DeviceMeterGroup.TABLE_NAME)) {
                deviceMeterGroup_from = true;
            } else if (tables[i].equalsIgnoreCase(com.cannontech.database.db.point.Point.TABLE_NAME)) {
                point_from = true;
            } else if (tables[i].equalsIgnoreCase(com.cannontech.database.db.point.RawPointHistory.TABLE_NAME)) {
                rawPointHistoryTable_from = true;
            } else if (tables[i].equalsIgnoreCase(com.cannontech.database.db.point.PointUnit.TABLE_NAME)) {
                pointUnit_from = true;
            } else if (tables[i].equalsIgnoreCase(com.cannontech.database.db.point.UnitMeasure.TABLE_NAME)) {
                unitMeasure_from = true;
            } else if (tables[i].equalsIgnoreCase(com.cannontech.database.db.device.DeviceCarrierSettings.TABLE_NAME)) {
                deviceCarrierSettings_from = true;
            }
        }
        return fromString;
    }

    private SqlStatementBuilder buildSelectClause(String[] columns) {
        SqlStatementBuilder selectString = new SqlStatementBuilder("SELECT DISTINCT " + columns[0]);
        for (int i = 1; i < columns.length; i++) {
            selectString.append(", " + columns[i]);
        }

        return selectString;
    }

    private SqlStatementBuilder buildWhereClause(List<String> groupVector, int[] analogOffsets, int[] pulseAccOffsets,
            int[] demandAccOffsets, Date date) {
        SqlFragmentCollection whereClauses = SqlFragmentCollection.newAndCollection();

        if (yukonPAObjectTable_from) {
            if (deviceMeterGroup_from) {
                whereClauses.addSimpleFragment(" YUKONPAOBJECT.PAOBJECTID = DEVICEMETERGROUP.DEVICEID ");
            }

            if (deviceCarrierSettings_from) {
                whereClauses.addSimpleFragment(" YUKONPAOBJECT.PAOBJECTID = DEVICECARRIERSETTINGS.DEVICEID ");
            }

            if (point_from) {
                whereClauses.addSimpleFragment(" YUKONPAOBJECT.PAOBJECTID = POINT.PAOBJECTID ");
            }

        }
        if (rawPointHistoryTable_from) {
            SqlStatementBuilder statementBuilder = new SqlStatementBuilder();
            statementBuilder.append(" RAWPOINTHISTORY.TIMESTAMP >").appendArgument(date);
            whereClauses.add(statementBuilder); // START BILLING DATE
            if (point_from) {
                whereClauses.addSimpleFragment(" RAWPOINTHISTORY.POINTID = POINT.POINTID ");
            }
        }
        if (groupVector.size() > 0) {
            String deviceGroupIdentity = null;
            if (yukonPAObjectTable_from) {
                deviceGroupIdentity = "YUKONPAOBJECT.PAOBJECTID";
            } else if (deviceMeterGroup_from) {
                deviceGroupIdentity = "DEVICEMETERGROUP.DEVICEID";
            } else if (device_from) {
                deviceGroupIdentity = "DEVICE.DEVICEID";
            } else if (deviceCarrierSettings_from) {
                deviceGroupIdentity = "DEVICECARRIERSETTINGS.DEVICEID";
            }

            if (deviceGroupIdentity != null) {
                DeviceGroupService deviceGroupService =
                    YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);

                Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(groupVector);
                SqlFragmentSource deviceGroupSqlWhereClause =
                    deviceGroupService.getDeviceGroupSqlWhereClause(deviceGroups, deviceGroupIdentity);
                whereClauses.add(deviceGroupSqlWhereClause);
            }
        }
        if (point_from) {
            if (pointUnit_from && unitMeasure_from) {
                whereClauses.addSimpleFragment(" POINT.POINTID = POINTUNIT.POINTID ");
                whereClauses.addSimpleFragment(" POINTUNIT.UOMID = UNITMEASURE.UOMID");
            }

            if (deviceMeterGroup_from) {
                whereClauses.addSimpleFragment(" POINT.PAOBJECTID = DEVICEMETERGROUP.DEVICEID");
            }

            if (deviceCarrierSettings_from) {
                whereClauses.addSimpleFragment(" POINT.PAOBJECTID = DEVICECARRIERSETTINGS.DEVICEID ");
            }

            // select valid pointtypes with appropriate pointoffsets.
            if (analogOffsets != null || pulseAccOffsets != null || demandAccOffsets != null) {
                String pointTypeString = "(";
                if (analogOffsets != null) {
                    if (pointTypeString.length() > 1) {
                        // though, it's the first one.)
                        pointTypeString += " OR ";
                    }

                    pointTypeString += " (POINTTYPE = 'Analog' AND POINTOFFSET IN (" + analogOffsets[0];
                    for (int i = 1; i < analogOffsets.length; i++) {
                        pointTypeString += ", " + analogOffsets[i];
                    }

                    pointTypeString += " ))";
                }
                if (pulseAccOffsets != null) {
                    if (pointTypeString.length() > 1) {
                        // though, it's the first one.)
                        pointTypeString += " OR ";
                    }

                    pointTypeString += " (POINTTYPE = 'PulseAccumulator' AND POINTOFFSET IN (" + pulseAccOffsets[0];
                    for (int i = 1; i < pulseAccOffsets.length; i++) {
                        pointTypeString += ", " + pulseAccOffsets[i];
                    }

                    pointTypeString += " ))";
                }
                if (demandAccOffsets != null) {
                    if (pointTypeString.length() > 1) {
                        // though, it's the first one.)
                        pointTypeString += " OR ";
                    }

                    pointTypeString += " (POINTTYPE = 'DemandAccumulator' AND POINTOFFSET IN (" + demandAccOffsets[0];
                    for (int i = 1; i < demandAccOffsets.length; i++) {
                        pointTypeString += ", " + demandAccOffsets[i];
                    }

                    pointTypeString += " ))";
                }

                pointTypeString += " )";
                whereClauses.addSimpleFragment(pointTypeString);
            }

        }

        return new SqlStatementBuilder("WHERE").appendFragment(whereClauses);
    }
}
