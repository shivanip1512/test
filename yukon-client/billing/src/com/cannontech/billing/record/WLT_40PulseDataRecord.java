package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
public class WLT_40PulseDataRecord implements BillingRecordBase 
{
	private String identNumber = null;
	private Integer sortCode = null;
	private java.util.Vector dataStatusZonesVector = null;
/**
 * WLT_40PulseDataRecord constructor comment.
 */
public WLT_40PulseDataRecord() {
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

	for(int i=0;i<4-getSortCode().toString().length();i++)
		writeToFile.append('0');
	writeToFile.append( getSortCode() );

	for(int i=0;i<getDataStatusZonesVector().size();i++)
	{
		String tempDataStatusZoneString = (String)getDataStatusZonesVector().get(i);
		for(int j=0;j<5-tempDataStatusZoneString.length();j++)
			writeToFile.append('0');
		writeToFile.append( tempDataStatusZoneString );
	}

	writeToFile.append("\r\n");
	return writeToFile.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 10:57:34 AM)
 * @return java.util.Vector
 */
public java.util.Vector getDataStatusZonesVector() {
	return dataStatusZonesVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:05:58 AM)
 * @return java.lang.String
 */
public java.lang.String getIdentNumber() {
	return identNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 10:57:34 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSortCode() {
	return sortCode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 10:57:34 AM)
 * @param newDataStatusZonesVector java.util.Vector
 */
public void setDataStatusZonesVector(java.util.Vector newDataStatusZonesVector) {
	dataStatusZonesVector = newDataStatusZonesVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:05:58 AM)
 * @param newIdentNumber java.lang.String
 */
public void setIdentNumber(java.lang.String newIdentNumber) {
	identNumber = newIdentNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 10:57:34 AM)
 * @param newSortCode java.lang.Integer
 */
public void setSortCode(java.lang.Integer newSortCode) {
	sortCode = newSortCode;
}
}
