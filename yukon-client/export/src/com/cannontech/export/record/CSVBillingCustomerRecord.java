package com.cannontech.export.record;

/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 2:52:49 PM)
 * @author: 
 */
public class CSVBillingCustomerRecord implements RecordBase
{
	private String customerName;
	private String energyDebtor;
	private String energyPremise;
	private String meterLocation;
	private Integer baselinePointId;
	private Integer curtailPointId;

/**
 * CSVBillingCustomerRecord constructor comment.
 */
public CSVBillingCustomerRecord() {
	super();
}
/**
 * CSVBillingCustomerRecord constructor comment.
 */
public CSVBillingCustomerRecord(String custName, String meterLoc, String engDebt, String engPre, Integer baselinePt, Integer curtailPt)
{
	super();
	setCustomerName(custName);
	setMeterLocation(meterLoc);
	setEnergyDebtor(engDebt);
	setEnergyPremise(engPre);
	setBaselinePointId(baselinePt);
	setCurtailPointId(curtailPt);
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2002 1:11:49 PM)
 * @return java.lang.String
 */
public String dataToString()
{
	//This is an extension of CSVBillingRecord.  It does not explicately return
	//  any sort of a dataToString value.
	return null;
}
public Integer getBaselinePointId()
{
	return baselinePointId;
}
public Integer getCurtailPointId()
{
	return curtailPointId;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 1:35:02 PM)
 * @return java.lang.String
 */
public String getCustomerName()
{
	return customerName;
}
public String getMeterLocation()
{
	return meterLocation;
}
public String getEnergyDebtor()
{
	return energyDebtor;
}
public String getEnergyPremise()
{
	return energyPremise;
}
public void setBaselinePointId(Integer newBaselinePt)
{
	baselinePointId = newBaselinePt;
}
public void setCurtailPointId(Integer newCurtailPt)
{
	curtailPointId = newCurtailPt;
}
public void setCustomerName(String newCustomerName)
{
	customerName = newCustomerName;
}
public void setEnergyDebtor(String newEnergyDebtor)
{
	energyDebtor = newEnergyDebtor;
}
public void setEnergyPremise(String newEnergyPremise)
{
	energyPremise = newEnergyPremise;
}
public void setMeterLocation(String newMeterLocation)
{
	meterLocation = newMeterLocation;
}
}
