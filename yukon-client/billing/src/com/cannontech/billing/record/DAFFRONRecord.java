package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class DAFFRONRecord implements BillingRecordBase 
{
	private String recordIndicator = "M";
	private String meterNumber = null;	//10
	private Double readingKWH = null;	//5
	private String time = null;			//HH:mm
	private String date = null;			//yy/MM/dd

	private Double peakReadingKW = null;	//7
	private String peakTimeKW = null;			//5  HH:mm
	private String peakDateKW = null;			//8  yy/MM/dd
	
	private String stat = "  6";		// always 6
		
	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yy/MM/dd");
	private static java.text.SimpleDateFormat TIME_FORMAT = new java.text.SimpleDateFormat("HH:mm");
	private static java.text.DecimalFormat KWH_FORMAT_NODECIMAL = new java.text.DecimalFormat("#####");
	private static java.text.DecimalFormat KW_FORMAT_3v3 = new java.text.DecimalFormat("##0.000");
/**
 * DAFFRONRecord constructor comment.
 */
public DAFFRONRecord() {
	super();
}
/**
 * DAFFRONRecord constructor comment.
 */
public DAFFRONRecord(String newMeterNumber)
{
	super();
	setMeterNumber(newMeterNumber);
}
/**
 * DAFFRONRecord constructor comment.
 */
public DAFFRONRecord(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
{
	super();
	setMeterNumber(newMeterNumber);
	setReadingKWH(reading);
	setDate(newTimestamp);
	setTime(newTimestamp);
}
/**
 * DAFFRONRecord constructor comment.
 */
public DAFFRONRecord(String newMeterNumber, java.sql.Timestamp newTimestamp)
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
 //r,mmmmmmmmmm,rrrrr,tt:tt,dd/dd/dd,bbbbbb,bbbbb,bbbbbbbb,bb6
 //r - 1 meterReading/header record
 //m - 10 meterNumber Left Justified 
 //r - 5 reading
 //t - 5 time (includes :)
 //d - 8 date (includes /s)
 //b - 6 peak reading (spaces - null)
 //b - 5 peak time (spaces - null)
 //b - 8 peak date (spaces - null)
 //b - 3 stat (always bb6)

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

	//Peak Reading
	if( getPeakReadingKW() != null)
	{
		for ( int i = 0; i < (7 - KW_FORMAT_3v3.format(getPeakReadingKW()).length());i++)
		{
			writeToFile.append(" ");
		}

		writeToFile.append(KW_FORMAT_3v3.format(getPeakReadingKW()));
	}
	else
	{
		writeToFile.append("      ");	// 6 spaces
	}
	writeToFile.append(',');

	//Peak Time
	if( getPeakTimeKW() != null)
	{
		writeToFile.append(getPeakTimeKW());
	}		
	else
	{
		writeToFile.append("     ");	//5 spaces
	}
	writeToFile.append(',');
	
	//Peak Date
	if( getPeakDateKW() != null)
	{
		writeToFile.append(getPeakDateKW());
	}
	else
	{
		writeToFile.append("        ");	//8 spaces	
	}
	writeToFile.append(',');
	
	//Stat
	writeToFile.append(getStat() + ',');
	
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
public String getPeakDateKW()
{
	return peakDateKW;
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
public Double getPeakReadingKW()
{
	return peakReadingKW;
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
public String getStat()
{
	if( stat == null)
		stat = "  6";
	return stat;
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
public String getPeakTimeKW()
{
	return peakTimeKW;
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
public void setPeakDateKW(java.sql.Timestamp timeStamp)
{
	java.util.Date d = new java.util.Date(timeStamp.getTime());
	peakDateKW = DATE_FORMAT.format(d);
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
public void setPeakReadingKW(double newReadingKW)
{
	peakReadingKW = new Double(newReadingKW);
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
public void setPeakTimeKW(java.sql.Timestamp timestamp)
{
	java.util.Date d = new java.util.Date(timestamp.getTime());
	peakTimeKW = TIME_FORMAT.format(d);
}
}
