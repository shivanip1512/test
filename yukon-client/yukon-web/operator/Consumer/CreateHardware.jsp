<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonListEntry" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.YukonListFuncs" %>
<%@ page import="com.cannontech.database.data.pao.PAOGroups" %>
<%@ page import="com.cannontech.database.data.pao.RouteTypes" %>
<%@ page import="com.cannontech.stars.web.util.InventoryManagerUtil" %>
<%
	boolean inWizard = request.getParameter("Wizard") != null;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	boolean invChecking = AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.INVENTORY_CHECKING);

	StarsInventory inventory = (StarsInventory) session.getAttribute(InventoryManagerUtil.STARS_INVENTORY_TEMP);
	boolean hasPrevStep = false;
	
	if (inWizard) {
		MultiAction actions = (MultiAction) session.getAttribute(ServletUtils.ATT_NEW_ACCOUNT_WIZARD);
		if (actions != null) {
			if (inventory == null) {
				SOAPMessage reqMsg = actions.getRequestMessage( CreateLMHardwareAction.class );
				if (reqMsg != null) {
					StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation(reqMsg);
					StarsCreateLMHardware createHw = reqOper.getStarsCreateLMHardware();
					if (createHw != null)
						inventory = (StarsInventory) StarsFactory.newStarsInv(createHw, StarsInventory.class);
				}
			}
			
			hasPrevStep = actions.getRequestMessage( NewCustAccountAction.class ) != null;
		}
	}
	
	if (inventory == null) {
		if (invChecking) {
			String redirect = request.getContextPath() + "/operator/Consumer/SerialNumber.jsp?action=New";
			if (inWizard) redirect += "&Wizard=true";
			response.sendRedirect(redirect);
			return;
		}
		
		inventory = (StarsInventory) StarsFactory.newStarsInv(StarsInventory.class);
		LMHardware hw = new LMHardware();
		hw.setManufacturerSerialNumber("");
		inventory.setLMHardware(hw);
	}
	
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
			deviceType = com.cannontech.database.data.pao.PAOGroups.getPAOTypeString(litePao.getType());
			serialName = litePao.getPaoName();
		}
		else if (inventory.getMCT() != null) {
			deviceType = YukonListFuncs.getYukonListEntry(inventory.getDeviceType().getEntryID()).getEntryText();
			serialName = inventory.getMCT().getDeviceName();
		}
		
		serialNameLabel = "Device Name";
		serialNameVar = "DeviceName";
	}
	
	String invNo = request.getParameter("InvNo");
	StarsInventory origInv = null;
	if (invNo != null) origInv = inventories.getStarsInventory(Integer.parseInt(invNo));
	
	YukonListEntry devTypeMCT = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT);
	
	Properties savedReq = null;
	if (request.getParameter("failed") != null)
		savedReq = (Properties) session.getAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	else
		session.removeAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	if (savedReq == null) savedReq = new Properties();
	
	String savedDeviceLabel = savedReq.getProperty("DeviceLabel");
	String savedAltTrackNo = savedReq.getProperty("AltTrackNo");
	String savedRecvDate = savedReq.getProperty("ReceiveDate");
	String savedRemvDate = savedReq.getProperty("RemoveDate");
	String savedNotes = savedReq.getProperty("Notes");
	String savedInstDate = savedReq.getProperty("InstallDate");
	String savedInstNotes = savedReq.getProperty("InstallNotes");
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

function validate(form) {
<% if (!invChecking) { %>
	if (document.getElementById("HardwareDiv").style.display == "") {
		if (form.SerialNo.value == "") {
			alert("Serial # cannot be empty!");
			return false;
		}
	}
	else {
		if (form.DeviceName.value == "") {
			alert("Device name cannot be empty!");
			return false;
		}
	}
	form.attributes["action"].value = "<%= request.getContextPath() %>/servlet/InventoryManager";
<% } %>
	return true;
}

function changeSerialNo() {
	document.snForm.submit();
}

