<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.roles.yukon.EnergyCompanyRole" %>
<%@ page import="com.cannontech.stars.util.ECUtils" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory inventory = inventories.getStarsInventory(invNo);
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
		for (int ln = 1; ln <= 8; ln++) {
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
<%
	boolean isExpressCom = false;
	boolean isVersaCom = false;
	if (ECUtils.isSA205(inventory.getDeviceType().getEntryID())) {
		SA205 sa205 = null;
		if (configuration != null) sa205 = configuration.getSA205();
		if (sa205 == null) sa205 = new SA205();
%>
                      <table width="180" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC">
                        <tr> 
                          <td class="TitleHeader" align="center"> Slot Addresses<br>
                            <table width="80%" border="0" cellspacing="3" cellpadding="0">
                              <tr> 
                                <td width="60%" align="center"> 
                                  <input type="text" name="SA205_Slot1" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot1()) %>" size="15" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td width="60%" align="center"> 
                                  <input type="text" name="SA205_Slot2" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot2()) %>" size="15" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td width="60%" align="center"> 
                                  <input type="text" name="SA205_Slot3" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot3()) %>" size="15" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td width="60%" align="center"> 
                                  <input type="text" name="SA205_Slot4" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot4()) %>" size="15" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td width="60%" align="center"> 
                                  <input type="text" name="SA205_Slot5" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot5()) %>" size="15" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td width="60%" align="center"> 
                                  <input type="text" name="SA205_Slot6" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot6()) %>" size="15" maxlength="15">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<%
	}
	else if (ECUtils.isSA305(inventory.getDeviceType().getEntryID())) {
		SA305 sa305 = null;
		if (configuration != null) sa305 = configuration.getSA305();
		if (sa305 == null) sa305 = new SA305();
%>
                      <table width="180" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC">
                        <tr>
                          <td align="center" class="TitleHeader">SA305 Addresses<br>
                            <table width="100%" border="0" cellspacing="3" cellpadding="0">
                              <tr> 
                                <td align="right" class="MainText" width="50%">Utility:</td>
                                <td width="50%"> 
                                  <input type="text" name="SA305_Utility" value="<%= ServletUtils.hideUnsetNumber(sa305.getUtility()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="50%">Group:</td>
                                <td width="50%"> 
                                  <input type="text" name="SA305_Group" value="<%= ServletUtils.hideUnsetNumber(sa305.getGroup()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="50%">Division:</td>
                                <td width="50%"> 
                                  <input type="text" name="SA305_Division" value="<%= ServletUtils.hideUnsetNumber(sa305.getDivision()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="50%">Substation:</td>
                                <td width="50%"> 
                                  <input type="text" name="SA305_Substation" value="<%= ServletUtils.hideUnsetNumber(sa305.getSubstation()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="TitleHeader" width="50%">Rate 
                                  Address </td>
                                <td width="50%">&nbsp;</td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="50%">Family:</td>
                                <td width="50%"> 
                                  <input type="text" name="SA305_RateFamily" value="<%= ServletUtils.hideUnsetNumber(sa305.getRateFamily()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="50%">Member:</td>
                                <td width="50%"> 
                                  <input type="text" name="SA305_RateMember" value="<%= ServletUtils.hideUnsetNumber(sa305.getRateMember()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="50%"> 
                                  Hierarchy:</td>
                                <td width="50%"> 
                                  <input type="text" name="SA305_RateHierarchy" value="<%= ServletUtils.hideUnsetNumber(sa305.getRateHierarchy()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<%
	}
	else if (ECUtils.isVersaCom(inventory.getDeviceType().getEntryID())) {
		isVersaCom = true;
		VersaCom vcom = null;
		if (configuration != null) vcom = configuration.getVersaCom();
		if (vcom == null) vcom = new VersaCom();
		
		String[] classChecked = new String[16];
		String[] divisionChecked = new String[16];
		Arrays.fill(classChecked, "");
		Arrays.fill(divisionChecked, "");
		
		for (int i = 0, bm = 1; i < 16; i++) {
			if ((vcom.getClassAddress() & bm) != 0) classChecked[i] = "checked";
			if ((vcom.getDivision() & bm) != 0) divisionChecked[i] = "checked";
			bm *= 2;
		}
%>
                      <table width="240" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC">
                        <tr> 
                          <td align="center" class="TitleHeader" height="69">VERSACOM 
                            Addresses<br>
                            <table width="100%" border="0" cellspacing="3" cellpadding="0">
                              <tr> 
                                <td align="right" class="MainText" width="20%">Utility:</td>
                                <td width="80%"> 
                                  <input type="text" name="VCOM_Utility" value="<%= ServletUtils.hideUnsetNumber(vcom.getUtility()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="20%">Section:</td>
                                <td width="80%"> 
                                  <input type="text" name="VCOM_Section" value="<%= ServletUtils.hideUnsetNumber(vcom.getSection()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td class="MainText" colspan="2"> 
                                  <hr>
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="20%">Class:</td>
                                <td width="80%"> 
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell2">
                                    <tr align="center"> 
                                      <td>1</td>
                                      <td>2</td>
                                      <td>3</td>
                                      <td>4</td>
                                      <td>5</td>
                                      <td>6</td>
                                      <td>7</td>
                                      <td>8</td>
                                    </tr>
                                    <tr align="center"> 
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="1" <%= classChecked[0] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="2" <%= classChecked[1] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="4" <%= classChecked[2] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="8" <%= classChecked[3] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="16" <%= classChecked[4] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="32" <%= classChecked[5] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="64" <%= classChecked[6] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="128" <%= classChecked[7] %>>
                                      </td>
                                    </tr>
                                    <tr align="center"> 
                                      <td>9</td>
                                      <td>10</td>
                                      <td>11</td>
                                      <td>12</td>
                                      <td>13</td>
                                      <td>14</td>
                                      <td>15</td>
                                      <td>16</td>
                                    </tr>
                                    <tr align="center"> 
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="256" <%= classChecked[8] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="512" <%= classChecked[9] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="1024" <%= classChecked[10] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="2048" <%= classChecked[11] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="4096" <%= classChecked[12] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="8192" <%= classChecked[13] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="16384" <%= classChecked[14] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Class" value="32768" <%= classChecked[15] %>>
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                              <tr> 
                                <td class="MainText" colspan="2"> 
                                  <hr>
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="20%">Division:</td>
                                <td width="80%"> 
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell2">
                                    <tr align="center"> 
                                      <td>1</td>
                                      <td>2</td>
                                      <td>3</td>
                                      <td>4</td>
                                      <td>5</td>
                                      <td>6</td>
                                      <td>7</td>
                                      <td>8</td>
                                    </tr>
                                    <tr align="center"> 
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="1" <%= divisionChecked[0] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="2" <%= divisionChecked[1] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="4" <%= divisionChecked[2] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="8" <%= divisionChecked[3] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="16" <%= divisionChecked[4] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="32" <%= divisionChecked[5] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="64" <%= divisionChecked[6] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="128" <%= divisionChecked[7] %>>
                                      </td>
                                    </tr>
                                    <tr align="center"> 
                                      <td>9</td>
                                      <td>10</td>
                                      <td>11</td>
                                      <td>12</td>
                                      <td>13</td>
                                      <td>14</td>
                                      <td>15</td>
                                      <td>16</td>
                                    </tr>
                                    <tr align="center"> 
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="256" <%= divisionChecked[8] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="512" <%= divisionChecked[9] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="1024" <%= divisionChecked[10] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="2048" <%= divisionChecked[11] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="4096" <%= divisionChecked[12] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="8192" <%= divisionChecked[13] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="16384" <%= divisionChecked[14] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="VCOM_Division" value="32768" <%= divisionChecked[15] %>>
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<%
	}
	else if (ECUtils.isExpressCom(inventory.getDeviceType().getEntryID())) {
		isExpressCom = true;
		ExpressCom xcom = null;
		if (configuration != null) xcom = configuration.getExpressCom();
		if (xcom == null) xcom = new ExpressCom();
		
		String[] checked = new String[16];
		Arrays.fill(checked, "");
		for (int i = 0, bm = 1; i < 16; i++) {
			if ((xcom.getFeeder() & bm) != 0) checked[i] = "checked";
			bm *= 2;
		}
%>
                      <table width="240" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC">
                        <tr> 
                          <td align="center" class="TitleHeader" height="69">EXPRESSCOM 
                            Addresses<br>
                            <table width="100%" border="0" cellspacing="3" cellpadding="0">
                              <tr> 
                                <td align="right" class="MainText" width="20%">SPID:</td>
                                <td width="80%"> 
                                  <input type="text" name="XCOM_SPID" value="<%= ServletUtils.hideUnsetNumber(xcom.getServiceProvider()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="20%">GEO:</td>
                                <td width="80%"> 
                                  <input type="text" name="XCOM_GEO" value="<%= ServletUtils.hideUnsetNumber(xcom.getGEO()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="20%">SUB:</td>
                                <td width="80%"> 
                                  <input type="text" name="XCOM_SUB" value="<%= ServletUtils.hideUnsetNumber(xcom.getSubstation()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td class="MainText" colspan="2"> 
                                  <hr>
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="20%">FEED:</td>
                                <td width="80%"> 
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell2">
                                    <tr align="center"> 
                                      <td>1</td>
                                      <td>2</td>
                                      <td>3</td>
                                      <td>4</td>
                                      <td>5</td>
                                      <td>6</td>
                                      <td>7</td>
                                      <td>8</td>
                                    </tr>
                                    <tr align="center"> 
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="1" <%= checked[0] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="2" <%= checked[1] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="4" <%= checked[2] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="8" <%= checked[3] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="16" <%= checked[4] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="32" <%= checked[5] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="64" <%= checked[6] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="128" <%= checked[7] %>>
                                      </td>
                                    </tr>
                                    <tr align="center"> 
                                      <td>9</td>
                                      <td>10</td>
                                      <td>11</td>
                                      <td>12</td>
                                      <td>13</td>
                                      <td>14</td>
                                      <td>15</td>
                                      <td>16</td>
                                    </tr>
                                    <tr align="center"> 
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="256" <%= checked[8] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="512" <%= checked[9] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="1024" <%= checked[10] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="2048" <%= checked[11] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="4096" <%= checked[12] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="8192" <%= checked[13] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="16384" <%= checked[14] %>>
                                      </td>
                                      <td> 
                                        <input type="checkbox" name="XCOM_FEED" value="32768" <%= checked[15] %>>
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                              <tr> 
                                <td class="MainText" colspan="2"> 
                                  <hr>
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="20%">ZIP:</td>
                                <td width="80%"> 
                                  <input type="text" name="XCOM_ZIP" value="<%= ServletUtils.hideUnsetNumber(xcom.getZip()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                              <tr> 
                                <td align="right" class="MainText" width="20%">USER:</td>
                                <td width="80%"> 
                                  <input type="text" name="XCOM_USER" value="<%= ServletUtils.hideUnsetNumber(xcom.getUserAddress()) %>" size="6" maxlength="15">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<%
	}
