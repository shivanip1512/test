package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 1:29:27 PM)
 * @author: 
 */
public class OPURecord implements BillingRecordBase
{
	private String name;		//20
	private String pointName;	//20
	private Double reading;			//7.2
	private String quality;			//3

	private String date;
	private String time;

	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("MM/dd/yyyy");
	private static java.text.SimpleDateFormat TIME_FORMAT = new java.text.SimpleDateFormat("HH:mm");
	private static java.text.DecimalFormat DECIMAL_FORMAT_7V2 = new java.text.DecimalFormat("#######.00");
/**
 * CTICSV constructor comment.
 */
public OPURecord()
{
	super();
}
/**
 * CTICSV constructor comment.
 */
public OPURecord(String nameString, double readingValue, java.sql.Timestamp timeStamp)
{
	super();
	setName( nameString );
	setReading( readingValue );
	setDate( timeStamp );
	setTime( timeStamp );
	
	//pointName defaults to KWH
	//quality defaults to N
}
/**
 * CTICSV constructor comment.
 */
public OPURecord(String nameString, String ptName, double readingValue, java.sql.Timestamp timeStamp, String readingQuality)
{
	super();
	setName( nameString );
	setPointName(ptName);
	setReading( readingValue );
	setDate( timeStamp );
	setTime( timeStamp );
	setQuality(readingQuality);
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 1:40:35 PM)
 * @return java.lang.String
//"nnnnnnnnnnnnnnnnnnnn,pppppppppppppppppppp,rrrrrrr.rr,mm-dd-yy,hh:mm,qqq"
// (quotes surround the entire data string)
//n - 20 name filed
//p - 20 point name (KWH default)
//r - 7.2 reading
//q - 3 point quality
 */
public String dataToString()
{
	StringBuffer writeToFile = new StringBuffer("\"");

	writeToFile.append(getName());
	for ( int i = name.length(); i < 20; i++)
	{
		writeToFile.append(" ");
	}
	writeToFile.append(",");

	writeToFile.append(getPointName());
	for ( int i = pointName.length(); i < 20; i++)
	{
		writeToFile.append(" ");
	}
	writeToFile.append(",");

	for ( int i = 0; i < (10 -  DECIMAL_FORMAT_7V2.format(getReading()).length());i++)
	{
		//technically this field is 10 places (if you include the decimal)
		writeToFile.append(" ");
	}
	writeToFile.append(DECIMAL_FORMAT_7V2.format(getReading()) + ",");
	
	writeToFile.append(getDate() + ",");
	
	writeToFile.append(getTime() + ",");
	
	writeToFile.append(getQuality() + "\"\r\n");
			
	return writeToFile.toString();
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
 * Creation date: (3/4/2002 1:35:23 PM)
 * @return java.lang.String
 */
public String getName()
{
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 1:36:10 PM)
 * @return java.lang.String
 */
public String getPointName()
{
	if( pointName == null)
		return "kWh";
	return pointName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 1:36:52 PM)
 * @return java.lang.String
 */
public String getQuality()
{
	if (quality == null)
		return "N";
		
	return quality;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 1:35:47 PM)
 * @return double
 */
public Double getReading()
{
	return reading;
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
 * Creation date: (3/4/2002 4:00:45 PM)
 * @param newName java.lang.String
 */
public void setName(String newName) 
{
	if( newName.length() > 20)
		name = newName.substring(0, 19);
	else 
		name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 4:01:04 PM)
 * @param newLabel java.lang.String
 */
public void setPointName(String newPtName)
{
	if( newPtName.length() > 20)
		pointName = newPtName.substring(0, 19);
	else
		pointName = newPtName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 4:01:36 PM)
 * @param newStatus java.lang.String
 */
public void setQuality(String newQuality)
{
	quality = newQuality;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 4:01:21 PM)
 * @param newReading java.lang.Double
 */
public void setReading(double newReading)
{
	reading = new Double(newReading);
	
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
}
