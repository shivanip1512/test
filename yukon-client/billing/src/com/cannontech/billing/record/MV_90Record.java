package com.cannontech.billing.record;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class MV_90Record implements BillingRecordBase 
{
	private String timeKW = null;			//HH:mm
	private String dateKW = null;			//MM/dd/yy (Changed from MM/dd/yyyy per document and DSutton)

	private String meterNumber = null;	//10
	private boolean newMeterNumber = false;
	private Vector<Double> readingKWVector = null;
	private Double readingKW = null;	//8.1

	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("MM/dd/yy");
	private static java.text.SimpleDateFormat TIME_FORMAT = new java.text.SimpleDateFormat("HH:mm");
	private static java.text.DecimalFormat KW_FORMAT_8v1 = new java.text.DecimalFormat("#######0.0");
	
	private String lastMeterNumber = "";
	
	/**
	 * MV_90Record constructor comment.
	 */
	public MV_90Record() {
		super();
	}
	/**
	 * MV_90Record constructor comment.
	 */
	public MV_90Record(String newMeterNumber)
	{
		super();
		setMeterNumber(newMeterNumber);
	}

	/**
	 * Converts data in a MV_90Format to a formatted StringBuffer for stream use.
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
	
		if( getNewMeterNumber())
		{
			writeToFile.append( "ID " +getMeterNumber() + "\r\n" );
		}
	
		writeToFile.append(getDateKW() + ',');
	
		writeToFile.append(getTimeKW() + ',');
			
		if( getReadingKWVector() != null)
		{
			Double value = getReadingKWVector().get(0);

			//Rounding Mode is not taken into affect
			writeToFile.append(KW_FORMAT_8v1.format(value.doubleValue()));
			
			for( int i = 1; i < getReadingKWVector().size(); i++)
			{
				value = getReadingKWVector().get(i);
				{
	//				for ( int j = 0; j < (10 - KW_FORMAT_8v1.format(value.length()));j++)
	//				{
	//					writeToFile.append(" ");
	//				}
				}
				//Rounding Mode is not taken into affect
				writeToFile.append(',' + KW_FORMAT_8v1.format(value.doubleValue()));
			}
		}
	
		writeToFile.append("\r\n");
	
		lastMeterNumber = getMeterNumber();	
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
				   (o instanceof MV_90Record) &&
				   ((MV_90Record)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
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
	
	public boolean getNewMeterNumber()
	{
		return newMeterNumber;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/27/2001 3:16:28 PM)
	 * @return java.lang.String
	 */
	public Vector<Double> getReadingKWVector()
	{
		return readingKWVector;
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
	/**
	 * Insert the method's description here.
	 * Creation date: (3/4/2002 4:02:16 PM)
	 * @param newDate java.lang.String
	 */
	public void setDateTimeKW(java.sql.Timestamp timeStamp)
	{
	    setTimeKW(timeStamp);
		java.util.Date d = new java.util.Date(timeStamp.getTime());
		
		if( getTimeKW().equalsIgnoreCase("24:00"))
		{
		    Calendar dummyDate = new GregorianCalendar();
		    dummyDate.setTimeInMillis(timeStamp.getTime());
		    dummyDate.add(Calendar.DATE, -1);
			d = dummyDate.getTime();
		}
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
	public void setNewMeterNumber(boolean isNewMeterNumber)
	{
		newMeterNumber = isNewMeterNumber;
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
	public void setReadingKWVector(Vector<Double> newReadingKWHVector)
	{
		readingKWVector = newReadingKWHVector;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/4/2002 4:01:56 PM)
	 * @param newTime java.lang.String
	 */
	private void setTimeKW(java.sql.Timestamp timestamp)
	{
		java.util.Date d = new java.util.Date(timestamp.getTime());
		
		//End of day adjustment for time.
		//Need to change to 24 hour instead of 0 hour.  But ONLY for the 00:00 reading.  So change all non :00 times to have 24 hour.
		timeKW = TIME_FORMAT.format(d);
		if (timeKW.startsWith("00")) {
		    if (timeKW.endsWith("00")) {
		        timeKW = timeKW.replaceFirst("00:", "24:");
		    }
		}
	}
}
