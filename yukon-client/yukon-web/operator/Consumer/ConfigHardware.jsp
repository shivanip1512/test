<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory inventory = inventories.getStarsInventory(invNo);
	
	ArrayList appList = new ArrayList();
	for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
		StarsAppliance app = appliances.getStarsAppliance(i);
		if (app.getInventoryID() == inventory.getInventoryID())
			appList.add(app);
	}
	
	StarsAppliance[] starsApps = new StarsAppliance[ appList.size() ];
	appList.toArray( starsApps );
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function sendCommand(cmd) {
	var form = document.invForm;
	form.action.value = cmd;
	form.submit();
}

function changeAppSelection(chkBox) {
	var grpList = document.getElementById('Group_App' + chkBox.value);
	grpList.disabled = !chkBox.checked;
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
          <td valign="bottom" height="102"> 
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
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
            <% String pageName = "ConfigHardware.jsp?InvNo=" + invNo; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "HARDWARE - CONFIGURATION"; %>
			  <%@ include file="include/InfoSearchBar.jsp" %>
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
	for (int i = 0; i < starsApps.length; i++) {
		StarsLMProgram program = null;
		for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
			StarsLMProgram starsProg = programs.getStarsLMProgram(j);
			if (starsProg.getProgramID() == starsApps[i].getLmProgramID()) {
				program = starsProg;
				break;
			}
		}
		
		StarsEnrLMProgram enrProg = null;
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory category = categories.getStarsApplianceCategory(j);
			if (category.getApplianceCategoryID() == starsApps[i].getApplianceCategoryID()) {
				for (int k = 0; k < category.getStarsEnrLMProgramCount(); k++) {
					StarsEnrLMProgram prog = category.getStarsEnrLMProgram(k);
					if (prog.getProgramID() == starsApps[i].getLmProgramID()) {
						enrProg = prog;
						break;
					}
				}
				break;
			}
		}
%>
                  <tr> 
                    <td width="5%" height="2"> 
                      <input type="checkbox" name="AppID" value="<%= starsApps[i].getApplianceID() %>" checked onClick="changeAppSelection(this)">
                    </td>
                    <td width="45%" class="TableCell" height="2"><%= program.getProgramName() %></td>
                    <td width="50%" height="2"> 
                      <select id="Group_App<%= starsApps[i].getApplianceID() %>" name="GroupID">
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
%>
                  <%
	for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
		StarsAppliance appliance = appliances.getStarsAppliance(i);
		if (appliance.getInventoryID() == 0 && appliance.getLmProgramID() > 0) {
			StarsEnrLMProgram program = null;
			for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
				StarsApplianceCategory category = categories.getStarsApplianceCategory(j);
				if (category.getApplianceCategoryID() == appliance.getApplianceCategoryID()) {
					for (int k = 0; k < category.getStarsEnrLMProgramCount(); k++) {
						StarsEnrLMProgram prog = category.getStarsEnrLMProgram(k);
						if (prog.getProgramID() == appliance.getLmProgramID()) {
							program = prog;
							break;
						}
					}
					break;
				}
			}
			boolean disabled = (program == null || program.getAddressingGroupCount() == 0);
%>
                  <tr> 
                    <td width="5%" height="2"> 
                      <input type="checkbox" name="AppID" value="<%= appliance.getApplianceID() %>" onClick="changeAppSelection(this)"
							 <%= (disabled)? "disabled" : "" %>>
                    </td>
                    <td width="45%" class="TableCell" height="2"><%= program.getProgramName() %></td>
                    <td width="50%" height="2"> 
                      <select id="Group_App<%= appliance.getApplianceID() %>" name="GroupID" disabled="true">
                        <%
			if (disabled) {
%>
                        <option value="0">(none)</option>
                        <%
			} else {
				for (int j = 0; j < program.getAddressingGroupCount(); j++) {
					AddressingGroup group = program.getAddressingGroup(j);
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
	}
%>
                </table>
                <br>
                <table width="350" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="center"> 
                      <input type="submit" name="UpdateLMHardwareConfig" value="Config">
                      <input type="button" name="SaveConfig" value="Save Config Only" onClick="sendCommand('SaveLMHardwareConfig')">
                    </td>
                  </tr>
                </table>
                <br>
                <table width="300" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td align="center"> 
                      <input type="button" name="Enable" value="Reenable" onClick="sendCommand('EnableLMHardware')">
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
	for (int i = 0; i < starsApps.length; i++) {
		StarsLMProgram program = null;
		for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
			StarsLMProgram starsProg = programs.getStarsLMProgram(j);
			if (starsProg.getProgramID() == starsApps[i].getLmProgramID()) {
				program = starsProg;
				break;
			}
		}
		
		StarsApplianceCategory category = null;
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			if (appCat.getApplianceCategoryID() == starsApps[i].getApplianceCategoryID()) {
				category = appCat;
				break;
			}
		}
%>
                <tr bgcolor="#FFFFFF" valign="top"> 
                  <td width="104" class="TableCell"> <%= starsApps[i].getDescription() %></td>
                  <td width="100" class="TableCell"><%= program.getStatus() %></td>
                  <td width="120"> 
                    <div align="center"> <img src="../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>"><br>
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
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
