<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ page import="java.util.*" %>
<%@ page import="javax.xml.soap.SOAPMessage" %>
<%@ page import="java.text.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.common.constants.YukonListEntry" %>
<%@ page import="com.cannontech.common.constants.YukonListEntryTypes" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionListDefs" %>
<%@ page import="com.cannontech.common.util.CtiUtilities" %>
<%@ page import="com.cannontech.common.version.VersionTools" %>
<%@ page import="com.cannontech.database.PoolManager" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation"%>
<%@ page import="com.cannontech.yukon.IDatabaseCache"%>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache"%>
<%@ page import="com.cannontech.database.cache.StarsDatabaseCache"%>
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
<%@ page import="com.cannontech.roles.application.TrendingRole"%>
<%@ page import="com.cannontech.roles.operator.ConsumerInfoRole" %>
<%@ page import="com.cannontech.roles.operator.InventoryRole" %>
<%@ page import="com.cannontech.roles.operator.WorkOrderRole" %>
<%@ page import="com.cannontech.roles.yukon.EnergyCompanyRole" %>
<%@ page import="com.cannontech.stars.core.dao.StarsCustAccountInformationDao"%>
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
<%@ page import="com.cannontech.clientutils.CTILogger"%>
<%@page import="com.cannontech.user.YukonUserContext"%>
<%@page import="com.cannontech.servlet.YukonUserContextUtils"%>
<%@page import="com.cannontech.core.service.DateFormattingService"%>
<%@page import="com.cannontech.spring.YukonSpringHook"%>
<%@page import="com.cannontech.core.service.DateFormattingService.DateFormatEnum"%>

<%@page import="com.cannontech.core.dao.AuthDao"%>
<%@page import="com.cannontech.database.data.customer.Customer"%>
<%@page import="com.cannontech.database.data.lite.LiteCustomer"%>
<%@page import="com.cannontech.database.Transaction"%>
<%@page import="com.cannontech.database.TransactionException"%>

<%@page import="com.cannontech.stars.core.dao.StarsCustAccountInformationDao"%>
<%@page import="com.cannontech.core.roleproperties.dao.RolePropertyDao"%>
<%@page import="com.cannontech.core.roleproperties.YukonRoleProperty"%>
<jsp:directive.page import="com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService"/>
<jsp:directive.page import="com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation"/>
<jsp:directive.page import="com.cannontech.database.data.lite.stars.LiteStarsAppliance"/>

<cti:includeScript link="PROTOTYPE" force="true"/>
<script language="JavaScript" type="text/javascript"  src="/JavaScript/yukonGeneral.js"></script>
 
