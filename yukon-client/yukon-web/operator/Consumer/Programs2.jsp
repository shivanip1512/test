<%@ include file="include/StarsHeader.jsp" %>
<%
	boolean inWizard = request.getParameter("Wizard") != null;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	ArrayList hardwares = new ArrayList();
	if (inventories != null) {
		for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
			if (inventories.getStarsInventory(i).getLMHardware() != null)
				hardwares.add(inventories.getStarsInventory(i));
		}
	}
	
	if (inWizard) {
		MultiAction actions = (MultiAction) session.getAttribute(ServletUtils.ATT_NEW_ACCOUNT_WIZARD);
		if (actions != null) {
			SOAPMessage reqMsg = actions.getRequestMessage( CreateLMHardwareAction.class );
			if (reqMsg != null) {
				StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation(reqMsg);
				StarsCreateLMHardware createHw = reqOper.getStarsCreateLMHardware();
				if (createHw != null) {
					StarsInventory inventory = (StarsInventory) StarsFactory.newStarsInv(createHw, StarsInventory.class);
					hardwares = new ArrayList();
					hardwares.add(inventory);
				}
			}
		}
	}
	
	StarsSULMPrograms suPrograms = null;
	MultiAction actions = (inWizard)?
			(MultiAction) session.getAttribute(ServletUtils.ATT_NEW_ACCOUNT_WIZARD) :
			(MultiAction) session.getAttribute(ServletUtils.ATT_MULTI_ACTIONS);
	if (actions != null) {
		SOAPMessage reqMsg = actions.getRequestMessage( ProgramSignUpAction.class );
		if (reqMsg != null) {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation(reqMsg);
			StarsProgramSignUp progSignUp = reqOper.getStarsProgramSignUp();
			if (progSignUp != null) suPrograms = progSignUp.getStarsSULMPrograms();
		}
	}
	if (suPrograms == null) suPrograms = new StarsSULMPrograms();
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function selectHardware(progNo, hwNo) {
	var checkboxes = document.getElementsByName("InvID" + progNo);
	var loadNoList = document.getElementsByName("LoadNo" + progNo);
	loadNoList[hwNo].disabled = !checkboxes[hwNo].checked;
}

function prepareSubmit(form) {
	var progIDs = document.getElementsByName("ProgID");
	for (i = 0; i < progIDs.length; i++) {
		var checkboxes = document.getElementsByName("InvID" + i);
		var loadNoList = document.getElementsByName("LoadNo" + i);
		var invIDs = document.getElementsByName("InvIDs")[i];
		var loadNos = document.getElementsByName("LoadNos")[i];
		var hwAssigned = false;
		
		for (j = 0; j < checkboxes.length; j++) {
			if (checkboxes[j].checked) {
				if (!hwAssigned) {
					invIDs.value = checkboxes[j].value;
					loadNos.value = loadNoList[j].value;
					hwAssigned = true;
				}
				else {
					invIDs.value += "," + checkboxes[j].value;
					loadNos.value += "," + loadNoList[j].value;
				}
			}
		}
<% if (hardwares.size() > 0) { %>
		if (!hwAssigned) {
			alert("There must be at least one hardware assigned for each enrolled program");
			return false;
		}
<% } %>
	}
	
	return true;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip" style="width: 1003px; height: 20px"></div>
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
<% if (!inWizard) { %>
		  <% String pageName = "Programs.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
<% } %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_ENROLLMENT, "PROGRAMS - ENROLLMENT"); %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jsp" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
<% } %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <table width="80%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td align="center" class="MainText">For each enrolled program, 
                    please select the group and hardware(s) assigned to it.</td>
                </tr>
              </table>
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return prepareSubmit(this);">
			    <input type="hidden" name="action" value="SetAddtEnrollInfo">
			    <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Programs.jsp">
			    <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/Programs2.jsp">
				<% if (inWizard) { %><input type="hidden" name="Wizard" value="true"><% } %>
                <table width="80%" border="1" cellspacing="0" cellpadding="3">
                  <tr align="center"> 
                    <td class="HeaderCell" width="25%">Enrolled Program</td>
                    <td class="HeaderCell" width="25%">Group Assigned</td>
                    <td class="HeaderCell" width="50%">Hardware(s) Assigned</td>
                  </tr>
                  <%
	for (int i = 0; i < suPrograms.getSULMProgramCount(); i++) {
		SULMProgram suProg = suPrograms.getSULMProgram(i);
		StarsLMProgram program = null;
		StarsApplianceCategory category = null;
		StarsEnrLMProgram enrProg = null;
		
		if (!inWizard) {
			for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
				if (programs.getStarsLMProgram(j).getProgramID() == suProg.getProgramID()) {
					program = programs.getStarsLMProgram(j);
					break;
				}
			}
		}
		
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory cat = categories.getStarsApplianceCategory(j);
			if (cat.getApplianceCategoryID() == suProg.getApplianceCategoryID()) {
				category = cat;
				for (int k = 0; k < category.getStarsEnrLMProgramCount(); k++) {
					if (category.getStarsEnrLMProgram(k).getProgramID() == suProg.getProgramID()) {
						enrProg = category.getStarsEnrLMProgram(k);
						break;
					}
				}
				break;
			}
		}
