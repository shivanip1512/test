<%@ page language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.common.constants.RoleTypes" %>
<%@ page import="com.cannontech.database.cache.functions.CustomerFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.data.lite.LiteCICustomer" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.graph.model.TrendModelType" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ taglib uri="/WEB-INF/jruntags.jar" prefix="jrun" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/>

<%
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
    String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	LiteYukonUser liteYukonUser = null;
	LiteContact liteContact = null;
	LiteCICustomer liteCICustomer = null;
	int liteYukonUserID = -5;
	int customerID = -1; 
	try
	{
		liteYukonUser = (LiteYukonUser) session.getAttribute("YUKON_USER");
		liteYukonUserID = liteYukonUser.getLiteID();		
	}
	catch (IllegalStateException ise)
	{
	}
	if (liteYukonUser == null)
	{ 
		response.sendRedirect("/login.jsp"); return;
	}

	liteContact = CustomerFuncs.getCustomerContact(liteYukonUserID);
	liteCICustomer = CustomerFuncs.getOwnerCICustomer(liteContact.getContactID());
	customerID = liteCICustomer.getCustomerID();

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

