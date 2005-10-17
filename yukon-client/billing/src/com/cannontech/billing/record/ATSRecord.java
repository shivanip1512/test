package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class ATSRecord extends TurtleRecordBase 
{
	/**
	 * ATSRecord constructor comment.
	 */
	public ATSRecord() {
		super();
	}
	/**
	 * ATSRecord constructor comment.
	 */
	public ATSRecord(String newMeterNumber)
	{
		super(newMeterNumber);
	}
	/**
	 * ATSRecord constructor comment.
	 */
	public ATSRecord(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, reading, newTimestamp);
	}
	/**
	 * ATSRecord constructor comment.
	 */
	public ATSRecord(String newMeterNumber, java.sql.Timestamp newTimestamp)
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
				   (o instanceof ATSRecord) &&
				   ((ATSRecord)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
	}
}
