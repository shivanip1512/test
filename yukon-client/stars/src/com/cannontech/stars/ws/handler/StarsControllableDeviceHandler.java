package com.cannontech.stars.ws.handler;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.ws.dto.StarsControllableDeviceDTO;
import com.cannontech.user.YukonUserContext;

public interface StarsControllableDeviceHandler {

    /**
     * For Import - Populates the Model object and calls Service to add a
     * hardware device to the customer account. Handles only LMHardware devices
     * for now, will need to support other device types later.
     * @param fields
     * @param energyCompany
     * @param userContext
     * @return LiteInventoryBase
     */
    public LiteInventoryBase addDeviceToAccount(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany, YukonUserContext userContext);

    /**
     * For Import - Populates the Model object and calls Service to Update a
     * hardware device info on the customer account. Ex., Field install date,
     * Service Company etc. Handles only LMHardware devices for now, will need
     * to support other device types later.
     * @param fields
     * @param energyCompany
     * @param userContext
     * @return LiteInventoryBase
     */
    public LiteInventoryBase updateDeviceOnAccount(
            StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany, YukonUserContext userContext);

    /**
     * For Import - Populates the Model object and calls Service to Remove a
     * hardware device from the customer account. Handles only LMHardware
     * devices for now, will need to support other device types later.
     * @param fields
     * @param energyCompany
     * @param userContext
     * @return
     */
    public void removeDeviceFromAccount(StarsControllableDeviceDTO deviceInfo,
            LiteStarsEnergyCompany energyCompany, YukonUserContext userContext);
}
