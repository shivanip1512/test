package com.cannontech.stars.core.dao;

import java.sql.SQLException;

import com.cannontech.database.YukonResultSet;
import com.cannontech.spring.SeparableRowMapper;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;

public class LiteInventoryBaseRowMapper extends SeparableRowMapper<LiteInventoryBase> {

    @Override
    protected LiteInventoryBase createObject(YukonResultSet rs) throws SQLException {
        return new LiteInventoryBase();
    }

    @Override
    protected void mapRow(YukonResultSet rs, LiteInventoryBase inventoryBase) throws SQLException {
        inventoryBase.setInventoryID(rs.getInt("InventoryId"));
        inventoryBase.setAccountID(rs.getInt("AccountId"));
        inventoryBase.setCategoryID(rs.getInt("CategoryId"));
        inventoryBase.setInstallationCompanyID(rs.getInt("InstallationCompanyId"));
        inventoryBase.setReceiveDate(rs.getDate("ReceiveDate").getTime());
        inventoryBase.setInstallDate(rs.getDate("InstallDate").getTime());
        inventoryBase.setRemoveDate(rs.getDate("RemoveDate").getTime());
        inventoryBase.setAlternateTrackingNumber(rs.getString("AlternateTrackingNumber"));
        inventoryBase.setVoltageID(rs.getInt("VoltageId"));
        inventoryBase.setNotes(rs.getString("Notes"));
        inventoryBase.setDeviceID(rs.getInt("DeviceId"));
        inventoryBase.setDeviceLabel(rs.getString("DeviceLabel"));
        inventoryBase.setCurrentStateID(rs.getInt("CurrentStateId"));
        inventoryBase.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));
    }

}
