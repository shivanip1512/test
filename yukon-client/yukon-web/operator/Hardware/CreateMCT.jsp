<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.*" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<%@ page import="com.cannontech.database.data.pao.RouteTypes" %>
<%
	if (request.getParameter("Init") != null) {
		// The "Create MCT" link in the nav is clicked
		session.removeAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
		session.removeAttribute(InventoryManagerUtil.DEVICE_SELECTED);
	}
	else if (request.getParameter("Member") != null) {
		// Request from the same page while member selection has been changed
		ServletUtils.saveRequest(request, session, new String[] {"Member", "MCTType", "DeviceName", "PhysicalAddr", "MeterNumber", "MCTRoute", "DeviceLabel", "AltTrackNo", "ReceiveDate", "Voltage", "ServiceCompany", "Notes"});
	}
	else if (request.getParameter("InvID") != null) {
		// Request from InventoryDetail.jsp to copy a hardware device
		LiteInventoryBase liteInv = liteEC.getInventoryBrief(Integer.parseInt(request.getParameter("InvID")), true);
		LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO(liteInv.getDeviceID());
		Properties savedReq = new Properties();
		savedReq.setProperty("MCTType", String.valueOf(litePao.getType()));
		savedReq.setProperty("DeviceName", litePao.getPaoName() + "(New)");
		savedReq.setProperty("MCTRoute", String.valueOf(litePao.getRouteID()));
		savedReq.setProperty("DeviceLabel", liteInv.getDeviceLabel());
		savedReq.setProperty("ReceiveDate", ServletUtils.formatDate(new Date(liteInv.getReceiveDate()), datePart));
		savedReq.setProperty("Voltage", String.valueOf(liteInv.getVoltageID()));
		savedReq.setProperty("ServiceCompany", String.valueOf(liteInv.getInstallationCompanyID()));
		savedReq.setProperty("Notes", liteInv.getNotes().replaceAll("<br>", System.getProperty("line.separator")));
		session.setAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST, savedReq);
		session.removeAttribute(InventoryManagerUtil.DEVICE_SELECTED);
	}
	else if (request.getParameter("Reset") != null) {
		// Request from the same page when the "Reset" or "Clear" button is clicked
		session.removeAttribute(InventoryManagerUtil.DEVICE_SELECTED);
	}
	
	Properties savedReq = (Properties) session.getAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	if (savedReq == null) savedReq = new Properties();
	
	int deviceID = 0;
	String deviceType = "(none)";
	String deviceName = "(none)";
	String disabled = "";
	
	LiteYukonPAObject selectedMCT = (LiteYukonPAObject) session.getAttribute(InventoryManagerUtil.DEVICE_SELECTED);
	if (selectedMCT != null) {
		deviceID = selectedMCT.getYukonID();
		deviceType = PAOGroups.getPAOTypeString(selectedMCT.getType());
		deviceName = selectedMCT.getPaoName();
		disabled = "disabled";
	}
	
	LiteStarsEnergyCompany member = null;
	if (savedReq.getProperty("Member") != null)
		member = StarsDatabaseCache.getInstance().getEnergyCompany(Integer.parseInt(savedReq.getProperty("Member")));
	if (member == null) member = liteEC;
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

