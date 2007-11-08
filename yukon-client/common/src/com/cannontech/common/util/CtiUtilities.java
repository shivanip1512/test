package com.cannontech.common.util;
/**
 * This type was created in VisualAge.
 */
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.MenuComponent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.util.FileCopyUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.data.lite.LiteComparators;

public final class CtiUtilities 
{
	private static final String YUKON_HELP_PREFIX = "com/cannontech/help/";
    public static final int MAX_UTILITY_ID = 254;
	public static final int MAX_SECTION_ID = 255;
	public static final int NONE_ZERO_ID = 0;

	public static final double INVALID_MIN_DOUBLE = -1E30;
	public static final double INVALID_MAX_DOUBLE = 1E30;
	
    public static final String CELSIUS_CHARACTER = "C";
    public static final String FAHRENHEIT_CHARACTER = "F";
	
	public static final String COPYRIGHT = "Copyright (C)1999-2007 Cannon Technologies";
	
	public static final String USER_DIR = System.getProperty("user.home") + System.getProperty("file.separator");
	public static final String CURRENT_DIR = System.getProperty("user.dir") + System.getProperty("file.separator");
	
	public static final String STRING_NONE = "(none)";
	public static final String STRING_DEFAULT = "Default";
	public static final String STRING_DASH_LINE = "  ----";

	public static final Character trueChar = new Character('Y');
	public static final Character falseChar = new Character('N');
	
	public static final String TRUE_STRING = "true";
	public static final String FALSE_STRING = "false";
	
	public static final String ENABLED_STRING = "enabled";
	public static final String DISABLED_STRING = "disabled";
	
	//PAOExclusion functionID constants
	public static final String EXCLUSION_TIME_INFO = "TimeInfo";
	public static final Integer EXCLUSION_TIMING_FUNC_ID = new Integer(2);
	public static final String LM_SUBORDINATION_INFO = "LMMemberControl";
	public static final Integer LM_SUBORDINATION_FUNC_ID = new Integer(3);
	
	private static java.util.GregorianCalendar gc1990 = null;
	
	//a universal formatter for numbers
	private static DecimalFormat numberFormatter = null;
	
	// image names
    public static final URL CTISMALL_GIF =
            CtiUtilities.class.getResource("/ctismall.gif");
    public static final URL ALARM_AU =
        CtiUtilities.class.getResource("/alarm.au");
    
    
    
	// image extensions
	public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    		
	private static String temp;

	static
	{		
		/** Init our beginning of time here */
		gc1990 = new java.util.GregorianCalendar();
		gc1990.set( Calendar.YEAR, 1990 );
		gc1990.set( Calendar.DAY_OF_YEAR, 1 );
		gc1990.set( Calendar.HOUR, 0 );
		gc1990.set( Calendar.MINUTE, 0 );
		gc1990.set( Calendar.SECOND, 0 );

		try
		{
			temp = java.net.InetAddress.getLocalHost().getHostAddress().toString()
						+ ":" + Long.toHexString(System.currentTimeMillis());
		}
		catch( java.net.UnknownHostException e )
		{
			CTILogger.info("*** UnknownHostException occured, using (null) for the source in the base message class.");
		}


	}

	public static final String DEFAULT_MSG_SOURCE = temp;

	public static final String OUTPUT_FILE_NAME = CtiUtilities.getConfigDirPath() + "TDCOut.DAT";
    
    public static String[] timeZones = null;

	private static final class CTIPrintStackTraceExc extends Exception
	{
		public java.io.ByteArrayOutputStream byteStream = new java.io.ByteArrayOutputStream();
		public java.io.PrintStream printStream = new java.io.PrintStream( byteStream );

		public CTIPrintStackTraceExc() {
			super();
		}

	}

/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 5:09:08 PM)
 * @return java.lang.Object
 * @param s java.io.Serializable
 * @exception java.io.IOException The exception description.
 */
/* This method first sorts list of objects using the passed in comparator.
 *  Then, it uses a binary search to find the first instance of the key.
 *  If there is more elements that equal key, then the algorithm walks
 *  backwards through the sorted list until it reaches the beginning of
 *  occurences of key. Then each occurence of key is returned
 *  in a List.
 */
public static final java.util.List binarySearchRepetition(
	java.util.List listToBeSorted, 
	Object key, 
	java.util.Comparator comp,
	java.util.List destList )
{
	destList.clear();

	//do the sort and search here	
	java.util.Collections.sort( listToBeSorted, comp );
	int loc = java.util.Collections.binarySearch(
						listToBeSorted,
						key, //must have the needed ID set in this key that comp uses!!
						comp );

	int listSize = listToBeSorted.size();

	if( loc >= 0 ) //only loop if there is a found item
	{
		//walk back thru the list and make sure we 
		//  have the first occurence of the ID
		for (int j = (loc-1); j >= 0; j--)
			if( comp.compare(listToBeSorted.get(j), key) == 0 ) //is equal
				loc--;
			else
				break;

		//the element in the location loc SHOULD/MUST be an instance of 
		//   what we are looking for!
		for( ; loc < listSize; loc++ )
			if( comp.compare(listToBeSorted.get(loc), key) == 0 ) //is equal
			{
				destList.add( listToBeSorted.get(loc) );
			}
			else
				break;  //we have went past all elements since they are sorted,
						  //  get out of the loop

	}

	return destList;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 5:09:08 PM)
 * @return java.lang.Object
 * @param s java.io.Serializable
 * @exception java.io.IOException The exception description.
 */
/* This method does a deep copy on any Serializable object.
 *  If the object has references to other objects, thos referenced objects
 *  must be serializable also!! */
