package com.cannontech.multispeak.service;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;

public class MultispeakMeterServiceBase {
    
    private static final Logger log = YukonLogManager.getLogger(MultispeakMeterServiceBase.class);
    
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    
    /**
     * Updates the billingCycle device group.
     * The exact parent group to update is configured in MultiSpeak global settings.
     */
    public boolean updateBillingCyle(String newBilling, String meterNumber, YukonDevice yukonDevice, String mspMethod,
            MultispeakVendor mspVendor) {

        if (!StringUtils.isBlank(newBilling)) {

            // Remove from all billing membership groups
            DeviceGroup billingCycledeviceGroup = multispeakFuncs.getBillingCycleDeviceGroup();
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(billingCycledeviceGroup);
            return updatePrefixGroup(newBilling, meterNumber, yukonDevice, mspMethod, mspVendor, deviceGroupParent);
        }

        return false;
    }

    /**
     * Updates the CIS Substation device group.
     * This group (should be) completely managed by MultiSpeak processing.
     */

    public boolean updateSubstationGroup(String substationName, String meterNumber, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor) {

        if (!StringUtils.isBlank(substationName)) {

            // Remove from all substation membership groups
            DeviceGroup substationNameDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.CIS_SUBSTATION);
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(substationNameDeviceGroup);
            return updatePrefixGroup(substationName, meterNumber, yukonDevice, mspMethod, mspVendor, deviceGroupParent);
        }
        return false;
    }

    /**
     * Removes meter from all immediate descendants of deviceGroupParent. Adds
     * meter to a subgroup of deviceGroupParent called groupName. If groupName
     * does not exist, a new group will be created.
     * 
     * @return true if added to new prefix group.
     */
    protected boolean updatePrefixGroup(String groupName, String meterNumber, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor, StoredDeviceGroup deviceGroupParent) {
        boolean alreadyInGroup = false;

        Set<StoredDeviceGroup> deviceGroups =
            deviceGroupMemberEditorDao.getGroupMembership(deviceGroupParent, yukonDevice);
        for (StoredDeviceGroup deviceGroup : deviceGroups) {
            if (deviceGroup.getName().equalsIgnoreCase(groupName)) {
                log.debug("MeterNumber(" + meterNumber + ") - Already in group:  " + groupName);
                alreadyInGroup = true;
            } else {
                int numAffected = deviceGroupMemberEditorDao.removeDevices(deviceGroup, yukonDevice);
                if (numAffected > 0) {
                    multispeakEventLogService.removeMeterFromGroup(meterNumber, deviceGroup.getFullName(), mspMethod,
                        mspVendor.getCompanyName());
                }
            }
        }

        if (!alreadyInGroup) {
            StoredDeviceGroup deviceGroup = deviceGroupEditorDao.getGroupByName(deviceGroupParent, groupName, true);
            int numAffected = deviceGroupMemberEditorDao.addDevice(deviceGroup, yukonDevice);
            if (numAffected > 0) {
                multispeakEventLogService.addMeterToGroup(meterNumber, deviceGroup.getFullName(), mspMethod,
                    mspVendor.getCompanyName());
            }
        }
        return !alreadyInGroup;
    }

}
