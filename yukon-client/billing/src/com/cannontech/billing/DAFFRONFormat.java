package com.cannontech.billing;


/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class DAFFRONFormat extends TurtleFormatBase 
{
	public static final String HEADER =
		"H    Meter    Kwh   Time   Date   Peak   PeakT  PeakD  Stat Sig Freq Phase\r\n";

	/**
	 * Default DAFFRON constructor
	 */
	public DAFFRONFormat() 
	{
		super();
	}

	public com.cannontech.billing.record.TurtleRecordBase createRecord(String meterNumber)
	{
		return new com.cannontech.billing.record.DAFFRONRecord(meterNumber);
	}
	public com.cannontech.billing.record.TurtleRecordBase getRecord(int index)
	{
		return (com.cannontech.billing.record.DAFFRONRecord)getRecordVector().get(index);
	}
	
	public String getHeader()
	{
		return HEADER;
	}
}
