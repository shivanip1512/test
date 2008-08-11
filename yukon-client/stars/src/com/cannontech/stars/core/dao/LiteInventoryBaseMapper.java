package com.cannontech.stars.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteMeterHardwareBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.util.YukonListEntryHelper;

/**
 * Mapper class which maps a result set row into a LiteInvenotryBase object
 */
public class LiteInventoryBaseMapper implements
		ParameterizedRowMapper<LiteInventoryBase> {

	private final LiteStarsEnergyCompany energyCompany;

	public LiteInventoryBaseMapper(LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
	}

	@Override
	public LiteInventoryBase mapRow(ResultSet rs, int rowNum)
			throws SQLException {

		LiteInventoryBase liteInventory = null;

		// Get category definition id for this row
		int categoryId = rs.getInt("CategoryId");
		int categoryDefId = YukonListEntryHelper.getYukonDefinitionId(
				energyCompany,
				YukonSelectionListDefs.YUK_LIST_NAME_INVENTORY_CATEGORY,
				categoryId);

		// Create the correct type of LiteInventoryBase based on category
		if (categoryDefId == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC
				|| categoryDefId == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC) {
			// Set LMHardware attributes
			LiteStarsLMHardware liteHardware = new LiteStarsLMHardware();
			liteHardware.setManufacturerSerialNumber(rs
					.getString("ManufacturerSerialNumber"));
			liteHardware.setLmHardwareTypeID(rs.getInt("LMHardwareTypeId"));
			liteHardware.setRouteID(rs.getInt("RouteId"));
			liteHardware.setConfigurationID(rs.getInt("ConfigurationId"));

			liteInventory = liteHardware;

		} else if (categoryDefId == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_NON_YUKON_METER) {
			// Set MeterHardware attributes
			LiteMeterHardwareBase liteMeter = new LiteMeterHardwareBase();
			liteMeter.setMeterNumber(rs.getString("MeterNumber"));
			liteMeter.setMeterTypeID(rs.getInt("MeterTypeId"));

			liteInventory = liteMeter;

		} else {
			// LiteInventory
			liteInventory = new LiteInventoryBase();
		}

		// Set generic LiteInventory attributes
		liteInventory.setInventoryID(rs.getInt("InventoryId"));
		liteInventory.setAccountID(rs.getInt("AccountId"));
		liteInventory.setCategoryID(rs.getInt("CategoryId"));
		liteInventory.setInstallationCompanyID(rs
				.getInt("InstallationCompanyId"));
		liteInventory.setReceiveDate(rs.getTimestamp("ReceiveDate").getTime());
		liteInventory.setInstallDate(rs.getTimestamp("InstallDate").getTime());
		liteInventory.setRemoveDate(rs.getTimestamp("RemoveDate").getTime());
		liteInventory.setAlternateTrackingNumber(rs
				.getString("AlternateTrackingNumber"));
		liteInventory.setVoltageID(rs.getInt("VoltageId"));
		liteInventory.setNotes(rs.getString("Notes"));
		liteInventory.setDeviceID(rs.getInt("DeviceId"));
		liteInventory.setDeviceLabel(rs.getString("DeviceLabel"));
		liteInventory.setCurrentStateID(rs.getInt("CurrentStateId"));

		liteInventory.setEnergyCompanyId(energyCompany.getEnergyCompanyID());

		return liteInventory;
	}

}
