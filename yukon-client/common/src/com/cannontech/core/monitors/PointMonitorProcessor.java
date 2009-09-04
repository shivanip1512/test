package com.cannontech.core.monitors;

import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dynamic.RichPointData;

public interface PointMonitorProcessor {

	/**
	 * Evaluates the pointValue for the processor.
	 * @param richPointValue
	 * @return true if the pointValue matches the qualifications of the processor, else false.
	 */
	public boolean evaluate(RichPointData richPointValue);

	/**
	 * Returns the group this processor is monitoring
	 * @return
	 */
	public DeviceGroup getGroupToMonitor();
	
	/**
	 * Returns the group this processors puts 
	 * @return
	 */
	public StoredDeviceGroup getResultsGroup();
}
