<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Feeder's CapBank Info" module="capcontrol_internal">
<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>
	
<%
	String feederid = request.getParameter("FeederID");
	int id = Integer.valueOf(feederid);
	Feeder feederobj = capControlCache.getFeeder(id);
	String feederName = feederobj.getCcName();
	CapBankDevice[] capArray = capControlCache.getCapBanksByFeeder(id);
%>
	
<form id="feederInfo" action="">
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
</form>


</cti:standardPage>