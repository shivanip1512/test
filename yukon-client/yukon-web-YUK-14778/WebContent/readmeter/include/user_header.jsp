<%@ page language="java" session="true" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.data.lite.LiteCICustomer" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ page import="com.cannontech.spring.YukonSpringHook"%> 
<%@ page import="com.cannontech.core.dao.YukonUserDao"%>
<%@ page import="com.cannontech.core.dao.ContactDao"%>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%
	LiteYukonUser liteYukonUser = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
	if (liteYukonUser == null)
	{ 
		response.sendRedirect("/login.jsp"); return;
	}

    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
    String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	LiteContact liteContact = YukonSpringHook.getBean(YukonUserDao.class).getLiteContact(liteYukonUser.getLiteID());
	LiteCICustomer liteCICustomer = YukonSpringHook.getBean(ContactDao.class).getCICustomer(liteContact.getContactID());

	int customerID = liteCICustomer.getCustomerID();

    Class[] types = { Integer.class,String.class };    
	Object[][] gData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, "SELECT GDEF.GRAPHDEFINITIONID, GDEF.NAME FROM GRAPHDEFINITION GDEF, GRAPHCUSTOMERLIST GCL WHERE GDEF.GRAPHDEFINITIONID=GCL.GRAPHDEFINITIONID AND GCL.CUSTOMERID = " + customerID+ " ORDER BY GDEF.NAME", types );	

	com.cannontech.graph.GraphBean graphBean = (com.cannontech.graph.GraphBean) session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	if(graphBean == null)
	{
		session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new com.cannontech.graph.GraphBean());
		graphBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	}
%>
