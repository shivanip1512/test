<%@ include file="StarsHeader.jsp" %>
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
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
</script>

<script language="JavaScript">
function sendCommand(cmd) {
	var form = document.invForm;
	form.action.value = cmd;
	form.submit();
}

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

function changeAppSelection(chkBox) {
	var grpList = document.getElementById('Group_App' + chkBox.value);
	grpList.disabled = !chkBox.checked;
	document.invForm.ConfigChanged.value = "true";
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
                <td id="Header" colspan="4" height="74" background="../Header.gif">&nbsp;</td>
<script language="JavaScript">
	document.getElementById("Header").background = '../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>';
</script>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
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
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "HARDWARE"; %>
              <%@ include file="InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
			  <form name="invForm" method="POST" action="/servlet/SOAPClient">
                <input type="hidden" name="action" value="UpdateLMHardware">
                <input type="hidden" name="InvID" value="<%= hardware.getInventoryID() %>">
				<input type="hidden" name="REDIRECT" value="/operator/Consumer/Inventory.jsp?InvNo=<%= invNo %>">
				<input type="hidden" name="REFERRER" value="/operator/Consumer/Inventory.jsp?InvNo=<%= invNo %>">
				<input type="hidden" name="ConfigChanged" value="false">
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                    <table width="300" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                          <td valign="top"><span class="MainHeader"><b>DEVICE</b></span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Type: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="DeviceType" size="24" maxlength="30" value="<%= hardware.getLMDeviceType().getContent() %>">
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
                                  <input type="text" name="Voltage" maxlength="30" size="24" value="<%= hardware.getVoltage().getContent() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Status: </div>
                                </td>
                                <td width="200"> 
                                  <select name="Status">
<%
	StarsCustSelectionList statusList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS );
	for (int i = 0; i < statusList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = statusList.getStarsSelectionListEntry(i);
		String selectedStr = (entry.getEntryID() == hardware.getDeviceStatus().getEntryID()) ? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
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
                            <td valign="top"><span class="MainHeader"><b>INSTALL</b></span> 
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
%>
                              		  <option value="<%= servCompany.getCompanyID() %>"><%= servCompany.getCompanyName() %></option>
<%
	}
%>
                                    </select>
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="100" class="TableCell"> 
                                    <div align="right">Location: </div>
                                  </td>
                                  <td width="200"> 
                                    <select name="Location">
<%
	StarsCustSelectionList locationList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION );
	for (int i = 0; i < locationList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = locationList.getStarsSelectionListEntry(i);
%>
                              		  <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
                      <br>
                      <table width="100%" border="0" height="68" >
                        <tr > 
                          <td class = "TableCell" align = "center">Service Company<br>
                            <table width="250" border="1" height="86" cellpadding="10" cellspacing = "0">
                              <tr> 
                                <td valign = "top" align = "center" class = "TableCell"> 
                                  <b><%= company.getCompanyName() %><br>
                                  <%= ServletUtils.getFormattedAddress( company.getCompanyAddress() ) %><br>
                                  <%= company.getMainPhoneNumber() %></b> </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      
                    </div>
                  </td>
                </tr>
              </table>
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center" height="66">
              <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF" height="65"> 
                    <span class="MainHeader">Select all programs controlled by this 
                    hardware, and assign groups to them:</span><br>
                    <table width="300" border="1" cellspacing="0" cellpadding="3">
                      <tr> 
                        <td width="10%" class="HeaderCell">&nbsp; </td>
                        <td width="40%" class="HeaderCell">Program</td>
                        <td width="50%" class="HeaderCell">Assigned Group</td>
                      </tr>
<%
	for (int i = 0; i < starsApps.length; i++) {
		StarsLMProgram program = null;
		for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
			StarsLMProgram starsProg = programs.getStarsLMProgram(j);
			if (starsProg.getProgramID() == starsApps[i].getLmProgramID()) {
				program = starsProg;
				break;
			}
		}
		
		StarsEnrLMProgram enrProg = null;
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory category = categories.getStarsApplianceCategory(j);
			if (category.getApplianceCategoryID() == starsApps[i].getApplianceCategoryID()) {
				for (int k = 0; k < category.getStarsEnrLMProgramCount(); k++) {
					StarsEnrLMProgram prog = category.getStarsEnrLMProgram(k);
					if (prog.getProgramID() == starsApps[i].getLmProgramID()) {
						enrProg = prog;
						break;
					}
				}
				break;
			}
		}
%>
                      <tr> 
                        <td width="27" height="2"> 
                          <input type="checkbox" name="AppID" value="<%= starsApps[i].getApplianceID() %>" checked onClick="changeAppSelection(this)">
                        </td>
                        <td width="73" class="TableCell" height="2"><%= program.getProgramName() %></td>
                        <td width="89" height="2"> 
                          <select id="Group_App<%= starsApps[i].getApplianceID() %>" name="GroupID">
<%
		if (enrProg == null || enrProg.getAddressingGroupCount() == 0) {
%>
                            <option value="0">(none)</option>
<%
		} else {
			for (int j = 0; j < enrProg.getAddressingGroupCount(); j++) {
				AddressingGroup group = enrProg.getAddressingGroup(j);
				String selectedStr = (group.getEntryID() == program.getGroupID()) ? "selected" : "";
%>
                            <option value="<%= group.getEntryID() %>" <%= selectedStr %>><%= group.getContent() %></option>
<%
			}
		}
