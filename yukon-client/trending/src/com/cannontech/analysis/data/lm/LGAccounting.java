/*
 * Created on Feb 9, 2004
 */
package com.cannontech.analysis.data.lm;


/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LGAccounting
{
	private String paoName = null;
	//date is intuitive from startDate
	private java.util.Date startDate = null;	//startTime
	private java.util.Date stopDate = null;	//stopTime
	private String duration = null;
	private String controlType = null; 
	private String dailyControl = null;
	private String monthlyControl = null;
	private String seasonalControl = null;
	private String annualControl = null;
		
	/**
	 * 
	 */
	public LGAccounting()
	{
		super();
	}

	/**
	 * @param paoName_
	 * @param startDate_
	 * @param stopDate_
	 * @param duration_
	 * @param controlType_
	 * @param daily_
	 * @param monthly_
	 * @param seasonal_
	 * @param annual_
	 */
	public LGAccounting(String paoName_, java.util.Date startDate_, java.util.Date stopDate_,
						Integer duration_, String controlType_, Integer daily_, Integer monthly_,
						Integer seasonal_, Integer annual_)
	{
		paoName = paoName_;
		startDate = startDate_;
		stopDate = stopDate_;
		duration = convertSecondsToTimeString(duration_.intValue());
		controlType = controlType_;
		dailyControl = convertSecondsToTimeString(daily_.intValue());
		monthlyControl = convertSecondsToTimeString(monthly_.intValue());
		seasonalControl = convertSecondsToTimeString(seasonal_.intValue());
		annualControl = convertSecondsToTimeString(annual_.intValue());
	}

	/**
	 * Convert seconds of time into hh:mm:ss string.
	 * @param int seconds
	 * @return String in format hh:mm:ss
	 */
	private String convertSecondsToTimeString(int seconds)
	{
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		int hour = seconds / 3600;
		int temp = seconds % 3600;
		int min = temp / 60;
		int sec = temp % 60; 
			
		return format.format(hour) + ":" + format.format(min) + ":" + format.format(sec);
	}

	/**
	 * @return
	 */
	public String getAnnualControl()
	{
		return annualControl;
	}

	/**
	 * @return
	 */
	public String getControlType()
	{
		return controlType;
	}

	/**
	 * @return
	 */
	public String getDailyControl()
	{
		return dailyControl;
	}

	/**
	 * @return
	 */
	public String getDuration()
	{
		return duration;
	}

	/**
	 * @return
	 */
	public String getMonthlyControl()
	{
		return monthlyControl;
	}

	/**
	 * @return
	 */
	public String getPaoName()
	{
		return paoName;
	}

	/**
	 * @return
	 */
	public String getSeasonalControl()
	{
		return seasonalControl;
	}

	/**
	 * @return
	 */
	public java.util.Date getStartDate()
	{
		return startDate;
	}

	/**
	 * @return
	 */
	public java.util.Date getStopDate()
	{
		return stopDate;
	}

}
