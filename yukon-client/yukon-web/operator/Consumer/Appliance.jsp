<%@ include file="StarsHeader.jsp" %>
<%
	if (appliances.getStarsApplianceCount() == 0) {
		response.sendRedirect("CreateAppliances.jsp"); return;
	}
	
	String appNoStr = request.getParameter("AppNo");
	int appNo = 0;
	if (appNoStr != null)
		try {
			appNo = Integer.parseInt(appNoStr);
		}
		catch (NumberFormatException e) {}
	if (appNo < 0 || appNo >= appliances.getStarsApplianceCount())
		appNo = 0;
		
	StarsAppliance appliance = appliances.getStarsAppliance(appNo);
	StarsLMHardware hardware = null;
	StarsLMProgram program = null;
	StarsApplianceCategory category = null;
	int progNo = 0;
	
	for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
		StarsLMHardware hw = inventories.getStarsLMHardware(i);
		if (hw.getInventoryID() == appliance.getInventoryID()) {
			hardware = hw;
			break;
		}
	}

	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
		StarsLMProgram starsProg = programs.getStarsLMProgram(i);
		if (starsProg.getProgramID() == appliance.getLmProgramID()) {
			program = starsProg;
			progNo = i;
			break;
		}
	}
	
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory appCat = categories.getStarsApplianceCategory(i);
		if (appCat.getApplianceCategoryID() == appliance.getApplianceCategoryID()) {
			category = appCat;
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
function deleteAppliance(form) {
<%
	if (program != null) {
%>
	if (!confirm('To delete the appliance, the program related with it will be removed as well, do you want to proceed?')) return;
<%
	}
	else {
%>
	if (!confirm('Are you sure you would like to delete this appliance?')) return;
<%
	}
%>
	form.elements('action').value = 'DeleteAppliance';
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
            <% String pageName = "Appliance.jsp?AppNo=" + appNo; %>
            <%@ include file="Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><% String header = "APPLIANCES"; %> <%@ include file="InfoSearchBar.jsp" %>
			<% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			
              <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                <tr>
				<form name="form1" method="post" action="/servlet/SOAPClient"> 
				  <input type="hidden" name="action" value="UpdateAppliance">
				  <input type="hidden" name="AppID" value="<%= appliance.getApplianceID() %>">
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Description: </div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="Category" maxlength="40" size="30" value="<%= appliance.getCategoryName() %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Manufacturer:</div>
                          </td>
                          <td width="200"> 
                            <select name="Manufacturer">
<%
	StarsCustSelectionList manuList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.common.constants.YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER );
	for (int i = 0; i < manuList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = manuList.getStarsSelectionListEntry(i);
		String selectedStr = (entry.getEntryID() == appliance.getManufacturer().getEntryID()) ? "selected" : "";
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
                            <div align="right">Model #:</div>
                          </td>
                          <td width="200">
                            <input type="text" name="ModelNumber" maxlength="40" size="30" value="">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Year Manufactured:</div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="ManuYear" maxlength="14" size="14" value="<%= appliance.getYearManufactured() %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Location:</div>
                          </td>
                          <td width="200"> 
                            <select name="Location">
<%
	StarsCustSelectionList locationList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.common.constants.YukonSelectionListDefs.YUK_LIST_NAME_LOCATION );
	for (int i = 0; i < locationList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = locationList.getStarsSelectionListEntry(i);
		String selectedStr = (entry.getEntryID() == appliance.getLocation().getEntryID()) ? "selected" : "";
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
                            <div align="right">Notes:</div>
                          </td>
                          <td width="200"> 
                            <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= appliance.getNotes() %></textarea>
                          </td>
                        </tr>
                      </table>
                      </td>
				  </form>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
<%
	if (program != null) {
%>
				    <form name="form7" method="POST" action="/servlet/SOAPClient">
					  <input type="hidden" name="action" value="GetLMCtrlHist">
					  <input type="hidden" name="Group" value="<%= program.getGroupID() %>">
					  <input type="hidden" name="REDIRECT" value="/operator/Consumer/ContHist.jsp?prog=<%= progNo %>">
                      <table width="250" border="1" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td width="109" class="HeaderCell"> 
                            <div align="center">
                             Enrolled Programs
                            </div>
                          </td>
                          <td width="151" class="HeaderCell"> 
                            <div align="center">Control 
                              History</div>
                          </td>
                        </tr>
                        <tr valign="top"> 
                          <td width="109" bgcolor="#FFFFFF"> 
                            <div align="center"> <img src="<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br>
							  <span class="TableCell"><%= program.getProgramName() %></span>
							</div>
                          </td>
                          <td width="151" bgcolor="#FFFFFF"> 
                            <table width="150" border="0" cellspacing="0" cellpadding="3" align="center">
                              <tr> 
                                <td width="180" valign="top" align="center"> 
                                  <select name="Period">
                                    <option value="PastWeek">Past Week</option>
                                    <option value="PastMonth">Past Month </option>
                                    <option value="All">All</option>
                                  </select>
                                  <br>
                                </td>
                              </tr>
                              <tr> 
                                <td width="180" valign="top" align="center"> 
                                  <input type="submit" name="View22" value="View">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <table width="250" border="1" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td class="TableCell"> 
                            <div align="center">Controllable kW:&nbsp; 
                              <input type="text" name="textfield2233" maxlength="2" size="10">
                            </div>
                          </td>
                          </tr>
                      </table>
                     </form>
<%
	}
	else {
%>
					<p align="center" class="Main">There is no program for 
                      this appliance</p>
<%
	}
%>
                    </td>
                </tr>
              </table>
              <table width="250" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr>
                  <td width="33%" align = "right"> 
                    <input type="button" name="Submit" value="Submit" onclick="document.form1.submit()">
                  </td>
                    <td width="33%" align = "center"> 
                      <div>
                        <input type="button" name="Cancel" value="Cancel" onclick="document.form1.reset()">
                      </div>
                    </td>
                    <td width="33%"> 
                      <div align="left"> 
                        <input type="button" name="Delete" value="Delete" onclick="deleteAppliance(document.form1)">
                      </div>
                    </td>
                </tr>
              </table>
              <hr>
              <div align="center" class="MainHeader"><br>
                <b>Hardware Summary</b></div>
<%
	if (hardware != null) {
%>
              <table width="360" border="1" cellspacing="0" cellpadding="3" align="center">
                <tr bgcolor="#CCCCCC"> 
                  <td width="75" class="HeaderCell">Category</td>
                  <td width="75" class="HeaderCell">Type</td>
                  <td width="75" class="HeaderCell">Serial #</td>
                </tr>
                <tr bgcolor="#FFFFFF"> 
                  <td width="75" class="TableCell"><%= hardware.getCategory() %></td>
                  <td width="75" class="TableCell"><%= hardware.getLMDeviceType().getContent() %></td>
                  <td width="75" class="TableCell"><%= hardware.getManufactureSerialNumber() %></td>
                </tr>
              </table>
<%
	}
	else {
%>
			  <p class="Main">There is no hardware attached to this appliance</p>
<%
	}
%>
              <p>&nbsp;</p>
              </div>
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
