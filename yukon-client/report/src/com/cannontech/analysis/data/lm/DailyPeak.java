/*
 * Created on Feb 10, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.data.lm;

import java.util.GregorianCalendar;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DailyPeak
{
	private Integer controlAreaID = null;
	private Double peakValue = null;
	private Integer peakDataQuality = null;
	private java.util.GregorianCalendar peakTimestamp = null;
	private Double offPeakValue = null;
	private Integer offPeakDataQuality = null;
	private java.util.GregorianCalendar offPeakTimestamp = null;
	private Double threshold = null;
	private Integer rank = null;
	

	public DailyPeak()
	{
		super();
	}

	public DailyPeak(Integer controlAreaID, Double peakValue, Integer peakDataQuality,
					GregorianCalendar peakTimestamp, Double offPeakValue,
					Integer offPeakDataQuality, GregorianCalendar offPeakTimestamp,
					Double threshold, Integer rank)
	{
		super();
		this.controlAreaID= controlAreaID;
		this.peakValue = peakValue;
		this.peakDataQuality = peakDataQuality;
		this.peakTimestamp = peakTimestamp;
		this.offPeakValue = offPeakValue;
		this.offPeakDataQuality = offPeakDataQuality;
		this.offPeakTimestamp = offPeakTimestamp;
		this.threshold = threshold;
		this.rank = rank;
	}
	
	/**
	 * @return
	 */
	public Integer getControlAreaID()
	{
		return controlAreaID;
	}

	/**
	 * @return
	 */
	public Integer getOffPeakDataQuality()
	{
		return offPeakDataQuality;
	}

	/**
	 * @return
	 */
	public GregorianCalendar getOffPeakTimestamp()
	{
		return offPeakTimestamp;
	}

	/**
	 * @return
	 */
	public Double getOffPeakValue()
	{
		return offPeakValue;
	}

	/**
	 * @return
	 */
	public Integer getPeakDataQuality()
	{
		return peakDataQuality;
	}

	/**
	 * @return
	 */
	public GregorianCalendar getPeakTimestamp()
	{
		return peakTimestamp;
	}

	/**
	 * @return
	 */
	public Double getPeakValue()
	{
		return peakValue;
	}

	/**
	 * @return
	 */
	public Double getThreshold()
	{
		return threshold;
	}

	/**
	 * @param integer
	 */
	public void setControlAreaID(Integer caID)
	{
		controlAreaID = caID;
	}

	/**
	 * @param integer
	 */
	public void setOffPeakDataQuality(Integer offPeakDataQual)
	{
		offPeakDataQuality = offPeakDataQual;
	}

	/**
	 * @param calendar
	 */
	public void setOffPeakTimestamp(GregorianCalendar offPeakTS)
	{
		offPeakTimestamp = offPeakTS;
	}

	/**
	 * @param double1
	 */
	public void setOffPeakValue(Double offPeakVal)
	{
		offPeakValue = offPeakVal;
	}

	/**
	 * @param integer
	 */
	public void setPeakDataQuality(Integer peakDataQual)
	{
		peakDataQuality = peakDataQual;
	}

	/**
	 * @param calendar
	 */
	public void setPeakTimestamp(GregorianCalendar peakTS)
	{
		peakTimestamp = peakTS;
	}

	/**
	 * @param double1
	 */
	public void setPeakValue(Double peakVal)
	{
		peakValue = peakVal;
	}

	/**
	 * @param double1
	 */
	public void setThreshold(Double threshold)
	{
		this.threshold = threshold;
	}

	/**
	 * @return
	 */
	public Integer getRank()
	{
		return rank;
	}

	/**
	 * @param integer
	 */
	public void setRank(Integer integer)
	{
		rank = integer;
	}

}
