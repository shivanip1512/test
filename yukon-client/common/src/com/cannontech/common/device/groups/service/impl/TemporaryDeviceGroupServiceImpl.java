package com.cannontech.common.device.groups.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class TemporaryDeviceGroupServiceImpl implements TemporaryDeviceGroupService {

    @Autowired private GlobalSettingDao globalSettingDao = null;
    private DeviceGroupEditorDao deviceGroupEditorDao = null;
    private ScheduledExecutor scheduledExecutor = null;
    
    private Logger log = YukonLogManager.getLogger(TemporaryDeviceGroupServiceImpl.class);
    
    @Override
    public StoredDeviceGroup createTempGroup(String groupName, int deleteDelay, TimeUnit deleteDelayUnit) {
        
        StoredDeviceGroup group = doCreateTempGroup(groupName);
        return group;
    }
    
    @Override
    public StoredDeviceGroup createTempGroup(String groupName) {
        
        StoredDeviceGroup group = doCreateTempGroup(groupName);
        return group;
    }

    @Override
    public StoredDeviceGroup createTempGroup() {
        return createTempGroup(null);
    }    

    private StoredDeviceGroup doCreateTempGroup(String groupName) {
        
        if (StringUtils.isBlank(groupName)) {
            groupName = UUID.randomUUID().toString();
        }
        
        String fullGroupName = SystemGroupEnum.TEMPORARYGROUPS.getFullPath() + groupName;
        final StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(fullGroupName, true);
        
        return group;
    }
        
    public void scheduleTempGroupsDeletion() {
       scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                deleteTemporaryGroups();
            }
            
        }, 0, 1, TimeUnit.DAYS);
        log.info("Scheduled a task to delete temporary device groups once a day.");
    }
    
    /* Delete temp groups older then the number of days specified in the Global Settings*/
    private void deleteTemporaryGroups() {
        
        int days = globalSettingDao.getInteger(GlobalSettingType.TEMP_DEVICE_GROUP_DELETION_IN_DAYS);
        Instant now = new Instant();
        Instant earliestDate = now.minus(Duration.standardDays(days));
        
        int groupsDeleted = 0;
        
        StoredDeviceGroup parentGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.TEMPORARYGROUPS);
        List<StoredDeviceGroup> childGroups = deviceGroupEditorDao.getChildGroups(parentGroup);
        for (StoredDeviceGroup childGroup : childGroups) {
            if(childGroup.getCreatedDate().isBefore(earliestDate)){
                deviceGroupEditorDao.removeGroup(childGroup);
                groupsDeleted++;
            }
        }
        log.info(groupsDeleted +" temporary device groups deleted.");
    }
    
    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Required
    public void setScheduledExecutor(ScheduledExecutor scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
    }
}
