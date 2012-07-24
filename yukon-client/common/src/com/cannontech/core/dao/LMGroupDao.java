package com.cannontech.core.dao;

import com.cannontech.stars.dr.hardware.model.ExpressComAddressView;

public interface LMGroupDao {

    /**
     * Returns the Utility Enrollment number for a group. This is an 8 bit value where 0x00 is a special value for all groups.
     * 
     * @param groupId
     * @return
     */
    public byte getUtilityEnrollmentGroupForSepGroup(int groupId);

    /**
     * Returns the java object representation of the ExpressComAddress_View table.
     */
    public ExpressComAddressView getExpressComAddressing(int groupId);

}