<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<%@ page import="java.util.*" %>
<%@ page import="javax.xml.soap.SOAPMessage" %>

<%@ page import="com.cannontech.common.constants.YukonListEntryTypes" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionListDefs" %>
<%@ page import="com.cannontech.database.cache.StarsDatabaseCache" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany" %>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.roles.cicustomer.CommercialMeteringRole" %>
<%@ page import="com.cannontech.roles.consumer.ResidentialCustomerRole" %>
<%@ page import="com.cannontech.roles.operator.ConsumerInfoRole" %>
<%@ page import="com.cannontech.stars.util.ServerUtils" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.stars.web.action.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.types.*" %>
<%@ page import="com.cannontech.stars.xml.util.SOAPUtil" %>
<%@ page import="com.cannontech.util.ServletUtil" %>

<cti:checklogin/>

<%
	LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
	StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
	
	if (user != null)
	{
		if (user.getUserID() != lYukonUser.getUserID()) {
			// Stars user doesn't match the yukon user, clear it
			user = null;
		}
		else if (user != StarsDatabaseCache.getInstance().getStarsYukonUser(lYukonUser)) {
			// User login no longer valid
			response.sendRedirect(request.getContextPath() + "/servlet/LoginController?ACTION=LOGOUT");
			return;
		}
	}
	
	if (user == null) {
		user = StarsDatabaseCache.getInstance().getStarsYukonUser( lYukonUser );
		if (user == null) {
			// Something wrong happened when instantiating the StarsYukonUser
			response.sendRedirect(request.getContextPath() + "/servlet/LoginController?ACTION=LOGOUT");
			return;
		}
		
		session.setAttribute(ServletUtils.ATT_STARS_YUKON_USER, user);
		
		// Get the energy company settings
		LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		StarsEnergyCompanySettings settings = liteEC.getStarsEnergyCompanySettings( user );
		session.setAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS, settings );
		
		// Get customer account information
		GetCustAccountAction action = new GetCustAccountAction();
		SOAPMessage reqMsg = action.build(request, session);
		SOAPMessage respMsg = action.process(reqMsg, session);
		action.parse(reqMsg, respMsg, session);
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
	
	LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
	
	StarsEnergyCompanySettings ecSettings = (StarsEnergyCompanySettings)
			session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
	StarsEnergyCompany energyCompany = ecSettings.getStarsEnergyCompany();
	StarsEnrollmentPrograms categories = ecSettings.getStarsEnrollmentPrograms();
	StarsCustomerFAQs customerFAQs = ecSettings.getStarsCustomerFAQs();
	StarsExitInterviewQuestions exitQuestions = ecSettings.getStarsExitInterviewQuestions();
	StarsDefaultThermostatSchedules dftThermoSchedules = ecSettings.getStarsDefaultThermostatSchedules();
	
	Hashtable selectionListTable = new Hashtable();
	if (ecSettings.getStarsCustomerSelectionLists() != null) {
		for (int i = 0; i < ecSettings.getStarsCustomerSelectionLists().getStarsCustSelectionListCount(); i++) {
			StarsCustSelectionList list = ecSettings.getStarsCustomerSelectionLists().getStarsCustSelectionList(i);
			selectionListTable.put( list.getListName(), list );
		}
		session.setAttribute(ServletUtils.ATT_CUSTOMER_SELECTION_LISTS, selectionListTable);
	}
	
	StarsCustAccountInformation accountInfo = null;
	StarsCustomerAccount account = null;
    StreetAddress propAddr = null;
    StarsSiteInformation siteInfo = null;
    BillingAddress billAddr = null;
    PrimaryContact primContact = null;
	
	StarsLMPrograms programs = null;
	StarsLMProgramHistory programHistory = null;
	StarsInventories thermostats = null;
	StarsSavedThermostatSchedules thermSchedules = null;
	StarsAppliances appliances = null;
	StarsUser userLogin = null;
	
	accountInfo = (StarsCustAccountInformation) session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
	
	// Register the account as active
	while (accountInfo != null && !liteEC.registerActiveAccount(accountInfo)) {
		accountInfo = liteEC.getStarsCustAccountInformation(accountInfo.getStarsCustomerAccount().getAccountID(), true);
		if (accountInfo != null)
			session.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, accountInfo);
		else
			ServletUtils.removeTransientAttributes(session);
	}
	
	if (accountInfo != null) {
		account = accountInfo.getStarsCustomerAccount();
		propAddr = account.getStreetAddress();
		siteInfo = account.getStarsSiteInformation();
		billAddr = account.getBillingAddress();
		primContact = account.getPrimaryContact();
		userLogin = accountInfo.getStarsUser();
		
		programs = accountInfo.getStarsLMPrograms();
		programHistory = programs.getStarsLMProgramHistory();
		
		thermostats = new StarsInventories();
		StarsInventories inventories = accountInfo.getStarsInventories();
		for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
			StarsInventory inventory = inventories.getStarsInventory(i);
			if (inventory.getLMHardware() != null && inventory.getLMHardware().getStarsThermostatSettings() != null)
				thermostats.addStarsInventory( inventory );
		}
		thermSchedules = accountInfo.getStarsSavedThermostatSchedules();
		
		appliances = accountInfo.getStarsAppliances();
	}
	
	TimeZone tz = TimeZone.getDefault(); 
	if(energyCompany != null)
		tz = TimeZone.getTimeZone( energyCompany.getTimeZone() );
		
	timePart.setTimeZone(tz);
	datePart.setTimeZone(tz);
	dateFormat.setTimeZone(tz);
	histDateFormat.setTimeZone(tz);
	
   
    String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
	
    Class[] types = { Integer.class,String.class };    
    java.lang.String sqlString =  "SELECT GDEF.GRAPHDEFINITIONID, GDEF.NAME " +
                                  " FROM GRAPHDEFINITION GDEF, GRAPHCUSTOMERLIST GCL "+
                                  " WHERE GDEF.GRAPHDEFINITIONID=GCL.GRAPHDEFINITIONID "+
                                  " AND GCL.CUSTOMERID = " + account.getCustomerID()+ " ORDER BY GDEF.NAME";

	Object[][] gData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sqlString, types );
	
	com.cannontech.graph.GraphBean graphBean = (com.cannontech.graph.GraphBean) session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	if(graphBean == null)
	{
		session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new com.cannontech.graph.GraphBean());
		graphBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	}
%>