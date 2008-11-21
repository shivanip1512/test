package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.device.lm.LMxmlParameter;

public interface XmlLMGroupParametersDao {

    public List<LMxmlParameter> getParametersForGroup(int groupId);
    
    public boolean add(LMxmlParameter param);
    
    public boolean removeAllByGroupId(int groupId);
    
}
