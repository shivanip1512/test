<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.*" %>
<%
	if (request.getParameter("Init") != null) {
		// The "Create Hardware" link in the nav is clicked
		session.removeAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
		session.removeAttribute(InventoryManagerUtil.HARDWARE_ADDRESSING);
	}
	else if (request.getParameter("InvID") != null) {
		// Request from InventoryDetail.jsp to copy a hardware device
		LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteEC.getInventory(Integer.parseInt(request.getParameter("InvID")), true);
		Properties savedReq = new Properties();
		savedReq.setProperty("DeviceType", String.valueOf(liteHw.getLmHardwareTypeID()));
		savedReq.setProperty("DeviceLabel", liteHw.getDeviceLabel());
		savedReq.setProperty("ReceiveDate", ServletUtils.formatDate(new Date(liteHw.getReceiveDate()), datePart));
		savedReq.setProperty("Voltage", String.valueOf(liteHw.getVoltageID()));
		savedReq.setProperty("ServiceCompany", String.valueOf(liteHw.getInstallationCompanyID()));
		savedReq.setProperty("Notes", liteHw.getNotes().replaceAll("<br>", System.getProperty("line.separator")));
		savedReq.setProperty("Route", String.valueOf(liteHw.getRouteID()));
		session.setAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST, savedReq);
		
		StarsInventory inventory = StarsLiteFactory.createStarsInventory(liteHw, liteEC);
		if (inventory.getLMHardware().getStarsLMConfiguration() != null)
			session.setAttribute(InventoryManagerUtil.HARDWARE_ADDRESSING, inventory.getLMHardware().getStarsLMConfiguration());
	}
	else if (request.getParameter("Member") != null || request.getParameter("DeviceType") != null) {
		// Request from the same page while member selection or device type selection has been changed
		ServletUtils.saveRequest(request, session, new String[] {"Member", "DeviceType", "SerialNo", "DeviceLabel", "AltTrackNo", "ReceiveDate", "Voltage", "ServiceCompany", "Notes", "Route"});
		if (request.getParameter("UseHardwareAddressing") != null) {
			StarsLMConfiguration configuration = new StarsLMConfiguration();
			InventoryManagerUtil.setStarsLMConfiguration(configuration, request);
			session.setAttribute(InventoryManagerUtil.HARDWARE_ADDRESSING, configuration);
		}
	}
	
	Properties savedReq = (Properties) session.getAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	if (savedReq == null) savedReq = new Properties();
	
	StarsLMConfiguration configuration = (StarsLMConfiguration) session.getAttribute(InventoryManagerUtil.HARDWARE_ADDRESSING);
	
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
function changeMember(form) {
	form.attributes["action"].value = "CreateHardware.jsp";
	form.submit();
}

function changeDeviceType(form) {
	form.attributes["action"].value = "CreateHardware.jsp";
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
		    <% String pageName = "CreateHardware.jsp"; %>
			<%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "CREATE NEW HARDWARE"; %>
              <%@ include file="include/SearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager" onsubmit="return validate(this)">
			    <input type="hidden" name="action" value="CreateHardware">
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
                          <td><span class="SubtitleHeader">DEVICE</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="SubtitleHeader"> 
                                  <div align="right">*Type:</div>
                                </td>
                                <td width="210" class="MainText"> 
                                  <select name="DeviceType" onchange="<% if (configuration != null) { %>changeDeviceType(this.form)<% } else { %>setContentChanged(true)<% } %>">
                                    <%
	int savedDeviceType = 0;
	if (savedReq.getProperty("DeviceType") != null)
		savedDeviceType = Integer.parseInt(savedReq.getProperty("DeviceType"));
	
	YukonSelectionList devTypeList = member.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < devTypeList.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) devTypeList.getYukonListEntries().get(i);
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) continue;
		String selected = (entry.getEntryID() == savedDeviceType)? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getEntryText() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="SubtitleHeader"> 
                                  <div align="right">*Serial #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="SerialNo" maxlength="30" size="24" onchange="setContentChanged(true)" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("SerialNo")) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Label:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("DeviceLabel")) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Alt Tracking #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" onchange="setContentChanged(true)" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("AltTrackNo")) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Received:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("ReceiveDate")) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Voltage:</div>
                                </td>
                                <td width="210"> 
                                  <select name="Voltage" onchange="setContentChanged(true)">
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
                                <td width="88" class="TableCell"> 
                                  <div align="right">Service Company:</div>
                                </td>
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
                                <td width="210"> <b>
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= StarsUtils.forceNotNull(savedReq.getProperty("Notes")) %></textarea>
                                  </b> </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td valign="top"><span class="SubtitleHeader">CONFIGURATION</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Route: </div>
                                </td>
                                <td width="210"> 
                                  <select name="Route" onchange="setContentChanged(true)">
                                    <%
	String dftRoute = PAOFuncs.getYukonPAOName(member.getDefaultRouteID());
	if (dftRoute != null)
		dftRoute = "Default - " + dftRoute;
	else
		dftRoute = "Default - (None)";
%>
                                    <option value="0"><%= dftRoute %></option>
                                    <%
	int savedRoute = 0;
	if (savedReq.getProperty("Route") != null)
		savedRoute = Integer.parseInt(savedReq.getProperty("Route"));
	
	LiteYukonPAObject[] routes = member.getAllRoutes();
	for (int i = 0; i < routes.length; i++) {
		String selected = (routes[i].getYukonID() == savedRoute)? "selected" : "";
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
                    </td>
                  </tr>
<%
	String trackHwAddr = member.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
	if (trackHwAddr != null && Boolean.valueOf(trackHwAddr).booleanValue() && configuration != null) {
		// Hide the hardware addressing tables if the protocol of the new device type is different from the saved protocol
		int hwConfigType = InventoryUtils.getHardwareConfigType(Integer.parseInt(savedReq.getProperty("DeviceType")));
		if (configuration.getExpressCom() != null && hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM
			|| configuration.getVersaCom() != null && hwConfigType == InventoryUtils.HW_CONFIG_TYPE_VERSACOM
			|| configuration.getSA205() != null && hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205
			|| configuration.getSA305() != null && hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305
			|| configuration.getSASimple() != null && hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA_SIMPLE)
		{
%>
                  <input type="hidden" name="UseHardwareAddressing" value="true">
                  <tr> 
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td> <span class="SubtitleHeader">ADDRESSING</span> 
                            <hr>
                            <%@ include file="../../include/hwconfig_addressing.jsp" %>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td> <span class="SubtitleHeader">RELAYS</span> 
                            <hr>
                            <%@ include file="../../include/hwconfig_relays.jsp" %>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
<%
		}
	}
%>
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
