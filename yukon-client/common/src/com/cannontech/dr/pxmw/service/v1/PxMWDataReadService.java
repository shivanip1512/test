package com.cannontech.dr.pxmw.service.v1;

import java.util.List;

public interface PxMWDataReadService {

    /*
     * Retrieves updated data for the specified devices from the PxMW
     */
    void collectDataForRead(List<Integer> deviceIds);

}
