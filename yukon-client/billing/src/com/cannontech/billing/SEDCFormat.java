package com.cannontech.billing;

import com.cannontech.billing.record.SEDCRecord;
import com.cannontech.billing.record.TurtleRecordBase;


/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class SEDCFormat extends TurtleFormatBase 
{
	/**
	 * Default SEDC constructor
	 */
	public SEDCFormat() 
	{
		super();
	}
	
	public TurtleRecordBase createRecord(String meterNumber)
	{
		return new SEDCRecord(meterNumber);
	}
	public TurtleRecordBase getRecord(int index)
	{
		return (SEDCRecord)getRecordVector().get(index);
	}
}
