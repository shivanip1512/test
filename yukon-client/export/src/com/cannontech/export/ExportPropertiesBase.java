package com.cannontech.export;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

public class ExportPropertiesBase
{
	private int formatID = -1;
	
	//CSVBILLING PROPERTIES
	private java.util.GregorianCalendar runDate = null;
	private java.util.GregorianCalendar maxTimestamp = null;
	private java.util.GregorianCalendar minTimestamp = null;

	private Character delimiter = new Character('|');
	private boolean showColumnHeadings = false;
	
	//DBPURGE PROPERTIES
	int daysToRetain = 90;
	Integer runTimeHour = null;
	boolean purgeData = false;
	
	/**
	 * Constructor for ExportPropertiesBase.
	 */
	public ExportPropertiesBase()
	{
		super();
	}
	/**
	 * Constructor for ExportPropertiesBase.
	 */
	public ExportPropertiesBase(int formatID)
	{
		super();
		this.formatID = formatID;
	}
	
	/**
	 * Returns the formatID.
	 * @return int
	 */
	public int getFormatID()
	{
		return formatID;
	}

	/**
	 * Sets the formatID.
	 * @param formatID The formatID to set
	 */
	public void setFormatID(int formatID)
	{
		this.formatID = formatID;
	}

	/**
	 * Returns the daysToRetain.
	 * @return int
	 */
	public int getDaysToRetain()
	{
		if (daysToRetain < 0)
		{		
			daysToRetain = 90;
		}
		return daysToRetain;
	}

	/**
	 * Returns the delimiter.
	 * @return Character
	 */
	public Character getDelimiter()
	{
		return delimiter;
	}

	/**
	 * Returns the maxTimestamp.
	 * @return java.util.GregorianCalendar
	 */
	public java.util.GregorianCalendar getMaxTimestamp()
	{
		if( this.maxTimestamp == null)
		{
			maxTimestamp = new java.util.GregorianCalendar();
			java.util.GregorianCalendar today = new java.util.GregorianCalendar();
			today.set(java.util.GregorianCalendar.HOUR_OF_DAY, 0);
			today.set(java.util.GregorianCalendar.MINUTE, 0);
			today.set(java.util.GregorianCalendar.SECOND, 0);
			maxTimestamp.setTime(today.getTime());
		}
		return this.maxTimestamp;
	}

	/**
	 * Returns the minTimestamp.
	 * @return java.util.GregorianCalendar
	 */
	public java.util.GregorianCalendar getMinTimestamp()
	{
		if (this.minTimestamp == null)
		{
			minTimestamp = new java.util.GregorianCalendar();
			java.util.GregorianCalendar yesterday = new java.util.GregorianCalendar();
			yesterday.set(java.util.GregorianCalendar.HOUR_OF_DAY, 0);
			yesterday.set(java.util.GregorianCalendar.MINUTE, 0);
			yesterday.set(java.util.GregorianCalendar.SECOND, 0);
	
			long minTime = yesterday.getTime().getTime() - 86400000;
			yesterday.setTime(new java.util.Date(minTime));
	
			this.minTimestamp.setTime(yesterday.getTime());
		}
		return this.minTimestamp;
	}

	/**
	 * Returns the purgeData.
	 * @return boolean
	 */
	public boolean isPurgeData()
	{
		return purgeData;
	}

	/**
	 * Returns the runDate.
	 * @return java.util.GregorianCalendar
	 */
	public java.util.GregorianCalendar getRunDate()
	{
		return runDate;
	}

	/**
	 * Returns the runTimeHour.
	 * @return int
	 */
	public Integer getRunTimeHour()
	{
		if( runTimeHour == null )
		{
			if( getFormatID() == ExportFormatTypes.IONEVENTLOG_FORMAT)
			{
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				int nowHour = cal.get(java.util.GregorianCalendar.HOUR_OF_DAY);
				setRunTimeHour(nowHour);
			}
			else
			{
				runTimeHour = new Integer(1);
			}
		}
		return runTimeHour;		
	}

	/**
	 * Returns the showColumnHeadings.
	 * @return boolean
	 */
	public boolean isShowColumnHeadings()
	{
		return showColumnHeadings;
	}

	/**
	 * Sets the daysToRetain.
	 * @param daysToRetain The daysToRetain to set
	 */
	public void setDaysToRetain(int daysToRetain)
	{
		this.daysToRetain = daysToRetain;
	}

	/**
	 * Sets the delimiter.
	 * @param delimiter The delimiter to set
	 */
	public void setDelimiter(Character delimiter)
	{
		this.delimiter = delimiter;
	}

	/**
	 * Sets the maxTimestamp.
	 * @param maxTimestamp The maxTimestamp to set
	 */
	public void setMaxTimestamp(java.util.GregorianCalendar maxTimestamp)
	{
		this.maxTimestamp = maxTimestamp;
	}

	/**
	 * Sets the minTimestamp.
	 * @param minTimestamp The minTimestamp to set
	 */
	public void setMinTimestamp(java.util.GregorianCalendar minTimestamp)
	{
		this.minTimestamp = minTimestamp;
	}

	/**
	 * Sets the purgeData.
	 * @param purgeData The purgeData to set
	 */
	public void setPurgeData(boolean purgeData)
	{
		this.purgeData = purgeData;
	}

	/**
	 * Sets the runDate.
	 * @param runDate The runDate to set
	 */
	public void setRunDate(java.util.GregorianCalendar runDate)
	{
		this.runDate = runDate;
	}

	/**
	 * Sets the runTimeHour.
	 * @param runTimeHour The runTimeHour to set
	 */
	public void setRunTimeHour(int runTimeHour)
	{
		this.runTimeHour = new Integer(runTimeHour);
	}

	/**
	 * Sets the showColumnHeadings.
	 * @param showColumnHeadings The showColumnHeadings to set
	 */
	public void setShowColumnHeadings(boolean showColumnHeadings)
	{
		this.showColumnHeadings = showColumnHeadings;
	}

}
