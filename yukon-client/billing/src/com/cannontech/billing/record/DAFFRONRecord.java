package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class DAFFRONRecord extends TurtleRecordBase
{
	private static String STAT = "  6";
	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yy/MM/dd");
	private static java.text.DecimalFormat KW_FORMAT_3v3 = new java.text.DecimalFormat("##0.000");
	private static int KW_FIELD_SIZE = 7;
	
	/**
	 * DAFFRONRecord constructor comment.
	 */
	public DAFFRONRecord() {
		super();
	}
	/**
	 * DAFFRONRecord constructor comment.
	 */
	public DAFFRONRecord(String newMeterNumber)
	{
		super(newMeterNumber);
	}
	/**
	 * DAFFRONRecord constructor comment.
	 */
	public DAFFRONRecord(String newMeterNumber, double reading, java.sql.Timestamp newTimestamp)
	{
		super(newMeterNumber, reading, newTimestamp);
	}
	/**
	 * DAFFRONRecord constructor comment.
	 */
	public DAFFRONRecord(String newMeterNumber, java.sql.Timestamp newTimestamp)
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
				   (o instanceof DAFFRONRecord) &&
				   ((DAFFRONRecord)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
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
	/**
	 * Returns the Date .
	 * Creation date: (7/31/2001 2:34:50 PM)
	 * @return SimpleDateFormat
	 */
	public java.text.SimpleDateFormat getDateFormat()
	{
		return DATE_FORMAT;
	}

	/**
	 * Returns the stat.
	 * @return String
	 */
	public String getStat()
	{
		return STAT;
	}		

}
