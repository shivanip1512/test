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

public class PorterResponseMonitorServiceImpl implements PorterResponseMonitorService {

    @Autowired private PorterResponseMonitorDao porterResponseMonitorDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    private Logger log = YukonLogManager.getLogger(StatusPointMonitorServiceImpl.class);

    @Override
    public boolean delete(int monitorId) throws NotFoundException {
        userSubscriptionDao.deleteSubscriptionsForItem(SubscriptionType.PORTER_RESPONSE_MONITOR, monitorId);
        return porterResponseMonitorDao.deleteMonitor(monitorId);
    }

	@Override
	public MonitorEvaluatorStatus toggleEnabled(int monitorId) throws NotFoundException {

		PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);

		MonitorEvaluatorStatus currentStatus = monitor.getEvaluatorStatus();
		MonitorEvaluatorStatus newStatus = MonitorEvaluatorStatus.invert(currentStatus);
		monitor.setEvaluatorStatus(newStatus);

		// update
		porterResponseMonitorDao.save(monitor);
		log.debug("Updated porterResponseMonitor evaluator status: status=" + newStatus + ", porterResponseMonitor=" + monitor);

		return newStatus;
	}
}
