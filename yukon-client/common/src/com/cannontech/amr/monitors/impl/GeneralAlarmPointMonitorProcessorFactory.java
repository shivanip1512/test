package com.cannontech.amr.monitors.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.core.monitors.PointMonitorListenerFactory;
import com.cannontech.core.monitors.PointMonitorProcessor;

public class GeneralAlarmPointMonitorProcessorFactory extends MonitorProcessorFactoryBase<TamperFlagMonitor> {

	public AttributeService attributeService;
	private DeviceGroupService deviceGroupService;
	private TamperFlagMonitorService tamperFlagMonitorService;
	private TamperFlagMonitorDao tamperFlagMonitorDao;
	private PointMonitorListenerFactory pointMonitorListenerFactory;
	
	@Override
	protected List<TamperFlagMonitor> getAllMonitors() {
	    return tamperFlagMonitorDao.getAll();
	}

	protected RichPointDataListener createPointListener(final TamperFlagMonitor tamperFlagMonitor) {
	    final DeviceGroup monitorGroup = deviceGroupService.findGroupName(tamperFlagMonitor.getGroupName());
	    final StoredDeviceGroup resultGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
	    PointMonitorProcessor monitorProcessor = new PointMonitorProcessor() {
	
	        @Override
	        public boolean evaluate(RichPointData richPointData) {

	            // the richPointData matches the attribute we're looking for
	            if (attributeService.isPointAttribute(richPointData.getPaoPointIdentifier(), BuiltInAttribute.GENERAL_ALARM_FLAG)) {

	                // if the value is 1 (TRUE), flag was set
	                if ( richPointData.getPointValue().getValue() == 1) {
	                    return true;
	                }
	            }
	            return false;
	        }

	        @Override
	        public DeviceGroup getGroupToMonitor() {
	            return monitorGroup;
	        }

	        @Override
	        public StoredDeviceGroup getResultsGroup() {
	            return resultGroup;
	        }

	    };
	    
	    RichPointDataListener pointDataListener = pointMonitorListenerFactory.createListener(monitorProcessor);
	    return pointDataListener;
	}
	
	@Autowired
	public void setTamperFlagMonitorDao(TamperFlagMonitorDao tamperFlagMonitorDao) {
        this.tamperFlagMonitorDao = tamperFlagMonitorDao;
    }

	@Autowired
	public void setTamperFlagMonitorService(
			TamperFlagMonitorService tamperFlagMonitorService) {
		this.tamperFlagMonitorService = tamperFlagMonitorService;
	}

	@Autowired
	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}
	
	@Autowired
	public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
		this.deviceGroupService = deviceGroupService;
	}
	
	@Autowired
	public void setPointMonitorListenerFactory(
            PointMonitorListenerFactory pointMonitorListenerFactory) {
        this.pointMonitorListenerFactory = pointMonitorListenerFactory;
    }
}
