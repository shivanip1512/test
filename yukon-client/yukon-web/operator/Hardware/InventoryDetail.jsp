<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.ContactFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.data.lite.stars.*" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<%
	int invID = Integer.parseInt(request.getParameter("InvId"));
	LiteInventoryBase liteInv = liteEC.getInventory(invID, true);
	StarsInventory inventory = StarsLiteFactory.createStarsInventory(liteInv, liteEC);
	
	String devTypeStr = YukonListFuncs.getYukonListEntry(inventory.getDeviceType().getEntryID()).getEntryText();
	if (inventory.getDeviceID() > 0)
		devTypeStr = PAOGroups.getPAOTypeString( PAOFuncs.getLiteYukonPAO(inventory.getDeviceID()).getType() );
	
	boolean isMCT = inventory.getDeviceID() > 0;
	
	String src = request.getParameter("src");
	String referer = "";
	
	if (src == null) {
		referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER2);
		if (referer == null) referer = "Inventory.jsp";
		if (referer.indexOf("ResultSet.jsp") >= 0)
			src = "ResultSet";
		else
			src = "Inventory";
	}
	else if (!src.equalsIgnoreCase("SelectInv")) {
		if (src.equalsIgnoreCase("Search")) {
			referer = "Inventory.jsp";
		}
		else if (src.equalsIgnoreCase("Inventory") || src.equalsIgnoreCase("ResultSet")) {
			referer = request.getHeader("referer");
			if (referer.indexOf("page=") < 0) {
				if (referer.indexOf("?") < 0)
					referer += "?page=1";
				else
					referer += "&page=1";
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REFERRER2, referer);
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function deleteHardware(form) {
<% if (liteInv.getAccountID() > 0) { %>
	if (!confirm("The hardware is currently assigned to a customer account. Are you sure you want to delete it from inventory?"))
		return;
<% } %>
<% if (isMCT) { %>
	form.attributes["action"].value = "DeleteInv.jsp";
<% } else if (liteInv.getAccountID() == 0) { %>
	if (!confirm("Are you sure you want to delete the hardware from inventory?"))
		return;
	form.action.value = "DeleteInventory";
<% } %>
	form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Hardware/Inventory.jsp";
	form.submit();
}

function configHardware(form) {
	form.action.value = "ConfigHardware";
	form.submit();
}

function saveToBatch(form) {
	form.action.value = "ConfigHardware";
	form.SaveToBatch.value = "true";
	form.submit();
}

function validate(form) {
	if (form.SerialNo.value == "") {
		alert("Serial # cannot be empty");
		return false;
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
<% if (!src.equalsIgnoreCase("SelectInv")) { %>
            <br>
            <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell1">
<%
	if (session.getAttribute(com.cannontech.common.constants.LoginController.SAVED_YUKON_USERS) == null
		|| liteEC.getParent() == null) {
%>
              <tr>
                <td width="5">&nbsp;</td>
                <td><a href="<%= referer %>" class="Link2" onclick="return warnUnsavedChanges()">[Back to List]</a></td>
              </tr>
<%
	}
%>
              <tr>
                <td width="5">&nbsp;</td>
                <td><a href="<%= isMCT? "CreateMCT.jsp" : "CreateHardware.jsp" %>?RefId=<%= inventory.getInventoryID() %>" class="Link2" onclick="return warnUnsavedChanges()">Copy</a></td>
              </tr>
<%
	if (!isMCT) {
%>
              <tr>
                <td width="5">&nbsp;</td>
                <td><a href="" onclick="configHardware(document.MForm); return false;" class="Link2">Config</a></td>
              </tr>
              <tr>
                <td width="5">&nbsp;</td>
                <td><a href="" onclick="saveToBatch(document.MForm); return false;" class="Link2">Save To Batch</a></td>
              </tr>
<%
	}
%>
            </table>
<% } %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "INVENTORY DETAIL"; %>
<% if (!src.equalsIgnoreCase("SelectInv")) { %>
              <%@ include file="include/SearchBar.jsp" %>
<% } else { %>
              <table width="100%" border="0" cellspacing="0" cellpadding="5">
                <tr>
                  <td align="center" class="TitleHeader"><%= header %></td>
                </tr>
              </table>
<% } %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  
              <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager" onsubmit="return validate(this)">
			    <input type="hidden" name="action" value="UpdateInventory">
                <input type="hidden" name="InvID" value="<%= inventory.getInventoryID() %>">
				<input type="hidden" name="DeviceID" value="<%= inventory.getDeviceID() %>">
				<input type="hidden" name="DeviceType" value="<%= inventory.getDeviceType().getEntryID() %>">
				<input type="hidden" name="SaveToBatch" value="false">
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvId=<%= invID %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvId=<%= invID %>">
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                  <tr> 
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><span class="SubtitleHeader">DEVICE</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Type:</div>
                                </td>
                                <td width="210" class="MainText"><%= devTypeStr %></td>
                              </tr>
<%	if (inventory.getLMHardware() != null) { %>
                              <tr> 
                                <td width="88" class="SubtitleHeader"> 
                                  <div align="right">*Serial #:</div>
                                </td>
                                <td width="210">
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= inventory.getLMHardware().getManufacturerSerialNumber() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
<%	}
	else {
		String deviceName = "(none)";
		if (inventory.getDeviceID() > 0)
			deviceName = PAOFuncs.getYukonPAOName(inventory.getDeviceID());
		else if (inventory.getMCT() != null)
			deviceName = inventory.getMCT().getDeviceName();
%>
							  <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Device Name:</div>
                                </td>
                                <td width="210" class="MainText"><%= deviceName %></td>
                              </tr>
<%	} %>
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
                                  <div align="right">Date Received:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getReceiveDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Removed:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getRemoveDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Voltage:</div>
                                </td>
                                <td width="210"> 
                                  <select name="Voltage" onchange="setContentChanged(true)">
                                    <%
	StarsCustSelectionList voltageList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
	for (int i = 0; i < voltageList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = voltageList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == inventory.getVoltage().getEntryID())? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Status:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="Status" maxlength="30" size="24" value="<%= inventory.getDeviceStatus().getContent() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Notes:</div>
                                </td>
                                <td width="210"> 
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= inventory.getNotes().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<% if (inventory.getLMHardware() != null) { %>
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
                                    <option value="0">(Default Route)</option>
<%
	LiteYukonPAObject[] routes = liteEC.getAllRoutes();
	for (int i = 0; i < routes.length; i++) {
		String selected = (routes[i].getYukonID() == inventory.getLMHardware().getRouteID())? "selected" : "";
%>
                                    <option value="<%= routes[i].getYukonID() %>" <%= selected %>><%= routes[i].getPaoName() %></option>
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
<% } %>
                    </td>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><span class="SubtitleHeader">INSTALL</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Installed:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getInstallDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Service Company:</div>
                                </td>
                                <td width="210"> 
                                  <select name="ServiceCompany" onchange="setContentChanged(true)">
                                    <%
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany company = companies.getStarsServiceCompany(i);
		String selected = (company.getCompanyID() == inventory.getInstallationCompany().getEntryID()) ? "selected" : "";
%>
                                    <option value="<%= company.getCompanyID() %>" <%= selected %>><%= company.getCompanyName() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Notes:</div>
                                </td>
                                <td width="210"> 
                                  <textarea name="InstallNotes" rows="3 wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= inventory.getInstallationNotes().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td><span class="SubtitleHeader"><br>
                            LAST KNOWN LOCATION</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="3" align="center">
                              <tr> 
                                <td class="MainText">Location: 
<% if (liteInv.getAccountID() == 0) { %>
                                  Warehouse
<% } else { %>
                                  <a href="" onclick="if (warnUnsavedChanges()) {document.cusForm.submit();return false;}">Customer</a>
<% } %>
                                </td>
                              </tr>
<%
	if (liteInv.getAccountID() > 0) {
		LiteStarsCustAccountInformation liteAcctInfo = liteEC.getBriefCustAccountInfo(liteInv.getAccountID(), true);
		LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
		LiteContact liteContact = ContactFuncs.getContact(liteAcctInfo.getCustomer().getPrimaryContactID());
		LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
		LiteAddress liteAddr = liteEC.getAddress(liteAcctSite.getStreetAddressID());
		
		String name = ECUtils.formatName(liteContact);
		String homePhone = ECUtils.getNotification(ContactFuncs.getContactNotification(liteContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE));
		String workPhone = ECUtils.getNotification(ContactFuncs.getContactNotification(liteContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE));
		String mapNo = StarsUtils.forceNotNone(liteAcctSite.getSiteNumber());
		
		StreetAddress starsAddr = new StreetAddress();
		StarsLiteFactory.setStarsCustomerAddress(starsAddr, liteAddr);
		String address = ServletUtils.formatAddress(starsAddr);
%>
                              <tr>
                                <td class="TableCell">
                                  Account # <%= liteAccount.getAccountNumber() %><br>
                                  <% if (name.length() > 0) { %><%= name %><br><% } %>
                                  <% if (homePhone.length() > 0) { %>Home #: <%= homePhone %><br><% } %>
                                  <% if (workPhone.length() > 0) { %>Work #: <%= workPhone %><br><% } %>
                                  <% if (address.length() > 0) { %><%= address %><br><% } %>
                                  <% if (mapNo.length() > 0) { %>Map # <%= mapNo %><% } %>
                                </td>
                              </tr>
<%	} %>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
<% if (!src.equalsIgnoreCase("SelectInv")) { %>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="40%" align="right"> 
                      <input type="submit" name="Save" value="Save">
                    </td>
                    <td width="20%" align="center"> 
                      <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
                    </td>
                    <td width="40%"> 
                      <input type="button" name="Delete" value="Delete" onclick="deleteHardware(this.form)">
                    </td>
                  </tr>
                </table>
<% } %>
<%
	String trackHwAddr = liteEC.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
	boolean configHw = (inventory.getLMHardware() != null) && trackHwAddr != null && Boolean.valueOf(trackHwAddr).booleanValue();
	if (configHw) {
		int hwConfigType = ECUtils.getHardwareConfigType(inventory.getDeviceType().getEntryID());
		StarsLMConfiguration configuration = inventory.getLMHardware().getStarsLMConfiguration();
%>
                <input type="hidden" name="UseHardwareAddressing" value="true">
                <table width="610" border="0" cellspacing="0" cellpadding="10">
                  <tr> 
                    <td valign="top"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td> <span class="SubtitleHeader">ADDRESSING</span> 
                            <hr>
                          </td>
                        </tr>
                        <tr>
                          <td align="center"> 
                            <%@ include file="../../include/hwconfig_addressing.jsp" %>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td valign="top"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td><span class="SubtitleHeader">RELAYS</span> 
                            <hr>
                          </td>
                        </tr>
                        <tr> 
                          <td align="center"> 
                            <%@ include file="../../include/hwconfig_relays.jsp" %>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
<% if (!src.equalsIgnoreCase("SelectInv")) { %>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="right"> 
                      <input type="submit" name="Save2" value="Save">
                    </td>
                    <td> 
                      <input type="button" name="Config" value="Config" onclick="configHardware(this.form)">
                    </td>
                  </tr>
                </table>
<% } %>
<%
	}
%>
<%
	if (src.equalsIgnoreCase("SelectInv") &&
		(session.getAttribute(com.cannontech.common.constants.LoginController.SAVED_YUKON_USERS) == null
		|| liteEC.getParent() == null))
	{
%>
                <table width="200" border="0" cellspacing="0" cellpadding="5" align="center">
                  <tr> 
                    <td align="center"> 
                      <input type="button" name="Back" value="Back" onclick="history.back()">
                    </td>
                  </tr>
                </table>
<% } %>
              </form>
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
