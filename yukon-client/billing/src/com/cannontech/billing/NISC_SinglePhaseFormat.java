package com.cannontech.billing;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class NISC_SinglePhaseFormat extends TurtleFormatBase 
{
	private static final String HEADER = "H    Meter    Kwh   Time   Date\r\n";
	
	/**
	 * Default NISC constructor
	 */
	public NISC_SinglePhaseFormat() 
	{
		super();
	}
	
	public com.cannontech.billing.record.TurtleRecordBase createRecord(String meterNumber)
	{
		return new com.cannontech.billing.record.NISC_SinglePhaseRecord(meterNumber);
	}
	public com.cannontech.billing.record.TurtleRecordBase getRecord(int index)
	{
		return (com.cannontech.billing.record.NISC_SinglePhaseRecord)getRecordVector().get(index);
	}
	public String getHeader()
	{
		return HEADER;
	}
}
