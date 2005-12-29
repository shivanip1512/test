package com.cannontech.billing;

import com.cannontech.billing.record.NISC_5Digit_kWh_Record;
import com.cannontech.billing.record.TurtleRecordBase;


/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class NISC_5Digit_kWh_Format extends TurtleFormatBase 
{
	/**
	 * Default NISC constructor
	 */
	public NISC_5Digit_kWh_Format() 
	{
		super();
	}
	
	public TurtleRecordBase createRecord(String meterNumber)
	{
		return new NISC_5Digit_kWh_Record(meterNumber);
	}
	public TurtleRecordBase getRecord(int index)
	{
		return (NISC_5Digit_kWh_Record)getRecordVector().get(index);
	}
}
