<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function validate(form) {
	var radioBtns = document.getElementsByName("CallID_");
	var idx = -1;
	for (i = 0; i < radioBtns.length; i++)
		if (radioBtns[i].checked) {
			idx = i;
			break;
		}
	if (idx == -1) return false;
	
	form.CallID.value = document.getElementsByName("CallID_")[idx].value;
	form.CallType.value = document.getElementsByName("CallType_")[idx].value;
	form.Description.value = document.getElementsByName("Description_")[idx].value;
	return true;
}

function deleteCall(form) {
	if (!validate(form)) return;
	if (!confirm("Are you sure you want to delete this call?")) return;
	form.action.value = "DeleteCall";
	form.submit();
}

function init() {
<% if (callHist.getStarsCallReportCount() > 0) { %>
	document.getElementsByName("CallID_")[0].checked = true;
<% } %>
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
		  <% String pageName = "Calls.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_CALL_TRACKING, "ACCOUNT - CALL TRACKING"); %>
              <%@ include file="include/InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
			    <input type="hidden" name="action" value="UpdateCall">
				<input type="hidden" name="CallID">
				<input type="hidden" name="CallType">
				<input type="hidden" name="Description">
                <table width="600" border="1" cellspacing="0" align="center" cellpadding="2">
                  <tr> 
                    <td class="HeaderCell" width="5%">&nbsp;</td>
                    <td class="HeaderCell" width="15%">Call #</td>
                    <td class="HeaderCell" width="15%">Date/Time</td>
                    <td class="HeaderCell" width="15%">Type</td>
                    <td class="HeaderCell" width="40%">Description</td>
                    <td class="HeaderCell" width="10%" nowrap>Taken By</td>
                  </tr>
<%
	for (int i = 0; i < callHist.getStarsCallReportCount(); i++) {
		StarsCallReport call = callHist.getStarsCallReport(i);
%>
                  <tr> 
                    <td class="TableCell" width="5%"> 
                      <div align="center">
                        <input type="radio" name="CallID_" value="<%= call.getCallID() %>">
                      </div>
                    </td>
                    <td class="TableCell" width="15%"><%= call.getCallNumber() %></td>
                    <td class="TableCell" width="15%"><%= ServletUtils.formatDate(call.getCallDate(), dateTimeFormat) %></td>
                    <td class="TableCell" width="15%"> 
                      <select name="CallType_" class="TableCell" onchange="setContentChanged(true)">
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
                    <td class="TableCell" width="40%"> 
                      <textarea name="Description_" rows="3" wrap="soft" cols="35" class="TableCell" onchange="setContentChanged(true)"><%= call.getDescription().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                    </td>
                    <td class="TableCell" width="10%"><%= call.getTakenBy() %></td>
                  </tr>
<%
	}
%>
                </table>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="43%"> 
                      <div align="right"> 
                        <input type="submit" name="Submit" value="Save">
                      </div>
                    </td>
					<td width="15%">
					  <div align="center"> 
                        <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
                      </div>
					</td>
                    <td width="42%"> 
                      <div align="left">
                        <input type="button" name="Delete" value="Delete" onclick="deleteCall(this.form)">
                      </div>
                    </td>
                  </tr>
                </table>
              </form>
            </div>
            <p align="center">&nbsp;</p>
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
