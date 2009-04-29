package com.cannontech.stars.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;

public interface StarsInventoryBaseDao {

	/**
	 * Finds Inventory by Inventory Id. Throws NotFoundException if the Inventory is not in the database.
	 * 
	 * @param inventoryId
	 * @return LiteInventoryBase
	 * @throws NotFoundException
	 */
    public LiteInventoryBase getByInventoryId(int inventoryId);
    
    /**
     * Finds Inventory by Device Id. Throws NotFoundException if the Inventory is not in the database.
     * 
     * @param deviceId
     * @return LiteInventoryBase
     * @throws NotFoundException
     */
    public LiteInventoryBase getByDeviceId(int deviceId);
    
    public List<LiteInventoryBase> getByIds(Collection<Integer> inventoryIds);

    public Map<Integer,LiteInventoryBase> getByIdsMap(Collection<Integer> inventoryIds);
    
    /**
     * Method to get a list of all MCTs that have an account for a given list of energy companies
     * @param energyCompanyList - Companies to get hardware for
     * @return List of MCTs with assigned account
     */
    public List<LiteInventoryBase> getAllMCTsWithAccount(List<LiteStarsEnergyCompany> energyCompanyList);

    /**
     * Method to get a list of all MCTs that do not have an account for a given list of energy companies
     * @param energyCompanyList - Companies to get hardware for
     * @return List of MCTs without assigned account
     */
    public List<LiteInventoryBase> getAllMCTsWithoutAccount(List<LiteStarsEnergyCompany> energyCompanyList);

    /**
     * Method to get a list of all hardware for a given list of energy companies
     * @param energyCompanyList - Companies to get hardware for
     * @return List of hardware
     */
    public List<LiteStarsLMHardware> getAllLMHardware(List<LiteStarsEnergyCompany> energyCompanyList);

    /**
     * Method to get a list of all hardware without a load group for a given list of energy companies
     * @param energyCompanyList - Companies to get hardware for
     * @return List of hardware
     */
    public List<LiteStarsLMHardware> getAllLMHardwareWithoutLoadGroups(List<LiteStarsEnergyCompany> energyCompanyList);
    
    /**
     * @deprecated - Any call to this method should be refactored, loading all
     *               inventory from an EnergyCompany at one time is to heavy. 
     */
    @Deprecated
    public List<LiteInventoryBase> getAllByEnergyCompanyList(List<LiteStarsEnergyCompany> energyCompanyList);
    
    /**
     * Saves a Inventory hardware device info on the customer account. Handles both insert/update records.
     */
    public LiteInventoryBase saveInventoryBase(LiteInventoryBase liteInv, int energyCompanyId);    
    
    /**
     * Saves a LM hardware device info on the customer account. Handles both insert/update records.
     */
    public LiteStarsLMHardware saveLmHardware(LiteStarsLMHardware lmHw, int energyCompanyId);
    
    /**
     * Removes a hardware device from the account.
     */
    public void removeInventoryFromAccount(LiteInventoryBase liteInv);    
    
    /**
     * Deletes a hardware device from the inventory.  Deletes only the LM Hardware for now.
     */
    public void deleteInventoryBase(int inventoryId);    
    
}
