package com.cannontech.dr.ecobee.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;

public class EcobeeZeusGroupServiceImpl implements EcobeeZeusGroupService {

    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusGroupServiceImpl.class);
    @Autowired private EcobeeZeusGroupDao ecobeeZeusGroupDao;

    @Override
    public List<String> getZeusGroupIdsForLmGroup(int yukonGroupId) {
        return ecobeeZeusGroupDao.getZeusGroupIdsForLmGroup(yukonGroupId);
    }

    @Override
    public List<String> getZeusGroupIdsForInventoryId(int inventoryId) {
        return ecobeeZeusGroupDao.getZeusGroupIdsForInventoryId(inventoryId);
    }
    
    @Override
    public String getZeusGroupId(int yukonGroupId, int inventoryId) {
        return ecobeeZeusGroupDao.getZeusGroupId(yukonGroupId, inventoryId);
    }

    @Override
    public boolean mapGroupIdToZeusGroup(int yukonGroupId, String zeusGroupId, String zeusGroupName) {
        boolean mappingSuccess = false;
        try {
            ecobeeZeusGroupDao.mapGroupIdToZeusGroup(yukonGroupId, zeusGroupId, zeusGroupName);
            mappingSuccess = true;
        } catch (DataAccessException e) {
            log.error("Error occurred while Inserting a mapping for Yukon group to Zeus group ID", e);
        }
        return mappingSuccess;
    }

    @Override
    public boolean removeGroupIdForZeusGroupId(int yukonGroupId, String zeusGroupId) {
        boolean deleteSuccess = false;
        try {
            ecobeeZeusGroupDao.removeGroupIdForZeusGroupId(yukonGroupId, zeusGroupId);
            deleteSuccess = true;
        } catch (DataAccessException e) {
            log.error("Error occurred while removing a mapping for Yukon group to Zeus group ID", e);
        }
        return deleteSuccess;
    }

    @Override
    public boolean mapInventoryToZeusGroupId(int inventoryId, String zeusGroupId) {
        boolean mappingSuccess = false;
        try {
            ecobeeZeusGroupDao.mapInventoryToZeusGroupId(inventoryId, zeusGroupId);
            mappingSuccess = true;
        } catch (DataAccessException e) {
            log.error("Error occurred while Inserting a mapping for inventory ID to Zeus group ID", e);
        }
        return mappingSuccess;
    }

    @Override
    public boolean deleteZeusGroupMappingForInventoryId(int inventoryId) {
        boolean deleteSuccess = false;
        try {
            ecobeeZeusGroupDao.deleteZeusGroupMappingForInventoryId(inventoryId);
            deleteSuccess = true;
        } catch (DataAccessException e) {
            log.error("Error occurred while deleting a mapping for inventory ID to Zeus group ID.", e);
        }
        return deleteSuccess;
    }

    @Override
    public boolean updateEventId(String eventId, String zeusGroupId) {
        boolean updateSuccess = false;
        try {
            ecobeeZeusGroupDao.updateEventId(eventId, zeusGroupId);
            updateSuccess = true;
        } catch (Exception e) {
            log.error("Error occurred while Inserting an event ID for a Zeus group ID (overwriting any existing value).", e);
        }
        return updateSuccess;
    }

    @Override
    public List<String> getEventIds(int yukonGroupId) {
        return ecobeeZeusGroupDao.getEventIds(yukonGroupId);
    }

    @Override
    public int getDeviceCount(String zeusGroupId) {
        return ecobeeZeusGroupDao.getDeviceCount(zeusGroupId);
    }

    @Override
    public List<Integer> getInventoryIdsForZeusGrouID(String zeusGroupId) {
        return ecobeeZeusGroupDao.getInventoryIdsForZeusGrouID(zeusGroupId);
    }

    @Override
    public String zeusGroupName(String zeusGroupId) {
        return ecobeeZeusGroupDao.getZeusGroupName(zeusGroupId);
    }
    
    @Override
    public int getGroupCount() {
        return ecobeeZeusGroupDao.getGroupCount();
    }
    
    @Override
    public int getAllThermostatCount() {
        return ecobeeZeusGroupDao.getAllThermostatCount();
    }

    @Override
    public void removeEventId(String zeusEventId) {
        ecobeeZeusGroupDao.removeEventId(zeusEventId);
    }
    
    @Override
    public String getNextGroupName(int yukonGroupId) {
        List<String> existingNames = ecobeeZeusGroupDao.getZeusGroupNames(yukonGroupId);
        String newGroupName = yukonGroupId + "_";
        int suffix = 0;
        // Find the max suffix used in the name and increment by 1 before using the suffix in group name.
        for (String name : existingNames) {
            //1st group may not contain _ but 2nd group onwards will have a _.
            if (!StringUtils.contains(name, "_")) {
                break;
            }
            String[] tokens = name.split("_");
            if (Integer.valueOf(tokens[1]) > suffix) {
                suffix = Integer.valueOf(tokens[1]);
            }
        }
        return newGroupName.concat(Integer.toString(suffix + 1));
    }

    @Override
    public Integer getGroupIdToEnroll(List<Integer> yukonGroupIds, int inventoryId) {
        // For only one group, add the device to the same group.
        if (CollectionUtils.size(yukonGroupIds) == 1) {
            return yukonGroupIds.get(0);
        }
        // If multiple groups, return the group which do not have mapping with the inventory.
        List<Integer> existingYukonGroupsForInventory = ecobeeZeusGroupDao.getLmGroupsForInventory(inventoryId);
        List<Integer> toEnollList = yukonGroupIds.stream()
                                                 .filter(groupId -> !existingYukonGroupsForInventory.contains(groupId))
                                                 .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(toEnollList) && toEnollList.size() != 1) {
            throw new BadConfigurationException("Found multiple groups to which device can be enrolled.");
        }
        return toEnollList.get(0);
    }
}