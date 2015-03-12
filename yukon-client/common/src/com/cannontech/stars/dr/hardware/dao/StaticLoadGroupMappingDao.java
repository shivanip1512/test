package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;

public interface StaticLoadGroupMappingDao {

    StarsStaticLoadGroupMapping getStaticLoadGroupMapping(StarsStaticLoadGroupMapping criteria);

    List<Integer> getLoadGroupIdsForApplianceCategory(int applianceCategoryId);

    List<StarsStaticLoadGroupMapping> getAll();

    List<Integer> getAllIds();
    
}