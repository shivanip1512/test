package com.cannontech.stars.util;

import java.util.*;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ServletUtils {
	
	public static class ProgramHistory {
		private Date date = null;
		private String action = null;
		private String duration = null;
		private ArrayList programList = new ArrayList();

		public Date getDate() {
			return date;
		}

		public String getDuration() {
			return duration;
		}

		public String[] getPrograms() {
			String[] programs = new String[ programList.size() ];
			programList.toArray( programs );
			return programs;
		}

		public String getAction() {
			return action;
		}

	}
	
	public static final String TRANSIENT_ATT_LEADING = "$$";
	
	public static final String ATT_ERROR_MESSAGE = "ERROR_MESSAGE";
	public static final String ATT_PASSWORD_VERIFIED = "PASSWORD_VERIFIED";
	public static final String ATT_REDIRECT = "REDIRECT";
	public static final String ATT_REDIRECT2 = "REDIRECT2";
	public static final String ATT_REFERRER = "REFERRER";
	public static final String ATT_OVER_PAGE_ACTION = "OVER_PAGE_ACTION";
	
	public static final String ATT_YUKON_USER = "YUKON_USER";
	public static final String ATT_STARS_YUKON_USER = "STARS_YUKON_USER";
	public static final String ATT_CUSTOMER_SELECTION_LISTS = "CUSTOMER_SELECTION_LISTS";
	public static final String ATT_ENROLLMENT_PROGRAMS = "ENROLLMENT_PROGRAMS";
	public static final String ATT_DEFAULT_THERMOSTAT_SETTINGS = "DEFAULT_THERMOSTAT_SETTINGS";
	public static final String ATT_CHANGED_THERMOSTAT_SETTINGS = "CHANGED_THERMOSTAT_SETTINGS";
	public static final String ATT_ACCOUNT_SEARCH_RESULTS = "ACCOUNT_SEARCH_RESULTS";
	public static final String ATT_EXIT_INTERVIEW_QUESTIONS = "EXIT_INTERVIEW_QUESTIONS";
	public static final String ATT_CALL_TRACKING_NUMBER = "CALL_TRACKING_NUMBER";
	public static final String ATT_ORDER_TRACKING_NUMBER = "ORDER_TRACKING_NUMBER";
	
	public static final String ATT_CUSTOMER_ACCOUNT_INFO = "CUSTOMER_ACCOUNT_INFORMATION";
	public static final String ATT_LM_PROGRAM_HISTORY = "LM_PROGRAM_HISTORY";
	
	public static final String IN_SERVICE = "In Service";
	public static final String OUT_OF_SERVICE = "Out of Service";

    private static java.text.DecimalFormat decFormat = new java.text.DecimalFormat("0.#");
    
    private static GregorianCalendar veryEarlyDate = new GregorianCalendar(1970, Calendar.JANUARY, 2);

    public ServletUtils() {
    }

    public static String getDurationString(int sec) {
        String durationStr = null;

        if (sec >= 3600)
            durationStr = decFormat.format(1.0 * sec / 3600) + " Hours";
        else
            durationStr = String.valueOf(sec / 60) + " Minutes";

        return durationStr;
    }
    
    public static String getDurationString(Date startDate, Date stopDate) {
    	if (startDate == null || stopDate == null) return "";
    	
    	int duration = (int) ((stopDate.getTime() - startDate.getTime()) * 0.001 / (3600 * 24) + 0.5);
    	String durStr = String.valueOf(duration);
    	if (duration > 1)
    		durStr += " Days";
    	else
    		durStr += " Day";
    		
    	return durStr;
    }
    
    public static ProgramHistory[] createProgramHistory(StarsLMPrograms programs) {
    	TreeMap progHistMap = new TreeMap();
    	
    	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
    		StarsLMProgram program = programs.getStarsLMProgram(i);
    		StarsLMProgramHistory starsProgHist = program.getStarsLMProgramHistory();
    		if (starsProgHist == null) continue;
    		
    		for (int j = 0; j < starsProgHist.getStarsLMProgramEventCount(); j++) {
    			StarsLMProgramEvent event = starsProgHist.getStarsLMProgramEvent(j);
    			
    			ProgramHistory progHist = new ProgramHistory();
    			progHist.date = event.getEventDateTime();
    			progHist.action = event.getEventAction();
    			progHist.programList.add( program.getProgramName() );
    			
    			if (event.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION) {
    				// Getting opt out duration by looking at the next "Future Activation" event,
    				boolean foundDuration = false;
    				while (j < starsProgHist.getStarsLMProgramEventCount() - 1) {
	    				StarsLMProgramEvent event2 = starsProgHist.getStarsLMProgramEvent(++j);
	    				if (event2.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION
	    					|| event2.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED)
	    				{
	    					progHist.duration = getDurationString( event.getEventDateTime(), event2.getEventDateTime() );
	    					foundDuration = true;
	    					break;
	    				}
	    				if (event2.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION)
	    					return null;
    				}
    				
    				if (!foundDuration) return null;
    			}
    			
	    		ProgramHistory progHist2 = (ProgramHistory) progHistMap.get( progHist.date );
	    		if (progHist2 == null)	// No other events happened at the same time
	    			progHistMap.put( progHist.date, progHist );
	    		else {	// Found events happened at the same time
	    			if (!progHist2.action.equals( progHist.action ))	// Not the same action
	    				progHistMap.put( progHist.date, progHist );
		    		else {	// Same event action
		    			if (progHist.duration == null)	// Not Temporary opt out action
		    				progHist2.programList.add( program.getProgramName() );
		    			else {	// Temporary opt out action
		    				if (progHist.duration.equals( progHist2.duration ))	// Same duration
		    					progHist2.programList.add( program.getProgramName() );
		    				else	// Different duration
		    					progHistMap.put( progHist.date, progHist );
		    			}
		    		}
	    		}
    		}
    	}
    	
    	ProgramHistory[] progHists = new ProgramHistory[ progHistMap.size() ];
    	progHistMap.values().toArray( progHists );
    	return progHists;
    }
    
    public static String getDateFormat(Date date, java.text.SimpleDateFormat format) {
    	GregorianCalendar cal = new GregorianCalendar();
    	cal.setTime( date );
    	if (cal.before( veryEarlyDate ))	// Too early date means the date should be empty
    		return "";
    		
    	return format.format( date );
    }
    
    public static StarsLMControlHistory getTodaysControlHistory(StarsLMControlHistory ctrlHist) {
        StarsLMControlHistory ctrlHistToday = new StarsLMControlHistory();
        ctrlHistToday.setBeingControlled( ctrlHist.getBeingControlled() );
        
        Date today = com.cannontech.util.ServletUtil.getToday();
        for (int i = ctrlHist.getControlHistoryCount() - 1; i >= 0; i--) {
        	ControlHistory hist = ctrlHist.getControlHistory(i);
        	if ( hist.getStartDateTime().before(today) ) break;
        	ctrlHistToday.addControlHistory( hist );
        }
        
        return ctrlHistToday;
    }
    
    /**
     * Format phone number to format "(...-#-)###-###-####"
     * In the original string, each segment between adjacent "-" above must be consecutive,
     * otherwise an empty string is returned
     */
    public static String formatPhoneNumber(String phoneNo) {
    	StringBuffer formatedPhoneNo = new StringBuffer();
    	int n = phoneNo.length() - 1;	// position of digit counted from the last
    	
    	/* Find the last 4 digits */
    	while (n >= 0 && !Character.isDigit( phoneNo.charAt(n) ))
    		n--;
    	if (n < 0) return "";
    	for (int i = 1; i < 4; i++) {
    		n--;
    		if (n < 0 || !Character.isDigit( phoneNo.charAt(n) ))
    			return "";
    	}
    	formatedPhoneNo.insert( 0, phoneNo.substring(n, n+4) );
    	
    	/* Find the middle 3 digits */
    	n--;
    	while (n >= 0 && !Character.isDigit( phoneNo.charAt(n) ))
    		n--;
    	if (n < 0) return "";
    	for (int i = 1; i < 3; i++) {
    		n--;
    		if (n < 0 || !Character.isDigit( phoneNo.charAt(n) ))
    			return "";
    	}
    	formatedPhoneNo.insert( 0, '-' );
    	formatedPhoneNo.insert( 0, phoneNo.substring(n, n+3) );
    	
    	/* Find the 3-digit area code */
    	n--;
    	while (n >= 0 && !Character.isDigit( phoneNo.charAt(n) ))
    		n--;
    	if (n < 0) return "";
    	for (int i = 1; i < 3; i++) {
    		n--;
    		if (n < 0 || !Character.isDigit( phoneNo.charAt(n) ))
    			return "";
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
    
    public static void removeTransientAttributes(StarsYukonUser user) {
        Enumeration enum = user.getAttributeNames();
        while (enum.hasMoreElements()) {
        	String attName = (String) enum.nextElement();
        	if (attName.startsWith( ServletUtils.TRANSIENT_ATT_LEADING ))
    			user.removeAttribute(attName);
        }
    }
}