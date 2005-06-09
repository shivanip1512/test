package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class SEDC_yyyyMMddRecord extends SEDCRecord 
{
	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyyy/MM/dd");
	/**
	 * SEDC_yyyyMMddRecord constructor comment.
	 */
	public SEDC_yyyyMMddRecord() {
		super();
	}
	/**
	 * SEDC_yyyyMMddRecord constructor comment.
	 */
	public SEDC_yyyyMMddRecord(String newMeterNumber)
	{
		super(newMeterNumber);
	}
	/**
	 * SEDC_yyyyMMddRecord constructor comment.
	 */
	public SEDC_yyyyMMddRecord(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, reading, newTimestamp);
	}
	/**
	 * SEDC_yyyyMMddRecord constructor comment.
	 */
	public SEDC_yyyyMMddRecord(String newMeterNumber, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, newTimestamp);
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
				   (o instanceof SEDC_yyyyMMddRecord) &&
				   ((SEDC_yyyyMMddRecord)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
	}
	
	/**
	 * Returns the Date format.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return java.text.SimpleDateFormat
	 */
	public java.text.SimpleDateFormat getDateFormat()
	{
		return DATE_FORMAT;
	}
}
