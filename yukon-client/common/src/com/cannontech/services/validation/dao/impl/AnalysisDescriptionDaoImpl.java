package com.cannontech.services.validation.dao.impl;

import java.util.Collections;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.services.validation.dao.AnalysisDescriptionDao;
import com.cannontech.services.validation.model.AnalysisDescription;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

public class AnalysisDescriptionDaoImpl implements AnalysisDescriptionDao {
    
    private Set<AnalysisDescription> analysisDescriptions = Sets.newHashSet();
    private DeviceGroupService deviceGroupService;

    @PostConstruct
    public void init() {
        String theGroupName = "/Validation";
        DeviceGroup theGroup = deviceGroupService.resolveGroupName(theGroupName);
        AnalysisDescription analysisDescription = new AnalysisDescription();
        analysisDescription.setDeviceGroup(theGroup);
        analysisDescriptions.add(analysisDescription);
    }

    public SetMultimap<AnalysisDescription, Integer> loadAnalysisDescriptions() {
        SetMultimap<AnalysisDescription, Integer> deviceGroupCache = HashMultimap.create();
        for (AnalysisDescription analysisDescription : analysisDescriptions) {
            Set<Integer> deviceIds = deviceGroupService.getDeviceIds(Collections.singleton(analysisDescription.getDeviceGroup()));
            deviceGroupCache .putAll(analysisDescription, deviceIds);
        }
        return deviceGroupCache;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
}
