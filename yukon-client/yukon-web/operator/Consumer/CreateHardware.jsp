<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	boolean inWizard = request.getParameter("Wizard") != null;
	
	boolean invCheckEarly = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.INVENTORY_CHECKING_TIME).equalsIgnoreCase(InventoryManager.INVENTORY_CHECKING_TIME_EARLY);
	if (!invCheckEarly)
		session.removeAttribute(InventoryManager.STARS_INVENTORY_TEMP);
	
	StarsInventory inventory = (StarsInventory) session.getAttribute(InventoryManager.STARS_INVENTORY_TEMP);
	if (inventory == null) {
		if (invCheckEarly) {
			String redirect = request.getContextPath() + "/operator/Consumer/SerialNumber.jsp?action=New";
			if (inWizard) redirect += "&Wizard=true";
			response.sendRedirect(redirect);
			return;
		}
		
		inventory = (StarsLMHardware) StarsFactory.newStarsInventory(StarsLMHardware.class);
	}
	
	StarsCustListEntry devTypeMCT = ServletUtils.getStarsCustListEntry(selectionListTable, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_METER);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function changeAppSelection(chkBox) {
	var grpList = document.getElementById('Group_App' + chkBox.value);
	grpList.disabled = !chkBox.checked;
}

function changeDeviceType() {
<% if (devTypeMCT != null) { %>
	if (document.MForm.DeviceType.value == <%= devTypeMCT.getEntryID() %>)
		document.getElementById("NameLabel").innerText = "Device Name";
	else
<% } %>
		document.getElementById("NameLabel").innerText = "Serial #";
}

function validate(form) {
	if (form.SerialNo.value == "") {
		alert(document.getElementById("NameLabel").innerText + " cannot be empty");
		return false;
	}
<% if (!invCheckEarly) { %>
	form.attributes["action"].value = "<%= request.getContextPath() %>/servlet/InventoryManager";
<% } %>
	return true;
}

function changeSerialNo() {
	document.snForm.submit();
}
</script>
</head>
<body class="Background" leftmargin="0" topmargin="0" onload="changeDeviceType()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
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
<%	if (inWizard) out.print("&nbsp;");
	else{
%>
		    <% String pageName = "CreateHardware.jsp"; %>
			<%@ include file="include/Nav.jsp" %>
<%	} %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "MainText" align="center">
              <% String header = "CREATE NEW HARDWARE"; %>
              <%@ include file="include/InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			    <input type="hidden" name="action" value="CreateLMHardware">
				<input type="hidden" name="InvID" value="<%= inventory.getInventoryID() %>">
				<input type="hidden" name="DeviceID" value="<%= inventory.getDeviceID() %>">
				<% if (inWizard) { %><input type="hidden" name="Wizard" value="true"><% } %>
                <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                  <tr> 
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td valign="top"><span class="SubtitleHeader">DEVICE</span> 
                            <hr>
<%
	if (invCheckEarly) {
		int devTypeID = 0;
		String devTypeStr = null;
		String valStr = null;
		
		if (inventory instanceof StarsLMHardware) {
			devTypeID = ((StarsLMHardware) inventory).getLMDeviceType().getEntryID();
			devTypeStr = ((StarsLMHardware) inventory).getLMDeviceType().getContent();
			valStr = ((StarsLMHardware) inventory).getManufactureSerialNumber();
		}
		else if (inventory instanceof StarsMCT) {
			devTypeID = devTypeMCT.getEntryID();
			devTypeStr = devTypeMCT.getContent();
			valStr = ((StarsMCT) inventory).getDeviceName();
		}
%>
							<input type="hidden" name="DeviceType" value="<%= devTypeID %>">
							<input type="hidden" name="SerialNo" value="<%= valStr %>">
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
                                <td width="100" class="TableCell" align="right"><span id="NameLabel"></span>: </td>
                                <td width="120" class="MainText"><%= valStr %></td>
                              </tr>
                            </table>
<%	} %>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
<%
	if (!invCheckEarly) {
		StarsLMHardware hardware = (StarsLMHardware) inventory;
%> 
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Type: </div>
                                </td>
                                <td width="200"> 
                                  <select name="DeviceType" onchange="changeDeviceType()">
                                    <%
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == hardware.getLMDeviceType().getEntryID())? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell" align="right"><span id="Label1">Serial 
                                  #: </span></td>
                                <td width="200"> 
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= hardware.getManufactureSerialNumber() %>">
                                </td>
                              </tr>