%>
                    </td>
                    <td valign="top" align="center"> 
<%
	String[] coldLoadPickup = new String[8];
	Arrays.fill(coldLoadPickup, "");
	String[] tamperDetect = new String[8];
	Arrays.fill(tamperDetect, "");
	String[] program = new String[8];
	Arrays.fill(program, "");
	String[] splinter = new String[8];
	Arrays.fill(splinter, "");
	
	if (configuration != null) {
		String[] clp = configuration.getColdLoadPickup().split(",");
		String[] td = configuration.getTamperDetect().split(",");
		System.arraycopy(clp, 0, coldLoadPickup, 0, clp.length);
		System.arraycopy(td, 0, tamperDetect, 0, td.length);
		
		if (configuration.getExpressCom() != null) {
			String[] prog = configuration.getExpressCom().getProgram().split(",");
			String[] splt = configuration.getExpressCom().getSplinter().split(",");
			System.arraycopy(prog, 0, program, 0, prog.length);
			System.arraycopy(splt, 0, splinter, 0, splt.length);
		}
	}
%>
                      <table border="1" cellspacing="0" cellpadding="0" bgcolor="#CCCCCC">
                        <tr> 
                          <td> 
                            <table border="0" cellspacing="3" cellpadding="0">
                              <tr class="TitleHeader" valign="top" align="center"> 
                                <td width="30" class="HeaderCell">Relay</td>
                                <td width="65" class="HeaderCell">Cold Load Pickup</td>
