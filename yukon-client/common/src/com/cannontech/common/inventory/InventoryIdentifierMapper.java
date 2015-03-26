package com.cannontech.common.inventory;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class InventoryIdentifierMapper implements YukonRowMapper<InventoryIdentifier> {
    
    private YukonListDao yukonListDao;
    
    @Override
    public InventoryIdentifier mapRow(YukonResultSet rs) throws SQLException {
        int inventoryId = rs.getInt("InventoryId");
        
        /* Both of these being null would mean this is an MCT and type should be 0 */
        Integer lmHardwareTypeId = rs.getNullableInt("LmHardwareTypeId");
        Integer meterTypeId = null;
        
        try {
            meterTypeId = rs.getNullableInt("MeterTypeId");
        } catch (SQLException e) { /* Must not be a 'stars' metering system. */ }
        
        int typeId = 0;
        
        if (lmHardwareTypeId != null) {
            typeId = yukonListDao.getYukonListEntry(lmHardwareTypeId).getYukonDefID();
        } else if (meterTypeId != null) {
            typeId = yukonListDao.getYukonListEntry(meterTypeId).getYukonDefID();
        }
        HardwareType type = HardwareType.valueOf(typeId);
        
        return new InventoryIdentifier(inventoryId, type);
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
    
}