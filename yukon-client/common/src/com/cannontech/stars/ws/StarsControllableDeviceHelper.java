package com.cannontech.stars.ws;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;

public interface StarsControllableDeviceHelper {

    /**
     * For Import - Populates the Model object and calls Service to add a
     * hardware device to the customer account. Handles only LMHardware devices
     * for now, will need to support other device types later.
     */
    public LiteInventoryBase addDeviceToAccount(LmDeviceDto dto, LiteYukonUser ecOperator);

    /**
     * For Import - Populates the Model object and calls Service to Update a
     * hardware device info on the customer account. Ex., Field install date,
     * Service Company etc. Handles only LMHardware devices for now, will need
     * to support other device types later.
     */
    public LiteInventoryBase updateDeviceOnAccount(LmDeviceDto dto, LiteYukonUser ecOperator);

    /**
     * For Import - Populates the Model object and calls Service to Remove a
     * hardware device from the customer account. Handles only LMHardware
     * devices for now, will need to support other device types later.
     */
    public void removeDeviceFromAccount(LmDeviceDto dto, LiteYukonUser ecOperator);
}
