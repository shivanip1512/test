<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>

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
	
	if (com.cannontech.database.cache.functions.AuthFuncs.checkRole(lYukonUser, RoleTypes.OPERATOR_CONSUMER_INFO) == null)
		return;
			
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
	
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
	
	StarsEnergyCompany energyCompany = (StarsEnergyCompany) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY );
	StarsWebConfig ecWebSettings = (StarsWebConfig) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_WEB_CONFIG );
	Hashtable selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
	StarsEnrollmentPrograms categories = (StarsEnrollmentPrograms) user.getAttribute( ServletUtils.ATT_ENROLLMENT_PROGRAMS );
	
	StarsCustAccountInformation accountInfo = null;
	StarsCustomerAccount account = null;
    StreetAddress propAddr = null;
    StarsSiteInformation siteInfo = null;
    BillingAddress billAddr = null;
    PrimaryContact primContact = null;
	
	StarsAppliances appliances = null;
	StarsInventories inventories = null;
	StarsLMPrograms programs = null;
	StarsServiceCompanies companies = null;
	StarsCallReportHistory callHist = null;
	StarsServiceRequestHistory serviceHist = null;
	StarsUser userLogin = null;
	
	accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
	if (accountInfo != null) {
		account = accountInfo.getStarsCustomerAccount();
		propAddr = account.getStreetAddress();
		siteInfo = account.getStarsSiteInformation();
		billAddr = account.getBillingAddress();
		primContact = account.getPrimaryContact();
		
		appliances = accountInfo.getStarsAppliances();
		inventories = accountInfo.getStarsInventories();
		programs = accountInfo.getStarsLMPrograms();
		companies = accountInfo.getStarsServiceCompanies();
		callHist = accountInfo.getStarsCallReportHistory();
		serviceHist = accountInfo.getStarsServiceRequestHistory();
		
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
	
	String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_ERROR_MESSAGE);
%>
