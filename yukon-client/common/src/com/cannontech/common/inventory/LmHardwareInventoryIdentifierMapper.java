package com.cannontech.common.inventory;

import java.sql.SQLException;

import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class LmHardwareInventoryIdentifierMapper implements YukonRowMapper<InventoryIdentifier> {
    @Override
    public InventoryIdentifier mapRow(YukonResultSet rs) throws SQLException {
        int inventoryId = rs.getInt("InventoryId");
        HardwareType type = rs.getEnum("YukonDefinitionId", HardwareType.class);
        
        return new InventoryIdentifier(inventoryId, type);
    }
}