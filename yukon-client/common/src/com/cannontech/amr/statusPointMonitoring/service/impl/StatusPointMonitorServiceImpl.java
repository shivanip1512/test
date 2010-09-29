package com.cannontech.amr.statusPointMonitoring.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.statusPointMonitoring.service.StatusPointMonitorService;
import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;

public class StatusPointMonitorServiceImpl implements StatusPointMonitorService {

	private StatusPointMonitorDao statusPointMonitorDao;
	private Logger log = YukonLogManager.getLogger(StatusPointMonitorServiceImpl.class);
	
	@Override
	public boolean delete(int statusPointMonitorId) throws NotFoundException {
        return statusPointMonitorDao.deleteStatusPointMonitor(statusPointMonitorId);
	}
	
	@Override
	public MonitorEvaluatorStatus toggleEnabled(int statusPointMonitorId) throws NotFoundException {
		
		StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
		
        MonitorEvaluatorStatus currentStatus = statusPointMonitor.getEvaluatorStatus();
        MonitorEvaluatorStatus newStatus = MonitorEvaluatorStatus.invert(currentStatus);
        statusPointMonitor.setEvaluatorStatus(newStatus);
        
        // update
        statusPointMonitorDao.save(statusPointMonitor);
		log.debug("Updated statusPointMonitor evaluator status: status=" + newStatus + ", statusPointMonitor=" + statusPointMonitor);
		
		return newStatus;
	}
	
	@Autowired
	public void setStatusPointMonitorDao(StatusPointMonitorDao statusPointMonitorDao) {
		this.statusPointMonitorDao = statusPointMonitorDao;
	}
}
