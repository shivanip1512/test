package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class WLT_40HeaderRecord0001 implements BillingRecordBase 
{
	private String identNumber = null;
	private String sortCode = "0001";
	private java.util.Vector pulseTotalVector = null;
	private java.util.Vector pulseMultiplierVector = null;
	private java.util.Vector unitOfMeasureCodeVector = null;
	private Integer translationMode = new Integer(0);
	private Integer lopIndicator = new Integer(1);
	private Integer originalRecordedIntervalsPerHour = null;
	private Integer daysPerRecording = null;
	private String applicationCode = "  ";
	private String cartridgeNumber = "      ";
	private Integer segmentedIntervalsPerHour = null;
	private Integer requestedOutputIntervalsPerHour = null;
	private java.util.GregorianCalendar dstChange = null;
	private String dstType = "N";
	private String padding1 = "9999999999";
	private String editFlag = "0";
	private String padding2 = "99999999999999999";
/**
 * WLT_40HeaderRecord0001 constructor comment.
 */
public WLT_40HeaderRecord0001() {
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

	if( getPulseTotalVector() == null )
	{
		for(int i=0;i<24;i++)
		{
			writeToFile.append('0');
		}
	}
	else
	{
		String tempString = "";
		for(int i=0;i<getPulseTotalVector().size();i++)
		{
			for(int j=0;j<(8-((Integer)getPulseTotalVector().get(i)).toString().length());j++)
			{
				tempString = tempString.concat("0");
			}
			tempString = tempString.concat( ((Integer)getPulseTotalVector().get(i)).toString() );
		}
		writeToFile.append( tempString );
		for(int i=0;i<24-tempString.length();i++)
			writeToFile.append("0");
	}

	if( getPulseMultiplierVector() == null )
	{
		for(int i=0;i<30;i++)
		{
			writeToFile.append('0');
		}
	}
	else
	{
		String tempString = "";
		for(int i=0;i<getPulseMultiplierVector().size();i++)
		{
			Double tempPulseMultiplierDouble = new Double(((Double)getPulseMultiplierVector().get(i)).doubleValue() * 1000000000);
			String tempPulseMultiplierString = Integer.toString(tempPulseMultiplierDouble.intValue());
			for(int j=0;j<10-tempPulseMultiplierString.length();j++)
			{
				tempString = tempString.concat("0");
			}
			tempString = tempString.concat( tempPulseMultiplierString );
		}
		writeToFile.append( tempString );
		for(int i=0;i<30-tempString.length();i++)
			writeToFile.append("0");
	}

	if( getUnitOfMeasureCodeVector() == null )
	{
		for(int i=0;i<8;i++)
		{
			writeToFile.append('0');
		}
	}
	else
	{
		String tempString = "";
		for(int i=0;i<getUnitOfMeasureCodeVector().size();i++)
		{
			for(int j=0;j<(2-((Integer)getUnitOfMeasureCodeVector().get(i)).toString().length());j++)
			{
				tempString = tempString.concat("0");
			}
			tempString = tempString.concat( ((Integer)getUnitOfMeasureCodeVector().get(i)).toString() );
		}
		writeToFile.append( tempString );
		for(int i=0;i<8-tempString.length();i++)
			writeToFile.append("0");
	}

	writeToFile.append( getTranslationMode() );
	writeToFile.append( getLopIndicator() );

	for(int i=0;i<(2-getOriginalRecordedIntervalsPerHour().toString().length());i++)
		writeToFile.append('0');
	writeToFile.append( getOriginalRecordedIntervalsPerHour() );

	for(int i=0;i<(3-getDaysPerRecording().toString().length());i++)
		writeToFile.append('0');
	writeToFile.append( getDaysPerRecording() );

	writeToFile.append( getApplicationCode() );
	writeToFile.append( getCartridgeNumber() );

	for(int i=0;i<(2-getSegmentedIntervalsPerHour().toString().length());i++)
		writeToFile.append('0');
	writeToFile.append( getSegmentedIntervalsPerHour() );

	for(int i=0;i<(2-getRequestedOutputIntervalsPerHour().toString().length());i++)
		writeToFile.append('0');
	writeToFile.append( getRequestedOutputIntervalsPerHour() );

	if( getDstChange() == null )
	{
		writeToFile.append( "0000000000" );
	}
	else
	{
		java.text.SimpleDateFormat timestampFormatter = new java.text.SimpleDateFormat("MMddyyHHmm");
		writeToFile.append( timestampFormatter.format(getDstChange().getTime()) );
	}

	writeToFile.append( getDstType() );
	writeToFile.append( getPadding1() );
	writeToFile.append( getEditFlag() );
	writeToFile.append( getPadding2() );

	writeToFile.append("\r\n");
	return writeToFile.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.String
 */
public java.lang.String getApplicationCode() {
	return applicationCode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.String
 */
public java.lang.String getCartridgeNumber() {
	return cartridgeNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDaysPerRecording() {
	return daysPerRecording;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getDstChange() {
	return dstChange;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.String
 */
public java.lang.String getDstType() {
	return dstType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.String
 */
public java.lang.String getEditFlag() {
	return editFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:06:46 AM)
 * @return java.lang.String
 */
public java.lang.String getIdentNumber() {
	return identNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLopIndicator() {
	return lopIndicator;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOriginalRecordedIntervalsPerHour() {
	return originalRecordedIntervalsPerHour;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.String
 */
public java.lang.String getPadding1() {
	return padding1;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.String
 */
public java.lang.String getPadding2() {
	return padding2;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:19:04 PM)
 * @return java.util.Vector
 */
public java.util.Vector getPulseMultiplierVector() {
	return pulseMultiplierVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:20:18 PM)
 * @return java.util.Vector
 */
public java.util.Vector getPulseTotalVector() {
	return pulseTotalVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRequestedOutputIntervalsPerHour() {
	return requestedOutputIntervalsPerHour;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSegmentedIntervalsPerHour() {
	return segmentedIntervalsPerHour;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.String
 */
public java.lang.String getSortCode() {
	return sortCode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getTranslationMode() {
	return translationMode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:19:04 PM)
 * @return java.util.Vector
 */
public java.util.Vector getUnitOfMeasureCodeVector() {
	return unitOfMeasureCodeVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newApplicationCode java.lang.String
 */
public void setApplicationCode(java.lang.String newApplicationCode) {
	applicationCode = newApplicationCode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newCartridgeNumber java.lang.String
 */
public void setCartridgeNumber(java.lang.String newCartridgeNumber) {
	cartridgeNumber = newCartridgeNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newDaysPerRecording java.lang.Integer
 */
public void setDaysPerRecording(java.lang.Integer newDaysPerRecording) {
	daysPerRecording = newDaysPerRecording;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newDstChange java.util.GregorianCalendar
 */
public void setDstChange(java.util.GregorianCalendar newDstChange) {
	dstChange = newDstChange;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newDstType java.lang.String
 */
public void setDstType(java.lang.String newDstType) {
	dstType = newDstType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newEditFlag java.lang.String
 */
public void setEditFlag(java.lang.String newEditFlag) {
	editFlag = newEditFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:06:46 AM)
 * @param newIdentNumber java.lang.String
 */
public void setIdentNumber(java.lang.String newIdentNumber) {
	identNumber = newIdentNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newLopIndicator java.lang.Integer
 */
public void setLopIndicator(java.lang.Integer newLopIndicator) {
	lopIndicator = newLopIndicator;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newOriginalRecordedIntervalsPerHour java.lang.Integer
 */
public void setOriginalRecordedIntervalsPerHour(java.lang.Integer newOriginalRecordedIntervalsPerHour) {
	originalRecordedIntervalsPerHour = newOriginalRecordedIntervalsPerHour;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newPadding1 java.lang.String
 */
public void setPadding1(java.lang.String newPadding1) {
	padding1 = newPadding1;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newPadding2 java.lang.String
 */
public void setPadding2(java.lang.String newPadding2) {
	padding2 = newPadding2;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:19:04 PM)
 * @param newPulseMultiplierVector java.util.Vector
 */
public void setPulseMultiplierVector(java.util.Vector newPulseMultiplierVector) {
	pulseMultiplierVector = newPulseMultiplierVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:20:18 PM)
 * @param newPulseTotalVector java.util.Vector
 */
public void setPulseTotalVector(java.util.Vector newPulseTotalVector) {
	pulseTotalVector = newPulseTotalVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newRequestedOutputIntervalsPerHour java.lang.Integer
 */
public void setRequestedOutputIntervalsPerHour(java.lang.Integer newRequestedOutputIntervalsPerHour) {
	requestedOutputIntervalsPerHour = newRequestedOutputIntervalsPerHour;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newSegmentedIntervalsPerHour java.lang.Integer
 */
public void setSegmentedIntervalsPerHour(java.lang.Integer newSegmentedIntervalsPerHour) {
	segmentedIntervalsPerHour = newSegmentedIntervalsPerHour;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newSortCode java.lang.String
 */
public void setSortCode(java.lang.String newSortCode) {
	sortCode = newSortCode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 2:12:30 PM)
 * @param newTranslationMode java.lang.Integer
 */
public void setTranslationMode(java.lang.Integer newTranslationMode) {
	translationMode = newTranslationMode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:19:04 PM)
 * @param newUnitOfMeasureCodeVector java.util.Vector
 */
public void setUnitOfMeasureCodeVector(java.util.Vector newUnitOfMeasureCodeVector) {
	unitOfMeasureCodeVector = newUnitOfMeasureCodeVector;
}
}