function createMCT(form) {
	if (form.CreateMCT.checked) {
		form.MCTType.disabled = false;
		form.PhysicalAddr.disabled = false;
		form.MeterNumber.disabled = false;
		form.MCTRoute.disabled = false;
		document.getElementById("DeviceDiv2").style.display = "";
	}
	else {
		document.getElementById("DeviceDiv2").style.display = "none";
		form.MCTType.disabled = true;
		form.PhysicalAddr.disabled = true;
		form.MeterNumber.disabled = true;
		form.MCTRoute.disabled = true;
	}
}

function changeDeviceType(form) {
<% if (devTypeMCT != null) { %>
	if (form.DeviceType.value == <%= devTypeMCT.getEntryID() %>) {
		if (document.getElementById("HardwareDiv") != null)
			document.getElementById("HardwareDiv").style.display = "none";
		if (document.getElementById("HardwareDiv2") != null)
			document.getElementById("HardwareDiv2").style.display = "none";
		if (document.getElementById("DeviceDiv") != null)
			document.getElementById("DeviceDiv").style.display = "";
		if (document.getElementById("DeviceDiv2") != null) {
			if (form.CreateMCT == null)
				document.getElementById("DeviceDiv2").style.display = "";
			else
				createMCT(form);
		}
	}
	else {
		if (document.getElementById("HardwareDiv") != null)
			document.getElementById("HardwareDiv").style.display = "";
		if (document.getElementById("HardwareDiv2") != null)
			document.getElementById("HardwareDiv2").style.display = "";
		if (document.getElementById("DeviceDiv") != null)
			document.getElementById("DeviceDiv").style.display = "none";
		if (document.getElementById("DeviceDiv2") != null)
			document.getElementById("DeviceDiv2").style.display = "none";
	}
<% } %>
}

function init() {
	changeDeviceType(document.MForm);
}

function confirmCancel() {
	if (confirm("Are you sure you want to quit from this wizard and discard all changes you've been made?"))
		location.href = "../Operations.jsp";
}
</script>
</head>
<body class="Background" leftmargin="0" topmargin="0" onload="init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <%@ include file="include/HeaderBar.jsp" %>
      <script language="JavaScript">setContentChanged(<%= inWizard || invChecking %>);</script>
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
<% if (!inWizard) { %>
		    <% String pageName = (invNo == null)? "CreateHardware.jsp" : "Inventory.jsp?InvNo=" + invNo; %>
			<%@ include file="include/Nav.jsp" %>
<% } %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "TableCell" align="center"> 
              <% String header = "CREATE NEW HARDWARE"; %>
              <% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jsp" %>
              <% } else { %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
              <% } %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
<% if (invNo == null) { %>
			    <input type="hidden" name="action" value="CreateLMHardware">
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?failed">
<% } else { %>
			    <input type="hidden" name="action" value="UpdateLMHardware">
				<input type="hidden" name="OrigInvID" value="<%= origInv.getInventoryID() %>">
				<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/Inventory.jsp?InvNo=<%= invNo %>">
				<input type="hidden" name="REFERRER" value="<%= request.getContextPath() %>/operator/Consumer/Inventory.jsp?InvNo=<%= invNo %>">
<% } %>
				<input type="hidden" name="InvID" value="<%= inventory.getInventoryID() %>">
				<input type="hidden" name="DeviceID" value="<%= inventory.getDeviceID() %>">
<% if (inWizard) { %>
				<input type="hidden" name="REDIRECT2" value="<%= request.getContextPath() %>/operator/Consumer/Programs.jsp?Wizard=true">
				<input type="hidden" name="Wizard" value="true">
<% } %>
                <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                  <tr> 
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td valign="top"><span class="SubtitleHeader">DEVICE</span> 
                            <hr>
