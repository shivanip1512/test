package com.cannontech.amr.deviceread.dao;

import java.util.Date;

import com.cannontech.amr.deviceread.CalculatedPointResults;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.database.data.lite.LiteYukonUser;


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
	public CalculatedPointResults calculatePoint(Meter meter, Date beginDate, LiteYukonUser liteYukonUser);
}
