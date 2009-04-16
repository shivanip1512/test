package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
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
	 * Method to find an Inventory by device id for a given energy company
	 * @param deviceId - Id of device
	 * @param energyCompany - Energy company to get device for
	 * @return Hardware if found or null if not found
	 * @throws ObjectInOtherEnergyCompanyException - If hardware exists in another energy company
	 */
	public LiteInventoryBase getByDeviceId(int deviceId, LiteStarsEnergyCompany energyCompany) 
			throws ObjectInOtherEnergyCompanyException;

	/**
	 * Method to find an Inventory by inventory id for a given energy company
	 * @param inventoryId - Inventory Id of device
	 * @param energyCompany - Energy company to get device for
	 * @return Hardware if found or null if not found
	 * @throws ObjectInOtherEnergyCompanyException - If hardware exists in another energy company
	 */
	public LiteInventoryBase getById(int inventoryId, LiteStarsEnergyCompany energyCompany) 
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

	/**
	 * Method to find a device by installation company id within a given list of energy companies
	 * @param installationCompanyId - Id of installation company
	 * @param energyCompanyList - Energy companies to look for device in
	 * @return List of hardware found
	 */
	public List<LiteInventoryBase> searchInventoryByInstallationCompany(
			int installationCompanyId, 
			List<LiteStarsEnergyCompany> energyCompanyList);
	
	/**
	 * Method to find all devices on a given route within a given list of energy companies
	 * @param routeId - Id of route
	 * @param energyCompanyList - Energy companies to look for device in
	 * @return List of hardware found
	 */
	public List<LiteStarsLMHardware> searchLMHardwareByRoute(
			int routeId, 
			List<LiteStarsEnergyCompany> energyCompanyList);

	/**
	 * Method to find all devices of a given type and in a given serial number range within a given 
	 * list of energy companies
	 * @param startSerialNumber - Start of serial number range
	 * @param endSerialNumber - End of serial number range
	 * @param deviceTypeDefinitionId - Type of device to look for
	 * @param energyCompanyList - Energy companies to look for device in
	 * @return List of hardware found
	 */
	public List<LiteStarsLMHardware> searchLMHardwareBySerialNumberRange(
			int startSerialNumber, 
			int endSerialNumber, 
			int deviceTypeDefinitionId, 
			List<LiteStarsEnergyCompany> energyCompanyList);
	
	
}
