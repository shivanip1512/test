package com.cannontech.maintenance.task.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<Integer> outOfServiceLcrInventoryIds =
            getAllTwoWayRfnLcrsByStatus(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL);

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
        List<Integer> inServiceLcrInventoryIds =
            getAllTwoWayRfnLcrsByStatus(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL);

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

    private boolean isSuppressMessages(int inventoryId) {
        EnergyCompany ec = ecDao.getEnergyCompanyByInventoryId(inventoryId);
        boolean suppressMessages =
            ecSettingDao.getBoolean(EnergyCompanySettingType.SUPPRESS_IN_OUT_SERVICE_MESSAGES, ec.getId());
        return suppressMessages;
    }

    /**
     * Method to get the all two way RFN LCRs in Yukon based on its service status
     * 
     * @return List of inventory ids for given status
     */
    private List<Integer> getAllTwoWayRfnLcrsByStatus(int status) {
        List<Integer> lcrsForStatus = new ArrayList<>();
        Set<Integer> inventortIds = inventoryDao.getAllTwoWayRfnLcrInventories();

        lcrsForStatus = inventortIds.stream().filter(
            inventoryId -> inventoryBaseDao.getDeviceStatus(inventoryId) == status).collect(Collectors.toList());
        return lcrsForStatus;
    }

}
