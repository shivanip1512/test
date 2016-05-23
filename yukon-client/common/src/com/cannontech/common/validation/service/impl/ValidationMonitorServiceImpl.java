package com.cannontech.common.validation.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.common.validation.service.ValidationMonitorService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class ValidationMonitorServiceImpl implements ValidationMonitorService {

    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    @Autowired private ValidationMonitorDao validationMonitorDao;
    private final Logger log = YukonLogManager.getLogger(ValidationMonitorServiceImpl.class);

    @Override
    public void create(ValidationMonitor validationMonitor) {
        validationMonitorDao.saveOrUpdate(validationMonitor);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.MONITOR, validationMonitor.getValidationMonitorId());
    }
    
    @Override
    public void update(ValidationMonitor validationMonitor) {
        validationMonitorDao.saveOrUpdate(validationMonitor);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.MONITOR, validationMonitor.getValidationMonitorId());
    }
    
    @Override
    public boolean delete(int validationMonitorId) {
        userSubscriptionDao.deleteSubscriptionsForItem(SubscriptionType.VALIDATION_MONITOR, validationMonitorId);
        // delete monitor

        boolean deleted = validationMonitorDao.delete(validationMonitorId);
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.MONITOR, validationMonitorId);
        return deleted;
    }
    
    @Override
    public MonitorEvaluatorStatus toggleEnabled(int validationMonitorId) throws ValidationMonitorNotFoundException {

        ValidationMonitor validationMonitor = validationMonitorDao.getById(validationMonitorId);

        // set status
        MonitorEvaluatorStatus newEvaluatorStatus;
        if (validationMonitor.getEvaluatorStatus().equals(MonitorEvaluatorStatus.DISABLED)) {
            newEvaluatorStatus = MonitorEvaluatorStatus.ENABLED;
        } else {
            newEvaluatorStatus = MonitorEvaluatorStatus.DISABLED;
        }
        validationMonitor.setEvaluatorStatus(newEvaluatorStatus);

        // update
        validationMonitorDao.saveOrUpdate(validationMonitor);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.MONITOR, validationMonitor.getValidationMonitorId());
        log.debug("Updated validationMonitor evaluator status: status=" + newEvaluatorStatus + ", validationMonitor=" + validationMonitor.toString());

        return newEvaluatorStatus;
    }
}