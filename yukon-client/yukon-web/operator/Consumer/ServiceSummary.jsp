<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
</script>

<script language="JavaScript">
var changed = false;

function setChanged(form, idx) {
	form.changed[idx].value = 'true';
	changed = true;
}

function checkOrderNo(form) {
	if (!changed) return false;
	for (i = 0; i < form.changed.length; i++) {
		if (form.changed[i].value == 'true' && form.OrderNo[i].value == '') {
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
                <td id="Header" colspan="4" height="74" background="../Header.gif">&nbsp;</td>
<script language="JavaScript">
	document.getElementById("Header").background = '../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>';
</script>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
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
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
              
            <div align="center">
              <% String header = "WORK ORDERS - SERVICE HISTORY"; %>
              <%@ include file="InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <span class="Main">Click on an Order # to view the complete order 
              history.</span>
              </div>
              
			<form method="post" action="/servlet/SOAPClient">
			<input type="hidden" name="action" value="UpdateOrders">
            <table width="615" border="1" cellspacing="0" cellpadding="3" align="center">
              <tr> 
                <td width="53" class="HeaderCell">Order # </td>
                <td width="65" class="HeaderCell">Date/Time</td>
                <td width="49" class="HeaderCell">Type</td>
                <td width="52" class="HeaderCell">Status</td>
                <td width="42" class="HeaderCell">By Who</td>
                <td width="74" class="HeaderCell">Assigned</td>
                <td width="222" class="HeaderCell">Desription</td>
              </tr>
<%
	for (int i = 0; i < serviceHist.getStarsServiceRequestCount(); i++) {
		StarsServiceRequest order = serviceHist.getStarsServiceRequest(i);
%>
			  <input type="hidden" name="OrderID" value="<%= order.getOrderID() %>">
			  <input type="hidden" name="changed" value="false">
              <tr valign="middle"> 
                <td width="53" class="TableCell"><a href="SOHistory.jsp" class="Link1"><%= order.getOrderNumber() %></a></td>
                <td width="65" class="TableCell"><% if (order.getDateReported() != null) out.print( histDateFormat.format(order.getDateReported()) ); %></td>
                <td width="49" class="TableCell">
				  <select name="ServiceType" onchange="setChanged(this.form, <%= i %>)">
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
                <td width="52" class="TableCell">
				  <select name="Status" onchange="setChanged(this.form, <%= i %>)">
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
                <td width="42" class="TableCell"><%= order.getOrderedBy() %></td>
				<td width="74" class="TableCell"><%= order.getServiceCompany().getContent() %></td>
				<td width="222"> 
				  <textarea name="Description" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setChanged(this.form, <%= i %>)"><%= order.getDescription().replaceAll("<br>", "\r\n") %></textarea>
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
                      <input type="submit" name="Save2" value="Save" onClick="return checkOrderNo(this.form)">
                    </div>
                  </td>
                  <td width="194"> 
                    <div align="left"> 
                      <input type="reset" name="Cancel2" value="Cancel">
                    </div>
                  </td>
                </tr>
              </table>
            </form>
              <p>&nbsp;</p>
                </td>
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
