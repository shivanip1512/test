/*
 * Created on Feb 10, 2004
 */
package com.cannontech.analysis.data.device;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;



/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MeterAndPointData
{	
//    private MeterData meterData = null;
    private Meter meter = null;
    private Integer pointID = null;
    private String pointName = null;
	private java.util.Date timeStamp = null;
	private Double value = null;
    private Integer quality = null; 
	
	/**
	 * 
	 */
	public MeterAndPointData()
	{
		super();
	}

	/**
	 * @param paobjectid_
	 * @param pointid_
	 * @param type_
	 * @param timestamp_
	 * @param value_
	 */
	public MeterAndPointData(Meter meter_, Integer pointID_, String pointName_, java.util.Date timeStamp_, Double value_, Integer quality_)
	{
	    meter = meter_;
	    pointID = pointID_;
		timeStamp = timeStamp_;
		value = value_;			
        pointName = pointName_;
        quality = quality_;
	}

    public MeterAndPointData(Meter meter_, Integer pointID_, String pointName_, java.util.Date timeStamp_, Double value_)
    {
        meter = meter_;
        pointID = pointID_;
        pointName = pointName_;
        timeStamp = timeStamp_;
        value = value_;         
    }
    
	/**
	 * @return
	 */
	public java.util.Date getTimeStamp()
	{
		return timeStamp;
	}

	/**
	 * @return
	 */
	public Double getValue()
	{
		return value;
	}
	
	/**
	 * @return Returns the meterData.
	 */
//	public MeterData getMeterData()
//	{
//	    return meterData;
//	}
	
    /**
     * @return Returns the pointID.
     */
    public Integer getPointID()
    {
        return pointID;
    }

    public Meter getMeter() {
        return meter;
    }
    
    /**
     * @return Returns the pointName.
     */
    public String getPointName()
    {
        return pointName;
    }

    /**
     * @return Returns the quality.
     */
    public Integer getQuality()
    {
        return quality;
    }
}
