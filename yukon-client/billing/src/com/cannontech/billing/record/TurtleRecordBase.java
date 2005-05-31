package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class TurtleRecordBase implements BillingRecordBase 
{
	private String recordIndicator = "M";	//1
	private String meterNumber = null;	//10
	private Double readingKWH = null;	//5
	private String time = null;	//HH:mm
	private String date = null;		//yy/MM/dd

	private Double readingKW = null;	//7 (modifiable)
	private String timeKW = null;		//HH:mm
	private String dateKW = null;		//yy/MM/dd

	private String stat = null;	//null or bb6(always)
	
	
	protected static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yy/MM/dd");
	protected static java.text.SimpleDateFormat TIME_FORMAT = new java.text.SimpleDateFormat("HH:mm");
	//***THIS FORMAT TRUNCATES THE VALUE (FROM THE LEFT) TO MAX 5 NUMBERS SO MOST SIGNIFICANT DIGITS MAY BE LOST!!!
	protected static java.text.DecimalFormat KWH_FORMAT_NODECIMAL = new java.text.DecimalFormat("#####");
	protected static java.text.DecimalFormat KW_FORMAT_3v3 = new java.text.DecimalFormat("##0.000");
	protected static int KW_FIELD_SIZE = 7;

	/**
	 * BaseRecord constructor comment.
	 */
	public TurtleRecordBase() {
		super();
		KWH_FORMAT_NODECIMAL.setMaximumIntegerDigits(5);
	}
	/**
	 * BaseRecord constructor comment.
	 */
	public TurtleRecordBase(String newMeterNumber)
	{
		super();
		setMeterNumber(newMeterNumber);
		KWH_FORMAT_NODECIMAL.setMaximumIntegerDigits(5);
	}
	/**
	 * BaseRecord constructor comment.
	 */
	public TurtleRecordBase(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super();
		setMeterNumber(newMeterNumber);
		setReadingKWH(reading);
		setDate(newTimestamp);
		setTime(newTimestamp);
		KWH_FORMAT_NODECIMAL.setMaximumIntegerDigits(5);
	}
	/**
	 * SEDCRecord constructor comment.
	 */
	public TurtleRecordBase(String newMeterNumber, java.sql.Timestamp newTimestamp)
	{
		super();
		setMeterNumber(newMeterNumber);
		setDate(newTimestamp);
		setTime(newTimestamp);
	}
	/**
	 * Converts data in a SEDCFormat to a formatted StringBuffer for stream use.
	 * Creation date: (5/24/00 10:58:48 AM)
	 *@return java.lang.String
	*/
	 //r,mmmmmmmmmm,rrrrr,tt:tt,dd/dd/dd
	 //r - 1 meterReading/header record
	 //m - 10 meterNumber Left Justified 
	 //r - 5 reading
	 //t - 5 time (includes :)
	 //d - 8 date (includes /s)
	
	public String dataToString()
	{
		StringBuffer writeToFile = new StringBuffer();
	
		writeToFile.append( getRecordIndicator() + ",");
	
		writeToFile.append( getMeterNumber());
		for ( int i = 0; i < (10 - getMeterNumber().length()); i++)
		{
			writeToFile.append(" ");
		}
		writeToFile.append(",");
		
		if( getReadingKWH() == null)
			writeToFile.append("     ,");
		else
		{
			for ( int i = 0; i < (5 - KWH_FORMAT_NODECIMAL.format(getReadingKWH()).length());i++)
			{
				writeToFile.append(" ");
			}
			writeToFile.append(KWH_FORMAT_NODECIMAL.format(getReadingKWH()) + ",");
		}
		
		writeToFile.append(getTime() + ",");
	
		writeToFile.append(getDate() + ",");
	
		if(this instanceof SEDC54Record)
		{
			writeToFile.append("\r\n");
			return writeToFile.toString();
		}
	
		//Peak
		if( getReadingKW() == null)
		{
			for (int i = 0; i < getKwFieldSize(); i++)
				writeToFile.append(" ");
			
			writeToFile.append(",");
		}
		else
		{
			for ( int i = 0; i < (getKwFieldSize() - getKwFormat().format(getReadingKW()).length());i++)
			{
				writeToFile.append(" ");
			}
	
			writeToFile.append(getKwFormat().format(getReadingKW()) + ",");
		}
			
		//PeakT
		writeToFile.append(getTimeKW() + ',');
		//PeakD
		writeToFile.append(getDateKW());
			
		if( this instanceof NCDCRecord || this instanceof NISC_NCDCRecord)
		{		
			writeToFile.append("\r\n");
			return writeToFile.toString();
		}
		writeToFile.append(',');
		
		//Stat
		writeToFile.append(getStat().toString() + ',');
		
		//Sig
		writeToFile.append(',');
		
		//Freq
		writeToFile.append(';');
	
		//Phase
		writeToFile.append("\r\n");
		
		return writeToFile.toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return boolean
	 * @param o java.lang.Object
	 */
	public boolean equals(Object o) 
	{
		return ( (o != null) &&
				   (o instanceof TurtleRecordBase) &&
				   ((TurtleRecordBase)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/4/2002 1:34:53 PM)
	 * @return java.lang.String
	 */
	public String getDate()
	{
		if( date == null)
			return "";

		return date;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/4/2002 1:34:53 PM)
	 * @return java.lang.String
	 */
	public String getDateKW()
	{
		if( dateKW == null)
			return "";
			
		return dateKW;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 5:33:51 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getMeterNumber() {
		return meterNumber;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/27/2001 3:16:28 PM)
	 * @return java.lang.String
	 */
	public Double getReadingKW()
	{
		return readingKW;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/27/2001 3:16:28 PM)
	 * @return java.lang.String
	 */
	public Double getReadingKWH()
	{
		return readingKWH;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/18/2002 10:49:23 AM)
	 * @return java.lang.String
	 */
	public String getRecordIndicator()
	{
		if (recordIndicator == null)
			return "M";
		return recordIndicator;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/4/2002 1:34:53 PM)
	 * @return java.lang.String
	 */
	public String getTime()
	{
		if(time == null)
			return "";
			
		return time;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/4/2002 1:34:53 PM)
	 * @return java.lang.String
	 */
	public String getTimeKW()
	{
		if( timeKW == null)
			return "";

		return timeKW;
	}

	/**
	 * Returns the KW reading field size.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return int
	 */
	public int getKwFieldSize()
	{
		return KW_FIELD_SIZE;
	}

	/**
	 * Returns the KW reading field size.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return int
	 */
	public String getStat()
	{
		if( stat == null)
			return "";
			
		return stat;
	}
	
	/**
	 * Returns the KW reading format.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return DecimalFormat
	 */
	public java.text.DecimalFormat getKwFormat()
	{
		return KW_FORMAT_3v3;
	}

	/**
	 * Returns the Date (timestamp) format.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return DecimalFormat
	 */
	public java.text.SimpleDateFormat getDateFormat()
	{
		return DATE_FORMAT;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/4/2002 4:02:16 PM)
	 * @param newDate java.lang.String
	 */
	public void setDate(java.sql.Timestamp timeStamp)
	{
		java.util.Date d = new java.util.Date(timeStamp.getTime());
		date = getDateFormat().format(d);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/4/2002 4:02:16 PM)
	 * @param newDate java.lang.String
	 */
	public void setDateKW(java.sql.Timestamp timeStamp)
	{
		java.util.Date d = new java.util.Date(timeStamp.getTime());
		dateKW = getDateFormat().format(d);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 5:33:51 PM)
	 * @param newMeterNumber java.lang.String
	 */
	public void setMeterNumber(java.lang.String newMeterNumber) {
		meterNumber = newMeterNumber;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/27/2001 3:16:28 PM)
	 * @param newReadingKWH java.lang.String
	 */
	public void setReadingKW(double newReadingKW)
	{
		readingKW = new Double(newReadingKW);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/27/2001 3:16:28 PM)
	 * @param newReadingKWH java.lang.String
	 */
	public void setReadingKWH(double newReadingKWH)
	{
		readingKWH = new Double(newReadingKWH);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/18/2002 10:49:42 AM)
	 * @param newIndicator java.lang.String
	 */
	public void setRecordIndicator(String newIndicator)
	{
		recordIndicator = newIndicator;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/4/2002 4:01:56 PM)
	 * @param newTime java.lang.String
	 */
	public void setTime(java.sql.Timestamp timestamp)
	{
		java.util.Date d = new java.util.Date(timestamp.getTime());
		time = TIME_FORMAT.format(d);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/4/2002 4:01:56 PM)
	 * @param newTime java.lang.String
	 */
	public void setTimeKW(java.sql.Timestamp timestamp)
	{
		java.util.Date d = new java.util.Date(timestamp.getTime());
		timeKW = TIME_FORMAT.format(d);
	}
}