public static Object copyObject(Object s) throws java.io.IOException 
{
	if( s instanceof java.io.Serializable )
	{
	   java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
	   java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(bout);

	   out.writeObject(s);
	   out.flush();
	   byte[] b = bout.toByteArray();
	   out.close();

	   java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(b));

	   Object result = null;
	   try
	   {
	   	result = in.readObject();
	   }
	   catch( ClassNotFoundException e )
	   {
		   e.printStackTrace( System.out );
	   }
	   

	   in.close();

	   return result ;
	}
	else
		throw new java.io.NotSerializableException("The object '" + s + "' does not implement java.io.Serializable" );
}


/**
 * This method was created by Cannon Technologies Inc.
 * 
 * @param inDate Date
 */
public final static Integer dateToSeconds(java.util.Date inDate) {
	java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
	tempCal.setTime( inDate );

	Integer outInteger = new Integer( tempCal.get( java.util.Calendar.HOUR_OF_DAY ) * 3600 +
																		tempCal.get( java.util.Calendar.MINUTE ) * 60 +
																		tempCal.get( java.util.Calendar.SECOND ) );
	return outInteger;
}



public final static void setJComboBoxSelection( JComboBox box, Object value )
{
   if( box == null || value == null )
      return; //blah, fool!
      
   //look for the value, if not found add it and set it selected
   boolean found = false;
   for( int i = 0; i < box.getItemCount(); i++ )
   {
      if( box.getItemAt(i).equals(value) )
      {
         found = true;
         box.setSelectedItem( value );
         break;
      }
   }
   
   if( !found )
   {
      box.addItem( value );
      box.setSelectedItem( value );
   }
   
}


public static DecimalFormat getDecimalFormatter()
{
	if( numberFormatter == null )
		numberFormatter = new DecimalFormat();

	return numberFormatter;
}

/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 3:09:53 PM)
 * @return int
 */
/* Takes a string in the format of:
	   X-X,x,x,x,
   where - represents and inclusive set of numbers
   */
public static int[] decodeRangeIDString( String string, final int maxID ) 
{

	if( string != null )
	{
		try
		{
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(string, ",");
			NativeIntVector utilIds = new NativeIntVector(tokenizer.countTokens());
			//java.util.ArrayList utilIds = new java.util.ArrayList(tokenizer.countTokens());

			for( int i = 0; tokenizer.hasMoreElements(); i++ )
			{
				String val = tokenizer.nextToken().trim();

				if( val.indexOf("-") != -1 ) //we have a range of ints
				{
					java.util.StringTokenizer gTk = new java.util.StringTokenizer(val, "-");
					int beg = Integer.parseInt( gTk.nextToken().trim() );
					int end = Integer.parseInt( gTk.nextToken().trim() );
					
					for( int j = beg; j <= end && j <= maxID; j++ )
					{
						if( !utilIds.contains(j) && j > 0 && j <= maxID )
							utilIds.add( j );
					}

				}
				else  //we only have one int alone
				{
					int tmp = Integer.parseInt(val);
					if( !utilIds.contains(tmp) && tmp > 0 && tmp <= maxID )
						utilIds.add( tmp );
				}

			}

			int[] ids = (int[])utilIds.toArray();
			java.util.Arrays.sort( ids );

			return ids;
		}
		catch( Exception e ) //catch all!
		{
			com.cannontech.clientutils.CTILogger.info("*** Unable to parse UtilityID range string : " + string );
		}

	}
	
	return new int[0];
}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 3:16:28 PM)
 * @return java.lang.String
 * @param seconds int
 *
 * Returns a string in the the format of
 *			HH:mm or H:mm
 *	where HH is hours and mm is minutes.
 *	WARNING: The hours returned may exceed 23
 **/
public static String decodeSecondsToTime(int seconds) 
{
	int intHour = seconds / 3600;
	String hour = Integer.toString(intHour);
//	if( hour.length() <= 1 )
//		hour = "0" + hour;
		
	String minute = Integer.toString( (seconds - (intHour*3600)) / 60);
	if( minute.length() <= 1 )
		minute = "0" + minute;
	
	return hour + ":" + minute;
}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 3:09:53 PM)
 * @return int
 */
/* Takes a string in the format of:
	   HH:mm
   where HH is hours and mm is seconds
   */

public static int decodeStringToSeconds( String string ) 
{
	if( string == null || string.length() != 5 )
		return 0;
		
	int hour = Integer.parseInt(string.substring( 0, 2 )) * 3600;
	int minute = Integer.parseInt(string.substring( 3, 5 )) * 60;
	
	return hour + minute;
}


/**
 * Insert the method's description here.
 * Creation date: (8/10/00 10:32:13 AM)
 * @return java.util.GregorianCalendar
 * @param timeString java.lang.String
 */
public final static java.util.GregorianCalendar decodeTimeString(String timeString) 
{
	java.util.GregorianCalendar returnCalendar = new java.util.GregorianCalendar();
	String hourString = new String("0");
	String minuteString = new String("0");
	char currentChar;
	boolean minutesField = false;
	for(int i=0;i<timeString.length();i++)
	{
		currentChar = timeString.charAt(i);
		if( currentChar >= '0' && currentChar <= '9' && !minutesField )
			hourString = hourString.concat(String.valueOf(currentChar));
		else if( currentChar >= '0' && currentChar <= '9' && minutesField )
			minuteString = minuteString.concat(String.valueOf(currentChar));
		else if( currentChar == ':' )
			minutesField = true;
	}
	returnCalendar.set( java.util.GregorianCalendar.HOUR_OF_DAY, (new Integer(hourString)).intValue() );
	returnCalendar.set( java.util.GregorianCalendar.MINUTE, (new Integer(minuteString)).intValue() );
	return returnCalendar;
}


/**
 * Insert the method's description here.
 * Creation date: (8/10/00 10:32:13 AM)
 * @return java.util.GregorianCalendar
 * @param timeString java.lang.String
 */
