package com.cannontech.stars.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;

/**
 * Mapper class which maps a result set row into a LiteStarsLMHardwareMapper object
 */
public class LiteStarsLMHardwareMapper implements
		ParameterizedRowMapper<LiteStarsLMHardware> {

	@Override
	public LiteStarsLMHardware mapRow(ResultSet rs, int rowNum)
			throws SQLException {

		// Set LMHardware attributes
		LiteStarsLMHardware liteHardware = new LiteStarsLMHardware();
		liteHardware.setManufacturerSerialNumber(rs
				.getString("ManufacturerSerialNumber"));
		liteHardware.setLmHardwareTypeID(rs.getInt("LMHardwareTypeId"));
		liteHardware.setRouteID(rs.getInt("RouteId"));
		liteHardware.setConfigurationID(rs.getInt("ConfigurationId"));

		// Set generic LiteInventory attributes
		liteHardware.setInventoryID(rs.getInt("InventoryId"));
		liteHardware.setAccountID(rs.getInt("AccountId"));
		liteHardware.setCategoryID(rs.getInt("CategoryId"));
		liteHardware.setInstallationCompanyID(rs
				.getInt("InstallationCompanyId"));
		liteHardware.setReceiveDate(rs.getTimestamp("ReceiveDate").getTime());
		liteHardware.setInstallDate(rs.getTimestamp("InstallDate").getTime());
		liteHardware.setRemoveDate(rs.getTimestamp("RemoveDate").getTime());
		liteHardware.setAlternateTrackingNumber(rs
				.getString("AlternateTrackingNumber"));
		liteHardware.setVoltageID(rs.getInt("VoltageId"));
		liteHardware.setNotes(rs.getString("Notes"));
		liteHardware.setDeviceID(rs.getInt("DeviceId"));
		liteHardware.setDeviceLabel(rs.getString("DeviceLabel"));
		liteHardware.setCurrentStateID(rs.getInt("CurrentStateId"));

		liteHardware.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));

		return liteHardware;
	}

}
