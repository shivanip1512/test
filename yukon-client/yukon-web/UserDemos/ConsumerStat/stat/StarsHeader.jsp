<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.types.*" %>
<%@ page import="com.cannontech.stars.web.StarsUser" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>

<%
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yy");
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	
	StarsUser user = null;
	try {
		user = (StarsUser) session.getAttribute("USER");
	}
	catch (IllegalStateException ise) {}
	if (user == null) {
		response.sendRedirect("/login.jsp"); return;
	}
	
    String dbAlias = user.getDatabaseAlias();	
	
	StarsCustAccountInformation accountInfo = null;
	StarsCustomerAccount account = null;
    StreetAddress propAddr = null;
    StarsSiteInformation siteInfo = null;
    BillingAddress billAddr = null;
    PrimaryContact primContact = null;
	
	StarsLMPrograms programs = null;
	StarsThermostatSettings thermoSettings = null;
	StarsDefaultThermostatSettings dftThermoSettings = null;
	StarsGetEnrollmentProgramsResponse categories = null;
	
	accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
	if (accountInfo != null) {
		account = accountInfo.getStarsCustomerAccount();
		propAddr = account.getStreetAddress();
		siteInfo = account.getStarsSiteInformation();
		billAddr = account.getBillingAddress();
		primContact = account.getPrimaryContact();
		
		programs = accountInfo.getStarsLMPrograms();
		thermoSettings = accountInfo.getStarsThermostatSettings();
		dftThermoSettings = accountInfo.getStarsDefaultThermostatSettings();
		categories = (StarsGetEnrollmentProgramsResponse) user.getAttribute( ServletUtils.ATT_ENROLLMENT_PROGRAMS );
		
		TimeZone tz = TimeZone.getTimeZone( account.getTimeZone() );
		datePart.setTimeZone(tz);
		dateFormat.setTimeZone(tz);
		histDateFormat.setTimeZone(tz);
	}
	
	String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_ERROR_MESSAGE);
%>
