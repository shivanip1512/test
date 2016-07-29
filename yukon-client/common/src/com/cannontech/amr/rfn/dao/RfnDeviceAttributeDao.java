package com.cannontech.amr.rfn.dao;

import java.util.Collection;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public interface RfnDeviceAttributeDao {

    /**
     * @return the full list of attributes that have corresponding RFN metric IDs.
     */
    Collection<BuiltInAttribute> getAttributesForAllTypes();

    /**
     * @param paoTypes the set of PaoTypes to limit the set to
     * @return attributes shared by the given paoTypes that have corresponding RFN metric IDs.
     */
    Collection<BuiltInAttribute> getAttributesForPaoTypes(Set<PaoType> paoTypes);
    
    /**
     * @return The metricId corresponding to the specified attribute.
     */
    Integer getMetricIdForAttribute(BuiltInAttribute attribute);
}
