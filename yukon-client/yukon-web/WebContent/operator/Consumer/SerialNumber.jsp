<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.web.navigation.CtiNavObject" %>
<%@ page import="com.cannontech.core.dao.NotFoundException" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<jsp:useBean id="configBean" class="com.cannontech.stars.web.bean.ConfigBean" scope="page"/>
<%
	String action = request.getParameter("action");
	String referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER);
	
	Integer invNo = null;
	if (request.getParameter("InvNo") != null)
		invNo = Integer.valueOf( request.getParameter("InvNo") );
	session.setAttribute(InventoryManagerUtil.STARS_INVENTORY_NO, invNo);
	
	int deviceType = 0;
	String serialNo = "";
	String deviceName = "";
	boolean needAppliance = false;
    
	if (action != null) {
		if (action.equalsIgnoreCase("New")) {
			// Came from the nav link
			session.removeAttribute(InventoryManagerUtil.STARS_INVENTORY_TEMP);
			referer = request.getContextPath() + "/operator/Consumer/CreateHardware.jsp";
			if (request.getParameter("Wizard") != null) referer += "?Wizard=true";
            if(configBean.isWriteToFileAllowed() && accountInfo != null) {
                for(int i = 0; i < accountInfo.getStarsAppliances().getStarsApplianceCount(); i++) {
                    if(accountInfo.getStarsAppliances().getStarsAppliance(i).getInventoryID() < 1) {
                        needAppliance = false;
                        session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "");          
                        break;
                    }
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Create an appliance before adding a new switch."); 
                    needAppliance = true;
                }
            }
		}
		else if (action != null && action.equalsIgnoreCase("Change")) {
			StarsInventory inventory = null;
			if (invNo != null) {
				// Came from Inventory.jsp when change button is clicked
				inventory = inventories.getStarsInventory(invNo.intValue());
				session.setAttribute(InventoryManagerUtil.STARS_INVENTORY_TEMP, inventory);
			}
			else {
				// Came from CreateHardware.jsp when change button is clicked
				inventory = (StarsInventory) session.getAttribute(InventoryManagerUtil.STARS_INVENTORY_TEMP);
			}
			
			if (inventory != null) {
				deviceType = inventory.getDeviceType().getEntryID();
				if (inventory.getLMHardware() != null)
					serialNo = inventory.getLMHardware().getManufacturerSerialNumber();
				else if (inventory.getDeviceID() > 0)
                {
					try
                    {
                        deviceName = DaoFactory.getPaoDao().getYukonPAOName(inventory.getDeviceID());
                    }
                    catch(NotFoundException e) {}
                }
				else if (inventory.getMCT() != null)
					deviceName = inventory.getMCT().getDeviceName();
			}
			
			referer = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
		}
		
		session.setAttribute(ServletUtils.ATT_REFERRER, referer);
		session.setAttribute(ServletUtils.ATT_REDIRECT, referer);
	}
	else {
		// Came from SelectInv.jsp or CreateHardware.jsp when cancel button is clicked
		StarsInventory inventory = (StarsInventory) session.getAttribute(InventoryManagerUtil.STARS_INVENTORY_TEMP);
		if (inventory == null && invNo != null)
			inventory = inventories.getStarsInventory(invNo.intValue());
		
		if (inventory != null) {
			deviceType = inventory.getDeviceType().getEntryID();
			if (inventory.getLMHardware() != null)
				serialNo = inventory.getLMHardware().getManufacturerSerialNumber();
			else if (inventory.getDeviceID() > 0)
            {
				try
                {
                    deviceName = DaoFactory.getPaoDao().getYukonPAOName(inventory.getDeviceID());
                }
                catch(NotFoundException e) {}
            }
			else if (inventory.getMCT() != null)
				deviceName = inventory.getMCT().getDeviceName();
		}
	}
	
	boolean inWizard = referer.indexOf("Wizard") >= 0;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	YukonListEntry devTypeMCT = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function validate(form) {
	<% if(needAppliance) { %>
        if (confirm("There is NOT an unassigned appliance on this account available for a new switch.  Do you wish to be redirected to the appliance page to create one?  (An appliance is REQUIRED for automatic addressing group assignment.)")) {
            form.action.value = "";
            form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Consumer/CreateAppliances.jsp";
            form.submit();
            return true;
            }                    
    <% } %> 
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
	
	return true;
}

