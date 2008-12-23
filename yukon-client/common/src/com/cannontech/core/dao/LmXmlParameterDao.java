package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.device.lm.LmXmlParameter;
import com.cannontech.database.data.device.lm.LMGroupXML.XmlType;

public interface LmXmlParameterDao {

    public List<LmXmlParameter> getParametersForGroup(int groupId, XmlType type);
    
    public boolean add(LmXmlParameter param);
    public boolean update(LmXmlParameter param);
    public boolean removeAllByGroupId(int groupId);
    
}