%>
                          </select>
                        </td>
                      </tr>
<%
	}
%>
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
%>
                      <tr> 
                        <td width="27" height="2"> 
                          <input type="checkbox" name="AppID" value="<%= appliance.getApplianceID() %>" onClick="changeAppSelection(this)">
                        </td>
                        <td width="73" class="TableCell" height="2"><%= program.getProgramName() %></td>
                        <td width="89" height="2"> 
                          <select id="Group_App<%= appliance.getApplianceID() %>" name="GroupID" disabled="true">
<%
			if (program == null || program.getAddressingGroupCount() == 0) {
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
                    <br>
                    <table width="300" border="0" cellspacing="0" cellpadding="0" height="65">
                    <tr> 
                        <td valign="top" align = "center" height="33"> 
                          <table width="46%" border="0" height="26" cellpadding = "3" cellspacing = "0">
                            <tr>
                              <td>
                                <table width="150" border="0" cellpadding = "3" cellspacing = "0" height="39" align = "center">
                                  <tr> 
                                    <td width="35%" align = "center">
                                      <input type="button" name="EnableService" value="In Service" onclick="sendCommand(this.name)">
                                    </td>
                                    <td width="35%" align = "center"> 
                                      <input type="button" name="DisableService" value="Out of Service" onclick="sendCommand(this.name)">
                                    </td>
									<td width="30%" align = "center">
									  <input type="button" name="Config" value="Config" onclick="sendCommand(this.name)">
									</td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                          
                        </td>
                    </tr>
                  </table>
                  
                </td>
                <td width="300" valign="top" bgcolor="#FFFFFF" height="65"> 
                  <div align="center">
<table width="305" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                          <td valign="top" align = "center" class = "TableCell"> 
                            <span class="MainHeader">Hardware History</span> <br>
                            <table width="250" border="1" cellspacing="0" cellpadding="3" align="center">
                              <tr> 
                                <td width="104" class="HeaderCell">Date</td>
                                <td width="100" class="HeaderCell">Action</td>
                              </tr>
<%
	StarsLMHardwareHistory hwHist = hardware.getStarsLMHardwareHistory();
	for (int i = hwHist.getStarsLMHardwareEventCount() - 1; i >= 0 && i >= hwHist.getStarsLMHardwareEventCount() - 5; i--) {
		StarsLMHardwareEvent event = hwHist.getStarsLMHardwareEvent(i);
%>
							  <tr valign="top"> 
							    <td width="104" class="TableCell" bgcolor="#FFFFFF"><%= datePart.format(event.getEventDateTime()) %></td>
							    <td width="100" class="TableCell" bgcolor="#FFFFFF"><%= event.getEventAction() %></td>
							  </tr>
<%
	}
%>
                            </table>
<%
	if (hwHist.getStarsLMHardwareEventCount() > 5) {
%>
                              <table width="250" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                  <td> 
                                    <div align="right">
                                      <input type="button" name="More" value="More" onclick="location='InventoryHist.jsp?InvNo=<%= invNoStr %>'">
                                    </div>
                                  </td>
                                </tr>
                              </table>
<%
	}
%>
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
            <div align="center"><br>
              <span class="Main"><b>Appliance Summary</b></span><br>
              <table width="350" border="1" cellspacing="0" cellpadding="3">
                <tr bgcolor="#FFFFFF"> 
                  <td width="104" class="HeaderCell"> Appliance Type</td>
                  <td width="100" class="HeaderCell"> Status</td>
                  <td width="120" class="HeaderCell"> Enrolled Programs</td>
                </tr>
<!--
                <tr bgcolor="#FFFFFF" valign="top"> 
                  <td width="104" class="TableCell"> Water Heater</td>
                  <td width="100" class="TableCell"> In Service</td>
                  <td width="120"> 
                    <div align="center"><img src="WaterHeater.gif" width="60" height="59"></div>
                  </td>
                </tr>
                <tr bgcolor="#FFFFFF" valign="top"> 
                  <td width="104" class="TableCell"> Air Conditioner</td>
                  <td width="100" class="TableCell"> Out of Service</td>
                  <td width="120"> 
                    <div align="center"><img src="AC.gif" width="60" height="59"></div>
                  </td>
                </tr>
-->
<%
	for (int i = 0; i < starsApps.length; i++) {
		StarsLMProgram program = null;
		for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
			StarsLMProgram starsProg = programs.getStarsLMProgram(j);
			if (starsProg.getProgramID() == starsApps[i].getLmProgramID()) {
				program = starsProg;
				break;
			}
		}
		
		StarsApplianceCategory category = null;
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			if (appCat.getApplianceCategoryID() == starsApps[i].getApplianceCategoryID()) {
				category = appCat;
				break;
			}
		}
%>
                <tr bgcolor="#FFFFFF" valign="top"> 
                  <td width="104" class="TableCell"> <%= starsApps[i].getDescription() %></td>
                  <td width="100" class="TableCell"><%= program.getStatus() %></td>
                  <td width="120"> 
                    <div align="center"> <img src="<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br>
					  <span class="TableCell"><%= program.getProgramName() %></span>
					</div>
                  </td>
                </tr>
<%
	}
%>
              </table>
            </div>
            <p>&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
