package com.cannontech.maintenance.task.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.maintenance.task.service.DrReconciliationService;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

public class DrReconciliationServiceImpl implements DrReconciliationService {
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private InventoryDao inventoryDao;

    @Override
    public List<Integer> getOutOfServiceExpectedLcrs() {
        List<Integer> oosExpectedLcrInventoryIds = new ArrayList<>();
        Set<Integer> outOfServiceLcrInventoryIds = getOutOfServiceLCRs();

        // check if these Out Of Service LCRs are unenrolled currently but was enrolled previously at least
        // once, then add such lCR to the list of LCRs that should be out of service.
        if (!CollectionUtils.isEmpty(outOfServiceLcrInventoryIds)) {
            outOfServiceLcrInventoryIds.forEach(inventoryId -> {
                boolean suppressMessages = isSuppressMessages(inventoryId);
                if (!suppressMessages && enrollmentDao.hasEnrollments(inventoryId)
                    && !enrollmentDao.isEnrolled(inventoryId)) {
                    oosExpectedLcrInventoryIds.add(inventoryId);
                }
            });
        }
        return oosExpectedLcrInventoryIds;
    }

    @Override
    public List<Integer> getInServiceExpectedLcrs() {
        List<Integer> inServiceExpectedLcrInventoryIds = new ArrayList<>();
        Set<Integer> inServiceLcrInventoryIds = getInServiceLCRs();

        // check if these inService LCRs are enrolled currently,
        // then add such lCRs to the list of LCRs that should be InService.
        if (!CollectionUtils.isEmpty(inServiceLcrInventoryIds)) {
            inServiceLcrInventoryIds.forEach(inventoryId -> {
                boolean suppressMessages = isSuppressMessages(inventoryId);
                if (!suppressMessages && enrollmentDao.isEnrolled(inventoryId)) {
                    inServiceExpectedLcrInventoryIds.add(inventoryId);
                }
            });
        }
        return inServiceExpectedLcrInventoryIds;
    }

    /**
     * Method gets all two way RFN LCRs whose service status is 'InService' in Yukon.
     * 
     * @return List of inventory Ids of LCRs
     */
    private Set<Integer> getInServiceLCRs() {
        Map<Integer, Set<Integer>> allLcrsWithServiceStatus = getAllTwoWayLcrStatus();
        Set<Integer> inServiceLcrInventoryIds =
            allLcrsWithServiceStatus.get(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL);
        return inServiceLcrInventoryIds;
    }

    /**
     * Method gets all two way RFN LCRs whose service status is 'out of service' in Yukon.
     * 
     * @return List of inventory Ids of LCRs
     */
    private Set<Integer> getOutOfServiceLCRs() {
        Map<Integer, Set<Integer>> allLcrsWithServiceStatus = getAllTwoWayLcrStatus();
        Set<Integer> outOfServiceLcrInventoryIds =
            allLcrsWithServiceStatus.get(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL);
        return outOfServiceLcrInventoryIds;
    }

    private boolean isSuppressMessages(int inventoryId) {
        EnergyCompany ec = ecDao.getEnergyCompanyByInventoryId(inventoryId);
        boolean suppressMessages =
            ecSettingDao.getBoolean(EnergyCompanySettingType.SUPPRESS_IN_OUT_SERVICE_MESSAGES, ec.getId());
        return suppressMessages;
    }

    /**
     * Method to get the service status of all two way RFN LCRs in Yukon
     * 
     * @return map of status and LCRs with that status
     */
    private Map<Integer, Set<Integer>> getAllTwoWayLcrStatus() {
        Map<Integer, Set<Integer>> statusInventoryMap = new HashMap<>(5);
        Set<Integer> availableLCRs = new HashSet<>();
        Set<Integer> unavailableLCRs = new HashSet<>();
        Set<Integer> tempUnavailableLCRs = new HashSet<>();
        statusInventoryMap.put(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL, availableLCRs);
        statusInventoryMap.put(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL, unavailableLCRs);
        statusInventoryMap.put(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL, tempUnavailableLCRs);

        Set<Integer> inventortIds = inventoryDao.getAllTwoWayLcrInventories();
        inventortIds.forEach(inventoryId -> {
            int status = inventoryBaseDao.getDeviceStatus(inventoryId);
            switch (status) {
            case YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL:
                availableLCRs.add(inventoryId);
                break;
            case YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL:
                unavailableLCRs.add(inventoryId);
                break;
            case YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL:
                tempUnavailableLCRs.add(inventoryId);
                break;
            }
        });
        return statusInventoryMap;
    }

}
