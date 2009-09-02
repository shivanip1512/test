package com.cannontech.amr.monitors.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.core.dynamic.impl.RichPointValue;
import com.cannontech.core.monitors.PointMonitorProcessor;

public class GeneralAlarmPointMonitorProcessorImpl implements PointMonitorProcessor {

	public AttributeService attributeService;
	private DeviceGroupService deviceGroupService;
	private TamperFlagMonitorService tamperFlagMonitorService;
	
	private TamperFlagMonitor tamperFlagMonitor;
	
	@Override
	public boolean evaluate(RichPointValue richPointValue) {

	    YukonDevice yukonDevice = new SimpleDevice(richPointValue.getPaoPointIdentifier().getPaoIdentifier());

	    PointIdentifier pointIdentifier = richPointValue.getPaoPointIdentifier().getPointIdentifier();
	    // get the paoPointIdentifier for the attribute 
	    PaoPointIdentifier paoPointIdentifier = attributeService.getPaoPointIdentifierForAttribute(yukonDevice, BuiltInAttribute.GENERAL_ALARM_FLAG);

		// the richPointValue matches the attribute we're looking for
	    if (pointIdentifier.equals(paoPointIdentifier.getPointIdentifier())) {
	    	
	    	// if the value is 1 (TRUE), flag was set
	    	if ( richPointValue.getPointValueQualityHolder().getValue() == 1) {
	    		return true;
	        }
		}
		return false;
	}
	
	@Override
	public DeviceGroup getGroupToMonitor() {
	    DeviceGroup result = deviceGroupService.resolveGroupName(tamperFlagMonitor.getGroupName());
		return result;
	}

	@Override
	public StoredDeviceGroup getResultsGroup() {
	    //TODO I'd like the follow to be set when this object is constructed
		StoredDeviceGroup result = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
		return result;
	}
	
	public TamperFlagMonitor getTamperFlagMonitor() {
		return tamperFlagMonitor;
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
}
