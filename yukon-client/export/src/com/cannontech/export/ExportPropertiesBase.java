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
	private int formatType = -1;
	
	//CSVBILLING PROPERTIES
	private java.util.GregorianCalendar runDate = null;
	private java.util.GregorianCalendar maxTimestamp = null;
	private java.util.GregorianCalendar minTimestamp = null;

	private Character delimiter = new Character('|');
	private boolean showColumnHeadings = false;
	private String filePrefix = "OfferBill";
	private String fileExtension = ".csv";
	private String energyFileName = "C:/yukon/client/config/EnergyNumbers.txt";
		
	//DBPURGE PROPERTIES
	int daysToRetain = 90;
	int runTimeHour = 1;
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
	public ExportPropertiesBase(int formatType)
	{
		super();
		this.formatType = formatType;
	}
	
	/**
	 * Returns the formatType.
	 * @return int
	 */
	public int getFormatType()
	{
		return formatType;
	}

	/**
	 * Sets the formatType.
	 * @param formatType The formatType to set
	 */
	public void setFormatType(int formatType)
	{
		this.formatType = formatType;
	}

	/**
	 * Returns the daysToRetain.
	 * @return int
	 */
	public int getDaysToRetain()
	{
		if (daysToRetain < 0)
		{		
			try
			{
				java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
				daysToRetain = (new Integer(bundle.getString("dbpurge_days_to_retain"))).intValue();
				com.cannontech.clientutils.CTILogger.info("  (config.prop)  Days To Retain in database = " + daysToRetain);
			}
			catch( Exception e)
			{
				daysToRetain = 90;
				com.cannontech.clientutils.CTILogger.error("  Days to retain was NOT found, DEFAULTED TO " + daysToRetain);
				com.cannontech.clientutils.CTILogger.info("  Add 'dbpurge_days_to_retain' to config.properties.");
			}
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
	 * Returns the energyFileName.
	 * @return String
	 */
	public String getEnergyFileName()
	{
		return energyFileName;
	}

	/**
	 * Returns the fileExtension.
	 * @return String
	 */
	public String getFileExtension()
	{
		return fileExtension;
	}

	/**
	 * Returns the filePrefix.
	 * @return String
	 */
	public String getFilePrefix()
	{
		return filePrefix;
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
		System.out.println(" stop DATE = " + this.maxTimestamp.getTime());
		
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
		System.out.println(" start DATE = " + this.minTimestamp.getTime());			
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
	public int getRunTimeHour()
	{
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
	 * Sets the energyFileName.
	 * @param energyFileName The energyFileName to set
	 */
	public void setEnergyFileName(String energyFileName)
	{
		this.energyFileName = energyFileName;
	}

	/**
	 * Sets the fileExtension.
	 * @param fileExtension The fileExtension to set
	 */
	public void setFileExtension(String fileExtension)
	{
		this.fileExtension = fileExtension;
	}

	/**
	 * Sets the filePrefix.
	 * @param filePrefix The filePrefix to set
	 */
	public void setFilePrefix(String filePrefix)
	{
		this.filePrefix = filePrefix;
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
		this.runTimeHour = runTimeHour;
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
