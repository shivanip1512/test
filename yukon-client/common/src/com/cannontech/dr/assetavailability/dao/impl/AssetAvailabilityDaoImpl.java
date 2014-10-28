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
        sql.append("select *");
        sql.append("from (");
        sql.append("select (ROW_NUMBER() OVER (ORDER BY");
        if (("serial_num").equals(sortingOrder)) {
            if (isOracle) {
                sql.append("CAST");
                sql.append(sortingOrder);
                sql.append("as int)");
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
        sql.append(")) AS rowNumber, appliances,deviceId,inventoryId,serial_num,type,last_comm,last_run,availability");
        sql.append("from ");
        sql.append("(select distinct ");
        sql.append("(select Description");
        sql.append("from ApplianceCategory");
        sql.append("where appliancecategoryid=appbase.ApplianceCategoryID) as appliances,");
        sql.append("inv.deviceid as deviceId,lmbase.InventoryID as inventoryId, lmbase.ManufacturerSerialNumber as serial_num,");
        sql.append("(select yukonDefinitionId");
        sql.append("from YukonListEntry");
        sql.append("where EntryID=lmbase.LMHardwareTypeID) as type,");
        sql.append("LastCommunication as last_comm, LastNonZeroRuntime as last_run,");
        sql.append("(select case when inv.deviceid=0 then 'ACTIVE' else");
        sql.append("(select case when lmbase.inventoryid in");
        sql.append("(select distinct ib.InventoryId");
        sql.append("from InventoryBase ib ");
        sql.append("join OptOutEvent ooe on ooe.InventoryId = ib.InventoryId where ooe.StartDate").lt(currentTime);
        sql.append("and");
        sql.append("ooe.StopDate").gt(currentTime);
        sql.append("and ooe.EventState = 'START_OPT_OUT_SENT') then 'OPTED_OUT' ");
        sql.append(" else case when LastNonZeroRuntime").gt(runtimeWindowEnd);
        sql.append("then 'ACTIVE'");
        sql.append(" else case when LastCommunication").gt(communicatingWindowEnd);
        sql.append("then 'INACTIVE' ");
        sql.append("else 'UNAVAILABLE' end end end");
        if (isOracle) {
            sql.append("from dual");
        }
        sql.append(")end");
        if (isOracle) {
            sql.append("from dual");
        }
        sql.append(")  as availability");
        sql.append("from LMHardwareBase lmbase , ApplianceBase appbase,LMHardwareConfiguration hdconf,InventoryBase inv");
        sql.append("left outer join DynamicLcrCommunications dynlcr on (inv.DeviceID=dynlcr.DeviceId)");
        sql.append("where inv.InventoryID=lmbase.InventoryID AND lmbase.InventoryID=hdconf.InventoryID");
        sql.append("and hdconf.ApplianceID=appbase.ApplianceID");
        sql.append("and lmbase.InventoryID in (select distinct inventoryid from LMHardwareConfiguration");
        sql.append("where AddressingGroupID").in(loadGroupIds);
        sql.append(")) innertable)outertable");
        if (null != pagingParameters || null != filterCriteria) {
            sql.append(" where ");
        }
        if (null != pagingParameters) {
            sql.append(" rowNumber between");
            sql.append(pagingParameters.getOneBasedStartIndex());
            sql.append(" and ");
            sql.append(pagingParameters.getOneBasedEndIndex());
        }
        if (null != pagingParameters && null != filterCriteria) {
            sql.append(" and ");
        }
        if (null != filterCriteria) {
            sql.append(" availability in ( ");
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
        sql.append("select availability, count(availability) as count");
        sql.append("from (select distinct lmbase.ManufacturerSerialNumber as serial_num,");
        sql.append("(select case when inv.deviceid=0 then 'ACTIVE'");
        sql.append("else (select case when lmbase.inventoryid in ");
        sql.append("(select distinct ib.InventoryId from InventoryBase ib ");
        sql.append("join OptOutEvent ooe on ooe.InventoryId = ib.InventoryId where ooe.StartDate").lt(currentTime);
        sql.append("and ooe.StopDate").gt(currentTime);
        sql.append("and ooe.EventState = 'START_OPT_OUT_SENT') then 'OPTED_OUT' ");
        sql.append("else case when LastNonZeroRuntime").gt(runtimeWindowEnd);
        sql.append("then 'ACTIVE' else case when LastCommunication").gt(communicatingWindowEnd);
        sql.append("then 'INACTIVE' else 'UNAVAILABLE' end end end");
        if (isOracle) {
            sql.append("from dual");
        }
        sql.append(")end");
        if (isOracle) {
            sql.append("from dual");
        }
        sql.append(")  as availability");
        sql.append("from LMHardwareBase lmbase , ApplianceBase appbase,LMHardwareConfiguration hdconf,InventoryBase inv");
        sql.append("left outer join DynamicLcrCommunications dynlcr on (inv.DeviceID=dynlcr.DeviceId) ");
        sql.append("where inv.InventoryID=lmbase.InventoryID and lmbase.InventoryID=hdconf.InventoryID ");
        sql.append("and hdconf.ApplianceID=appbase.ApplianceID");
        sql.append("and lmbase.InventoryID in (select distinct inventoryid from LMHardwareConfiguration");
        sql.append("where AddressingGroupID").in(loadGroupIds);
        sql.append(") ) outertable group by availability");

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
