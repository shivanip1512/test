/*
 * Created on Feb 10, 2004
 */
package com.cannontech.analysis.data.device;

import java.util.Date;

import com.cannontech.amr.meter.model.YukonMeter;



/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MeterAndPointData
{	
    private YukonMeter meter = null;
    private Integer pointID = null;
    private String pointName = null;
	private java.util.Date timeStamp = null;
	private Double value = null;
    private Integer quality = null; 
	
	/**
	 * @param paobjectid_
	 * @param pointid_
	 * @param type_
	 * @param timestamp_
	 * @param value_
	 */
	public MeterAndPointData(YukonMeter meter_, Integer pointID_, String pointName_, java.util.Date timeStamp_, Double value_, Integer quality_) {
	    meter = meter_;
	    pointID = pointID_;
		timeStamp = timeStamp_;
		value = value_;			
        pointName = pointName_;
        quality = quality_;
	}

    public MeterAndPointData(YukonMeter meter_, Integer pointID_, String pointName_, java.util.Date timeStamp_, Double value_) {
        meter = meter_;
        pointID = pointID_;
        pointName = pointName_;
        timeStamp = timeStamp_;
        value = value_;         
    }
    
    public MeterAndPointData(YukonMeter meter, Date ts) {
        this.meter = meter;
        this.timeStamp = ts;
    }
    
    public MeterAndPointData(YukonMeter meter_)  {
        meter = meter_;
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
     * @return Returns the pointID.
     */
    public Integer getPointID()
    {
        return pointID;
    }

    public YukonMeter getMeter() {
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
