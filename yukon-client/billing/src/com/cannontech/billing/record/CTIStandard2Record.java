package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 1:29:27 PM)
 * @author: 
 */
public class CTIStandard2Record implements BillingRecordBase
{
	private String address;		//8
	private String pointName;	//20
	private Double reading;			//8.3
	private String date;	// mm/dd/yy
	private String time;	// hh:mm
	private String quality;	//1 ( N|F )
	
	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("MM/dd/yy");
	private static java.text.SimpleDateFormat TIME_FORMAT = new java.text.SimpleDateFormat("HH:mm");
	private static java.text.DecimalFormat DECIMAL_FORMAT_8V3 = new java.text.DecimalFormat("########.000");
/**
 * CTICSV constructor comment.
 */
public CTIStandard2Record()
{
	super();
}
/**
 * CTICSV constructor comment.
 */
public CTIStandard2Record(String address_, String pointName_, double reading_, java.sql.Timestamp timeStamp_)
{
	this(address_, pointName_, reading_, timeStamp_, "N");
}
/**
 * CTICSV constructor comment.
 */
public CTIStandard2Record(String address_, String pointName_, double reading_, java.sql.Timestamp timeStamp_, String quality_)
{
	super();
	setAddress(address_);
	setPointName(pointName_);
	setReading(reading_);
	setDate( timeStamp_ );
	setTime( timeStamp_ );
	setQuality( quality_ );
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 1:40:35 PM)
 * @return java.lang.String
 * ssssssss,pppppppppppppppppppp,rrrrrrr.rr,mm/dd/yyyy,hh:mm,qqq
		s = 8 digit MCT Serial Number (including leading 10) 	
		p = 20 character point name field (can be KWH, WATER, ect.) 	
		r = 9 numeric characters with 1 decimal point which is the reading 
		m = 2 character month the read was taken
		d = 2 character day the read was taken
		y = 2 character year the read was taken
		h = 2 character hour the read was taken 
		m = 2 character minutes the read was taken
		q = 3 character quality of reading. N is normal reading, F is reading failed.
 */
public String dataToString()
{
	StringBuffer writeToFile = new StringBuffer();

	writeToFile.append(getAddress());
//	for ( int i = getAddress().length(); i < 8; i++)
//	{
//		writeToFile.append(" ");
//	}
	writeToFile.append(",");

	writeToFile.append(getPointName());
	for ( int i = getPointName().length(); i < 20; i++)
	{
		writeToFile.append(" ");
	}
	writeToFile.append(",");

//	for ( int i = 0; i < (12 -  DECIMAL_FORMAT_8V3.format(getReading()).length());i++)
//	{
//		//technically this field is 13 places (if you include the decimal)
//		writeToFile.append(" ");
//	}
	writeToFile.append(DECIMAL_FORMAT_8V3.format(getReading()) + ",");
	writeToFile.append(getDate() + ",");
	writeToFile.append(getTime() + ",");
	writeToFile.append(getQuality() + "\r\n");
			
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
 * Creation date: (3/4/2002 1:35:47 PM)
 * @return double
 */
public Double getReading()
{
	return reading;
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
 * Creation date: (3/4/2002 4:01:21 PM)
 * @param newReading java.lang.Double
 */
public void setReading(double newReading)
{
	reading = new Double(newReading);
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 4:01:36 PM)
 * @param newStatus java.lang.String
 */
public void setQuality(String quality_)
{
	quality = quality_;
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
	 * @return
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * @return
	 */
	public String getPointName()
	{
		return pointName;
	}

	/**
	 * @param string
	 */
	public void setAddress(String string)
	{
		address = string;
	}

	/**
	 * @param string
	 */
	public void setPointName(String string)
	{
		pointName = string;
	}

}
