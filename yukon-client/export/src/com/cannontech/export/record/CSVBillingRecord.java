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
	private String servicePt;
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
	//private Integer hdl_adl_greaterthan_scl; //hdl - adl > scl (1, 0)
	//private Integer hdl_adl_equal_scl; //hdl - adl = scl (1, 0)
	private Integer compliant; //hdl_adl_greaterthan_scl + hdl_adl_equal_scl = 1 (1, 0)
	private Double nonComplianceLevel; //compliant = 0 ( scl - hdl_adl, 0)
	private Double curtailLevel; //compliant = 1 (scl, scl - non_compliance_level)
	private Double curtailEnergyCredits; //curtail_level * curtail_rate/4
	private Double nonComplianceFee; // -non_compliance_level * non_compliance_fee_rate/4
	private Double netCredits; //curtail_energy_credits * non_compliance_fee

	private static String[] columnHeadings =
	{
		"CustomerName",
		"Service Pt.#",
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
 * CSVBillingFormat constructor comment.
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
	
	String dataString = new String( getCustomerName() + ", ");
	
	dataString += getServicePt() + ", ";

	dataString += getCurtailOffer() + ", ";

	dataString += getMeterLocation() + ", ";

	dataString += getCurtailDate() + ", ";
	
	dataString += getCurtailPeriodInterval() + ", ";
	
	dataString += DECIMAL_FORMAT.format(getCurtailRate().doubleValue()) + ", ";
	
	dataString += getHDL() + ", ";
	
	dataString += getSCL() + ", ";
	
	dataString += getADL() + ", ";
	
	dataString += getNonComplianceFeeRate() + ", ";
	
	dataString += getHDL_ADL() + ", ";
	
	dataString += getCompliant() + ", ";
	
	dataString += getNonComplianceLevel() + ", ";
	
	dataString += getCurtailLevel() + ", ";

	dataString += getCurtailEnergyCredits() + ", ";

	dataString += getNonComplianceFee() + ", ";
		
	dataString += getNetCredits() + "\r\n";
	
	return dataString;
}
public Double getADL()
{
	return adl;
}
/**
 * Insert the method's description here.
 * Creation date: (4/1/2002 1:33:38 PM)
 * @return java.lang.String
 */
public static  String getColumnHeadingsString()
{
	if( columnHeadings.length > 0)
	{
		String dataString = new String(columnHeadings[0]);
		
		for (int i = 1; i < columnHeadings.length; i++)
		{
			dataString += ", " + columnHeadings[i];
		}

		dataString += "\r\n";
		return dataString;
	}
	else
		return null;
}
public Integer getCompliant()
{
	if (compliant == null)
	{
		if( getHDL_ADL().doubleValue() >= getSCL().doubleValue())
		{
			compliant = new Integer(1);
		}
		else
		{
			compliant = new Integer(0);
		}
	}
	return compliant;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 1:35:57 PM)
 * @return java.lang.String
 */
public String getCurtailDate()
{
	return curtailDate;
}
public Double getCurtailEnergyCredits()
{
	if (curtailEnergyCredits == null)
	{
		curtailEnergyCredits = new Double(getCurtailLevel().doubleValue() * 
									getCurtailRate().doubleValue() / 4);
	}
	return curtailEnergyCredits;
}
public Double getCurtailLevel()
{
	if (curtailLevel == null)
	{
		curtailLevel = new Double (getSCL().doubleValue() - getNonComplianceLevel().doubleValue());
	}
	return curtailLevel;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 1:35:33 PM)
 * @return java.lang.Integer
 */
public String getCurtailOffer()
{
	return curtailOffer;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 1:36:54 PM)
 * @return java.lang.String
 */
public String getCurtailPeriodInterval()
{
	return curtailPeriodInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 1:36:18 PM)
 * @return java.lang.String
 */
public Double getCurtailRate() 
{
	return curtailRate;
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
public Double getHDL()
{
	return hdl;
}
//Baseline - Actual
// - or -
// (HDL - ADL)
public Double getHDL_ADL()
{
	if ( hdl_adl == null)
	{
		hdl_adl = new Double (getHDL().doubleValue() - getADL().doubleValue());
	}
	return hdl_adl;
}
public String getMeterLocation()
{
	return meterLocation;
}
public Double getNetCredits()
{
	if (netCredits == null)
	{
		netCredits = new Double(getCurtailEnergyCredits().doubleValue() + 
									getNonComplianceFee().doubleValue());
	}
	return netCredits;
}
public Double getNonComplianceFee()
{
	if( nonComplianceFee == null)
	{
		nonComplianceFee = new Double ( (0 - getNonComplianceLevel().doubleValue()) *
							getNonComplianceFeeRate().doubleValue() / 4 );
	}
	return nonComplianceFee;
}
//CURTAIL RATE * (some PUC constant rate)

public Double getNonComplianceFeeRate()
{
	if (nonComplianceFeeRate == null)
	{
		nonComplianceFeeRate = new Double (getCurtailRate().doubleValue() * 1.25);
	}
	return nonComplianceFeeRate;
}
//If Compliant  then 0
//Else SCL - (HDL -ADL)
public Double getNonComplianceLevel()
{
	if( nonComplianceLevel == null)
	{
		if( getCompliant().intValue() > 0 )
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
public Double getSCL()
{
	return scl;
}
public String getServicePt()
{
	return servicePt;
}
public void setADL(Double newADL)
{
	adl = newADL;
}
public void setCurtailDate(java.util.GregorianCalendar newCurtailDate)
{
	curtailDate = CURTAIL_DATE_FORMAT.format(newCurtailDate.getTime());
}
public void setCurtailOffer(String newCurtailOffer)
{
	curtailOffer = newCurtailOffer;
}
	public void setCurtailPeriodInterval(String newCurtailPeriodInterval)
	{
		curtailPeriodInterval = newCurtailPeriodInterval;
	}
public void setCurtailPeriodInterval(java.util.GregorianCalendar newCurtailPeriodInterval)
{
	curtailPeriodInterval = INTERVAL_FORMAT.format(newCurtailPeriodInterval.getTime());
}
//This value is stored in Cents and needs to be displayed as $/kwh
public void setCurtailRate(Double newCurtailRate)
{
	curtailRate = new Double(newCurtailRate.doubleValue() * .01);
}
	public void setCustomerName(String newCustomerName)
	{
		customerName = newCustomerName;
	}
	public void setHDL(Double newHDL)
	{
		hdl = newHDL;
	}
public void setMeterLocation(String newMeterLocation)
{
	meterLocation = newMeterLocation;
}
	public void setSCL(Double newSCL)
	{
		scl = newSCL;
	}
public void setServicePt(String newServicePt)
{
	servicePt = newServicePt;
}
}
