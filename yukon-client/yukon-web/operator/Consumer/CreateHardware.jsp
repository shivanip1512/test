<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="CssLink" rel="stylesheet" href="../demostyle.css" type="text/css">
<% if (ecWebSettings.getURL().length() > 0) { %>
	<script language="JavaScript">document.getElementById("CssLink").href = "../<%= ecWebSettings.getURL() %>";</script>
<% } %>

<script language="JavaScript">
function confirmCancel() {
	if (confirm('Are you sure you want to stop the process? (You can make changes to the account later)'))
		location = "../Operations.jsp";
}

function changeAppSelection(chkBox) {
	var grpList = document.getElementById('Group_App' + chkBox.value);
	grpList.disabled = !chkBox.checked;
}
</script>
</head>
<body class="Background" leftmargin="0" topmargin="0" >
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td id="Header" colspan="4" height="74" background="../Header.gif">&nbsp;</td>
<% if (ecWebSettings.getLogoLocation().length() > 0) { %>
	<script language="JavaScript">document.getElementById("Header").background = "../<%= ecWebSettings.getLogoLocation() %>";</script>
<% } %>
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
          <td width="1" height="102" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
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
<% if (request.getParameter("Wizard") == null) { %>
		    <% String pageName = "CreateHardware.jsp"; %><%@ include file="Nav.jsp" %>
<% } else out.print("&nbsp;"); %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "Main" align="center">
              <% String header = "CREATE NEW HARDWARE"; %>
              <%@ include file="InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="MForm" method="post" action="/servlet/SOAPClient">
			    <input type="hidden" name="action" value="CreateLMHardware">
				<input type="hidden" name="InvNo" value="<%= inventories.getStarsLMHardwareCount() %>">
				<input type="hidden" name="Wizard" value="false">
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
                                  <select name="DeviceType">
                              <%
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
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
                                  <div align="right">Serial #: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Alt Tracking #: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Receive Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Remove Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="">
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
                                  <div align="right">Status: </div>
                                </td>
                                <td width="200"> 
                                  <select name="Status">
                              <%
	StarsCustSelectionList statusList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS );
	for (int i = 0; i < statusList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = statusList.getStarsSelectionListEntry(i);
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
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
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
                          <td valign="top"><span class="MainHeader"><b>INSTALL</b></span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Date Installed: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= datePart.format(new Date()) %>">
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
%>
                              		<option value="<%= company.getCompanyID() %>"><%= company.getCompanyName() %></option>
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
                                  <textarea name="InstallNotes" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <br>
                      <span class="Main">Select appliances connected with 
                      this hardware:</span><br>
                      <table width="300" border="1" cellspacing="0" cellpadding="3">
                        <tr> 
                          <td width="10%" class="HeaderCell">&nbsp; </td>
                          <td width="40%" class="HeaderCell">Appliance</td>
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
%>
                        <tr> 
                          <td width="27" height="2"> 
                            <input type="checkbox" name="AppID" value="<%= appliance.getApplianceID() %>" onclick="changeAppSelection(this)">
                          </td>
                          <td width="73" class="TableCell" height="2"><%= appliance.getDescription() %></td>
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
                    </td>
                  </tr>
                </table>
                <br>
                <table width="400" border="0">
                <tr>
                  <td align ="right" width = "50%"> 
<% if (request.getParameter("Wizard") == null) { %>
                    <input type="submit" name="Submit" value="Save">
<% } else { %>
                    <input type="submit" name="Done" value="Done" onclick="this.form.Wizard.value='true'">
<% } %>
                  </td>
                  <td> 
<% if (request.getParameter("Wizard") == null) { %>
                    <input type="reset" name="Cancel" value="Cancel">
<% } else { %>
                    <input type="button" name="Cancel2" value="Cancel" onclick="confirmCancel()">
<% } %>
                  </td>
                </tr>
              </table><br>
              </form>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
