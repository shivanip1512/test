package com.cannontech.maintenance.task.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

        if (YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL == status) {
            sql.append("AND YukonDefinitionID").eq_k(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION);
        } else if (YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL == status) {
            sql.append("AND YukonDefinitionID NOT IN (");
            sql.append(    YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).append(",");
            sql.append(    YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION).append(",");
            sql.append(    YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).append(")");
        }

        sql.append(  "AND InventoryID IN (");
        if (YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL == status) {
            sql.append(getAllEnrolledDevicesSql());
        } else if (YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL == status) {
            sql.append(getAllUnenrolledDevicesSql());
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

    /**
     * This method is to generate a SQL which will fetch all devices that are
     * 1. Enrolled currently in any of the program
     * 2. Not Opted out currently
     */
    private SqlFragmentSource getAllEnrolledDevicesSql() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(    "SELECT InventoryID ");
        sql.append(    "FROM (");
        sql.append(        "SELECT InventoryID, GroupEnrollStart, GroupEnrollStop, ROW_NUMBER() ");
        sql.append(          "OVER (PARTITION BY lhcg.InventoryID, lhcg.programId ORDER BY lhcg.ControlEntryID DESC) AS rnum");
        sql.append(        "FROM LMHardwareControlGroup lhcg");
        sql.append(    ") lmInventories");
        sql.append(    "WHERE rnum = 1"); 
        sql.append(      "AND lmInventories.GroupEnrollStart IS NOT NULL");
        sql.append(      "AND lmInventories.GroupEnrollStop IS NULL");
        return sql;
        
    }

    /**
     * This method is to generate a SQL which fetches all devices that are
     * 1. Not enrolled currently in any of the program
     * 2. Not opted out currently
     */
    private SqlFragmentSource getAllUnenrolledDevicesSql() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(    "SELECT InventoryID ");
        sql.append("FROM LMHardwareControlGroup lhcg");
        sql.append("WHERE InventoryID NOT IN (");
        sql.append(getAllEnrolledDevicesSql()).append(") ");
        sql.append(  "AND lhcg.type").eq(LMHardwareControlGroup.ENROLLMENT_ENTRY);
        return sql;
    }
}
