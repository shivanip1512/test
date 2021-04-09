package com.cannontech.dr.pxmw.service.v1;

import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Multimap;

public interface PxMWDataReadService {

    /*
     * Retrieves updated data for the specified devices from the PxMW
     */
    Multimap<PaoIdentifier, PointData> collectDataForRead(Set<Integer> deviceIds);

}
