package com.cannontech.common.device.groups.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
        scheduleTempGroupDeletion(group, deleteDelay, deleteDelayUnit);
        
        return group;
    }
    
    @Override
    public StoredDeviceGroup createTempGroup(String groupName) {
        
        StoredDeviceGroup group = doCreateTempGroup(groupName);
        scheduleTempGroupDeletion(group);
        
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
    
    private void scheduleTempGroupDeletion(final StoredDeviceGroup group) {
        int days = globalSettingDao.getInteger(GlobalSettingType.TEMPORARY_DEVICE_GROUP_DELETION_PERIOD);
        scheduleTempGroupDeletion(group, days, TimeUnit.DAYS);
    }    
    
    private void scheduleTempGroupDeletion(final StoredDeviceGroup group, int delay, TimeUnit unit) {
        
        scheduledExecutor.schedule(new Runnable() {
            public void run() {
                log.info("deleting temporary group after delay: " + group);
                deviceGroupEditorDao.removeGroup(group);
            }
            
        }, delay, unit);
    }
    
    @Override
    public void deleteTemporaryGroups() {
        
        StoredDeviceGroup parentGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.TEMPORARYGROUPS);
        List<StoredDeviceGroup> childGroups = deviceGroupEditorDao.getChildGroups(parentGroup);
        for (StoredDeviceGroup childGroup : childGroups) {
            deviceGroupEditorDao.removeGroup(childGroup);
        }
        log.info(childGroups.size()+" temporary device groups deleted.");
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
