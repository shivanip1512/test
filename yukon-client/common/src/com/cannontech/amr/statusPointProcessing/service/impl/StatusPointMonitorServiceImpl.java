package com.cannontech.amr.statusPointProcessing.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointProcessing.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitor;
import com.cannontech.amr.statusPointProcessing.service.StatusPointMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.StatusPointMonitorNotFoundException;

public class StatusPointMonitorServiceImpl implements StatusPointMonitorService {

	private StatusPointMonitorDao statusPointMonitorDao;
	private Logger log = YukonLogManager.getLogger(StatusPointMonitorServiceImpl.class);
	
	@Override
	public boolean delete(int statusPointMonitorId) throws StatusPointMonitorNotFoundException {
        return statusPointMonitorDao.deleteStatusPointMonitor(statusPointMonitorId);
	}
	
	@Override
	public MonitorEvaluatorStatus toggleEnabled(int statusPointMonitorId) throws StatusPointMonitorNotFoundException {
		
		StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
        
		// set status
        MonitorEvaluatorStatus newEvaluatorStatus;
        if (statusPointMonitor.getEvaluatorStatus().equals(MonitorEvaluatorStatus.DISABLED)) {
        	newEvaluatorStatus = MonitorEvaluatorStatus.ENABLED;
        } else {
        	newEvaluatorStatus = MonitorEvaluatorStatus.DISABLED;
        }
        statusPointMonitor.setEvaluatorStatus(newEvaluatorStatus);
        
        // update
        statusPointMonitorDao.save(statusPointMonitor);
		log.debug("Updated statusPointMonitor evaluator status: status=" + newEvaluatorStatus + ", statusPointMonitor=" + statusPointMonitor.toString());
		
		return newEvaluatorStatus;
	}
	
	@Autowired
	public void setStatusPointMonitorDao(StatusPointMonitorDao statusPointMonitorDao) {
		this.statusPointMonitorDao = statusPointMonitorDao;
	}
}
