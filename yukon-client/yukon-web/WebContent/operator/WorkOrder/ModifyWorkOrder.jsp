<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.data.lite.LiteAddress" %>
<%@ page import="com.cannontech.stars.database.data.lite.*" %>
<%@ page import="com.cannontech.stars.database.cache.StarsDatabaseCache" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.cannontech.stars.web.util.WorkOrderManagerUtil" %>
<%@ page import="com.cannontech.common.util.Pair" %>
<%@ page import="com.cannontech.stars.core.dao.StarsWorkOrderBaseDao"%>
<%@ page import="com.cannontech.stars.dr.event.dao.EventWorkOrderDao"%>
<%@ page import="com.cannontech.spring.YukonSpringHook"%>
<%@ page import="com.cannontech.stars.database.data.event.EventWorkOrder"%>
<%@ page import="com.cannontech.stars.dr.event.model.EventBase"%>
<%@ page import="com.cannontech.common.model.ContactNotificationType"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    LiteWorkOrderBase liteOrder = null;
    LiteStarsEnergyCompany liteWOEnergyCompany = null;
    ArrayList workOrderSetList = (ArrayList)session.getAttribute(WorkOrderManagerUtil.WORK_ORDER_SET);
    if (workOrderSetList.get(0) instanceof Pair)
        liteOrder = (LiteWorkOrderBase)((Pair)workOrderSetList.get(0)).getFirst();
    else
        liteOrder = (LiteWorkOrderBase)workOrderSetList.get(0);
    
    StarsWorkOrderBaseDao starsWorkOrderBaseDao = YukonSpringHook.getBean("starsWorkOrderBaseDao",StarsWorkOrderBaseDao.class);
    liteOrder = starsWorkOrderBaseDao.getById(liteOrder.getOrderID());
    
    EventWorkOrderDao eventWorkOrderDao = YukonSpringHook.getBean("eventWorkOrderDao", EventWorkOrderDao.class);
    List<EventBase> eventBases = eventWorkOrderDao.getByWorkOrderId(liteOrder.getOrderID());
    
	int statusPending = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING).getEntryID();
	int statusAssigned = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_ASSIGNED).getEntryID();
	int statusScheduled = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_SCHEDULED).getEntryID();
	int statusCompleted = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_COMPLETED).getEntryID();
	int statusCancelled = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_CANCELLED).getEntryID();
%>
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
<%if (liteOrder.getCurrentStateID() != statusCompleted && liteOrder.getCurrentStateID() != statusCancelled) {%>
	if (form.CurrentState.value == "<%=statusCancelled%>") {
		if (!confirm("Are you sure you want to cancel this service order?"))
			return false;
	}
<%}%>
	return true;
}

function deleteWorkOrder(form) {
	if (!confirm('Are you sure you want to delete this service order?'))
		return;
	form.action.value = "DeleteWorkOrder";
	form.REDIRECT.value = "<%=request.getContextPath()%>/operator/WorkOrder/WorkOrder.jsp";
	form.submit();
}

function searchAccount(form) {
	if (form.AcctNo.value == "") {
		alert("Account # cannot be empty");
		return;
	}
	form.attributes["action"].value = "<%=request.getContextPath()%>/servlet/WorkOrderManager";
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
	form.elements["Date" + divName].disabled = false;
	form.elements["Time" + divName].disabled = false;
	form.elements["Date" + divName].value = getCurrentDate();
	form.elements["Time" + divName].value = getCurrentTime();
	form.elements["Date" + divName].focus();
}

function resetOrder(form) {
	form.elements["DateEventTimestamp"].value = "";
	form.elements["TimeEventTimestamp"].value = "";
}

function scheduleOrder(form) {
	form.CurrentState.value = "<%=statusScheduled%>";
	resetOrder(form);
	showDateDiv(form, "EventTimestamp");
}

function closeOrder(form) {
	resetOrder(form);
	showDateDiv(form, "EventTimstamp");
}

