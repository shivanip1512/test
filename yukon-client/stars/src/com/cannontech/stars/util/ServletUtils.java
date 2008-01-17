package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.customer.CustomerFactory;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.ContactNotification;
import com.cannontech.stars.xml.serialize.ControlSummary;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsCustomerContact;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings;
import com.cannontech.util.PhoneNumber;
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
	public static final String ATT_PROMPT_MESSAGE = "PROMPT_MESSAGE";
	public static final String ATT_REDIRECT = ServletUtil.ATT_REDIRECT;
	public static final String ATT_REDIRECT2 = ServletUtil.ATT_REDIRECT2;
	public static final String ATT_REFERRER = ServletUtil.ATT_REFERRER;
	public static final String ATT_REFERRER2 = ServletUtil.ATT_REFERRER2;
	
    public static final int ACTION_CHANGEDEVICE = 1;
    public static final int ACTION_CHANGESTATE = 2;
    public static final int ACTION_TOSERVICECOMPANY = 3;
    public static final int ACTION_TOWAREHOUSE = 4;
    public static final int ACTION_CHANGE_WO_SERVICE_STATUS = 5;
    public static final int ACTION_CHANGE_WO_SERVICE_TYPE = 6;
	/**
	 * When used in session, the attribute with this name should be passed a CtiNavObject
	 */
	public static final String NAVIGATE = ServletUtil.NAVIGATE;
	
    /**
     * When used in session, the attribute with this name should be passed an ArrayList of FilterWrappers
     */
    public static final String FILTER_INVEN_LIST = ServletUtil.FILTER_INVEN_LIST;
    
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
	public static final String ATT_LAST_ACCOUNT_SEARCH_OPTION = "LAST_ACCOUNT_SEARCH_OPTION";
    public static final String ATT_LAST_ACCOUNT_SEARCH_VALUE = "LAST_ACCOUNT_SEARCH_VALUE";
    public static final String ATT_LAST_INVENTORY_SEARCH_OPTION = "LAST_INVENTORY_SEARCH_OPTION";
    public static final String ATT_LAST_INVENTORY_SEARCH_VALUE = "LAST_INVENTORY_SEARCH_VALUE";
	public static final String ATT_LAST_SERVICE_SEARCH_OPTION = "LAST_SERVICE_SEARCH_OPTION";
    public static final String ATT_LAST_SERVICE_SEARCH_VALUE = "LAST_SERVICE_SEARCH_VALUE";
	
	public static final String ATT_MULTI_ACTIONS = "MULTI_ACTIONS";
	public static final String ATT_NEW_ACCOUNT_WIZARD = "NEW_ACCOUNT_WIZARD";
	public static final String ATT_LAST_SUBMITTED_REQUEST = "LAST_SUBMITTED_REQUEST";
	
	public static final String ATT_OMIT_GATEWAY_TIMEOUT = "OMIT_GATEWAY_TIMEOUT";
	public static final String ATT_THERMOSTAT_INVENTORY_IDS = "THERMOSTAT_INVENTORY_IDS";
	
	public static final String ATT_CONTEXT_SWITCHED = "CONTEXT_SWITCHED";
	
	public static final String NEED_MORE_INFORMATION = "NeedMoreInformation";
	public static final String CONFIRM_ON_MESSAGE_PAGE = "ConfirmOnMessagePage";
	public static final String ATT_MSG_PAGE_REDIRECT = "MSG_PAGE_REDIRECT";
	public static final String ATT_MSG_PAGE_REFERRER = "MSG_PAGE_REFERRER";
	
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
		if (str == null || str.trim().equals(""))
			return "&nbsp;";
		return str;
	}

	public static String getDurationFromSeconds(int sec) {
		String durationStr = null;

		if (sec >= 3600)
			durationStr = decFormat.format(1.0 * sec / 3600) + " Hours";
		else
			durationStr = String.valueOf(sec / 60) + " Minutes";

		return durationStr;
	}
	
	public static String getDurationFromHours(int hour) {
		String durationStr = null;
		
		if (hour >= 24) {
			int numDays = (int) (hour / 24.0 + 0.5);
			durationStr = String.valueOf(numDays) + " Day";
			if (numDays > 1) durationStr += "s";
		}
		else {
			durationStr = String.valueOf(hour) + " Hour";
			if (hour > 1) durationStr += "s";
		}
		
		return durationStr;
	}
    
	public static String formatDate(Date date, java.text.SimpleDateFormat format) {
		return formatDate( date, format, "" );
	}
    
	public static String formatDate(Date date, java.text.SimpleDateFormat format, String emptyStr) {
		if (date == null) return emptyStr;
		if (date.getTime() < StarsUtils.VERY_EARLY_TIME) return emptyStr;
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
	
	public static StarsLMControlHistory getControlHistory(StarsLMProgram program, StarsAppliances appliances,
		StarsCtrlHistPeriod period, LiteStarsEnergyCompany energyCompany, LiteYukonUser currentUser, int accountId)
	{
        Date startDate = LMControlHistoryUtil.getPeriodStartTime( period, energyCompany.getDefaultTimeZone() );
		String trackHwAddr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING );
		/*
         * GRE and similar systems
		 */
        if (trackHwAddr != null && Boolean.valueOf(trackHwAddr).booleanValue()) {
			ArrayList groupIDs = new ArrayList();
			
			for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
				StarsAppliance app = appliances.getStarsAppliance(i);
				if (app.getProgramID() == program.getProgramID() && app.getInventoryID() > 0) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( app.getInventoryID(), true );
					int[] grpIDs = null;
					if (liteHw.getLMConfiguration() != null)
						grpIDs = LMControlHistoryUtil.getControllableGroupIDs( liteHw.getLMConfiguration(), app.getLoadNumber() );
					else if (program.getGroupID() > 0)
						grpIDs = new int[] { program.getGroupID() };
					
					if (grpIDs != null) {
						for (int j = 0; j < grpIDs.length; j++) {
							Integer groupID = new Integer( grpIDs[j] );
							if (!groupIDs.contains(groupID)) groupIDs.add( groupID );
						}
					}
				}
			}
			
			StarsLMControlHistory lmCtrlHist = new StarsLMControlHistory();
			lmCtrlHist.setControlSummary( new ControlSummary() );
            
			for (int i = 0; i < groupIDs.size(); i++) {
				StarsLMControlHistory ctrlHist = LMControlHistoryUtil.getStarsLMControlHistory(
						((Integer)groupIDs.get(i)).intValue(), accountId, startDate, energyCompany.getDefaultTimeZone(), currentUser );
				
				for (int j = 0, k = 0; j < ctrlHist.getControlHistoryCount(); j++) {
					while (k < lmCtrlHist.getControlHistoryCount()
						&& !lmCtrlHist.getControlHistory(k).getStartDateTime().after( ctrlHist.getControlHistory(j).getStartDateTime() ))
						k++;
					lmCtrlHist.addControlHistory(k++, ctrlHist.getControlHistory(j));
				}
				
				if (ctrlHist.getBeingControlled()) lmCtrlHist.setBeingControlled(true);
				
				if (ctrlHist.getControlSummary() != null) {
					lmCtrlHist.getControlSummary().setDailyTime(
							lmCtrlHist.getControlSummary().getDailyTime() + ctrlHist.getControlSummary().getDailyTime() );
					//TODO: calculate with opt out hours and enrollment periods?
                    lmCtrlHist.getControlSummary().setMonthlyTime(
							lmCtrlHist.getControlSummary().getMonthlyTime() + ctrlHist.getControlSummary().getMonthlyTime() );
//                  TODO: calculate with opt out hours and enrollment periods?
					lmCtrlHist.getControlSummary().setSeasonalTime(
							lmCtrlHist.getControlSummary().getSeasonalTime() + ctrlHist.getControlSummary().getSeasonalTime() );
//                  TODO: calculate with opt out hours and enrollment periods?
					lmCtrlHist.getControlSummary().setAnnualTime(
							lmCtrlHist.getControlSummary().getAnnualTime() + ctrlHist.getControlSummary().getAnnualTime() );
				}
			}
			
			return lmCtrlHist;
		}
		else {
			return LMControlHistoryUtil.getStarsLMControlHistory( program.getGroupID(), accountId, startDate, energyCompany.getDefaultTimeZone(), currentUser );
		}
	}
	
    /**
     * Strips a phone number down to a simple string of digits
     */
	public static String formatPhoneNumberForSearch(String phoneNo) throws WebClientException {
		phoneNo = phoneNo.trim();
		if (phoneNo.equals("")) return "";
        
        //get rid of US country code (long distance)
        if(phoneNo.startsWith("1"))
            phoneNo = phoneNo.replaceFirst("1", "");
        
        return formatPhoneNumberForStorage(phoneNo);
	}
    
    public static String formatPhoneNumberForStorage(String phoneNo) throws WebClientException {
        phoneNo = phoneNo.trim();
        if (phoneNo.equals("")) return "";
        
        char[] checkDigits = phoneNo.toCharArray();
        for(char j: checkDigits) {
            if(!Character.isDigit(j) && j != '-' && j != 'x' && j != '(' && j != ')' && j != ' ')
                throw new WebClientException("Invalid phone number format '" + phoneNo + "'.  The phone number contains non-digits.");
        }
        
        return PhoneNumber.extractDigits(phoneNo);
    }
    
    public static String formatPhoneNumberForDisplay(String phoneNo) throws WebClientException {
        phoneNo = phoneNo.trim();
        if (phoneNo.equals("")) return "";
        
        /*//verify no non-digits are present
        char[] checkDigits = phoneNo.toCharArray();
        for(char j: checkDigits) {
            if(!Character.isDigit(j) && j != '-' && j != 'x' && j != '(' && j != ')' && j != ' ')
                throw new WebClientException("Invalid phone number format '" + phoneNo + "'.  The phone number contains non-digits.");
        }*/
        
        return PhoneNumber.format(phoneNo);
    }
    
    public static String formatPin(String pin) throws WebClientException 
    {
        pin = pin.trim();
        if (pin.equals("")) return "";
        
        if(pin.length() > 19)
        {
            throw new WebClientException( "Invalid IVR information format '" + pin + "'. This IVR field should have fewer than 20 digits." );
        }
        
        try
        {
            Long.parseLong(pin);
        }
        catch(NumberFormatException e)
        {
            throw new WebClientException( "Invalid IVR information format '" + pin + "'. This field is required to be numeric.'" );
        }
        
        return pin;
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
			sBuf.append( starsAddr.getStreetAddr1() ).append(", ");
		if (starsAddr.getStreetAddr2().trim().length() > 0)
			sBuf.append( starsAddr.getStreetAddr2() ).append(", ");
		if (starsAddr.getCity().trim().length() > 0)
			sBuf.append( starsAddr.getCity() ).append(", ");
		if (starsAddr.getState().trim().length() > 0)
			sBuf.append( starsAddr.getState() ).append(" ");
		if (starsAddr.getZip().trim().length() > 0)
			sBuf.append( starsAddr.getZip() );
    	
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
		Enumeration attributeEnum = session.getAttributeNames();
		ArrayList attToBeRemoved = new ArrayList();
		
		while (attributeEnum.hasMoreElements()) {
			String attName = (String) attributeEnum.nextElement();
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
		String[] names = StarsUtils.splitString( starsProg.getStarsWebConfig().getAlternateDisplayName(), "," );
		String[] dispNames = new String[2];
		for (int i = 0; i < 2; i++) {
			if (i < names.length)
				dispNames[i] = names[i].trim();
			else
				dispNames[i] = "";
		}
    	
		// If not provided, default display name to program name, and short name to display name
		if (dispNames[0].length() == 0) {
			if (starsProg.getYukonName() != null)
				dispNames[0] = starsProg.getYukonName();
			else
				dispNames[0] = "(none)";
		}
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
	
	public static String getInventoryLabel(StarsInventory starsInv) {
		String label = starsInv.getDeviceLabel();
		if (label.equals("")) {
			if (starsInv.getLMHardware() != null)
				label = starsInv.getLMHardware().getManufacturerSerialNumber();
			else if (starsInv.getMCT() != null)
				label = starsInv.getMCT().getDeviceName();
            else if (starsInv.getMeterNumber() != null)
                label = starsInv.getMeterNumber();
		}
		
		return label;
	}
	
	public static StarsEnrLMProgram getEnrollmentProgram(StarsEnrollmentPrograms categories, int programID) {
		for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
			StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
			for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
				if (category.getStarsEnrLMProgram(j).getProgramID() == programID)
					return category.getStarsEnrLMProgram(j);
			}
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
	
	public static String getBriefText(String fullText, int limit) {
		if (limit <= 0 || fullText.length() <= limit)
			return fullText;
		
		int len = fullText.lastIndexOf( ' ', limit );
		if (len < 0) len = limit;
		return fullText.substring( 0, len ) + " ...";
	}
	
	public static String hideUnsetNumber(int num, int num_unset) {
		return (num == num_unset)? "" : String.valueOf(num);
	}
	
	public static int parseNumber(String str, int lowerLimit, int upperLimit, String fieldName) throws WebClientException {
		if (str == null || str.trim().equals(""))
			throw new WebClientException( "The '" + fieldName + "' field cannot be empty" );
		
		try {
			int value = Integer.parseInt( str );
			if (value < lowerLimit || value > upperLimit)
				throw new WebClientException("The value of '" + fieldName + "' must be between " + lowerLimit + " and " + upperLimit);
			return value;
		}
		catch (NumberFormatException e) {
			throw new WebClientException( "Invalid numeric value \"" + str + "\"" );
		}
	}
	
	public static int parseNumber(String str, int lowerLimit, int upperLimit, int num_unset, String fieldName)
		throws WebClientException
	{
		if (str == null || str.trim().equals(""))
			return num_unset;
		return parseNumber( str, lowerLimit, upperLimit, fieldName );
	}
	
	public static void saveRequest(HttpServletRequest req, HttpSession session, String[] params) {
		Properties savedReq = new Properties();
		for (int i = 0; i < params.length; i++) {
			if (req.getParameter(params[i]) != null)
				savedReq.setProperty( params[i], req.getParameter(params[i]) );
		}
		
		session.setAttribute( ATT_LAST_SUBMITTED_REQUEST, savedReq );
	}
	
	public static ContactNotification getContactNotification(StarsCustomerContact contact, int notifCatID) {
		for (int i = 0; i < contact.getContactNotificationCount(); i++) {
			if (contact.getContactNotification(i) != null && contact.getContactNotification(i).getNotifCatID() == notifCatID)
				return contact.getContactNotification(i);
		}
		
		return null;
	}
	
	public static ContactNotification createContactNotification(String value, int notifCatID) {
		if (value != null && value.trim().length() > 0) {
			ContactNotification contNotif = new ContactNotification();
			contNotif.setNotifCatID( notifCatID );
			contNotif.setNotification( value );
			return contNotif;
		}
		
		return null;
	}
	
	/**
	 * Get the FAQ link (usually a URL to the customer's website rather than using the default FAQ page).
	 * It will search in the first operator group, then in the first customer group.
	 * If FAQ link is not set in either of them, a null value will be returned.
	 */
	public static String getCustomerFAQLink(LiteStarsEnergyCompany energyCompany) {
		String faqLink = null;
		
		LiteYukonGroup[] operGroups = energyCompany.getWebClientOperatorGroups();
		if (operGroups.length > 0)
			faqLink = DaoFactory.getAuthDao().getRolePropValueGroup( operGroups[0], ConsumerInfoRole.WEB_LINK_FAQ, null );
		
		if (StarsUtils.forceNotNone(faqLink).length() == 0) {
			LiteYukonGroup[] custGroups = energyCompany.getResidentialCustomerGroups();
			if (custGroups.length > 0)
				faqLink = DaoFactory.getAuthDao().getRolePropValueGroup(custGroups[0], ResidentialCustomerRole.WEB_LINK_FAQ, null);
		}
		
		if (StarsUtils.forceNotNone(faqLink).length() == 0)
			faqLink = null;
		
		return faqLink;
	}
	
	/**
	 * Get the FAQ link that the given user of the given energy company should see.
	 * If the user is an operator, search in the operator groups; if it's a residential customer, search in
	 * the customer groups. If FAQ link is not set in either of them, and the given company has a parent,
	 * dedicate the search to the parent company, and so on.
	 */
	public static String getCustomerFAQLink(StarsYukonUser user, LiteStarsEnergyCompany energyCompany) {
		String faqLink = null;
		
		if (user.getEnergyCompanyID() == energyCompany.getLiteID()) {
			if (StarsUtils.isOperator(user))
				faqLink = DaoFactory.getAuthDao().getRolePropertyValue( user.getYukonUser(), ConsumerInfoRole.WEB_LINK_FAQ );
			else
				faqLink = DaoFactory.getAuthDao().getRolePropertyValue( user.getYukonUser(), ResidentialCustomerRole.WEB_LINK_FAQ );
		}
		else {
			if (StarsUtils.isOperator(user)) {
				LiteYukonGroup[] operGroups = energyCompany.getWebClientOperatorGroups();
				if (operGroups.length > 0)
					faqLink = DaoFactory.getAuthDao().getRolePropValueGroup( operGroups[0], ConsumerInfoRole.WEB_LINK_FAQ, null );
			}
			else {
				LiteYukonGroup[] custGroups = energyCompany.getResidentialCustomerGroups();
				if (custGroups.length > 0)
					faqLink = DaoFactory.getAuthDao().getRolePropValueGroup(custGroups[0], ResidentialCustomerRole.WEB_LINK_FAQ, null);
			}
		}
		
		if (StarsUtils.forceNotNone(faqLink).length() == 0)
			faqLink = null;
		
		if (faqLink != null || energyCompany.getCustomerFAQs() != null)
			return faqLink;
		else if (energyCompany.getParent() != null)
			return getCustomerFAQLink( user, energyCompany.getParent() );
		else
			return null;
	}
    
    public final static void updateCustomerTemperatureUnit(
            LiteCustomer customer, String temperatureUnit) throws Exception {
        if (temperatureUnit.matches("^[FC]$")) {
            try {
                Customer cust = CustomerFactory.createCustomer(customer);
                cust.getCustomer().setTemperatureUnit(temperatureUnit);
                Transaction.createTransaction(com.cannontech.database.Transaction.UPDATE,
                                              cust).execute();
                ServerUtils.handleDBChange(customer,
                                           DBChangeMsg.CHANGE_TYPE_UPDATE);
            } catch (Exception e) {
                throw new Exception("Couldn't update Customer's temperature unit",
                                    e);
            }
        } else {
            throw new Exception("Invalid temperature unit: " + temperatureUnit);
        }
    }
    
    public static StarsYukonUser getStarsYukonUser(final HttpSession session ) {
        return (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
    }

    /**
     * @return the current StarsYukonUser Object found in the request.
     * @throws NotLoggedInException if no session exists
     */
    public static StarsYukonUser getStarsYukonUser(final ServletRequest request) throws NotLoggedInException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            throw new NotLoggedInException();
        }
        StarsYukonUser starsYukonUser = getStarsYukonUser(session);
        if (starsYukonUser == null) {
            throw new NotLoggedInException();
        }
        return starsYukonUser;
    }
    
    public static String getFormField(final List<FileItem> itemList, final String fieldName) {
    	for (final FileItem item : itemList) {
    		if (item.isFormField() && item.getFieldName().equals(fieldName)) {
    			return item.getString();
    		}
    	}
    	return null;
    }
	
	@SuppressWarnings("unchecked")
	public static List<FileItem> getItemList(final HttpServletRequest request) throws FileUploadException {
		DiskFileUpload upload = new DiskFileUpload();
		List<FileItem> items = upload.parseRequest( request );
		return items;
	}
	
	public static boolean isMultiPartRequest(final HttpServletRequest request) {
		boolean result = DiskFileUpload.isMultipartContent(request);
		return result;
	}
    
    public static FileItem getUploadFile(final List<FileItem> itemList, final String fieldName) throws WebClientException {
        for (final FileItem item : itemList) {
            if (!item.isFormField() && item.getFieldName().equals(fieldName)) {
                if (!item.getName().equals("")) return item;
            }
        }
        return null;
    }
    
}