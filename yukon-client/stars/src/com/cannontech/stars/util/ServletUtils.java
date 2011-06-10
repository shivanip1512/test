package com.cannontech.stars.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.Instant;

import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.ContactNotification;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsCustomerContact;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.util.PhoneNumber;
import com.cannontech.util.ServletUtil;

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
     * When used in session, the attribute with this name should be passed a
     * CtiNavObject
     */
    public static final String NAVIGATE = ServletUtil.NAVIGATE;

    /**
     * When used in session, the attribute with this name should be passed an
     * ArrayList of FilterWrappers
     */
    public static final String FILTER_INVEN_LIST = ServletUtil.FILTER_INVEN_LIST;

    public static final String ATT_YUKON_USER = ServletUtil.ATT_YUKON_USER;
    @Deprecated
    public static final String ATT_STARS_YUKON_USER = "STARS_YUKON_USER";
    @Deprecated
    public static final String ATT_ENERGY_COMPANY_SETTINGS = "ENERGY_COMPANY_SETTINGS";
    public static final String ATT_CUSTOMER_SELECTION_LISTS = "CUSTOMER_SELECTION_LISTS";
    public static final String ATT_DEFAULT_THERMOSTAT_SETTINGS = "DEFAULT_THERMOSTAT_SETTINGS";
    public static final String ATT_CUSTOMER_ACCOUNT_INFO = "CUSTOMER_ACCOUNT_INFORMATION";
    public static final String TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO = ServletUtils.TRANSIENT_ATT_LEADING
                                                                     + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO;

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

    public static final String ATT_THERMOSTAT_INVENTORY_IDS = "THERMOSTAT_INVENTORY_IDS";

    public static final String ATT_CONTEXT_SWITCHED = "CONTEXT_SWITCHED";

    public static final String NEED_MORE_INFORMATION = "NeedMoreInformation";
    public static final String CONFIRM_ON_MESSAGE_PAGE = "ConfirmOnMessagePage";
    public static final String ATT_MSG_PAGE_REDIRECT = "MSG_PAGE_REDIRECT";
    public static final String ATT_MSG_PAGE_REFERRER = "MSG_PAGE_REFERRER";

    public static final String IN_SERVICE = "In Service";
    public static final String OUT_OF_SERVICE = "Out of Service";

    public static final int MAX_NUM_IMAGES = 3;

    public static final String UTIL_COMPANY_ADDRESS = "<<COMPANY_ADDRESS>>";
    public static final String UTIL_PHONE_NUMBER = "<<PHONE_NUMBER>>";
    public static final String UTIL_FAX_NUMBER = "<<FAX_NUMBER>>";
    public static final String UTIL_EMAIL = "<<EMAIL>>";

    public static final String INHERITED_FAQ = "INHERITED_FAQ";

    private static final java.text.SimpleDateFormat[] timeFormat = {
            new java.text.SimpleDateFormat("hh:mm a"),
            new java.text.SimpleDateFormat("hh:mma"),
            new java.text.SimpleDateFormat("HH:mm"), };

    // this static initializer sets all the simpledateformat to lenient
    static {
        for (int i = 0; i < timeFormat.length; i++)
            timeFormat[i].setLenient(true);
    }

    public static String getApplianceDescription(StarsEnrollmentPrograms categories, StarsAppliance  appliance) {
        for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
            StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
            if (category.getApplianceCategoryID() == appliance.getApplianceCategoryID())
                return category.getDescription();
        }
        
        return "(none)";
    }

    public static String getDurationFromHours(int hour) {
        String durationStr = null;

        if (hour >= 24) {
            int numDays = (int) (hour / 24.0 + 0.5);
            durationStr = String.valueOf(numDays) + " Day";
            if (numDays > 1) {
                durationStr += "s";
            }
        } else {
            durationStr = String.valueOf(hour) + " Hour";
            if (hour > 1) {
                durationStr += "s";
            }
        }

        return durationStr;
    }

    public static String formatDate(Date date, java.text.SimpleDateFormat format) {
        return formatDate(date, format, "");
    }

    public static String formatDate(Date date, java.text.SimpleDateFormat format, String emptyStr) {
        if (date == null) {
            return emptyStr;
        }
        if (date.getTime() < StarsUtils.VERY_EARLY_TIME) {
            return emptyStr;
        }
        return format.format(date);
    }

    /**
     * Strips a phone number down to a simple string of digits
     */
    public static String formatPhoneNumberForSearch(String phoneNo) throws WebClientException {
        phoneNo = phoneNo.trim();
        if (phoneNo.equals("")) {
            return "";
        }

        // get rid of US country code (long distance)
        if (phoneNo.startsWith("1")) {
            phoneNo = phoneNo.replaceFirst("1", "");
        }

        return formatPhoneNumberForStorage(phoneNo);
    }

    public static String formatPhoneNumberForStorage(String phoneNo) throws WebClientException {
        phoneNo = phoneNo.trim();
        if (phoneNo.equals("")) {
            return "";
        }

        char[] checkDigits = phoneNo.toCharArray();
        for (char j : checkDigits) {
            if (!Character.isDigit(j) && j != '-' && j != 'x' && j != '(' && j != ')' && j != ' ') {
                throw new WebClientException("Invalid phone number format '"+ phoneNo
                                             + "'.  The phone number contains non-digits.");
            }
        }

        return PhoneNumber.extractDigits(phoneNo);
    }

    public static String formatPin(String pin) throws WebClientException {
        pin = pin.trim();
        if (pin.equals("")) {
            return "";
        }

        if (pin.length() > 19) {
            throw new WebClientException("Invalid IVR information format '" + pin
                                         + "'. This IVR field should have fewer than 20 digits.");
        }

        try {
            Long.parseLong(pin);
        } catch (NumberFormatException e) {
            throw new WebClientException("Invalid IVR information format '" + pin
                                         + "'. This field is required to be numeric.'");
        }

        return pin;
    }
    
    public static String getOneLineAddress(StarsCustomerAddress starsAddr) {
		if (starsAddr == null){
		    return "(none)";
		}
    	
		StringBuffer sBuf = new StringBuffer();
		if (starsAddr.getStreetAddr1().trim().length() > 0) {
			sBuf.append( starsAddr.getStreetAddr1() ).append(", ");
		}
		if (starsAddr.getStreetAddr2().trim().length() > 0){
			sBuf.append( starsAddr.getStreetAddr2() ).append(", ");
		}
		if (starsAddr.getCity().trim().length() > 0) {
			sBuf.append( starsAddr.getCity() ).append(", ");
		}
		if (starsAddr.getState().trim().length() > 0) {
            sBuf.append( starsAddr.getState() ).append(" ");
        }
		if (starsAddr.getZip().trim().length() > 0) {
            sBuf.append( starsAddr.getZip() );
        }
    	
		return sBuf.toString();
	}
    

    public static TimeZone getTimeZone(String timeZoneStr) {
        if (timeZoneStr.equalsIgnoreCase("AST")) {
            return TimeZone.getTimeZone("US/Alaska");
        } else if (timeZoneStr.equalsIgnoreCase("PST")) {
            return TimeZone.getTimeZone("US/Pacific");
        } else if (timeZoneStr.equalsIgnoreCase("MST")) {
            return TimeZone.getTimeZone("US/Mountain");
        } else if (timeZoneStr.equalsIgnoreCase("CST")) {
            return TimeZone.getTimeZone("US/Central");
        } else if (timeZoneStr.equalsIgnoreCase("EST")) {
            return TimeZone.getTimeZone("US/Eastern");
        } else {
            return null;
        }
    }

    public static void removeTransientAttributes(HttpSession session) {
        @SuppressWarnings("unchecked")
        Enumeration<String> attributeEnum = session.getAttributeNames();
        List<String> attToBeRemoved = new ArrayList<String>();

        while (attributeEnum.hasMoreElements()) {
            String attName = attributeEnum.nextElement();
            if (attName.startsWith(ServletUtils.TRANSIENT_ATT_LEADING)) {
                attToBeRemoved.add(attName);
            }
        }

        for (int i = 0; i < attToBeRemoved.size(); i++) {
            session.removeAttribute(attToBeRemoved.get(i));
        }
    }

    // Return image names: large icon, small icon, saving icon, control icon,
    // environment icon
    public static String[] getImageNames(String imageStr) {
        String[] names = imageStr.split(",");
        String[] imgNames = new String[MAX_NUM_IMAGES];
        for (int i = 0; i < MAX_NUM_IMAGES; i++) {
            if (i < names.length) {
                imgNames[i] = names[i].trim();
            } else {
                imgNames[i] = "";
            }
        }

        return imgNames;
    }
    
    // Return program display names: display name, short name (used in enrollment page)
    public static String[] getProgramDisplayNames(StarsEnrLMProgram starsProg) {
        String[] names = StarsUtils.splitString( starsProg.getStarsWebConfig().getAlternateDisplayName(), "," );
        String[] dispNames = new String[2];
        for (int i = 0; i < 2; i++) {
            if (i < names.length) {
                dispNames[i] = names[i].trim();
            } else {
                dispNames[i] = "";
            }
        }
        
        // If not provided, default display name to program name, and short name to display name
        if (dispNames[0].length() == 0) {
            if (starsProg.getYukonName() != null) {
                dispNames[0] = starsProg.getYukonName();
            } else {
                dispNames[0] = "(none)";
            }
        }
        if (dispNames[1].length() == 0) {
            dispNames[1] = dispNames[0];
        }
        return dispNames;
    }

    public static String getInventoryLabel(StarsInventory starsInv) {
        String label = starsInv.getDeviceLabel();
        if (label.equals("")) {
            if (starsInv.getLMHardware() != null) {
                label = starsInv.getLMHardware().getManufacturerSerialNumber();
            } else if (starsInv.getMCT() != null) {
                label = starsInv.getMCT().getDeviceName();
            } else if (starsInv.getMeterNumber() != null) {
                label = starsInv.getMeterNumber();
            }
        }

        return label;
    }

    public static StarsEnrLMProgram getEnrollmentProgram(StarsEnrollmentPrograms categories, int programID) {
        for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
            StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
            for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
                if (category.getStarsEnrLMProgram(j).getProgramID() == programID) {
                    return category.getStarsEnrLMProgram(j);
                }
            }
        }

        return null;
    }
    
    public static void saveRequest(HttpServletRequest req, HttpSession session, String[] params) {
        Properties savedReq = new Properties();
        for (int i = 0; i < params.length; i++) {
            if (req.getParameter(params[i]) != null) {
                savedReq.setProperty( params[i], req.getParameter(params[i]) );
            }
        }
        
        session.setAttribute( ATT_LAST_SUBMITTED_REQUEST, savedReq );
    }

    public static void newEnergyCompanySaveRequest(HttpServletRequest req, HttpSession session,
                                   String[] params) {
        Properties savedReq = new Properties();
        for (int i = 0; i < params.length; i++) {
            if (req.getParameter(params[i]) != null) {
                savedReq.setProperty(params[i], req.getParameter(params[i]));
            }
        }

        session.setAttribute(ATT_LAST_SUBMITTED_REQUEST, savedReq);
    }

    public static ContactNotification getContactNotification(StarsCustomerContact contact, int notifCatID) {
        for (int i = 0; i < contact.getContactNotificationCount(); i++) {
            if (contact.getContactNotification(i) != null
                && contact.getContactNotification(i).getNotifCatID() == notifCatID) {
                return contact.getContactNotification(i);
            }
        }

        return null;
    }

    public static ContactNotification createContactNotification(String value, int notifCatID) {
        if (value != null && value.trim().length() > 0) {
            ContactNotification contNotif = new ContactNotification();
            contNotif.setNotifCatID(notifCatID);
            contNotif.setNotification(value);
            return contNotif;
        }
        return null;
    }

    @Deprecated
    public static StarsYukonUser getStarsYukonUser(final HttpSession session) {
        return (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
    }

    public static StarsEnergyCompanySettings removeEnergyCompanySettings(final HttpSession session) {
        StarsEnergyCompanySettings settings = (StarsEnergyCompanySettings) session.getAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
        session.removeAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
        return settings;
    }

    public static String removeErrorMessage(final HttpSession session) {
        String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
        session.removeAttribute(ServletUtils.ATT_ERROR_MESSAGE);
        return errorMsg;
    }

    public static String removeConfirmMessage(final HttpSession session) {
        String confirmMsg = (String) session.getAttribute(ServletUtils.ATT_CONFIRM_MESSAGE);
        session.removeAttribute(ServletUtils.ATT_CONFIRM_MESSAGE);
        return confirmMsg;
    }

    public static StarsCustAccountInformation removeAccountInformation(final HttpSession session) {
        StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) session.getAttribute(ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO);
        session.removeAttribute(ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO);
        return accountInfo;
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
    
    /** 
     * This method should be removed once Work Order processing is removed.  Do not use.
     * @param instant
     * @param format
     * @return
     */
    public static String formatInstant(Instant instant, SimpleDateFormat format) {
        return formatDate(instant.toDate(), format);
    }
    
    /**
     * This method should be removed once Work Order processing is removed.  Do not use.
     * @param str
     * @return
     */
    public static String forceNotEmpty(String str) {
        if (str == null || str.trim().equals(""))
            return "&nbsp;";
        return str;
    }
    
    /**
     * This method should be removed once Work Order processing is removed.  Do not use. 
     * @param starsAddr
     * @return
     */
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
    
    public static String hideUnsetNumber(int num, int num_unset) {
        return (num == num_unset)? "" : String.valueOf(num);
    }
    
    public static void closePopup(HttpServletResponse resp, String popupId) throws IOException {
        resp.getWriter().print("<script type=\"text/javascript\">");
        resp.getWriter().print("$('" + popupId + "').hide();");
        resp.getWriter().print("window.location = window.location;");
        resp.getWriter().print("</script>");
        
    }
    
}