public final static java.util.Date decodeTimeStringToDate(String timeString) 
{
	java.util.GregorianCalendar returnCalendar = new java.util.GregorianCalendar();
	String hourString = new String("0");
	String minuteString = new String("0");
	char currentChar;
	boolean minutesField = false;
	for(int i=0;i<timeString.length();i++)
	{
		currentChar = timeString.charAt(i);
		if( currentChar >= '0' && currentChar <= '9' && !minutesField )
			hourString = hourString.concat(String.valueOf(currentChar));
		else if( currentChar >= '0' && currentChar <= '9' && minutesField )
			minuteString = minuteString.concat(String.valueOf(currentChar));
		else if( currentChar == ':' )
			minutesField = true;
	}
	returnCalendar.set( java.util.GregorianCalendar.HOUR_OF_DAY, (new Integer(hourString)).intValue() );
	returnCalendar.set( java.util.GregorianCalendar.MINUTE, (new Integer(minuteString)).intValue() );
	
	return returnCalendar.getTime();
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param s Stack
 * @param o java.lang.Object
 */
private static boolean findPath(java.util.Stack s, Object o) {

	javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode) s.peek();

	if( node.getUserObject().equals(o) )
		return true;
	else
	{
		for( int i = 0; i < node.getChildCount(); i++ )
		{
			s.push( node.getChildAt(i) );

			if( findPath( s, o ) )
				return true;
			
			s.pop();
		}
	}	
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2001 10:00:50 AM)
 * @return java.util.GregorianCalendar
 */
public static java.util.GregorianCalendar get1990GregCalendar() {
	return gc1990;
}

/**
 * This method will return the java.awt.Frame associated with a component
 * If no parent frame is found null will be returned
 * @return String
 */
public final static String getCanonicalFile(String directory)
{
	try
	{
		java.io.File tempFile = new java.io.File(directory);
		return tempFile.getCanonicalPath().toString() +"\\";
	}
	catch(java.io.IOException ioe)
	{
		ioe.printStackTrace();
	}
     
	return directory;
}


/**
 * This method will return the java.awt.Frame associated with a component
 * If no parent frame is found null will be returned
 * @return String
 */
public final static String getCommandsDirPath()
{
     return CURRENT_DIR + "../commands/";
}


public final static String getConfigDirPath()
{
	return USER_DIR + "/";
}


/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 2:42:31 PM)
 * @return java.lang.String
 */
public final static String getDatabaseAlias() {
	return "yukon";
}

/**
 * Returns a registration string for the current running app
 * @return java.lang.String
 */
public final static String getAppRegistration()
{
	return
		getApplicationName() + "-" + 
		getUserIPAddress() + ":" +
		Integer.toHexString(Thread.currentThread().hashCode()) + "  (" +
		getUserName() + ")";
}

/**
 * This method was created in VisualAge.
 * @return javax.swing.JDesktopPane
 * @param comp Component
 */
public static final javax.swing.JDesktopPane getDesktopPane(java.awt.Component comp) {
	
	while( comp != null && !( comp instanceof javax.swing.JDesktopPane) )
	{
		comp = comp.getParent();
	}

	return (javax.swing.JDesktopPane) comp;
}


/**
 * This method will return the java.awt.Frame associated with a component
 * If no parent frame is found null will be returned
 * @return java.awt.Frame
 */
