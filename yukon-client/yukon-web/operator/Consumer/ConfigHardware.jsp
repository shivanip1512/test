<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.util.ECUtils" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory inventory = inventories.getStarsInventory(invNo);
	int hwConfigType = ECUtils.getHardwareConfigType(inventory.getDeviceType().getEntryID());
	StarsLMConfiguration configuration = inventory.getLMHardware().getStarsLMConfiguration();
	
	ArrayList attachedApps = new ArrayList();
	ArrayList attachedProgs = new ArrayList();
	
	for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
		StarsAppliance app = appliances.getStarsAppliance(i);
		if (app.getInventoryID() == inventory.getInventoryID()) {
			int idx = 0;
			for (idx = 0; idx < attachedApps.size(); idx++) {
				if (((StarsAppliance)attachedApps.get(idx)).getLoadNumber() > app.getLoadNumber())
					break;
			}
			attachedApps.add(idx, app);
			
			for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
				StarsLMProgram program = programs.getStarsLMProgram(j);
				if (program.getProgramID() == app.getProgramID()) {
					if (!attachedProgs.contains(program)) attachedProgs.add(program);
					break;
				}
			}
		}
	}
	
	ArrayList unattachedProgs = new ArrayList();
	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
		StarsLMProgram program = programs.getStarsLMProgram(i);
		if (!attachedProgs.contains(program)) unattachedProgs.add(program);
	}
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function init() {
	if (document.getElementById("Override") != null)
		document.getElementById("Override").value = '<cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_OVERRIDE %>" format="all_capital"/>';
	document.getElementById("Reenable").value = '<cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_REENABLE %>" format="all_capital"/>';
}

function sendCommand(cmd) {
	var form = document.invForm;
	form.action.value = cmd;
	form.submit();
}

function saveToBatch(form) {
	form.SaveToBatch.value = true;
	form.submit();
}

function saveConfigOnly(form) {
	form.SaveConfigOnly.value = true;
	form.submit();
}

