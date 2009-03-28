<%@ page language="java" %>
<%@ page import="java.util.*" %>

<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.roles.application.*"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.data.lite.LiteCICustomer" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.db.customer.DeviceCustomerList"%>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@page import="com.cannontech.servlet.YukonUserContextUtils"%>
<%@page import="com.cannontech.user.YukonUserContext"%>

<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.analysis.*"%> 	
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%
	LiteYukonUser liteYukonUser = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
	int liteYukonUserID = liteYukonUser.getLiteID();

	LiteContact liteContact = DaoFactory.getYukonUserDao().getLiteContact(liteYukonUserID);
	LiteCICustomer liteCICustomer = null;
	int customerID = -1;	//default?

	//Attempt to populate this information.  If setup correctly we should never get null values.
	if( liteContact != null)
		liteCICustomer = DaoFactory.getContactDao().getCICustomer(liteContact.getContactID());

	if (liteCICustomer != null)
		customerID = liteCICustomer.getCustomerID();
	
	YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(pageContext);
    TimeZone tz = userContext.getTimeZone();

	java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
    
    datePart.setTimeZone(tz);
    timePart.setTimeZone(tz);
    dateFormat.setTimeZone(tz);
    
	String errorMsg = (String) session.getAttribute(ServletUtil.ATT_ERROR_MESSAGE);
	session.removeAttribute(ServletUtil.ATT_ERROR_MESSAGE);
    
	String confirmMsg = (String) session.getAttribute(ServletUtil.ATT_CONFIRM_MESSAGE);
	session.removeAttribute(ServletUtil.ATT_CONFIRM_MESSAGE);

    String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
	java.util.Vector custDevices = null;
	try{
		com.cannontech.database.data.customer.Customer cust = null;
		cust = (com.cannontech.database.data.customer.Customer)com.cannontech.database.data.lite.LiteFactory.createDBPersistent(
						new com.cannontech.database.data.lite.LiteCustomer(customerID));

		com.cannontech.database.Transaction t = com.cannontech.database.Transaction.createTransaction(
												com.cannontech.database.Transaction.RETRIEVE, cust);
		cust = (com.cannontech.database.data.customer.Customer)t.execute();
		custDevices = cust.getDeviceVector();
	}
	catch( java.lang.Exception e){
		com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
	}

    Class[] types = { Integer.class,String.class };    
    java.lang.String sqlString =  "SELECT GDEF.GRAPHDEFINITIONID, GDEF.NAME " +
                                  " FROM GRAPHDEFINITION GDEF, GRAPHCUSTOMERLIST GCL "+
                                  " WHERE GDEF.GRAPHDEFINITIONID=GCL.GRAPHDEFINITIONID "+
                                  " AND GCL.CUSTOMERID = " + customerID+ " ORDER BY GDEF.NAME";
    
	Object[][] gData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sqlString, types );

	com.cannontech.graph.GraphBean graphBean = (com.cannontech.graph.GraphBean) session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	if(graphBean == null)
	{
		session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new com.cannontech.graph.GraphBean());
		graphBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	}
%>