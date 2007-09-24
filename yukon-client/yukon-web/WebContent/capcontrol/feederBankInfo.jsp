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
	
<form>
<cti:titledContainer title="Feeder CapBank Information">
	<div>
   		<table id="Table" width="95%" border="0" cellspacing="1" cellpadding="1" class="main">
			<tr class="columnHeader lAlign ">
				<td border="1">CapBank Name</td>
				<td border="1">Display Order</td>
				<td border="1">Close Order</td>
				<td border="1">Trip Order</td>
			</tr>
<%
String css = "tableCell";
for( CapBankDevice cap : capArray )
{
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
%>
	<tr class=<%=css %>>
		<td border="1"><%=cap.getCcName() %></td>
		<td border="1"><%=cap.getControlOrder() %></td>
		<td border="1"><%=cap.getCloseOrder() %></td>
		<td border="1"><%=cap.getTripOrder() %></td>
	</tr>	
<%
}
%>		
		</table>   
	</div>
	</cti:titledContainer >
</form>


</cti:standardPage>