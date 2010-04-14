package com.cannontech.stars.core.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;

public interface StarsInventoryBaseService {

    /**
     * Adds a hardware device to the customer account. If the hardware doesn't
     * exist, then adds it to the inventory and account. If it is in the
     * warehouse, just adds it to the account. Errors out with 
     * StarsDeviceAlreadyAssignedException, if it is currently
     * assigned to another account. If replaceAccount is desired, caller needs to remove it 
     * from the previous account and add it to the new account. Handles only LMHardware
     * devices for now, will need to support other device types later.
     * If allowCreateLcrIfAlreadyHasAssignedDevice is true, for 2 way lcr's, a new 2 way device
     * will be created and assigned to the inventory even if it already had one before.
     * @param liteInv
     * @param energyCompany
     * @param user
     * @param allowCreateLcrIfAlreadyHasAssignedDevice
     * @return LiteInventoryBase
     */
    public LiteInventoryBase addDeviceToAccount(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user, boolean allowCreateLcrIfAlreadyHasAssignedDevice) throws StarsTwoWayLcrYukonDeviceCreationException;

    /**
     * Updates a hardware device info on the customer account. Ex., Field
     * install date, Service Company etc. Handles only LMHardware devices for
     * now, will need to support other device types later.
     * @param liteInv
     * @param energyCompany
     * @param user
     * @return LiteInventoryBase
     */
    public LiteInventoryBase updateDeviceOnAccount(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user);

    /**
     * Removes a hardware device from the customer account. If
     * deleteFromInventory is true, deletes the device from the Inventory also.
     * Handles only LMHardware devices for now, will need to support other
     * device types later.
     * @param liteInv
     * @param deleteFromInventory
     * @param energyCompany
     * @param user
     */
    public void removeDeviceFromAccount(LiteInventoryBase liteInv,
            boolean deleteFromInventory, LiteStarsEnergyCompany energyCompany,
            LiteYukonUser user);

    /**
     * Initializes the default Thermostat Schedule on the account.
     * @param lmHardware
     * @param energyCompany
     */
    public void initThermostatSchedule(LiteStarsLMHardware lmHardware, LiteStarsEnergyCompany energyCompany);

    /**
     * Maps the static load group, if static load group mapping exists
     * @param lmHardware
     * @param energyCompany
     */
    public void initStaticLoadGroup(LiteStarsLMHardware lmHardware, LiteStarsEnergyCompany energyCompany);

    /**
     * Adds the Install hardware event
     * @param liteInv
     * @param energyCompany
     * @param user
     */
    public void addInstallHardwareEvent(LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany, LiteYukonUser user);
}
