package com.cannontech.clientutils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Insert the type's description here.
 * Creation date: (2/11/00 1:18:48 PM)
 * @author: 
 */

public final class CommonUtils 
{
   // states
	public static final int NORMAL_STATE = 0x00;
	public static final int INIT_WITH_DEFAULT_STATE = 0x01;
	public static final int INIT_WITH_LAST_VALUE_STATE = 0x02;
	public static final int INIT_UNKNOWN_STATE = 0x04;     // Has not been initialized
	public static final int SET_BY_EXTERN_APP_STATE = 0x08;
/**
 * Insert the method's description here.
 * Creation date: (4/19/00 3:54:35 PM)
 * Version: <version>
 * @param columnLocation int
 */
public static String createString(Object value) 
{
	if( value == null )
		return "";
	else
		return value.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 4:44:11 PM)
 * @return java.lang.String
 * @param original java.lang.String
 * @param amount int
 */
public static String format3CharMonth( int monthNumber )
{
	if( monthNumber < 0 || monthNumber > 11 )
		return "NUL";

	switch( monthNumber )
	{
		case 0:
		return "JAN";
		case 1:
		return "FEB";
		case 2:
		return "MAR";
		case 3:
		return "APR";
		case 4:
		return "MAY";
		case 5:
		return "JUN";
		case 6:
		return "JUL";
		case 7:
		return "AUG";
		case 8:
		return "SEP";
		case 9:
		return "OCT";
		case 10:
		return "NOV";
		case 11:
		return "DEC";

		default:
		return "NUL";
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/20/00 11:03:51 AM)
 * Version: <version>
 * @return java.lang.String
 * @param date java.sql.Date
 */
public static String formatDate(java.util.Date date) 
{
	if( date == null )
		return "Null";
		
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

	return formatter.format( date );
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 5:20:10 PM)
 */
public static String formatDecimalPlaces( double value, int decPlaces )
{
	java.text.DecimalFormat doubleToLong = new java.text.DecimalFormat();
	doubleToLong.setParseIntegerOnly( true );

	String pattern = null;
	
	if( decPlaces == 0 )
		pattern = new String("0");
	else
	{
		pattern = new String("0.");
				
		for( int i = 0; i < decPlaces; i++ )
			pattern += "0";
	}

	//apply the pattern
	doubleToLong.applyPattern(pattern);
	
	// By not returning a Double object, the column class becomes a String 
	return doubleToLong.format( value );		
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 4:44:11 PM)
 * @return java.lang.String
 * @param original java.lang.String
 * @param amount int
 */
public static String formatMonthString( int monthNumber )
{
	if( monthNumber < 0 || monthNumber > 11 )
		throw new IllegalArgumentException( monthNumber + " is not a valid month number");

	switch( monthNumber )
	{
		case 0:
		return "January";
		case 1:
		return "February";
		case 2:
		return "March";
		case 3:
		return "April";
		case 4:
		return "May";
		case 5:
		return "June";
		case 6:
		return "July";
		case 7:
		return "August";
		case 8:
		return "September";
		case 9:
		return "October";
		case 10:
		return "November";
		case 11:
		return "December";

		default:
		return "NUL";
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 4:44:11 PM)
 * @return java.lang.String
 * @param original java.lang.String
 * @param amount int
 */
public static String formatString(String original, int amount) 
{
	if( original == null )
	{
		String temp = new String();
		
		for( int i = 0; i < amount; i++ )
			temp +=	" ";

		return temp;
	}	
		
	String buffer = new String( original );
	
	if( buffer.length() == amount )
		return buffer;
	else if( buffer.length() > amount )
		return buffer.substring( 0, amount );
	else
	{
		for( int i = buffer.length(); i < amount; i++ )
			buffer += " ";

		return buffer;
	}
	
}
/**
 * This method will return the java.awt.Dialog associated with a component
 * If no parent dialog is found null will be returned
 * MAKE SURE A DIALOG OR JDIALOG IS THERE OR NULL IS RETURNED!!!
 */
public final static java.awt.Dialog getParentDialog(java.awt.Component comp) 
{	
	while( comp != null && !( comp instanceof java.awt.Dialog) )
	{
		comp = comp.getParent();
	}

	return (java.awt.Dialog) comp;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/00 1:19:38 PM)
 * @return java.lang.String[]
 * @param delimitor char
 * @param strings java.lang.String[]
 */
public static String stringDelimitor(String delimitor, Object[] strings)
{
	if( strings == null )
		return null;
		
	String delimitedString = new String();
		
	for( int i = 0; i < strings.length; i++ )
	{
		delimitedString += strings[i].toString();

		if( i != (strings.length - 1) )
			delimitedString += delimitor;
	}	
	
	return delimitedString;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/00 1:19:38 PM)
 * @return java.lang.String[]
 * @param delimitor char
 * @param strings java.lang.String[]
 */
public static String stringDelimitor(String delimitor, String[] strings) 
{
	if( strings == null )
		return null;
		
	String delimitedString = new String();
		
	for( int i = 0; i < strings.length; i++ )
	{
		delimitedString += strings[i];

		if( i != (strings.length - 1) )
			delimitedString += delimitor;
	}	
	
	return delimitedString;
}
/**
 * This method was created in VisualAge.
 * @param source int
 * @param dest int
 */
public static void swap( java.util.Vector vector, int source, int dest) {

	Object temp = new Object();

	if ( source != dest &&
		 source >= 0 && source < vector.size() &&
		 dest >= 0 && dest < vector.size() )
	{

		temp = vector.elementAt( source );
		vector.setElementAt( vector.elementAt( dest ), source );
		vector.setElementAt( temp, dest );
	}
	
}

//default date for DB is 1990-01-01 00:00:00.000
public static Date getDefaultStartTime() {
    Calendar cal = new GregorianCalendar(1990, Calendar.JANUARY, 1, 0, 0, 0);
    return cal.getTime();
}
}
