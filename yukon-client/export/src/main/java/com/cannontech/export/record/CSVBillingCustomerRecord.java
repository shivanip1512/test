package com.cannontech.export.record;

/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 2:52:49 PM)
 * @author: 
 */
public class CSVBillingCustomerRecord implements RecordBase
{
	private String customerName;
	private Integer customerID;
	private String meterLocation;
	private String accountNumber;
	private Integer baselinePointId;
	private Integer curtailPointId;
	private Double pdl;
	
	private static Character delimiter = new Character('|');
	/**
	 * CSVBillingCustomerRecord constructor comment.
	 */
	public CSVBillingCustomerRecord() {
		super();
	}
	/**
	 * CSVBillingCustomerRecord constructor comment.
	 */
	public CSVBillingCustomerRecord(String custName, Integer custID, String meterLoc,
									String actNum, Integer baselinePt, Integer curtailPt, Double pdl)
	{
		super();
		setCustomerName(custName);
		setCustomerID(custID);
		setMeterLocation(meterLoc);
		setAccountNumber(actNum);
		setBaselinePointId(baselinePt);
		setCurtailPointId(curtailPt);
		setPDL(pdl);
	}
	/**
	 * @see com.cannontech.export.record.RecordBase#dataToString()
	 * This is an extension of CSVBillingRecord, but is does not need to return anything.
	 * Returns null
	 */
	public String dataToString()
	{		
		String dataString = new String("CUSTOMER: " + getCustomerName() + "\n");
		
		dataString += "Account: " + getAccountNumber() + "\n";	
			
		dataString += "Meter: " + getMeterLocation() + "\n";

		dataString += "PDL: " + getPDL() + "\r\n";
	
		return dataString;
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
	 * Sets the meterLocation.
	 * @param meterLocation The meterLocation to set
	 */
	public void setMeterLocation(String meterLocation)
	{
		this.meterLocation = meterLocation;
	}

	/**
	 * @return
	 */
	public Double getPDL()
	{
		return pdl;
	}

	/**
	 * @param double1
	 */
	public void setPDL(Double pdl)
	{
		this.pdl = pdl;
	}

	/**
	 * @return
	 */
	public Character getDelimiter()
	{
		return delimiter;
	}

	/**
	 * @param character
	 */
	public void setDelimiter(Character character)
	{
		delimiter = character;
	}

	/**
	 * @return
	 */
	public Integer getCustomerID()
	{
		return customerID;
	}

	/**
	 * @param integer
	 */
	public void setCustomerID(Integer integer)
	{
		customerID = integer;
	}

	/**
	 * @return
	 */
	public String getAccountNumber()
	{
		return accountNumber;
	}

	/**
	 * @param string
	 */
	public void setAccountNumber(String string)
	{
		accountNumber = string;
	}

}