<%	} %> 
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Label: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= inventory.getDeviceLabel() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Alt Tracking #: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= inventory.getAltTrackingNumber() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Receive Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getReceiveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Remove Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getRemoveDate(), datePart) %>">
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
                                  <div align="right">Notes: </div>
                                </td>
                                <td width="200"> 
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= inventory.getNotes() %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
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
                                  <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getInstallDate(), datePart) %>">
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
		StarsServiceCompany company = companies.getStarsServiceCompany(i);
		String selected = (company.getCompanyID() == inventory.getInstallationCompany().getEntryID())? "selected" : "";
%>
                              		<option value="<%= company.getCompanyID() %>" <%= selected %>><%= company.getCompanyName() %></option>
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
                                  <textarea name="InstallNotes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= inventory.getInstallationNotes() %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<%
	if (inventory instanceof StarsLMHardware) {
%>
                      <br>
                      <span class="Subtext">Select all programs controlled by this 
                      hardware, and assign groups to them:</span><br>
                      <table width="300" border="1" cellspacing="0" cellpadding="3">
                        <tr> 
                          <td width="10%" class="HeaderCell">&nbsp; </td>
                          <td width="40%" class="HeaderCell">Program</td>
                          <td width="50%" class="HeaderCell">Assigned Group</td>
                        </tr>
<%
	for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
		StarsAppliance appliance = appliances.getStarsAppliance(i);
		if (appliance.getInventoryID() == 0 && appliance.getLmProgramID() > 0) {
			StarsEnrLMProgram program = null;
			for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
				StarsApplianceCategory category = categories.getStarsApplianceCategory(j);
				if (category.getApplianceCategoryID() == appliance.getApplianceCategoryID()) {
					for (int k = 0; k < category.getStarsEnrLMProgramCount(); k++) {
						StarsEnrLMProgram prog = category.getStarsEnrLMProgram(k);
						if (prog.getProgramID() == appliance.getLmProgramID()) {
							program = prog;
							break;
						}
					}
					break;
				}
			}
			boolean disabled = (program == null || program.getAddressingGroupCount() == 0);
%>
                        <tr> 
                          <td width="27" height="2"> 
                            <input type="checkbox" name="AppID" value="<%= appliance.getApplianceID() %>" onclick="changeAppSelection(this)"
							 <%= (disabled)? "disabled" : "" %>>
                          </td>
                          <td width="73" class="TableCell" height="2"><%= program.getProgramName() %></td>
                          <td width="89" height="2"> 
                            <select id="Group_App<%= appliance.getApplianceID() %>" name="GroupID" disabled>
<%
			if (disabled) {
%>
                              <option value="0">(none)</option>
<%
			} else {
				for (int j = 0; j < program.getAddressingGroupCount(); j++) {
					AddressingGroup group = program.getAddressingGroup(j);
%>
                              <option value="<%= group.getEntryID() %>"><%= group.getContent() %></option>
<%
				}
			}
%>
                            </select>
                          </td>
                        </tr>
<%
		}
	}
%>
                      </table>
<%	} %>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="400" border="0">
                <tr>
                  <td align ="right" width = "50%"> 
<% if (!inWizard) { %>
                    <input type="submit" name="Submit" value="Save" onclick="return validate(this.form)">
<% } else { %>
                    <input type="submit" name="Next" value="Next" onclick="return validate(this.form)">
<% } %>
                  </td>
                  <td> 
<% if (!inWizard) { %>
                    <input type="reset" name="Cancel" value="Reset">
<% } else { %>
                    <input type="button" name="Cancel" value="Cancel" onclick="location.href = '../Operations.jsp'">
<% } %>
                  </td>
                </tr>
              </table><br>
              </form>
              <form name="snForm" method="post" action="SerialNumber.jsp">
                <input type="hidden" name="action" value="Change">
              </form>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
