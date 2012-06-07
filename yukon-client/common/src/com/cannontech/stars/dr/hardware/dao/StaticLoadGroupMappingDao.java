package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;

public interface StaticLoadGroupMappingDao {

    public StarsStaticLoadGroupMapping getStaticLoadGroupMapping(
            StarsStaticLoadGroupMapping criteria);

    public List<Integer> getLoadGroupIdsForApplianceCategory(
            int applianceCategoryId);
}
