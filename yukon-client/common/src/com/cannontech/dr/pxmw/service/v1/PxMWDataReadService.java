package com.cannontech.dr.pxmw.service.v1;

public interface PxMWDataReadService {

    /*
     * Retrieves updated data for the specified devices from the PxMW
     */
    void collectDataForRead(int deviceId);

}
