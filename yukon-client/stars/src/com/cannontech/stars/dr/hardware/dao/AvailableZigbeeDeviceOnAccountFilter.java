package com.cannontech.stars.dr.hardware.dao;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class AvailableZigbeeDeviceOnAccountFilter implements SqlFilter {
    
    private int accountId;
    
    public AvailableZigbeeDeviceOnAccountFilter(int accountId) {
        this.accountId = accountId;
    }
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
    	SqlStatementBuilder assignedDeviceIds = new SqlStatementBuilder("SELECT DeviceId FROM ZBGatewayToDeviceMapping");
    	
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("lmhw.inventoryId IN (");
        sql.append("  SELECT ib.inventoryId FROM inventoryBase ib ");
        sql.append("    JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("    JOIN LMHardwareBase lmhb ON lmhb.inventoryId = ib.InventoryId");
        sql.append("    JOIN YukonListEntry yle on yle.EntryID = lmhb.LMHardwareTypeID");
        sql.append("  WHERE ib.accountId").eq(accountId);
        sql.append("    AND yle.YukonDefinitionID ").eq(HardwareType.UTILITY_PRO_ZIGBEE);
        sql.append("    AND ib.DeviceID NOT").in(assignedDeviceIds).append(")");

        return sql;
    }
    
}