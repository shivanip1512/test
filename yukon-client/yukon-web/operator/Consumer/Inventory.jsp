<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory inventory = inventories.getStarsInventory(invNo);
	
	boolean invChanged = request.getParameter("Changed") != null;
	StarsInventory newInv = null;
	
	if (invChanged) {
		newInv = (StarsInventory) session.getAttribute(InventoryManager.STARS_INVENTORY_TEMP + String.valueOf(invNo));
		errorMsg = "The hardware has been changed, click \"Submit\" button to save it, or click \"Reset\" button to discard the changes.";
	}
	else {
		newInv = inventory;
		session.setAttribute(InventoryManager.STARS_INVENTORY_TEMP + String.valueOf(invNo), inventory);
	}
	
	boolean invChecking = AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.INVENTORY_CHECKING);
	
	String devTypeStr = "(none)";
	String serialNo = "(none)";
	String serialLabel = "(none)";
	
	if (newInv.getLMHardware() != null) {
		devTypeStr = newInv.getDeviceType().getContent();
		serialNo = newInv.getLMHardware().getManufacturerSerialNumber();
		serialLabel = "Serial #";
	}
	else {
		if (newInv.getDeviceID() > 0) {
			LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO(newInv.getDeviceID());
			devTypeStr = PAOGroups.getPAOTypeString(litePao.getType());
			serialNo = litePao.getPaoName();
		}
		else if (newInv.getMCT() != null) {
			devTypeStr = newInv.getDeviceType().getContent();
			serialNo = newInv.getMCT().getDeviceName();
		}
		serialLabel = "Device Name";
	}
	
	StarsServiceCompany company = null;
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany comp = companies.getStarsServiceCompany(i);
		if (comp.getCompanyID() == newInv.getInstallationCompany().getEntryID()) {
			company = comp;
			break;
		}
	}
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function deleteHardware(form) {
	if (!confirm('Deleting the hardware will also disable all the programs associated with it. Are you sure you want to continue?'))
		return;
	form.attributes["action"].value = "<%= request.getContextPath() %>/servlet/InventoryManager";
	form.action.value = "DeleteLMHardware";
	form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Consumer/Update.jsp";
	form.submit();
}

function validate(form) {
	if (form.SerialNo.value == "") {
		alert(document.getElementById("NameLabel").innerText + " cannot be empty");
		return false;
	}
	
	if (form.DeviceType.value != <%= inventory.getDeviceType().getEntryID() %>
	<% if (inventory.getLMHardware() != null) { %>
		|| form.SerialNo.value != "<%= inventory.getLMHardware().getManufacturerSerialNumber() %>"
	<% } %>)
	{
		form.attributes["action"].value = "<%= request.getContextPath() %>/servlet/InventoryManager";
	}
	return true;
}

