<%@ page language="java" session="true" %>
<%@ page import="com.cannontech.database.cache.functions.ContactFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.YukonUserFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.data.lite.LiteCICustomer" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.roles.cicustomer.CommercialMeteringRole"%>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/>
<%
	LiteYukonUser liteYukonUser = (LiteYukonUser) session.getAttribute("YUKON_USER");
	if (liteYukonUser == null)
	{ 
		response.sendRedirect("/login.jsp"); return;
	}

    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
    String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	LiteContact liteContact = YukonUserFuncs.getLiteContact(liteYukonUser.getLiteID());
	LiteCICustomer liteCICustomer = ContactFuncs.getCICustomer(liteContact.getContactID());

	int customerID = liteCICustomer.getCustomerID();

    Class[] types = { Integer.class,String.class };    
	Object[][] gData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, "SELECT GDEF.GRAPHDEFINITIONID, GDEF.NAME FROM GRAPHDEFINITION GDEF, GRAPHCUSTOMERLIST GCL WHERE GDEF.GRAPHDEFINITIONID=GCL.GRAPHDEFINITIONID AND GCL.CUSTOMERID = " + customerID+ " ORDER BY GDEF.NAME", types );	
%>

<jsp:useBean id="graphBean" class="com.cannontech.graph.GraphBean" scope="session">
<%-- this body is executed only if the bean is created --%>
<jsp:setProperty name="graphBean" property="viewType" value="<%=GraphRenderers.LINE%>"/>
<jsp:setProperty name="graphBean" property="start" value="<%=datePart.format(ServletUtil.getToday())%>"/>
<jsp:setProperty name="graphBean" property="period" value="<%=ServletUtil.historicalPeriods[0]%>"/>
<jsp:setProperty name="graphBean" property="format" value="png"/>	
<jsp:setProperty name="graphBean" property="gdefid" value="-1"/>	
<%-- intialize bean properties --%>
</jsp:useBean>
