package com.cannontech.export.record;

/**
 * Insert the type's description here.
 * Creation date: (3/27/2002 12:33:38 PM)
 * @author: 
 */
public class CSVBillingRecord implements RecordBase
{
	private static java.text.SimpleDateFormat CURTAIL_DATE_FORMAT = new java.text.SimpleDateFormat("dd-MMM");
	private static java.text.SimpleDateFormat INTERVAL_FORMAT = new java.text.SimpleDateFormat("HH:mm");
	private static java.text.DecimalFormat DECIMAL_FORMAT = new java.text.DecimalFormat("#########0.00");

	private String customerName;
	private String energyDebtor;
	private String energyPremise;
	private String curtailOffer;
	private String meterLocation;
	private String curtailDate;
	private String curtailPeriodInterval;
	private Double curtailRate;
	private Double hdl;
	private Double scl;
	private Double adl;
	private Double nonComplianceFeeRate;	//curtailRate * 1.25 (or some dynamic number)
	private Double hdl_adl; //hdl - adl
	private Character compliant; //hdl_adl_greaterthan_scl + hdl_adl_equal_scl = 1 (1, 0)
	private Double nonComplianceLevel; //compliant = 0 ( scl - hdl_adl, 0)
	private Double curtailLevel; //compliant = 1 (scl, scl - non_compliance_level)
	private Double curtailEnergyCredits; //curtail_level * curtail_rate/4
	private Double nonComplianceFee; // -non_compliance_level * non_compliance_fee_rate/4
	private Double netCredits; //curtail_energy_credits * non_compliance_fee

	private final double PUC_CONSTANT_RATE = 1.25;
	private static Character delimiter = new Character('|');

	private static String[] columnHeadings =
	{
		"CustomerName",
		"Energy Debtor #",
		"Energy Premise #",
		"Curtail. Offer#",
		"Meter Location",
		"Curtail. Date",
		"15-min Curtail. Period Ending",
		"Curtail. Rate ($/kwh)",
		"HDL (kw)",
		"SCL (kw)",
		"ADL (kw)",
		"Non-Compliance Fee Rate ($/kwh)",
		"HDL - ADL (kw)",
		"Compliant",
		"Non-Compliance Level (kw)",
		"Curtail. Level (kw)",
		"Curtail.Evergy Credits ($)",
		"Non-Compliance Fee ($)",
		"Net Credits ($)" 
	};
	
