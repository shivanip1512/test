package com.cannontech.report.loadmanagement;

/**
 * Insert the type's description here.
 * Creation date: (6/28/00 11:55:04 AM)
 * @author: 
 */
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.point.CTIPointQuailtyException;
import com.cannontech.report.ReportRecordBase;
public class DailyPeaksRecord implements ReportRecordBase
{
	private String controlAreaName = null;
	private Double peakValue = null;
	private Integer peakDataQuality = null;
	private java.util.GregorianCalendar peakTimestamp = null;
	private Double offPeakValue = null;
	private Integer offPeakDataQuality = null;
	private java.util.GregorianCalendar offPeakTimestamp = null;
	private Double threshold = null;
	
/**
 * CADPFormat constructor comment.
 */
public DailyPeaksRecord()
{
	super();
}
/**
 * CADPFormat constructor comment.
 */
public DailyPeaksRecord(String controlAreaName, Double peakValue, Integer peakDataQuality,
												java.util.GregorianCalendar peakTimestamp, Double offPeakValue,
												Integer offPeakDataQuality, java.util.GregorianCalendar offPeakTimestamp,
												Double threshold)
{
	super();
	setControlAreaName(controlAreaName);
	setPeakValue(peakValue);
	setPeakDataQuality(peakDataQuality);
	setPeakTimestamp(peakTimestamp);
	setOffPeakValue(offPeakValue);
	setOffPeakDataQuality(offPeakDataQuality);
	setOffPeakTimestamp(offPeakTimestamp);
	setThreshold(threshold);
}
/**
 * dataToString method comment.
 */
public String dataToString()
{
	StringBuffer returnBuffer = new StringBuffer();

	java.text.SimpleDateFormat dateTimeFormatter = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	java.text.DecimalFormat doubleFormatter = new java.text.DecimalFormat();
	doubleFormatter.applyPattern("###,###,##0.00");

	{
		String peakValStr = doubleFormatter.format( getPeakValue().doubleValue() );
		for(int i=0;i<(12-peakValStr.length());i++)
		{
			returnBuffer.append(" ");
		}
		if( peakValStr.length() <= 12 )
		{
			returnBuffer.append(peakValStr);
		}
		else
		{
			returnBuffer.append(peakValStr.substring(0,9));
			returnBuffer.append("...");
		}
		returnBuffer.append(" ");

		if( getPeakDataQuality().intValue() != com.cannontech.database.data.point.PointQualities.UNINTIALIZED_QUALITY )
		{
			try
			{
				String peakQualityStr = com.cannontech.database.data.point.PointQualities.getQuality(getPeakDataQuality().intValue());
				if( peakQualityStr.length() <= 10 )
				{
					returnBuffer.append(peakQualityStr);
				}
				else
				{
					returnBuffer.append(peakQualityStr.substring(0,10));
				}
				for(int i=0;i<(10-peakQualityStr.length());i++)
				{
					returnBuffer.append(" ");
				}
			}
			catch( CTIPointQuailtyException ex )
			{
				CTILogger.error("Pt Quaility not found", ex );
			}
		}
		else
		{
			returnBuffer.append("   ---    ");
		}
		returnBuffer.append(" ");


		if( getPeakTimestamp().getTime().getTime() > (new java.util.GregorianCalendar(1991,1,1)).getTime().getTime() )
		{
			returnBuffer.append(dateTimeFormatter.format(getPeakTimestamp().getTime()));
		}
		else
		{
			returnBuffer.append("     -----         ");
		}
		returnBuffer.append("     ");
	}

	{
		String offPeakValStr = doubleFormatter.format( getOffPeakValue().doubleValue() );
		for(int i=0;i<(12-offPeakValStr.length());i++)
		{
			returnBuffer.append(" ");
		}
		if( offPeakValStr.length() <= 12 )
		{
			returnBuffer.append(offPeakValStr);
		}
		else
		{
			returnBuffer.append(offPeakValStr.substring(0,9));
			returnBuffer.append("...");
		}
		returnBuffer.append(" ");

		if( getOffPeakDataQuality().intValue() != com.cannontech.database.data.point.PointQualities.UNINTIALIZED_QUALITY )
		{
			try
			{
				String offPeakQualityStr = com.cannontech.database.data.point.PointQualities.getQuality(getOffPeakDataQuality().intValue());
				if( offPeakQualityStr.length() <= 10 )
				{
					returnBuffer.append(offPeakQualityStr);
				}
				else
				{
					returnBuffer.append(offPeakQualityStr.substring(0,10));
				}
				for(int i=0;i<(10-offPeakQualityStr.length());i++)
				{
					returnBuffer.append(" ");
				}
			}
			catch( CTIPointQuailtyException ex )
			{
				CTILogger.error("Pt Quaility not found", ex );
			}
		}
		else
		{
			returnBuffer.append("   ---    ");
		}
		returnBuffer.append(" ");

		if( getOffPeakTimestamp().getTime().getTime() > (new java.util.GregorianCalendar(1991,1,1)).getTime().getTime() )
		{
			returnBuffer.append(dateTimeFormatter.format(getOffPeakTimestamp().getTime()));
		}
		else
		{
			returnBuffer.append("     -----         ");
		}
	}

	return returnBuffer.toString();
}
/**
 */
public java.util.Vector getControlAreaHeaderVector()
{
	java.util.Vector returnVector = new java.util.Vector();

	returnVector.add("Printed on: " + dateFormatter.format(new java.util.Date()));
	returnVector.add("Control Summary for Control Area: " + getControlAreaName());

	return returnVector;
}

/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @return java.lang.String
 */
public java.lang.String getControlAreaName() {
	return controlAreaName;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOffPeakDataQuality() {
	return offPeakDataQuality;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getOffPeakTimestamp() {
	return offPeakTimestamp;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @return java.lang.Double
 */
public java.lang.Double getOffPeakValue() {
	return offPeakValue;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPeakDataQuality() {
	return peakDataQuality;
}
/**
 */
public java.util.Vector getPeakHeaderVector()
{
	java.util.Vector returnVector = new java.util.Vector();

	returnVector.add("                   PEAK TIME                                        OFF PEAK TIME               ");
	returnVector.add("\r\n");
	returnVector.add("      Peak Value   Quality          Time              Peak Value   Quality          Time        ");
	returnVector.add("     ------------ ---------- -------------------     ------------ ---------- -------------------");
	//sample template and ruler
	//returnVector.add("1    5534.98      Normal     MM/dd/yyyy HH:mm:ss     5534.98      Normal     MM/dd/yyyy HH:mm:ss");
	//returnVector.add("         1         2         3         4         5         6         7         8         9        10        11");
	//returnVector.add("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

	return returnVector;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getPeakTimestamp() {
	return peakTimestamp;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @return java.lang.Double
 */
public java.lang.Double getPeakValue() {
	return peakValue;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @return java.lang.Double
 */
public java.lang.Double getThreshold() {
	return threshold;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @param newControlAreaName java.lang.String
 */
public void setControlAreaName(java.lang.String newControlAreaName) {
	controlAreaName = newControlAreaName;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @param newOffPeakDataQuality java.lang.Integer
 */
public void setOffPeakDataQuality(java.lang.Integer newOffPeakDataQuality) {
	offPeakDataQuality = newOffPeakDataQuality;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @param newOffPeakTimestamp java.util.GregorianCalendar
 */
public void setOffPeakTimestamp(java.util.GregorianCalendar newOffPeakTimestamp) {
	offPeakTimestamp = newOffPeakTimestamp;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @param newOffPeakValue java.lang.Double
 */
public void setOffPeakValue(java.lang.Double newOffPeakValue) {
	offPeakValue = newOffPeakValue;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @param newPeakDataQuality java.lang.Integer
 */
public void setPeakDataQuality(java.lang.Integer newPeakDataQuality) {
	peakDataQuality = newPeakDataQuality;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @param newPeakTimestamp java.util.GregorianCalendar
 */
public void setPeakTimestamp(java.util.GregorianCalendar newPeakTimestamp) {
	peakTimestamp = newPeakTimestamp;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @param newPeakValue java.lang.Double
 */
public void setPeakValue(java.lang.Double newPeakValue) {
	peakValue = newPeakValue;
}
/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 3:00:38 PM)
 * @param newThreshold java.lang.Double
 */
public void setThreshold(java.lang.Double newThreshold) {
	threshold = newThreshold;
}
}