<% if (invChecking) { %>
							<input type="hidden" name="DeviceType" value="<%= inventory.getDeviceType().getEntryID() %>">
							<input type="hidden" name="<%= serialNameVar %>" value="<%= serialName %>">
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center" class="TableCell">
                              <tr> 
                                <td width="100" align="right">Type: </td>
                                <td width="120" class="MainText"><%= deviceType %></td>
                                <td width="80" rowspan="2">
<%  if (inWizard) { %>
                                  <input type="button" name="Change" value="Change" onclick="changeSerialNo()">
<%  } %>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" align="right"><%= serialNameLabel %>: 
                                </td>
                                <td width="120" class="MainText"><%= serialName %></td>
                              </tr>
                            </table>
<% } else { %>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center" class="TableCell">
                              <tr> 
                                <td width="100" class="SubtitleHeader"> 
                                  <div align="right">*Type: </div>
                                </td>
                                <td width="200"> 
                                  <select name="DeviceType" onchange="changeDeviceType(this.form);setContentChanged(true);">
                                    <%
	int savedDeviceType = inventory.getDeviceType().getEntryID();
	if (savedReq.getProperty("DeviceType") != null)
		savedDeviceType = Integer.parseInt(savedReq.getProperty("DeviceType"));
	
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == savedDeviceType)? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                            </table>
							<div id="HardwareDiv">
                              <table width="300" border="0" cellspacing="0" cellpadding="1" align="center" class="TableCell">
                                <tr> 
                                  <td width="100" align="right" class="SubtitleHeader">*Serial #: </td>
                                <td width="200"> 
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("SerialNo")) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
							</table>
							</div>
							<div id="DeviceDiv" style="display:none">
                              <table width="300" border="0" cellspacing="0" cellpadding="1" align="center" class="TableCell">
                                <tr> 
                                  <td width="100" align="right" class="SubtitleHeader">*Device Name: </td>
                                  <td width="200"> 
                                    <input type="text" name="DeviceName" maxlength="30" size="24" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("DeviceName")) %>" onchange="setContentChanged(true)">
                                  </td>
                                </tr>
                                <tr>
                                  <td width="100" align="right">&nbsp; </td>
                                  <td width="200">
                                    <input type="checkbox" name="CreateMCT" value="true" onclick="createMCT(this.form)" <% if (savedReq.getProperty("CreateMCT") != null) out.print("checked"); %>>
                                    Create new device</td>
                                </tr>
                              </table>
							</div>
<% } %>
<% if (inventory.getDeviceID() == 0) { %>
							<div id="DeviceDiv2" style="display:none">
                              <table width="300" border="0" cellspacing="0" cellpadding="1" align="center" class="TableCell">
                                <tr> 
                                  <td width="100" align="right" class="SubtitleHeader">*MCT Type: </td>
                                  <td width="200"> 
                                    <select name="MCTType" onchange="setContentChanged(true)">
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
                                  <td width="100" align="right" class="SubtitleHeader">*Physical Addr: </td>
                                  <td width="200"> 
                                    <input type="text" name="PhysicalAddr" maxlength="30" size="24" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("PhysicalAddr")) %>" onblur="setDefaultMeterNumber(this.form)" onchange="setContentChanged(true)">
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="100" align="right" class="SubtitleHeader">*Meter Number: </td>
                                  <td width="200"> 
                                    <input type="text" name="MeterNumber" maxlength="30" size="24" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("MeterNumber")) %>" onchange="setContentChanged(true)">
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="100" align="right" class="SubtitleHeader">*Route: </td>
                                  <td width="200"> 
                                    <select name="MCTRoute" onChange="setContentChanged(true)">
                                      <%
	int savedRoute = 0;
	if (savedReq.getProperty("MCTRoute") != null)
		savedRoute = Integer.parseInt(savedReq.getProperty("MCTRoute"));
	
	LiteYukonPAObject[] mctRoutes = liteEC.getAllRoutes();
	for (int i = 0; i < mctRoutes.length; i++) {
		if (mctRoutes[i].getType() == RouteTypes.ROUTE_CCU || mctRoutes[i].getType() == RouteTypes.ROUTE_MACRO) {
			String selected = (mctRoutes[i].getYukonID() == savedRoute)? "selected" : "";
%>
                                      <option value="<%= mctRoutes[i].getYukonID() %>" <%= selected %>><%= mctRoutes[i].getPaoName() %></option>
                                      <%
		}
	}
%>
                                    </select>
                                  </td>
                                </tr>
                                <tr>
                                  <td width="100" align="right">&nbsp;</td>
                                  <td width="200">&nbsp;</td>
                                </tr>
                              </table>
							</div>
