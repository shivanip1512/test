<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
var changed = false;

function setChanged(idx) {
	document.getElementsByName("Changed")[idx].value = 'true';
	changed = true;
}

function setDeleted(idx, deleted) {
	document.getElementsByName("Deleted")[idx].value = deleted;
	changed = true;
}

function checkOrderNo(form) {
	if (!changed) return false;
	for (i = 0; i < form.Changed.length; i++) {
		if (form.Deleted[i].value == 'false' && form.Changed[i].value == 'true' && form.OrderNo[i].value == '') {
			alert("Order # cannot be empty");
			return false;
		}
	}
	return true;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jsp" %>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">
		  <% String pageName = "ServiceSummary.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
              
            <div align="center"> 
              <% String header = "WORK ORDERS - SERVICE HISTORY"; %>
              <%@ include file="include/InfoSearchBar.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <span class="MainText">Click on an Order # to view the complete 
              order history.</span> 
            </div>
            <br>
			<table width="95%" border="1" cellspacing="0" cellpadding="1" align="center">
              <tr> 
                <td width="13%" class="HeaderCell" nowrap>Order # </td>
                <td width="13%" class="HeaderCell">Date/Time</td>
                <td width="10%" class="HeaderCell">Type</td>
                <td width="10%" class="HeaderCell">Status</td>
                <td width="8%" class="HeaderCell" nowrap>By Who</td>
                <td width="12%" class="HeaderCell">Assigned</td>
                <td width="34%" class="HeaderCell">Desription</td>
              </tr>
              <%
	for (int i = 0; i < serviceHist.getStarsServiceRequestCount(); i++) {
		StarsServiceRequest order = serviceHist.getStarsServiceRequest(i);
%>
              <tr valign="middle"> 
                <td width="13%" class="TableCell"><a href="SOHistory.jsp?OrderNo=<%= i %>" class="Link1"><%= order.getOrderNumber() %></a></td>
                <td width="13%" class="TableCell"><%= ServletUtils.formatDate(order.getDateReported(), dateTimeFormat) %></td>
                <td width="10%" class="TableCell"><%= ServletUtils.forceNotEmpty(order.getServiceType().getContent()) %></td>
                <td width="10%" class="TableCell"><%= order.getCurrentState().getContent() %></td>
                <td width="8%" class="TableCell"><%= ServletUtils.forceNotEmpty(order.getOrderedBy()) %></td>
                <td width="12%" class="TableCell"><%= ServletUtils.forceNotEmpty(order.getServiceCompany().getContent()) %></td>
                <td width="34%"> 
                  <textarea name="Description" rows="3" wrap="soft" cols="35" class="TableCell" readonly><%= order.getDescription().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                </td>
              </tr>
              <%
	}
%>
            </table>
            <p>&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
