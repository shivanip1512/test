<%@ page language="java" %>
<%@ page import="com.cannontech.common.constants.RoleTypes" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ page import="com.cannontech.database.cache.functions.EnergyCompanyFuncs" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ page import="com.cannontech.common.util.CtiUtilities" %>

<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/>
<jsp:useBean id="YUKON_USER" scope="session" class="com.cannontech.database.data.lite.LiteYukonUser"/>

<%
    String content;

  	// YUKON_USER is an ugly name, give it an alias
	LiteYukonUser user = YUKON_USER;	
    long energyCompanyID =EnergyCompanyFuncs.getEnergyCompany(user).getEnergyCompanyID();

    LCConnectionServlet cs = (LCConnectionServlet) application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
    LoadcontrolCache cache = cs.getCache();
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
    
    String dbAlias = CtiUtilities.getDatabaseAlias();
%>
