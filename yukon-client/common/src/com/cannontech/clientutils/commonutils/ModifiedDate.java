package com.cannontech.clientutils.commonutils;

/**
 * Insert the type's description here.
 * Creation date: (6/14/00 11:08:03 AM)
 * @author: 
 */
import com.cannontech.clientutils.CommonUtils;

public class ModifiedDate extends java.util.Date 
{
/**
 * ModifiedDate constructor comment.
 */
public ModifiedDate() {
	super();
}
/**
 * ModifiedDate constructor comment.
 * @param year int
 * @param month int
 * @param date int
 */
public ModifiedDate(int year, int month, int date) {
	super(year, month, date);
}
/**
 * ModifiedDate constructor comment.
 * @param year int
 * @param month int
 * @param date int
 * @param hrs int
 * @param min int
 */
public ModifiedDate(int year, int month, int date, int hrs, int min) {
	super(year, month, date, hrs, min);
}
/**
 * ModifiedDate constructor comment.
 * @param year int
 * @param month int
 * @param date int
 * @param hrs int
 * @param min int
 * @param sec int
 */
public ModifiedDate(int year, int month, int date, int hrs, int min, int sec) {
	super(year, month, date, hrs, min, sec);
}
/**
 * ModifiedDate constructor comment.
 * @param date long
 */
public ModifiedDate(long date) {
	super(date);
}
/**
 * ModifiedDate constructor comment.
 * @param s java.lang.String
 */
public ModifiedDate(String s) {
	super(s);
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/00 11:08:21 AM)
 */
public String getDateString() 
{
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM-dd-yyyy");

	return formatter.format( this );
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/00 11:08:21 AM)
 */
public String getTimeString() 
{
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm:ss");

	return formatter.format( this );
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/00 11:08:21 AM)
 */
public String toString() 
{
	return CommonUtils.formatDate( this );
}
}
