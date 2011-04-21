package com.cannontech.stars.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteMeterHardwareBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;

public interface StarsInventoryBaseDao {

	/**
	 * Finds Inventory by Inventory Id. Throws NotFoundException if the Inventory is not in the database.
	 * 
	 * @param inventoryId
	 * @return LiteInventoryBase
	 * @throws NotFoundException
	 */
    public LiteInventoryBase getByInventoryId(int inventoryId) throws NotFoundException;

    public LiteStarsLMHardware getHardwareByInventoryId(int inventoryId);

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
     * Method to get a list of all hardware with given inventory ids
     * @param inventoryIds
     * @return
     */
    public List<LiteStarsLMHardware> getLMHardwareForIds(List<Integer> inventoryIds);
    
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
     * Updates the deviceId of the InventoryBase table with inventoryId.
     * @param inventoryId
     * @param deviceId
     */
    public void updateInventoryBaseDeviceId(int inventoryId, int deviceId);
    
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
    public void removeInventoryFromAccount(int inventoryId, Instant removeDate);
    
    /**
     * Deletes a hardware device from the inventory.  Deletes only the LM Hardware for now.
     */
    public void deleteInventoryBase(int inventoryId);    
    
    /**
     * Method to get a list of paos that are not associated with an inventory object
     */
    public List<PaoIdentifier> getPaosNotInInventory();

    /**
     * Method to get a list of paos that are associated with an inventory object and are NOT
     * on an account
     */
    public List<PaoIdentifier> getPaosNotOnAnAccount(List<LiteStarsEnergyCompany> energyCompanyList);

    /**
     * Returns a list of switch inventory id's assigned to the meter.
     */
    public List<Integer> getSwitchAssignmentsForMeter(int meterId);

    /**
     * Returns a list of DisplayableLmHardware's of the specified LMHardwareClass on this account.
     */
    public List<DisplayableLmHardware> getLmHardwareForAccount(int accountId,HardwareClass lmHardwareClass);
    
    /**
     * Returns the meter id if this switch is assigned to a meter, null if it is not.
     * @return the meter id this switch is assigned to or null
     */
    public Integer findMeterAssignment(int lmHardwareId);

    /**
     * Saves a Meter hardware device info on the customer account. Handles both insert/update records.
     * @return LiteMeterHardwareBase the meter that was inserted or updated.
     */
    public LiteMeterHardwareBase saveMeterHardware(LiteMeterHardwareBase meterHardwareBase, int energyCompanyId);

    /**
     * Saves the switch to meter mappings in LMHardwareToMeterMapping
     */
    public void saveSwitchAssignments(Integer meterId, List<Integer> switchIds);

}
