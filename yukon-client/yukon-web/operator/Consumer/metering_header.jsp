<%@ page language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.graph.model.TrendModelType" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ taglib uri="/WEB-INF/jruntags.jar" prefix="jrun" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/>

<%
    java.text.SimpleDateFormat calFormat = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	LiteYukonUser liteYukonUser = null;
	int liteYukonUserID = -1;
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
		response.sendRedirect(request.getContextPath() + "/login.jsp"); return;
	}


    Class[] types = { Integer.class,String.class };    
	Object[][] gData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, "SELECT GDEF.GRAPHDEFINITIONID, GDEF.NAME FROM GRAPHDEFINITION GDEF, OPERATORLOGINGRAPHLIST OLGL WHERE GDEF.GRAPHDEFINITIONID = OLGL.GRAPHDEFINITIONID  AND OLGL.OPERATORLOGINID = " + liteYukonUserID + " ORDER BY GDEF.NAME", types );    
%>

	<jsp:useBean id="graphBean" class="com.cannontech.graph.GraphBean" scope="session">
		<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="graphBean" property="viewType" value="<%=TrendModelType.LINE_VIEW%>"/>
	<jsp:setProperty name="graphBean" property="start" value="<%=datePart.format(ServletUtil.getToday())%>"/>
	<jsp:setProperty name="graphBean" property="period" value="<%=ServletUtil.historicalPeriods[0]%>"/>
	<jsp:setProperty name="graphBean" property="gdefid" value="-1"/>	
	    <%-- intialize bean properties --%>
	</jsp:useBean>
