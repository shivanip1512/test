package com.cannontech.stars.core.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;

public interface StarsInventoryBaseService {

    /**
     * Adds a hardware device to the customer account. If the hardware doesn't
     * exist, then adds it to the inventory and account. If it is in the
     * warehouse, just adds it to the account. Errors out, if it is currently
     * assigned to another account. If replaceAccount is desired, caller needs to remove it 
     * from the previous account and add it to the new account. Handles only LMHardware
     * devices for now, will need to support other device types later.
     * @param liteInv
     * @param energyCompany
     * @param user
     * @return LiteInventoryBase
     */
    public LiteInventoryBase addDeviceToAccount(LiteInventoryBase liteInv,
            LiteStarsEnergyCompany energyCompany, LiteYukonUser user);

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
     * @return
     */
    public void removeDeviceFromAccount(LiteInventoryBase liteInv,
            boolean deleteFromInventory, LiteStarsEnergyCompany energyCompany,
            LiteYukonUser user);
}
