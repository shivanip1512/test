<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.ContactFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.data.lite.stars.*" %>
<%@ page import="com.cannontech.stars.util.ECUtils" %>
<%
	int orderID = Integer.parseInt(request.getParameter("OrderId"));
	LiteWorkOrderBase liteOrder = liteEC.getWorkOrderBase(orderID, true);
	StarsServiceRequest order = StarsLiteFactory.createStarsServiceRequest(liteOrder, liteEC);
	
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
function validate(form) {
	if (form.OrderNo != null && form.OrderNo.value == "") {
		alert("Work Order # cannot be empty");
		return false;
	}
<%
	if (liteOrder.getCurrentStateID() != statusCompleted && liteOrder.getCurrentStateID() != statusCancelled) {
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

function deleteWorkOrder(form) {
	if (!confirm('Are you sure you want to delete this service order?'))
		return;
	form.action.value = "DeleteWorkOrder";
	form.REDIRECT.value = "<%= request.getContextPath() %>/operator/WorkOrder/SOList.jsp";
	form.submit();
}

function searchAccount(form) {
	if (form.AcctNo.value == "") {
		alert("Account # cannot be empty");
		return;
	}
	form.attributes["action"].value = "<%= request.getContextPath() %>/servlet/WorkOrderManager";
	form.action.value = "SearchCustAccount";
	form.submit();
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
	if (liteOrder.getCurrentStateID() != statusCompleted && liteOrder.getCurrentStateID() != statusCancelled) {
%>
	if (form.CurrentState.value == "<%= statusCompleted %>" || form.CurrentState.value == "<%= statusCancelled %>")
		closeOrder(form);
	else if (form.CurrentState.value == "<%= statusScheduled %>")
<%
		if (liteOrder.getCurrentStateID() != statusScheduled) {
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
<% if (liteOrder.getCurrentStateID() == statusPending) { %>
	document.soForm.Schedule.disabled = false;
<% } %>
<% if (liteOrder.getCurrentStateID() == statusScheduled || order.getDateScheduled() != null) { %>
	document.soForm.DateScheduled.disabled = false;
	document.soForm.TimeScheduled.disabled = false;
	document.getElementById("DivScheduled").style.display = "";
<% } %>
<% if (liteOrder.getCurrentStateID() == statusCompleted || liteOrder.getCurrentStateID() == statusCancelled) { %>
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
              <a href="SOList.jsp" class="Link2">Back to List</a></div>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "SERVICE ORDER"; %>
              <%@ include file="include/SearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              
			  <form name="soForm" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)" onreset="resetOrder(this)">
			    <input type="hidden" name="action" value="UpdateWorkOrder">
                <input type="hidden" name="OrderID" value="<%= order.getOrderID() %>">
				<input type="hidden" name="AccountID" value="<%= liteOrder.getAccountID() %>">
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?OrderId=<%= orderID %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?OrderId=<%= orderID %>">
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
                                  <input type="text" name="DateReported" size="14" value="<%= ServletUtils.formatDate(order.getDateReported(), datePart) %>">
                                  -
                                  <input type="text" name="TimeReported" size="8" value="<%= ServletUtils.formatDate(order.getDateReported(), timeFormat) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="30%" class="TableCell"> 
                                  <div align="right">Service Type:</div>
                                </td>
                                <td width="70%"> 
                                  <select name="ServiceType">
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
                                  <input type="text" name="OrderedBy" size="14" value="<%= order.getOrderedBy() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="30%" class="TableCell"> 
                                  <div align="right">Assigned to:</div>
                                </td>
                                <td width="70%"> 
                                  <select name="ServiceCompany">
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
                                  <textarea name="Description" rows="3" wrap="soft" cols="35" class = "TableCell"><%= order.getDescription().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                                </td>
                              </tr>
							</table>
						  </td>
						</tr>
					  </table>
					  <br>
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
                                  <select name="CurrentState" onChange="changeStatus(this.form)">
                                    <%
	StarsCustSelectionList serviceStatusList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS );
	for (int i = 0; i < serviceStatusList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = serviceStatusList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == order.getCurrentState().getEntryID())? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
                                    <%	} %>
                                  </select>
                                  <input type="button" name="Schedule" value="Schedule" onClick="scheduleOrder(this.form)" disabled>
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
                              <input type="text" name="DateScheduled" size="14" value="<%= ServletUtils.formatDate(order.getDateScheduled(), datePart) %>" disabled>
                              - 
                              <input type="text" name="TimeScheduled" size="8" value="<%= ServletUtils.formatDate(order.getDateScheduled(), timeFormat) %>" disabled>
                            </td>
                          </tr>
                        </table>
                      </div>
                      <table width="100%" border="0" cellspacing="0" cellpadding="1">
                        <tr> 
                          <td width="30%" align="right" class="TableCell">Date 
                            Closed:</td>
                          <td width="70%"> 
                            <input type="text" name="DateCompleted" size="14" value="<%= ServletUtils.formatDate(order.getDateCompleted(), datePart) %>" disabled>
                            - 
                            <input type="text" name="TimeCompleted" size="8" value="<%= ServletUtils.formatDate(order.getDateCompleted(), timeFormat) %>" disabled>
                          </td>
                        </tr>
                      </table>
                      <table width="100%" border="0" cellspacing="0" cellpadding="1" align="center">
                        <tr>
                          <td width="30%" class="TableCell"> 
                            <div align="right">Action Taken:</div>
                          </td>
                          <td width="70%"> 
                            <textarea name="ActionTaken" rows="3" wrap="soft" cols="35" class = "TableCell"><%= order.getActionTaken().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                          </td>
                        </tr>
                      </table>
                      
                    </td>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
<%
	if (liteOrder.getAccountID() > 0) {
		LiteStarsCustAccountInformation liteAcctInfo = liteEC.getBriefCustAccountInfo(liteOrder.getAccountID(), true);
		LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
		LiteContact liteContact = liteEC.getContact(liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo);
		LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
		LiteAddress liteAddr = liteEC.getAddress(liteAcctSite.getStreetAddressID());
		
		String name = ECUtils.formatName(liteContact);
		String homePhone = ECUtils.getNotification(ContactFuncs.getContactNotification(liteContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE));
		String workPhone = ECUtils.getNotification(ContactFuncs.getContactNotification(liteContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE));
		String mapNo = ServerUtils.forceNotNone(liteAcctSite.getSiteNumber());
		
		StreetAddress starsAddr = new StreetAddress();
		StarsLiteFactory.setStarsCustomerAddress(starsAddr, liteAddr);
		String address = ServletUtils.formatAddress(starsAddr);
		if (address.length() == 0) address = "Address N/A";
%>
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td><span class="SubtitleHeader">CUSTOMER CONTACT</span> 
                            <hr>
                            <table width="100%" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td class="TableCell"><a href="" onClick="document.cusForm.submit(); return false;"> 
                                  Account # <%= liteAccount.getAccountNumber() %></a><br>
                                  <% if (name.length() > 0) { %><%= name %><br><% } %>
                                  <% if (homePhone.length() > 0) { %>Home #: <%= homePhone %><br><% } %>
                                  <% if (workPhone.length() > 0) { %>Work #: <%= workPhone %><br><% } %>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <br>
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td><span class="SubtitleHeader">SERVICE ADDRESS</span> 
                            <hr>
                            <table width="100%" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td class="TableCell"><%= address %><br>
                                  <% if (mapNo.length() > 0) { %>Map # <%= mapNo %><% } %>
                                </td>
                              </tr>
                              <tr> 
                                <td class="TableCell"> 
                                  <textarea name="textarea3" rows="3" wrap="soft" cols="35" class="TableCell" readonly><%= liteAcctSite.getPropertyNotes().replaceAll("<br>", "\r\n") %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<%	}
	else {
%>
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td><span class="SubtitleHeader">ASSIGN TO</span> 
                            <hr>
                            <table width="100%" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td class="TableCell" width="30%" align="right">Account 
                                  #: </td>
                                <td class="TableCell" width="70%">
                                  <input type="text" name="AcctNo" size="14">
                                  <input type="button" name="Submit" value="Submit" onclick="searchAccount(this.form)">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<%	} %>
                    </td>
                  </tr>
                </table>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="42%" align="right"> 
                      <input type="submit" name="Save" value="Save">
                    </td>
                    <td width="15%" align="center"> 
                      <input type="reset" name="Reset" value="Reset">
                    </td>
                    <td width="43%"> 
                      <input type="button" name="Delete" value="Delete" onclick="deleteWorkOrder(this.form)">
                    </td>
                  </tr>
                </table>
              </form>
              <form name="cusForm" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient">
                <input type="hidden" name="action" value="GetCustAccount">
                <input type="hidden" name="AccountID" value="<%= liteOrder.getAccountID() %>">
                <input type="hidden" name="REDIRECT" value=" <%=request.getContextPath() %>/operator/Consumer/Update.jsp">
                <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?OrderId=<%= orderID %>">
              </form>
              <p>&nbsp;</p>
              </div>
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
