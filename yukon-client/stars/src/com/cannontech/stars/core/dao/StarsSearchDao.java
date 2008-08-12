package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;

public interface StarsSearchDao {

	public LiteStarsLMHardware getLMHardwareBySerialNumber(
			String serialNumber,
			LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException;
	
	public LiteInventoryBase searchForDevice(
			int categoryID, 
			String deviceName, 
			LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException;
	
	public LiteInventoryBase getDevice(int deviceID, LiteStarsEnergyCompany energyCompany) 
			throws ObjectInOtherEnergyCompanyException;
	
	public List<LiteStarsLMHardware> getLMHardwareBySerialNumber(
			String serialNumber, 
			List<LiteStarsEnergyCompany> energyCompany);
	
}
