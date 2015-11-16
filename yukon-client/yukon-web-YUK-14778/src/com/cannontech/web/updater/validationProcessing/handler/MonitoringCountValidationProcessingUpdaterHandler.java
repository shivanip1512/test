package com.cannontech.web.updater.validationProcessing.handler;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.validationProcessing.ValidationMonitorUpdaterTypeEnum;

public class MonitoringCountValidationProcessingUpdaterHandler implements ValidationProcessingUpdaterHandler {

    private ValidationMonitorDao validationMonitorDao;
    private DeviceGroupService deviceGroupService;

    @Override
    public String handle(int validationMonitorId, YukonUserContext userContext) {

        String countStr = "N/A";
        
        try {
            
            ValidationMonitor validationMonitor = validationMonitorDao.getById(validationMonitorId);
            String groupName = validationMonitor.getDeviceGroupName();
            
            DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
            int deviceCount = deviceGroupService.getDeviceCount(Collections.singletonList(group));
            countStr = String.valueOf(deviceCount);
            
        } catch (ValidationMonitorNotFoundException e) {
            // no monitor by that id
        } catch (NotFoundException e) {
            // no group. that sucks, but it could totally happen at any time, don't blow up.
        }
        
        return countStr;
    }

    @Override
    public ValidationMonitorUpdaterTypeEnum getUpdaterType() {
        return ValidationMonitorUpdaterTypeEnum.MONITORING_COUNT;
    }
    
    @Autowired
    public void setValidationMonitorDao(ValidationMonitorDao validationMonitorDao) {
        this.validationMonitorDao = validationMonitorDao;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
}