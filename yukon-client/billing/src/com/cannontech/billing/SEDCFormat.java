package com.cannontech.billing;


/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class SEDCFormat extends TurtleFormatBase 
{

	private static final String HEADER = "H    Meter    Kwh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase\r\n";
	/**
	 * Default SEDC constructor
	 */
	public SEDCFormat() 
	{
		super();
	}
	
	public com.cannontech.billing.record.TurtleRecordBase createRecord(String meterNumber)
	{
		return new com.cannontech.billing.record.SEDCRecord(meterNumber);
	}
	public com.cannontech.billing.record.TurtleRecordBase getRecord(int index)
	{
		return (com.cannontech.billing.record.SEDCRecord)getRecordVector().get(index);
	}
	
	public String getHeader()
	{
		return HEADER;
	}
}
