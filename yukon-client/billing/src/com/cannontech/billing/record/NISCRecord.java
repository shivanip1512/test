package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class NISCRecord extends TurtleRecordBase 
{
	//KW Reading is only 6 places.
	private static java.text.DecimalFormat KW_FORMAT_3v2 = new java.text.DecimalFormat("##0.00");
	private static int KW_FIELD_SIZE = 6;
	/**
	 * NISCRecord constructor comment.
	 */
	public NISCRecord()
	{
		super();
	}
	/**
	 * NISCRecord constructor comment.
	 */
	public NISCRecord(String newMeterNumber)
	{
		super(newMeterNumber);
	}
	/**
	 * NISCRecord constructor comment.
	 */
	public NISCRecord(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, reading, newTimestamp);
	}
	/**
	 * NISCRecord constructor comment.
	 */
	public NISCRecord(String newMeterNumber, java.sql.Timestamp newTimestamp)
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
				   (o instanceof NISCRecord) &&
				   ((NISCRecord)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
	}
	
	public int getKwFieldSize()
	{
		return KW_FIELD_SIZE;
	}
	public java.text.DecimalFormat getKwFormat()
	{
		return KW_FORMAT_3v2;
	}

}
