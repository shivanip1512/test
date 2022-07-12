package com.cannontech.dr.eatonCloud.job.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobRequestV1;
import com.google.common.collect.Lists;

public abstract class EatonCloudJobHelperService {
    
    protected int maxDevicesPerJob;
    /**
     * Creates new job requests to send to Eaton Cloud
     */
    protected List<EatonCloudJobRequestV1> getRequests(Map<Integer, String> guids, Set<Integer> devices, Map<String, Object> params) {
        List<List<Integer>> devicesPerJob = Lists.partition(Lists.newArrayList(devices), maxDevicesPerJob);
        return devicesPerJob.stream().map(paoIds -> {
            List<String> deviceGuids = paoIds.stream()
                    .map(paoId -> guids.get(paoId))
                    .collect(Collectors.toList());
            return new EatonCloudJobRequestV1(deviceGuids, "LCR_Control", params);
        }).collect(Collectors.toList());
    }
}