function changeStatus(form) {
	if( form.CurrentState.value == "<%=liteOrder.getCurrentStateID()%>" )
	{
		form.elements["DateEventTimestamp"].value = "<%=(eventBases.size() > 0 ? (ServletUtils.formatInstant(eventBases.get(0).getEventTimestamp(), datePart)) : "")%>";
		form.elements["TimeEventTimestamp"].value = "<%=(eventBases.size() > 0 ? (ServletUtils.formatInstant(eventBases.get(0).getEventTimestamp(), timeFormat)) : "")%>";
		document.getElementById("DivEventTimestamp").disabled = true;
	}
	else {
		document.getElementById("DivEventTimestamp").disabled = false;
		resetOrder(form);
		showDateDiv(form, "EventTimestamp");
	}
}

function changeServiceCompany(form) {
	if( form.ServiceCompany.value == "<%=liteOrder.getServiceCompanyID()%>" )
	{
		var group = document.getElementById("CurrentState");
		for (var i = 0; i < group.length; i++) {
			if( group[i].value == <%=liteOrder.getCurrentStateID()%>)
			{
				group[i].selected = true;	
			}
		}
	}
	else {
		if (!confirm("A Change to Service Company also changes the Current State to Assigned.\r\nYou may override the Current State change after pressing OK."))
		{
			form.ServiceCompany.value = "<%=liteOrder.getServiceCompanyID()%>";
			return false;	
		}
		
		var group = document.getElementById("CurrentState");
		for (var i = 0; i < group.length; i++) {
			if( group[i].value == <%=statusAssigned%>)
			{
				group[i].selected = true;	
			}
		}
	}
	changeStatus(form);
}

function init() {
}

function getPrintableVersion() {
	if (warnUnsavedChanges())
		document.rptForm.submit();
}

