<%@page import="java.util.*" %>
<%@page import="com.cannontech.spring.YukonSpringHook" %>
<%@page import="com.cannontech.roles.capcontrol.CBCSettingsRole" %>
<%@page import="com.cannontech.roles.application.WebClientRole" %>
<%@page import="com.cannontech.roles.application.CommanderRole" %>
<%@page import="com.cannontech.yukon.cbc.*" %>
<%@page import="com.cannontech.util.*" %>
<%@page import="com.cannontech.cbc.web.*" %>
<%@page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@page import="com.cannontech.servlet.CBCServlet" %>
<%@page import="com.cannontech.database.data.lite.*" %>
<%@page import="com.cannontech.web.lite.*" %>
<%@page import="com.cannontech.core.dao.*" %>

<%@page import="com.cannontech.common.util.CtiUtilities" %>
<%@page import="com.cannontech.database.data.point.SystemLogData" %>
<jsp:directive.page import="com.cannontech.cbc.cache.CapControlCache"/><%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%
    CapControlCache capControlCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
	String feederid = request.getParameter("FeederID");
	int id = Integer.valueOf(feederid);
	Feeder feederobj = capControlCache.getFeeder(id);
	String feederName = feederobj.getCcName();
	CapBankDevice[] capArray = capControlCache.getCapBanksByFeeder(id);
%>
	
<cti:titledContainer title="Feeder CapBank Information">
	<div>
   		<table id="Table" width="95%" border="0" cellspacing="1" cellpadding="1" class="main">
			<tr class="columnHeader lAlign ">
				<td>CapBank Name</td>
				<td>Display Order</td>
				<td>Close Order</td>
				<td>Trip Order</td>
			</tr>
<%
String css = "tableCell";
for( CapBankDevice cap : capArray )
{
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
%>
	<tr class=<%=css %>>
		<td><%=cap.getCcName() %></td>
		<td ><%=cap.getControlOrder() %></td>
		<td ><%=cap.getCloseOrder() %></td>
		<td ><%=cap.getTripOrder() %></td>
	</tr>	
<%
}
%>		
		</table>   
	</div>
	</cti:titledContainer >


