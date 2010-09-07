package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.capcontrol.model.PointDelta;

public interface DynamicDataDao {

    public List<PointDelta> getAllPointDeltasForBankIds(List<Integer> bankIds);
    
}
