<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory inventory = inventories.getStarsInventory(invNo);
	
	boolean invChecking = AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.INVENTORY_CHECKING);
	
	String deviceType = "(none)";
	String serialName = "";
	String serialNameLabel = "Serial #";
	String serialNameVar = "SerialNo";
	
	if (inventory.getLMHardware() != null) {
		deviceType = YukonListFuncs.getYukonListEntry(inventory.getDeviceType().getEntryID()).getEntryText();
		serialName = inventory.getLMHardware().getManufacturerSerialNumber();
	}
	else {
		if (inventory.getDeviceID() > 0) {
			LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO(inventory.getDeviceID());
			deviceType = PAOGroups.getPAOTypeString(litePao.getType());
			serialName = litePao.getPaoName();
		}
		else if (inventory.getMCT() != null) {
			deviceType = YukonListFuncs.getYukonListEntry(inventory.getDeviceType().getEntryID()).getEntryText();
			serialName = inventory.getMCT().getDeviceName();
		}
		
		serialNameLabel = "Device Name";
		serialNameVar = "DeviceName";
	}
	
	StarsServiceCompany company = null;
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany comp = companies.getStarsServiceCompany(i);
		if (comp.getCompanyID() == inventory.getInstallationCompany().getEntryID()) {
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
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function deleteHardware(form) {
<% if (inventory.getLMHardware() != null) { %>
	if (!confirm('Deleting the hardware will also disable all the programs associated with it. Are you sure you want to continue?'))
		return;
<% } %>
	form.attributes["action"].value = "DeleteInv.jsp";
	form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Consumer/Update.jsp";
	form.submit();
}

function validate(form) {
	if (form.SerialNo != null) {
		if (form.SerialNo.value == "") {
			alert("Serial # cannot be empty");
			return false;
		}
		if (form.DeviceType.value != <%= inventory.getDeviceType().getEntryID() %>
			|| form.SerialNo.value != "<%= serialName %>")
		{
			form.attributes["action"].value = "<%= request.getContextPath() %>/servlet/InventoryManager";
		}
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
			  <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  
			  <form name="MForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
                <input type="hidden" name="action" value="UpdateLMHardware">
                <input type="hidden" name="OrigInvID" value="<%= inventory.getInventoryID() %>">
                <input type="hidden" name="InvID" value="<%= inventory.getInventoryID() %>">
                <input type="hidden" name="DeviceID" value="<%= inventory.getDeviceID() %>">
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
                <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                    <table width="300" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                          <td valign="top"><span class="SubtitleHeader">DEVICE</span> 
                            <hr>
<% if (invChecking || inventory.getLMHardware() == null) { %>
                            <input type="hidden" name="DeviceType" value="<%= inventory.getDeviceType().getEntryID() %>">
							<input type="hidden" name="<%= serialNameVar %>" value="<%= serialName %>">
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="100" class="TableCell" align="right">Type: 
                                </td>
                                <td width="120" class="MainText"><%= deviceType %></td>
                                <td width="80" rowspan="2"> 
<%  if (invChecking) { %>
                                  <input type="button" name="Change" value="Change" onclick="changeSerialNo()">
<%  } %>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell" align="right"><%= serialNameLabel %>: </td>
                                <td width="120" class="MainText"><%= serialName %></td>
                              </tr>
                            </table>
<% } %>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
<% if (!invChecking && inventory.getLMHardware() != null) { %>
                              <tr> 
                                <td width="100" class="SubtitleHeader"> 
                                  <div align="right">*Type: </div>
                                </td>
                                <td width="200" class="MainText">
                                  <select name="DeviceType" onchange="setContentChanged(true)">
<%
		StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
		for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) continue;
			String selected = (entry.getEntryID() == inventory.getDeviceType().getEntryID())? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
<%
		}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="SubtitleHeader" align="right">Serial 
                                  #: </td>
                                <td width="200"> 
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= serialName %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
<% } %>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Label: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= inventory.getDeviceLabel() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Alt Tracking #: </div>
                                </td>
                                <td width="200">
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= inventory.getAltTrackingNumber() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Receive Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getReceiveDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Remove Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getRemoveDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Voltage: </div>
                                </td>
                                <td width="200"> 
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
                                <td width="100" class="TableCell"> 
                                  <div align="right">Status: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="Status" maxlength="30" size="24" value="<%= inventory.getDeviceStatus().getContent() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Notes: </div>
                                </td>
                                <td width="200"> 
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= inventory.getNotes().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
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
                                    <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getInstallDate(), datePart) %>" onchange="setContentChanged(true)">
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="100" class="TableCell"> 
                                    <div align="right">Service Company: </div>
                                  </td>
                                  <td width="200"> 
                                    <select name="ServiceCompany" onchange="setContentChanged(true)">
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
                                    <textarea name="InstallNotes" rows="3 wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= inventory.getInstallationNotes().replaceAll(System.getProperty("line.separator"), "<br>") %></textarea>
                                  </td>
                                </tr>
                              </table>
                            </td>
                        </tr>
                      </table>
                        <table width="100%" border="0" >
                          <tr > 
                          <td class = "TableCell" align = "center">
                              <table width="250" border="1" cellpadding="5" cellspacing = "0">
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
<% if (inventory.getLMHardware() != null) { %>
                        <table width="300" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td valign="top"><span class="SubtitleHeader"><br>
                              CONFIGURATION</span> 
                              <hr>
                              <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                                <tr> 
                                  <td width="100" class="TableCell"> 
                                    <div align="right">Route: </div>
                                  </td>
                                  <td width="200"> 
                                    <select name="Route" onchange="setContentChanged(true)">
<%
	String dftRoute = PAOFuncs.getYukonPAOName(liteEC.getDefaultRouteID());
	if (dftRoute != null)
		dftRoute = "Default - " + dftRoute;
	else
		dftRoute = "Default - (None)";
%>
                                      <option value="0"><%= dftRoute %></option>
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
                      </div>
                  </td>
                </tr>
              </table>
            <table width="400" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
              <tr> 
                  <td width="42%" align="right"> 
                    <input type="submit" name="Submit" value="Save">
                  </td>
                  <td width="15%" align="center"> 
                    <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
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
<%	if (inventory.getStarsLMHardwareHistory() != null) { %>
            <hr>
            <div align="center"> 
              <span class="TitleHeader">Hardware History</span><br>
              <table width="300" border="1" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td width="50%" class="HeaderCell">Date</td>
                  <td width="50%" class="HeaderCell">Action</td>
                </tr>
<%
		StarsLMHardwareHistory hwHist = inventory.getStarsLMHardwareHistory();
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
                      <input type="button" name="More2" value="More" onclick="if (warnUnsavedChanges()) location='InventoryHist.jsp?InvNo=<%= invNo %>'">
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
