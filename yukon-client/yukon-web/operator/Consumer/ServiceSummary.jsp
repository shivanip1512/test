<%@ include file="StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
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
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
              
            <div align="center">
              <% String header = "WORK ORDERS - SERVICE HISTORY"; %>
              <%@ include file="InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <span class="MainText">Click on an Order # to view the complete order history.</span>
              </div>
              
			<form method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			<input type="hidden" name="action" value="UpdateOrders">
              <table width="615" border="1" cellspacing="0" cellpadding="1" align="center">
                <tr> 
                  <td width="25" class="HeaderCell">Delete</td>
                  <td width="60" class="HeaderCell" nowrap>Order # </td>
                  <td width="50" class="HeaderCell">Date</td>
                  <td width="50" class="HeaderCell">Type</td>
                  <td width="50" class="HeaderCell">Status</td>
                  <td width="45" class="HeaderCell" nowrap>By Who</td>
                  <td width="50" class="HeaderCell">Assigned</td>
                  <td width="225" class="HeaderCell">Desription</td>
                </tr>
                <%
	for (int i = 0; i < serviceHist.getStarsServiceRequestCount(); i++) {
		StarsServiceRequest order = serviceHist.getStarsServiceRequest(i);
%>
                <input type="hidden" name="OrderID" value="<%= order.getOrderID() %>">
				<input type="hidden" name="OrderNo" value="<%= order.getOrderNumber() %>">
                <input type="hidden" name="Changed" value="false">
				<input type="hidden" name="Deleted" value="false">
                <tr valign="middle"> 
                  <td width="25" class="TableCell"> 
                    <div align="center">
                      <input type="checkbox" name="DeleteOrder" value="true" onclick="setDeleted(<%= i %>, this.checked)">
                    </div>
                  </td>
                  <td width="60" class="TableCell"><a href="SOHistory.jsp" class="Link1"><%= order.getOrderNumber() %></a></td>
                  <td width="50" class="TableCell"><%= datePart.format(order.getDateReported()) %></td>
                  <td width="50" class="TableCell"> 
                    <select name="ServiceType" class="TableCell" onchange="setChanged(<%= i %>)">
<%
	StarsCustSelectionList serviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE );
	for (int j = 0; j < serviceTypeList.getStarsSelectionListEntryCount(); j++) {
		StarsSelectionListEntry entry = serviceTypeList.getStarsSelectionListEntry(j);
		String selectedStr = (order.getServiceType().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
                      <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
	}
%>
                    </select>
                  </td>
                  <td width="50" class="TableCell"> 
                    <select name="Status" class="TableCell" onchange="setChanged(<%= i %>)">
<%
	StarsCustSelectionList statusList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS );
	for (int j = 0; j < statusList.getStarsSelectionListEntryCount(); j++) {
		StarsSelectionListEntry entry = statusList.getStarsSelectionListEntry(j);
		String selectedStr = (order.getCurrentState().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
                      <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
	}
%>
                    </select>
                  </td>
                  <td width="40" class="TableCell"><%= order.getOrderedBy() %></td>
                  <td width="50" class="TableCell"><%= order.getServiceCompany().getContent() %></td>
                  <td width="225"> 
                    <textarea name="Description" rows="3" wrap="soft" cols="28" class="TableCell" onchange="setChanged(<%= i %>)"><%= order.getDescription().replaceAll("<br>", "\r\n") %></textarea>
                  </td>
                </tr>
<%
	}
%>
              </table>
			  <br>
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr> 
                  <td width="186"> 
                    <div align="right"> 
                      <input type="submit" name="Submit" value="Submit" onClick="return checkOrderNo(this.form)">
                    </div>
                  </td>
                  <td width="194"> 
                    <div align="left"> 
                      <input type="reset" name="Cancel" value="Cancel">
                    </div>
                  </td>
                </tr>
              </table>
            </form>
            <p>&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