function sendWorkOrder() {
	if (warnUnsavedChanges())
		document.woForm.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
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
          <td  valign="top" width="101"><br> 
            <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell1">
              <tr>
                <td width="5">&nbsp;</td>
                <td><a href="WorkOrder.jsp" class="Link2">[Back to List]</a></td>
              </tr>
              <tr>
                <td width="5">&nbsp;</td>
                <td><a href="" onclick="getPrintableVersion(); return false;" class="Link2">Printable Version</a></td>
              </tr>
<%--              <tr>
                <td width="5">&nbsp;</td>
                <td><a href="" onclick="sendWorkOrder(); return false;" class="Link2">Send To Service Company</a></td>
              </tr>--%>
            </table>
			<form name="rptForm" method="post" action="<%=request.getContextPath()%>/servlet/ReportGenerator">
			  <input type="hidden" name="ACTION" value="DownloadReport">
			  <input type="hidden" name="type" value="<%=com.cannontech.analysis.ReportTypes.EC_WORK_ORDER%>">
			  <input type="hidden" name="fileName" value="WorkOrder">
			  <input type="hidden" name="NoCache">
			  <input type="hidden" name="OrderID" value="<%=liteOrder.getOrderID()%>">
                <input type="hidden" name="OrderNo" value="<%=liteOrder.getOrderNumber()%>">
			  <input type="hidden" name="REDIRECT" value="<%=request.getRequestURI()%>">
			  <input type="hidden" name="REFERRER" value="<%=request.getRequestURI()%>">
			</form>
			<form name="woForm" method="post" action="<%=request.getContextPath()%>/servlet/WorkOrderManager">
			  <input type="hidden" name="action" value="SendWorkOrder">
              <input type="hidden" name="OrderID" value="<%=liteOrder.getOrderID()%>">
                <input type="hidden" name="OrderNo" value="<%=liteOrder.getOrderNumber()%>">
              <input type="hidden" name="REDIRECT" value="<%=request.getRequestURI()%>">
              <input type="hidden" name="REFERRER" value="<%=request.getRequestURI()%>">
			  <input type="hidden" name="<%=ServletUtils.CONFIRM_ON_MESSAGE_PAGE%>">
			</form>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <%
                   String header = "SERVICE ORDER";
               %>
              <%@ include file="include/SearchBar.jspf" %>
			  <%
			      if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>");
			  %>
              <%
                  if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>");
              %>			  
              
			  <form name="soForm" method="post" action="<%=request.getContextPath()%>/servlet/SOAPClient" onsubmit="return validate(this)" onreset="resetOrder(this)">
			    <input type="hidden" name="action" value="UpdateWorkOrder">
                <input type="hidden" name="OrderNo" value="<%=liteOrder.getOrderNumber()%>">
                <input type="hidden" name="OrderID" value="<%=liteOrder.getOrderID()%>">
				<input type="hidden" name="AccountID" value="<%=liteOrder.getAccountID()%>">
				<input type="hidden" name="REDIRECT" value="<%=request.getRequestURI()%>">
				<input type="hidden" name="REFERRER" value="<%=request.getRequestURI()%>">
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
                                <td width="70%" class="MainText"><%=liteOrder.getOrderNumber()%></td>
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
                                    		String selected = (entry.getEntryID() == liteOrder.getWorkTypeID())? "selected" : "";
                                    %>
                                    <option value="<%=entry.getEntryID()%>" <%=selected%>><%=entry.getContent()%></option>
                                    <%
                                        }
                                    %>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="30%" class="TableCell"> 
                                  <div align="right">Ordered By:</div>
                                </td>
                                <td width="70%"> 
                                  <input type="text" name="OrderedBy" size="14" value="<%=StringEscapeUtils.escapeHtml(liteOrder.getOrderedBy())%>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="30%" class="TableCell"> 
                                  <div align="right"><cti:getProperty propertyid="<%=WorkOrderRole.ADDTL_ORDER_NUMBER_LABEL%>" defaultvalue="Addtl Order #"/>:</div>
                                </td>
                                <td width="70%"> 
                                  <input type="text" name="AddtlOrderNumber" size="14" value="<%=liteOrder.getAdditionalOrderNumber()%>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              
                              <tr> 
                                <td width="30%" class="TableCell"> 
                                  <div align="right">Assigned to:</div>
                                </td>
                                <td width="70%"> 
                                  <select name="ServiceCompany" onchange="changeServiceCompany(this.form);setContentChanged(true)">
                                    <%
                                        for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
                                    		StarsServiceCompany company = companies.getStarsServiceCompany(i);
                                    		String selected = (company.getCompanyID() == liteOrder.getServiceCompanyID())? "selected" : "";
                                    %>
                                    <option value="<%=company.getCompanyID()%>" <%=selected%>><%=company.getCompanyName()%></option>
                                    <%
                                        }
                                    %>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="30%" class="TableCell"> 
                                  <div align="right">Notes:</div>
                                </td>
                                <td width="70%"> 
                                  <textarea name="Description" rows="3" wrap="soft" cols="35" class = "TableCell" onchange="setContentChanged(true)"><%=liteOrder.getDescription().replaceAll("<br>", System.getProperty("line.separator"))%></textarea>
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
                                  <div align="right">Current State:</div>
                                </td>
                                <td width="70%"> 
                                  <select id="CurrentStateID" name="CurrentState" onchange="changeStatus(this.form);setContentChanged(true);">
                                    <%
                                        StarsCustSelectionList serviceStatusList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS );
                                    	for (int i = 0; i < serviceStatusList.getStarsSelectionListEntryCount(); i++) {
                                    		StarsSelectionListEntry entry = serviceStatusList.getStarsSelectionListEntry(i);
                                    		String selected = (entry.getEntryID() == liteOrder.getCurrentStateID())? "selected" : "";
                                    %>
                                    <option value="<%=entry.getEntryID()%>" <%=selected%>><%=entry.getContent()%></option>
                                    <%
                                        }
                                    %>
                                  </select>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <div id="DivEventTimestamp" style="disabled:true"> 
                        <table width="100%" border="0" cellspacing="0" cellpadding="1">
                          <tr> 
                            <td width="30%" align="right" class="TableCell">Event Date:</td>
                            <td width="70%"> 
                              <input type="text" name="DateEventTimestamp" size="14" value="<%=(eventBases.size() > 0 ? (ServletUtils.formatInstant(eventBases.get(0).getEventTimestamp(), datePart)) : "")%>" disabled onchange="setContentChanged(true)">
                              - 
                              <input type="text" name="TimeEventTimestamp" size="8" value="<%=(eventBases.size() > 0 ? (ServletUtils.formatInstant(eventBases.get(0).getEventTimestamp(), timeFormat)) : "")%>" disabled onchange="setContentChanged(true)">
                            </td>
                          </tr>
                        </table>
                      </div>
                        <br>
						<table width="100%" border="0" cellspacing="0" cellpadding="1" align="center">
	                      <tr> 
	                        <td width="30%" align="right" valign="top" class="TableCell">Event History:
	                        <td width="70%">
						      <table width="95%" border="1" cellspacing="0" cellpadding="1" align="left" valign="top">
	                            <%
	                                for (int i = 0; i < eventBases.size(); i++) {
	                            %>
	                            <tr> 
	                              <td width="40%" class="TableCell">&nbsp;<%=DaoFactory.getYukonListDao().getYukonListEntry(eventBases.get(i).getActionId()).getEntryText()%>&nbsp;</td>
	                              <td width="60%" class="TableCell">&nbsp;<%=ServletUtils.formatInstant(eventBases.get(i).getEventTimestamp(), datePart)%>&nbsp;-&nbsp;<%=ServletUtils.formatInstant(eventBases.get(i).getEventTimestamp(), timeFormat)%></td>
	                            </tr>
	                            <%
	                                }
	                            %>
	                          </table>                                
	                        </td>
	                      </tr>
	                    </table>
                      <table width="100%" border="0" cellspacing="0" cellpadding="1" align="center">
                        <tr>
                          <td width="30%" align="right" class="TableCell">Action Taken:</td>
                          <td width="70%"> 
                            <textarea name="ActionTaken" rows="3" wrap="soft" cols="35" class = "TableCell" onchange="setContentChanged(true)"><%=liteOrder.getActionTaken().replaceAll("<br>", System.getProperty("line.separator"))%></textarea>
                          </td>
                        </tr>
                      </table>
                      
                    </td>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
