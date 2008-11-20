package com.cannontech.stars.dr.hardware.dao;

import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;

public interface StaticLoadGroupMappingDao {

    public StarsStaticLoadGroupMapping getStaticLoadGroupMapping(
            StarsStaticLoadGroupMapping criteria);
}
