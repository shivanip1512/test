<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	String action = request.getParameter("action");
	String referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER);
	String redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
	
	String invNo = request.getParameter("InvNo");
	if (invNo == null) invNo = "";
	session.setAttribute(InventoryManager.STARS_INVENTORY_NO, invNo);
	
	StarsCustListEntry devTypeMCT = ServletUtils.getStarsCustListEntry(selectionListTable, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_METER);
	
	int deviceType = 0;
	String valStr = "";
	
	if (action != null) {
		if (action.equalsIgnoreCase("New")) {
			// Came from the nav link, next page is CreateHardware.jsp
			session.removeAttribute(InventoryManager.STARS_INVENTORY_TEMP);
			
			referer = request.getContextPath() + "/operator/Consumer/CreateHardware.jsp";
			if (request.getParameter("Wizard") != null) referer += "?Wizard=true";
		}
		else if (action.equalsIgnoreCase("Change")) {
			// Came from CreateHardware.jsp or Inventory.jsp
			StarsInventory inventory = (StarsInventory) session.getAttribute(InventoryManager.STARS_INVENTORY_TEMP + invNo);
			
			if (inventory != null) {
				if (inventory instanceof StarsLMHardware) {
					deviceType = ((StarsLMHardware)inventory).getLMDeviceType().getEntryID();
					valStr = ((StarsLMHardware)inventory).getManufactureSerialNumber();
				}
				else if (inventory instanceof StarsMCT) {
					deviceType = devTypeMCT.getEntryID();
					valStr = ((StarsMCT)inventory).getDeviceName();
				}
			}
			
			referer = request.getHeader("referer");
		}
		
		redirect = referer;
		if (redirect.indexOf("Consumer/Inventory.jsp") >= 0 && redirect.indexOf("Changed=true") < 0)
			redirect += "&Changed=true";
		
		session.setAttribute(ServletUtils.ATT_REFERRER, referer);
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
	}
	else {
		// From SelectInv.jsp when cancel button is clicked
		StarsInventory inventory = (StarsInventory) session.getAttribute(InventoryManager.STARS_INVENTORY_TEMP + invNo);
		
		if (inventory != null) {
			if (inventory instanceof StarsLMHardware) {
				deviceType = ((StarsLMHardware)inventory).getLMDeviceType().getEntryID();
				valStr = ((StarsLMHardware)inventory).getManufactureSerialNumber();
			}
			else if (inventory instanceof StarsMCT) {
				deviceType = devTypeMCT.getEntryID();
				valStr = ((StarsMCT)inventory).getDeviceName();
			}
		}
	}
	
	boolean inWizard = referer.indexOf("Wizard=true") >= 0;
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function validate(form) {
	if (form.SerialNo.value == "") {
		alert(document.getElementById("NameLabel").innerText + " cannot be empty!");
		return false;
	}
	return true;
}

function selectInventory(form) {
	form.attributes["action"].value = "../Hardware/SelectInv.jsp";
	form.submit();
}

function selectMeter(form) {
	form.attributes["action"].value = "../Hardware/SelectMeter.jsp";
	form.submit();
}

function changeDeviceType() {
<% if (devTypeMCT != null) { %>
	if (document.MForm.DeviceType.value == <%= devTypeMCT.getEntryID() %>)
		document.getElementById("NameLabel").innerText = "Device Name";
	else
<% } %>
		document.getElementById("NameLabel").innerText = "Serial #";
}
</script>
</head>
<body class="Background" leftmargin="0" topmargin="0" onload="changeDeviceType()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
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
          <td width="1" height="102" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
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
<%	if (inWizard) out.print("&nbsp;");
	else { %>
		    <% String pageName = referer.substring(referer.lastIndexOf('/') + 1); %>
			<%@ include file="include/Nav.jsp" %>
<%	} %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "MainText" align="center">
              <% String header = "INVENTORY CHECKING"; %>
              <%@ include file="include/InfoSearchBar.jsp" %>
			  
			  <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager">
			    <input type="hidden" name="action" value="CheckInventory">
				<input type="hidden" name="REDIRECT" value="<%= redirect %>">
                <table width="480" border="0" cellspacing="0" cellpadding="3" class="MainText">
                  <tr> 
                    <td width="322">Please select from the inventory:</td>
                    <td width="146"> 
                      <input type="button" name="SelectInv" value="Select Inventory" onclick="selectInventory(this.form)">
                    </td>
                  </tr>
<% if (devTypeMCT != null) { %>
                  <tr> 
                    <td width="322">Or select from the list of all meters:</td>
                    <td width="146"> 
                      <input type="button" name="SelectMCT" value="Select Meter" onclick="selectMeter(this.form)">
                    </td>
                  </tr>
<% } %>
                  <tr> 
                    <td colspan="2">
                      <p>&nbsp;</p>
                    </td>
                  </tr>
                  <tr> 
                    <td width="322"> 
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td width="15%" class="MainText">Or enter</td>
                          <td width="85%"> 
                            <table width="100%" border="0" cellspacing="0" cellpadding="3" class="MainText">
                              <tr> 
                                <td align="right" width="35%">Device type: </td>
                                <td width="65%"> 
                                  <select name="DeviceType" onchange="changeDeviceType()">
                                    <%
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == deviceType)? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" width="30%"><span id="NameLabel"></span>: </td>
                                <td width="70%"> 
                                  <input type="text" name="SerialNo" maxlength="30" size="20" value="<%= valStr %>">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="146"> 
                      <input type="submit" name="CheckInv" value="Check Inventory" onclick="return validate(this.form)">
                    </td>
                  </tr>
                </table>
              </form>
			  <% if (!inWizard) { %>
                <input type="button" name="Cancel" value="Cancel" onclick="location.href = '<%= referer %>'">
			  <% } else { %>
                <input type="button" name="Cancel" value="Cancel" onclick="location.href = '../Operations.jsp'">
			  <% } %>
              <p>&nbsp;</p>
              <p>&nbsp;</p>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