%>
                  <tr> 
                    <input type="hidden" name="ProgID" value="<%= suProg.getProgramID() %>">
					<input type="hidden" name="AppCatID" value="<%= suProg.getApplianceCategoryID() %>">
                    <input type="hidden" name="InvIDs" value="">
                    <input type="hidden" name="LoadNos" value="">
                    <td width="25%" class="TableCell"><%= ServletUtils.getProgramDisplayNames(enrProg)[0] %></td>
                    <td width="25%" class="TableCell"> 
                      <select name="GroupID">
                        <%
		for (int j = 0; j < enrProg.getAddressingGroupCount(); j++) {
			AddressingGroup group = enrProg.getAddressingGroup(j);
			String selected = (program != null && program.getGroupID() == group.getEntryID())? "selected" : "";
%>
                        <option value="<%= group.getEntryID() %>" <%= selected %>><%= group.getContent() %></option>
                        <%
		}
%>
                      </select>
                    </td>
                    <td width="50%" class="TableCell"> 
                      <table width="100%" border="0" cellspacing="0" cellpadding="1" class="TableCell">
                        <%
		for (int j = 0; j < hardwares.size(); j++) {
			StarsInventory hardware = (StarsInventory) hardwares.get(j);
			
			String label = hardware.getDeviceLabel();
			if (label.equals("")) label = hardware.getLMHardware().getManufacturerSerialNumber();
			
			boolean checked = false;
			int loadNo = 0;
			if (hardwares.size() == 1) checked = true;
			
			if (!inWizard) {
				for (int k = 0; k < appliances.getStarsApplianceCount(); k++) {
					StarsAppliance app = appliances.getStarsAppliance(k);
					if (app.getInventoryID() == hardware.getInventoryID() &&
						(app.getProgramID() == suProg.getProgramID() || app.getApplianceCategoryID() == suProg.getApplianceCategoryID()))
					{
						checked = true;
						loadNo = app.getLoadNumber();
						break;
					}
				}
			}
%>
                        <tr> 
                          <td width="5%" align="right"> 
                            <input type="checkbox" name="InvID<%= i %>" value="<%= hardware.getInventoryID() %>"
							  onClick="selectHardware(<%= i %>, <%= j %>)" <%= (checked)?"checked":"" %>>
                          </td>
                          <td width="50%"><%= label %></td>
                          <td width="45%">Relay
<select name="LoadNo<%= i %>" <%= (checked)?"":"disabled" %>>
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
%>
                      </table>
                    </td>
                  </tr>
                  <%
	}
%>
                </table>
                <br>
                <table width="50%" border="0" cellspacing="0" cellpadding="5" align="center">
                  <tr> 
                    <td width="50%" align="right"> 
                      <input type="submit" name="Submit" value="Submit">
                    </td>
                    <td width="50%" align="left"> 
					  <input type="button" name="Cancel" value="Cancel" onclick="history.back()">
                    </td>
                  </tr>
                </table>
                </form>
              <p>&nbsp;</p>
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
