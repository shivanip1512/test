<%@ page language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.roles.application.*"%>
<%@ page import="com.cannontech.roles.cicustomer.*"%>
<%@ page import="com.cannontech.database.cache.functions.ContactFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.YukonUserFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.EnergyCompanyFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.data.lite.LiteCICustomer" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.graph.model.TrendModelType" %>

<%@ page import="com.cannontech.roles.yukon.EnergyCompanyRole" %>

<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ taglib uri="/WEB-INF/jruntags.jar" prefix="jrun" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/>

<%
	LiteYukonUser liteYukonUser = (LiteYukonUser) session.getAttribute("YUKON_USER");
	int liteYukonUserID = liteYukonUser.getLiteID();

	LiteContact liteContact = YukonUserFuncs.getLiteContact(liteYukonUserID);
	LiteCICustomer liteCICustomer = ContactFuncs.getCICustomer(liteContact.getContactID());
	
	int customerID = liteCICustomer.getCustomerID();
	int energyCompanyID =EnergyCompanyFuncs.getEnergyCompany(liteYukonUser).getEnergyCompanyID();
	
	LiteYukonUser ecUser = EnergyCompanyFuncs.getEnergyCompanyUser(energyCompanyID);
	TimeZone tz = TimeZone.getTimeZone(AuthFuncs.getRolePropertyValue(ecUser, EnergyCompanyRole.DEFAULT_TIME_ZONE));

	java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
    
    datePart.setTimeZone(tz);
    timePart.setTimeZone(tz);
    dateFormat.setTimeZone(tz);
    
    String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
	
    Class[] types = { Integer.class,String.class };    
	Object[][] gData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, "SELECT GDEF.GRAPHDEFINITIONID, GDEF.NAME FROM GRAPHDEFINITION GDEF, GRAPHCUSTOMERLIST GCL WHERE GDEF.GRAPHDEFINITIONID=GCL.GRAPHDEFINITIONID AND GCL.CUSTOMERID = " + customerID+ " ORDER BY GDEF.NAME", types );
%>

	<jsp:useBean id="graphBean" class="com.cannontech.graph.GraphBean" scope="session">
		<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="graphBean" property="viewType" value="<%=TrendModelType.LINE_VIEW%>"/>
	<jsp:setProperty name="graphBean" property="start" value="<%=datePart.format(ServletUtil.getToday())%>"/>
	<jsp:setProperty name="graphBean" property="period" value="<%=ServletUtil.historicalPeriods[0]%>"/>
	<jsp:setProperty name="graphBean" property="gdefid" value="-1"/>	
	    <%-- intialize bean properties --%>
	</jsp:useBean>

