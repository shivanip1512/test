package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class NCDCRecord extends TurtleRecordBase
{
	private static java.text.DecimalFormat KW_FORMAT_3v2 = new java.text.DecimalFormat("##0.00");
	private static int KW_FIELD_SIZE = 6;
	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyyy/MM/dd");	

	/**
	 * NCDCRecord constructor comment.
	 */
	public NCDCRecord() {
		super();
	}
	/**
	 * NCDCRecord constructor comment.
	 */
	public NCDCRecord(String newMeterNumber)
	{
		super(newMeterNumber);
	}
	/**
	 * NCDCRecord constructor comment.
	 */
	public NCDCRecord(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, reading, newTimestamp);
	}
	/**
	 * NCDCRecord constructor comment.
	 */
	public NCDCRecord(String newMeterNumber, java.sql.Timestamp newTimestamp)
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
				   (o instanceof NCDCRecord) &&
				   ((NCDCRecord)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
	}
	
	public int getKwFieldSize()
	{
		return KW_FIELD_SIZE;
	}
	public java.text.DecimalFormat getKwFormat()
	{
		return KW_FORMAT_3v2;
	}
	public java.text.SimpleDateFormat getDateFormat()
	{
		return DATE_FORMAT;
	}
	
}