<% if (!isVersaCom && !isExpressCom) { %>
                                <td width="65" class="HeaderCell">Tamper Detect</td>
<% } %>
<% if (isExpressCom) { %>
                                <td width="65" class="HeaderCell">Program</td>
                                <td width="65" class="HeaderCell">Splinter</td>
<% } %>
                              </tr>
<%
	int numRelay = isExpressCom? 8 : 4;
	for (int i = 0; i < numRelay; i++) {
%>
                              <tr align="center"> 
                                <td width="30" class="HeaderCell"><%= i+1 %></td>
                                <td width="65"> 
                                  <input type="text" name="ColdLoadPickup" value="<%= coldLoadPickup[i] %>" size="4" maxlength="10">
                                </td>
<% if (!isVersaCom && !isExpressCom) { %>
                                <td width="65"> 
                                  <input type="text" name="TamperDetect" value="<%= tamperDetect[i] %>" size="4" maxlength="10">
                                </td>
<% } %>
<% if (isExpressCom) { %>
                                <td width="65"> 
                                  <input type="text" name="XCOM_Program" value="<%= program[i] %>" size="4" maxlength="10">
                                </td>
                                <td width="65"> 
                                  <input type="text" name="XCOM_Splinter" value="<%= splinter[i] %>" size="4" maxlength="10">
                                </td>
<% } %>
                              </tr>
<%
	}
%>
                            </table>
                          </td>
                        </tr>
                      </table>
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
                      <input type="button" name="SaveToBatch" value="Save To Batch" onclick="saveToBatch(this.form)">
					  <input type="button" name="SaveConfig" value="Save Config Only" onclick="saveConfigOnly(this.form)">
                    </td>
                  </tr>
                </table>
                <br>
                <table width="300" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td align="center"> 
                      <input type="button" name="Enable" value="Reenable" onclick="sendCommand('EnableLMHardware')">
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
