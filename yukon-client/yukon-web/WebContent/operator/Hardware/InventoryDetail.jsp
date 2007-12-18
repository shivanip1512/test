<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.data.lite.LiteAddress" %>
<%@ page import="com.cannontech.database.data.lite.stars.*" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<%@ page import="com.cannontech.web.navigation.CtiNavObject" %>
<%@ page import="com.cannontech.core.dao.NotFoundException" %>
<jsp:useBean id="configBean" class="com.cannontech.stars.web.bean.ConfigBean" scope="page"/>
<jsp:useBean id="detailBean" class="com.cannontech.stars.web.bean.InventoryDetailBean" scope="page"/>

    <%pageContext.setAttribute("liteEC", liteEC);%>
    <c:set target="${detailBean}" property="energyCompany" value="${liteEC}" />
    <%pageContext.setAttribute("currentUser", lYukonUser);%>
    <c:set target="${detailBean}" property="currentUser" value="${currentUser}" />

<%int invID = Integer.parseInt(request.getParameter("InvId"));
            LiteInventoryBase liteInv = liteEC.getInventory(invID, true);
            pageContext.setAttribute("currentInv", liteInv);
            StarsInventory inventory = StarsLiteFactory.createStarsInventory(liteInv,
                                                                             liteEC);

            String devTypeStr = DaoFactory.getYukonListDao().getYukonListEntry(inventory.getDeviceType()
                                                                          .getEntryID())
                                              .getEntryText();
            if (inventory.getDeviceID() > 0)
                devTypeStr = PAOGroups.getPAOTypeString(DaoFactory.getPaoDao().getLiteYukonPAO(inventory.getDeviceID())
                                                                .getType());

            boolean isMCT = inventory.getDeviceID() > 0;

            String src = request.getParameter("src");
            String referer = "";
			String pageName = "Inventory.jsp";
			if(((ArrayList)session.getAttribute(ServletUtil.FILTER_INVEN_LIST)) == null || 
				((ArrayList)session.getAttribute(ServletUtil.FILTER_INVEN_LIST)).size() < 1) { 
				pageName = "Filter.jsp";	
			}
            if (src == null) {
                referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER2);
                if (referer == null)
                    referer = pageName;
                if (referer.indexOf("ResultSet.jsp") >= 0)
                    src = "ResultSet";
                else
                    src = "Inventory";
            } else if (!src.equalsIgnoreCase("SelectInv")) {
                if (src.equalsIgnoreCase("Search")) {
                    referer = "Inventory.jsp";
                } else if (src.equalsIgnoreCase("Inventory") || src.equalsIgnoreCase("ResultSet")) {
                    referer = ((CtiNavObject) session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
                    if (referer == null)
                        referer = "Inventory.jsp";

                    if (referer.indexOf("page=") < 0) {
                        if (referer.indexOf("?") < 0)
                            referer += "?page=1";
                        else
                            referer += "&page=1";
                    }
                }

                session.setAttribute(ServletUtils.ATT_REFERRER2, referer);
            }

            boolean viewOnly = src.equalsIgnoreCase("SelectInv");
%>
<c:set target="${detailBean}" property="currentInventory" value="${currentInv}" />
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript" src="../../JavaScript/calendar.js"></script>
<script language="JavaScript">
function deleteHardware(form) {
<% if (liteInv.getAccountID() > 0) { %>
	if (!confirm("The hardware is currently assigned to a customer account. Are you sure you want to delete it from inventory?"))
		return;
<% } %>
<% if (isMCT) { %>
	form.attributes["action"].value = "DeleteInv.jsp";
<% } else { %>
<%  if (liteInv.getAccountID() == 0) { %>
	if (!confirm("Are you sure you want to delete the hardware from inventory?"))
		return;
<%  } %>
	form.action.value = "DeleteInventory";
<% } %>
	form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Hardware/Inventory.jsp";
	form.submit();
}

function copyHardware(form) {
	if (<%= isMCT %>)
		form.attributes["action"].value = "CreateMCT.jsp"
	else
		form.attributes["action"].value = "CreateHardware.jsp";
	form.submit();
}

function saveToBatch(form) {
	form.SaveToBatch.value = true;
	form.submit();
}

function saveConfigOnly(form) {
	form.SaveConfigOnly.value = true;
	form.submit();
}

function validate(form) {
	<%if(inventory.getLMHardware() != null) {%>
    if (form.SerialNo.value == "") {
        alert("Serial # cannot be empty");
        return false;
    }
    <%}%>
    return true;
}

function revealLog() {
    document.getElementById("stateChangeHistory").style.display = "";
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
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
          <td  valign="top" width="101"> 
<%if (!viewOnly) {
                %>
            <br>
            <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell1">
<%if (session.getAttribute(ServletUtils.ATT_CONTEXT_SWITCHED) == null) {
                    %>
              <tr> 
                <td width="5">&nbsp;</td>
                <td><a href="<%= referer %>" class="Link2" onclick="return warnUnsavedChanges()">[Back 
                  to List]</a></td>
              </tr>
              
<%}
            %>
            </table>
<%}
            %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <%String header = "INVENTORY DETAIL";
            %>
<%if (!viewOnly) {

                %>
              <%@ include file="include/SearchBar.jspf" %>
<%} else {
                %>
              <table width="100%" border="0" cellspacing="0" cellpadding="5">
                <tr>
                  <td align="center" class="TitleHeader"><%=header%></td>
                </tr>
              </table>
<%}
            %>
			  <%if (errorMsg != null)
                out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>");
            %>
			  <%if (confirmMsg != null)
                out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>");
            %>
			  
              <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager" onsubmit="return <%= !viewOnly %> && validate(this)">
			    <input type="hidden" name="action" value="UpdateInventory">
                <input type="hidden" name="InvID" value="<%= inventory.getInventoryID() %>">
				<input type="hidden" name="oldStateID" value='<c:out value="${detailBean.currentInventory.currentStateID}"/>'>
                <input type="hidden" name="DeviceID" value="<%= inventory.getDeviceID() %>">
				<input type="hidden" name="DeviceType" value="<%= inventory.getDeviceType().getEntryID() %>">
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvId=<%= invID %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvId=<%= invID %>">
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                  <tr> 
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><span class="SubtitleHeader">DEVICE INFO</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Type:</div>
                                </td>
                                <td width="210" class="MainText"><%=devTypeStr%></td>
                              </tr>
<%if (inventory.getLMHardware() != null) {
                %>
                              <tr> 
                                <td width="88" class="SubtitleHeader"> 
                                  <div align="right">*Serial #:</div>
                                </td>
                                <td width="210">
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= inventory.getLMHardware().getManufacturerSerialNumber() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
<%} else {
                String deviceName = "(none)";
                if (inventory.getDeviceID() > 0)
                {
                    try
                    {
                        deviceName = DaoFactory.getPaoDao().getYukonPAOName(inventory.getDeviceID());
                    }
                    catch(NotFoundException e) {}
                }
                else if (inventory.getMCT() != null)
                    deviceName = inventory.getMCT().getDeviceName();
%>
							  <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Device Name:</div>
                                </td>
                                <td width="210" class="MainText"><%=deviceName%></td>
                              </tr>
<%}
            %>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Label:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= inventory.getDeviceLabel() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Alt Tracking #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= inventory.getAltTrackingNumber() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Voltage:</div>
                                </td>
                                <td width="210"> 
                                  <select name="Voltage" onchange="setContentChanged(true)">
                                    <%StarsCustSelectionList voltageList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE);
            for (int i = 0; i < voltageList.getStarsSelectionListEntryCount(); i++) {
                StarsSelectionListEntry entry = voltageList.getStarsSelectionListEntry(i);
                String selected = (entry.getEntryID() == inventory.getVoltage()
                                                                  .getEntryID()) ? "selected"
                        : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%=entry.getContent()%></option>
                                    <%}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
				                <td width="88" class="TableCell"> 
				                  	<div align="right">Field Install Date:</div>
				                </td>
				                <td width="210"> 
				                  <input id="fieldInstallDate" type="text" name="fieldInstallDate" maxlength="30" size="24" value='<c:out value="${detailBean.fieldInstallDate}"/>' onchange="setContentChanged(true)">
				   				  	<a href="javascript:openCalendar(document.getElementById('fieldInstallDate'))"
										onMouseOver="window.status='Field Install Calendar';return true;"
										onMouseOut="window.status='';return true;"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="absmiddle" border="0"> 
			                        </a>
				                </td>
				          	  </tr>
				          	  <tr> 
				                <td width="88" class="TableCell"> 
				                  	<div align="right">Field Receive Date:</div>
				                </td>
				                <td width="210"> 
				                  <input id="fieldReceiveDate" type="text" name="fieldReceiveDate" maxlength="30" size="24" value='<c:out value="${detailBean.fieldReceiveDate}"/>' onchange="setContentChanged(true)">
				   				  	<a href="javascript:openCalendar(document.getElementById('fieldReceiveDate'))"
										onMouseOver="window.status='Field Receive Calendar';return true;"
										onMouseOut="window.status='';return true;"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="absmiddle" border="0"> 
			                        </a>
				                </td>
				          	  </tr>
				          	  <tr> 
				                <td width="88" class="TableCell"> 
				                  	<div align="right">Field Remove Date:</div>
				                </td>
				                <td width="210"> 
				                  <input id="fieldRemoveDate" type="text" name="fieldRemoveDate" maxlength="30" size="24" value='<c:out value="${detailBean.fieldRemoveDate}"/>' onchange="setContentChanged(true)">
				   				  	<a href="javascript:openCalendar(document.getElementById('fieldRemoveDate'))"
										onMouseOver="window.status='Field Remove Calendar';return true;"
										onMouseOut="window.status='';return true;"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="absmiddle" border="0"> 
			                        </a>
				                </td>
				          	  </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Notes:</div>
                                </td>
                                <td width="210"> 
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%=inventory.getNotes()
                                    .replaceAll("<br>",
                                                System.getProperty("line.separator"))%></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td><span class="SubtitleHeader"><br>
                            DEVICE STATUS</span> 
                            <hr>
                                <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                                    <tr> 
                                        <td width="88" class="TableCell"> 
                                            <div align="right">Status:</div>
                                        </td>
                                        <td width="210">
                                            <select id='Status' name='Status' size="1" onChange="setContentChanged(true)">
                                                <option value="0" selected> <c:out value="(none)"/> </option>
                                                <c:forEach var="deviceState" items="${detailBean.availableDeviceStates.yukonListEntries}">
                                                    <c:choose>
                                                        <c:when test="${deviceState.entryID == detailBean.currentInventory.currentStateID}">
                                                            <option value='<c:out value="${deviceState.entryID}"/>' selected> <c:out value="${deviceState.entryText}"/> </option>
                                                        </c:when> 
                                                        <c:otherwise>
                                                            <option value='<c:out value="${deviceState.entryID}"/>'> <c:out value="${deviceState.entryText}"/> </option>
                                                        </c:otherwise> 
                                                    </c:choose>
                                                </c:forEach> 
                                            </select>
                                        </td>
                                    </tr>
                                    <!-- <tr> 
                                        <td width="88" class="TableCell"> 
                                          <div align="right">Date Installed:</div>
                                        </td>
                                        <td width="210"> 
                                            <c:out value="${detailBean.currentInstallDate}"/>
                                        </td>
                                    </tr>
                                    <tr> 
                                        <td width="88" class="TableCell"> 
                                            <div align="right">Date Received:</div>
                                        </td>
                                        <td width="210"> 
                                            <c:out value="${detailBean.currentReceiveDate}"/> 
                                        </td>
                                    </tr>
                                    <tr> 
                                        <td width="88" class="TableCell"> 
                                            <div align="right">Date Removed:</div>
                                        </td>
                                        <td width="210"> 
                                            <c:out value="${detailBean.currentRemoveDate}"/>
                                        </td>
                                    </tr>-->
                                    <tr> 
                                        <td width="88" class="TableCell"> 
                                            <div align="right">Status History:</div>
                                        </td>
                                        <td width="210"> 
                                            <input type="button" name="ViewLog" value="View" onclick="revealLog()">
                                        </td>
                                    </tr>
                                  
                                </table>
                              </td>
                            </tr>
                          </table>

                    </td>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><span class="SubtitleHeader">SERVICE AND STORAGE</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Service Company:</div>
                                </td>
                                <td width="210"> 
                                  <select name="ServiceCompany" onchange="setContentChanged(true)">
                                    <%for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
                StarsServiceCompany company = companies.getStarsServiceCompany(i);
                String selected = (company.getCompanyID() == inventory.getInstallationCompany()
                                                                      .getEntryID()) ? "selected"
                        : "";
%>
                                    <option value="<%= company.getCompanyID() %>" <%= selected %>><%=company.getCompanyName()%></option>
                                    <%}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Warehouse:</div>
                                </td>
                                <td width="210"> 
                                    <select id='Warehouse' name='Warehouse' size="1" onChange="setContentChanged(true)">
                                        <option value="0" selected> <c:out value="(none)"/> </option>
                                        <c:forEach var="warehouse" items="${detailBean.availableWarehouses}">
                                            <c:choose>
                                                <c:when test="${warehouse.warehouseID == detailBean.currentWarehouse.warehouseID}">
                                                    <option value='<c:out value="${warehouse.warehouseID}"/>' selected> <c:out value="${warehouse.warehouseName}"/> </option>
                                                </c:when> 
                                                <c:otherwise>
                                                    <option value='<c:out value="${warehouse.warehouseID}"/>'> <c:out value="${warehouse.warehouseName}"/> </option>
                                                </c:otherwise> 
                                            </c:choose>
                                        </c:forEach> 
                                    </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Notes:</div>
                                </td>
                                <td width="210"> 
                                  <textarea name="InstallNotes" rows="3 wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%=inventory.getInstallationNotes()
                                    .replaceAll("<br>",
                                                System.getProperty("line.separator"))%></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<%if (inventory.getLMHardware() != null) {
                %>
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td valign="top"><span class="SubtitleHeader"><br>
                            CONFIGURATION</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Route: </div>
                                </td>
                                <td width="210"> 
                                  <select name="Route" onchange="setContentChanged(true)">
<%
    String dftRoute;
    try
    {
        dftRoute = DaoFactory.getPaoDao().getYukonPAOName(liteEC.getDefaultRouteID());
        dftRoute = "Default - " + dftRoute;
    }
    catch(NotFoundException e)
    {
        dftRoute = "Default - (None)";
    }
%>
                                    <option value="0"><%=dftRoute%></option>
<%LiteYukonPAObject[] routes = liteEC.getAllRoutes();
                for (int i = 0; i < routes.length; i++) {
                    String selected = (routes[i].getYukonID() == inventory.getLMHardware()
                                                                          .getRouteID()) ? "selected"
                            : "";
%>
                                    <option value="<%= routes[i].getYukonID() %>" <%= selected %>><%=routes[i].getPaoName()%></option>
<%}
            %>
                                  </select>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<%}
            %>
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td><span class="SubtitleHeader"><br>
                            LAST KNOWN LOCATION</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="3" align="center">
                              <tr> 
                                <td class="MainText">Location: 
                                    <c:choose>
                                        <c:when test="${detailBean.currentInventory.accountID == 0}">
                                            <c:choose>
                                                <c:when test="${detailBean.currentWarehouse.warehouseID > 0}">
                                                    <c:out value="${detailBean.currentWarehouse.warehouseName}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    General Inventory
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="" onclick="if (warnUnsavedChanges()) {document.cusForm.submit();return false;}">Customer</a>
                                        </c:otherwise>      
                                    </c:choose> 
                                </td>
                              </tr>
