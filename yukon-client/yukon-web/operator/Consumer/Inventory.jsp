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
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
function sendCommand(cmd) {
	var form = document.ctrlForm;
	form.action.value = cmd;
	form.submit();
}

function deleteHardware(form) {
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
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
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
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF" height="319"> 
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
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= ServletUtils.getDateFormat(hardware.getReceiveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Remove Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= ServletUtils.getDateFormat(hardware.getRemoveDate(), datePart) %>">
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
	StarsCustSelectionList statusList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.common.constants.YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS );
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
                  <td width="300" valign="top" bgcolor="#FFFFFF" height="319"> 
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
                                    <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= ServletUtils.getDateFormat(hardware.getInstallDate(), datePart) %>">
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="100" class="TableCell"> 
                                    <div align="right">Service Company: </div>
                                  </td>
                                  <td width="200"> 
                                    <select name="ServiceCompany">
                              <%
	StarsCustSelectionList serviceCompanyList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
	for (int i = 0; i < serviceCompanyList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = serviceCompanyList.getStarsSelectionListEntry(i);
		String selectedStr = (entry.getEntryID() == hardware.getInstallationCompany().getEntryID())? "selected" : "";
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
                                    <div align="right">Location: </div>
                                  </td>
                                  <td width="200"> 
                                    <select name="Location">
                                      <option>Outside North</option>
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
                                  <%= company.getCompanyAddress().getStreetAddr1() %><br>
								  <% if (company.getCompanyAddress().getStreetAddr2().length() > 0) out.write(company.getCompanyAddress().getStreetAddr2() + "<br>"); %>
                                  <%= company.getCompanyAddress().getCity() %>, <%= company.getCompanyAddress().getState() %> <%= company.getCompanyAddress().getZip() %><br>
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
			</form>
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center" height="66">
              <tr> 
                <td width="300" valign="top" bgcolor="#FFFFFF" height="65"> 
                  <table width="300" border="0" cellspacing="0" cellpadding="0" height="65">
                    <tr> 
                      <form name="ctrlForm" method="POST" action="/servlet/SOAPClient">
					    <input type="hidden" name="action" value="">
						<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() + "?InvNo=" + invNo %>">
						<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() + "?InvNo=" + invNo %>">
						<input type="hidden" name="InvID" value="<%= hardware.getInventoryID() %>">
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
                      </form>
                    </tr>
                  </table>
                  
                </td>
                <td width="300" valign="top" bgcolor="#FFFFFF" height="65"> 
                  <div align="center">
<table width="305" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                        <form name="form3" method="get" action="InventoryHist.jsp">
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
                              <!--<table width="250" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                  <td> 
                                    <div align="right">
                                      <input type="submit" name="More" value="More">
                                    </div>
                                  </td>
                                </tr>
                              </table>-->
                            </td>
                        </form>
                      </tr>
                    </table>
                  </div>
                  
                </td>
              </tr>
            </table>
            </div>
            <table width="400" border="0" cellspacing="0" cellpadding="3" align="center" bgcolor="#FFFFFF">
              <tr> 
                  <td width="42%"> 
                    <div align="right"> 
                      <input type="button" name="Submit2" value="Submit" onclick="document.invForm.submit()">
                    </div>
                  </td>
                  <td width="15%" align = "center"> 
                    <input type="button" name="Cancel2" value="Cancel" onclick="document.invForm.reset()">
                  </td>
                  <td width="43%"> 
                    <div align="left">
                      <input type="button" name="Submit" value="Delete" onclick="deleteHardware(document.invForm)">
                    </div>
                  </td>
              </tr>
            </table>
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
                  <td width="104" class="TableCell"> <%= starsApps[i].getCategoryName() %></td>
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
