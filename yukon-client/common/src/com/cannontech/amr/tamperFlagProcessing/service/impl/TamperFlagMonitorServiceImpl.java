package com.cannontech.amr.tamperFlagProcessing.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;

public class TamperFlagMonitorServiceImpl implements TamperFlagMonitorService {

    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private TamperFlagMonitorDao tamperFlagMonitorDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    private Logger log = YukonLogManager.getLogger(TamperFlagMonitorServiceImpl.class);

    @Override
    public StoredDeviceGroup getTamperFlagGroup(String name) {

        StoredDeviceGroup tamperFlagGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.TAMPER_FLAG, name, true);
        return tamperFlagGroup;
    }
	
	@Override
	public boolean deleteTamperFlagMonitor(int tamperFlagMonitorId) throws TamperFlagMonitorNotFoundException {

		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
        
        // delete tamper flag group
        try {
			StoredDeviceGroup tamperFlagGroup = getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
			deviceGroupEditorDao.removeGroup(tamperFlagGroup);
		} catch (NotFoundException e) {
			// may have been deleted? who cares
		}

        userSubscriptionDao.deleteSubscriptionsForItem(SubscriptionType.TAMPER_FLAG_MONITOR, tamperFlagMonitorId);
        // delete monitor
        return tamperFlagMonitorDao.delete(tamperFlagMonitorId);
	}
	
	@Override
	public MonitorEvaluatorStatus toggleEnabled(int tamperFlagMonitorId) throws TamperFlagMonitorNotFoundException {
		
		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
        
		// set status
        MonitorEvaluatorStatus newEvaluatorStatus;
        if (tamperFlagMonitor.getEvaluatorStatus().equals(MonitorEvaluatorStatus.DISABLED)) {
        	newEvaluatorStatus = MonitorEvaluatorStatus.ENABLED;
        } else {
        	newEvaluatorStatus = MonitorEvaluatorStatus.DISABLED;
        }
        tamperFlagMonitor.setEvaluatorStatus(newEvaluatorStatus);
        
        // update
        tamperFlagMonitorDao.saveOrUpdate(tamperFlagMonitor);
		log.debug("Updated tamperFlagMonitor evaluator status: status=" + newEvaluatorStatus + ", tamperFlagMonitor=" + tamperFlagMonitor.toString());
		
		return newEvaluatorStatus;
	}
}