<%if (liteInv.getAccountID() > 0) {
                LiteStarsCustAccountInformation liteAcctInfo = liteEC.getBriefCustAccountInfo(liteInv.getAccountID(),
                                                                                              true);
                LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
                LiteContact liteContact = DaoFactory.getContactDao().getContact(liteAcctInfo.getCustomer()
                                                                              .getPrimaryContactID());
                LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
                LiteAddress liteAddr = liteEC.getAddress(liteAcctSite.getStreetAddressID());

                String name = StarsUtils.formatName(liteContact);
                String homePhone = ServletUtils.formatPhoneNumberForDisplay(StarsUtils.getNotification(DaoFactory.getContactDao().getContactNotification(liteContact,
                                                                                                  YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE)));
                String workPhone = ServletUtils.formatPhoneNumberForDisplay(StarsUtils.getNotification(DaoFactory.getContactDao().getContactNotification(liteContact,
                                                                                                  YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE)));
                String mapNo = StarsUtils.forceNotNone(liteAcctSite.getSiteNumber());

                StreetAddress starsAddr = new StreetAddress();
                StarsLiteFactory.setStarsCustomerAddress(starsAddr, liteAddr);
                String address = ServletUtils.formatAddress(starsAddr);
%>
                              <tr>
                                <td class="TableCell">
                                  Account # <%=liteAccount.getAccountNumber()%><br>
                                  <%if (name.length() > 0) {
                    %><%=name%><br><%}
                %>
                                  <%if (homePhone.length() > 0) {
                    %>Home #: <%=homePhone%><br><%}
                %>
                                  <%if (workPhone.length() > 0) {
                    %>Work #: <%=workPhone%><br><%}
                %>
                                  <%if (address.length() > 0) {
                    %><%=address%><br><%}
                %>
                                  <%if (mapNo.length() > 0) {
                    %>Map # <%=mapNo%><%}
            %>
                                </td>
                              </tr>
<%}
            %>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <div id="stateChangeHistory" style="display:none">
                    <div align="center">
                        <span class="TitleHeader">INVENTORY STATE CHANGE HISTORY</span>
                    </div>
                    <br>
                    <table width="600" border="1" cellspacing="0" cellpadding="5" align="center">
                        <tr>
                            <td class='HeaderCell' width='200'>Event</td>
                            <td class='HeaderCell' width='200'>User Name</td>
                            <td class='HeaderCell' width='200'>Time of Event</td>
                        </tr>
                        <c:forEach var="event" items="${detailBean.currentEvents}">
                            <tr>
                                <td class='TableCell' width='200'><c:out value="${event.actionText}"/></td>
                                <td class='TableCell' width='200'><c:out value="${event.userName}"/></td>
                                <td class='TableCell' width='200'><c:out value="${event.eventBase.eventTimestamp}"/></td>
                            </tr>
                        </c:forEach>
                    </table>
               </div>
               
<%if (!viewOnly) {%>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="right" width="35%"> 
                      <input type="submit" name="Save" value="Save">
                    </td>
                    <td align="center" width="15%"> 
                      <input type="reset" name="Reset" value="Reset" onClick="setContentChanged(false)">
                    </td>
                    <%if(!isMCT) { %>
		                <td align="center" width="15%"> 
		                	<input type="button" name="Copy" value="Copy" onClick="if (warnUnsavedChanges()) copyHardware(this.form);">
		                </td>
		            <%} %>
                    <td width="35%"> 
                      <input type="button" name="Delete" value="Delete" onClick="deleteHardware(this.form)">
                    </td>
                  </tr>
                </table>
<%}

            %>
			  </form>
<%if (inventory.getLMHardware() != null) {
                boolean configurable = InventoryUtils.supportConfiguration(inventory.getDeviceType()
                                                                                    .getEntryID());
%>
			  <form name="cfgForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager" onsubmit="return <%= !viewOnly %>">
			    <input type="hidden" name="action" value="ConfigLMHardware">
                <input type="hidden" name="InvID" value="<%= inventory.getInventoryID() %>">
				<input type="hidden" name="SaveToBatch" value="false">
				<input type="hidden" name="SaveConfigOnly" value="false">
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvId=<%= invID %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvId=<%= invID %>">
				<input type="hidden" name="<%= ServletUtils.CONFIRM_ON_MESSAGE_PAGE %>">
				<cti:checkProperty propertyid="<%=ConsumerInfoRole.DISABLE_SWITCH_SENDING%>">
					<%configurable = false;%>
				</cti:checkProperty>
<%String trackHwAddr = liteEC.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
                if (trackHwAddr != null && Boolean.valueOf(trackHwAddr)
                                                  .booleanValue()) {
                    int hwConfigType = InventoryUtils.getHardwareConfigType(inventory.getDeviceType()
                                                                                     .getEntryID());
                    StarsLMConfiguration configuration = inventory.getLMHardware()
                                                                  .getStarsLMConfiguration();

                    %>
                <input type="hidden" name="UseHardwareAddressing" value="true">
                <table width="610" border="0" cellspacing="0" cellpadding="10">
                  <tr> 
                    <td valign="top" align="center"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td> <span class="SubtitleHeader">ADDRESSING</span> 
                            <hr>
                          </td>
                        </tr>
                        <tr>
                          <td align="center"> 
                            <%@ include file="../../include/hwconfig_addressing.jspf" %>
                          </td>
                        </tr>
                      </table>
                    </td>
<%if (configurable) {

                        %>
                    <td valign="top" align="center"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td><span class="SubtitleHeader">RELAYS</span> 
                            <hr>
                          </td>
                        </tr>
                        <tr> 
                          <td align="center"> 
                            <%@ include file="../../include/hwconfig_relays.jspf" %>
                          </td>
                        </tr>
                      </table>
                    </td>
<%}
                    %>
                  </tr>
                </table>
<%if (!viewOnly) {
                        %>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="center"> 
<%if (configurable) {%>
                      <input type="submit" name="Config" value="Config">
                      <input type="button" name="SaveBatch" value="Save To Batch" onclick="saveToBatch(this.form)">
<%}
                    %>
                      <input type="button" name="SaveConfig" value="Save Config Only" onclick="saveConfigOnly(this.form)">
                    </td>
                  </tr>
                </table>
<%}

                %>
<%} else if (liteInv.getAccountID() > 0 && !viewOnly && configurable) {

                    %>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="center"> 
                      <%if (!configBean.isWriteToFileAllowed()) {%> 
                      <input type="submit" name="Config" value="Config">
                        <%}

                %>
                      <input type="button" name="SaveBatch" value="Save To Batch" onclick="saveToBatch(this.form)">
                    </td>
                  </tr>
                </table>
<%}

            %>
              </form>
<%}
            %>
<%if (viewOnly && session.getAttribute(ServletUtils.ATT_CONTEXT_SWITCHED) == null) {%>
              <table width="200" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td align="center"> 
                    <input type="button" name="Back" value="Back" onclick="history.back()">
                  </td>
                </tr>
              </table>
<%}
            %>
              <form name="cusForm" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient">
                <input type="hidden" name="action" value="GetCustAccount">
                <input type="hidden" name="AccountID" value="<%= liteInv.getAccountID() %>">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/Update.jsp">
                <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvId=<%= invID %>">
              </form>
              <br>
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
