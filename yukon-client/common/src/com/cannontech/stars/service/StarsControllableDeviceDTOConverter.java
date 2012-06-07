package com.cannontech.stars.service;

import java.text.ParseException;

import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.ws.StarsControllableDeviceDTO;

public interface StarsControllableDeviceDTOConverter {

	/**
	 * Creates a new device DTO based off a customer account number, and an array of hardware data as supplied by the IportCustAccountsTask. 
	 * @param accountNumber
	 * @param hwFields
	 * @param energyCompany
	 * @return
	 * @throws ParseException
	 */
	public StarsControllableDeviceDTO createNewDto(String accountNumber, String[] hwFields, LiteStarsEnergyCompany energyCompany) throws ParseException;
	
	/**
	 * Creates a device DTO for a customer account, based on an existing hardware device.
	 * @param accountNumber
	 * @param liteInv
	 * @param energyCompany
	 * @return
	 */
	public StarsControllableDeviceDTO getDtoForHardware(String accountNumber, LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany);

	/**
	 * Updates a device DTO based on an array of hardware data as supplied by the IportCustAccountsTask.
	 * @param dto
	 * @param hwFields
	 * @param energyCompany
	 * @throws ParseException
	 */
	public void updateDtoWithHwFields (StarsControllableDeviceDTO dto, String[] hwFields, LiteStarsEnergyCompany energyCompany) throws ParseException;
}