<% } %>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Label: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= (savedDeviceLabel != null)? savedDeviceLabel : inventory.getDeviceLabel() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Alt Tracking #: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= (savedAltTrackNo != null)? savedAltTrackNo : inventory.getAltTrackingNumber() %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Receive Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= (savedRecvDate != null)? savedRecvDate : ServletUtils.formatDate(inventory.getReceiveDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Remove Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= (savedRemvDate != null)? savedRemvDate : ServletUtils.formatDate(inventory.getRemoveDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Voltage: </div>
                                </td>
                                <td width="200"> 
                                  <select name="Voltage" onchange="setContentChanged(true)">
                                    <%
	int savedVoltage = inventory.getVoltage().getEntryID();
	if (savedReq.getProperty("Voltage") != null)
		savedVoltage = Integer.parseInt(savedReq.getProperty("Voltage"));
	
	StarsCustSelectionList voltageList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
	for (int i = 0; i < voltageList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = voltageList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == savedVoltage)? "selected" : "";
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
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= (savedNotes != null)? savedNotes : inventory.getNotes() %></textarea>
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
                                  <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= (savedInstDate != null)? savedInstDate : ServletUtils.formatDate(inventory.getInstallDate(), datePart) %>" onchange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Service Company: </div>
                                </td>
                                <td width="200"> 
                                  <select name="ServiceCompany" onchange="setContentChanged(true)">
<%
	int savedServComp = inventory.getInstallationCompany().getEntryID();
	if (savedReq.getProperty("ServiceCompany") != null)
		savedServComp = Integer.parseInt(savedReq.getProperty("ServiceCompany"));
	
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany company = companies.getStarsServiceCompany(i);
		String selected = (company.getCompanyID() == savedServComp)? "selected" : "";
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
                                  <textarea name="InstallNotes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= (savedInstNotes != null)? savedInstNotes : inventory.getInstallationNotes() %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<% if (inventory.getLMHardware() != null) { %>
                      <div id="HardwareDiv2">
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
                                    <option value="0">(Default Route)</option>
<%
	int savedRoute = inventory.getLMHardware().getRouteID();
	if (savedReq.getProperty("Route") != null)
		savedRoute = Integer.parseInt(savedReq.getProperty("Route"));
	
	LiteYukonPAObject[] routes = liteEC.getAllRoutes();
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
					  </div>
<% } %>
                    </td>
                  </tr>
                </table>
                <br>
<% if (inWizard) { %>
                <table width="400" border="0" cellpadding="5" cellspacing="0">
                  <tr>
                    <td width="35%" align="right"> 
                      <input type="submit" name="Submit" value="Next">
                    </td>
                    <td width="15%" align="center"> 
                      <input type="submit" name="Done" value="Done">
                    </td>
                    <td width="15%" align="center"> 
                      <input type="button" name="Back" value="Back" onclick="location.href = 'New.jsp?Wizard=true'" <% if (!hasPrevStep) { %>disabled<% } %>>
                    </td>
                    <td width="35%" align="left"> 
                      <input type="button" name="Cancel" value="Cancel" onclick="confirmCancel()">
                    </td>
                  </tr>
                </table>
<% } else { %>
                <table width="400" border="0" cellpadding="5" cellspacing="0">
                  <tr>
                    <td width="50%" align="right"> 
                      <input type="submit" name="Submit" value="Save">
                    </td>
                    <td width="50%" align="left"> 
<%  if (invChecking) { %>
                      <input type="button" name="Cancel" value="Cancel" onclick="location.href = 'SerialNumber.jsp<% if (invNo != null) out.print("?InvNo=" + invNo); %>'">
<%  } else { %>
                      <input type="button" name="Cancel" value="Reset" onclick="location.reload()">
<%  } %>
                    </td>
                  </tr>
                </table>
<% } %>
			    <br>
              </form>
              <form name="snForm" method="post" action="SerialNumber.jsp">
                <input type="hidden" name="action" value="Change">
              </form>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../WebConfig/yukon/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
