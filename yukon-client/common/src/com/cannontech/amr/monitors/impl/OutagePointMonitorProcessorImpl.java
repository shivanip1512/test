package com.cannontech.amr.monitors.impl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.RichPointValue;
import com.cannontech.core.monitors.PointMonitorProcessor;
import com.cannontech.core.service.SystemDateFormattingService;

public class OutagePointMonitorProcessorImpl implements PointMonitorProcessor {

	public OutageMonitorDao outageMonitorDao;
	public AttributeService attributeService;
	private DeviceGroupService deviceGroupService;
	private RawPointHistoryDao rawPointHistoryDao;
	private OutageMonitorService outageMonitorService;
	private SystemDateFormattingService systemDateFormattingService;
	
	private OutageMonitor outageMonitor;
	
	@Override
	public boolean evaluate(RichPointValue richPointValue) {

	    YukonDevice yukonDevice = new SimpleDevice(richPointValue.getPaoPointIdentifier().getPaoIdentifier());

	    PointIdentifier pointIdentifier = richPointValue.getPaoPointIdentifier().getPointIdentifier();
	    // get the paoPointIdentifier for the attribute 
	    PaoPointIdentifier paoPointIdentifier = attributeService.getPaoPointIdentifierForAttribute(yukonDevice, BuiltInAttribute.BLINK_COUNT);

		// the richPointValue matches the attribute we're looking for
	    if (pointIdentifier.equals(paoPointIdentifier.getPointIdentifier())) {
	        List<PointValueHolder> latestPreviousReadings = getLatestPreviousReading(richPointValue);
	        if( !latestPreviousReadings.isEmpty()) {
	            PointValueHolder latestPreviousReading = latestPreviousReadings.get(0);
	            double thisReading = richPointValue.getPointValueQualityHolder().getValue();
	            double previousReading = latestPreviousReading.getValue();
	            double delta = thisReading - previousReading;
	
	            // if delta is equal to or exceeds our allowed number of outages
	            if (delta >= outageMonitor.getNumberOfOutages()) {
	                return true;
	            }
	        }
		}
		return false;
	}
	

	private List<PointValueHolder> getLatestPreviousReading(RichPointValue richPointValue) {
		Calendar startDate = outageMonitorService.getLatestPreviousReadingDate(outageMonitor);
		Calendar stopDate = systemDateFormattingService.getSystemCalendar();
		
		//use a threshold of time that is up to x times the time period.
		//TODO - possibly look at using a roleProperty or config for setting the 4 part.
		int maxNumberOfDays = getOutageMonitor().getTimePeriodDays() * 4;
		stopDate.add(Calendar.DATE, -maxNumberOfDays);
		return rawPointHistoryDao.getPointData(richPointValue.getPointValueQualityHolder().getId(), startDate.getTime(), stopDate.getTime(), 1);
	}

	
	@Override
	public DeviceGroup getGroupToMonitor() {
	    DeviceGroup result = deviceGroupService.resolveGroupName(outageMonitor.getGroupName());
		return result;
	}

	@Override
	public StoredDeviceGroup getResultsGroup() {
	    //TODO I'd like the follow to be set when this object is constructed
		StoredDeviceGroup result = outageMonitorService.getOutageGroup(outageMonitor.getOutageMonitorName());
		return result;
	}
	
	public OutageMonitor getOutageMonitor() {
		return outageMonitor;
	}

	@Autowired
	public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
		this.rawPointHistoryDao = rawPointHistoryDao;
	}
	
	@Autowired
	public void setOutageMonitorService(
			OutageMonitorService outageMonitorService) {
		this.outageMonitorService = outageMonitorService;
	}
	@Autowired
	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}
	@Autowired
	public void setOutageMonitorDao(OutageMonitorDao outageMonitorDao) {
		this.outageMonitorDao = outageMonitorDao;
	}
	@Autowired
	public void setSystemDateFormattingService(
			SystemDateFormattingService systemDateFormattingService) {
		this.systemDateFormattingService = systemDateFormattingService;
	}
	@Autowired
	public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
		this.deviceGroupService = deviceGroupService;
	}
}
