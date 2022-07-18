package com.cannontech.dr.eatonCloud.job.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobRequestV1;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public abstract class EatonCloudJobHelperService {
    
    protected int maxDevicesPerJob;
    /**
     * Creates new job requests to send to Eaton Cloud
     */
    protected Iterable<EatonCloudJobRequestV1> getRequests(Collection<String> guids, Map<String, Object> params) {
        Iterable<List<String>> devicesPerJob = Iterables.partition(guids, maxDevicesPerJob);
        return Iterables.transform(devicesPerJob, 
                deviceGuids -> new EatonCloudJobRequestV1(deviceGuids, "LCR_Control", params));
    }
}
