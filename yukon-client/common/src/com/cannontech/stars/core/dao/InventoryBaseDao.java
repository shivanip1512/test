package com.cannontech.stars.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteMeterHardwareBase;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface InventoryBaseDao {

	/**
	 * Finds Inventory by Inventory Id. Throws NotFoundException if the Inventory is not in the database.
	 *
	 * @param inventoryId
	 * @return LiteInventoryBase
	 * @throws NotFoundException
	 */
    public LiteInventoryBase getByInventoryId(int inventoryId) throws NotFoundException;

    public LiteLmHardwareBase getHardwareByInventoryId(int inventoryId);

    public LiteLmHardwareBase getHardwareByDeviceId(int deviceId);

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
     * Method to get a list of all hardware for a given list of energy companies
     * @param energyCompanyList - Companies to get hardware for
     * @return List of hardware
     */
    public List<LiteLmHardwareBase> getAllLMHardware(Collection<YukonEnergyCompany> yecList);

    /**
     * Method to get a list of all hardware with given inventory ids
     * @param inventoryIds
     * @return
     */
    public List<LiteLmHardwareBase> getLMHardwareForIds(Collection<Integer> inventoryIds);

    /**
     * Method to get a list of all hardware without a load group for a given list of energy companies
     * @param energyCompanyList - Companies to get hardware for
     * @return List of hardware
     */
    public List<LiteLmHardwareBase> getAllLMHardwareWithoutLoadGroups(Collection<YukonEnergyCompany> yecList);

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
    public LiteLmHardwareBase saveLmHardware(LiteLmHardwareBase lmHw, int energyCompanyId);

    /**
     * Removes a hardware device from the account.
     */
    public void removeInventoryFromAccount(int inventoryId, Instant removeDate, String removeLbl);

    /**
     * Method to get a list of paos that are not associated with an inventory object
     */
    public List<PaoIdentifier> getPaosNotInInventory();

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

    public InventoryBase findById(int inventoryId);

    public List<Integer> getInventoryIdsByAccountId(int accountId);

    public void updateCurrentState(int inventoryId, int stateId);

    public int getDeviceStatus(int inventoryId);

    /**
     * Adds the entry for this inventory (meter) to LMHardwareBase. Meter's do not start out with this value
     * and it needs to be added for meters that are used for enrollment.
     */
    public void addLmHardwareToMeterIfMissing(String manufacturerSerialNumber, int inventoryId, YukonEnergyCompany ec);


}