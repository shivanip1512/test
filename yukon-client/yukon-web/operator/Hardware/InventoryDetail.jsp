<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.*" %>
<%
	int invID = Integer.parseInt(request.getParameter("InvId"));
	LiteStarsEnergyCompany liteEC = SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
	LiteStarsLMHardware liteHw = liteEC.getBriefLMHardware(invID, true);
	StarsLMHardware hardware = StarsLiteFactory.createStarsLMHardware(liteHw, liteEC);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function deleteHardware(form) {
<% if (liteHw.getAccountID() == 0) { %>
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
            <div align="center" class="TableCell1"><br>
              <a href="Inventory.jsp" class="Link2">Back to List</a></div>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "INVENTORY DETAIL"; %>
              <%@ include file="include/SearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="invForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager" onsubmit="return validate(this)">
			    <input type="hidden" name="action" value="UpdateInventory">
                <input type="hidden" name="InvID" value="<%= hardware.getInventoryID() %>">
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
								<input type="hidden" name="DeviceType" value="<%= hardware.getLMDeviceType().getEntryID() %>">
                                <td width="210" class="MainText"><%= hardware.getLMDeviceType().getContent() %></td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right"> Serial #:</div>
                                </td>
                                <td width="210">
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= hardware.getManufactureSerialNumber() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right"> Label:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= hardware.getDeviceLabel() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Alt Tracking #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= hardware.getAltTrackingNumber() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Received:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(hardware.getReceiveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Removed:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(hardware.getRemoveDate(), datePart) %>">
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
		String selected = (entry.getEntryID() == hardware.getVoltage().getEntryID())? "selected" : "";
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
                                  <input type="text" name="Status" maxlength="30" size="24" value="<%= hardware.getDeviceStatus().getContent() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Notes:</div>
                                </td>
                                <td width="210"> 
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= hardware.getNotes().replaceAll("<br>", "\r\n") %></textarea>
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
                                  <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(hardware.getInstallDate(), datePart) %>">
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
		String selectedStr = (company.getCompanyID() == hardware.getInstallationCompany().getEntryID()) ? "selected" : "";
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
                                  <textarea name="InstallNotes" rows="3 wrap="soft" cols="28" class = "TableCell"><%= hardware.getInstallationNotes().replaceAll("<br>", "\r\n") %></textarea>
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
<% if (liteHw.getAccountID() == 0) { %>
                                  Warehouse
<% } else { %>
                                  <a href="" onclick="document.cusForm.submit(); return false;">Customer</a>
<% } %>
                                </td>
                              </tr>
<%
	if (liteHw.getAccountID() > 0) {
		LiteStarsCustAccountInformation liteAcctInfo = liteEC.getBriefCustAccountInfo(liteHw.getAccountID(), true);
		LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
		LiteCustomerContact liteContact = liteEC.getCustomerContact(liteAcctInfo.getCustomer().getPrimaryContactID());
		LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
		LiteAddress liteAddr = liteEC.getAddress(liteAcctSite.getStreetAddressID());
		String mapNo = ServerUtils.forceNotNone(liteAcctSite.getSiteNumber());
%>
                              <tr>
                                <td class="TableCell">
                                  Account # <%= liteAccount.getAccountNumber() %><br>
                                  <%= ServerUtils.getFormattedName(liteContact) %><br>
                                  <% if (liteContact.getHomePhone() != null) { %>Home #: <%= liteContact.getHomePhone() %><br><% } %>
                                  <% if (liteContact.getWorkPhone() != null) { %>Work #: <%= liteContact.getWorkPhone() %><br><% } %>
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
                        <input type="button" name="Delete" value="Delete" onClick="deleteHardware(this.form)">
                      </div>
                    </td>
                  </tr>
                </table>
              </form>
              <form name="cusForm" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient">
                <input type="hidden" name="action" value="GetCustAccount">
                <input type="hidden" name="AccountID" value="<%= liteHw.getAccountID() %>">
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
