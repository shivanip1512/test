package com.cannontech.dr.pxmw.service.v1;

import java.util.Set;

public interface PxMWDataReadService {

    /*
     * Retrieves updated data for the specified devices from the PxMW
     */
    void collectDataForRead(Set<Integer> deviceIds);

}