function changeSerialNo() {
	document.snForm.submit();
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
            <% String pageName = "Inventory.jsp?InvNo=" + invNo; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "HARDWARE - INFORMATION"; %><%@ include file="include/InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
			  <form name="MForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
                <input type="hidden" name="action" value="UpdateLMHardware">
                <input type="hidden" name="OrigInvID" value="<%= inventory.getInventoryID() %>">
                <input type="hidden" name="InvID" value="<%= newInv.getInventoryID() %>">
                <input type="hidden" name="DeviceID" value="<%= newInv.getDeviceID() %>">
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
                <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                    <table width="300" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                          <td valign="top"><span class="SubtitleHeader">DEVICE</span> 
                            <hr>
<%	if (invChecking) { %>
                            <input type="hidden" name="DeviceType" value="<%= newInv.getDeviceType().getEntryID() %>">
							<input type="hidden" name="SerialNo" value="<%= serialNo %>">
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="100" class="TableCell" align="right">Type: 
                                </td>
                                <td width="120" class="MainText"><%= devTypeStr %></td>
                                <td width="80" rowspan="2"> 
                                  <input type="button" name="Change" value="Change" onclick="changeSerialNo()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell" align="right"><%= serialLabel %>: </td>
                                <td width="120" class="MainText"><%= serialNo %></td>
                              </tr>
                            </table>
<%	} %>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
<%	if (!invChecking) { %>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Type: </div>
                                </td>
                                <td width="200" class="MainText">
                                  <select name="DeviceType">
<%
		StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
		for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
			String selected = (entry.getEntryID() == newInv.getDeviceType().getEntryID())? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
<%
		}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell" align="right">Serial 
                                  #: </td>
                                <td width="200"> 
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= serialNo %>">
                                </td>
                              </tr>
<%	} %>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Label: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= newInv.getDeviceLabel() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Alt Tracking #: </div>
                                </td>
                                <td width="200">
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= newInv.getAltTrackingNumber() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Receive Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(newInv.getReceiveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Remove Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(newInv.getRemoveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Voltage: </div>
                                </td>
                                <td width="200"> 
                                  <select name="Voltage">
                                    <%
	StarsCustSelectionList voltageList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
	for (int i = 0; i < voltageList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = voltageList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == newInv.getVoltage().getEntryID())? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Status: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="Status" maxlength="30" size="24" value="<%= newInv.getDeviceStatus().getContent() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Notes: </div>
                                </td>
                                <td width="200"> 
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= newInv.getNotes().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                      </tr>
                    </table>
                  </td>
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                    <div align="center"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                            <td valign="top"><span class="SubtitleHeader">INSTALL</span> 
                              <hr>
                              <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                                <tr> 
                                  <td width="100" class="TableCell"> 
                                    <div align="right">Date Installed: </div>
                                  </td>
                                  <td width="200"> 
                                    <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(newInv.getInstallDate(), datePart) %>">
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="100" class="TableCell"> 
                                    <div align="right">Service Company: </div>
                                  </td>
                                  <td width="200"> 
                                    <select name="ServiceCompany">
<%
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany servCompany = companies.getStarsServiceCompany(i);
		String selectedStr = (servCompany.equals(company)) ? "selected" : "";
%>
                              		  <option value="<%= servCompany.getCompanyID() %>" <%= selectedStr %>><%= servCompany.getCompanyName() %></option>
<%
	}
%>
                                    </select>
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="100" class="TableCell"> 
                                    <div align="right">Notes: </div>
                                  </td>
                                  <td width="200"> 
                                    <textarea name="InstallNotes" rows="3 wrap="soft" cols="28" class = "TableCell"><%= newInv.getInstallationNotes().replaceAll(System.getProperty("line.separator"), "<br>") %></textarea>
                                  </td>
                                </tr>
                              </table>
                            </td>
                        </tr>
                      </table>
                      <table width="100%" border="0" height="68" >
                        <tr > 
                          <td class = "TableCell" align = "center">
                            <table width="250" border="1" height="86" cellpadding="10" cellspacing = "0">
                              <tr> 
                                  <td valign = "top" align = "center" class = "TableCell"><b>Service 
                                    Company</b><br>
                                     
<% if (company == null || company.getCompanyID() == 0) { %>
								  None
<% } else { %>
                                  <%= company.getCompanyName() %><br>
                                  <%= ServletUtils.formatAddress( company.getCompanyAddress() ) %><br>
                                  <%= company.getMainPhoneNumber() %> </td>
<% } %>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                     </div>
                  </td>
                </tr>
              </table>
            <table width="400" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
              <tr> 
                  <td width="42%" align="right"> 
                    <input type="submit" name="Submit" value="Submit">
                  </td>
                  <td width="15%" align="center"> 
                  <% if (invChanged) { %>
                    <input type="button" name="Reset" value="Reset" onclick="location.href = 'Inventory.jsp?InvNo=<%= invNo %>'">
                  <% } else { %>
                    <input type="reset" name="Reset" value="Reset">
                  <% } %>
                  </td>
                  <td width="43%" align="left"> 
                    <input type="button" name="Delete" value="Delete" onclick="deleteHardware(this.form)">
                  </td>
              </tr>
            </table>
            </form>
            <form name="snForm" method="post" action="SerialNumber.jsp?InvNo=<%= invNo %>">
              <input type="hidden" name="action" value="Change">
            </form>
            </div>
<%	if (newInv.getStarsLMHardwareHistory() != null) { %>
            <hr>
            <div align="center"> 
              <span class="TitleHeader">Hardware History</span><br>
              <table width="300" border="1" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td width="50%" class="HeaderCell">Date</td>
                  <td width="50%" class="HeaderCell">Action</td>
                </tr>
<%
		StarsLMHardwareHistory hwHist = newInv.getStarsLMHardwareHistory();
		for (int i = hwHist.getStarsLMHardwareEventCount() - 1; i >= 0 && i >= hwHist.getStarsLMHardwareEventCount() - 5; i--) {
			StarsLMHardwareEvent event = hwHist.getStarsLMHardwareEvent(i);
%>
                <tr valign="top"> 
                  <td width="50%" class="TableCell" bgcolor="#FFFFFF"><%= ServletUtils.formatDate(event.getEventDateTime(), datePart, "----") %></td>
                  <td width="50%" class="TableCell" bgcolor="#FFFFFF"><%= event.getEventAction() %></td>
                </tr>
<%
		}
%>
              </table>
<%		if (hwHist.getStarsLMHardwareEventCount() > 5) { %>
              <table width="300" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td> 
                    <div align="right"> 
                      <input type="button" name="More2" value="More" onClick="location='InventoryHist.jsp?InvNo=<%= invNo %>'">
                    </div>
                  </td>
                </tr>
              </table>
<%		} %>
            </div>
<%	} %>
            <p>&nbsp;</p>
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