function selectInventory(form) {
	<% if(needAppliance) { %>
        if (confirm("There is NOT an unassigned appliance on this account available for a new switch.  Do you wish to be redirected to the appliance page to create one?  (An appliance is REQUIRED for automatic addressing group assignment.)")) {
            form.action.value = "";
            form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Consumer/CreateAppliances.jsp";
            form.submit(); 
            return;    
        }      
    <% } %>    
    <%  if(((ArrayList)session.getAttribute(ServletUtil.FILTER_INVEN_LIST)) == null || ((ArrayList)session.getAttribute(ServletUtil.FILTER_INVEN_LIST)).size() < 1) 
        { %>
       		form.attributes["action"].value = "SelectInvFilter.jsp";
    <%  }
    	else 
    	{
    %>
		form.attributes["action"].value = "SelectInv.jsp";
	<% 	} %>
	form.submit();
}

function selectMCT(form) {
	form.attributes["action"].value = "SelectMCT.jsp";
	form.submit();
}

function confirmCancel() {
	if (confirm("Are you sure you want to quit and discard all changes you've been made?"))
		location.href = "../Operations.jsp";
}

function changeDeviceType(type) {
<% if (devTypeMCT != null) { %>
	if (type == <%= devTypeMCT.getEntryID() %>) {
		document.getElementById("HardwareDiv").style.display = "none";
		document.getElementById("DeviceDiv").style.display = "";
	}
	else {
		document.getElementById("DeviceDiv").style.display = "none";
		document.getElementById("HardwareDiv").style.display = "";
	}
<% } %>
}

function init() {
	changeDeviceType(document.MForm.DeviceType.value);
}
</script>
</head>
<body class="Background" leftmargin="0" topmargin="0" onload="init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <%@ include file="include/HeaderBar.jspf" %>
      <script language="JavaScript">setContentChanged(<%= inWizard %>);</script>
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
			<%@ include file="include/Nav.jspf" %>
<% } %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "MainText" align="center">
              <% String header = "INVENTORY CHECKING"; %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jspf" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jspf" %>
<% } %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
			  <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager">
			    <input type="hidden" name="action" value="CheckInventory">
				<input type="hidden" name="REDIRECT" value="<%= referer %>">
                Please select a device from the current inventory (Select Inventory),<br>
<%String meterBase = DaoFactory.getAuthDao().getRolePropertyValue( lYukonUser, EnergyCompanyRole.METER_MCT_BASE_DESIGNATION);
if (account != null && meterBase != null && meterBase.compareTo(com.cannontech.stars.util.StarsUtils.METER_BASE_DESIGNATION) == 0)
{%>
				create a new Meter (Create New Meter),<br>
<%}
else if (devTypeMCT != null && meterBase != null && meterBase.compareTo(com.cannontech.stars.util.StarsUtils.MCT_BASE_DESIGNATION) == 0) { %>
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
<%if (account != null && meterBase != null && meterBase.compareTo(com.cannontech.stars.util.StarsUtils.METER_BASE_DESIGNATION) == 0)
{%>
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
                              <input type="button" name="NewMeter" value="Create New Meter" onClick="location.href ='<%=request.getContextPath()%>/operator/Consumer/MeterProfile.jsp?MetRef=-1'">
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
<%}
else if (devTypeMCT != null && meterBase != null && meterBase.compareTo(com.cannontech.stars.util.StarsUtils.MCT_BASE_DESIGNATION) == 0) { %>
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
                                <td align="right" width="30%">Device Type: </td>
                                <td width="70%"> 
                                  <select name="DeviceType" onchange="changeDeviceType(this.value)">
                                    <%
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
		if(entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) continue;
		String selected = (entry.getEntryID() == deviceType)? "selected" : "";
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
                            <table width="100%" border="0" cellspacing="0" cellpadding="3" class="MainText" bgcolor="#CCCCCC">
                              <tr> 
                                <td align="right" width="20%">
                                    Serial #: 
                                </td> 
                                <td align="left" width="80%"> 
                                  <div align="left">
                                      <input type="text" name="SerialNo" maxlength="60" size="24" value="<%= serialNo %>">
                                  </div>
                                </td>
                              </tr>
                            </table>
							</div>
							<div id="DeviceDiv">
                            <table width="100%" border="0" cellspacing="0" cellpadding="3" class="MainText" bgcolor="#CCCCCC">
                              <tr> 
                                <td align="right" width="30%">Device Name: </td>
                                <td width="70%"> 
                                  <input type="text" name="DeviceName" maxlength="30" size="24" value="<%= deviceName %>">
                                </td>
                              </tr>
                            </table>
							</div>
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
<% if (session.getAttribute(InventoryManagerUtil.STARS_INVENTORY_TEMP) != null) { %>
                <input type="button" name="Cancel" value="Cancel" onclick="location.href = '<%= referer %>'">
<% } %>
              <p>&nbsp;</p>
              <p>&nbsp;</p>
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
