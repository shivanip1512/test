package com.cannontech.amr.statusPointMonitoring.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.statusPointMonitoring.service.StatusPointMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class StatusPointMonitorServiceImpl implements StatusPointMonitorService {

    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private StatusPointMonitorDao statusPointMonitorDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    private final Logger log = YukonLogManager.getLogger(StatusPointMonitorServiceImpl.class);

    @Override
    public void create(StatusPointMonitor statusPointMonitor) {
        statusPointMonitorDao.save(statusPointMonitor);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.MONITOR, statusPointMonitor.getStatusPointMonitorId());
    }
    
    @Override
    public void update(StatusPointMonitor statusPointMonitor) {
        statusPointMonitorDao.save(statusPointMonitor);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.MONITOR, statusPointMonitor.getStatusPointMonitorId());
    }

    @Override
    public boolean delete(int statusPointMonitorId) throws NotFoundException {
        userSubscriptionDao.deleteSubscriptionsForItem(SubscriptionType.STATUS_POINT_MONITOR, statusPointMonitorId);
        boolean deleted = statusPointMonitorDao.deleteStatusPointMonitor(statusPointMonitorId);
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.MONITOR, statusPointMonitorId);
        return deleted;
    }
    
    @Override
    public MonitorEvaluatorStatus toggleEnabled(int statusPointMonitorId) throws NotFoundException {

        StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);

        MonitorEvaluatorStatus currentStatus = statusPointMonitor.getEvaluatorStatus();
        MonitorEvaluatorStatus newStatus = MonitorEvaluatorStatus.invert(currentStatus);
        statusPointMonitor.setEvaluatorStatus(newStatus);

        // update
        statusPointMonitorDao.save(statusPointMonitor);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.MONITOR, statusPointMonitor.getStatusPointMonitorId());
        log.debug("Updated statusPointMonitor evaluator status: status=" + newStatus + ", statusPointMonitor=" + statusPointMonitor);

        return newStatus;
    }
}