function changeProgSelection(chkBox) {
	var grpList = document.getElementById('Group_Prog' + chkBox.value);
	var loadList = document.getElementById('Load_Prog' + chkBox.value);
	grpList.disabled = !chkBox.checked;
	loadList.disabled = !chkBox.checked;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
            <% String pageName = "ConfigHardware.jsp?InvNo=" + invNo; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "HARDWARE - CONFIGURATION"; %>
			  <%@ include file="include/InfoSearchBar.jsp" %>
			  <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
			  <form name="invForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
                <input type="hidden" name="action" value="UpdateLMHardwareConfig">
                <input type="hidden" name="InvID" value="<%= inventory.getInventoryID() %>">
				<input type="hidden" name="SaveToBatch" value="false">
				<input type="hidden" name="SaveConfigOnly" value="false">
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
                <table width="60%" border="1" cellspacing="0" cellpadding="3">
                  <tr> 
                    <td width="5%" class="HeaderCell">&nbsp; </td>
                    <td width="35%" class="HeaderCell">Program</td>
                    <td width="35%" class="HeaderCell">Assigned Group</td>
                    <td width="25%" class="HeaderCell">Relay</td>
                  </tr>
<%
	for (int i = 0; i < attachedProgs.size(); i++) {
		StarsLMProgram program = (StarsLMProgram) attachedProgs.get(i);
		StarsEnrLMProgram enrProg = ServletUtils.getEnrollmentProgram(categories, program.getProgramID());
		
		int loadNo = 0;
		for (int j = 0; j < appliances.getStarsApplianceCount(); j++) {
			StarsAppliance app = appliances.getStarsAppliance(j);
			if (app.getInventoryID() == inventory.getInventoryID() && app.getProgramID() == program.getProgramID()) {
				loadNo = app.getLoadNumber();
				break;
			}
		}
%>
                  <tr> 
                    <td width="5%" height="2"> 
                      <input type="checkbox" name="ProgID" value="<%= program.getProgramID() %>" checked onclick="changeProgSelection(this)">
                    </td>
                    <td width="35%" class="TableCell" height="2"><%= program.getProgramName() %></td>
                    <td width="35%" height="2"> 
                      <select id="Group_Prog<%= program.getProgramID() %>" name="GroupID">
<%
		for (int j = 0; j < enrProg.getAddressingGroupCount(); j++) {
			AddressingGroup group = enrProg.getAddressingGroup(j);
			String selected = (group.getEntryID() == program.getGroupID()) ? "selected" : "";
%>
                        <option value="<%= group.getEntryID() %>" <%= selected %>><%= group.getContent() %></option>
<%
		}
%>
                      </select>
                    </td>
                    <td width="25%" height="2">
                      <select id="Load_Prog<%= program.getProgramID() %>" name="LoadNo">
                        <option value="0">(none)</option>
<%
		for (int ln = 1; ln <= 8; ln++) {
			String selected = (ln == loadNo)? "selected" : "";
%>
                        <option value="<%= ln %>" <%= selected %>><%= ln %></option>
<%
		}
%>
                      </select>
                    </td>
                  </tr>
<%
	}
	
	for (int i = 0; i < unattachedProgs.size(); i++) {
		StarsLMProgram program = (StarsLMProgram) unattachedProgs.get(i);
		StarsEnrLMProgram enrProg = ServletUtils.getEnrollmentProgram(categories, program.getProgramID());
%>
                  <tr> 
                    <td width="5%" height="2"> 
                      <input type="checkbox" name="ProgID" value="<%= program.getProgramID() %>" onclick="changeProgSelection(this)">
                    </td>
                    <td width="35%" class="TableCell" height="2"><%= program.getProgramName() %></td>
                    <td width="35%" height="2"> 
                      <select id="Group_Prog<%= program.getProgramID() %>" name="GroupID" disabled="true">
<%
			for (int j = 0; j < enrProg.getAddressingGroupCount(); j++) {
				AddressingGroup group = enrProg.getAddressingGroup(j);
%>
                        <option value="<%= group.getEntryID() %>"><%= group.getContent() %></option>
<%
		}
%>
                      </select>
                    </td>
                    <td width="25%" height="2">
                      <select id="Load_Prog<%= program.getProgramID() %>" name="LoadNo" disabled="true">
                        <option value="0">(none)</option>
<%
		int numRelays = (hwConfigType == ECUtils.HW_CONFIG_TYPE_EXPRESSCOM)? 8 : 4;
		for (int ln = 1; ln <= numRelays; ln++) {
%>
                        <option value="<%= ln %>"><%= ln %></option>
<%
		}
%>
                      </select>
                    </td>
                  </tr>
<%
	}
%>
                </table>
<%
	String trackHwAddr = liteEC.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
	if (trackHwAddr != null && Boolean.valueOf(trackHwAddr).booleanValue()) {
%>
                <br>
				<input type="hidden" name="UseHardwareAddressing" value="true">
                <table width="500" border="0" cellspacing="0" cellpadding="10">
                  <tr> 
                    <td valign="top" align="center"> 
                      <%@ include file="../../include/hwconfig_addressing.jsp" %>
                    </td>
                    <td valign="top" align="center"> 
                      <%@ include file="../../include/hwconfig_relays.jsp" %>
                    </td>
                  </tr>
                </table>
<%
	}
%>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="center"> 
                      <input type="submit" name="Config" value="Config">
                      <input type="button" name="SaveBatch" value="Save To Batch" onclick="saveToBatch(this.form)">
					  <input type="button" name="SaveConfig" value="Save Config Only" onclick="saveConfigOnly(this.form)">
                    </td>
                  </tr>
                </table>
                <br>
                <table width="300" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td align="center"> 
<cti:checkProperty propertyid="<%= ConsumerInfoRole.OVERRIDE_HARDWARE %>">
                      <input type="button" id="Override" value="Override" onclick="location.href='OverrideHardware.jsp?InvNo=<%= invNo %>'">
</cti:checkProperty>
                      <input type="button" id="Reenable" value="Reenable" onclick="sendCommand('EnableLMHardware')">
                    </td>
                  </tr>
                </table>
              </form>
            </div>
            <hr>
            <div align="center"> <span class="TitleHeader">Appliance Summary</span><br>
              <table width="60%" border="1" cellspacing="0" cellpadding="3">
                <tr bgcolor="#FFFFFF"> 
                  <td width="25%" class="HeaderCell"> Appliance Type</td>
                  <td width="15%" class="HeaderCell">Load #</td>
                  <td width="15%" class="HeaderCell"> Status</td>
                  <td width="45%" class="HeaderCell"> Enrolled Programs</td>
                </tr>
                <%
	for (int i = 0; i < attachedApps.size(); i++) {
		StarsAppliance appliance = (StarsAppliance) attachedApps.get(i);
		
		String loadNo = "(none)";
		if (appliance.getLoadNumber() > 0) loadNo = String.valueOf(appliance.getLoadNumber());
		
		StarsLMProgram program = null;
		for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
			StarsLMProgram starsProg = programs.getStarsLMProgram(j);
			if (starsProg.getProgramID() == appliance.getProgramID()) {
				program = starsProg;
				break;
			}
		}
		
		StarsApplianceCategory category = null;
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			if (categories.getStarsApplianceCategory(j).getApplianceCategoryID() == program.getApplianceCategoryID()) {
				category = categories.getStarsApplianceCategory(j);
				break;
			}
		}
%>
                <tr bgcolor="#FFFFFF" valign="top"> 
                  <td width="25%" class="TableCell"><%= ServletUtils.getApplianceDescription(categories, appliance) %></td>
                  <td width="15%" class="TableCell"><%= loadNo %></td>
                  <td width="15%" class="TableCell"><%= program.getStatus() %></td>
                  <td width="45%"> 
                    <div align="center"> 
                      <% if (!category.getStarsWebConfig().getLogoLocation().equals("")) { %>
                      <img src="../../WebConfig/<%= category.getStarsWebConfig().getLogoLocation() %>"><br>
                      <% } %>
                      <span class="TableCell"><%= program.getProgramName() %></span> 
                    </div>
                  </td>
                </tr>
                <%
	}
%>
              </table>
            </div>
            <p>&nbsp;</p>
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
