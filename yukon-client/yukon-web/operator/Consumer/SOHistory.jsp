<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int orderNo = Integer.parseInt(request.getParameter("OrderNo"));
	StarsServiceRequest order = serviceHist.getStarsServiceRequest(orderNo);
	
	int statusPending = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING).getEntryID();
	int statusScheduled = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_SCHEDULED).getEntryID();
	int statusCompleted = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_COMPLETED).getEntryID();
	int statusCancelled = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_CANCELLED).getEntryID();
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function deleteWorkOrder(form) {
	if (!confirm('Are you sure you want to delete this service order?'))
		return;
	form.action.value = "DeleteWorkOrder";
	form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Consumer/ServiceSummary.jsp";
	form.submit();
}

function validate(form) {
	if (form.OrderNo != null && form.OrderNo.value == "") {
		alert("Work Order # cannot be empty");
		return false;
	}
<%
	if (order.getCurrentState().getEntryID() != statusCompleted && order.getCurrentState().getEntryID() != statusCancelled) {
%>
	if (form.CurrentState.value == "<%= statusCancelled %>") {
		if (!confirm("Are you sure you want to cancel this service order?"))
			return false;
	}
<%
	}
%>
	return true;
}

function getCurrentDate() {
	var date = new Date();
	var year = date.getFullYear();
	var month = "0" + (date.getMonth() + 1);
	month = month.substring(month.length - 2);
	var date = "0" + date.getDate();
	date = date.substring(date.length - 2);
	return (month + "/" + date + "/" + year);
}

function getCurrentTime() {
	var date = new Date();
	var hour = "0" + date.getHours();
	hour = hour.substring(hour.length - 2);
	var minute = "0" + date.getMinutes();
	minute = minute.substring(minute.length - 2);
	return (hour + ":" + minute);
}

var currentDivName = null;

function showDateDiv(form, divName) {
	currentDivName = divName;
	form.elements["Date" + divName].disabled = false;
	form.elements["Time" + divName].disabled = false;
	form.elements["Date" + divName].value = getCurrentDate();
	form.elements["Time" + divName].value = getCurrentTime();
	if (document.getElementById("Div" + divName) != null)
		document.getElementById("Div" + divName).style.display = "";
	form.elements["Date" + divName].focus();
}

function resetOrder(form) {
	if (currentDivName != null) {
		if (document.getElementById("Div" + currentDivName) != null)
			document.getElementById("Div" + currentDivName).style.display = "none";
		form.elements["Date" + currentDivName].value = "";
		form.elements["Time" + currentDivName].value = "";
		form.elements["Date" + currentDivName].disabled = true;
		form.elements["Time" + currentDivName].disabled = true;
		currentDivName = null;
	}
}

function scheduleOrder(form) {
	form.CurrentState.value = "<%= statusScheduled %>";
	resetOrder(form);
	showDateDiv(form, "Scheduled");
}

function closeOrder(form) {
	resetOrder(form);
	showDateDiv(form, "Completed");
}

function changeStatus(form) {
<%
	if (order.getCurrentState().getEntryID() != statusCompleted && order.getCurrentState().getEntryID() != statusCancelled) {
%>
	if (form.CurrentState.value == "<%= statusCompleted %>" || form.CurrentState.value == "<%= statusCancelled %>")
		closeOrder(form);
	else if (form.CurrentState.value == "<%= statusScheduled %>")
<%
		if (order.getCurrentState().getEntryID() != statusScheduled) {
%>
		scheduleOrder(form);
<%
		}
		else {
%>
		resetOrder(form);
<%
		}
%>
	else if (form.CurrentState.value == "<%= statusPending %>")
		resetOrder(form);
<%
	}
%>
}

function init() {
<% if (order.getCurrentState().getEntryID() == statusPending) { %>
	document.soForm.Schedule.disabled = false;
<% } %>
<% if (order.getCurrentState().getEntryID() == statusScheduled || order.getDateScheduled() != null) { %>
	document.soForm.DateScheduled.disabled = false;
	document.soForm.TimeScheduled.disabled = false;
	document.getElementById("DivScheduled").style.display = "";
<% } %>
<% if (order.getCurrentState().getEntryID() == statusCompleted || order.getCurrentState().getEntryID() == statusCancelled) { %>
	document.soForm.DateCompleted.disabled = false;
	document.soForm.TimeCompleted.disabled = false;
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
            <div align="center" class="TableCell1"><br>
              <a href="ServiceSummary.jsp" class="Link2" onclick="return warnUnsavedChanges()">Back to List</a></div>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center">
			  <% String header = "WORK ORDERS - SERVICE HISTORY"; %>
			  <%@ include file="include/InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			</div>
			<form name="soForm" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)" onreset="resetOrder(this)">
			  <input type="hidden" name="action" value="UpdateWorkOrder">
              <input type="hidden" name="OrderID" value="<%= order.getOrderID() %>">
			  <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?OrderNo=<%= orderNo %>">
			  <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?OrderNo=<%= orderNo %>">
              <table width="640" border="0" cellspacing="0" cellpadding="10" align="center">
                <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td><span class="SubtitleHeader">SERVICE REQUEST INFORMATION</span> 
                          <hr>
                          <table width="100%" border="0" cellspacing="0" cellpadding="1" align="center">
                            <tr> 
                              <td width="30%" class="TableCell"> 
                                <div align="right">Work Order #:</div>
                              </td>
                              <td width="70%" class="MainText"><%= order.getOrderNumber() %></td>
                            </tr>
                            <tr> 
                              <td width="30%" class="TableCell"> 
                                <div align="right">Date Reported:</div>
                              </td>
                              <td width="70%"> 
                                <input type="text" name="DateReported" size="14" value="<%= ServletUtils.formatDate(order.getDateReported(), datePart) %>" onchange="setContentChanged(true)">
                                - 
                                <input type="text" name="TimeReported" size="8" value="<%= ServletUtils.formatDate(order.getDateReported(), timeFormat) %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                            <tr> 
                              <td width="30%" class="TableCell"> 
                                <div align="right">Service Type:</div>
                              </td>
                              <td width="70%"> 
                                <select name="ServiceType" onchange="setContentChanged(true)">
                                  <%
	StarsCustSelectionList serviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE );
	for (int i = 0; i < serviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = serviceTypeList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == order.getServiceType().getEntryID())? "selected" : "";
