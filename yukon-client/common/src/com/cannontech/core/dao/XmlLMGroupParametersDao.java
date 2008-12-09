package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.device.lm.LmXmlParameter;

public interface XmlLMGroupParametersDao {

    public List<LmXmlParameter> getParametersForGroup(int groupId);
    
    public boolean add(LmXmlParameter param);
    
    public boolean removeAllByGroupId(int groupId);
    
}