<%
    final StarsCustAccountInformationDao starsCustAccountInformationDao = YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
    final DateFormattingService dateFormattingService = YukonSpringHook.getBean("dateFormattingService", DateFormattingService.class);
    final AuthDao authDao = YukonSpringHook.getBean("authDao", AuthDao.class);
    final RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
    
    StarsCustAccountInformation accountInfo = null;
    
    String errorMsg = null;
    String confirmMsg = null;
    LiteStarsEnergyCompany liteEC = null;
    StarsEnergyCompanySettings ecSettings = null;
    StarsEnergyCompany energyCompany = null;
    StarsEnrollmentPrograms categories = null;
    StarsServiceCompanies companies = null;
    StarsSubstations substations = null;
    StarsExitInterviewQuestions exitQuestions = null;
    Map<String,StarsCustSelectionList> selectionListTable = null;
    
    StarsCustomerAccount account = null;
    StreetAddress propAddr = null;
    StarsSiteInformation siteInfo = null;
    BillingAddress billAddr = null;
    PrimaryContact primContact = null;
    
    StarsResidenceInformation residence = null;
    StarsAppliances appliances = null;
    StarsAppliances unassignedAppliances = null;
    // This is a a list of all appliances for showing on the left nav bar.
    // Currently it is simple a combination of "appliances" and
    // "unassignedAppliances".  For better or worse, there are many pages that
    // depend on this.  YUK-7070 suggests this be cleaned up.
    List<StarsAppliance> allAppliances = new ArrayList<StarsAppliance>();
    StarsInventories inventories = null;
    StarsLMPrograms programs = null;
    StarsLMProgramHistory programHistory = null;
    StarsCallReportHistory callHist = null;
    StarsServiceRequestHistory serviceHist = null;
    StarsUser userLogin = null;
    
    Vector<GraphCustomerList> custGraphs = null;
    HashMap<Integer, List<Integer>> partialOptOutMap = null;
    
    com.cannontech.graph.GraphBean graphBean = null;
    
    final SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");
    final SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm z");
    final SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
    final SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
    final SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
    final SimpleDateFormat dateTimeExtFormat = new java.text.SimpleDateFormat("MM/dd/yy - HH:mm:ss");
    final SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm z");
    final SimpleDateFormat ampmTimeFormat = new java.text.SimpleDateFormat("hh:mm a");
    
    final DecimalFormat format_nv3 = new java.text.DecimalFormat("#0.000");
    final DecimalFormat format_nsec = new java.text.DecimalFormat("#0 secs");
    
    boolean starsExists = false;

    try {
        starsExists = VersionTools.starsExists(); 
    } catch (RuntimeException ignore) { }
    
    final LiteYukonUser lYukonUser = ServletUtil.getYukonUser(session);
    final StarsYukonUser user = ServletUtils.getStarsYukonUser(session);
    final YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(pageContext);
    
    if ((starsExists) && (user != null)) {
    
    	errorMsg = ServletUtils.removeErrorMessage(session);
    	confirmMsg = ServletUtils.removeConfirmMessage(session);
    
    	liteEC = StarsDatabaseCache.getInstance().getEnergyCompany(user.getEnergyCompanyID());
    	if(liteEC != null){
	        ecSettings = ServletUtils.removeEnergyCompanySettings(session);
	        if (ecSettings != null) { // ensure there is a new instance of StarsEnergyCompanySettings for each request
	            ecSettings = liteEC.getStarsEnergyCompanySettings(user);
	            session.setAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS, ecSettings);
	        }
	        
	        energyCompany = ecSettings.getStarsEnergyCompany();
	    	categories = ecSettings.getStarsEnrollmentPrograms();
	    	companies = ecSettings.getStarsServiceCompanies();
	    	substations = ecSettings.getStarsSubstations();
	    	exitQuestions = ecSettings.getStarsExitInterviewQuestions();
	        
	        TimeZone tz = TimeZone.getTimeZone(energyCompany.getTimeZone());
	        datePart.setTimeZone(tz);
	        timePart.setTimeZone(tz);
	        dateFormat.setTimeZone(tz);
	        timeFormat.setTimeZone(tz);
	        dateTimeFormat.setTimeZone(tz);
	        dateTimeExtFormat.setTimeZone(tz);
	        histDateFormat.setTimeZone(tz);
	        ampmTimeFormat.setTimeZone(tz);
	        
            if (ecSettings.getStarsCustomerSelectionLists() != null) {
                selectionListTable = new HashMap<String, StarsCustSelectionList>();
                
                for (int i = 0; i < ecSettings.getStarsCustomerSelectionLists().getStarsCustSelectionListCount(); i++) {
                    StarsCustSelectionList value = ecSettings.getStarsCustomerSelectionLists().getStarsCustSelectionList(i);
                    String key = value.getListName();
                    selectionListTable.put(key, value);
                }
                session.setAttribute(ServletUtils.ATT_CUSTOMER_SELECTION_LISTS, selectionListTable);
            }
	        
	        accountInfo = ServletUtils.removeAccountInformation(session);
	        if (accountInfo != null) { // ensure there is a new instance of StarsCustAccountInformation for each request
	            
	        	int acctId = accountInfo.getStarsCustomerAccount().getAccountID();
	        	
	        	accountInfo = liteEC.getStarsCustAccountInformation(acctId, true);
	        
	        	if (accountInfo != null) {
	        
		            session.setAttribute(ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO, accountInfo);
		    			
		            account = accountInfo.getStarsCustomerAccount();
		            propAddr = account.getStreetAddress();
		    		siteInfo = account.getStarsSiteInformation();
		    		billAddr = account.getBillingAddress();
		    		primContact = account.getPrimaryContact();
		    		residence = accountInfo.getStarsResidenceInformation();
		    		appliances = accountInfo.getStarsAppliances();
		    		unassignedAppliances = accountInfo.getUnassignedStarsAppliances();
		    		for (StarsAppliance starsAppliance : appliances) {
		    		    allAppliances.add(starsAppliance);
		    		}
		    		for (StarsAppliance starsAppliance : unassignedAppliances) {
		    		    allAppliances.add(starsAppliance);
		    		}
		    		inventories = accountInfo.getStarsInventories();
		    		programs = accountInfo.getStarsLMPrograms();
		    		programHistory = programs.getStarsLMProgramHistory();
		    		callHist = accountInfo.getStarsCallReportHistory();
		    		serviceHist = accountInfo.getStarsServiceRequestHistory();
		    		userLogin = accountInfo.getStarsUser();
		            
		    		try{
		                Customer cust = (Customer) com.cannontech.database.data.lite.LiteFactory.createDBPersistent(
		                                new LiteCustomer(account.getCustomerID()));
		                
		                Transaction<Customer> t = Transaction.createTransaction(Transaction.RETRIEVE, cust);
		                cust = t.execute();
		    
		                custGraphs = cust.getGraphVector();
		            } catch(TransactionException e){
		                com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
		            }
		            
		            // New enrollment, opt out, and control history tracking
		            //-------------------------------------------------------------------------------
		            final LMHardwareControlInformationService lmHardwareControlInformationService = YukonSpringHook.getBean("lmHardwareControlInformationService", LMHardwareControlInformationService.class);
		            
		            LiteStarsCustAccountInformation currentLiteAcctInfo = starsCustAccountInformationDao.getById(account.getAccountID(), liteEC.getEnergyCompanyID());
		            List<LiteStarsAppliance> currentAppList = currentLiteAcctInfo.getAppliances();
		            partialOptOutMap = new HashMap<Integer, List<Integer>>();
		            for(int x = 0; x < currentAppList.size(); x++) {
		                LiteStarsAppliance currentApp = currentAppList.get(x);
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
	        	}
	        }
	    
	    	graphBean = (com.cannontech.graph.GraphBean) session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	    	if (graphBean == null) {
	    		session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new com.cannontech.graph.GraphBean());
	    		graphBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	    	}
    	}
    
    }
	
%>