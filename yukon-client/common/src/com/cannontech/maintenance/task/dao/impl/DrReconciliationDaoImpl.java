package com.cannontech.maintenance.task.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.maintenance.task.dao.DrReconciliationDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DrReconciliationDaoImpl implements DrReconciliationDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<Integer> getInServiceExpectedLcrs() {
        List<Integer> inServiceExpectedLcrInventoryIds =
            getAllTwoWayRfnLcrsByStatus(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL);
        return inServiceExpectedLcrInventoryIds;
    }

    @Override
    public List<Integer> getOutOfServiceExpectedLcrs() {
        List<Integer> outOfServiceExpectedLcrInventoryIds =
            getAllTwoWayRfnLcrsByStatus(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL);
        return outOfServiceExpectedLcrInventoryIds;
    }

    private List<Integer> getAllTwoWayRfnLcrsByStatus(int status) {
        /*
         * 1. SQL first fetches inventory ids with their yukonDefinitionIds
         * a. We fetch the latest LM hardware event of two way RFN LCR
         * b. Then we make sure all above event should belong to either InService/OOS LCRs as per status given
         * for this we use above yukonDefinitionIds
         * By this, we will have All 2 way RFN LCRs whose service status in Yukon is required status
         * (InService / OOS)
         * 2. Now we take inventories from above list only if it matches with our enrollment requirement
         * a. We consider LCR for In-Service if it is enrolled currently
         * b. We consider LCR for OOS if its is not enrolled currently and should have at least 1 enrollment
         * previously
         */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        List<Integer> requiredInventories = new ArrayList<>();
        sql.append("SELECT InventoryID");
        sql.append("FROM (");
        sql.append(    "SELECT ib.InventoryID, yle.YukonDefinitionID,");
        sql.append(      "ROW_NUMBER() OVER (PARTITION BY ib.InventoryID ORDER BY ce.EventDateTime DESC) AS rn");
        sql.append(    "FROM InventoryBase ib");
        sql.append(      "JOIN YukonPAObject ypo ON ypo.PAObjectID = ib.DeviceID");
        sql.append(      "JOIN LMHardwareEvent he ON he.InventoryID = ib.InventoryID");
        sql.append(      "JOIN LMCustomerEventBase ce ON ce.EventID = he.EventID");
        sql.append(      "JOIN ECToLMCustomerEventMapping map ON map.EventID = ce.EventID");
        sql.append(      "JOIN YukonListEntry yle ON yle.EntryID = ce.ActionID");
        sql.append(    "WHERE ypo.Type").in(PaoType.getRfLcrTypes());
        sql.append(      "AND yle.YukonDefinitionID IN (");
        sql.append(       YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).append(",");
        sql.append(       YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL).append(",");
        sql.append(       YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).append(",");
        sql.append(       YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG).append(",");
        sql.append(       YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).append(")");
        sql.append(") inventories ");
        sql.append("WHERE rn = 1");

        if (status == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
            sql.append("AND YukonDefinitionID").eq_k(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION);
        } else if (status == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL) {
            sql.append("AND YukonDefinitionID NOT IN (");
            sql.append(    YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).append(",");
            sql.append(    YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION).append(",");
            sql.append(    YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).append(")");
        }

        sql.append(  "AND InventoryID IN (");
        if (status == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
            sql.append(getAllUnenrolledDevicesSql());
        } else if (status == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL) {
            sql.append(getAllEnrolledDevicesSql());
        }
        sql.append(    ")");

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int inventoryId = rs.getInt("InventoryID");
                requiredInventories.add(inventoryId);
            }
        });

        return requiredInventories;
    }
    
    @Override
    public List<Integer> getGroupsWithRfnDeviceEnrolled() {

        final List<Integer> groupsWithRfnDeviceEnrolled = new ArrayList<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT AddressingGroupId");
        sql.append("FROM LMHardwareConfiguration hdconf");
        sql.append("  JOIN InventoryBase inv ON hdconf.InventoryId = inv.InventoryId");
        sql.append("  JOIN YukonPaobject pao ON inv.DeviceID = pao.PAObjectID");
        sql.append("WHERE type").in(PaoType.getRfLcrTypes());

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                groupsWithRfnDeviceEnrolled.add(rs.getInt("AddressingGroupId"));
            }
        });
        return groupsWithRfnDeviceEnrolled;
    }
    
    @Override
    public List<Integer> getEnrolledRfnLcrForGroup(int groupId) {

        final List<Integer> lcrsInGroup = new ArrayList<>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inv.deviceId");
        sql.append("FROM InventoryBase inv");
        sql.append("  JOIN LMHardwareConfiguration hdconf ON inv.InventoryId = hdconf.InventoryId");
        sql.append("WHERE AddressingGroupId").eq(groupId);

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                lcrsInGroup.add(rs.getInt("deviceId"));
            }
        });
        return lcrsInGroup;
    }

    @Override
    public Multimap<Integer, Integer> getLcrEnrolledInMultipleGroup(Set<Integer> lcrs) {

        final Multimap<Integer, Integer> lcrEnrolledInMultipleGroups = HashMultimap.create();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT inv.deviceId, hdconf.AddressingGroupId");
        sql.append("FROM InventoryBase inv ");
        sql.append("  JOIN LMHardwareConfiguration hdconf ON inv.InventoryId = hdconf.InventoryId");
        sql.append("  AND inv.InventoryID IN");
        sql.append("      ( SELECT InventoryID");
        sql.append("        FROM LMHardwareConfiguration");
        sql.append("       GROUP BY InventoryID");
        sql.append("       HAVING COUNT(InventoryID) > 1)");
        sql.append("  AND inv.DeviceId != 0");
        sql.append("  AND inv.DeviceId").in(lcrs);

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                lcrEnrolledInMultipleGroups.put(rs.getInt("deviceId"), rs.getInt("AddressingGroupId"));
            }
        });
        return lcrEnrolledInMultipleGroups;
    }

    /**
     * This method is to generate a SQL which will fetch all devices that are
     * enrolled currently in any of the program
     */
    private SqlFragmentSource getAllEnrolledDevicesSql() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryID ");
        sql.append("FROM LMHardwareControlGroup lhcg");
        sql.append("WHERE lhcg.GroupEnrollStart IS NOT NULL");
        sql.append("AND lhcg.GroupEnrollStop IS NULL");
        return sql;
        
    }

    /**
     * This method is to generate a SQL which fetches all devices that are
     * not enrolled currently in any of the program but have been enrolled previously.
     */
    private SqlFragmentSource getAllUnenrolledDevicesSql() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryID ");
        sql.append("FROM LMHardwareControlGroup lhcg");
        sql.append("WHERE InventoryID NOT IN (");
        sql.append(getAllEnrolledDevicesSql()).append(") ");
        sql.append("AND lhcg.type").eq(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        return sql;
    }
    
    @Override
    public Map<Integer, Integer> getLCRWithLatestEvent(Set<Integer> allLcrs, int noOfLcrs) {
        final Map<Integer, Integer> sendMessageForLcrs = new HashMap<>();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TOP ");
        sql.append(noOfLcrs);
        sql.append(" deviceId, inventoryId FROM (");
        sql.append("    SELECT ib.deviceId as deviceId, ib.InventoryID, MAX(EventDateTime) AS MaxEventTime,");
        sql.append("    CASE WHEN (MAX(LastCommunication)) > (MAX(EventDateTime) +1) THEN 1 ELSE 0 END as lastCommunicated");
        sql.append("    FROM LMHardwareEvent he");
        sql.append("        JOIN LMCustomerEventBase heb ON he.EventID = heb.EventID");
        sql.append("        JOIN InventoryBase ib ON he.InventoryID = ib.InventoryID");
        sql.append("        JOIN DynamicLcrCommunications dylcr ON dylcr.DeviceId = ib.DeviceID");
        sql.append("        JOIN ECToLMCustomerEventMapping map ON map.EventID = heb.EventID");
        sql.append("        JOIN YukonListEntry yle ON yle.EntryID = heb.ActionID");
        sql.append("    WHERE ib.deviceId").in(allLcrs);
        sql.append("    AND yle.YukonDefinitionID IN (");
        sql.append(       YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).append(",");
        sql.append(       YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL).append(",");
        sql.append(       YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG).append(",");
        sql.append(       YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).append(")");
        sql.append("    GROUP BY ib.deviceId, ib.InventoryID");
        sql.append("    ) innerTable ");
        sql.append("    WHERE lastCommunicated = 1");
        sql.append("    ORDER BY MaxEventTime DESC");

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                sendMessageForLcrs.put(rs.getInt("deviceId"), rs.getInt("inventoryId"));
            }
        });
        return sendMessageForLcrs;
    }
}
