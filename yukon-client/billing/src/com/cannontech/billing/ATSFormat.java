package com.cannontech.billing;

import com.cannontech.billing.record.ATSRecord;
import com.cannontech.billing.record.TurtleRecordBase;


/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class ATSFormat extends TurtleFormatBase 
{
	/**
	 * Default ATS constructor
	 */
	public ATSFormat() 
	{
		super();
	}
	
	public TurtleRecordBase createRecord(String meterNumber)
	{
		return new ATSRecord(meterNumber);
	}
	public TurtleRecordBase getRecord(int index)
	{
		return (ATSRecord)getRecordVector().get(index);
	}
}
