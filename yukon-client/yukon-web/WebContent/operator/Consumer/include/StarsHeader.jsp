<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<%@ page import="java.util.*" %>
<%@ page import="javax.xml.soap.SOAPMessage" %>

<%@ page import="com.cannontech.common.constants.YukonListEntry" %>
<%@ page import="com.cannontech.common.constants.YukonListEntryTypes" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionListDefs" %>
<%@ page import="com.cannontech.common.util.CtiUtilities" %>
<%@ page import="com.cannontech.common.version.VersionTools" %>
<%@ page import="com.cannontech.database.PoolManager" %>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache"%>
<%@ page import="com.cannontech.database.cache.StarsDatabaseCache"%>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.EnergyCompanyFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.YukonListFuncs" %>
<%@ page import="com.cannontech.database.data.device.DeviceTypesFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteGraphDefinition"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany" %>
<%@ page import="com.cannontech.database.db.graph.GraphCustomerList" %>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.roles.consumer.ResidentialCustomerRole" %>
<%@ page import="com.cannontech.roles.operator.AdministratorRole" %>
<%@ page import="com.cannontech.roles.operator.CommercialMeteringRole"%>
<%@ page import="com.cannontech.roles.operator.ConsumerInfoRole" %>
<%@ page import="com.cannontech.roles.operator.InventoryRole" %>
<%@ page import="com.cannontech.roles.operator.WorkOrderRole" %>
<%@ page import="com.cannontech.roles.yukon.EnergyCompanyRole" %>
<%@ page import="com.cannontech.stars.util.ECUtils" %> 
<%@ page import="com.cannontech.stars.util.InventoryUtils" %> 
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.stars.util.StarsUtils" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.stars.web.action.*" %>
<%@ page import="com.cannontech.stars.web.util.InventoryManagerUtil" %>
<%@ page import="com.cannontech.stars.web.util.StarsAdminUtil" %>
<%@ page import="com.cannontech.stars.xml.StarsFactory" %>
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
	
	if (user == null && VersionTools.starsExists()) {
		user = StarsDatabaseCache.getInstance().getStarsYukonUser( lYukonUser );
		if (user != null) {
			session.setAttribute(ServletUtils.ATT_STARS_YUKON_USER, user);
			
			// Get the energy company settings
			LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			StarsEnergyCompanySettings settings = liteEC.getStarsEnergyCompanySettings( user );
			session.setAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS, settings );
		}
	}
	
	java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");
	java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm z");
	java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
	java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm z");
	java.text.SimpleDateFormat ampmTimeFormat = new java.text.SimpleDateFormat("hh:mm a");
	
	java.text.DecimalFormat format_nv3 = new java.text.DecimalFormat("#0.000");
	java.text.DecimalFormat format_nsec = new java.text.DecimalFormat("#0 secs");
	
	String dbAlias = CtiUtilities.getDatabaseAlias();
	String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	String confirmMsg = (String) session.getAttribute(ServletUtils.ATT_CONFIRM_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_CONFIRM_MESSAGE);
	
	LiteStarsEnergyCompany liteEC = null;
	
	StarsEnergyCompanySettings ecSettings = null;
	StarsEnergyCompany energyCompany = null;
	StarsEnrollmentPrograms categories = null;
	StarsServiceCompanies companies = null;
	StarsSubstations substations = null;
	StarsCustomerFAQs customerFAQs = null;
	StarsExitInterviewQuestions exitQuestions = null;
	StarsDefaultThermostatSchedules dftThermoSchedules = null;
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
	StarsSavedThermostatSchedules thermSchedules = null;
	StarsUser userLogin = null;
	
	java.util.Vector custGraphs = null;

	if (user != null) {
		liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		ecSettings = (StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
		if (ecSettings != null) {   
			energyCompany = ecSettings.getStarsEnergyCompany();
			categories = ecSettings.getStarsEnrollmentPrograms();
			companies = ecSettings.getStarsServiceCompanies();
			substations = ecSettings.getStarsSubstations();
			customerFAQs = ecSettings.getStarsCustomerFAQs();
			exitQuestions = ecSettings.getStarsExitInterviewQuestions();
			dftThermoSchedules = ecSettings.getStarsDefaultThermostatSchedules();
			
			if (ecSettings.getStarsCustomerSelectionLists() != null) {
				selectionListTable = new Hashtable();
				for (int i = 0; i < ecSettings.getStarsCustomerSelectionLists().getStarsCustSelectionListCount(); i++) {
					StarsCustSelectionList list = ecSettings.getStarsCustomerSelectionLists().getStarsCustSelectionList(i);
					selectionListTable.put( list.getListName(), list );
				}
				session.setAttribute(ServletUtils.ATT_CUSTOMER_SELECTION_LISTS, selectionListTable);
				
				String value = AuthFuncs.getRolePropertyValue(lYukonUser, AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY);
				if (value != null && value.equals(StarsAdminUtil.FIRST_TIME)
					&& selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE) != null)
				{
					// The default operator login for the first time, edit the device type list first!
					com.cannontech.database.data.lite.LiteYukonGroup adminGroup = liteEC.getOperatorAdminGroup();
					if (StarsAdminUtil.updateGroupRoleProperty(
						adminGroup, AdministratorRole.ROLEID, AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY, CtiUtilities.TRUE_STRING))
					{
						com.cannontech.stars.util.ServerUtils.handleDBChange(
							adminGroup, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE );
					}
					
					session.setAttribute(ServletUtils.ATT_MSG_PAGE_REDIRECT, request.getContextPath() + "/operator/Admin/SelectionList.jsp?List=DeviceType");
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "This is the first time you have logged in as the default operator. Please edit the device type list and other energy company settings.");
					response.sendRedirect(request.getContextPath() + "/operator/Admin/Message.jsp?delay=0");
					return;
				}
			}
		}
		
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
			
			residence = accountInfo.getStarsResidenceInformation();
			appliances = accountInfo.getStarsAppliances();
			inventories = accountInfo.getStarsInventories();
			programs = accountInfo.getStarsLMPrograms();
			programHistory = programs.getStarsLMProgramHistory();
			callHist = accountInfo.getStarsCallReportHistory();
			serviceHist = accountInfo.getStarsServiceRequestHistory();
			thermSchedules = accountInfo.getStarsSavedThermostatSchedules();
			userLogin = accountInfo.getStarsUser();
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
		try{
			com.cannontech.database.data.customer.Customer cust = null;
			cust = (com.cannontech.database.data.customer.Customer)com.cannontech.database.data.lite.LiteFactory.createDBPersistent(
							new com.cannontech.database.data.lite.LiteCustomer(account.getCustomerID()));
		
			com.cannontech.database.Transaction t = com.cannontech.database.Transaction.createTransaction(
													com.cannontech.database.Transaction.RETRIEVE, cust);
			cust = (com.cannontech.database.data.customer.Customer)t.execute();
			custGraphs = cust.getGraphVector();
		}
		catch( java.lang.Exception e){
			com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
		}
	}

	com.cannontech.graph.GraphBean graphBean = (com.cannontech.graph.GraphBean) session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	if(graphBean == null)
	{
		session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new com.cannontech.graph.GraphBean());
		graphBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	}
%>