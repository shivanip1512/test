package com.cannontech.dr.assetavailability.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.SerialNumberValidation;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.AssetAvailabilityDetails;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.dao.AssetAvailabilityDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.optout.model.OptOutEventState;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class AssetAvailabilityDaoImpl implements AssetAvailabilityDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private EnergyCompanyDao ecDao;

    @Override
    public SearchResults<AssetAvailabilityDetails> getAssetAvailabilityDetails(Iterable<Integer> loadGroupIds,
            PagingParameters pagingParameters, AssetAvailabilityCombinedStatus[] filterCriteria,
            SortingParameters sortingParameters, Instant communicatingWindowEnd, Instant runtimeWindowEnd,
            Instant currentTime, YukonUserContext userContext) {

        String sortingOrder = (sortingParameters == null) ? "SERIAL_NUM" : sortingParameters.getSort();
        Direction sortingDirection = ((sortingParameters == null) ? Direction.asc : sortingParameters.getDirection());
        
        /*
         * SQL first fetches
         * AppliancesCategory,DeviceId,InventoryId,ManufacturerSerialNumber,Type,LastCommunication,
         * LastNonZeroRuntime,Availability for those inventory that belongs to the passed load group.
         * Availability is considered as
         *  1. Active - If the device is in oneway its always considered active. If its a two way device and
         *  its LastNonZeroRuntime greater than the runtimeWindowEnd.
         *  2. Inactive - If LastCommunication is greater than communication window then its Inactive.
         *  3. OptedOut - If the inventory is in OptOutEvent table with StartDate less than current time and
         *  stopDate greater than currentTime and eventState as START_OPT_OUT_SENT. Then that inventory is
         *  considered to be opted out.
         *  4. Unavailable - If non of the above status are valid then the device is considered as unavailable.
         * This row set has a alias innertable. Next the records are ordered by the sortBy column and then a
         * row number is attached to each row in the innertable, this row set is given alias outertable. Then
         * to get the final row set its filtered by row number based on pagination values and filter values.
         */
        
        SqlStatementBuilder sqlPaginateQuery = new SqlStatementBuilder();
        SqlStatementBuilder sqlTotalCountQuery = new SqlStatementBuilder();
        SqlStatementBuilder sqlCommon = new SqlStatementBuilder();
        sqlTotalCountQuery.append("SELECT COUNT(*)");
        sqlPaginateQuery.append("SELECT *");

        sqlPaginateQuery.append("FROM (");
        sqlPaginateQuery.append("SELECT (ROW_NUMBER() OVER (ORDER BY");
        if (("SERIAL_NUM").equals(sortingOrder)) {
            if (isSerialNumberNumeric(userContext)) {
                sqlPaginateQuery.append(castToNumeric(sortingOrder).getSql());
            } else {
                sqlPaginateQuery.append(sortingOrder);
            }
        } else {
            sqlPaginateQuery.append(sortingOrder);
        }
        sqlPaginateQuery.append(" ");
        sqlPaginateQuery.append(sortingDirection);
        sqlPaginateQuery.append(")) AS RowNumber, Appliances,DeviceId,InventoryId,serial_num,Type,last_comm,last_run,"
            + "Availability");
        sqlCommon.append("FROM ");
        sqlCommon.append("(SELECT DISTINCT ");
        sqlCommon.append("(SELECT Description");
        sqlCommon.append("FROM ApplianceCategory");
        sqlCommon.append("WHERE ApplianceCategoryId=appbase.ApplianceCategoryID) AS appliances,");
        sqlCommon.append("inv.DeviceId AS deviceid,lmbase.InventoryID AS inventoryid, lmbase.ManufacturerSerialNumber "
            + "AS serial_num,");
        sqlCommon.append("(SELECT YukonDefinitionId");
        sqlCommon.append("FROM YukonListEntry");
        sqlCommon.append("WHERE EntryID=lmbase.LMHardwareTypeID) AS type,");
        sqlCommon.append("LastCommunication AS last_comm, LastNonZeroRuntime AS last_run,");
        sqlCommon.append("(SELECT CASE WHEN inv.DeviceId=0 THEN").appendArgument_k(
            AssetAvailabilityCombinedStatus.ACTIVE).append("ELSE");
        sqlCommon.append("(SELECT CASE WHEN lmbase.InventoryId IN");
        sqlCommon.append("(SELECT DISTINCT ib.InventoryId");
        sqlCommon.append("FROM InventoryBase ib ");
        sqlCommon.append("JOIN OptOutEvent ooe ON ooe.InventoryId = ib.InventoryId WHERE ooe.StartDate").lt(currentTime);
        sqlCommon.append("AND ooe.StopDate").gt(currentTime);
        sqlCommon.append("AND ooe.EventState").eq_k(OptOutEventState.START_OPT_OUT_SENT).append(")THEN").
            appendArgument_k(AssetAvailabilityCombinedStatus.OPTED_OUT);
        sqlCommon.append("WHEN LastNonZeroRuntime").gt(runtimeWindowEnd);
        sqlCommon.append("THEN").appendArgument_k(AssetAvailabilityCombinedStatus.ACTIVE);
        sqlCommon.append("WHEN LastCommunication").gt(communicatingWindowEnd);
        sqlCommon.append("THEN").appendArgument_k(AssetAvailabilityCombinedStatus.INACTIVE);
        sqlCommon.append("ELSE").appendArgument_k(AssetAvailabilityCombinedStatus.UNAVAILABLE).append("END");
        sqlCommon.append(getTable().getSql());
        sqlCommon.append(")END");
        sqlCommon.append(getTable().getSql());
        sqlCommon.append(")  AS availability");
        sqlCommon.append("FROM LMHardwareBase lmbase , ApplianceBase appbase,LMHardwareConfiguration hdconf,"
            + "InventoryBase inv");
        sqlCommon.append("LEFT OUTER JOIN DynamicLcrCommunications dynlcr ON (inv.DeviceID=dynlcr.DeviceId)");
        sqlCommon.append("WHERE inv.InventoryID=lmbase.InventoryID AND lmbase.InventoryID=hdconf.InventoryID");
        sqlCommon.append("AND hdconf.ApplianceID=appbase.ApplianceID");
        sqlCommon.append("AND lmbase.InventoryID IN (SELECT DISTINCT InventoryId FROM LMHardwareConfiguration");
        sqlCommon.append("WHERE AddressingGroupID").in(loadGroupIds);
        sqlCommon.append(")) innertable ");
        if (null != filterCriteria) {
            sqlCommon.append(" WHERE Availability").in(Lists.newArrayList(filterCriteria));
        }
        sqlTotalCountQuery.append(sqlCommon);
        sqlPaginateQuery.append(sqlCommon);
        sqlPaginateQuery.append(")outertable");
        if (null != pagingParameters) {
            sqlPaginateQuery.append(" WHERE RowNumber BETWEEN");
            sqlPaginateQuery.append(pagingParameters.getOneBasedStartIndex());
            sqlPaginateQuery.append(" AND ");
            sqlPaginateQuery.append(pagingParameters.getOneBasedEndIndex());
        }
        final List<AssetAvailabilityDetails> resultList = new ArrayList<AssetAvailabilityDetails>();

        int totalHitCount = yukonJdbcTemplate.queryForInt(sqlTotalCountQuery);

        if (totalHitCount > 0) {
            yukonJdbcTemplate.query(sqlPaginateQuery, new YukonRowCallbackHandler() {
                @Override
                public void processRow(YukonResultSet rs) throws SQLException {
                    AssetAvailabilityDetails assetAvailability = new AssetAvailabilityDetails();
                    assetAvailability.setAppliances(rs.getString("appliances"));
                    assetAvailability.setSerialNumber(rs.getString("serial_num"));
                    assetAvailability.setType(HardwareType.valueOf(rs.getInt("type")));
                    assetAvailability.setLastComm(rs.getInstant("last_comm"));
                    assetAvailability.setLastRun(rs.getInstant("last_run"));
                    assetAvailability.setAvailability(rs.getEnum("availability", AssetAvailabilityCombinedStatus.class));
                    resultList.add(assetAvailability);
                }
            });
        }
        SearchResults<AssetAvailabilityDetails> result =
            SearchResults.pageBasedForSublist(resultList, pagingParameters, totalHitCount);
        return result;

    }
    @Override
    public AssetAvailabilitySummary getAssetAvailabilitySummary(Iterable<Integer> loadGroupIds,
            Instant communicatingWindowEnd, Instant runtimeWindowEnd, Instant currentTime) {
        /*
         * First the ManufacturerSerialNumber and Availability is fetched for those inventory that belongs to
         * the passed load group. This row set have alias outertable.Then the count of each
         * status(Availability) is found.
         * Availability is considered as
         *  1. Active - If the device is in oneway its always considered active. If its a two way device and
         *  its LastNonZeroRuntime greater than the runtimeWindowEnd.
         *  2. Inactive - If LastCommunication is greater than communication window then its Inactive.
         *  3. OptedOut - If the inventory is in OptOutEvent table with StartDate less than current time and
         *  stopDate greater than currentTime and eventState as START_OPT_OUT_SENT. Then that inventory is
         *  considered to be opted out.
         *  4. Unavailable - If non of the above status are valid then the device is considered as unavailable.
         */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Availability, COUNT(Availability) AS count");
        sql.append("FROM (SELECT DISTINCT lmbase.ManufacturerSerialNumber as serial_num,");
        sql.append("(SELECT CASE WHEN inv.DeviceId=0 THEN").appendArgument_k(AssetAvailabilityCombinedStatus.ACTIVE);
        sql.append("ELSE (SELECT CASE WHEN lmbase.InventoryId IN ");
        sql.append("(SELECT DISTINCT ib.InventoryId FROM InventoryBase ib ");
        sql.append(  "JOIN OptOutEvent ooe ON ooe.InventoryId = ib.InventoryId WHERE ooe.StartDate").lt(currentTime);
        sql.append(  "AND ooe.StopDate").gt(currentTime);
        sql.append("AND ooe.EventState").eq_k(OptOutEventState.START_OPT_OUT_SENT).append(") THEN").appendArgument_k(
            AssetAvailabilityCombinedStatus.OPTED_OUT);
        sql.append("WHEN LastNonZeroRuntime").gt(runtimeWindowEnd);
        sql.append("THEN").appendArgument_k(AssetAvailabilityCombinedStatus.ACTIVE);
        sql.append("WHEN LastCommunication").gt(communicatingWindowEnd);
        sql.append("THEN").appendArgument_k(AssetAvailabilityCombinedStatus.INACTIVE);
        sql.append("ELSE").appendArgument_k(AssetAvailabilityCombinedStatus.UNAVAILABLE).append("END");
        sql.append(getTable().getSql());
        sql.append(")END");
        sql.append(getTable().getSql());
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
                AssetAvailabilityCombinedStatus availability =
                    rs.getEnum("availability", AssetAvailabilityCombinedStatus.class);
                int count = rs.getInt("count");    
                switch (availability) {
                case ACTIVE:
                    assetAvailabilitySummary.setActiveSize(count);
                    break;
                case INACTIVE:
                    assetAvailabilitySummary.setInactiveSize(count);
                    break;
                case UNAVAILABLE:
                    assetAvailabilitySummary.setUnavailableSize(count);
                    break;
                case OPTED_OUT:
                    assetAvailabilitySummary.setOptedOutSize(count);
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
    

    private SqlFragmentSource castToNumeric(String sortingOrder) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder oracleSql =
            builder.buildFor(DatabaseVendor.ORACLE9I, DatabaseVendor.ORACLE10G, DatabaseVendor.ORACLE11G,
                DatabaseVendor.ORACLE12C);
        oracleSql.append("CAST (");
        oracleSql.append(sortingOrder);
        oracleSql.append(" AS NUMBER(19))");

        SqlBuilder otherSql = builder.buildOther();
        otherSql.append("CAST (");
        otherSql.append(sortingOrder);
        otherSql.append(" AS BIGINT)");

        return builder;
    }
    
    private SqlFragmentSource getTable() {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder oracleSql =
            builder.buildFor(DatabaseVendor.ORACLE9I, DatabaseVendor.ORACLE10G, DatabaseVendor.ORACLE11G,
                DatabaseVendor.ORACLE12C);
        oracleSql.append("FROM Dual");

        SqlBuilder otherSql = builder.buildOther();
        otherSql.append("");

        return builder;
    }
    
    private boolean isSerialNumberNumeric(YukonUserContext userContext) {
        YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());

        SerialNumberValidation value =
            energyCompanySettingDao.getEnum(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION,
                SerialNumberValidation.class, yukonEnergyCompany.getEnergyCompanyId());
        if (value.equals(SerialNumberValidation.NUMERIC)) {
            return true;
        } else {
            return false;
        }

    }
}
