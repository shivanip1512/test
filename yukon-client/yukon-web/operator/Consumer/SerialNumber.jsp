<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%
	String action = request.getParameter("action");
	String referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER);
	String redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
	
	String invNo = request.getParameter("InvNo");
	if (invNo == null) invNo = "";
	session.setAttribute(InventoryManager.STARS_INVENTORY_NO, invNo);
	
	int deviceType = 0;
	String serialNo = "";
	
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
				deviceType = inventory.getDeviceType().getEntryID();
				if (inventory.getLMHardware() != null)
					serialNo = inventory.getLMHardware().getManufacturerSerialNumber();
				else if (inventory.getDeviceID() > 0)
					serialNo = PAOFuncs.getYukonPAOName(inventory.getDeviceID());
				else if (inventory.getMCT() != null)
					serialNo = inventory.getMCT().getDeviceName();
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
		
		if (inventory != null && inventory.getLMHardware() != null) {
			deviceType = inventory.getDeviceType().getEntryID();
			serialNo = inventory.getLMHardware().getManufacturerSerialNumber();
		}
	}
	
	boolean inWizard = referer.indexOf("Wizard=true") >= 0;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	boolean hasMCT = ServletUtils.getStarsCustListEntry(selectionListTable,
			YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) != null;
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
	form.attributes["action"].value = "SelectInv.jsp";
	form.submit();
}

function selectMCT(form) {
	form.attributes["action"].value = "SelectMCT.jsp";
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
<% if (!inWizard) { %>
		    <% String pageName = referer.substring(referer.lastIndexOf('/') + 1); %>
			<%@ include file="include/Nav.jsp" %>
<% } %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "MainText" align="center">
              <% String header = "INVENTORY CHECKING"; %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jsp" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
<% } %>
			  
			  <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager">
			    <input type="hidden" name="action" value="CheckInventory">
				<input type="hidden" name="REDIRECT" value="<%= redirect %>">
                Please select a device from the current inventory (Select Inventory),<br>
<% if (hasMCT) { %>
                select a meter from the list of all MCTs (Select Meter),<br>
<% } %>
                or check the inventory for a specific device type and serial number 
                (Check Inventory).<br>
                <br>
                <table width="300" border="0" cellspacing="0" cellpadding="2">
                  <tr> 
                    <td> 
                      <table width="300" border="1" cellspacing="0" cellpadding="2" bgcolor="#CCCCCC" height="40">
                        <tr> 
                          <td height="30"> 
                            <div align="center"> 
                              <input type="button" name="SelectInv" value="Select Inventory" onClick="selectInventory(this.form)">
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
<% if (hasMCT) { %>
                  <tr> 
                    <td> 
                      <div align="center" class="TableCell">or</div>
                    </td>
                  </tr>
                  <tr> 
                    <td> 
                      <table width="300" border="1" cellspacing="0" cellpadding="2" bgcolor="#CCCCCC" height="40">
                        <tr> 
                          <td height="30"> 
                            <div align="center">
                              <input type="button" name="SelectMCT" value="Select Meter" onClick="selectMCT(this.form)">
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
<% } %>
                  <tr> 
                    <td> 
                      <div align="center" class="TableCell">or</div>
                    </td>
                  </tr>
                  <tr> 
                    <td> 
                      <table width="300" border="1" cellspacing="0" cellpadding="2" bgcolor="#CCCCCC" height="100">
                        <tr> 
                          <td> 
                            <table width="100%" border="0" cellspacing="0" cellpadding="3" class="MainText" bgcolor="#CCCCCC">
                              <tr> 
                                <td align="right" width="30%">Device type: </td>
                                <td width="70%"> 
                                  <select name="DeviceType">
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
                                <td align="right" width="30%">Serial #: </td>
                                <td width="70%"> 
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= serialNo %>">
                                </td>
                              </tr>
                            </table>
                            <div align="center"> 
                              <input type="submit" name="CheckInv" value="Check Inventory" onClick="return validate(this.form)">
                            </div>
                          </td>
                        </tr>
                      </table>
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
