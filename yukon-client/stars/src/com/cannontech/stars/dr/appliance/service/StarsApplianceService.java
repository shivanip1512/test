package com.cannontech.stars.dr.appliance.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.xml.serialize.StarsAppliance;


public interface StarsApplianceService {
    
    /**
     * This method creates a Stars appliance including any information for the appliance subtypes 
     * such as Air Conditioner or Water Heater.
     * 
     * NOTE: This code was mostly ported from a legacy STARS action.
     * 
     */
    public void createStarsAppliance(StarsAppliance liteStarsAppliance, 
                                      int energyCompanyId,
                                      int accountId);

    
    /**
     * This method deletes a Stars appliance including any information for the appliance subtypes 
     * such as Air Conditioner or Water Heater.  It also handles enrollment if the selected
     * appliance is a part of an active enrollment.
     * 
     * NOTE: This code was mostly ported from a legacy STARS action.
     * 
     */
    public void removeStarsAppliance(int applianceId, 
                                       int energyCompanyId, 
                                       String accountNumber, 
                                       LiteYukonUser user);

    /**
     * 
     * This method updates a Stars appliance including any information for the appliance subtypes 
     * such as Air Conditioner or Water Heater.  It will also preserve an enrollment that
     * is currently attached to the the supplied appliance
     * 
     * NOTE: This code was mostly ported from a legacy STARS action.
     * 
     */
    public void updateStarsAppliance(StarsAppliance liteStarsAppliance, 
                                       int energyCompanyId);

}