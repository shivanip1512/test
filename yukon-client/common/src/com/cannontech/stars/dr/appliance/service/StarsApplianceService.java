package com.cannontech.stars.dr.appliance.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.xml.serialize.StarsAppliance;


public interface StarsApplianceService {
    
    /**
     * This method creates a Stars appliance including any information in its appliance subtype.  (Ex. Air Condition or Water Heater) 
     * 
     * NOTE: This code was mostly ported from a legacy STARS action.
     * 
     */
    public void createStarsAppliance(StarsAppliance liteStarsAppliance, 
                                     int energyCompanyId,
                                     int accountId,
                                     LiteYukonUser user);

    
    /**
     * This method deletes a Stars appliance including any information in its appliance subtype.  (Ex. Air Condition or Water Heater)
     * It also handles unenrolling the appliance if the appliance is actively enrolled.
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
     * This method updates a Stars appliance including any information for its appliance subtype.  (Ex. Air Condition or Water Heater) 
     * It will also preserve any enrollment that is currently attached to the supplied appliance.
     * 
     * NOTE: This code was mostly ported from a legacy STARS action.
     * 
     */
    public void updateStarsAppliance(StarsAppliance liteStarsAppliance, 
                                       int energyCompanyId,
                                       LiteYukonUser user);

}