<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.xml.soap.SOAPMessage" %>

<%@ page import="com.cannontech.common.constants.YukonListEntryTypes" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionListDefs" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.data.device.DeviceTypesFuncs" %>

<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.roles.operator.ConsumerInfoRole" %>
<%@ page import="com.cannontech.roles.operator.InventoryRole" %>
<%@ page import="com.cannontech.roles.operator.WorkOrderRole" %>

<%@ page import="com.cannontech.stars.xml.StarsFactory" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.types.*" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.stars.util.ServerUtils" %>
<%@ page import="com.cannontech.stars.web.action.*" %>
<%@ page import="com.cannontech.stars.web.servlet.InventoryManager" %>
<%@ page import="com.cannontech.stars.web.servlet.SOAPServer" %>
<%@ page import="com.cannontech.stars.xml.util.SOAPUtil" %>

<%@ page import="com.cannontech.roles.operator.CommercialMeteringRole"%>
<%@ page import="com.cannontech.database.cache.functions.EnergyCompanyFuncs" %>
<%@ page import="com.cannontech.graph.model.TrendModelType" %>
<%@ page import="com.cannontech.util.ServletUtil" %>

<cti:checklogin/>
 
<%
	LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
	if (com.cannontech.database.cache.functions.YukonUserFuncs.getLiteYukonUser(lYukonUser.getUserID()) != lYukonUser)
	{
		// User login no longer valid
		response.sendRedirect(request.getContextPath() + "/servlet/LoginController?ACTION=LOGOUT");
		return;
	}
	
	java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");
	java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm z");
	java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
	java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm z");
	java.text.SimpleDateFormat ampmTimeFormat = new java.text.SimpleDateFormat("hh:mm a");
	
	String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
	String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	String confirmMsg = (String) session.getAttribute(ServletUtils.ATT_CONFIRM_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_CONFIRM_MESSAGE);
	
	StarsYukonUser user = null;
	
	StarsGetEnergyCompanySettingsResponse ecSettings = null;
	StarsEnergyCompany energyCompany = null;
	StarsEnrollmentPrograms categories = null;
	StarsServiceCompanies companies = null;
	StarsCustomerFAQs customerFAQs = null;
	StarsExitInterviewQuestions exitQuestions = null;
	StarsDefaultThermostatSettings[] allDftThermoSettings = null;
	
	Hashtable selectionListTable = null;
	
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
	StarsLMProgramHistory programHistory = null;
	StarsCallReportHistory callHist = null;
	StarsServiceRequestHistory serviceHist = null;
	StarsUser userLogin = null;
	
	Object[][] gData = null;

	if (com.cannontech.database.cache.functions.AuthFuncs.checkRole(lYukonUser, ConsumerInfoRole.ROLEID) != null)
	{
		user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
		if (user == null || user.getYukonUser() != lYukonUser) {
			// This is logged in using the normal LoginController, not the StarsLoginController
			user = SOAPServer.getStarsYukonUser( lYukonUser );
			session.setAttribute(ServletUtils.ATT_STARS_YUKON_USER, user);
			
			GetEnergyCompanySettingsAction action = new GetEnergyCompanySettingsAction();
			
			SOAPMessage reqMsg = action.build(request, session);
			SOAPUtil.logSOAPMsgForOperation( reqMsg, "*** Send Message *** " );
			SOAPMessage respMsg = action.process(reqMsg, session);
			SOAPUtil.logSOAPMsgForOperation( respMsg, "*** Receive Message *** " );
			action.parse(reqMsg, respMsg, session);
		}
		
		ecSettings = (StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
		if (ecSettings != null) {   
			energyCompany = ecSettings.getStarsEnergyCompany();
			categories = ecSettings.getStarsEnrollmentPrograms();
			companies = ecSettings.getStarsServiceCompanies();
			customerFAQs = ecSettings.getStarsCustomerFAQs();
			exitQuestions = ecSettings.getStarsExitInterviewQuestions();
			allDftThermoSettings = ecSettings.getStarsDefaultThermostatSettings();
		}
		
		selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
		
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
			programHistory = programs.getStarsLMProgramHistory();
			callHist = accountInfo.getStarsCallReportHistory();
			serviceHist = accountInfo.getStarsServiceRequestHistory();
			
			userLogin = accountInfo.getStarsUser();
			if (userLogin == null) {
				userLogin = new StarsUser();
				userLogin.setUsername( "" );
				userLogin.setPassword( "" );
			}
		}
	}
	
	TimeZone tz = null;
	if(energyCompany != null)
		tz = TimeZone.getTimeZone( energyCompany.getTimeZone() );
	if (tz == null) tz = TimeZone.getDefault(); 
		
	datePart.setTimeZone(tz);
	timePart.setTimeZone(tz);
	dateFormat.setTimeZone(tz);
	timeFormat.setTimeZone(tz);
	dateTimeFormat.setTimeZone(tz);
	histDateFormat.setTimeZone(tz);
	
if( account != null)
{
    Class[] types = { Integer.class,String.class };  
    java.lang.String sqlString =  "SELECT DISTINCT GDEF.GRAPHDEFINITIONID, GDEF.NAME " +
                                  " FROM GRAPHDEFINITION GDEF, GRAPHCUSTOMERLIST GCL "+
                                  " WHERE GCL.CUSTOMERID = " + account.getCustomerID()+ 
                                  " AND GDEF.GRAPHDEFINITIONID = GCL.GRAPHDEFINITIONID " +
                                  " ORDER BY GDEF.NAME";

	gData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sqlString);
}
%>
	<jsp:useBean id="graphBean" class="com.cannontech.graph.GraphBean" scope="session">
		<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="graphBean" property="viewType" value="<%=TrendModelType.LINE_VIEW%>"/>
	<jsp:setProperty name="graphBean" property="start" value="<%=datePart.format(ServletUtil.getToday())%>"/>
	<jsp:setProperty name="graphBean" property="period" value="<%=ServletUtil.historicalPeriods[0]%>"/>
	<jsp:setProperty name="graphBean" property="gdefid" value="-1"/>	
	    <%-- intialize bean properties --%>
	</jsp:useBean>