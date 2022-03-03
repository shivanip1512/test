package com.cannontech.dr.eatonCloud.dao;

import com.cannontech.core.dao.NotFoundException;

public interface EatonCloudDao {

    /**
     * Returns virtual relay Id for a group
     */
    int getVirtualRelayId(int yukonLmGroupId) throws NotFoundException;

}
