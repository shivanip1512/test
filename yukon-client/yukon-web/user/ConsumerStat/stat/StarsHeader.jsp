<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.types.*" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionListDefs" %>

<%@ page import="javax.xml.soap.SOAPMessage" %>
<%@ page import="com.cannontech.stars.web.action.*" %>
<%@ page import="com.cannontech.stars.web.servlet.SOAPServer" %>
<%@ page import="com.cannontech.stars.xml.util.SOAPUtil" %>
<%@ page import="com.cannontech.roles.consumer.ResidentialCustomerRole" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>

<cti:checklogin/>
<%
	LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
	if (com.cannontech.database.cache.functions.YukonUserFuncs.getLiteYukonUser(lYukonUser.getUserID()) != lYukonUser)
	{
		// User login no longer valid
		response.sendRedirect(request.getContextPath() + "/servlet/LoginController?ACTION=LOGOUT");
		return;
	}
	
	java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yy");
	java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
	java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	java.text.SimpleDateFormat ampmTimeFormat = new java.text.SimpleDateFormat("hh:mm a");
	
	String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	String confirmMsg = (String) session.getAttribute(ServletUtils.ATT_CONFIRM_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_CONFIRM_MESSAGE);
	
	StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
	if (user == null || user.getYukonUser() != lYukonUser) {
		// This is logged in using the normal LoginController, not the StarsLoginController
		user = SOAPServer.getStarsYukonUser( lYukonUser );
		session.setAttribute(ServletUtils.ATT_STARS_YUKON_USER, user);
		
		MultiAction actions = new MultiAction();
		actions.addAction( new GetEnergyCompanySettingsAction(), request, session );
		actions.addAction( new GetCustAccountAction(), request, session );
		
		SOAPMessage reqMsg = actions.build(request, session);
		SOAPUtil.logSOAPMsgForOperation( reqMsg, "*** Send Message *** " );
		SOAPMessage respMsg = actions.process(reqMsg, session);
		SOAPUtil.logSOAPMsgForOperation( respMsg, "*** Receive Message *** " );
		actions.parse(reqMsg, respMsg, session);
	}
	
	StarsGetEnergyCompanySettingsResponse ecSettings = (StarsGetEnergyCompanySettingsResponse)
			user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
	StarsEnergyCompany energyCompany = ecSettings.getStarsEnergyCompany();
	StarsEnrollmentPrograms categories = ecSettings.getStarsEnrollmentPrograms();
	StarsCustomerFAQs customerFAQs = ecSettings.getStarsCustomerFAQs();
	StarsExitInterviewQuestions exitQuestions = ecSettings.getStarsExitInterviewQuestions();
	StarsDefaultThermostatSettings dftThermoSettings = ecSettings.getStarsDefaultThermostatSettings();
	
	StarsCustAccountInformation accountInfo = null;
	StarsCustomerAccount account = null;
    StreetAddress propAddr = null;
    StarsSiteInformation siteInfo = null;
    BillingAddress billAddr = null;
    PrimaryContact primContact = null;
	
	StarsLMPrograms programs = null;
	StarsThermostatSettings thermoSettings = null;
	StarsUser userLogin = null;
	
	Hashtable selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
	
	accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
	if (accountInfo != null) {
		account = accountInfo.getStarsCustomerAccount();
		propAddr = account.getStreetAddress();
		siteInfo = account.getStarsSiteInformation();
		billAddr = account.getBillingAddress();
		primContact = account.getPrimaryContact();
		
		programs = accountInfo.getStarsLMPrograms();
		thermoSettings = accountInfo.getStarsThermostatSettings();
		
		userLogin = accountInfo.getStarsUser();
		if (userLogin == null) {
			userLogin = new StarsUser();
			userLogin.setUsername( "" );
			userLogin.setPassword( "" );
		}
	}
	
	TimeZone tz = TimeZone.getDefault(); 
	if(energyCompany != null)
		tz = TimeZone.getTimeZone( energyCompany.getTimeZone() );
		
	timePart.setTimeZone(tz);
	datePart.setTimeZone(tz);
	dateFormat.setTimeZone(tz);
	histDateFormat.setTimeZone(tz);
	
	String logOffUrl = "";
	{
		String link = com.cannontech.stars.util.ServerUtils.forceNotNone(
				AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LINK_LOG_OFF) );
		if (link.length() > 0) logOffUrl = "&REDIRECT=" + link;
	}
%>
