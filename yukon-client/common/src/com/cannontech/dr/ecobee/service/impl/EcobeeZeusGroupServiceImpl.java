package com.cannontech.dr.ecobee.service.impl;

import java.util.List;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;

public class EcobeeZeusGroupServiceImpl implements EcobeeZeusGroupService {

    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusGroupServiceImpl.class);
    @Autowired private EcobeeZeusGroupDao ecobeeZeusGroupDao;

    @Override
    public String getZeusGroupIdForLmGroup(int yukonGroupId) {
        return ecobeeZeusGroupDao.getZeusGroupIdForLmGroup(yukonGroupId);
    }

    @Override
    public List<String> getZeusGroupIdsForInventoryId(int inventoryId) {
        return ecobeeZeusGroupDao.getZeusGroupIdsForInventoryId(inventoryId);
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
    public boolean removeGroupIdForZeusGroupId(String zeusGroupId) {
        boolean deleteSuccess = false;
        try {
            ecobeeZeusGroupDao.removeGroupIdForZeusGroupId(zeusGroupId);
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
    public boolean updateEventId(String eventId, int yukonGroupId) {
        boolean updateSuccess = false;
        try {
            ecobeeZeusGroupDao.updateEventId(eventId, yukonGroupId);
            updateSuccess = true;
        } catch (Exception e) {
            log.error("Error occurred while Inserting an event ID for a Zeus group ID (overwriting any existing value).", e);
        }
        return updateSuccess;
    }

    @Override
    public String getEventId(int yukonGroupId) {
        return ecobeeZeusGroupDao.getEventId(yukonGroupId);
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
}