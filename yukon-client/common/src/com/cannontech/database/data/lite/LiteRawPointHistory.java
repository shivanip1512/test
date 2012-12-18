package com.cannontech.database.data.lite;

import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;

/*
 * This class exists so Objects (specifically Vectors) of RawPointHistory do not need to be so heavy.
 * Reporting and trending can handle more data with the Lite object compared to the heavy object,
 *  avoiding OutOfMemory errors...or at least prolonging them!
 */
public class LiteRawPointHistory
{
	private long changeID = 0;
	private int pointID = 0;
	private long timeStamp = CtiUtilities.get1990GregCalendar().getTimeInMillis();
	private int quality = PointQuality.Invalid.getQuality();
	private double value = 0.0;

	/**
	 * LitePoint
	 */
	public LiteRawPointHistory( int pntID ) 
	{
		setPointID(pntID);
	}
	/**
	 * LiteRawPointHistory
	 */
	public LiteRawPointHistory( long changeID_, int pointID_, long timestamp_, int quality_, double value_) 
	{
		setPointID(pointID_);
		setTimeStamp(timestamp_);
		setQuality(quality_);
		setValue(value_);
	}
	
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String toString()
	{
		return DaoFactory.getPointDao().getPointName(getPointID()) + ":" + getTimeStamp() + ":" + getValue();
	}

	/**
	 * @return
	 */
	public long getChangeID()
	{
		return changeID;
	}

	/**
	 * @return
	 */
	public int getPointID()
	{
		return pointID;
	}

	/**
	 * @return
	 */
	public int getQuality()
	{
		return quality;
	}

	/**
	 * @return
	 */
	public long getTimeStamp()
	{
		return timeStamp;
	}

	/**
	 * @return
	 */
	public double getValue()
	{
		return value;
	}

	/**
	 * @param i
	 */
	public void setChangeID(long changeID)
	{
		this.changeID = changeID;
	}

	/**
	 * @param i
	 */
	public void setPointID(int i)
	{
		pointID = i;
	}

	/**
	 * @param i
	 */
	public void setQuality(int i)
	{
		quality = i;
	}

	/**
	 * @param l
	 */
	public void setTimeStamp(long l)
	{
		timeStamp = l;
	}

	/**
	 * @param d
	 */
	public void setValue(double d)
	{
		value = d;
	}

}
