package com.cannontech.amr.rfn.dao;

import java.util.Collection;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public interface RfnDeviceAttributeDao {
    
    Collection<BuiltInAttribute> getAttributesForAllTypes();
    Collection<BuiltInAttribute> getAttributesForPaoTypes(Set<PaoType> paoTypes);
    
    BuiltInAttribute getAttributeForMetricId(Integer metricId);
}
