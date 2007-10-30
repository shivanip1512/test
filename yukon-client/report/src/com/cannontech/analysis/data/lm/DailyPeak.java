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
    private String controlAreaName = null;
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

	public DailyPeak(String controlAreaName, Integer controlAreaID, Double peakValue, Integer peakDataQuality,
					GregorianCalendar peakTimestamp, Double offPeakValue,
					Integer offPeakDataQuality, GregorianCalendar offPeakTimestamp,
					Double threshold, Integer rank)
	{
		super();
        this.controlAreaName = controlAreaName;
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
	
    public String getControlAreaName() {
        return controlAreaName;
    }
    
	public Integer getControlAreaID() {
		return controlAreaID;
	}

		public Integer getOffPeakDataQuality(){
		return offPeakDataQuality;
	}

	public GregorianCalendar getOffPeakTimestamp()	{
		return offPeakTimestamp;
	}

	public Double getOffPeakValue() {
		return offPeakValue;
	}

	public Integer getPeakDataQuality() {
		return peakDataQuality;
	}

	public GregorianCalendar getPeakTimestamp() {
		return peakTimestamp;
	}

	public Double getPeakValue() {
		return peakValue;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setControlAreaID(Integer caID) {
		controlAreaID = caID;
	}

	public void setOffPeakDataQuality(Integer offPeakDataQual) {
		offPeakDataQuality = offPeakDataQual;
	}

	public void setOffPeakTimestamp(GregorianCalendar offPeakTS) {
		offPeakTimestamp = offPeakTS;
	}

	public void setOffPeakValue(Double offPeakVal) {
		offPeakValue = offPeakVal;
	}

	public void setPeakDataQuality(Integer peakDataQual) {
		peakDataQuality = peakDataQual;
	}

	public void setPeakTimestamp(GregorianCalendar peakTS) {
		peakTimestamp = peakTS;
	}

	public void setPeakValue(Double peakVal) {
		peakValue = peakVal;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer integer) {
		rank = integer;
	}
}
