<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.util.Pair" %>
<%@ page import="com.cannontech.database.cache.functions.YukonListFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsLMHardware" %>
<%@ page import="com.cannontech.stars.util.ECUtils" %>
<%@ page import="com.cannontech.stars.web.servlet.InventoryManager" %>
<%
	String trackHwAddr = liteEC.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
	boolean useHardwareAddressing = Boolean.valueOf(trackHwAddr).booleanValue();
	
	int deviceTypeID = 0;
	StarsLMConfiguration configuration = null;
	
	ArrayList invToConfig = (ArrayList) session.getAttribute(InventoryManager.INVENTORY_TO_CONFIG);
	if (invToConfig == null) {
		invToConfig = new ArrayList();
		session.setAttribute(InventoryManager.INVENTORY_TO_CONFIG, invToConfig);
	}
	else if (invToConfig.size() > 0) {
		if (invToConfig.get(0) instanceof Pair)
			deviceTypeID = ((Integer) ((Pair)invToConfig.get(0)).getFirst()).intValue();
		else
			deviceTypeID = ((LiteStarsLMHardware)invToConfig.get(0)).getLmHardwareTypeID();
	}
	
	if (request.getParameter("AddRange") != null) {
		Integer newDevTypeID = Integer.valueOf(request.getParameter("DeviceType"));
		if (deviceTypeID > 0 && newDevTypeID.intValue() != deviceTypeID) {
			errorMsg = "The device type you tried to add is not the same as those already added. You can only configure hardwares with the same device type at a time.";
		}
		else {
			try {
				Integer[] snRange = new Integer[2];
				snRange[0] = Integer.valueOf(request.getParameter("From"));
				snRange[1] = Integer.valueOf(request.getParameter("To"));
				
				boolean foundRange = false;
				for (int i = 0; i < invToConfig.size(); i++) {
					if (invToConfig.get(i) instanceof Pair) {
						Integer[] range = (Integer[]) ((Pair)invToConfig.get(i)).getSecond();
						if (range[0].intValue() == snRange[0].intValue() && range[1].intValue() == snRange[1].intValue()) {
							foundRange = true;
							break;
						}
					}
				}
				
				if (!foundRange) invToConfig.add(new Pair(newDevTypeID, snRange));
			}
			catch (NumberFormatException e) {
				errorMsg = "Invalid number format in the SN range";
			}
		}
	}
	else if (request.getParameter("Add") != null) {
		LiteStarsLMHardware liteHw = (LiteStarsLMHardware) session.getAttribute(InventoryManager.INVENTORY_TO_CHECK);
		if (liteHw != null) {
			session.removeAttribute(InventoryManager.INVENTORY_TO_CHECK);
			if (deviceTypeID > 0 && liteHw.getLmHardwareTypeID() != deviceTypeID) {
				errorMsg = "The device type you tried to add is not the same as those already added. You can only configure hardwares with the same device type at a time.";
			}
			else {
				if (!invToConfig.contains(liteHw)) invToConfig.add(liteHw);
			}
		}
	}
	else if (request.getParameter("Remove") != null) {
		try {
			invToConfig.remove( Integer.parseInt(request.getParameter("Remove")) );
		}
		catch (IndexOutOfBoundsException e) {}
	}
	else if (request.getParameter("RemoveAll") != null) {
		invToConfig.clear();
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="Javascript">
function addConfigRange(form) {
	if (form.From.value == "" || form.To.value == "") {
		alert("The 'From' and 'To' field cannot be empty");
		return;
	}
	form.attributes["action"].value = "ConfigSN.jsp?AddRange";
	form.submit();
}

function selectFromInventory(form) {
	form.attributes["action"].value = "SelectInv.jsp";
	form.REDIRECT.value = "<%= request.getRequestURI() %>?Add";
	form.submit();
}

function removeConfig(form, idx) {
	if (!confirm("Are you sure you want to remove this configuration entry?"))
		return;
	form.attributes["action"].value = "ConfigSN.jsp?Remove=" + idx;
	form.submit();
}

function removeAllConfig(form) {
	if (!confirm("Are you sure you want to remove all configuration entries?"))
		return;
	form.attributes["action"].value = "ConfigSN.jsp?RemoveAll";
	form.submit();
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
            <% String pageName = "ConfigSN.jsp"; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "CONFIGURE SERIAL NUMBER RANGE"; %>
              <%@ include file="include/SearchBar.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  
              <table width="80%" border="0" cellspacing="0" cellpadding="0" class="MainText">
                <tr> 
                  <td align="center">Add serial numbers to be configured by range 
                    (Add Range),<br>
                    or by selecting individual devices from the current inventory 
                    (Select From Inventory).</td>
                </tr>
              </table>
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager">
			    <input type="hidden" name="action" value="ConfigSNRange">
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                <table width="64%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                  <tr>
                    <td align="center"> 
                      <table width="100%" border="1" cellspacing="0" cellpadding="3" bgcolor="CCCCCC">
                        <tr> 
                          <td align="center"> 
                            <table width="100%" border="0" class="TableCell2">
                              <tr> 
                                <td width="25%"> 
                                  <div align="right">Range:</div>
                                </td>
                                <td width="75%"> 
                                  <input type="text" name="From" size="10">
                                  &nbsp;to&nbsp; 
                                  <input type="text" name="To" size="10">
                                </td>
                              </tr>
                              <tr> 
                                <td width="25%"> 
                                  <div align="right">Device Type:</div>
                                </td>
                                <td width="75%"> 
                                  <select name="DeviceType">
                                    <%
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) continue;
%>
                                    <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                            </table>
                            <input type="button" name="AddConfigRange" value="Add Range" onClick="addConfigRange(this.form)">
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr>
                    <td align="center" height="20">or</td>
                  </tr>
                  <tr>
                    <td align="center">
                      <table width="100%" border="1" cellspacing="0" cellpadding="3" bgcolor="CCCCCC">
                        <tr>
                          <td align="center"> 
                            <input type="button" name="AddFromInventory" value="Select From Inventory" onClick="selectFromInventory(this.form)">
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <p> 
                <table width="64%" border="1" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td class="MainText" bgcolor="#CCCCCC"><b>Serial Numbers to 
                      be Configured</b></td>
                  </tr>
                  <tr> 
                    <td align="center"> 
                      <table width="400" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td class="SubtitleHeader" width="128">Device Type</td>
                          <td class="SubtitleHeader" width="158">Serial # (Range)</td>
                          <td class="SubtitleHeader" width="100">&nbsp;</td>
                        </tr>
                        <tr> 
                          <td class="TableCell" colspan="3"> 
                            <hr>
                          </td>
                        </tr>
<%
	for (int i = 0; i < invToConfig.size(); i++) {
		String serialNo = null;
		if (invToConfig.get(i) instanceof Pair) {
			deviceTypeID = ((Integer) ((Pair)invToConfig.get(i)).getFirst()).intValue();
			Integer[] snRange = (Integer[]) ((Pair)invToConfig.get(i)).getSecond();
			serialNo = snRange[0].toString() + " to " + snRange[1].toString();
		}
		else {
			deviceTypeID = ((LiteStarsLMHardware)invToConfig.get(i)).getLmHardwareTypeID();
			serialNo = ((LiteStarsLMHardware)invToConfig.get(i)).getManufacturerSerialNumber();
		}
		String deviceType = YukonListFuncs.getYukonListEntry(deviceTypeID).getEntryText();
%>
                        <tr> 
                          <td class="TableCell" width="128"><%= deviceType %></td>
                          <td class="TableCell" width="158"><%= serialNo %></td>
                          <td class="TableCell" width="100"> 
                            <input type="button" name="Submit3" value="Remove" onClick="removeConfig(this.form, <%= i %>)">
                          </td>
                        </tr>
<%
	}
%>
                      </table>
                      <br>
<%
	if (invToConfig.size() > 0) {
%>
                      <input type="button" name="RemoveAllConfig" value="Remove All" onClick="removeAllConfig(this.form)">
<%
	}
%>
                    </td>
                  </tr>
                </table>
<%
	if (invToConfig.size() > 0) {
%>
                <p> 
                <table width="64%" border="1" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td class="MainText" bgcolor="#CCCCCC"><b>Hardware Configuration</b></td>
                  </tr>
                  <tr> 
                    <td align="center" class="TableCell"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                        <tr> 
                          <td> 
                            <input type="radio" name="UseRoute" value="false" checked onclick="this.form.Route.disabled = true">
                            Use routes assigned to the switches.</td>
                        </tr>
                        <tr> 
                          <td> 
                            <input type="radio" name="UseRoute" value="true" onclick="this.form.Route.disabled = false">
                            Specify a route: 
                            <select name="Route" disabled>
                              <option value="0">(Default Route)</option>
<%
	LiteYukonPAObject[] routes = liteEC.getAllRoutes();
	for (int i = 0; i < routes.length; i++) {
%>
                              <option value="<%= routes[i].getYukonID() %>"><%= routes[i].getPaoName() %></option>
<%
	}
%>
                            </select>
                          </td>
                        </tr>
                      </table>
                      <hr>
                      <table width="300" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                        <tr> 
                          <td> 
                            <input type="radio" name="UseConfig" value="false" checked onClick="document.getElementById('Configuration').style.display = 'none'">
                            Use configuration stored in the switches.</td>
                        </tr>
                        <tr> 
                          <td> 
                            <input type="radio" name="UseConfig" value="true" onClick="document.getElementById('Configuration').style.display = ''">
                            Specify a new configuration:</td>
                        </tr>
                      </table>
                      <div id="Configuration" style="display:none"> 
<%
		if (useHardwareAddressing) {
%>
                        <input type="hidden" name="UseHardwareAddressing" value="true">
                        <table border="0" cellspacing="0" cellpadding="10">
                          <tr> 
                            <td align="center" valign="top"> 
                              <%@ include file="../../include/hwconfig_addressing.jsp" %>
                            </td>
                            <td align="center" valign="top"> 
                              <%@ include file="../../include/hwconfig_relays.jsp" %>
                            </td>
                          </tr>
                        </table>
<%
		}
		else {
			TreeMap map = new TreeMap();
			for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
				for (int j = 0; j < categories.getStarsApplianceCategory(i).getStarsEnrLMProgramCount(); j++) {
					StarsEnrLMProgram program = categories.getStarsApplianceCategory(i).getStarsEnrLMProgram(j);
					for (int k = 0; k < program.getAddressingGroupCount(); k++) {
						AddressingGroup group = program.getAddressingGroup(k);
						if (group.getEntryID() > 0) map.put(group.getContent(), group);
					}
				}
			}
%>
                        Group: 
                        <select name="Group">
<%
			Iterator it = map.values().iterator();
			while (it.hasNext()) {
				AddressingGroup group = (AddressingGroup) it.next();
%>
                          <option value="<%= group.getEntryID() %>"><%= group.getContent() %></option>
<%
			}
%>
                        </select>
<%
		}
%>
                      </div>
                    </td>
                  </tr>
                </table>
<p>
                <table width="64%" border="0" cellspacing="0" cellpadding="3" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td> 
                      <div align="right"> 
                        <input type="submit" name="ConfigNow" value="Configure Now">
                      </div>
                    </td>
                    <td> 
                      <div align="left"> 
                        <input type="submit" name="ScheduleConfig" value="Save To Batch">
                      </div>
                    </td>
                  </tr>
                </table>
<%
	}
%>
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
