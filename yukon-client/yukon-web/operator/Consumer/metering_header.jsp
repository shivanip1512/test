//NOT USED ANYMORE??? SN 8/26/2003
<%@ page language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<%@ page import="com.cannontech.roles.operator.CommercialMeteringRole"%>
<%@ page import="com.cannontech.database.cache.functions.EnergyCompanyFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.graph.model.TrendModelType" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/>

<%
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
	int energyCompanyID = EnergyCompanyFuncs.getEnergyCompany(liteYukonUser).getEnergyCompanyID();
	if (liteYukonUser == null)
	{
		response.sendRedirect(request.getContextPath() + "/login.jsp"); return;
	}

    Class[] types = { Integer.class,String.class };    
    java.lang.String sqlString =  "SELECT DISTINCT GDEF.GRAPHDEFINITIONID, GDEF.NAME " +
                                  " FROM GRAPHDEFINITION GDEF, GRAPHCUSTOMERLIST GCL, ENERGYCOMPANYCUSTOMERLIST ECCL "+
                                  " WHERE ECCL.ENERGYCOMPANYID = " + energyCompanyID + 
                                  " AND GDEF.GRAPHDEFINITIONID = GCL.GRAPHDEFINITIONID " +
                                  " AND GCL.CUSTOMERID = ECCL.CUSTOMERID";
//	MOVED DEFINITION TO STARS HEADER FOR COMPILING //
	Object[][] gData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sqlString);
%>
	<jsp:useBean id="graphBean" class="com.cannontech.graph.GraphBean" scope="session">
		<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="graphBean" property="viewType" value="<%=TrendModelType.LINE_VIEW%>"/>
	<jsp:setProperty name="graphBean" property="start" value="<%=datePart.format(ServletUtil.getToday())%>"/>
	<jsp:setProperty name="graphBean" property="period" value="<%=ServletUtil.historicalPeriods[0]%>"/>
	<jsp:setProperty name="graphBean" property="gdefid" value="-1"/>	
	    <%-- intialize bean properties --%>
	</jsp:useBean>