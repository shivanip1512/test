<%@ include file="include/StarsHeader.jsp" %>
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

function checkCallNo(form) {
	if (!changed) return false;
	for (i = 0; i < form.Changed.length; i++) {
		if (form.Deleted[i].value == 'false' && form.Changed[i].value == 'true' && form.CallNo[i].value == '') {
			alert("Call # cannot be empty");
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
		  <% String pageName = "Calls.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "ACCOUNT - CALL TRACKING"; %>
              <%@ include file="include/InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			    <input type="hidden" name="action" value="UpdateCalls">
                <table width="600" border="1" cellspacing="0" align="center" cellpadding="2">
                  <tr> 
                    <td class="HeaderCell" width="35">Delete</td>
                    <td class="HeaderCell" width="60">Call #</td>
                    <td class="HeaderCell" width="50">Date</td>
                    <td class="HeaderCell" width="50">Type</td>
                    <td class="HeaderCell" width="288">Description</td>
                    <td class="HeaderCell" width="60" nowrap>Taken By</td>
                  </tr>
<%
	for (int i = 0; i < callHist.getStarsCallReportCount(); i++) {
		StarsCallReport call = callHist.getStarsCallReport(i);
%>
                  <input type="hidden" name="CallID" value="<%= call.getCallID() %>">
				  <input type="hidden" name="CallNo" value="<%= call.getCallNumber() %>">
                  <input type="hidden" name="Changed" value="false">
                  <input type="hidden" name="Deleted" value="false">
                  <tr> 
                    <td class="TableCell" width="35"> 
                      <div align="center">
                        <input type="checkbox" name="DeleteCall" value="true" onclick="setDeleted(<%= i %>, this.checked)">
                      </div>
                    </td>
                    <td class="TableCell" width="60"><%= call.getCallNumber() %></td>
                    <td class="TableCell" width="50"><%= datePart.format( call.getCallDate() ) %></td>
                    <td class="TableCell" width="50"> 
                      <select name="CallType" class="TableCell" onchange="setChanged(<%= i %>)">
<%
	StarsCustSelectionList callTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE );
	for (int j = 0; j < callTypeList.getStarsSelectionListEntryCount(); j++) {
		StarsSelectionListEntry entry = callTypeList.getStarsSelectionListEntry(j);
		String selectedStr = (call.getCallType().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
                        <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
	}
%>
                      </select>
                    </td>
                    <td class="TableCell" width="288"> 
                      <textarea name="Description" rows="3" wrap="soft" cols="50" class="TableCell" onchange="setChanged(<%= i %>)"><%= call.getDescription().replaceAll("<br>", "\r\n") %></textarea>
                    </td>
                    <td class="TableCell" width="60"><%= call.getTakenBy() %></td>
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
                        <input type="submit" name="Submit" value="Submit" onclick="return checkCallNo(this.form)">
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
            </div>
            <p align="center">&nbsp;</p>
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
