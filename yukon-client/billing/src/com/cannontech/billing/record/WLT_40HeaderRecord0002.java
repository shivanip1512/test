package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class WLT_40HeaderRecord0002 implements BillingRecordBase 
{
	private String identNumber = null;
	private String sortCode = "0002";
	private String name = "";
	private String address = "";
	private String accountNumber = "";
	private java.util.Vector pulseOffsetVector = null;
	private String recorderType = "  ";
	private String classOfAccount = "    ";
	private Double ptRatio = null;
	private Double ctRatio = null;
	private String padding = "99999999999999";
/**
 * WLT_40HeaderRecord0002 constructor comment.
 */
public WLT_40HeaderRecord0002() {
	super();
}
/**
 * Converts data in a WLT_40Format to a formatted StringBuffer for stream use.
 * Creation date: (5/24/00 10:58:48 AM)
 * @return java.lang.String
 */
public final String dataToString() 
{
	StringBuffer writeToFile = new StringBuffer();
	writeToFile.append( getIdentNumber() );
	for(int i=0;i<(20-getIdentNumber().length());i++)
		writeToFile.append(' ');
	writeToFile.append( getSortCode() );

	writeToFile.append( getName() );
	for(int i=0;i<(20-getName().length());i++)
		writeToFile.append(' ');

	writeToFile.append( getAddress() );
	for(int i=0;i<(20-getAddress().length());i++)
		writeToFile.append(' ');

	writeToFile.append( getAccountNumber() );
	for(int i=0;i<(20-getAccountNumber().length());i++)
		writeToFile.append(' ');

	if( getPulseOffsetVector() == null )
	{
		for(int i=0;i<24;i++)
		{
			writeToFile.append('0');
		}
	}
	else
	{
		String tempString = "";
		for(int i=0;i<getPulseOffsetVector().size();i++)
		{
			Double tempPulseOffsetDouble = new Double(((Double)getPulseOffsetVector().get(i)).doubleValue() * 10000000);
			String tempPulseOffsetString = Integer.toString(tempPulseOffsetDouble.intValue());
			for(int j=0;j<(8-tempPulseOffsetString.length());j++)
			{
				tempString = tempString.concat("0");
			}
			tempString = tempString.concat( tempPulseOffsetString );
		}
		writeToFile.append( tempString );
		for(int i=0;i<24-tempString.length();i++)
			writeToFile.append("0");
	}

	writeToFile.append( getRecorderType() );
	writeToFile.append( getClassOfAccount() );

	if( getPtRatio() == null )
	{
		writeToFile.append( "00000000" );
	}
	else
	{
		Double tempPTRatioDouble = new Double(getPtRatio().doubleValue() * 10000000);
		for(int i=0;i<(8-Integer.toString(tempPTRatioDouble.intValue()).length());i++)
			writeToFile.append('0');
		writeToFile.append( Integer.toString(tempPTRatioDouble.intValue()) );
	}

	if( getCtRatio() == null )
	{
		writeToFile.append( "00000000" );
	}
	else
	{
		Double tempCTRatioDouble = new Double(getCtRatio().doubleValue() * 10000000);
		for(int i=0;i<(8-Integer.toString(tempCTRatioDouble.intValue()).length());i++)
			writeToFile.append('0');
		writeToFile.append( Integer.toString(tempCTRatioDouble.intValue()) );
	}

	writeToFile.append( getPadding() );

	writeToFile.append("\r\n");
	return writeToFile.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @return java.lang.String
 */
public java.lang.String getAccountNumber() {
	return accountNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @return java.lang.String
 */
public java.lang.String getAddress() {
	return address;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @return java.lang.String
 */
public java.lang.String getClassOfAccount() {
	return classOfAccount;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @return java.lang.Double
 */
public java.lang.Double getCtRatio() {
	return ctRatio;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:06:29 AM)
 * @return java.lang.String
 */
public java.lang.String getIdentNumber() {
	return identNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @return java.lang.String
 */
public java.lang.String getPadding() {
	return padding;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @return java.lang.Double
 */
public java.lang.Double getPtRatio() {
	return ptRatio;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2002 2:17:51 PM)
 * @return java.util.Vector
 */
public java.util.Vector getPulseOffsetVector() {
	return pulseOffsetVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @return java.lang.String
 */
public java.lang.String getRecorderType() {
	return recorderType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @return java.lang.String
 */
public java.lang.String getSortCode() {
	return sortCode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @param newAccountNumber java.lang.String
 */
public void setAccountNumber(java.lang.String newAccountNumber) {
	accountNumber = newAccountNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @param newAddress java.lang.String
 */
public void setAddress(java.lang.String newAddress) {
	address = newAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @param newClassOfAccount java.lang.String
 */
public void setClassOfAccount(java.lang.String newClassOfAccount) {
	classOfAccount = newClassOfAccount;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @param newCtRatio java.lang.Double
 */
public void setCtRatio(java.lang.Double newCtRatio) {
	ctRatio = newCtRatio;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:06:29 AM)
 * @param newIdentNumber java.lang.String
 */
public void setIdentNumber(java.lang.String newIdentNumber) {
	identNumber = newIdentNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @param newPadding java.lang.String
 */
public void setPadding(java.lang.String newPadding) {
	padding = newPadding;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @param newPtRatio java.lang.Double
 */
public void setPtRatio(java.lang.Double newPtRatio) {
	ptRatio = newPtRatio;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2002 2:17:51 PM)
 * @param newPulseOffsetVector java.util.Vector
 */
public void setPulseOffsetVector(java.util.Vector newPulseOffsetVector) {
	pulseOffsetVector = newPulseOffsetVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @param newRecorderType java.lang.String
 */
public void setRecorderType(java.lang.String newRecorderType) {
	recorderType = newRecorderType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 3:34:33 PM)
 * @param newSortCode java.lang.String
 */
public void setSortCode(java.lang.String newSortCode) {
	sortCode = newSortCode;
}
}
