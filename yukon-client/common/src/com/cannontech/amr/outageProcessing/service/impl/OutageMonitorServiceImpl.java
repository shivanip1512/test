package com.cannontech.amr.outageProcessing.service.impl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;

public class OutageMonitorServiceImpl implements OutageMonitorService {

    @Autowired private OutageMonitorDao outageMonitorDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
	private Logger log = YukonLogManager.getLogger(OutageMonitorServiceImpl.class);
	
	@Override
	public StoredDeviceGroup getOutageGroup(String name) {
	    
        StoredDeviceGroup outageGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.OUTAGE, name, true);
		return outageGroup;
	}
	
	@Override
	public boolean deleteOutageMonitor(int outageMonitorId) throws OutageMonitorNotFoundException {
		
        OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
        
        // delete outage group
        try {
			StoredDeviceGroup outageGroup = getOutageGroup(outageMonitor.getOutageMonitorName());
			deviceGroupEditorDao.removeGroup(outageGroup, false);
		} catch (NotFoundException e) {
			// may have been deleted? who cares
		}
        
        userSubscriptionDao.deleteSubscriptionsForItem(SubscriptionType.OUTAGE_MONITOR, outageMonitorId);
        // delete processor
        return outageMonitorDao.delete(outageMonitorId);
	}
	
	@Override
	public Date getLatestPreviousReadingDate(OutageMonitor outageMonitor) {
	    Duration outagePeriod = Duration.standardDays(outageMonitor.getTimePeriodDays());
	    Instant now = new Instant();
		return now.minus(outagePeriod).toDate();
	}
	
	@Override
	public MonitorEvaluatorStatus toggleEnabled(int outageMonitorId) throws OutageMonitorNotFoundException {
		
		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
        
		// set status
        MonitorEvaluatorStatus newEvaluatorStatus;
        if (outageMonitor.getEvaluatorStatus().equals(MonitorEvaluatorStatus.DISABLED)) {
        	newEvaluatorStatus = MonitorEvaluatorStatus.ENABLED;
        } else {
        	newEvaluatorStatus = MonitorEvaluatorStatus.DISABLED;
        }
        outageMonitor.setEvaluatorStatus(newEvaluatorStatus);
        
        // update
		outageMonitorDao.saveOrUpdate(outageMonitor);
		log.debug("Updated outageMonitor evaluator status: status=" + newEvaluatorStatus + ", outageMonitor=" + outageMonitor.toString());
		
		return newEvaluatorStatus;
	}
}
