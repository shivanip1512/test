<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory inventory = inventories.getStarsInventory(invNo);
	
	ArrayList attachedApps = new ArrayList();
	ArrayList attachedProgs = new ArrayList();
	
	for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
		StarsAppliance app = appliances.getStarsAppliance(i);
		if (app.getInventoryID() == inventory.getInventoryID()) {
			attachedApps.add(app);
			
			for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
				StarsLMProgram program = programs.getStarsLMProgram(j);
				if (program.getProgramID() == app.getLmProgramID()) {
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

function changeProgSelection(chkBox) {
	var grpList = document.getElementById('Group_Prog' + chkBox.value);
	grpList.disabled = !chkBox.checked;
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
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
                <table width="350" border="1" cellspacing="0" cellpadding="3">
                  <tr> 
                    <td width="5%" class="HeaderCell">&nbsp; </td>
                    <td width="45%" class="HeaderCell">Program</td>
                    <td width="50%" class="HeaderCell">Assigned Group</td>
                  </tr>
<%
	for (int i = 0; i < attachedProgs.size(); i++) {
		StarsLMProgram program = (StarsLMProgram) attachedProgs.get(i);
		
		StarsEnrLMProgram enrProg = null;
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			if (appCat.getApplianceCategoryID() == program.getApplianceCategoryID()) {
				for (int k = 0; k < appCat.getStarsEnrLMProgramCount(); k++) {
					if (appCat.getStarsEnrLMProgram(k).getProgramID() == program.getProgramID()) {
						enrProg = appCat.getStarsEnrLMProgram(k);
						break;
					}
				}
				break;
			}
		}
%>
                  <tr> 
                    <td width="5%" height="2"> 
                      <input type="checkbox" name="ProgID" value="<%= program.getProgramID() %>" checked onclick="changeProgSelection(this)">
                    </td>
                    <td width="45%" class="TableCell" height="2"><%= program.getProgramName() %></td>
                    <td width="50%" height="2"> 
                      <select id="Group_Prog<%= program.getProgramID() %>" name="GroupID">
<%
		if (enrProg == null || enrProg.getAddressingGroupCount() == 0) {
%>
                        <option value="0">(none)</option>
<%
		} else {
			for (int j = 0; j < enrProg.getAddressingGroupCount(); j++) {
				AddressingGroup group = enrProg.getAddressingGroup(j);
				String selectedStr = (group.getEntryID() == program.getGroupID()) ? "selected" : "";
%>
                        <option value="<%= group.getEntryID() %>" <%= selectedStr %>><%= group.getContent() %></option>
<%
			}
		}
%>
                      </select>
                    </td>
                  </tr>
<%
	}
	
	for (int i = 0; i < unattachedProgs.size(); i++) {
		StarsLMProgram program = (StarsLMProgram) unattachedProgs.get(i);
		
		StarsEnrLMProgram enrProg = null;
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			if (appCat.getApplianceCategoryID() == program.getApplianceCategoryID()) {
				for (int k = 0; k < appCat.getStarsEnrLMProgramCount(); k++) {
					if (appCat.getStarsEnrLMProgram(k).getProgramID() == program.getProgramID()) {
						enrProg = appCat.getStarsEnrLMProgram(k);
						break;
					}
				}
				break;
			}
		}
		
		boolean disabled = (enrProg == null) || (enrProg.getAddressingGroupCount() == 0);
%>
                  <tr> 
                    <td width="5%" height="2"> 
                      <input type="checkbox" name="ProgID" value="<%= program.getProgramID() %>" onClick="changeProgSelection(this)"
							 <%= (disabled)? "disabled" : "" %>>
                    </td>
                    <td width="45%" class="TableCell" height="2"><%= program.getProgramName() %></td>
                    <td width="50%" height="2"> 
                      <select id="Group_Prog<%= program.getProgramID() %>" name="GroupID" disabled="true">
<%
		if (disabled) {
%>
                        <option value="0">(none)</option>
<%
		} else {
				for (int j = 0; j < enrProg.getAddressingGroupCount(); j++) {
					AddressingGroup group = enrProg.getAddressingGroup(j);
%>
                        <option value="<%= group.getEntryID() %>"><%= group.getContent() %></option>
<%
				}
		}
%>
                      </select>
                    </td>
                  </tr>
<%
	}
%>
                </table>
                <br>
                <table width="350" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="center"> 
                      <input type="submit" name="UpdateConfig" value="Config Now">
                      <input type="button" name="SaveConfig" value="Save To Batch" onclick="sendCommand('SaveLMHardwareConfig')">
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
              <table width="350" border="1" cellspacing="0" cellpadding="3">
                <tr bgcolor="#FFFFFF"> 
                  <td width="104" class="HeaderCell"> Appliance Type</td>
                  <td width="100" class="HeaderCell"> Status</td>
                  <td width="120" class="HeaderCell"> Enrolled Programs</td>
                </tr>
<%
	for (int i = 0; i < attachedApps.size(); i++) {
		StarsAppliance appliance = (StarsAppliance) attachedApps.get(i);
		
		StarsLMProgram program = null;
		for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
			StarsLMProgram starsProg = programs.getStarsLMProgram(j);
			if (starsProg.getProgramID() == appliance.getLmProgramID()) {
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
                  <td width="104" class="TableCell"><%= ServletUtils.getApplianceDescription(categories, appliance) %></td>
                  <td width="100" class="TableCell"><%= program.getStatus() %></td>
                  <td width="120"> 
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
