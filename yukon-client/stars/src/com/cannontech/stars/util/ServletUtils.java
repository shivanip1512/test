package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import com.cannontech.stars.xml.serialize.ControlHistory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsCustListEntry;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings;
import com.cannontech.util.ServletUtil;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ServletUtils {
	
	// Attribute names to store objects in session or StarsYukonUser object
	public static final String TRANSIENT_ATT_LEADING = "$$";
	
	public static final String ATT_ERROR_MESSAGE = ServletUtil.ATT_ERROR_MESSAGE;
	public static final String ATT_CONFIRM_MESSAGE = ServletUtil.ATT_CONFIRM_MESSAGE;
	public static final String ATT_PASSWORD_VERIFIED = "PASSWORD_VERIFIED";
	public static final String ATT_REDIRECT = ServletUtil.ATT_REDIRECT;
	public static final String ATT_REDIRECT2 = ServletUtil.ATT_REDIRECT2;
	public static final String ATT_REFERRER = ServletUtil.ATT_REFERRER;
	public static final String ATT_REFERRER2 = ServletUtil.ATT_REFERRER2;
	
	public static final String ATT_YUKON_USER = ServletUtil.ATT_YUKON_USER;
	public static final String ATT_STARS_YUKON_USER = "STARS_YUKON_USER";
	public static final String ATT_ENERGY_COMPANY_SETTINGS = "ENERGY_COMPANY_SETTINGS";
	public static final String ATT_CUSTOMER_SELECTION_LISTS = "CUSTOMER_SELECTION_LISTS";
	public static final String ATT_DEFAULT_THERMOSTAT_SETTINGS = "DEFAULT_THERMOSTAT_SETTINGS";
	public static final String ATT_CUSTOMER_ACCOUNT_INFO = "CUSTOMER_ACCOUNT_INFORMATION";
	
	public static final String ATT_LM_PROGRAM_HISTORY = "LM_PROGRAM_HISTORY";
	public static final String ATT_CHANGED_THERMOSTAT_SETTINGS = "CHANGED_THERMOSTAT_SETTINGS";
	public static final String ATT_APPLY_TO_WEEKEND = "APPLY_TO_WEEKEND";
	public static final String ATT_APPLY_TO_WEEKDAYS = "APPLY_TO_WEEKDAYS";
	public static final String ATT_ACCOUNT_SEARCH_RESULTS = "ACCOUNT_SEARCH_RESULTS";
	public static final String ATT_NEW_CUSTOMER_ACCOUNT = "NEW_CUSTOMER_ACCOUNT";
	public static final String ATT_LAST_SEARCH_OPTION = "LAST_SEARCH_OPTION";
	
	public static final String ATT_MULTI_ACTIONS = "MULTI_ACTIONS";
	public static final String ATT_NEW_ACCOUNT_WIZARD = "NEW_ACCOUNT_WIZARD";
	
	public static final String ATT_OMIT_GATEWAY_TIMEOUT = "OMIT_GATEWAY_TIMEOUT";
	public static final String ATT_THERMOSTAT_INVENTORY_IDS = "THERMOSTAT_INVENTORY_IDS";
	public static final String ATT_CONTEXT_SWITCHED = "CONTEXT_SWITCHED";
	
	public static final String IN_SERVICE = "In Service";
	public static final String OUT_OF_SERVICE = "Out of Service";
	
	public static final int MAX_NUM_IMAGES = 3;
	public static final int GATEWAY_TIMEOUT_HOURS = 24;
	
	public static final String UTIL_COMPANY_ADDRESS = "<<COMPANY_ADDRESS>>";
	public static final String UTIL_PHONE_NUMBER = "<<PHONE_NUMBER>>";
	public static final String UTIL_FAX_NUMBER = "<<FAX_NUMBER>>";
	public static final String UTIL_EMAIL = "<<EMAIL>>";

	private static java.text.DecimalFormat decFormat = new java.text.DecimalFormat("0.#");
	
	private static final java.text.SimpleDateFormat[] timeFormat =
	{
		new java.text.SimpleDateFormat("hh:mm a"),
		new java.text.SimpleDateFormat("hh:mma"),
		new java.text.SimpleDateFormat("HH:mm"),
	};

	//this static initializer sets all the simpledateformat to lenient
	static
	{
		for (int i = 0; i < timeFormat.length; i++)
			timeFormat[i].setLenient( true );
	}
    

	public static String forceNotEmpty(String str) {
		if (str == null || str.equals(""))
			return "&nbsp;";
		return str;
	}

	public static String getDurationString(int sec) {
		String durationStr = null;

		if (sec >= 3600)
			durationStr = decFormat.format(1.0 * sec / 3600) + " Hours";
		else
			durationStr = String.valueOf(sec / 60) + " Minutes";

		return durationStr;
	}
    
	public static String formatDate(Date date, java.text.SimpleDateFormat format) {
		return formatDate( date, format, "" );
	}
    
	public static String formatDate(Date date, java.text.SimpleDateFormat format, String emptyStr) {
		if (date == null) return emptyStr;
		if (date.getTime() < ServerUtils.VERY_EARLY_TIME) return emptyStr;
		return format.format( date );
	}

	/**
	 * Parse time in the specified time zone 
	 */
	public static Date parseTime(String timeStr, TimeZone tz) {
		Date retVal = null;
		
		for( int i = 0; i < timeFormat.length; i++ ) {
			try {
				timeFormat[i].setTimeZone(tz);
				retVal = timeFormat[i].parse(timeStr);
				break;
			}
			catch( java.text.ParseException pe ) {}
		}
		
		return retVal;	
	}
	
	/**
	 * Parse date/time in the format of MM/dd/yyyy HH:mm,
	 * MM/dd/yyyy hh:mm a, etc., in the specified time zone
	 */
	public static Date parseDateTime(String dateStr, String timeStr, TimeZone tz) {
		Date datePart = com.cannontech.util.ServletUtil.parseDateStringLiberally( dateStr, tz );
		if (datePart == null) return null;
		
		Date timePart = parseTime( timeStr, tz );
		if (timePart == null) return null;
		
		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime( datePart );
		Calendar timeCal = Calendar.getInstance();
		timeCal.setTime( timePart );
		
		dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
		dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
		dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
		
		return dateCal.getTime();
	}
    
	public static StarsLMControlHistory getControlHistory(StarsLMControlHistory ctrlHist, StarsCtrlHistPeriod period, TimeZone tz) {
		Date date = LMControlHistoryUtil.getPeriodStartTime( period, tz );
    	
		StarsLMControlHistory ctrlHistToday = new StarsLMControlHistory();
		ctrlHistToday.setBeingControlled( ctrlHist.getBeingControlled() );
        
		for (int i = ctrlHist.getControlHistoryCount() - 1; i >= 0; i--) {
			ControlHistory hist = ctrlHist.getControlHistory(i);
			if ( hist.getStartDateTime().before(date) ) break;
			ctrlHistToday.addControlHistory( hist );
		}
        
		return ctrlHistToday;
	}
    
	/**
	 * Format phone number to format "[...-#-###-]###-#### x#..."
	 * E.g. 763-595-7777 x5529 (5529 is the extension number)
	 * In the original string, each segment between adjacent "-" above must be consecutive
	 */
	public static String formatPhoneNumber(String phoneNo) throws WebClientException {
		phoneNo = phoneNo.trim();
		if (phoneNo.equals("")) return "";
		
		String errorMsg = "Invalid phone number format '" + phoneNo + "', the phone number should be in the form of 'xxx-xxx-xxxx'";
    	
		StringBuffer formatedPhoneNo = new StringBuffer();
		int n = phoneNo.length() - 1;	// position of digit counted from the last
		
		// Everything after the first character "x" is considered extension number and copied into the phone number
		int extStartIdx = phoneNo.indexOf( 'x' );
		if (extStartIdx < 0) extStartIdx = phoneNo.indexOf( 'X' );
		
		if (extStartIdx >= 0) {
			formatedPhoneNo.append(" x").append( phoneNo.substring(extStartIdx+1) );
			n = extStartIdx - 1;
		}
    	
		/* Find the last 4 digits */
		while (n >= 0 && !Character.isDigit( phoneNo.charAt(n) ))
			n--;
		if (n < 0)
			throw new WebClientException( errorMsg );
    	
		for (int i = 1; i < 4; i++) {
			n--;
			if (n < 0 || !Character.isDigit( phoneNo.charAt(n) ))
				throw new WebClientException( errorMsg );
		}
		formatedPhoneNo.insert( 0, phoneNo.substring(n, n+4) );
    	
		/* Find the middle 3 digits */
		n--;
		while (n >= 0 && !Character.isDigit( phoneNo.charAt(n) ))
			n--;
		if (n < 0)
			throw new WebClientException( errorMsg );
    	
		for (int i = 1; i < 3; i++) {
			n--;
			if (n < 0 || !Character.isDigit( phoneNo.charAt(n) ))
				throw new WebClientException( errorMsg );
		}
		formatedPhoneNo.insert( 0, '-' );
		formatedPhoneNo.insert( 0, phoneNo.substring(n, n+3) );
    	
		/* Find the 3-digit area code */
		n--;
		while (n >= 0 && !Character.isDigit( phoneNo.charAt(n) ))
			n--;
		if (n < 0) return formatedPhoneNo.toString();
    	
		for (int i = 1; i < 3; i++) {
			n--;
			if (n < 0 || !Character.isDigit( phoneNo.charAt(n) ))
				throw new WebClientException( errorMsg );
		}
		formatedPhoneNo.insert( 0, '-' );
		formatedPhoneNo.insert( 0, phoneNo.substring(n, n+3) );
    	
		/* Find the 1 digit before area code of 800 number or long distance */
		n--;
		while (n >= 0 && !Character.isDigit( phoneNo.charAt(n) ))
			n--;
		if (n < 0) return formatedPhoneNo.toString();
		formatedPhoneNo.insert( 0, '-' );
		formatedPhoneNo.insert( 0, phoneNo.charAt(n) );
    	
		/* Save all the remaining digits (country code etc.) in one segment */
		n--;
		while (n >= 0 && !Character.isDigit( phoneNo.charAt(n) ))
			n--;
		if (n < 0) return formatedPhoneNo.toString();
		formatedPhoneNo.insert( 0, '-' );
		for (; n >= 0; n--) {
			if (Character.isDigit( phoneNo.charAt(n) ))
				formatedPhoneNo.insert( 0, phoneNo.charAt(n) );
		}
    	
		return formatedPhoneNo.toString();
	}
    
	public static String formatAddress(StarsCustomerAddress starsAddr) {
		if (starsAddr == null) return "";
    	
		StringBuffer sBuf = new StringBuffer();
		if (starsAddr.getStreetAddr1().trim().length() > 0)
			sBuf.append( starsAddr.getStreetAddr1() ).append( "<br>" );
		if (starsAddr.getStreetAddr2().trim().length() > 0)
			sBuf.append( starsAddr.getStreetAddr2() ).append( "<br>" );
		if (starsAddr.getCity().trim().length() > 0)
			sBuf.append( starsAddr.getCity() ).append( ", " );
		if (starsAddr.getState().trim().length() > 0)
			sBuf.append( starsAddr.getState() ).append( " " );
		if (starsAddr.getZip().trim().length() > 0)
			sBuf.append( starsAddr.getZip() );
    	
		return sBuf.toString();
	}
    
	public static String getOneLineAddress(StarsCustomerAddress starsAddr) {
		if (starsAddr == null) return "(none)";
    	
		StringBuffer sBuf = new StringBuffer();
		if (starsAddr.getStreetAddr1().trim().length() > 0)
			sBuf.append( starsAddr.getStreetAddr1() );
		if (starsAddr.getStreetAddr2().trim().length() > 0) {
			if (sBuf.length() > 0) sBuf.append( ", " );
			sBuf.append( starsAddr.getStreetAddr2() );
		}
		if (starsAddr.getCity().trim().length() > 0) {
			if (sBuf.length() > 0) sBuf.append( ", " );
			sBuf.append( starsAddr.getCity() );
		}
		if (starsAddr.getState().trim().length() > 0) {
			if (sBuf.length() > 0) sBuf.append( ", " );
			sBuf.append( starsAddr.getState() ).append( " " ).append( starsAddr.getZip() );
		}
    	
		return sBuf.toString();
	}
    
	public static TimeZone getTimeZone(String timeZoneStr) {
		if (timeZoneStr.equalsIgnoreCase("AST"))
			return TimeZone.getTimeZone( "US/Alaska" );
		else if (timeZoneStr.equalsIgnoreCase("PST"))
			return TimeZone.getTimeZone( "US/Pacific" );
		else if (timeZoneStr.equalsIgnoreCase("MST"))
			return TimeZone.getTimeZone( "US/Mountain" );
		else if (timeZoneStr.equalsIgnoreCase("CST"))
			return TimeZone.getTimeZone( "US/Central" );
		else if (timeZoneStr.equalsIgnoreCase("EST"))
			return TimeZone.getTimeZone( "US/Eastern" );
		else
			return null;
	}
    
	public static String getTimeZoneStr(TimeZone timeZone) {
		if (timeZone.equals( TimeZone.getTimeZone("AST") ))
			return "AST";
		else if (timeZone.equals( TimeZone.getTimeZone("PST") ))
			return "PST";
		else if (timeZone.equals( TimeZone.getTimeZone("MST") ))
			return "MST";
		else if (timeZone.equals( TimeZone.getTimeZone("CST") ))
			return "CST";
		else if (timeZone.equals( TimeZone.getTimeZone("EST") ))
			return "EST";
		else
			return null;
	}
    
	public static void removeTransientAttributes(HttpSession session) {
		Enumeration enum = session.getAttributeNames();
		ArrayList attToBeRemoved = new ArrayList();
		
		while (enum.hasMoreElements()) {
			String attName = (String) enum.nextElement();
			if (attName.startsWith( ServletUtils.TRANSIENT_ATT_LEADING ))
				attToBeRemoved.add( attName );
		}
		
		for (int i = 0; i < attToBeRemoved.size(); i++)
			session.removeAttribute( (String)attToBeRemoved.get(i) );
	}
    
	// Return image names: large icon, small icon, saving icon, control icon, environment icon
	public static String[] getImageNames(String imageStr) {
		String[] names = imageStr.split(",");
		String[] imgNames = new String[ MAX_NUM_IMAGES ];
		for (int i = 0; i < MAX_NUM_IMAGES; i++) {
			if (i < names.length)
				imgNames[i] = names[i].trim();
			else
				imgNames[i] = "";
		}
    	
		return imgNames;
	}
    
	// Return program display names: display name, short name (used in enrollment page)
	public static String[] getProgramDisplayNames(StarsEnrLMProgram starsProg) {
		String[] names = ServerUtils.splitString( starsProg.getStarsWebConfig().getAlternateDisplayName(), "," );
		String[] dispNames = new String[2];
		for (int i = 0; i < 2; i++) {
			if (i < names.length)
				dispNames[i] = names[i].trim();
			else
				dispNames[i] = "";
		}
    	
		// If not provided, default display name to program name, and short name to display name
		if (dispNames[0].length() == 0)
			dispNames[0] = starsProg.getProgramName();
		if (dispNames[1].length() == 0)
			dispNames[1] = dispNames[0];
		return dispNames;
	}
	
	public static String getApplianceDescription(StarsEnrollmentPrograms categories, StarsAppliance appliance) {
		for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
			StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
			if (category.getApplianceCategoryID() == appliance.getApplianceCategoryID())
				return category.getDescription();
		}
		
		return "(none)";
	}
    
	public static String capitalize(String word) {
		return word.substring(0,1).toUpperCase().concat( word.substring(1).toLowerCase() );
	}
    
	public static String capitalize2(String phrase) {
		StringTokenizer st = new StringTokenizer( phrase, " ", true );
		StringBuffer sb = new StringBuffer();
		while (st.hasMoreTokens()) {
			String word = st.nextToken();
			if (word.equals(" "))
				sb.append( word );
			else
				sb.append( word.substring(0,1).toUpperCase() ).append( word.substring(1).toLowerCase() );
		}
    	
		return sb.toString();
	}

	public static StarsCustListEntry getStarsCustListEntry(java.util.Hashtable selectionLists, String listName, int yukonDefID) {
		StarsCustSelectionList list = (StarsCustSelectionList) selectionLists.get( listName );
		for (int i = 0; i < list.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = list.getStarsSelectionListEntry(i);
			if (entry.getYukonDefID() == yukonDefID)
				return entry;
		}
		
		return null;
	}

	public static StarsCustListEntry getStarsCustListEntryByID(java.util.Hashtable selectionLists, String listName, int entryID) {
		StarsCustSelectionList list = (StarsCustSelectionList) selectionLists.get( listName );
		for (int i = 0; i < list.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = list.getStarsSelectionListEntry(i);
			if (entry.getEntryID() == entryID)
				return entry;
		}
		
		return null;
	}
	
	public static boolean isWeekday(StarsThermoDaySettings day) {
		return (day.getType() == StarsThermoDaySettings.WEEKDAY_TYPE ||
				day.getType() == StarsThermoDaySettings.MONDAY_TYPE ||
				day.getType() == StarsThermoDaySettings.TUESDAY_TYPE ||
				day.getType() == StarsThermoDaySettings.WEDNESDAY_TYPE ||
				day.getType() == StarsThermoDaySettings.THURSDAY_TYPE ||
				day.getType() == StarsThermoDaySettings.FRIDAY_TYPE);
	}
	
	public static StarsThermoDaySettings getCurrentDay() {
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
			return StarsThermoDaySettings.MONDAY;
		else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
			return StarsThermoDaySettings.TUESDAY;
		else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
			return StarsThermoDaySettings.WEDNESDAY;
		else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
			return StarsThermoDaySettings.THURSDAY;
		else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
			return StarsThermoDaySettings.FRIDAY;
		else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			return StarsThermoDaySettings.SATURDAY;
		else
			return StarsThermoDaySettings.SUNDAY;
	}
	
	public static boolean isGatewayTimeout(Date lastUpdatedTime) {
		if (lastUpdatedTime == null) return true;
		return (new Date().getTime() - lastUpdatedTime.getTime() > GATEWAY_TIMEOUT_HOURS * 3600 * 1000);
	}

}