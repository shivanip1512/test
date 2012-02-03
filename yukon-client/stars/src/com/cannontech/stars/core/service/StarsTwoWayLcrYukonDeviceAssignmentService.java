package com.cannontech.stars.core.service;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceAssignmentException;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.xml.serialize.StarsInv;

public interface StarsTwoWayLcrYukonDeviceAssignmentService {

	/**
	 * Used to do the work of either creating a new Yukon device and assigning to the Two Way LCR
	 * or to assigning an existing Yukon device to the LCR.
	 * @param starsInv
	 * @param liteInv
	 * @param energyCompany
	 * @throws WebClientException
	 */
	public void assignTwoWayLcrDevice(StarsInv starsInv, LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) throws StarsTwoWayLcrYukonDeviceAssignmentException;

	/**
	 * Creates a new Yukon device and assigns it to the Two Way LCR.
	 * @param liteInv inventory representing the Two Way LCR
	 * @param energyCompany
	 * @param yukonDeviceTypeId the type of Yukon device to create (needs to be a Two Way LCR type or an exception will be thrown0
	 * @param deviceName name of new Yukon Device. Use null to generate a unique device name base on the LCR serial number.
	 * @param demandRateMinutes set to null to use default rate of 300 (5 minute), specifiy in seconds.
	 * @param allowCreateIfAlreadyHasAssignedDevice if false, the Yukon device will not be created if the LCR already has a device assigned to it.
	 * @throws Lcr3102YukonDeviceCreationException if there is an exception creating the new Yukon device, or assigning to the LCR
	 */
	public void assignNewDeviceToLcr(LiteInventoryBase liteInv,
			LiteStarsEnergyCompany energyCompany, 
			int yukonDeviceTypeId,
			String deviceName,
			Integer demandRateSeconds,
			boolean allowCreateIfAlreadyHasAssignedDevice)
			throws Lcr3102YukonDeviceCreationException;
	
}
