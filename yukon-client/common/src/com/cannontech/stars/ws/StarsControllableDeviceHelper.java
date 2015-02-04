package com.cannontech.stars.ws;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface StarsControllableDeviceHelper {

    /**
     * For Import - Populates the Model object and calls Service to add a
     * hardware device to the customer account. Handles only LMHardware devices
     * for now, will need to support other device types later.
     */
    public LiteInventoryBase addDeviceToAccount(LmDeviceDto dto, LiteYukonUser user);

    /**
     * For Import - Populates the Model object and calls Service to add a
     * hardware device to the customer account. Use this method only when you
     * already have energy company or in large loops. This method reduces the
     * number of Database hits considerably
     */
    public LiteInventoryBase addDeviceToAccount(LmDeviceDto dto, LiteYukonUser user, YukonEnergyCompany energyCompany);

    /**
     * For Import - Populates the Model object and calls Service to Update a
     * hardware device info on the customer account. Ex., Field install date,
     * Service Company etc. Handles only LMHardware devices for now, will need
     * to support other device types later.
     */
    public LiteInventoryBase updateDeviceOnAccount(LmDeviceDto dto, LiteYukonUser user);

    /**
     * For Import - Populates the Model object and calls Service to Update a
     * hardware device info on the customer account. Ex., Field install date,
     * Service Company etc. Use this method only when you already have energy
     * company or in large loops. This method reduces the number of Database
     * hits considerably
     */

    public LiteInventoryBase updateDeviceOnAccount(LmDeviceDto dto, LiteYukonUser user, YukonEnergyCompany energyCompany);

    /**
     * For Import - Populates the Model object and calls Service to Remove a
     * hardware device from the customer account. Handles only LMHardware
     * devices for now, will need to support other device types later.
     */
    public void removeDeviceFromAccount(LmDeviceDto dto, LiteYukonUser user);

    /**
     * For Import - Populates the Model object and calls Service to Remove a
     * hardware device from the customer account. Use this method only when you
     * already have energy company or in large loops. This method reduces the
     * number of Database hits considerably
     */

    public void removeDeviceFromAccount(LmDeviceDto dto, LiteYukonUser user, YukonEnergyCompany energyCompany);
}
