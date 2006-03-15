package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class NISC_NoLimt_kWh_Record extends NISCRecord 
{
	/**
	 * NISC_5Digit_kWh_Record constructor comment.
	 */
	public NISC_NoLimt_kWh_Record()
	{
		super();
//		some large number to override TurtleRecordBase
		KWH_FORMAT_NODECIMAL.setMaximumIntegerDigits(20);
	}
	/**
	 * NISC_5Digit_kWh_Record constructor comment.
	 */
	public NISC_NoLimt_kWh_Record(String newMeterNumber)
	{
		super(newMeterNumber);
		KWH_FORMAT_NODECIMAL.setMaximumIntegerDigits(20);
	}
	/**
	 * NISC_5Digit_kWh_Record constructor comment.
	 */
	public NISC_NoLimt_kWh_Record(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, reading, newTimestamp);
		KWH_FORMAT_NODECIMAL.setMaximumIntegerDigits(20);
	}
	/**
	 * NISC_5Digit_kWh_Record constructor comment.
	 */
	public NISC_NoLimt_kWh_Record(String newMeterNumber, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, newTimestamp);
		KWH_FORMAT_NODECIMAL.setMaximumIntegerDigits(20);
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
				   (o instanceof NISC_NoLimt_kWh_Record) &&
				   ((NISC_NoLimt_kWh_Record)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
	}
}
