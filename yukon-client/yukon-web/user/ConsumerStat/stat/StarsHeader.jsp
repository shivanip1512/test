<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.types.*" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>

<%@ page import="javax.xml.soap.SOAPMessage" %>
<%@ page import="com.cannontech.stars.web.action.*" %>
<%@ page import="com.cannontech.stars.web.servlet.SOAPServer" %>
<%@ page import="com.cannontech.stars.xml.util.SOAPUtil" %>
<%@ page import="com.cannontech.common.constants.RoleTypes" %>

<%
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yy");
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	
	LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
	if (lYukonUser == null) {
		response.sendRedirect("/login.jsp"); return;
	}
	
	StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
	if (user == null || user.getYukonUser().getUserID() != lYukonUser.getUserID()) {	// This is logged in using the normal LoginController, not the StarsLoginController
		user = SOAPServer.getStarsYukonUser( lYukonUser );
		session.setAttribute(ServletUtils.ATT_STARS_YUKON_USER, user);
		
		MultiAction actions = new MultiAction();
		actions.addAction( new GetEnrollmentProgramsAction(), request, session );
		actions.addAction( new GetCustomerFAQsAction(), request, session );
		actions.addAction( new GetCustAccountAction(), request, session );
		
		SOAPMessage reqMsg = actions.build(request, session);
		SOAPUtil.logSOAPMsgForOperation( reqMsg, "*** Send Message *** " );
		SOAPMessage respMsg = actions.process(reqMsg, session);
		SOAPUtil.logSOAPMsgForOperation( respMsg, "*** Receive Message *** " );
		actions.parse(reqMsg, respMsg, session);
	}
	
	StarsCustAccountInformation accountInfo = null;
	StarsCustomerAccount account = null;
    StreetAddress propAddr = null;
    StarsSiteInformation siteInfo = null;
    BillingAddress billAddr = null;
    PrimaryContact primContact = null;
	
	StarsLMPrograms programs = null;
	StarsThermostatSettings thermoSettings = null;
	StarsDefaultThermostatSettings dftThermoSettings = null;
	StarsUser userLogin = null;
	
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
		
		userLogin = accountInfo.getStarsUser();
		if (userLogin == null) {
			userLogin = new StarsUser();
			userLogin.setUsername( "" );
			userLogin.setPassword( "" );
		}
		
		if (account.getTimeZone() != null && !account.getTimeZone().equals("") && !account.getTimeZone().equals("(none)")) {
			TimeZone tz = TimeZone.getTimeZone( account.getTimeZone() );
			datePart.setTimeZone(tz);
			dateFormat.setTimeZone(tz);
			histDateFormat.setTimeZone(tz);
		}
	}
	
	StarsGetEnrollmentProgramsResponse categories = (StarsGetEnrollmentProgramsResponse) user.getAttribute( ServletUtils.ATT_ENROLLMENT_PROGRAMS );
	StarsGetCustomerFAQsResponse customerFAQs = (StarsGetCustomerFAQsResponse) user.getAttribute( ServletUtils.ATT_CUSTOMER_FAQS );
	
	String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_ERROR_MESSAGE);
%>
