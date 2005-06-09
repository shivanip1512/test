package com.cannontech.billing;


/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class SEDCFormat_yyyyMMdd extends SEDCFormat 
{

	/**
	 * Default SEDC_yyyyMMdd constructor
	 */
	public SEDCFormat_yyyyMMdd() 
	{
		super();
	}
	
	public com.cannontech.billing.record.TurtleRecordBase createRecord(String meterNumber)
	{
		return new com.cannontech.billing.record.SEDC_yyyyMMddRecord(meterNumber);
	}
	public com.cannontech.billing.record.TurtleRecordBase getRecord(int index)
	{
		return (com.cannontech.billing.record.SEDC_yyyyMMddRecord)getRecordVector().get(index);
	}
}
