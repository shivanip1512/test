package com.cannontech.amr.rfn.dao;

import java.util.Collection;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public interface RfnDeviceAttributeDao {
    
    /**
     * Returns the full list of attributes that have corresponding RFN metric IDs.
     * @return
     */
    Collection<BuiltInAttribute> getAttributesForAllTypes();
    
    /**
     * Returns attributes shared by the given paoTypes that have corresponding RFN metric IDs. 
     * @param paoTypes the set of PaoTypes to limit the set to
     * @return
     */
    Collection<BuiltInAttribute> getAttributesForPaoTypes(Set<PaoType> paoTypes);
    
    /**
     * Gets the attribute associated with a given RFN metric ID.
     * @param metricId the RFN metric ID to map to an attribute
     */
    BuiltInAttribute getAttributeForMetricId(Integer metricId);
}
