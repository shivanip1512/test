package com.cannontech.maintenance.task.service.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.maintenance.task.dao.DrReconciliationDao;
import com.cannontech.maintenance.task.service.DrReconciliationService;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

public class DrReconciliationServiceImpl implements DrReconciliationService {
    @Autowired private DrReconciliationDao drReconciliationDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;

    @Override
    public List<Integer> getOutOfServiceExpectedLcrs() {
        if (!isSupressMessage()) {
            List<Integer> oosExpectedLcrInventoryIds = drReconciliationDao.getOutOfServiceExpectedLcrs();
            return oosExpectedLcrInventoryIds;
        }
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getInServiceExpectedLcrs() {
        if (!isSupressMessage()) {
            List<Integer> inServiceExpectedLcrInventoryIds = drReconciliationDao.getInServiceExpectedLcrs();
            return inServiceExpectedLcrInventoryIds;
        }
        return Collections.emptyList();
    }

    private boolean isSupressMessage() {
        List<EnergyCompany> allEnergyCompanies = (List<EnergyCompany>) energyCompanyDao.getAllEnergyCompanies();
        for (EnergyCompany energyCompany : allEnergyCompanies) {
            boolean isSupressMessage = ecSettingDao.getBoolean(
                EnergyCompanySettingType.SUPPRESS_IN_OUT_SERVICE_MESSAGES, energyCompany.getId());
            if (isSupressMessage) {
                return isSupressMessage;
            }
        }
        return false;
    }

}
