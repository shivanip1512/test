<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.*" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<%@ page import="com.cannontech.database.data.pao.RouteTypes" %>
<%@ page import="com.cannontech.stars.web.util.InventoryManagerUtil" %>
<%
	StarsInventory inventory = null;
	String referer = request.getContextPath() + "/operator/Hardware/CreateMCT.jsp";
	
	if (request.getParameter("RefId") != null) {
		int refID = Integer.parseInt( request.getParameter("RefId") );
		LiteInventoryBase liteInv = liteEC.getInventoryBrief(refID, true);
		if (liteInv != null)
			inventory = StarsLiteFactory.createStarsInventory(liteInv, liteEC);
		referer += "?RefId=" + refID;
	}
	
	if (inventory == null)
		inventory = (StarsInventory) StarsFactory.newStarsInv(StarsInventory.class);
	inventory.setInstallDate(null);
	
	int deviceID = 0;
	String deviceType = "(none)";
	String deviceName = "(none)";
	String disabled = "";
	
	if (request.getParameter("Selected") == null)
		session.removeAttribute(InventoryManagerUtil.DEVICE_SELECTED);
	
	LiteYukonPAObject selectedMCT = (LiteYukonPAObject) session.getAttribute(InventoryManagerUtil.DEVICE_SELECTED);
	if (selectedMCT != null) {
		deviceID = selectedMCT.getYukonID();
		deviceType = PAOGroups.getPAOTypeString(selectedMCT.getType());
		deviceName = selectedMCT.getPaoName();
		disabled = "disabled";
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function setDefaultMeterNumber(form) {
	if (form.PhysicalAddr.value != "" && form.MeterNumber.value == "") {
		if (form.MCTType.value == <%= PAOGroups.MCT410IL %>)
			form.MeterNumber.value = form.PhysicalAddr.value;
		else
			form.MeterNumber.value = "10" + form.PhysicalAddr.value;
	}
}

function selectMCT(form) {
	form.attributes["action"].value = "SelectMCT.jsp";
	form.submit();
}

function validate(form) {
	if (form.DeviceName.value == "") {
		if (form.DeviceID.value == -1) {
			alert("You must either enter device name for a new MCT, or select a meter from the list of all MCTs");
			return false;
		}
	}
	else {
		if (form.PhysicalAddr.value == "") {
			alert("Physical address cannot be empty");
			return false;
		}
		if (form.MeterNumber.value == "") {
			alert("Meter number cannot be empty");
			return false;
		}
	}
	return true;
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
		    <% String pageName = "CreateMCT.jsp"; %>
			<%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "CREATE NEW MCT"; %>
              <%@ include file="include/SearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager" onsubmit="return validate(this)">
			    <input type="hidden" name="action" value="CreateMCT">
				<input type="hidden" name="DeviceID" value="<%= deviceID %>">
				<input type="hidden" name="DeviceType" value="<%= liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT).getEntryID() %>">
				<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Hardware/InventoryDetail.jsp?InvId=">
                <input type="hidden" name="REFERRER" value="<%= referer %>">
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                  <tr> 
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><span class="SubtitleHeader">MCT</span> 
                            <hr>
                            <span class="MainText">Create a new MCT: <br>
                            <br>
                            </span> 
                            <table width="300" border="0" cellspacing="0" cellpadding="1" class="TableCell">
                              <tr> 
                                <td align="right" width="88" class="SubtitleHeader">*Type:</td>
                                <td width="210"> 
                                  <select name="MCTType" onchange="setContentChanged(true)" <%= disabled %>>
                                    <%
	for (int i = 0; i < InventoryManagerUtil.MCT_TYPES.length; i++) {
		int mctType = PAOGroups.getDeviceType(InventoryManagerUtil.MCT_TYPES[i]);
%>
                                    <option value="<%= mctType %>"><%= InventoryManagerUtil.MCT_TYPES[i] %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" width="88" class="SubtitleHeader">*Device Name:</td>
                                <td width="210"> 
                                  <input type="text" name="DeviceName" size="24" onchange="setContentChanged(true)" <%= disabled %>>
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" width="88" class="SubtitleHeader">*Physical Addr:</td>
                                <td width="210"> 
                                  <input type="text" name="PhysicalAddr" size="24" onblur="setDefaultMeterNumber(this.form)" onchange="setContentChanged(true)" <%= disabled %>>
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" width="88" class="SubtitleHeader">*Meter Number:</td>
                                <td width="210"> 
                                  <input type="text" name="MeterNumber" size="24" onchange="setContentChanged(true)" <%= disabled %>>
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" width="88" class="SubtitleHeader">*Route:</td>
                                <td width="210"> 
                                  <select name="MCTRoute" onchange="setContentChanged(true)" <%= disabled %>>
                                    <%
	LiteYukonPAObject[] routes = liteEC.getAllRoutes();
	for (int i = 0; i < routes.length; i++) {
		if (routes[i].getType() == RouteTypes.ROUTE_CCU || routes[i].getType() == RouteTypes.ROUTE_MACRO) {
%>
                                    <option value="<%= routes[i].getYukonID() %>"><%= routes[i].getPaoName() %></option>
                                    <%
		}
	}
%>
                                  </select>
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
                          <td> <span class="SubtitleHeader">&nbsp;</span>
						    <hr>
                            <span class="MainText">Or select from the list of 
                            all MCTs:</span> 
<% if (selectedMCT != null) { %>
                            <p align="center"> 
                            <table width="300" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                              <tr> 
                                <td align="right" width="135">Type:</td>
                                <td width="153"><%= deviceType %></td>
                              </tr>
                              <tr> 
                                <td align="right" width="135">Device Name:</td>
                                <td width="153"><%= deviceName %></td>
                              </tr>
                            </table>
                            <br>
                              <input type="button" name="ChangeMCT" value="Change" onclick="selectMCT(this.form)">
                              <input type="button" name="ClearMCT" value="Clear" onclick="location.href = '<%= referer %>'">
                            </p>
<% } else { %>
                            <p align="center"> 
                              <input type="button" name="SelectMCT" value="Select Meter" onclick="selectMCT(this.form)">
                            </p>
<% } %>
                          </td>
                        </tr>
                      </table>
                      <script language="JavaScript">setContentChanged(<%= request.getParameter("selected") != null %>);</script>
                    </td>
                  </tr>
                  <tr>
                    <td width="300" valign="top" bgcolor="#FFFFFF">
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><span class="SubtitleHeader">DEVICE</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Label:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= inventory.getDeviceLabel() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Alt Tracking #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= inventory.getAltTrackingNumber() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Received:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getReceiveDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Removed:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getRemoveDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Voltage:</div>
                                </td>
                                <td width="210"> 
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
                                <td width="88" class="TableCell"> 
                                  <div align="right">Notes:</div>
                                </td>
                                <td width="210"> 
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= inventory.getNotes().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
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
                                  <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getInstallDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Service Company:</div>
                                </td>
                                <td width="210"> 
                                  <select name="ServiceCompany" onchange="setContentChanged(true)">
                                    <%
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany company = companies.getStarsServiceCompany(i);
		String selected = (company.getCompanyID() == inventory.getInstallationCompany().getEntryID()) ? "selected" : "";
%>
                                    <option value="<%= company.getCompanyID() %>" <%= selected %>><%= company.getCompanyName() %></option>
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
                                  <textarea name="InstallNotes" rows="3 wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= inventory.getInstallationNotes().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <table width="300" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="right"> 
                      <input type="submit" name="Save" value="Save">
                    </td>
                    <td> 
                      <input type="button" name="Reset" value="Reset" onclick="location.href='<%= referer %>'">
                    </td>
                  </tr>
                </table>
              </form>
              <br>
            </div>
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
