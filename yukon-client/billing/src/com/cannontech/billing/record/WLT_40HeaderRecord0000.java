package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class WLT_40HeaderRecord0000 implements BillingRecordBase 
{
	private String identNumber = null;
	private String sortCode = "0000";
	private String rejectCode = "0";
	private java.util.GregorianCalendar translationDateAndDayOfWeek = new java.util.GregorianCalendar();
	private java.util.GregorianCalendar startTime = null;
	private java.util.GregorianCalendar stopTime = null;
	private Integer totalIntervals = null;
	private Integer predictedIntervals = null;
	private Integer missingIntervals = null;
	private Integer falseIntervals = null;
	private java.util.Vector startMeterReadingVector = null;
	private java.util.Vector endMeterReadingVector = null;
	private java.util.Vector meterMultiplierVector = null;
/**
 * WLT_40HeaderRecord0000 constructor comment.
 */
public WLT_40HeaderRecord0000() {
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
	writeToFile.append( getRejectCode() );

	java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("MMddyy");
	writeToFile.append( dateFormatter.format(getTranslationDateAndDayOfWeek().getTime()) );
	writeToFile.append( getTranslationDateAndDayOfWeek().get(java.util.Calendar.DAY_OF_WEEK) );

	writeToFile.append( dateFormatter.format(getStartTime().getTime()) );
	writeToFile.append( "0001" );//beginning of start day
	java.util.GregorianCalendar tempGreg = new java.util.GregorianCalendar();
	tempGreg.setTime(getStopTime().getTime());
	tempGreg.add(java.util.Calendar.DATE,-1);
	writeToFile.append( dateFormatter.format(tempGreg.getTime()) );
	writeToFile.append( "2400" );//ending of stop day

	for(int i=0;i<(5-getTotalIntervals().toString().length());i++)
		writeToFile.append('0');
	writeToFile.append( getTotalIntervals() );

	for(int i=0;i<(5-getPredictedIntervals().toString().length());i++)
		writeToFile.append('0');
	writeToFile.append( getPredictedIntervals() );

	for(int i=0;i<(5-getMissingIntervals().toString().length());i++)
		writeToFile.append('0');
	writeToFile.append( getMissingIntervals() );

	for(int i=0;i<(5-getFalseIntervals().toString().length());i++)
		writeToFile.append('0');
	writeToFile.append( getFalseIntervals() );

	if( getStartMeterReadingVector() == null )
	{
		for(int i=0;i<24;i++)
		{
			writeToFile.append('0');
		}
	}
	else
	{
		String tempString = "";
		for(int i=0;i<getStartMeterReadingVector().size();i++)
		{
			for(int j=0;j<(8-((Integer)getStartMeterReadingVector().get(i)).toString().length());j++)
			{
				tempString = tempString.concat("0");
			}
			tempString = tempString.concat( ((Integer)getStartMeterReadingVector().get(i)).toString() );
		}
		writeToFile.append( tempString );
		for(int i=0;i<24-tempString.length();i++)
			writeToFile.append("0");
	}

	if( getEndMeterReadingVector() == null )
	{
		for(int i=0;i<24;i++)
		{
			writeToFile.append('0');
		}
	}
	else
	{
		String tempString = "";
		for(int i=0;i<getEndMeterReadingVector().size();i++)
		{
			for(int j=0;j<(8-((Integer)getEndMeterReadingVector().get(i)).toString().length());j++)
			{
				tempString = tempString.concat("0");
			}
			tempString = tempString.concat( ((Integer)getEndMeterReadingVector().get(i)).toString() );
		}
		writeToFile.append( tempString );
		for(int i=0;i<24-tempString.length();i++)
			writeToFile.append("0");
	}

	if( getMeterMultiplierVector() == null )
	{
		for(int i=0;i<24;i++)
		{
			writeToFile.append('0');
		}
	}
	else
	{
		String tempString = "";
		for(int i=0;i<getMeterMultiplierVector().size();i++)
		{
			for(int j=0;j<(8-((Integer)getMeterMultiplierVector().get(i)).toString().length());j++)
			{
				tempString = tempString.concat("0");
			}
			tempString = tempString.concat( ((Integer)getMeterMultiplierVector().get(i)).toString() );
		}
		writeToFile.append( tempString );
		for(int i=0;i<24-tempString.length();i++)
			writeToFile.append("0");
	}

	writeToFile.append("\r\n");
	return writeToFile.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:10:30 PM)
 * @return java.util.Vector
 */
public java.util.Vector getEndMeterReadingVector() {
	return endMeterReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getFalseIntervals() {
	return falseIntervals;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:07:04 AM)
 * @return java.lang.String
 */
public java.lang.String getIdentNumber() {
	return identNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:10:30 PM)
 * @return java.util.Vector
 */
public java.util.Vector getMeterMultiplierVector() {
	return meterMultiplierVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMissingIntervals() {
	return missingIntervals;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPredictedIntervals() {
	return predictedIntervals;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @return java.lang.String
 */
public java.lang.String getRejectCode() {
	return rejectCode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @return java.lang.String
 */
public java.lang.String getSortCode() {
	return sortCode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:10:30 PM)
 * @return java.util.Vector
 */
public java.util.Vector getStartMeterReadingVector() {
	return startMeterReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getStartTime() {
	return startTime;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getStopTime() {
	return stopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getTotalIntervals() {
	return totalIntervals;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 1:34:31 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getTranslationDateAndDayOfWeek() {
	return translationDateAndDayOfWeek;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:10:30 PM)
 * @param newEndMeterReadingVector java.util.Vector
 */
public void setEndMeterReadingVector(java.util.Vector newEndMeterReadingVector) {
	endMeterReadingVector = newEndMeterReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @param newFalseIntervals java.lang.Integer
 */
public void setFalseIntervals(java.lang.Integer newFalseIntervals) {
	falseIntervals = newFalseIntervals;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:07:04 AM)
 * @param newIdentNumber java.lang.String
 */
public void setIdentNumber(java.lang.String newIdentNumber) {
	identNumber = newIdentNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:10:30 PM)
 * @param newMeterMultiplierVector java.util.Vector
 */
public void setMeterMultiplierVector(java.util.Vector newMeterMultiplierVector) {
	meterMultiplierVector = newMeterMultiplierVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @param newMissingIntervals java.lang.Integer
 */
public void setMissingIntervals(java.lang.Integer newMissingIntervals) {
	missingIntervals = newMissingIntervals;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @param newPredictedIntervals java.lang.Integer
 */
public void setPredictedIntervals(java.lang.Integer newPredictedIntervals) {
	predictedIntervals = newPredictedIntervals;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @param newRejectCode java.lang.String
 */
public void setRejectCode(java.lang.String newRejectCode) {
	rejectCode = newRejectCode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @param newSortCode java.lang.String
 */
public void setSortCode(java.lang.String newSortCode) {
	sortCode = newSortCode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 4:10:30 PM)
 * @param newStartMeterReadingVector java.util.Vector
 */
public void setStartMeterReadingVector(java.util.Vector newStartMeterReadingVector) {
	startMeterReadingVector = newStartMeterReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @param newStartTime java.util.GregorianCalendar
 */
public void setStartTime(java.util.GregorianCalendar newStartTime) {
	startTime = newStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @param newStopTime java.util.GregorianCalendar
 */
public void setStopTime(java.util.GregorianCalendar newStopTime) {
	stopTime = newStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 11:24:05 AM)
 * @param newTotalIntervals java.lang.Integer
 */
public void setTotalIntervals(java.lang.Integer newTotalIntervals) {
	totalIntervals = newTotalIntervals;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2002 1:34:31 PM)
 * @param newTranslationDateAndDayOfWeek java.util.GregorianCalendar
 */
public void setTranslationDateAndDayOfWeek(java.util.GregorianCalendar newTranslationDateAndDayOfWeek) {
	translationDateAndDayOfWeek = newTranslationDateAndDayOfWeek;
}
}
