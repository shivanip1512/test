<%@ include file="StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	if (inventories.getStarsLMHardwareCount() == 0) {
		response.sendRedirect("CreateHardware.jsp"); return;
	}
	
	String invNoStr = request.getParameter("InvNo");
	int invNo = 0;
	if (invNoStr != null)
		try {
			invNo = Integer.parseInt(invNoStr);
		}
		catch (NumberFormatException e) {}
	if (invNo < 0 || invNo >= inventories.getStarsLMHardwareCount())
		invNo = 0;

	StarsLMHardware hardware = inventories.getStarsLMHardware(invNo);
	ArrayList appList = new ArrayList();
	StarsServiceCompany company = null;
	
	for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
		StarsAppliance app = appliances.getStarsAppliance(i);
		if (app.getInventoryID() == hardware.getInventoryID())
			appList.add(app);
	}
	
	StarsAppliance[] starsApps = new StarsAppliance[ appList.size() ];
	appList.toArray( starsApps );
	
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany comp = companies.getStarsServiceCompany(i);
		if (comp.getCompanyID() == hardware.getInstallationCompany().getEntryID()) {
			company = comp;
			break;
		}
	}
	
	String hwGroupName = "Switch";
	if (hardware.getStarsThermostatSettings() != null)
		hwGroupName = "Thermostat";
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function deleteHardware() {
	var form = document.invForm;
<%
	if (starsApps.length > 0) {
%>
	if (!confirm('To delete the hardware, all programs related with it will be invalidated, do you want to proceed?')) return;
<%
	}
	else {
%>
	if (!confirm('Are you sure you would like to delete this hardware?')) return;
<%
	}
%>
	form.elements('action').value = "DeleteLMHardware";
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
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
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
            <% String pageName = "Inventory.jsp?InvNo=" + invNo; %>
            <%@ include file="Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "HARDWARE"; %><%@ include file="InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
			  <form name="invForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
                <input type="hidden" name="action" value="UpdateLMHardware">
                <input type="hidden" name="InvID" value="<%= hardware.getInventoryID() %>">
				<input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Inventory.jsp?InvNo=<%= invNo %>">
				<input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/Inventory.jsp?InvNo=<%= invNo %>">
                <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                    <table width="300" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                          <td valign="top"><span class="SubtitleHeader">DEVICE</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Type: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" size="24" maxlength="30" value="<%= hardware.getLMDeviceType().getContent() %>">
                                  <input type="hidden" name="DeviceType" value="<%= hardware.getLMDeviceType().getEntryID() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Serial #: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= hardware.getManufactureSerialNumber() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Label: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= hardware.getDeviceLabel() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Alt Tracking #: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= hardware.getAltTrackingNumber() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Receive Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(hardware.getReceiveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Remove Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(hardware.getRemoveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Voltage: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" maxlength="30" size="24" value="<%= hardware.getVoltage().getContent() %>">
                                  <input type="hidden" name="Voltage" value="<%= hardware.getVoltage().getEntryID() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Status: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="Status" maxlength="30" size="24" value="<%= hardware.getDeviceStatus().getContent() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Notes: </div>
                                </td>
                                <td width="200"> 
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= hardware.getNotes() %></textarea>
                                </td>
                              </tr>
                              <!--
                            <tr>
                              <td width="100" class="TableCell">&nbsp;</td>
                              <td width="200">
                                <input type="button" name="Submit5" value="Config">
                              </td>
                            </tr>
-->
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
                                    <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(hardware.getInstallDate(), datePart) %>">
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
                                    <textarea name="InstallNotes" rows="3 wrap="soft" cols="28" class = "TableCell"><%= hardware.getInstallationNotes() %></textarea>
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
                                  <%= ServletUtils.getFormattedAddress( company.getCompanyAddress() ) %><br>
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
                  <td width="42%"> 
                    <div align="right"> 
                      <input type="submit" name="Submit2" value="Submit">
                    </div>
                  </td>
                  <td width="15%" align = "center"> 
                    <input type="reset" name="Cancel2" value="Cancel">
                  </td>
                  <td width="43%"> 
                    <div align="left">
                      <input type="button" name="Submit" value="Delete" onclick="deleteHardware()">
                    </div>
                  </td>
              </tr>
            </table>
              </form>
            </div>
            <hr>
            <div align="center"> 
              <div align="center"><span class="TitleHeader">Hardware History</span><br>
                <table width="300" border="1" cellspacing="0" cellpadding="3" align="center">
                  <tr> 
                    <td width="50%" class="HeaderCell">Date</td>
                    <td width="50%" class="HeaderCell">Action</td>
                  </tr>
                  <%
	StarsLMHardwareHistory hwHist = hardware.getStarsLMHardwareHistory();
	for (int i = hwHist.getStarsLMHardwareEventCount() - 1; i >= 0 && i >= hwHist.getStarsLMHardwareEventCount() - 5; i--) {
		StarsLMHardwareEvent event = hwHist.getStarsLMHardwareEvent(i);
%>
                  <tr valign="top"> 
                    <td width="50%" class="TableCell" bgcolor="#FFFFFF"><%= datePart.format(event.getEventDateTime()) %></td>
                    <td width="50%" class="TableCell" bgcolor="#FFFFFF"><%= event.getEventAction() %></td>
                  </tr>
                  <%
	}
%>
                </table>
                <%
	if (hwHist.getStarsLMHardwareEventCount() > 5) {
%>
                <table width="300" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td> 
                      <div align="right"> 
                        <input type="button" name="More2" value="More" onClick="location='InventoryHist.jsp?InvNo=<%= invNoStr %>'">
                      </div>
                    </td>
                  </tr>
                </table>
                <%
	}
%>
              </div>
            </div>
            <p>&nbsp;</p>
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