<%
     if (liteOrder.getAccountID() > 0) {
 		LiteAccountInfo liteAcctInfo = starsCustAccountInformationDao.getById(liteOrder.getAccountID(), liteEC.getEnergyCompanyID());
 		LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
 		LiteContact liteContact = DaoFactory.getContactDao().getContact(liteAcctInfo.getCustomer().getPrimaryContactID());
 		LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
 		LiteAddress liteAddr = liteEC.getAddress(liteAcctSite.getStreetAddressID());
 		
 		String name = StarsUtils.formatName(liteContact);
 		String homePhone = StarsUtils.getNotification(DaoFactory.getContactNotificationDao().getFirstNotificationForContactByType(liteContact, ContactNotificationType.HOME_PHONE));
 		String workPhone = StarsUtils.getNotification(DaoFactory.getContactNotificationDao().getFirstNotificationForContactByType(liteContact, ContactNotificationType.WORK_PHONE));
 		String mapNo = StarsUtils.forceNotNone(liteAcctSite.getSiteNumber());
 		
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
                                <td class="TableCell">
                                <c:set var="accountId" value="<%= liteAccount.getAccountID() %>" />
                                <cti:url var="accountEditUrl" value="/stars/operator/account/view">
                                    <cti:param name="accountId" value="${accountId}"/>
                                </cti:url>
                                <a href="${accountEditUrl}">Account # <%= liteAccount.getAccountNumber() %></a><BR>
                                  <% if (name.length() > 0) { %><%= name %><br><% } %>
                                  <% if (homePhone.length() > 0) { %>Home #: <cti:formatPhoneNumber value="<%=homePhone%>" htmlEscape="true"/><br><% } %>
                                  <% if (workPhone.length() > 0) { %>Work #: <cti:formatPhoneNumber value="<%=workPhone%>" htmlEscape="true"/><br><% } %>
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
                                  <input type="text" name="AcctNo" size="14" onchange="setContentChanged(true)">
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
                      <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
                    </td>
                    <td width="43%"> 
                      <input type="button" name="Delete" value="Delete" onclick="deleteWorkOrder(this.form)">
                    </td>
                  </tr>
                </table>
              </form>
              <form name="cusForm" method="post" action="<%=request.getContextPath()%>/servlet/SOAPClient">
                <input type="hidden" name="action" value="GetCustAccount">
                <input type="hidden" name="AccountID" value="<%= liteOrder.getAccountID() %>">
                <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Update.jsp">
                <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
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
