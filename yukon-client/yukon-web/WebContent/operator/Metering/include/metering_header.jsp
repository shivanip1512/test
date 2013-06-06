<%@ page language="java" %>
<%@ page import="java.util.*" %>


<%@ page import="com.cannontech.spring.YukonSpringHook"%> 
<%@ page import="com.cannontech.stars.energyCompany.dao.EnergyCompanyDao" %>
<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<%@ page import="com.cannontech.roles.application.TrendingRole "%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.database.db.company.EnergyCompany" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

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
	Integer energyCompanyID = null;
	if(liteYukonUser != null && YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(liteYukonUser) != null)
		energyCompanyID = new Integer( YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(liteYukonUser).getEnergyCompanyID());
		
    Class[] types = { Integer.class,String.class };    
    java.lang.String sqlString =  "SELECT DISTINCT GDEF.GRAPHDEFINITIONID, GDEF.NAME " +
                                  " FROM GRAPHDEFINITION GDEF ";
	if( energyCompanyID != null && energyCompanyID.intValue() != EnergyCompany.DEFAULT_ENERGY_COMPANY_ID)
	{
		sqlString += ", GRAPHCUSTOMERLIST GCL, ENERGYCOMPANYCUSTOMERLIST ECCL "+
                     " WHERE ECCL.ENERGYCOMPANYID = " + energyCompanyID.intValue() + 
                     " AND GCL.CUSTOMERID = ECCL.CUSTOMERID " + 
                     " AND GDEF.GRAPHDEFINITIONID = GCL.GRAPHDEFINITIONID ";
	}
	sqlString += " ORDER BY GDEF.NAME";
	
	Object[][] ecGraphs = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sqlString, types);
	
	com.cannontech.graph.GraphBean graphBean = (com.cannontech.graph.GraphBean) session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	if(graphBean == null)
	{
		session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new com.cannontech.graph.GraphBean());
		graphBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	}
%>