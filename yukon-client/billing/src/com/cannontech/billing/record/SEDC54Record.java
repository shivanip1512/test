package com.cannontech.billing.record;

import com.cannontech.billing.FileFormatTypes;
/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class SEDC54Record extends TurtleRecordBase 
{
	private static java.text.DecimalFormat KW_FORMAT_3v3 = new java.text.DecimalFormat("##0.000");
	private static int KW_FIELD_SIZE = 7;
	/**
	 * SEDC54Record constructor comment.
	 */
	public SEDC54Record() {
		super();
	}
	/**
	 * SEDC54Record constructor comment.
	 */
	public SEDC54Record(String newMeterNumber)
	{
		super(newMeterNumber);
	}
	/**
	 * SEDC54Record constructor comment.
	 */
	public SEDC54Record(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, reading, newTimestamp);
	}
	/**
	 * SEDC54Record constructor comment.
	 */
	public SEDC54Record(String newMeterNumber, java.sql.Timestamp newTimestamp)
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
				   (o instanceof SEDC54Record) &&
				   ((SEDC54Record)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
	}

	/**
	 * Returns the KW reading field size.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return int
	 */
	public int getKwFieldSize()
	{
		return KW_FIELD_SIZE;
	}

	/**
	 * Returns the KW reading format.
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return DecimalFormat
	 */
	public java.text.DecimalFormat getKwFormat()
	{
		return KW_FORMAT_3v3;
	}
}
