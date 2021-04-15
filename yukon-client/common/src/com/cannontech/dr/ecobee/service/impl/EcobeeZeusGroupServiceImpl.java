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
    public List<String> getZeusGroupIdsForLmGroup(String yukonGroupId) {
        return ecobeeZeusGroupDao.getZeusGroupIdsForLmGroup(yukonGroupId);
    }

    @Override
    public List<String> getZeusGroupIdsForInventoryId(String inventoryId) {
        return ecobeeZeusGroupDao.getZeusGroupIdsForInventoryId(inventoryId);
    }

    @Override
    public String getZeusGroupId(String yukonGroupId, String inventoryId) {
        return ecobeeZeusGroupDao.getZeusGroupId(yukonGroupId, inventoryId);
    }

    @Override
    public boolean mapGroupIdToZeusGroupId(String yukonGroupId, String zeusGroupId) {
        boolean mappingSuccess = false;
        try {
            ecobeeZeusGroupDao.mapGroupIdToZeusGroupId(yukonGroupId, zeusGroupId);
            mappingSuccess = true;
        } catch (DataAccessException e) {
            log.error("Error occurred while Inserting a mapping for Yukon group to Zeus group ID", e);
        }
        return mappingSuccess;
    }

    @Override
    public boolean removeGroupIdForZeusGroupId(String yukonGroupId, String zeusGroupId) {
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
    public boolean mapInventoryToZeusGroupId(String inventoryId, String zeusGroupId) {
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
    public boolean deleteInventoryToZeusGroupId(String inventoryId) {
        boolean deleteSuccess = false;
        try {
            ecobeeZeusGroupDao.deleteInventoryToZeusGroupId(inventoryId);
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
    public List<String> getEventIds(String yukonGroupId) {
        return ecobeeZeusGroupDao.getEventIds(yukonGroupId);
    }
}