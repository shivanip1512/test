package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 5:32:56 PM)
 * @author: 
 */
//Use this class if you want to have any Record that is just a String
//   too bad you cant extend String here!!!
public class StringRecord implements BillingRecordBase 
{
	private String data = null;
/**
 * StringRecord constructor comment.
 */
protected StringRecord() {
	super();
}
/**
 * StringRecord constructor comment.
 */
public StringRecord( String value ) 
{
	super();
	setData( value );
}
/**
 * Converts data in a StringFormat to a formatted StringBuffer for stream use.
 * Creation date: (5/24/00 10:58:48 AM)
 * @return java.lang.String
 */
public final String dataToString() 
{
	return getData();
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2001 5:48:12 PM)
 * @return java.lang.String
 */
public java.lang.String getData() {
	return data;
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2001 5:48:12 PM)
 * @param newData java.lang.String
 */
public void setData(java.lang.String newData) {
	data = newData;
}
}
