package com.cannontech.amr.porterResponseMonitor.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.service.PorterResponseMonitorService;
import com.cannontech.amr.statusPointMonitoring.service.impl.StatusPointMonitorServiceImpl;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class PorterResponseMonitorServiceImpl implements PorterResponseMonitorService {

    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private PorterResponseMonitorDao porterResponseMonitorDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    private final Logger log = YukonLogManager.getLogger(StatusPointMonitorServiceImpl.class);

    @Override
    public void create(PorterResponseMonitor monitor) {
        porterResponseMonitorDao.save(monitor);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.PORTER_RESPONSE_MONITOR, monitor.getMonitorId());
    }

    @Override
    public void update(PorterResponseMonitor monitor) {
        porterResponseMonitorDao.save(monitor);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.PORTER_RESPONSE_MONITOR, monitor.getMonitorId());
    }

    @Override
    public boolean delete(int monitorId) throws NotFoundException {
        userSubscriptionDao.deleteSubscriptionsForItem(SubscriptionType.PORTER_RESPONSE_MONITOR, monitorId);
        boolean deleted = porterResponseMonitorDao.deleteMonitor(monitorId);
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.PORTER_RESPONSE_MONITOR, monitorId);
        return deleted;
    }

    @Override
    public MonitorEvaluatorStatus toggleEnabled(int monitorId) throws NotFoundException {

        PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);

        MonitorEvaluatorStatus currentStatus = monitor.getEvaluatorStatus();
        MonitorEvaluatorStatus newStatus = MonitorEvaluatorStatus.invert(currentStatus);
        monitor.setEvaluatorStatus(newStatus);

        // update
        porterResponseMonitorDao.save(monitor);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.PORTER_RESPONSE_MONITOR, monitor.getMonitorId());
        log.debug("Updated porterResponseMonitor evaluator status: status=" + newStatus + ", porterResponseMonitor=" + monitor);

        return newStatus;
    }
}
