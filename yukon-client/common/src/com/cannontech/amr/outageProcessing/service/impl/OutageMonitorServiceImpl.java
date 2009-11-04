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
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;

public class OutageMonitorServiceImpl implements OutageMonitorService {

	private OutageMonitorDao outageMonitorDao;
	private DeviceGroupEditorDao deviceGroupEditorDao;
	private Logger log = YukonLogManager.getLogger(OutageMonitorServiceImpl.class);
	
	@Override
	public StoredDeviceGroup getOutageGroup(String name) {
		
		String outageGroupName = SystemGroupEnum.OUTAGE_PROCESSING.getFullPath() + name;
		StoredDeviceGroup outageGroup = deviceGroupEditorDao.getStoredGroup(outageGroupName, true);
		
		return outageGroup;
	}
	
	@Override
	public boolean deleteOutageMonitor(int outageMonitorId) throws OutageMonitorNotFoundException {
		
        OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
        
        // delete outage group
        try {
			StoredDeviceGroup outageGroup = getOutageGroup(outageMonitor.getOutageMonitorName());
			deviceGroupEditorDao.removeGroup(outageGroup);
		} catch (NotFoundException e) {
			// may have been deleted? who cares
		}
        
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
	
	@Autowired
	public void setOutageMonitorDao(OutageMonitorDao outageMonitorDao) {
		this.outageMonitorDao = outageMonitorDao;
	}
	
	@Autowired
	public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
		this.deviceGroupEditorDao = deviceGroupEditorDao;
	}
}
