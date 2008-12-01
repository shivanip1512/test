package com.cannontech.stars.ws.helper;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.stars.ws.dto.StarsControllableDeviceDTO;

public interface StarsControllableDeviceHelper {

    /**
     * For Import - Populates the Model object and calls Service to add a
     * hardware device to the customer account. Handles only LMHardware devices
     * for now, will need to support other device types later.
     * @param deviceInfo
     * @param user
     * @return LiteInventoryBase
     */
    public LiteInventoryBase addDeviceToAccount(
            StarsControllableDeviceDTO deviceInfo, LiteYukonUser user);

    /**
     * For Import - Populates the Model object and calls Service to Update a
     * hardware device info on the customer account. Ex., Field install date,
     * Service Company etc. Handles only LMHardware devices for now, will need
     * to support other device types later.
     * @param deviceInfo
     * @param user
     * @return LiteInventoryBase
     */
    public LiteInventoryBase updateDeviceOnAccount(
            StarsControllableDeviceDTO deviceInfo, LiteYukonUser user);

    /**
     * For Import - Populates the Model object and calls Service to Remove a
     * hardware device from the customer account. Handles only LMHardware
     * devices for now, will need to support other device types later.
     * @param deviceInfo
     * @param user
     */
    public void removeDeviceFromAccount(StarsControllableDeviceDTO deviceInfo,
            LiteYukonUser user);
}
