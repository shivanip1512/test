package com.cannontech.billing;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class SEDC54Format extends TurtleFormatBase 
{

	private static final String HEADER = "H    Meter    Kwh   Time   Date\r\n";
/**
 * Default SEDC54 constructor
 */
public SEDC54Format() 
{
	super();
}

public com.cannontech.billing.record.TurtleRecordBase createRecord(String meterNumber)
{
	return new com.cannontech.billing.record.SEDC54Record(meterNumber);
}
public com.cannontech.billing.record.TurtleRecordBase getRecord(int index)
{
	return (com.cannontech.billing.record.SEDC54Record)getRecordVector().get(index);
}

public String getHeader()
{
	return HEADER;
}
}
