<%@ page language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<%@ page import="com.cannontech.roles.operator.CommercialMeteringRole"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.billing.mainprograms.BillingFileDefaults" %>
<%@ page import="com.cannontech.billing.FileFormatTypes"%>
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
		liteYukonUser = (LiteYukonUser) session.getAttribute("YUKON_USER");
		liteYukonUserID = liteYukonUser.getLiteID();		
	}
	catch (IllegalStateException ise)
	{
	}

	// GET A LIST OF VALID METERS? OR GROUPS? FOR EACH OPERATOR?
    Class[] types = { Integer.class,String.class };    
//	Object[][] gData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, "some sql statement", types );    
%>

<jsp:useBean id="billingBean" class="com.cannontech.billing.mainprograms.BillingBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
<jsp:setProperty name="billingBean" property="endDateStr" value="<%=datePart.format(com.cannontech.util.ServletUtil.getToday())%>"/>
<jsp:setProperty name="billingBean" property="demandDaysPrev" value="30"/>
<jsp:setProperty name="billingBean" property="energyDaysPrev" value="7"/>
<jsp:setProperty name="billingBean" property="fileFormat" value="1"/>
    <%-- intialize bean properties --%>
</jsp:useBean>