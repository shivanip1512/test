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
	 * @see com.cannontech.export.record.RecordBase#dataToString()
	 * This is an extension of CSVBillingRecord, but is does not need to return anything.
	 * Returns null
	 */
	public String dataToString()
	{
		return null;
	}
	/**
	 * Returns the baselinePointId.
	 * @return Integer
	 */
	public Integer getBaselinePointId()
	{
		return baselinePointId;
	}

	/**
	 * Returns the curtailPointId.
	 * @return Integer
	 */
	public Integer getCurtailPointId()
	{
		return curtailPointId;
	}

	/**
	 * Returns the customerName.
	 * @return String
	 */
	public String getCustomerName()
	{
		return customerName;
	}

	/**
	 * Returns the energyDebtor.
	 * @return String
	 */
	public String getEnergyDebtor()
	{
		return energyDebtor;
	}

	/**
	 * Returns the energyPremise.
	 * @return String
	 */
	public String getEnergyPremise()
	{
		return energyPremise;
	}

	/**
	 * Returns the meterLocation.
	 * @return String
	 */
	public String getMeterLocation()
	{
		return meterLocation;
	}

	/**
	 * Sets the baselinePointId.
	 * @param baselinePointId The baselinePointId to set
	 */
	public void setBaselinePointId(Integer baselinePointId)
	{
		this.baselinePointId = baselinePointId;
	}

	/**
	 * Sets the curtailPointId.
	 * @param curtailPointId The curtailPointId to set
	 */
	public void setCurtailPointId(Integer curtailPointId)
	{
		this.curtailPointId = curtailPointId;
	}

	/**
	 * Sets the customerName.
	 * @param customerName The customerName to set
	 */
	public void setCustomerName(String customerName)
	{
		this.customerName = customerName;
	}

	/**
	 * Sets the energyDebtor.
	 * @param energyDebtor The energyDebtor to set
	 */
	public void setEnergyDebtor(String energyDebtor)
	{
		this.energyDebtor = energyDebtor;
	}

	/**
	 * Sets the energyPremise.
	 * @param energyPremise The energyPremise to set
	 */
	public void setEnergyPremise(String energyPremise)
	{
		this.energyPremise = energyPremise;
	}

	/**
	 * Sets the meterLocation.
	 * @param meterLocation The meterLocation to set
	 */
	public void setMeterLocation(String meterLocation)
	{
		this.meterLocation = meterLocation;
	}

}