function changeMember(form) {
	form.attributes["action"].value = "CreateMCT.jsp";
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
                <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
<% if (liteEC.getChildren().size() > 0 && AuthFuncs.checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MANAGE_MEMBERS)) { %>
                  <tr align="center"> 
                    <td colspan="2" valign="top" bgcolor="#FFFFFF" class="TableCell"> 
                      Member: 
                      <select name="Member" onChange="changeMember(this.form)">
                        <%
	ArrayList descendants = ECUtils.getAllDescendants(liteEC);
	for (int i = 0; i < descendants.size(); i++) {
		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
		String selected = company.equals(member)? "selected" : "";
%>
                        <option value="<%= company.getLiteID() %>" <%= selected %>><%= company.getName() %></option>
                        <%
	}
%>
                      </select>
                    </td>
                  </tr>
<% } %>
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
	int savedMCTType = 0;
	if (savedReq.getProperty("MCTType") != null)
		savedMCTType = Integer.parseInt(savedReq.getProperty("MCTType"));
	
	for (int i = 0; i < InventoryManagerUtil.MCT_TYPES.length; i++) {
		int mctType = PAOGroups.getDeviceType(InventoryManagerUtil.MCT_TYPES[i]);
		String selected = (mctType == savedMCTType)? "selected" : "";
%>
                                    <option value="<%= mctType %>" <%= selected %>><%= InventoryManagerUtil.MCT_TYPES[i] %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" width="88" class="SubtitleHeader">*Device 
                                  Name:</td>
                                <td width="210"> 
                                  <input type="text" name="DeviceName" size="24" onchange="setContentChanged(true)" <%= disabled %> value="<%= StarsUtils.forceNotNull(savedReq.getProperty("DeviceName")) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" width="88" class="SubtitleHeader">*Physical 
                                  Addr:</td>
                                <td width="210"> 
                                  <input type="text" name="PhysicalAddr" size="24" onblur="setDefaultMeterNumber(this.form)" onchange="setContentChanged(true)" <%= disabled %> value="<%= StarsUtils.forceNotNull(savedReq.getProperty("PhysicalAddr")) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" width="88" class="SubtitleHeader">*Meter 
                                  Number:</td>
                                <td width="210"> 
                                  <input type="text" name="MeterNumber" size="24" onchange="setContentChanged(true)" <%= disabled %> value="<%= StarsUtils.forceNotNull(savedReq.getProperty("MeterNumber")) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" width="88" class="SubtitleHeader">*Route:</td>
                                <td width="210"> 
                                  <select name="MCTRoute" onchange="setContentChanged(true)" <%= disabled %>>
                                    <%
	int savedMCTRoute = 0;
	if (savedReq.getProperty("MCTRoute") != null)
		savedMCTRoute = Integer.parseInt(savedReq.getProperty("MCTRoute"));
	
	LiteYukonPAObject[] routes = member.getAllRoutes();
	for (int i = 0; i < routes.length; i++) {
		if (routes[i].getType() == RouteTypes.ROUTE_CCU || routes[i].getType() == RouteTypes.ROUTE_MACRO) {
			String selected = (routes[i].getYukonID() == savedMCTRoute)? "selected" : "";
%>
                                    <option value="<%= routes[i].getYukonID() %>" <%= selected %>><%= routes[i].getPaoName() %></option>
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
                      <script language="JavaScript">setContentChanged(<%= request.getParameter("selected") != null %>);</script>
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td> <span class="SubtitleHeader"><br>
                            </span><span class="MainText">Or select from the list 
                            of all MCTs:</span><br>
<% if (selectedMCT != null) { %>
                            <br>
                            <table width="300" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                              <tr> 
                                <td align="right" width="88">Type:</td>
                                <td width="210"><%= deviceType %></td>
                              </tr>
                              <tr> 
                                <td align="right" width="88">Device Name:</td>
                                <td width="210"><%= deviceName %></td>
                              </tr>
                            </table>
                            <br>
                            <input type="button" name="ChangeMCT" value="Change" onClick="selectMCT(this.form)">
                            <input type="button" name="ClearMCT" value="Clear" onClick="location.href = 'CreateMCT.jsp?Reset'">
<% } else { %>
                            <p> 
                              <input type="button" name="SelectMCT" value="Select Meter" onClick="selectMCT(this.form)">
                            </p>
<% } %>
                          </td>
                        </tr>
                      </table>
                    </td>
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
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("DeviceLabel")) %>" onChange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Alt Tracking #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("AltTrackNo")) %>" onChange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Received:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("ReceiveDate")) %>" onChange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Voltage:</div>
                                </td>
                                <td width="210"> 
                                  <select name="Voltage" onChange="setContentChanged(true)">
                                    <%
	int savedVoltage = 0;
	if (savedReq.getProperty("Voltage") != null)
		savedVoltage = Integer.parseInt(savedReq.getProperty("Voltage"));
	
	YukonSelectionList voltageList = member.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
	for (int i = 0; i < voltageList.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) voltageList.getYukonListEntries().get(i);
		String selected = (entry.getEntryID() == savedVoltage)? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getEntryText() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr>
                                <td width="88" class="TableCell" align="right">Service 
                                  Company: </td>
                                <td width="210">
                                  <select name="ServiceCompany">
                                    <%
	int savedServiceCompany = 0;
	if (savedReq.getProperty("ServiceCompany") != null)
		savedServiceCompany = Integer.parseInt(savedReq.getProperty("ServiceCompany"));
	
	StarsServiceCompanies companyList = member.getStarsServiceCompanies();
	for (int i = 0; i < companyList.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany servCompany = companyList.getStarsServiceCompany(i);
		String selected = (servCompany.getCompanyID() == savedServiceCompany)? "selected" : "";
%>
                                    <option value="<%= servCompany.getCompanyID() %>" <%= selected %>><%= servCompany.getCompanyName() %></option>
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
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell" onChange="setContentChanged(true)"><%= StarsUtils.forceNotNull(savedReq.getProperty("Notes")) %></textarea>
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
                      <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
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
