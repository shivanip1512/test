<%@ page language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<%@ page import="com.cannontech.roles.operator.CommercialMeteringRole"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.billing.mainprograms.BillingFileDefaults" %>
<%@ page import="com.cannontech.database.db.device.DeviceMeterGroup" %>
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
		liteYukonUser = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
		liteYukonUserID = liteYukonUser.getLiteID();		
	}
	catch (IllegalStateException ise)
	{
	}
	com.cannontech.billing.mainprograms.BillingBean billingBean = 
			(com.cannontech.billing.mainprograms.BillingBean) session.getAttribute(ServletUtil.ATT_BILLING_BEAN);
	if(billingBean == null)
	{
		session.setAttribute(ServletUtil.ATT_BILLING_BEAN, new com.cannontech.billing.mainprograms.BillingBean());
		billingBean = (com.cannontech.billing.mainprograms.BillingBean)session.getAttribute(ServletUtil.ATT_BILLING_BEAN);
	}
%>