package com.cannontech.core.dao;

import com.cannontech.loadcontrol.loadgroup.model.SEPGroupAttributes;
import com.cannontech.stars.dr.hardware.model.ExpressComAddressView;

public interface LMGroupDao {

    /**
     * Returns the SEPGroupAttributes for a group. 
     * 
     * It includes an 8 bit value for Utility Enrollment Group where 0x00 is a special value for all groups.
     * It also includes the Ramp In and Ramp Out time for the device.
     * 
     * @param groupId
     * @return
     */
    public SEPGroupAttributes getSEPAttributesGroupForSepGroup(int groupId);

    /**
     * Returns the java object representation of the ExpressComAddress_View table.
     */
    public ExpressComAddressView getExpressComAddressing(int groupId);

}