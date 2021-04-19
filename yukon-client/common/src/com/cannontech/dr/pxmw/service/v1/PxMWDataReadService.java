package com.cannontech.dr.pxmw.service.v1;

import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Multimap;

public interface PxMWDataReadService {

    /*
     * Retrieves updated data for the specified devices from the PxMW. Attempts to retrieve data for all attributes on each device. 
     * Non Cellular-LCR devices will be ignored. 
     */
    Multimap<PaoIdentifier, PointData> collectDataForRead(Set<Integer> deviceIds);

    /**
     * Retrieves updated data for the specified devices from the PxMW. All attributes provided will be queried for the provided devices. 
     * If devices are not cellular-LCR devices, or attributes are not available on some attributes. 
     */
    Multimap<PaoIdentifier, PointData> collectDataForRead(Set<Integer> deviceIds, Set<BuiltInAttribute> attributes);

}