	/**
	 * CSVBillingRecord constructor comment.
	 */
	public CSVBillingRecord() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/21/2002 1:11:49 PM)
	 * @return java.lang.String
	 * @param record com.cannontech.export.DBPurgeRecord
	 */
	public String dataToString()
	{		
		String dataString = new String( getCustomerName() + getDelimiter().toString());
		
		dataString += getEnergyDebtor() + getDelimiter().toString();
	
		dataString += getEnergyPremise() + getDelimiter().toString();
	
		dataString += getCurtailOffer() + getDelimiter().toString();
	
		dataString += getMeterLocation() + getDelimiter().toString();
	
		dataString += getCurtailDate() + getDelimiter().toString();
		
		dataString += getCurtailPeriodInterval() + getDelimiter().toString();
		
		dataString += DECIMAL_FORMAT.format(getCurtailRate().doubleValue()) + getDelimiter().toString();
		
		dataString += getHDL() + getDelimiter().toString();
		
		dataString += getSCL() + getDelimiter().toString();
		
		dataString += getADL() + getDelimiter().toString();
		
		dataString += getNonComplianceFeeRate() + getDelimiter().toString();
		
		dataString += getHDL_ADL() + getDelimiter().toString();
		
		dataString += getCompliant() + getDelimiter().toString();
		
		dataString += getNonComplianceLevel() + getDelimiter().toString();
		
		dataString += getCurtailLevel() + getDelimiter().toString();
	
		dataString += getCurtailEnergyCredits() + getDelimiter().toString();
	
		dataString += getNonComplianceFee() + getDelimiter().toString();
			
		dataString += getNetCredits() + "\r\n";
		
		return dataString;
	}
	
	/**
	 * Returns the adl.
	 * @return Double
	 */
	public Double getADL()
	{
		return adl;
	}
	
	/**
	 * Return columnHeadings as a comma separated String.
	 * @return String
	 */
	public static  String getColumnHeadingsString()
	{
		if( columnHeadings.length > 0)
		{
			String dataString = new String(columnHeadings[0]);
			
			for (int i = 1; i < columnHeadings.length; i++)
			{
				dataString += ',' + columnHeadings[i];
			}
	
			dataString += "\r\n";
			return dataString;
		}
		else
			return null;
	}
	
	/**
	 * Return compliant.
	 * If compliant is null, set compliant using hdl_adl >= scl.
	 * @return Character
	 */
	public Character getCompliant()
	{
		if (compliant == null)
		{
			if( getHDL_ADL().doubleValue() >= getSCL().doubleValue())
			{
				compliant = new Character('Y');
			}
			else
			{
				compliant = new Character('N');
			}
		}
		return compliant;
	}
	
	/**
	 * Returns the curtailDate.
	 * @return String
	 */
	public String getCurtailDate()
	{
		return curtailDate;
	}
	
	/**
	 * Returns the curtailEnergyCredits.
	 * If curtailEnergyCredits is null, set using curtailLevel * curtailRate / 4
	 * @return Double
	 */
	public Double getCurtailEnergyCredits()
	{
		if (curtailEnergyCredits == null)
		{
			curtailEnergyCredits = new Double(getCurtailLevel().doubleValue() * 
										getCurtailRate().doubleValue() / 4);
		}
		return curtailEnergyCredits;
	}
	
	/**
	 * Returns the curtailLevel.
	 * If curtailLevel is null, set using scl - nonComplianceLevel.
	 * @return Double
	 */
	public Double getCurtailLevel()
	{
		if (curtailLevel == null)
		{
			curtailLevel = new Double (getSCL().doubleValue() - getNonComplianceLevel().doubleValue());
		}
		return curtailLevel;
	}

	/**
	 * Returns the curtailOffer.
	 * @return String
	 */
	public String getCurtailOffer()
	{
		return curtailOffer;
	}
	
	/**
	 * Returns the curtailPeriodInterval.
	 * @return String
	 */
	public String getCurtailPeriodInterval()
	{
		return curtailPeriodInterval;
	}

	/**
	 * Returns the curtailRate.
	 * @return Double
	 */
	public Double getCurtailRate() 
	{
		return curtailRate;
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
	 * Returns the delimiter.
	 * @return Character
	 */
	public static Character getDelimiter()
	{
		return delimiter;
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
	 * Returns the hdl.
	 * @return Double
	 */
	public Double getHDL()
	{
		return hdl;
	}
	
	/**
	 * Returns the hdl_adl. (Baseline - Actual)
	 * If hdl_adl is null, set using hdl - adl.
	 * @return Double
	 */
	public Double getHDL_ADL()
	{
		if ( hdl_adl == null)
		{
			hdl_adl = new Double (getHDL().doubleValue() - getADL().doubleValue());
		}
		return hdl_adl;
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
	 * Returns the netCredits.
	 * If netCredits is null, set using curtailEnergyCredis + nonComplianceFee.
	 * @return Double
	 */
	public Double getNetCredits()
	{
		if (netCredits == null)
		{
			netCredits = new Double(getCurtailEnergyCredits().doubleValue() + 
										getNonComplianceFee().doubleValue());
		}
		return netCredits;
	}
	
	/**
	 * Returns the nonComplianceFee.
	 * If nonComplianceFee is null, set using (0 - nonComplianceLevel) * nonComplianceFeeRate / 4.
	 * @return Double
	 */
	public Double getNonComplianceFee()
	{
		if( nonComplianceFee == null)
		{
			nonComplianceFee = new Double ( (0 - getNonComplianceLevel().doubleValue()) *
								getNonComplianceFeeRate().doubleValue() / 4 );
		}
		return nonComplianceFee;
	}
	
	/**
	 * Returns the nonComplianceFeeRate.
	 * If nonComplianceFeeRate is null, set using curtailRate * PUC_CONSTANT_RATE.
	 * @return Double
	 */
	public Double getNonComplianceFeeRate()
	{
		if (nonComplianceFeeRate == null)
		{
			nonComplianceFeeRate = new Double (getCurtailRate().doubleValue() * PUC_CONSTANT_RATE);
		}
		return nonComplianceFeeRate;
	}
	
	/**
	 * Returns the nonComplianceLevel.
	 * If nonComplianceLeve is null, set 0 when compliant='Y' else set using scl - hdl_adl.
	 * @return Double
	 */
	public Double getNonComplianceLevel()
	{
		if( nonComplianceLevel == null)
		{
			if( getCompliant().compareTo(new Character('Y')) ==  0 )
			{
				nonComplianceLevel = new Double(0);
			}
			else
			{
				nonComplianceLevel = new Double(getSCL().doubleValue() - getHDL_ADL().doubleValue());
			}
		}
		
		return nonComplianceLevel;
	}
	
	/**
	 * Returns the scl.
	 * @return Double
	 */
	public Double getSCL()
	{
		return scl;
	}
	
	/**
	 * Sets the adl.
	 * @param newADL java.lang.Double
	 */
	public void setADL(Double newADL)
	{
		adl = newADL;
	}
	
	/**
	 * Sets the curtailDate.
	 * Formats newCurtailDate using CURTAIL_DATE_FORMAT.
	 * @param newCurtailDate java.util.GregorianCalendar
	 */
	public void setCurtailDate(java.util.GregorianCalendar newCurtailDate)
	{
		curtailDate = CURTAIL_DATE_FORMAT.format(newCurtailDate.getTime());
	}
	
	/**
	 * Sets the curtailOffer.
	 * @param newCurtailOffer java.lang.String
	 */
	public void setCurtailOffer(String newCurtailOffer)
	{
		curtailOffer = newCurtailOffer;
	}
	
	/**
	 * Sets the curtailPeriodInterval.
	 * @param newCurtailPeriodInterval java.lang.String
	 */
	public void setCurtailPeriodInterval(String newCurtailPeriodInterval)
	{
		curtailPeriodInterval = newCurtailPeriodInterval;
	}
	
	/**
	 * Sets the curtailPeriodInterval.
	 * Formats newCurtailPeriodInterval using INTERVAL_FORMAT.
	 * @param newCurtailPeriodInterval java.util.GregorianCalendar
	 */
	public void setCurtailPeriodInterval(java.util.GregorianCalendar newCurtailPeriodInterval)
	{
		curtailPeriodInterval = INTERVAL_FORMAT.format(newCurtailPeriodInterval.getTime());
	}
	
	/**
	 * Sets the curtailRate as (newCurtailRate * .01)
	 * CurtailRate is stored in Cents and needs to be displayed as $/kwh
	 * @param newCurtailRate java.lang.Double
	 */
	public void setCurtailRate(Double newCurtailRate)
	{
		curtailRate = new Double(newCurtailRate.doubleValue() * .01);
	}
	
	/**
	 * Sets the customerName.
	 * @param newCustomerName java.lang.String
	 */
	public void setCustomerName(String newCustomerName)
	{
		customerName = newCustomerName;
	}
	
	/**
	 * Sets the celimiter.
	 * @param newDelimiter java.lang.Character
	 */
	public void setDelimiter(Character newDelimiter)
	{
		delimiter = newDelimiter;
	}
	
	/**
	 * Sets the cnergyDebtor.
	 * @param newEnergyDebtor java.lang.String
	 */
	public void setEnergyDebtor(String newEnergyDebtor)
	{
		energyDebtor = newEnergyDebtor;
	}
	
	/**
	 * Sets the energyPremise.
	 * @param newEnergyPremise java.lang.String
	 */
	public void setEnergyPremise(String newEnergyPremise)
	{
		energyPremise = newEnergyPremise;
	}
	
	/**
	 * Sets the hdl.
	 * @param newHDL java.lang.Double
	 */
	public void setHDL(Double newHDL)
	{
		hdl = newHDL;
	}
	
	/**
	 * Sets the meterLocation.
	 * @param newMeterLocation java.lang.String
	 */
	public void setMeterLocation(String newMeterLocation)
	{
		meterLocation = newMeterLocation;
	}
	
	/**
	 * Sets the scl.
	 * @param newSCL java.lang.Double
	 */
	public void setSCL(Double newSCL)
	{
		scl = newSCL;
	}
}
