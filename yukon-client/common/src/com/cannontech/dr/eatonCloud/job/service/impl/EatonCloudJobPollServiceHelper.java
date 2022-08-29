package com.cannontech.dr.eatonCloud.job.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobDeviceErrorV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobStatusResponseV1;

public abstract class EatonCloudJobPollServiceHelper {
    
    private static final Logger log = YukonLogManager.getLogger(EatonCloudJobPollServiceHelper.class);
    @Autowired private DeviceDao deviceDao;
    
    protected Map<String, Integer> getDeviceIdsForGuids(EatonCloudJobStatusResponseV1 response) {
        List<String> guids = new ArrayList<>();
        if (!CollectionUtils.isEmpty(response.getSuccesses())) {
            guids.addAll(response.getSuccesses());
        }
        if (!CollectionUtils.isEmpty(response.getFailures())) {
            guids.addAll(response.getFailures().keySet());
        }
        return deviceDao.getDeviceIds(guids);
    }
    

    protected int parseErrorCode(EatonCloudJobDeviceErrorV1 error, String debugText) {
        int deviceError = 0;
        try {
            deviceError = Integer.valueOf(error.getErrorNumber());
        } catch (Exception e) {
            log.error(debugText + "POLL unable to parse error code:{}",
                    error.getErrorNumber(), e);
        }
        return deviceError;
    }
}
