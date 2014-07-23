package com.cannontech.billing.record;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class NCDC_Handheld implements BillingRecordBase
{
	private String location = null;		//25
	private String acctNumber = null;		//12
	private String meterNumber = null;	//10
	private Double kwhReading = new Double(0);	//7
	private Double kwReading = new Double(0);		//6.5
	private Double kvaReading = new Double(0);	//6.5
	private String readDate = null;		//yymmdd
	private String readTime = null;		//hhmmss
	private String commentCode = null;	//2
	private String tou = null;			//1
	
	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyyyMMdd");
	private static java.text.SimpleDateFormat TIME_FORMAT = new java.text.SimpleDateFormat("HHmmss");
	private static java.text.DecimalFormat KW_KVA_FORMAT_6v5 = new java.text.DecimalFormat("000000.00000");
	private static java.text.DecimalFormat KWH_FORMAT_7 = new java.text.DecimalFormat("0000000");
	
	/**
	 * Constructor for NCDC_Handheld.
	 */
	public NCDC_Handheld()
	{
		super();
	}

	/**
	 * @see com.cannontech.billing.record.BillingRecordBase#dataToString()
	 */
	public String dataToString()
	{
		StringBuffer writeToFile = new StringBuffer();
	
		writeToFile.append( getLocation());	//length is 25 from input file
		for ( int i = 0; i < (25 - getLocation().length()); i++)
		{
			writeToFile.append(" ");
		}

		writeToFile.append( getAcctNumber());	//length is 12 from input file
		for ( int i = 0; i < (12 - getAcctNumber().length()); i++)
		{
			writeToFile.append(" ");
		}
		
		writeToFile.append( getMeterNumber());
		for ( int i = 0; i < (10 - getMeterNumber().length()); i++)
		{
			writeToFile.append(" ");
		}
		
		//Rounding Mode is not taken into affect
		writeToFile.append(KWH_FORMAT_7.format(getKwhReading()));
		
		//Rounding Mode is not taken into affect
		writeToFile.append(KW_KVA_FORMAT_6v5.format(getKwReading()));

		//Rounding Mode is not taken into affect
		writeToFile.append(KW_KVA_FORMAT_6v5.format(getKvaReading()));

		writeToFile.append(getReadDate());

		writeToFile.append(getReadTime());
		
		writeToFile.append(getCommentCode());
		
		writeToFile.append(getTou());
	
		writeToFile.append("\r\n");
		
		return writeToFile.toString();
	}
	
	/**
	 * @return boolean
	 * @param o java.lang.Object
	 */
	public boolean equals(Object o) 
	{
		return ( (o != null) &&
				   (o instanceof NCDC_Handheld) &&
				   ((NCDC_Handheld)o).getMeterNumber().equalsIgnoreCase(getMeterNumber()) );
	}

	/**
	 * Returns the acctNumber.
	 * @return String
	 */
	public String getAcctNumber()
	{
		return acctNumber;
	}

	/**
	 * Returns the commentCode.
	 * @return Integer
	 */
	public String getCommentCode()
	{
		if( commentCode == null)
			return "00";
		return commentCode;
	}

	/**
	 * Returns the kvaReading.
	 * @return Double
	 */
	public Double getKvaReading()
	{
		return kvaReading;
	}

	/**
	 * Returns the kwhReading.
	 * @return Double
	 */
	public Double getKwhReading()
	{
		return kwhReading;
	}

	/**
	 * Returns the kwReading.
	 * @return Double
	 */
	public Double getKwReading()
	{
		return kwReading;
	}

	/**
	 * Returns the location.
	 * @return String
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * Returns the meterNumber.
	 * @return String
	 */
	public String getMeterNumber()
	{
		return meterNumber;
	}

	/**
	 * Returns the readDate.
	 * @return String
	 */
	public String getReadDate()
	{
		return readDate;
	}

	/**
	 * Returns the readTime.
	 * @return String
	 */
	public String getReadTime()
	{
		return readTime;
	}

	/**
	 * Returns the tou.
	 * @return Integer
	 */
	public String getTou()
	{
		if (tou == null)
			return "1";
		return tou;
	}

	/**
	 * Sets the acctNumber.
	 * @param acctNumber The acctNumber to set
	 */
	public void setAcctNumber(String acctNumber)
	{
		this.acctNumber = acctNumber;
	}

	/**
	 * Sets the commentCode.
	 * @param commentCode The commentCode to set
	 */
	public void setCommentCode(String commentCode)
	{
		this.commentCode = commentCode;
	}

	/**
	 * Sets the kvaReading.
	 * @param kvaReading The kvaReading to set
	 */
	public void setKvaReading(Double kvaReading)
	{
		this.kvaReading = kvaReading;
	}

	/**
	 * Sets the kwhReading.
	 * @param kwhReading The kwhReading to set
	 */
	public void setKwhReading(Double kwhReading)
	{
		this.kwhReading = kwhReading;
	}

	/**
	 * Sets the kwReading.
	 * @param kwReading The kwReading to set
	 */
	public void setKwReading(Double kwReading)
	{
		this.kwReading = kwReading;
	}

	/**
	 * Sets the location.
	 * @param location The location to set
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}

	/**
	 * Sets the meterNumber.
	 * @param meterNumber The meterNumber to set
	 */
	public void setMeterNumber(String meterNumber)
	{
		this.meterNumber = meterNumber;
	}

	/**
	 * Sets the readDate.
	 * @param readDateTimestamp The readDate to set
	 */
	public void setReadDate(java.sql.Timestamp readDateTimestamp)
	{
		java.util.Date d = new java.util.Date(readDateTimestamp.getTime());
		readDate = DATE_FORMAT.format(d);
	}

	/**
	 * Sets the readTime.
	 * @param readTimeTimestamp The readTime to set
	 */
	public void setReadTime(java.sql.Timestamp readTimeTimestamp)
	{
		java.util.Date d = new java.util.Date(readTimeTimestamp.getTime());
		readTime = TIME_FORMAT.format(d);
	}

	/**
	 * Sets the tou.
	 * @param tou The tou to set
	 */
	public void setTou(String tou)
	{
		this.tou = tou;
	}

}
