<%@ page language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<%@ page import="com.cannontech.roles.operator.CommercialMeteringRole"%>
<%@ page import="com.cannontech.database.cache.functions.EnergyCompanyFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/>

<%
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
    String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	LiteYukonUser liteYukonUser = null;
	int liteYukonUserID = -1;

	try
	{
		liteYukonUser = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
		liteYukonUserID = liteYukonUser.getLiteID();		
	}
	catch (IllegalStateException ise)
	{
	}
	int energyCompanyID =EnergyCompanyFuncs.getEnergyCompany(liteYukonUser).getEnergyCompanyID();
		
    Class[] types = { Integer.class,String.class };    
    java.lang.String sqlString =  "SELECT DISTINCT GDEF.GRAPHDEFINITIONID, GDEF.NAME " +
                                  " FROM GRAPHDEFINITION GDEF, GRAPHCUSTOMERLIST GCL, ENERGYCOMPANYCUSTOMERLIST ECCL "+
                                  " WHERE ECCL.ENERGYCOMPANYID = " + energyCompanyID + 
                                  " AND GDEF.GRAPHDEFINITIONID = GCL.GRAPHDEFINITIONID " +
                                  " AND GCL.CUSTOMERID = ECCL.CUSTOMERID" + 
                                  " ORDER BY GDEF.NAME";
	Object[][] ecGraphs = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sqlString, types);
	
	com.cannontech.graph.GraphBean graphBean = (com.cannontech.graph.GraphBean) session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	if(graphBean == null)
	{
		session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new com.cannontech.graph.GraphBean());
		graphBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	}

%>