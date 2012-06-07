<%@page import="com.cannontech.clientutils.CTILogger"%>
<jsp:directive.page import="com.cannontech.common.version.VersionTools"/>
<jsp:directive.page import="com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation"/>
<jsp:directive.page import="com.cannontech.stars.database.data.lite.LiteStarsAppliance"/><%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="starsLMPermissionBean" class="com.cannontech.stars.web.bean.StarsLMPermissionBean" scope="page"/>

<%@ page import="java.util.*" %>
<%@ page import="javax.xml.soap.SOAPMessage" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.common.constants.YukonListEntry" %>
<%@ page import="com.cannontech.common.constants.YukonListEntryTypes" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionListDefs" %>
<%@ page import="com.cannontech.stars.database.cache.StarsDatabaseCache" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany" %>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.roles.application.TrendingRole" %>
<%@ page import="com.cannontech.roles.consumer.ResidentialCustomerRole" %>
<%@ page import="com.cannontech.roles.operator.ConsumerInfoRole" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.stars.util.StarsUtils" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.stars.web.action.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.types.*" %>
<%@ page import="com.cannontech.stars.xml.util.SOAPUtil" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<jsp:directive.page import="com.cannontech.spring.YukonSpringHook"/>
<jsp:directive.page import="com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService"/>
<jsp:directive.page import="com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation"/>
<jsp:directive.page import="com.cannontech.stars.database.data.lite.LiteStarsAppliance"/>

<%
	LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
	StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
	if (user != null)
	{
		if (user.getUserID() != lYukonUser.getUserID()) {
			// Stars user doesn't match the yukon user, clear it
			user = null;
		}
		else if (!user.equals(StarsDatabaseCache.getInstance().getStarsYukonUser(lYukonUser))) {
			// User login no longer valid
            CTILogger.error("Stars User login no longer valid");
			response.sendRedirect(request.getContextPath() + "/servlet/LoginController?ACTION=LOGOUT");
			return;
		}
	}
    
    /*This is now preset by the SessionInitializer so it should not be null*/
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
	StarsInventories optOutChoices = null;
	StarsAppliances appliances = null;
	StarsUser userLogin = null;
	LiteStarsCustAccountInformation liteAcctInfo = null;
	
	accountInfo = ServletUtils.removeAccountInformation(session);

	if (accountInfo != null) {
        accountInfo = liteEC.getStarsCustAccountInformation(accountInfo.getStarsCustomerAccount().getAccountID(), true);
        session.setAttribute(ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO, accountInfo);
        
		account = accountInfo.getStarsCustomerAccount();
		liteAcctInfo = liteEC.getCustAccountInformation(account.getAccountID(), true);
		propAddr = account.getStreetAddress();
		siteInfo = account.getStarsSiteInformation();
		billAddr = account.getBillingAddress();
		primContact = account.getPrimaryContact();
		userLogin = accountInfo.getStarsUser();
		
		programs = accountInfo.getStarsLMPrograms();
		programHistory = programs.getStarsLMProgramHistory();
		
		optOutChoices = accountInfo.getStarsInventories();
		thermostats = new StarsInventories();
		StarsInventories inventories = accountInfo.getStarsInventories();
		for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
			StarsInventory inventory = inventories.getStarsInventory(i);
			if (inventory.getLMHardware() != null && inventory.getLMHardware().getStarsThermostatSettings() != null)
				thermostats.addStarsInventory( inventory );
		}
		
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
	
	// New enrollment, opt out, and control history tracking
    //-------------------------------------------------------------------------------
    LMHardwareControlInformationService lmHardwareControlInformationService = (LMHardwareControlInformationService) YukonSpringHook.getBean("lmHardwareControlInformationService");
	List<LiteStarsAppliance> currentAppList = liteAcctInfo.getAppliances();
	HashMap<Integer, List<Integer>> partialOptOutMap = new HashMap<Integer, List<Integer>>();
	for(int x = 0; x < currentAppList.size(); x++) {
		LiteStarsAppliance currentApp = (LiteStarsAppliance)currentAppList.get(x);
        List<Integer> inventoryNotOptedOut = lmHardwareControlInformationService.getInventoryNotOptedOutForThisProgram(currentApp.getProgramID(), account.getAccountID());
        for(Integer invenId : inventoryNotOptedOut) {
        	if(currentApp.getInventoryID() == invenId) {
        		List<Integer> progInventory = partialOptOutMap.get(currentApp.getProgramID());
        		if(progInventory == null) {
        			partialOptOutMap.put(currentApp.getProgramID(), new ArrayList<Integer>());
        			progInventory = partialOptOutMap.get(currentApp.getProgramID());
        		}
        		if(! progInventory.contains(invenId))
        			progInventory.add(invenId);
        	}
        }
	}
	//-------------------------------------------------------------------------------
%>

<%pageContext.setAttribute("liteEC",liteEC);%>
<c:set target="${starsLMPermissionBean}" property="energyCompany" value="${liteEC}" />
<%pageContext.setAttribute("currentUser", lYukonUser);%>
<c:set target="${starsLMPermissionBean}" property="currentUser" value="${currentUser}" />
<%pageContext.setAttribute("starsEnrolledLMPrograms", programs);%>
<c:set target="${starsLMPermissionBean}" property="starsEnrolledLMPrograms" value="${starsEnrolledLMPrograms}" />