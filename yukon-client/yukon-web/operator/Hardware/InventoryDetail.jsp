<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.ContactFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.data.lite.stars.*" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<%
	LiteStarsEnergyCompany liteEC = SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
	
	int invID = Integer.parseInt(request.getParameter("InvId"));
	LiteInventoryBase liteInv = liteEC.getInventoryBrief(invID, true);
	StarsInventory inventory = StarsLiteFactory.createStarsInventory(liteInv, liteEC);
	
	String devTypeStr = "(none)";
	if (inventory.getLMHardware() != null)
		devTypeStr = inventory.getLMHardware().getLMHardwareType().getContent();
	else if (inventory.getDeviceID() > 0)
		devTypeStr = PAOGroups.getPAOTypeString( PAOFuncs.getLiteYukonPAO(inventory.getDeviceID()).getType() );
	
	String src = request.getParameter("src");
	String referer = "";
	
	if (src == null) {
		// Happpens only after hardware is saved
		referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER2);
		
		if (referer != null && referer.indexOf("ResultSet.jsp") >= 0) {
			src = "ResultSet";
			referer = "ResultSet.jsp";
		}
		else {
			src = "Inventory";
			referer = "Inventory.jsp";
		}
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
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function deleteHardware(form) {
<% if (liteInv.getAccountID() == 0) { %>
	if (!confirm('Are you sure you want to delete the hardware from inventory?'))
<% } else { %>
	if (!confirm('The hardware is currently assigned to a customer account. Are you sure you want to remove it from the account and delete it from inventory?'))
<% } %>
		return;
	form.action.value = "DeleteInventory";
	form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Hardware/Inventory.jsp";
	form.submit();
}

function validate(form) {
	if (form.SerialNo.value == "") {
		alert(document.getElementById("NameLabel").innerText + " cannot be empty");
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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="InventoryImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height="28" class="PageHeader">&nbsp;&nbsp;&nbsp;Hardware 
                  Inventory </td>
                <td width="235" height = "30" valign="middle">&nbsp;</td>
                <form method="post" action="../Operations.jsp">
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
                </form>
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
<% if (!src.equalsIgnoreCase("SelectInv")) { %>
            <div align="center" class="TableCell1"><br>
              <a href="<%= referer %>" class="Link2">Back to List</a></div>
<% } %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
			  
              <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager" onsubmit="return validate(this)">
			    <input type="hidden" name="action" value="UpdateInventory">
                <input type="hidden" name="InvID" value="<%= inventory.getInventoryID() %>">
				<input type="hidden" name="DeviceID" value="<%= inventory.getDeviceID() %>">
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
<% if (inventory.getLMHardware() != null) { %>
							  <input type="hidden" name="DeviceType" value="<%= inventory.getLMHardware().getLMHardwareType().getEntryID() %>">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Serial #:</div>
                                </td>
                                <td width="210">
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= inventory.getLMHardware().getManufacturerSerialNumber() %>">
                                </td>
                              </tr>
<% } else { %>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Device Name:</div>
                                </td>
                                <td width="210" class="MainText"><%= PAOFuncs.getYukonPAOName(inventory.getDeviceID()) %></td>
                              </tr>
<% } %>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Label:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= inventory.getDeviceLabel() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Alt Tracking #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= inventory.getAltTrackingNumber() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Received:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getReceiveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Removed:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getRemoveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Voltage:</div>
                                </td>
                                <td width="210"> 
                                  <select name="Voltage">
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
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= inventory.getNotes().replaceAll("<br>", "\r\n") %></textarea>
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
                          <td><span class="SubtitleHeader">INSTALL</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Installed:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getInstallDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Service Company:</div>
                                </td>
                                <td width="210"> 
                                  <select name="ServiceCompany">
                                    <%
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany company = companies.getStarsServiceCompany(i);
		String selectedStr = (company.getCompanyID() == inventory.getInstallationCompany().getEntryID()) ? "selected" : "";
%>
                                    <option value="<%= company.getCompanyID() %>" <%= selectedStr %>><%= company.getCompanyName() %></option>
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
                                  <textarea name="InstallNotes" rows="3 wrap="soft" cols="28" class = "TableCell"><%= inventory.getInstallationNotes().replaceAll("<br>", "\r\n") %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <br>
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td><span class="SubtitleHeader">LAST KNOWN LOCATION</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="3" align="center">
                              <tr> 
                                <td class="MainText">Location: 
<% if (liteInv.getAccountID() == 0) { %>
                                  Warehouse
<% } else { %>
                                  <a href="" onclick="document.cusForm.submit(); return false;">Customer</a>
<% } %>
                                </td>
                              </tr>
<%
	if (liteInv.getAccountID() > 0) {
		LiteStarsCustAccountInformation liteAcctInfo = liteEC.getBriefCustAccountInfo(liteInv.getAccountID(), true);
		LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
		LiteContact liteContact = liteEC.getContact(liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo);
		LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
		LiteAddress liteAddr = liteEC.getAddress(liteAcctSite.getStreetAddressID());
		
		String homePhone = ServerUtils.getNotification(ContactFuncs.getContactNotification(liteContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE));
		String workPhone = ServerUtils.getNotification(ContactFuncs.getContactNotification(liteContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE));
		String mapNo = ServerUtils.forceNotNone(liteAcctSite.getSiteNumber());
%>
                              <tr>
                                <td class="TableCell">
                                  Account # <%= liteAccount.getAccountNumber() %><br>
                                  <%= ServerUtils.getFormattedName(liteContact) %><br>
                                  <% if (homePhone.length() > 0) { %>Home #: <%= homePhone %><br><% } %>
                                  <% if (workPhone.length() > 0) { %>Work #: <%= workPhone %><br><% } %>
                                  <%= ServerUtils.getFormattedAddress(liteAddr) %><br>
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
                    <td width="43%"> 
                      <div align="right"> 
                        <input type="submit" name="Save" value="Save">
                      </div>
                    </td>
                    <td width="15%"> 
                      <div align="center">
                        <input type="reset" name="Cancel" value="Cancel">
                      </div>
                    </td>
                    <td width="42%"> 
                      <div align="left">
                        <input type="button" name="Delete" value="Delete" onclick="deleteHardware(this.form)">
                      </div>
                    </td>
                  </tr>
                </table>
<% } else { %>
                <table width="200" border="0" cellspacing="0" cellpadding="5" align="center">
                  <tr> 
                    <td align="center"> 
                      <input type="button" name="Back" value="Back" onClick="history.back()">
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
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
