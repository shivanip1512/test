<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.YukonListFuncs" %>
<%
	boolean inWizard = request.getParameter("Wizard") != null;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	boolean invChecking = AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.INVENTORY_CHECKING);
	if (!invChecking)
		session.removeAttribute(InventoryManager.STARS_INVENTORY_TEMP + "_NEW");
	
	StarsInventory inventory = (StarsInventory) session.getAttribute(InventoryManager.STARS_INVENTORY_TEMP + "_NEW");
	
	if (inventory == null && inWizard) {
		MultiAction actions = (MultiAction) session.getAttribute(ServletUtils.ATT_NEW_ACCOUNT_WIZARD);
		if (actions != null) {
			SOAPMessage reqMsg = actions.build(request, session);
			if (reqMsg != null) {
				StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation(reqMsg);
				StarsCreateLMHardware createHw = reqOper.getStarsCreateLMHardware();
				if (createHw != null) {
					inventory = (StarsInventory) StarsFactory.newStarsInv(createHw, StarsInventory.class);
					inventory.getDeviceType().setContent(
							YukonListFuncs.getYukonListEntry(createHw.getDeviceType().getEntryID()).getEntryText());
				}
			}
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
	
	String devTypeStr = "(none)";
	String serialNo = "(none)";
	String serialLabel = "(none)";
	
	if (inventory.getLMHardware() != null) {
		devTypeStr = inventory.getDeviceType().getContent();
		serialNo = inventory.getLMHardware().getManufacturerSerialNumber();
		serialLabel = "Serial #";
	}
	else if (inventory.getDeviceID() > 0) {
		com.cannontech.database.data.lite.LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO(inventory.getDeviceID());
		devTypeStr = com.cannontech.database.data.pao.PAOGroups.getPAOTypeString(litePao.getType());
		serialNo = litePao.getPaoName();
		serialLabel = "Device Name";
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function changeAppSelection(chkBox) {
	var grpList = document.getElementById('Group_App' + chkBox.value);
	grpList.disabled = !chkBox.checked;
}

function validate(form) {
	if (form.SerialNo.value == "") {
		alert("Serial # cannot be empty");
		return false;
	}
<% if (!invChecking) { %>
	form.attributes["action"].value = "<%= request.getContextPath() %>/servlet/InventoryManager";
<% } %>
	return true;
}

function changeSerialNo() {
	document.snForm.submit();
}

function confirmCancel() {
	if (confirm("Are you sure you want to quit from this wizard and discard all changes you've been made?"))
		location.href = "../Operations.jsp";
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
<% if (!inWizard) { %>
		    <% String pageName = "CreateHardware.jsp"; %>
			<%@ include file="include/Nav.jsp" %>
<% } %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "MainText" align="center">
              <% String header = "CREATE NEW HARDWARE"; %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jsp" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
<% } %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
			    <input type="hidden" name="action" value="CreateLMHardware">
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
<%	if (invChecking) { %>
							<input type="hidden" name="DeviceType" value="<%= inventory.getDeviceType().getEntryID() %>">
							<input type="hidden" name="SerialNo" value="<%= serialNo %>">
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="100" class="TableCell" align="right">Type: 
                                </td>
                                <td width="120" class="MainText"><%= devTypeStr %></td>
                                <td width="80" rowspan="2">
                                  <input type="button" name="Change" value="Change" onclick="changeSerialNo()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell" align="right"><%= serialLabel %>: </td>
                                <td width="120" class="MainText"><%= serialNo %></td>
                              </tr>
                            </table>
<%	} %>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
<%	if (!invChecking) { %>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Type: </div>
                                </td>
                                <td width="200"> 
                                  <select name="DeviceType">
                                    <%
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
%>
                                    <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell" align="right">Serial 
                                  #: </td>
                                <td width="200"> 
                                  <input type="text" name="SerialNo" maxlength="30" size="24" value="">
                                </td>
                              </tr>
<%	} %> 
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Label: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" value="<%= inventory.getDeviceLabel() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Alt Tracking #: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" value="<%= inventory.getAltTrackingNumber() %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Receive Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getReceiveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Remove Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getRemoveDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Voltage: </div>
                                </td>
                                <td width="200"> 
                                  <select name="Voltage">
                                    <%
	StarsCustSelectionList voltageList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
	for (int i = 0; i < voltageList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = voltageList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == inventory.getVoltage().getEntryID())? "selected" : "";
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
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= inventory.getNotes() %></textarea>
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
                                  <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= ServletUtils.formatDate(inventory.getInstallDate(), datePart) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Service Company: </div>
                                </td>
                                <td width="200"> 
                                  <select name="ServiceCompany">
<%
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany company = companies.getStarsServiceCompany(i);
		String selected = (company.getCompanyID() == inventory.getInstallationCompany().getEntryID())? "selected" : "";
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
                                  <textarea name="InstallNotes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= inventory.getInstallationNotes() %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
<%
	if (inventory.getLMHardware() != null && appliances != null) {
%>
                      <br>
                      <span class="Subtext">Select all programs controlled by this 
                      hardware, and assign groups to them:</span><br>
                      <table width="300" border="1" cellspacing="0" cellpadding="3">
                        <tr> 
                          <td width="10%" class="HeaderCell">&nbsp; </td>
                          <td width="40%" class="HeaderCell">Program</td>
                          <td width="50%" class="HeaderCell">Assigned Group</td>
                        </tr>
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
                          <td width="27" height="2"> 
                            <input type="checkbox" name="AppID" value="<%= appliance.getApplianceID() %>" onclick="changeAppSelection(this)"
							 <%= (disabled)? "disabled" : "" %>>
                          </td>
                          <td width="73" class="TableCell" height="2"><%= program.getProgramName() %></td>
                          <td width="89" height="2"> 
                            <select id="Group_App<%= appliance.getApplianceID() %>" name="GroupID" disabled>
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
<%	} %>
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
                      <input type="button" name="Back" value="Back" onclick="location.href = 'New.jsp?Wizard=true'">
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
                      <input type="reset" name="Cancel" value="Reset">
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
