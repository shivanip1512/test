package com.cannontech.billing.record;

import com.cannontech.billing.FileFormatTypes;
/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class SEDCRecord implements BillingRecordBase 
{
	private String recordIndicator = "M";
	private String meterNumber = null;	//10
	private Double readingKWH = null;	//5
	private String time = null;			//HH:mm
	private String date = null;			//yy/MM/dd

	private Double readingKW = null;	//7
	private String timeKW = null;			//HH:mm
	private String dateKW = null;			//yy/MM/dd

	
	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yy/MM/dd");
	private static java.text.SimpleDateFormat TIME_FORMAT = new java.text.SimpleDateFormat("HH:mm");
	private static java.text.DecimalFormat KWH_FORMAT_NODECIMAL = new java.text.DecimalFormat("#####");
	private static java.text.DecimalFormat KW_FORMAT_3v3 = new java.text.DecimalFormat("##0.000");

	//Used to represent what version of SEDC is being used.
	//'Old' SEDC5.4 version doesn't accept the extra commas and semicolon.
	//Default is 'new' version.
	private int version = FileFormatTypes.NEW_VERSION;

/**
 * SEDCRecord constructor comment.
 */
public SEDCRecord() {
	super();
}
/**
 * SEDCRecord constructor comment.
 */
public SEDCRecord(String newMeterNumber)
{
	super();
	setMeterNumber(newMeterNumber);
}
/**
 * SEDCRecord constructor comment.
 */
public SEDCRecord(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
{
	super();
	setMeterNumber(newMeterNumber);
	setReadingKWH(reading);
	setDate(newTimestamp);
	setTime(newTimestamp);
}
/**
 * SEDCRecord constructor comment.
 */
public SEDCRecord(String newMeterNumber, java.sql.Timestamp newTimestamp)
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
	
	for ( int i = 0; i < (5 - KWH_FORMAT_NODECIMAL.format(getReadingKWH()).length());i++)
	{
		writeToFile.append(" ");
	}
	writeToFile.append(KWH_FORMAT_NODECIMAL.format(getReadingKWH()) + ",");
	
	writeToFile.append(getTime() + ",");

	writeToFile.append(getDate() + ",");

	if(getVersion() == FileFormatTypes.OLD_VERSION)
	{
		//quit now... forget peak crap!
		writeToFile.append("\r\n");		
		return writeToFile.toString();
	}

	//Peak
	if( getReadingKW() != null)
	{
		for ( int i = 0; i < (7 - KW_FORMAT_3v3.format(getReadingKW()).length());i++)
		{
			writeToFile.append(" ");
		}

		writeToFile.append(KW_FORMAT_3v3.format(getReadingKW()));
	}
	writeToFile.append(',');
	//PeakT
	if( getTimeKW() != null)
		writeToFile.append(getTimeKW());
		
	writeToFile.append(',');
	//PeakD
	if( getDateKW() != null)
		writeToFile.append(getDateKW());
		
	writeToFile.append(',');
	
	//Stat
	writeToFile.append(',');
	
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
			   (o instanceof SEDCRecord) &&
			   ((SEDCRecord)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 1:34:53 PM)
 * @return java.lang.String
 */
public String getDate()
{
	return date;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 1:34:53 PM)
 * @return java.lang.String
 */
public String getDateKW()
{
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
	return time;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 1:34:53 PM)
 * @return java.lang.String
 */
public String getTimeKW()
{
	return timeKW;
}

private int getVersion()
{
	return version;
}
public void setVersion(int newVersionType)
{
	version = newVersionType;
}

/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 4:02:16 PM)
 * @param newDate java.lang.String
 */
public void setDate(java.sql.Timestamp timeStamp)
{
	java.util.Date d = new java.util.Date(timeStamp.getTime());
	date = DATE_FORMAT.format(d);
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 4:02:16 PM)
 * @param newDate java.lang.String
 */
public void setDateKW(java.sql.Timestamp timeStamp)
{
	java.util.Date d = new java.util.Date(timeStamp.getTime());
	dateKW = DATE_FORMAT.format(d);
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
