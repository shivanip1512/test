package com.cannontech.billing.record;

import com.cannontech.billing.FileFormatTypes;
/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class NISC_SinglePhaseRecord extends TurtleRecordBase 
{
	//Single Phase Meters, only has KWH, meter cannot/doesnot have a recorded demand(KW) reading
	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("MM/dd/yyyy");	
	/**
	 * SEDCRecord constructor comment.
	 */
	public NISC_SinglePhaseRecord() {
		super();
	}
	/**
	 * SEDCRecord constructor comment.
	 */
	public NISC_SinglePhaseRecord(String newMeterNumber)
	{
		super(newMeterNumber);
	}
	/**
	 * SEDCRecord constructor comment.
	 */
	public NISC_SinglePhaseRecord(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, reading, newTimestamp);
	}
	/**
	 * SEDCRecord constructor comment.
	 */
	public NISC_SinglePhaseRecord(String newMeterNumber, java.sql.Timestamp newTimestamp)
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
				   (o instanceof NISC_SinglePhaseRecord) &&
				   ((NISC_SinglePhaseRecord)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
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
