<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.common.constants.YukonListEntryTypes" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionListDefs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.types.*" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>

<%@ page import="javax.xml.soap.SOAPMessage" %>
<%@ page import="com.cannontech.stars.web.action.*" %>
<%@ page import="com.cannontech.stars.web.servlet.SOAPServer" %>
<%@ page import="com.cannontech.stars.xml.util.SOAPUtil" %>
<%@ page import="com.cannontech.common.constants.RoleTypes" %>

<%
	LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
	if (lYukonUser == null) {
		response.sendRedirect("/login.jsp"); return;
	}
	
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
    java.text.SimpleDateFormat ampmTimeFormat = new java.text.SimpleDateFormat("hh:mm a");
	
	String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
	String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	
	StarsGetEnergyCompanySettingsResponse ecSettings = null;
	StarsEnergyCompany energyCompany = null;
	StarsWebConfig ecWebSettings = null;
	StarsEnrollmentPrograms categories = null;
	StarsServiceCompanies companies = null;
	StarsExitInterviewQuestions exitQuestions = null;
	StarsDefaultThermostatSettings dftThermoSettings = null;
	Hashtable selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
	
	StarsCustAccountInformation accountInfo = null;
	StarsCustomerAccount account = null;
    StreetAddress propAddr = null;
    StarsSiteInformation siteInfo = null;
    BillingAddress billAddr = null;
    PrimaryContact primContact = null;
	
	StarsResidenceInformation residence = null;
	StarsAppliances appliances = null;
	StarsInventories inventories = null;
	StarsLMPrograms programs = null;
	StarsCallReportHistory callHist = null;
	StarsServiceRequestHistory serviceHist = null;
	StarsThermostatSettings thermoSettings = null;
	StarsUser userLogin = null;
	
	if (com.cannontech.database.cache.functions.AuthFuncs.checkRole(lYukonUser, RoleTypes.OPERATOR_CONSUMER_INFO) != null)
	{
		StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
		if (user == null || user.getYukonUser().getUserID() != lYukonUser.getUserID()) {	// This is logged in using the normal LoginController, not the StarsLoginController
			user = SOAPServer.getStarsYukonUser( lYukonUser );
			session.setAttribute(ServletUtils.ATT_STARS_YUKON_USER, user);
			
			MultiAction actions = new MultiAction();
			actions.addAction( new GetEnergyCompanySettingsAction(), request, session );
			
			SOAPMessage reqMsg = actions.build(request, session);
			SOAPUtil.logSOAPMsgForOperation( reqMsg, "*** Send Message *** " );
			SOAPMessage respMsg = actions.process(reqMsg, session);
			SOAPUtil.logSOAPMsgForOperation( respMsg, "*** Receive Message *** " );
			actions.parse(reqMsg, respMsg, session);
		}
		
		ecSettings = (StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
		if (ecSettings != null) {
			energyCompany = ecSettings.getStarsEnergyCompany();
			ecWebSettings = ecSettings.getStarsWebConfig();
			categories = ecSettings.getStarsEnrollmentPrograms();
			companies = ecSettings.getStarsServiceCompanies();
			exitQuestions = ecSettings.getStarsExitInterviewQuestions();
			dftThermoSettings = ecSettings.getStarsDefaultThermostatSettings();
		}
		
		Hashtable selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
		
		accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
		if (accountInfo != null) {
			account = accountInfo.getStarsCustomerAccount();
			propAddr = account.getStreetAddress();
			siteInfo = account.getStarsSiteInformation();
			billAddr = account.getBillingAddress();
			primContact = account.getPrimaryContact();
			
			residence = accountInfo.getStarsResidenceInformation();
			appliances = accountInfo.getStarsAppliances();
			inventories = accountInfo.getStarsInventories();
			programs = accountInfo.getStarsLMPrograms();
			callHist = accountInfo.getStarsCallReportHistory();
			serviceHist = accountInfo.getStarsServiceRequestHistory();
			thermoSettings = accountInfo.getStarsThermostatSettings();
			
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
	}
%>