%>
                                  <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
                                  <%	} %>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="30%" class="TableCell"> 
                                <div align="right">Ordered By:</div>
                              </td>
                              <td width="70%"> 
                                <input type="text" name="OrderedBy" size="14" value="<%= order.getOrderedBy() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                            <tr> 
                              <td width="30%" class="TableCell"> 
                                <div align="right">Assigned to:</div>
                              </td>
                              <td width="70%"> 
                                <select name="ServiceCompany" onchange="setContentChanged(true)">
                                  <%
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany company = companies.getStarsServiceCompany(i);
		String selected = (company.getCompanyID() == order.getServiceCompany().getEntryID())? "selected" : "";
%>
                                  <option value="<%= company.getCompanyID() %>" <%= selected %>><%= company.getCompanyName() %></option>
                                  <%	} %>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="30%" class="TableCell"> 
                                <div align="right">Description:</div>
                              </td>
                              <td width="70%"> 
                                <textarea name="Description" rows="3" wrap="soft" cols="35" class = "TableCell" onchange="setContentChanged(true)"><%= order.getDescription().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td><span class="SubtitleHeader">STATUS</span> 
                          <hr>
                          <table width="100%" border="0" cellspacing="0" cellpadding="1" align="center">
                            <tr> 
                              <td width="30%" class="TableCell"> 
                                <div align="right">Status:</div>
                              </td>
                              <td width="70%"> 
                                <select name="CurrentState" onchange="changeStatus(this.form);setContentChanged(true);">
                                  <%
	StarsCustSelectionList serviceStatusList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS );
	for (int i = 0; i < serviceStatusList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = serviceStatusList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == order.getCurrentState().getEntryID())? "selected" : "";
%>
                                  <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
                                  <%	} %>
                                </select>
                                <input type="button" name="Schedule" value="Schedule" onclick="scheduleOrder(this.form);setContentChanged(true);" disabled>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <div id="DivScheduled" style="display:none"> 
                      <table width="100%" border="0" cellspacing="0" cellpadding="1">
                        <tr> 
                          <td width="30%" align="right" class="TableCell">Date 
                            Scheduled:</td>
                          <td width="70%"> 
                            <input type="text" name="DateScheduled" size="14" value="<%= ServletUtils.formatDate(order.getDateScheduled(), datePart) %>" disabled onchange="setContentChanged(true)">
                            - 
                            <input type="text" name="TimeScheduled" size="8" value="<%= ServletUtils.formatDate(order.getDateScheduled(), timeFormat) %>" disabled onchange="setContentChanged(true)">
                          </td>
                        </tr>
                      </table>
                    </div>
                    <table width="100%" border="0" cellspacing="0" cellpadding="1">
                      <tr> 
                        <td width="30%" align="right" class="TableCell">Date Closed:</td>
                        <td width="70%"> 
                          <input type="text" name="DateCompleted" size="14" value="<%= ServletUtils.formatDate(order.getDateCompleted(), datePart) %>" disabled onchange="setContentChanged(true)">
                          - 
                          <input type="text" name="TimeCompleted" size="8" value="<%= ServletUtils.formatDate(order.getDateCompleted(), timeFormat) %>" disabled onchange="setContentChanged(true)">
                        </td>
                      </tr>
                    </table>
                    <table width="100%" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr>
                        <td width="30%" class="TableCell"> 
                          <div align="right">Action Taken:</div>
                        </td>
                        <td width="70%"> 
                          <textarea name="ActionTaken" rows="3" wrap="soft" cols="35" class = "TableCell" onchange="setContentChanged(true)"><%= order.getActionTaken().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr> 
                  <td width="42%" align="right"> 
                    <input type="submit" name="Save" value="Save">
                  </td>
                  <td width="15%" align="center"> 
                    <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
                  </td>
                  <td width="43%"> 
                    <input type="button" name="Delete" value="Delete" onclick="deleteWorkOrder(this.form)">
                  </td>
                </tr>
              </table>
            </form>
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
