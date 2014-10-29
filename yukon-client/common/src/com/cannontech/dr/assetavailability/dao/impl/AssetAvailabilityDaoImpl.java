package com.cannontech.dr.assetavailability.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.dao.AssetAvailabilityDao;

public class AssetAvailabilityDaoImpl implements AssetAvailabilityDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityDaoImpl.class);

    private boolean checkOracleDatabase() {
        try {
            String databaseProductName =
                (String) JdbcUtils.extractDatabaseMetaData(yukonJdbcTemplate.getDataSource(), "getDatabaseProductName");

            if (databaseProductName.contains("Oracle")) {
                return true;
            }
        } catch (MetaDataAccessException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    @Override
    public List<AssetAvailabilityDetails> getAssetAvailabilityDetails(Iterable<Integer> loadGroupIds,
            PagingParameters pagingParameters, String filterCriteria, String sortingOrder, String sortingDirection,
            String communicatingWindowEnd, String runtimeWindowEnd, String currentTime) {
        boolean isOracle = checkOracleDatabase();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM (");
        sql.append("SELECT (ROW_NUMBER() OVER (ORDER BY");
        if (("serial_num").equals(sortingOrder)) {
            if (isOracle) {
                sql.append("CAST");
                sql.append(sortingOrder);
                sql.append("AS int)");
            } else {
                sql.append("CONVERT(int,");
                sql.append(sortingOrder);
                sql.append(")");
            }
        } else {
            sql.append(sortingOrder);
        }
        sql.append(" ");
        sql.append(sortingDirection);
        sql.append(")) AS RowNumber, Appliances,DeviceId,InventoryId,serial_num,Type,last_comm,last_run,Availability");
        sql.append("FROM ");
        sql.append("(SELECT DISTINCT ");
        sql.append("(SELECT Description");
        sql.append("FROM ApplianceCategory");
        sql.append("WHERE ApplianceCategoryId=appbase.ApplianceCategoryID) AS appliances,");
        sql.append("inv.DeviceId AS deviceid,lmbase.InventoryID AS inventoryid, lmbase.ManufacturerSerialNumber AS serial_num,");
        sql.append("(SELECT YukonDefinitionId");
        sql.append("FROM YukonListEntry");
        sql.append("WHERE EntryID=lmbase.LMHardwareTypeID) AS type,");
        sql.append("LastCommunication AS last_comm, LastNonZeroRuntime AS last_run,");
        sql.append("(SELECT CASE WHEN inv.DeviceId=0 THEN 'ACTIVE' ELSE");
        sql.append("(SELECT CASE WHEN lmbase.InventoryId IN");
        sql.append("(SELECT DISTINCT ib.InventoryId");
        sql.append("FROM InventoryBase ib ");
        sql.append(  "JOIN OptOutEvent ooe ON ooe.InventoryId = ib.InventoryId WHERE ooe.StartDate").lt(currentTime);
        sql.append(  "AND ooe.StopDate").gt(currentTime);;
        sql.append(  "AND ooe.EventState = 'START_OPT_OUT_SENT') THEN 'OPTED_OUT' ");
        sql.append("ELSE CASE WHEN LastNonZeroRuntime").gt(runtimeWindowEnd);
        sql.append("THEN 'ACTIVE'");
        sql.append("ELSE CASE WHEN LastCommunication").gt(communicatingWindowEnd);
        sql.append("THEN 'INACTIVE' ");
        sql.append("ELSE 'UNAVAILABLE' END END END");
        if (isOracle) {
            sql.append("FROM Dual");
        }
        sql.append(")END");
        if (isOracle) {
            sql.append("FROM Dual");
        }
        sql.append(")  AS availability");
        sql.append("FROM LMHardwareBase lmbase , ApplianceBase appbase,LMHardwareConfiguration hdconf,InventoryBase inv");
        sql.append(  "LEFT OUTER JOIN DynamicLcrCommunications dynlcr ON (inv.DeviceID=dynlcr.DeviceId)");
        sql.append("WHERE inv.InventoryID=lmbase.InventoryID AND lmbase.InventoryID=hdconf.InventoryID");
        sql.append(  "AND hdconf.ApplianceID=appbase.ApplianceID");
        sql.append(  "AND lmbase.InventoryID IN (SELECT DISTINCT InventoryId FROM LMHardwareConfiguration");
        sql.append("WHERE AddressingGroupID").in(loadGroupIds);
        sql.append(")) innertable)outertable");
        if (null != pagingParameters || null != filterCriteria) {
            sql.append(" WHERE ");
        }
        if (null != pagingParameters) {
            sql.append(" RowNumber BETWEEN");
            sql.append(pagingParameters.getOneBasedStartIndex());
            sql.append(  " AND ");
            sql.append(pagingParameters.getOneBasedEndIndex());
        }
        if (null != pagingParameters && null != filterCriteria) {
            sql.append(  " AND ");
        }
        if (null != filterCriteria) {
            sql.append(" Availability IN ( ");
            sql.append(filterCriteria);
            sql.append(")");
        }

        final List<AssetAvailabilityDetails> resultList = new ArrayList<AssetAvailabilityDetails>();

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                AssetAvailabilityDetails assetAvailability = new AssetAvailabilityDetails();
                assetAvailability.setAppliances(rs.getString("appliances"));
                assetAvailability.setSerialNumber(rs.getString("serial_num"));
                assetAvailability.setType(HardwareType.valueOf(rs.getInt("type")));
                assetAvailability.setLastComm(rs.getInstant("last_comm"));
                assetAvailability.setLastRun(rs.getInstant("last_run"));
                if (AssetAvailabilityCombinedStatus.ACTIVE.name().equals(rs.getString("availability"))) {
                    assetAvailability.setAvailability(AssetAvailabilityCombinedStatus.ACTIVE);
                } else if (AssetAvailabilityCombinedStatus.INACTIVE.name().equals(rs.getString("availability"))) {
                    assetAvailability.setAvailability(AssetAvailabilityCombinedStatus.INACTIVE);
                } else if (AssetAvailabilityCombinedStatus.UNAVAILABLE.name().equals(rs.getString("availability"))) {
                    assetAvailability.setAvailability(AssetAvailabilityCombinedStatus.UNAVAILABLE);
                } else if (AssetAvailabilityCombinedStatus.OPTED_OUT.name().equals(rs.getString("availability"))) {
                    assetAvailability.setAvailability(AssetAvailabilityCombinedStatus.OPTED_OUT);
                }
                resultList.add(assetAvailability);
            }
        });
        return resultList;
    }

    @Override
    public AssetAvailabilitySummary getAssetAvailabilitySummary(Iterable<Integer> loadGroupIds,
            String communicatingWindowEnd, String runtimeWindowEnd, String currentTime) {
        boolean isOracle = checkOracleDatabase();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Availability, COUNT(Availability) AS count");
        sql.append("FROM (SELECT DISTINCT lmbase.ManufacturerSerialNumber as serial_num,");
        sql.append("(SELECT CASE WHEN inv.DeviceId=0 THEN 'ACTIVE'");
        sql.append("ELSE (SELECT CASE WHEN lmbase.InventoryId IN ");
        sql.append("(SELECT DISTINCT ib.InventoryId FROM InventoryBase ib ");
        sql.append(  "JOIN OptOutEvent ooe ON ooe.InventoryId = ib.InventoryId WHERE ooe.StartDate").lt(currentTime);
        sql.append(  "AND ooe.StopDate").gt(currentTime);
        sql.append(  "AND ooe.EventState = 'START_OPT_OUT_SENT') THEN 'OPTED_OUT' ");
        sql.append("ELSE CASE WHEN LastNonZeroRuntime").gt(runtimeWindowEnd);
        sql.append("THEN 'ACTIVE' ELSE CASE WHEN LastCommunication").gt(communicatingWindowEnd);
        sql.append("THEN 'INACTIVE' ELSE 'UNAVAILABLE' END END END");
        if (isOracle) {
            sql.append("FROM Dual");
        }
        sql.append(")END");
        if (isOracle) {
            sql.append("From Dual");
        }
        sql.append(")  AS availability");
        sql.append("FROM LMHardwareBase lmbase, ApplianceBase appbase,LMHardwareConfiguration hdconf,InventoryBase inv");
        sql.append("LEFT OUTER JOIN DynamicLcrCommunications dynlcr ON (inv.DeviceId=dynlcr.DeviceId) ");
        sql.append("WHERE inv.InventoryID=lmbase.InventoryID AND lmbase.InventoryID=hdconf.InventoryID ");
        sql.append(  "AND hdconf.ApplianceID=appbase.ApplianceID");
        sql.append(  "AND lmbase.InventoryID IN (SELECT DISTINCT InventoryId FROM LMHardwareConfiguration");
        sql.append("WHERE AddressingGroupID").in(loadGroupIds);
        sql.append(") ) outertable GROUP BY availability");

        final AssetAvailabilitySummary assetAvailabilitySummary = new AssetAvailabilitySummary();

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                String availability = rs.getString("availability");

                switch (availability) {
                case "ACTIVE":
                    assetAvailabilitySummary.setActiveSize(Integer.valueOf(rs.getString("count")));
                    break;
                case "INACTIVE":
                    assetAvailabilitySummary.setInactiveSize(Integer.valueOf(rs.getString("count")));
                    break;
                case "UNAVAILABLE":
                    assetAvailabilitySummary.setUnavailableSize(Integer.valueOf(rs.getString("count")));
                    break;
                case "OPTED_OUT":
                    assetAvailabilitySummary.setOptedOutSize(Integer.valueOf(rs.getString("count")));
                    break;
                }
            }
        });
        int total =
            assetAvailabilitySummary.getActiveSize() + assetAvailabilitySummary.getInactiveSize()
                + assetAvailabilitySummary.getOptedOutSize() + assetAvailabilitySummary.getUnavailableSize();

        assetAvailabilitySummary.setTotalSize(total);
        return assetAvailabilitySummary;
    }
}
