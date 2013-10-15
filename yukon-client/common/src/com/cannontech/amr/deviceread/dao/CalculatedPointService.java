package com.cannontech.amr.deviceread.dao;

import java.util.Date;

import com.cannontech.amr.deviceread.CalculatedPointResults;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.user.YukonUserContext;


/**
 * @author mkruse
 *
 */
public interface CalculatedPointService {
	
	/**
	 * This calculates a point in the past from the current usage. 
	 * 
	 * @param device
	 * @param beginDate
	 */
	public CalculatedPointResults calculatePoint(PlcMeter meter, Date beginDate, DeviceRequestType type, YukonUserContext userContext);
}
