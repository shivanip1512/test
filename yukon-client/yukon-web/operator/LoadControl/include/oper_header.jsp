<%@ page language="java" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ page import="com.cannontech.database.cache.functions.EnergyCompanyFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ page import="com.cannontech.common.util.CtiUtilities" %> 
<%@ page import="com.cannontech.util.ServletUtil" %>

<%@ page import="com.cannontech.roles.yukon.EnergyCompanyRole" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.roles.operator.DirectLoadcontrolRole" %>
<%@ page import="com.cannontech.roles.operator.DirectCurtailmentRole" %>
<%@ page import="com.cannontech.roles.operator.EnergyBuybackRole" %>

<%@ page import="java.util.TimeZone" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>

<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
 
<cti:checklogin/>
<jsp:useBean id="YUKON_USER" scope="session" class="com.cannontech.database.data.lite.LiteYukonUser"/>

<%  
    String content;

  	// YUKON_USER is an ugly name, give it an alias
	LiteYukonUser user = YUKON_USER;	
    int energyCompanyID = 1;
    LiteYukonUser ecUser = null;
    TimeZone tz = TimeZone.getDefault();	//init to the timezone of the running program
    if( EnergyCompanyFuncs.getEnergyCompany(user) != null)
    {
    	energyCompanyID = EnergyCompanyFuncs.getEnergyCompany(user).getEnergyCompanyID();
	    ecUser = EnergyCompanyFuncs.getEnergyCompanyUser(energyCompanyID);
		tz = TimeZone.getTimeZone(AuthFuncs.getRolePropertyValue(ecUser, EnergyCompanyRole.DEFAULT_TIME_ZONE));
	}
    LCConnectionServlet cs = (LCConnectionServlet) application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
    LoadcontrolCache cache = cs.getCache();
   	
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
    
    datePart.setTimeZone(tz);
    timePart.setTimeZone(tz);
    dateFormat.setTimeZone(tz);
    
    String dbAlias = CtiUtilities.getDatabaseAlias();
%>