public final static String getExportDirPath()
{
	return getYukonBase() + "/Client/export/";
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public final static Character getFalseCharacter() {
	return falseChar;
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/00 2:01:01 PM)
 * @return java.lang.Integer
 * @param comboBox javax.swing.JComboBox
 */
public final static Integer getIntervalComboBoxSecondsValue(JComboBox comboBox) 
{
	return getIntervalSecondsValue( (String)comboBox.getSelectedItem() );
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/00 2:01:01 PM)
 * @return java.lang.Integer
 * @param selectedString String
 */
public final static Integer getIntervalSecondsValue(String selectedString) 
{
	Integer generic = null;
	Integer retVal = null;
	int multiplier = 1;

	if( selectedString == null )
	{
		retVal = new Integer(0);  //we have no idea, just use zero
	}
	else if( selectedString.toLowerCase().compareTo("daily") == 0)
	{
		generic = new Integer(86400);
		return generic;
	}
	else if( selectedString.toLowerCase().compareTo("weekly") == 0)
	{
		generic = new Integer(604800);
		return generic;
	}
	else if( selectedString.toLowerCase().compareTo("monthly") == 0)
	{
		generic = new Integer(2592000);
		return generic;
	}
	else if( selectedString.toLowerCase().indexOf("second") != -1 )
	{
		multiplier = 1;
	}
	else if( selectedString.toLowerCase().indexOf("minute") != -1 )
	{
		multiplier = 60;
	}
	else if( selectedString.toLowerCase().indexOf("hour") != -1 )
	{
		multiplier = 3600;
	}
	else if( selectedString.toLowerCase().indexOf("day") != -1 )
	{
		multiplier = 86400;
	}
	else
		multiplier = 0;  //we have no idea, just use zero
		
	try
	{
		int loc = selectedString.toLowerCase().indexOf(" ");
	
		retVal = new Integer( 
			multiplier * Integer.parseInt(
				          selectedString.toLowerCase().substring( 0, loc ) ));
	}
	catch( Exception e )
	{
		CTILogger.error( "Unable to parse combo box text string into seconds, using ZERO", e );
		retVal = new Integer(0);
	}

	return retVal;
}

public final static Integer getIntervalSecondsValueFromDecimal(String selectedString) 
{
	Double retVal = null;
	double multiplier = 1;

	if( selectedString == null )
	{
		retVal = new Double(0);  //we have no idea, just use zero
	}
	else if( selectedString.toLowerCase().indexOf("second") != -1 )
	{
		multiplier = 1;
	}
	else if( selectedString.toLowerCase().indexOf("minute") != -1 )
	{
		multiplier = 60;
	}
	else if( selectedString.toLowerCase().indexOf("hour") != -1 )
	{
		multiplier = 3600;
	}
	else if( selectedString.toLowerCase().indexOf("day") != -1 )
	{
		multiplier = 86400;
	}
	else
		multiplier = 0;  //we have no idea, just use zero
		
	try
	{
		int loc = selectedString.toLowerCase().indexOf(" ");
	
		retVal = new Double( 
			multiplier * Double.parseDouble(
						  selectedString.toLowerCase().substring( 0, loc ) ));
	}
	catch( Exception e )
	{
		CTILogger.error( "Unable to parse combo box text string into seconds, using ZERO", e );
		retVal = new Double(0);
	}

	return new Integer(retVal.intValue());
}

/**
 * Returns the directory log files should go to
 * Here is the search order:
 *  1) Uses System.getProperty("yukon.logdir") if found
 *  2) Uses \yukon\client\log if found, else creates yukon\client\log
 * 
 */
public final static String getLogDirPath()
{   
	//Logs go different places depending on whether we are running
	//as a client application or a server application
	final String fs = System.getProperty("file.separator");
	String clientLoc = getYukonBase() + fs + "client" + fs + "log" + fs;
	String logDir = System.getProperty("yukon.logdir");
	
	if(logDir != null)
	{
		return logDir;
	}
	else
	{
		return clientLoc;
	}
}

/**
 * Returns the base/home directory where yukon is installed.
 * From here we can assume the canonical yukon directory
 * structure.
 * @return
 */
public final static String getYukonBase() 
{
	final String fs = System.getProperty("file.separator");	
	
	//First try to use yukon.base
	String yukonBase = System.getProperty("yukon.base");
	if(yukonBase != null) 
	{
		return yukonBase;
	}
    
    // Next try to use the environment variable that is now available (thanks Sun)
    String envYukonBase = System.getenv("YUKON_BASE");
    if (envYukonBase != null) {
        envYukonBase = StringUtils.remove(envYukonBase, "\"");
        return envYukonBase;
    }
	
	//That failed, so...
	//Are we running inside tomcat? assume we live in yukon/server/web then
	//and calculate yukon base accordingly 
	final String catBase = System.getProperty("catalina.base");
	if(catBase != null) 
	{
		try 
		{
			File yb = new File(catBase + fs + ".." + fs + "..");			
			return yb.getCanonicalPath();
		}
		catch(IOException ioe) {} 

		//maybe we are in a development environment where we might not be running in a 
		//good yukon directory structure?
		return fs + "yukon";
	}
	
	//So, we must be a client running from the command line?
	//assume we are running from the yukon/client/bin directory
	try 
	{
		File yb = new File(".." + fs + "..");
		return yb.getCanonicalPath();
	}
	catch(IOException ioe) {}
	
	//Last resort, return the current directory
	try 
	{
		return new File(".").getCanonicalPath();
	}
	catch(IOException ioe) {}
	
	//total failure, doh!
	return fs + "yukon";
}

public static boolean isRunningAsWebApplication() {
    return (System.getProperty("catalina.base") != null);
}

/**
 * This method will return the name of this application.
 * If no name is found, a dummy string is returned.
 */
public final static String getApplicationName()
{
	if( System.getProperty("cti.app.name") == null ) {
        if (isRunningAsWebApplication()) {
            System.setProperty("cti.app.name", "Webserver");
        } else {
            System.setProperty("cti.app.name", "UnknownApplication");
        }
    }
    String appName = System.getProperty("cti.app.name");
    return appName;
}

/**
 * This method will return the java.awt.Dialog associated with a component
 * If no parent dialog is found null will be returned
 * @return java.awt.Dialog
 * @param comp java.awt.Component
 */
public final static java.awt.Dialog getParentDialog(java.awt.Component comp) {
	
	while( comp != null && !( comp instanceof java.awt.Dialog) )
	{
		comp = comp.getParent();
	}

	return (java.awt.Dialog) comp;
}

/**
 * This method will return the java.awt.Frame associated with a component
 * If no parent frame is found null will be returned
 * @return java.awt.Frame
 * @param comp java.awt.Component
 */
public final static java.awt.Frame getParentFrame(java.awt.Component comp) {
	
	while( comp != null && !( comp instanceof java.awt.Frame) )
	{
		comp = comp.getParent();
	}

	return (java.awt.Frame) comp;
}

/**
 * This method was created in VisualAge.
 * @return JInternalFrame
 * @param comp java.awt.Component
 */
public final static javax.swing.JInternalFrame getParentInternalFrame(Component comp) {
	
	while( comp != null && !( comp instanceof javax.swing.JInternalFrame) )
	{
		comp = comp.getParent();
	}

	return (javax.swing.JInternalFrame) comp;
}


/**
 * This method will return the java.awt.Window associated with a component
 * If no parent window is found null will be returned.
 * @return java.awt.Window
 * @param comp java.awt.Component
 */
public final static java.awt.Window getParentWindow(java.awt.Component comp) {
	
	while( comp != null && !( comp instanceof java.awt.Window) )
	{
		comp = comp.getParent();
	}

	return (java.awt.Window) comp;
}

/**
 * This method will return the javax.swing.JRootPane associated with a components parent
 * If no parent is found with a root panethen null will be returned
 * @return java.awt.Frame
 * @param comp java.awt.Component
 */
public final static javax.swing.JRootPane getParentRootPane(java.awt.Component comp) {
	
	while(comp != null)
	{
		if(comp instanceof javax.swing.JFrame) 
		{
			return ((javax.swing.JFrame) comp).getRootPane(); 
		}
		else 
		if(comp instanceof javax.swing.JApplet) 
		{
			return ((javax.swing.JApplet) comp).getRootPane();
		}
		else
		if(comp instanceof javax.swing.JDialog)
		{
			return ((javax.swing.JDialog) comp).getRootPane();
		}
	
		comp = comp.getParent();
	}

	return null;
}

/**
 * This method was created by Cannon Technologies Inc.
 * 
 * @param inDate Date
 */
/* This method creates a new CTIPrintStackTaceExc() instance and  *
 *  returns the current stack trace    */
public final static String getSTACK_TRACE()
{
	CTIPrintStackTraceExc e = new CTIPrintStackTraceExc();

	try
	{
		e.printStackTrace( e.printStream );
		e.byteStream.flush();
	}
	catch( java.io.IOException io )
	{
		io.printStackTrace(System.out);
		return null;
	}
	
	return e.byteStream.toString();
}


/**
 * This method was created in VisualAge.
 */
public final static String[] getStateAbbreviations()
{
	String[] s = {"AL","AK","AZ","AR","CA","CO","CT","DE","DC",
		"FL","GA","GU","HI","ID","IL","IN","IA","KS",
		"KY","LA","ME","MD","MA","MI","MN","MS","MO",
		"MT","NE","NV","NH","NJ","NM","NY","NC","ND",
		"OH","OK","OR","PA","PR","RI","SC","SD","TN",
		"TX","UT","VT","VA","VI","WA","WV","WI","WY" };

	return s;
}


/**
 * This method was created in VisualAge.
 * @return TreePath
 * @param tree javax.swing.JTree
 * @param o java.lang.Object
 */
public final static javax.swing.tree.TreePath getTreePath(javax.swing.JTree tree, Object o) {

	javax.swing.tree.DefaultMutableTreeNode rootNode = (javax.swing.tree.DefaultMutableTreeNode) tree.getModel().getRoot();

	java.util.Stack s = new java.util.Stack();
	s.push(rootNode);
	
	if( findPath( s, o ) )
	{
		java.util.Vector v = new java.util.Vector();

		while( !s.isEmpty() )
			v.insertElementAt( s.pop(), 0 );

		Object[] path = new Object[v.size()];

		v.copyInto( path );

		return new javax.swing.tree.TreePath(path);
	}
	else
		return null;	
}

/**
 * Returns valid US timezone strings.
 */
public static final String[] getTimeZones()
{
	if(timeZones == null)
    {
        String[] availableIDs = TimeZone.getAvailableIDs();
    	Vector retVals = new Vector(16);
    	
    	for (int i = 0; i < availableIDs.length; i++) {
    		String zone = availableIDs[i];
    		if ( (zone.matches("^US.*") || zone.matches("^Canada.*")) && !zone.matches("^US.Pacific-New") ) {
    			retVals.add( zone );
    		}
    	}
    	Collections.sort(retVals, LiteComparators.liteNameComparator);
    	availableIDs = new String[retVals.size()];
        timeZones = (String[])retVals.toArray(availableIDs);
    }
	return timeZones;
    
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public final static Character getTrueCharacter() {
	return trueChar;
}

public static char getBooleanCharacter(boolean value) {
    return value ? trueChar : falseChar;
}

/**
 * 
 * 
 */
public final static String getUserIPAddress()
{
	try
	{
		return java.net.InetAddress.getLocalHost().getHostAddress();
	}
	catch( java.net.UnknownHostException e )
	{
		return "(unknown)";
	}

}


/**
 * Returns the YukonUser name. This method use to return the
 * windows user name. 
 */
public final static String getUserName()
{
	if( ClientSession.getInstance().getUser() != null )
		return ClientSession.getInstance().getUser().getUsername();
	else
		return CtiUtilities.STRING_NONE;
	//return System.getProperty("user.name");
}


/**
 * This method is used to determine whether a given java.lang.Character
 * represents a boolean equal to true.  Use this to isolate the actual
 * characters that represent true or false.
 * @return boolean
 * @param c java.lang.Character
 */
public final static boolean isTrue(java.lang.Character c) {
	return trueChar.charValue() == Character.toUpperCase(c.charValue());
}

/**
 * This method is used to determine whether a given java.lang.Character
 * represents a boolean equal to true.  Use this to isolate the actual
 * characters that represent true or false.
 */
public final static boolean isTrue(char c) {
	return trueChar.charValue() == Character.toUpperCase(c);
}


/** 
 * Return true if the given string represents true.
 * Case insensitive
 * @param s String
 * @return boolean
*/
public final static boolean isTrue(String s) {
	return (TRUE_STRING.equalsIgnoreCase(s));
}

/**
 * Return true if the given string represents false
 * @param s
 * @return
 */
public final static boolean isFalse(String s) {
	return (s == null || FALSE_STRING.equalsIgnoreCase(s));
}

/**
 * Returns true of the given string represents enabled
 * @param enableStr
 * @return boolean
 */
public static final boolean isEnabled(String enableStr) {
	return (ENABLED_STRING.equalsIgnoreCase(enableStr));
}

/**
 * Returns true of the given string represents disabled
 * @param enableStr
 * @return boolean
 */
public static final boolean isDisabled(String disableStr) {
	return (DISABLED_STRING.equalsIgnoreCase(disableStr));
}

/**
 * Returns true of the given id is in the given array
 */
public static final boolean isInSet( int[] idSet, int id )
{
	if( idSet == null )
		return false;
   	
	for( int i = 0; i < idSet.length; i++ )
		if( idSet[i] == id )
			return true;

	return false;
}

/**
 * Returns true of the given id is in the given array
 */
public static final boolean isInSet( Integer[] idSet, int id )
{
    if( idSet == null )
        return false;
    
    for( int i = 0; i < idSet.length; i++ )
        if( idSet[i] == id )
            return true;

    return false;
}

/**
 * This method was created in VisualAge.
 * @param cBox javax.swing.JCheckBox
 * @param state java.lang.Integer
 */
public static void setCheckBoxState(javax.swing.JCheckBox cBox, Character state ) {

	char c =  Character.toLowerCase( state.charValue() );

	if( c == 'y' )
		cBox.setSelected(true);
	else
	if( c == 'n' )
		cBox.setSelected(false);
}


/**
 * This method was created in VisualAge.
 * @param cBox javax.swing.JCheckBox
 * @param state java.lang.Integer
 */
public static void setCheckBoxState(javax.swing.JCheckBox cBox, Integer state ) {

	if( state.intValue() == 0 )
		cBox.setSelected(false);
	else
		cBox.setSelected(true);
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:08:43 PM)
 * @param comboBox javax.swing.JComboBox
 * @param scanRateSecs int
 */
public static final void setIntervalComboBoxSelectedItem(JComboBox comboBox, double scanRateSecs) 
{
	String scanRateString = null;
	boolean found = false;

	//when we divide the scanRateSecs value, we must use a double formatted number
	// so we are returned a double value (Ex: getDecimalFormatter().format(scanRateSecs/60.0) 

	if( scanRateSecs < 60 )
	{
		scanRateString = getDecimalFormatter().format(scanRateSecs) + " second";
	}
	else if( scanRateSecs < 3600 )
	{
		scanRateString = getDecimalFormatter().format(scanRateSecs/60.0) + " minute";
	}
	else if( scanRateSecs < 86400 )
	{
		scanRateString = getDecimalFormatter().format(scanRateSecs/3600.0) + " hour";
	}
	else
	{
		if(scanRateSecs == 86400)
			scanRateString = "Daily";
		else if(scanRateSecs == 604800)
			scanRateString = "Weekly";
		else if(scanRateSecs == 2592000)
			scanRateString = "Monthly";
		else
			scanRateString = getDecimalFormatter().format(scanRateSecs/86400.0) + " day";
	}
	

	for(int i=0;i<comboBox.getModel().getSize();i++)
	{
		if( ((String)comboBox.getItemAt(i)).indexOf(scanRateString) != -1 )
		{
			comboBox.setSelectedIndex(i);
			found = true;
			break;
		}
	}

	if( !found )
	{
		comboBox.addItem( scanRateString );
		comboBox.setSelectedItem( scanRateString );
	}
}

//this is mainly for gear refresh rates
public static final void setIntervalComboBoxSelectedItem(JComboBox comboBox, JComboBox comboBox2, double scanRateSecs) 
{
	String scanRateString = null;
	String scanRateUnitString = null;
	boolean found = false;
	boolean unitsFound = false;

	//when we divide the scanRateSecs value, we must use a double formatted number
	// so we are returned a double value (Ex: getDecimalFormatter().format(scanRateSecs/60.0) 

	if( scanRateSecs < 3600 )
	{
		scanRateString = getDecimalFormatter().format(scanRateSecs/60.0);
		scanRateUnitString = "minutes";
	}
	else
	{
		scanRateString = getDecimalFormatter().format(scanRateSecs/3600.0);
		if(scanRateString.indexOf(".") != -1)
		{
			scanRateString = getDecimalFormatter().format(scanRateSecs/60.0);
			scanRateUnitString = "minutes";
		}
		else
			scanRateUnitString = "hours";
	}
	
	
	
	comboBox.setSelectedItem( scanRateString );
	comboBox2.setSelectedItem( scanRateUnitString );
	
}
/**
 * This method was created in VisualAge.
 * @param comboBox javax.swing.JComboBox
 * @param val java.lang.Object
 */
public final static void setSelectedInComboBox( javax.swing.JComboBox comboBox, Object val ) {

	int items = comboBox.getItemCount();
	boolean foundIt = false;

	for( int i = 0; i < items; i++ )
	{
		Object item = comboBox.getItemAt(i);

		if( item.equals( val ) )
		{
			comboBox.setSelectedIndex(i);
			foundIt = true;
			break;
		}
	}

	//Add it if we didn't find it
	if( foundIt == false )
	{
		comboBox.addItem( val );
		comboBox.setSelectedItem( val );
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public static final void showHelp( String helpFileName ) 
{
    try {
        // prepend classpath prefix to helpFile
        String helpFilePath = YUKON_HELP_PREFIX + helpFileName;

        // get reference
        ClassLoader classLoader = CtiUtilities.class.getClassLoader();
        InputStream helpContents = classLoader.getResourceAsStream(helpFilePath);
        if (helpContents == null) {
            throw new IOException("Couldn't find helpfile on classpath: " + helpFilePath);
        }

        // copy contents to temporary file
        File tempContents = File.createTempFile("yukon", helpFileName);
        tempContents.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempContents);
        
        FileCopyUtils.copy(helpContents, fos);
        CTILogger.info("Help contents copied out to: " + tempContents);

        String[] cmd = new String[2];
        cmd[0] = "hh.exe";
        cmd[1] = tempContents.getAbsolutePath();

        Runtime.getRuntime().exec( cmd );
    } catch( IOException e ) {
        CTILogger.info("Unable to display help for: " + helpFileName, e);
    }
}


/**
 * This method was created in VisualAge.
 */
public final static void startModal(javax.swing.JInternalFrame frame) {
	try {
			// can't use instanceof EventDispatchThread because the class isn't public
			if (Thread.currentThread().getClass().getName().endsWith("EventDispatchThread")) {
				EventQueue theQueue = java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue();
				while (frame.isVisible()) {
					// This is essentially the body of EventDispatchThread
					AWTEvent event = theQueue.getNextEvent();
					Object src = event.getSource();
					// can't call theQueue.dispatchEvent, so I pasted it's body here
					/*if (event instanceof ActiveEvent) {
					  ((ActiveEvent) event).dispatch();
					  } else */ if (src instanceof Component) {
						  ((Component) src).dispatchEvent(event);
					  } else if (src instanceof MenuComponent) {
						  ((MenuComponent) src).dispatchEvent(event);
					  } else {
						  System.err.println("unable to dispatch event: " + event);
					  }
				}
				com.cannontech.clientutils.CTILogger.info("here");
			} else
				while (frame.isVisible())
					frame.wait();
		} catch(InterruptedException e){}
}


/**
 * This method was created in VisualAge.
 */
public final static void stopModal(javax.swing.JInternalFrame frame) {
	frame.notifyAll();
}


/**
 * This method will return a string representing
 * a the date passed in.
 * @return String
 */
public static final String toDatabaseString(java.util.Date date)
{
	if( date == null )
		return null;

	java.text.SimpleDateFormat format = 
			new java.text.SimpleDateFormat("dd-MMM-yyyy");

	return format.format(date).toUpperCase();
}

/*
 * Get the extension of a file.
 */
public static String getExtension(java.io.File f) {
	String ext = null;
    String s = f.getName();
    
    int i = s.lastIndexOf('.');

    if (i > 0 &&  i < s.length() - 1) {
    	ext = s.substring(i+1).toLowerCase();
    }
    return ext;
 }
 
 public static void setLaF() {
 	try {
		javax.swing.UIManager.setLookAndFeel(
			javax.swing.UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
			e.printStackTrace(); /*Not much to do about*/
	}
 }





	/**
	 * Insert the method's description here.
	 * Creation date: (3/5/2001 11:32:59 AM)
	 * @return String
	 * 
	 * This method returns the String that represents the key_ as a method.
	 * We search for a method of the vlaue_ object that returns a String
	 * and has a the name:
	 *   get(key_);
	 * If anything goes wrong, we print out all possible getters that return
	 * a String and use the default_ as our value.
	 * At most we accept 1 getter method. The percent(%) sign is used as a
	 * token seperator.  key_ may look like this:
	 *   CBC %PAOName%
	 * A call to getPAOName() will replace the %PAOName%.
	 */
	public static String getReflectiveProperty( 
			final Object value_, String key_, final String default_ )
	{
		if( value_ == null )
			return default_;


		java.lang.reflect.Method[] methods = value_.getClass().getMethods();
      
		try
		{
			StringBuffer buf = new StringBuffer(key_);
			String methodName = methodName = buf.substring( key_.indexOf("%")+1, key_.lastIndexOf("%") );
         
			for( int i = 0; i < methods.length; i++ )
			{
				if( methods[i].getName().toLowerCase().startsWith("get") 
						&& methods[i].getReturnType().equals(String.class)
						&& methods[i].getName().toLowerCase().endsWith(methodName.toLowerCase()) )
				{
					String s = (String)methods[i].invoke( value_, (Object[]) null);
               
					buf.replace( key_.indexOf("%")+1, key_.lastIndexOf("%"), s );
               
					//remove all % signs
					while( buf.toString().indexOf("%") != -1 )
						buf.deleteCharAt( buf.toString().indexOf("%") );
                  
					return (buf.toString() == null ? default_ : buf.toString());
				}         
			}
		}
		catch( Exception e )
		{} //no biggy, print some info and use the default_ value


		/******************  ERROR HANDLING BELOW *****************/
		//oops we failed, list the properties for this reflective class
		com.cannontech.clientutils.CTILogger.info("*** PROPERTY REFLECTIVE TRANSLATION ERROR: " + key_ + " key/value not stored.");
		com.cannontech.clientutils.CTILogger.debug("Available REFLECTIVE properties for: " + value_.getClass().getName());

		for( int i = 0; i < methods.length; i++ )
		{
			if( methods[i].getName().toLowerCase().startsWith("get") 
					&& methods[i].getReturnType().equals(String.class) )
			{
				com.cannontech.clientutils.CTILogger.info( "   " +
						methods[i].getName().substring(3) );
			}         
		}
    
		return default_;
	}

	
/**
 * Returns a string with (best guess) html removed.
 * Useful for taking text from web pages and using it in java.
 * @param code
 * @return string
 */
public static String removeHTML( String code )
{
	if( code.indexOf("<") > -1 )	//something that looks like html exists...lets parse it!
	{
		StringBuffer tempBuff = new StringBuffer(code);
		JEditorPane tempPane = new JEditorPane("text/html", tempBuff.toString());
		try
		{
			int docLength = tempPane.getDocument().getLength();
			String text = tempPane.getDocument().getText(0, docLength);
			return text.trim();
		}
		catch (BadLocationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	return code;
}	

/**
 * Converts a temperature from/to Celsius or Fahrenheit.
 * @param temperature a temperature of the fromUnit scale
 * @param fromUnit the scale of the input, either FAHRENHEIT_CHARACTER or CELSIUS_CHARACTER
 * @param toUnit the scale of the output, either FAHRENHEIT_CHARACTER or CELSIUS_CHARACTER
 * @return a temperature of the toUnit scale equal to the input temperature
 */
public static double convertTemperature(double temperature, String fromUnit, String toUnit) {
    if (fromUnit.equals(toUnit)) {
        return temperature;
    } else if (fromUnit.equals(FAHRENHEIT_CHARACTER) && toUnit.equals(CELSIUS_CHARACTER)) {
        return (temperature - 32) / 9 * 5;
    } else if (fromUnit.equals(CELSIUS_CHARACTER) && toUnit.equals(FAHRENHEIT_CHARACTER)) {
        return (temperature * 9 / 5) + 32;
    } else {
        throw new IllegalArgumentException("Unknown fromUnit or toUnit, must specify either \"F\" or \"C\"");
    }
}

    public static long convertTemperature(long temperature, String fromUnit, String toUnit) {
        double dblTemperature = temperature;
        return Math.round(convertTemperature(dblTemperature, fromUnit, toUnit));
    }
    
    public static Integer[] ensureNotNull(Integer[] arr) {
        return (arr == null ? new Integer[0] : arr);
    }

    public static <T> Set<T> asSet(T... a) {
        Set<T> s = new HashSet<T>((int) (a.length/0.75f+1),0.75f);
        for (T t : a) {
            s.add(t);
        }
        return s;
    }
    
    public static String findConstantName(int i, Class<?> c) {
        Field[] declaredFields = c.getFields();
        for (int j = 0; j < declaredFields.length; j++) {
            Field field = declaredFields[j];
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                continue;
            }
            if (Integer.class.isAssignableFrom(field.getType())) {
                try {
                    Integer value = (Integer) field.get(null);
                    if (value.intValue() == i) {
                        return field.getName();
                    }
                } catch (IllegalArgumentException e) {
                    CTILogger.error(e);
                } catch (IllegalAccessException e) {
                    CTILogger.error(e);
                }
            }

            if (Integer.TYPE.isAssignableFrom(field.getType())) {
                try {
                    int value = field.getInt(null);
                    if (value == i) {
                        return field.getName();
                    }
                } catch (IllegalArgumentException e) {
                    CTILogger.error(e);
                } catch (IllegalAccessException e) {
                    CTILogger.error(e);
                }
            }
            
        }
        return Integer.toString(i);
    }
    
    /**
     * Gets the IP address for the client
     * @return The IP address of the client's computer
     */
    public static String getIPAddress() {
        
        //Get the Local interfaces using the NetworkInterface class
        Enumeration<NetworkInterface> ifEnum = null;
        
        //return value for client IP address
        try {
            ifEnum = NetworkInterface.getNetworkInterfaces();
            while(ifEnum.hasMoreElements()) {
                //get each NetworkInterface object
                NetworkInterface localIf = ifEnum.nextElement();

                //get the addresses of this interface
                Enumeration<InetAddress> ifAddrEnum = localIf.getInetAddresses();

                while(ifAddrEnum.hasMoreElements()){
                    InetAddress ifAddr = ifAddrEnum.nextElement();
                    if( !ifAddr.isLoopbackAddress() ) {
                        String ipAddress = ifAddr.getHostAddress();
                        return ipAddress;
                    }    
                }
            }    
        } catch (SocketException e) {
            CTILogger.error("Couldn't find an IP address for the client, returning null");
        } 
        return "unknown"; 
    }


    public static Throwable getRootCause(Throwable e) {
        Throwable rc = ExceptionUtils.getRootCause(e);
        if (rc == null) {
            rc = e;
        }
        return rc;
    }
    
    public static String getSystemInfoString() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        out.println("Yukon Version: " + VersionTools.getYUKON_VERSION());
        try {
            out.println("Local IP: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
        }
        out.println("getYukonBase(): " + getYukonBase());
        out.println("USER_TIMEZONE: " + SystemUtils.USER_TIMEZONE);
        out.println("USER_COUNTRY: " + SystemUtils.USER_COUNTRY);
        out.println("OS_ARCH: " + SystemUtils.OS_ARCH);
        out.println("OS_NAME: " + SystemUtils.OS_NAME);
        out.println("OS_VERSION: " + SystemUtils.OS_VERSION);
        out.println("JAVA_HOME: " + SystemUtils.JAVA_HOME);
        out.println("JAVA_VERSION: " + SystemUtils.JAVA_VERSION);
        out.println("JAVA_CLASS_PATH: " + SystemUtils.JAVA_CLASS_PATH);
        out.println("JAVA_LIBRARY_PATH: " + SystemUtils.JAVA_LIBRARY_PATH);
        out.println("JAVA_EXT_DIRS: " + SystemUtils.JAVA_EXT_DIRS);
        out.println("JAVA_ENDORSED_DIRS: " + SystemUtils.JAVA_ENDORSED_DIRS);
        out.println("JAVA_VM_NAME: " + SystemUtils.JAVA_VM_NAME);
        out.println("JAVA_VM_INPUT_ARGS: " + CtiUtilities.getJvmInputArgs());
        

        return sw.toString();
    }
    
    public static Collection<String> getAllJars(File base, String jarName)
            throws IOException {
        Collection<String> helper = new LinkedHashSet<String>();
        collectAllJars(base, jarName, helper);
        return helper;
    }

    private static void collectAllJars(File base, String jarName,
            Collection<String> jarList) throws IOException {
        File mainJarFile = new File(base, jarName);
        JarFile jar = null;
        try {
            jar = new JarFile(mainJarFile);
            Manifest manifest = jar.getManifest();
            if (manifest == null) {
                return;
            }
            Attributes mainAttributes = manifest.getMainAttributes();
            String classPath = mainAttributes.getValue(Attributes.Name.CLASS_PATH);
            if (classPath == null || StringUtils.isBlank(classPath)) {
                return;
            }
            String[] allJarNames = classPath.split("\\s+");
            for (int i = 0; i < allJarNames.length; i++) {
                String string = allJarNames[i];
                if (!string.toLowerCase().endsWith(".jar") || jarList.contains(string)) {
                    continue;
                }
                try {
                    jarList.add(string);
                    collectAllJars(base, string, jarList);

                } catch (IOException e) {
                    jarList.remove(string);
                }
            }

            return;
        } finally {
            if (jar != null) {
                jar.close();
            }
        }
    }

    public static String getJREInstaller() {
        final StringBuilder dirPath = new StringBuilder(CtiUtilities.getYukonBase());
        dirPath.append(System.getProperty("file.separator"));
        dirPath.append("Server");
        dirPath.append(System.getProperty("file.separator"));
        dirPath.append("Static");
        dirPath.append(System.getProperty("file.separator"));
        dirPath.append("JRE");
        
        File[] fileList = new File(dirPath.toString()).listFiles();
        if (fileList != null && fileList.length > 0) {
            return fileList[0].getName();
        }
        return null;
    }
    
    public static String getJREDownloadURL() {
        try {
            String urlString = "http://www.java.com/getjava/";
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return urlString;
            }
        } catch (java.io.IOException ignore) { }
        return "http://java.sun.com";
    }
    
    private static String getJvmInputArgs() {
        final List<String> inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        final StringBuilder sb = new StringBuilder();
        for (final String arg : inputArgs) {
            sb.append(arg + " ");
        }
        return sb.toString().trim();
    }

}


