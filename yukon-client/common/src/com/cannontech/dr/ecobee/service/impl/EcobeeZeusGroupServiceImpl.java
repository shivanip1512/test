package com.cannontech.dr.ecobee.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;

public class EcobeeZeusGroupServiceImpl implements EcobeeZeusGroupService {

    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusGroupServiceImpl.class);
    @Autowired private EcobeeZeusGroupDao ecobeeZeusGroupDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private LMHardwareControlGroupDao hardwareControlGroupDao;

    @Override
    public List<String> getZeusGroupIdsForLmGroup(int yukonGroupId, int programId) {
        return ecobeeZeusGroupDao.getZeusGroupIdsForLmGroup(yukonGroupId, programId);
    }

    @Override
    public String getZeusGroupId(int yukonGroupId, int inventoryId, int programId) {
        return ecobeeZeusGroupDao.getZeusGroupId(yukonGroupId, inventoryId, programId);
    }

    @Override
    public boolean mapGroupIdToZeusGroup(int yukonGroupId, String zeusGroupId, String zeusGroupName, int programId) {
        boolean mappingSuccess = false;
        try {
            ecobeeZeusGroupDao.mapGroupIdToZeusGroup(yukonGroupId, zeusGroupId, zeusGroupName, programId);
            mappingSuccess = true;
        } catch (DataAccessException e) {
            log.error("Error occurred while Inserting a mapping for Yukon group: {} to Zeus group ID: {}", yukonGroupId,
                    zeusGroupId);
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
    public boolean deleteZeusGroupMappingForInventory(int inventoryId, String zeusGroupId) {
        boolean deleteSuccess = false;
        try {
            ecobeeZeusGroupDao.deleteZeusGroupMappingForInventory(inventoryId, zeusGroupId);
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
        return ecobeeZeusGroupDao.getInventoryIdsForZeusGroupID(zeusGroupId);
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
    public String getNextGroupName(int yukonGroupId, int programId) {
        List<String> existingNames = ecobeeZeusGroupDao.getZeusGroupNames(yukonGroupId);
        String newGroupName = yukonGroupId + "_";
        String programIdSuffix = "_" + programId;
        int suffix = 0;
        // Find the max suffix used in the name and increment by 1 before using the suffix in group name.
        for (String name : existingNames) {
            // 1st group may not contain _ but 2nd group onwards will have a _.
            if (!StringUtils.contains(name, "_")) {
                continue;
            }
            String[] tokens = name.split("_");
            if (Integer.valueOf(tokens[1]) > suffix) {
                suffix = Integer.valueOf(tokens[1]);
            }
        }
        return newGroupName.concat(Integer.toString(suffix + 1)).concat(programIdSuffix);
    }

    @Override
    public boolean shouldEnrollToGroup(int inventoryId, int programId) {
        return ecobeeZeusGroupDao.getLmGroupForInventory(inventoryId, programId) == -1;
    }

    @Override
    public void updateProgramId(String zeusGroupId, int programId) {
        ecobeeZeusGroupDao.updateProgramId(zeusGroupId, programId);
    }

    @Override
    public int getProgramIdToEnroll(int inventoryId, int lmGroupId) {
        List<Integer> programIds = getActiveEnrolmentProgramIds(inventoryId, lmGroupId);
        // In case of single program enrollment, only 1 program id will be available else multiple program ids will be there.
        // Remove already enrolled program ids when multiple program ids are found.
        if (programIds.size() > 1) {
            List<Integer> ecobeeEnrolledProgramIds = ecobeeZeusGroupDao.getProgramIdsEnrolled(inventoryId, lmGroupId);
            programIds.removeIf(programId -> ecobeeEnrolledProgramIds.contains(programId));
        }
        if (programIds.size() > 1) {
            throw new BadConfigurationException("Number of Program IDs in Yukon enrollment should not be more that 1.");
        }
        return programIds.get(0);
    }

    @Override
    public boolean shouldUpdateProgramId(String zeusGroupId) {
        return ecobeeZeusGroupDao.getProgramIdForZeusGroup(zeusGroupId) == DEFAULT_PROGRAM_ID;
    }

    @Override
    public int getProgramIdToUnenroll(int inventoryId, int lmGroupId) {
        List<Integer> yukonEnrolledProgramIds = getActiveEnrolmentProgramIds(inventoryId, lmGroupId);
        // In case of single program enrollment, only 1 program id will be available else multiple program ids will be there.
        // Remove active enrollment program ids from ecobeeEnrolledProgramIds.
        List<Integer> ecobeeEnrolledProgramIds = ecobeeZeusGroupDao.getProgramIdsEnrolled(inventoryId, lmGroupId);
        if (ecobeeEnrolledProgramIds.size() > 0) {
            ecobeeEnrolledProgramIds.removeIf(programId -> yukonEnrolledProgramIds.contains(programId));
        }
        if (ecobeeEnrolledProgramIds.size() == 0) {
            return DEFAULT_PROGRAM_ID;
        }
        if (ecobeeEnrolledProgramIds.size() > 1) {
            throw new BadConfigurationException("Number of Program IDs in ecobee enrollment should not be more that 1.");
        } else {
            return ecobeeEnrolledProgramIds.get(0);
        }
    }

    @Override
    public List<Integer> getActiveEnrolmentProgramIds(int inventoryId, int lmGroupId) {
        int accountId = customerAccountDao.getAccountByInventoryId(inventoryId).getAccountId();
        return hardwareControlGroupDao.getActiveEnrolmentProgramIds(
                inventoryId, lmGroupId, accountId);
    }

    @Override
    public List<String> getZeusGroupIdsForInventoryId(int inventoryId) {
        return ecobeeZeusGroupDao.getZeusGroupIdsForInventoryId(inventoryId);
    }

    @Override
    public void deleteZeusGroupMapping(String zeusGroupId) {
        ecobeeZeusGroupDao.deleteZeusGroupMapping(zeusGroupId);
    }

    @Override
    public void updateZeusGroupId(String oldZeusGroupId, String newZeusGroupId) {
        ecobeeZeusGroupDao.updateZeusGroupId(oldZeusGroupId, newZeusGroupId);
    }

}