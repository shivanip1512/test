package com.cannontech.stars.core.service;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceAssignmentException;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;

public interface StarsTwoWayLcrYukonDeviceAssignmentService {

	/**
	 * Creates a new Yukon device and assigns it to the Two Way LCR.
	 * @param liteInv inventory representing the Two Way LCR
	 * @param energyCompany
	 * @param yukonDeviceTypeId the type of Yukon device to create (needs to be a Two Way LCR type or an exception will be thrown0
	 * @param deviceName name of new Yukon Device. Use null to generate a unique device name base on the LCR serial number.
	 * @param demandRate set to null to use default rate of 300 (5 minute)
	 * @param allowCreateIfAlreadyHasAssignedDevice if false, the Yukon device will not be created if the LCR already has a device assigned to it.
	 * @throws StarsTwoWayLcrYukonDeviceCreationException if there is an exception creating the new Yukon device, or assigning to the LCR
	 */
	public void assignNewDeviceToLcr(LiteInventoryBase liteInv,
			LiteStarsEnergyCompany energyCompany, 
			int yukonDeviceTypeId,
			String deviceName,
			Integer demandRate,
			boolean allowCreateIfAlreadyHasAssignedDevice)
			throws StarsTwoWayLcrYukonDeviceCreationException;
	
	/**
	 * Assigns an existing Yukon device to the Two Way LCR
	 * @param liteInv inventory representing the Two Way LCR
	 * @param energyCompany
	 * @param deviceId the devieId of the existing Yukon device
	 * @throws StarsTwoWayLcrYukonDeviceAssignmentException
	 */
	public void assignExistingDeviceToLcr(LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany, int deviceId) throws StarsTwoWayLcrYukonDeviceAssignmentException;
}
