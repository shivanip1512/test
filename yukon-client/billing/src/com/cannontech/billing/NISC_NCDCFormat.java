package com.cannontech.billing;


/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class NISC_NCDCFormat extends NCDCFormat 
{
	/**
	 * Default NISC constructor
	 */
	public NISC_NCDCFormat() 
	{
		super();
	}
	
	public com.cannontech.billing.record.TurtleRecordBase createRecord(String meterNumber)
	{
		return new com.cannontech.billing.record.NISC_NCDCRecord(meterNumber);
	}
	public com.cannontech.billing.record.TurtleRecordBase getRecord(int index)
	{
		return (com.cannontech.billing.record.NISC_NCDCRecord)getRecordVector().get(index);
	}
}
