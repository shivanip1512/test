package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;

public interface StarsSearchDao {

	/**
	 * Method to get an LMHardware by serial number within a given energy company
	 * @param serialNumber - Serial number to search for
	 * @param energyCompany - Energy company to get device for
	 * @return Hardware if found or null if not found
	 * @throws ObjectInOtherEnergyCompanyException - If hardware exists in another energy company
	 */
	public LiteInventoryBase searchLMHardwareBySerialNumber(
			String serialNumber,
			LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException;

	/**
	 * Method to get an LMHardware by serial number within a given list of energy companies
	 * @param serialNumber - Serial number to search for
	 * @param energyCompanyList - Energy companies to look for device in
	 * @return List of hardware found
	 */
	public List<LiteInventoryBase> searchLMHardwareBySerialNumber(
			String serialNumber, 
			List<LiteStarsEnergyCompany> energyCompanyList);
	
	/**
	 * Method to search for a device by category and name
	 * @param categoryID - Category of device to find
	 * @param deviceName - Name of device to find (method adds wild card on end of name)
	 * @param energyCompany - Energy company to get device for
	 * @return Hardware if found or null if not found
	 * @throws ObjectInOtherEnergyCompanyException - If hardware exists in another energy company
	 */
	public LiteInventoryBase searchForDevice(
			int categoryID, 
			String deviceName, 
			LiteStarsEnergyCompany energyCompany)
			throws ObjectInOtherEnergyCompanyException;

	/**
	 * Method to search for a device by name within a given list of energy companies
	 * @param deviceName - Name of device to find (method adds wild card on end of name)
	 * @param energyCompanyList - Energy companies to look for device in
	 * @return List of hardware found
	 */
	public List<LiteInventoryBase> searchInventoryByDeviceName(
			String deviceName, 
			List<LiteStarsEnergyCompany> energyCompanyList);
	
	/**
	 * Method to find a device by device id for a given energy company
	 * @param deviceID - Id of device
	 * @param energyCompany - Energy company to get device for
	 * @return Hardware if found or null if not found
	 * @throws ObjectInOtherEnergyCompanyException - If hardware exists in another energy company
	 */
	public LiteInventoryBase getDevice(int deviceID, LiteStarsEnergyCompany energyCompany) 
			throws ObjectInOtherEnergyCompanyException;

	/**
	 * Method to find a device by Alternate tracking number within a given list of energy companies
	 * @param altTrackNumber - Tracking number to find (exact match only)
	 * @param energyCompanyList - Energy companies to look for device in
	 * @return List of hardware found
	 */
	public List<LiteInventoryBase> searchInventoryByAltTrackNumber(
			String altTrackNumber, 
			List<LiteStarsEnergyCompany> energyCompanyList);
	
	
}
