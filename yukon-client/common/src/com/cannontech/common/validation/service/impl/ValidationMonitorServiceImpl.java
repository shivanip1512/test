package com.cannontech.common.validation.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.common.validation.service.ValidationMonitorService;

public class ValidationMonitorServiceImpl implements ValidationMonitorService {
	
	private ValidationMonitorDao validationMonitorDao;
	private Logger log = YukonLogManager.getLogger(ValidationMonitorServiceImpl.class);

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
		log.debug("Updated validationMonitor evaluator status: status=" + newEvaluatorStatus + ", validationMonitor=" + validationMonitor.toString());
		
		return newEvaluatorStatus;
	}
	
	@Autowired
	public void setValidationMonitorDao(ValidationMonitorDao validationMonitorDao) {
		this.validationMonitorDao = validationMonitorDao;
	}
}
