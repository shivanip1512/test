package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class SEDCRecord extends TurtleRecordBase 
{
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
		super(newMeterNumber);
	}
	/**
	 * SEDCRecord constructor comment.
	 */
	public SEDCRecord(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, reading, newTimestamp);
	}
	/**
	 * SEDCRecord constructor comment.
	 */
	public SEDCRecord(String newMeterNumber, java.sql.Timestamp newTimestamp)
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
				   (o instanceof SEDCRecord) &&
				   ((SEDCRecord)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
	}
}
