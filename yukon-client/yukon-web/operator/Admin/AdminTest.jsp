<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.util.CtiUtilities" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.YukonUserFuncs" %>
<%@ page import="com.cannontech.roles.consumer.ResidentialCustomerRole" %>
<%	if (!AuthFuncs.checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY)
		|| ecSettings == null) {
		response.sendRedirect("../Operations.jsp"); return;
	}
%>
<%
	com.cannontech.database.data.lite.LiteYukonGroup[] custGroups = liteEC.getResidentialCustomerGroups();
	
	ArrayList members = liteEC.getChildren();
	ArrayList memberCandidates = new ArrayList();
	if (AuthFuncs.checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MANAGE_MEMBERS)) {
		ArrayList energyCompanies = StarsDatabaseCache.getInstance().getAllEnergyCompanies();
		for (int i = 0; i < energyCompanies.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) energyCompanies.get(i);
			if (ECUtils.isDefaultEnergyCompany(company)) continue;	// exclude default energy company
			if (members.contains(company)) continue;	// exclude existing members
			if (ECUtils.getAllDescendants(company).contains(liteEC)) continue;	// prevent circular reference
			memberCandidates.add(company);
		}
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function confirmDeleteAccount(form) {
	if (form.AcctNo.value == "*")
		return confirm("Are you sure you want to delete all customer accounts?");
	return true;
}

function confirmDeleteAppCat() {
	return confirm("If you delete the appliance category, all the programs and customer appliances under this category will also be deleted. Are you sure you want to continue?");
}

function confirmDeleteAllAppCats() {
	return confirm("Are you sure you want to delete all appliance categories, programs, and customer appliances under these categories?");
}

function editServiceCompany(form, compIdx) {
	form.attributes["action"].value = 'ServiceCompany.jsp?Company=' + compIdx;
	form.action.value = "init";
	form.submit();
}

function confirmDeleteCompany() {
	return confirm("Are you sure you want to delete the service company?");
}

function confirmDeleteAllCompanies() {
	return confirm("Are you sure you want to delete all service companies?");
}

function confirmDeleteOperatorLogin() {
	return confirm("Are you sure you want to delete the operator login?");
}

function confirmDeleteAllOperatorLogins() {
	return confirm("Are you sure you want to delete all operator logins (except the default login)?");
}

var memberLoginList = new Array(<%= memberCandidates.size() %>);
<%
	for (int i = 0; i < memberCandidates.size(); i++) {
		LiteStarsEnergyCompany candidate = (LiteStarsEnergyCompany) memberCandidates.get(i);
		ArrayList loginIDs = candidate.getOperatorLoginIDs();
%>
memberLoginList[<%= i %>] = new Array(<%= loginIDs.size() %>);
<%
		for (int j = 0; j < loginIDs.size(); j++) {
			int loginID = ((Integer) loginIDs.get(j)).intValue();
			LiteYukonUser login = YukonUserFuncs.getLiteYukonUser(loginID);
%>
memberLoginList[<%= i %>][<%= j %>] = new Array(2);
memberLoginList[<%= i %>][<%= j %>][0] = <%= loginID %>;
memberLoginList[<%= i %>][<%= j %>][1] = '<%= login.getUsername() %>';
<%
		}
	}
%>

function updateMemberLoginList(form) {
	var idx = form.NewMember.selectedIndex - 1;
	for (i = form.NewMemberLogin.options.length - 1; i > 0; i--)
		form.NewMemberLogin.options.remove(i);
	if (idx >= 0 && idx < memberLoginList.length) {
		for (i = 0; i < memberLoginList[idx].length; i++) {
			form.NewMemberLogin.options.add(document.createElement("OPTION"));
			form.NewMemberLogin.options[i+1].value = memberLoginList[idx][i][0];
			form.NewMemberLogin.options[i+1].innerText = memberLoginList[idx][i][1];
		}
	}
}

function addMember(form) {
	if (form.NewMember.value == -1) {
		alert("Please select an energy company from the list");
		return;
	}
	form.MemberID.value = form.NewMember.value;
	form.LoginID.value = form.NewMemberLogin.value;
	form.action.value = "AddMemberEnergyCompany";
	form.submit();
}

function updateMember(form, idx) {
	form.MemberID.value = document.getElementsByName("Member")[idx].value;
	form.LoginID.value = document.getElementsByName("MemberLogin")[idx].value;
	form.submit();
}

function removeMember(form, idx) {
	if (!confirm("Are you sure you want to remove the member?"))
		return;
	form.MemberID.value = document.getElementsByName("Member")[idx].value;
	form.action.value = "RemoveMemberEnergyCompany";
	form.submit();
}

function removeAllMembers(form) {
	if (!confirm("Are you sure you want to remove all members?"))
		return;
	form.MemberID.value = -1;
	form.action.value = "RemoveMemberEnergyCompany";
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
            <br>
            <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td class="HeaderCell">Edit Energy Company Settings</td>
              </tr>
              <tr> 
                <td height="118"> 
                  <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                    <tr> 
                      <td> 
                        <form name="form6" method="post" action="EnergyCompany.jsp">
                          <input type="hidden" name="action" value="init">
                          <b><font color="#0000FF">Energy Company:</font></b> 
                          <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                  <tr> 
                                    <td width="5%">&nbsp;</td>
                                    <td width="70%"><%= energyCompany.getCompanyName() %></td>
                                    <td width="25%"> 
                                      <input type="submit" name="Edit3" value="Edit">
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
<cti:checkNoProperty propertyid="<%= EnergyCompanyRole.SINGLE_ENERGY_COMPANY %>">
                    <tr> 
                      <td><b><font color="#0000FF">Routes:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr> 
                                  <td class="TableCell" width="5%">&nbsp;</td>
                                  <td class="TableCell" width="70%"> 
<%
	LiteYukonPAObject[] inheritedRoutes = null;
	if (liteEC.getParent() != null)
		inheritedRoutes= liteEC.getParent().getAllRoutes();
	LiteYukonPAObject[] routes = liteEC.getRoutes(inheritedRoutes);
	
	for (int i = 0; i < routes.length && i < 3; i++) {
%>
                                    <%= routes[i].getPaoName() %><br>
<%
	}
	if (routes.length < 3 && inheritedRoutes != null) {
		for (int i = 0; i < inheritedRoutes.length && i < 3 - routes.length; i++) {
%>
                                    <%= inheritedRoutes[i].getPaoName() %> (Inherited)<br>
<%
		}
	}
	if (routes.length > 3) {
%>
                                    And more...<br>
<%
	}
%>
                                  </td>
                                  <td width="25%" class="TableCell"> 
                                    <input type="button" name="Edit" value="Edit" onClick="location.href='Routes.jsp'">
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                    </tr>
</cti:checkNoProperty>
                    <tr> 
                      <td> 
                        <form name="form3" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
                          <b><font color="#0000FF">Appliance Categories &amp; 
                          Published Programs:</font></b> 
                          <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="3">
                                  <input type="hidden" name="action" value="DeleteApplianceCategory">
                                  <input type="hidden" name="AppCatID" value="0">
                                  <%
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
%>
                                  <tr> 
                                    <td width="15%" class="TableCell" align="center"> 
                                      <%
		if (!category.getStarsWebConfig().getLogoLocation().equals("")) {
%>
                                      <img src="../../WebConfig/<%= category.getStarsWebConfig().getLogoLocation() %>"> 
                                      <%
		}
%>
                                    </td>
                                    <td width="60%" class="TableCell" valign="top"><%= category.getDescription() %> 
                                      <%
		if (!category.getStarsWebConfig().getAlternateDisplayName().equals( category.getDescription() )) {
%>
                                      (<%= category.getStarsWebConfig().getAlternateDisplayName() %>) 
                                      <%
		}
%>
                                      <table width="100%" border="0" cellspacing="3" cellpadding="0">
                                        <%
		for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
			StarsEnrLMProgram program = category.getStarsEnrLMProgram(j);
			String[] dispNames = StarsUtils.splitString(program.getStarsWebConfig().getAlternateDisplayName(), ",");
			
			String progName = program.getYukonName();
			if (progName == null) progName = "&lt;Virtual Program&gt;";
			
			String progAlias = "";
			if (dispNames.length > 0) {
				if (dispNames[0].length() > 0) {
					progAlias += dispNames[0];
					if (dispNames.length > 1 && dispNames[1].length() > 0)
						progAlias += " / ";
				}
				if (dispNames.length > 1 && dispNames[1].length() > 0)
					progAlias += dispNames[1];
			}
			if (progAlias.length() > 0) progAlias = "(" + progAlias + ")";
%>
                                        <tr> 
                                          <td width="15" class="TableCell">&nbsp;</td>
                                          <td width="458" class="TableCell"><%= progName %> 
                                            <%= progAlias %></td>
                                        </tr>
                                        <%
		}
%>
                                      </table>
                                    </td>
                                    <%
		if (category.getInherited()) {
%>
                                    <td width="25%" class="TableCell">
                                      <input type="button" name="Edit9" value="View" onClick="location.href='ApplianceCategory.jsp?Category=<%= i %>'">
                                      (Inherited) </td>
                                    <%
		} else {
%>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Edit" value="Edit" onclick="location.href='ApplianceCategory.jsp?Category=<%= i %>'">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="submit" name="Delete" value="Delete" onclick="this.form.AppCatID.value=<%= category.getApplianceCategoryID() %>; return confirmDeleteAppCat();">
                                    </td>
                                    <%
		}
%>
                                  </tr>
                                  <%
	}
%>
                                </table>
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td width="20%"> 
                                <input type="submit" name="DeleteAll" value="Delete All" onclick="this.form.AppCatID.value=-1; return confirmDeleteAllAppCats();">
                              </td>
                              <td width="80%"> 
                                <input type="button" name="New" value="New" onclick="location.href='ApplianceCategory.jsp?Category=<%= categories.getStarsApplianceCategoryCount() %>'">
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
                    <%
	if (AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.CONSUMER_INFO_HARDWARES) ||
		AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.CONSUMER_INFO_WORK_ORDERS))
	{
%>
                    <tr> 
                      <td> 
                        <form name="form4" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
                          <b><font color="#0000FF">Service Companies:</font></b> 
                          <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                  <input type="hidden" name="action" value="DeleteServiceCompany">
                                  <input type="hidden" name="CompanyID" value="0">
                                  <%
		// The first service company is always "(none)"
		for (int i = 1; i < companies.getStarsServiceCompanyCount(); i++) {
			StarsServiceCompany company = companies.getStarsServiceCompany(i);
%>
                                  <tr> 
                                    <td class="TableCell" width="5%">&nbsp;</td>
                                    <td class="TableCell" width="70%"><%= company.getCompanyName() %></td>
<%
			if (company.getInherited()) {
%>
                                    <td width="25%" class="TableCell">
                                      <input type="button" name="Edit8" value="View" onClick="editServiceCompany(this.form, <%= i %>)">
                                      (Inherited) </td>
<%
			} else {
%>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Edit" value="Edit" onclick="editServiceCompany(this.form, <%= i %>)">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="submit" name="Delete" value="Delete" onclick="this.form.CompanyID.value=<%= company.getCompanyID() %>; return confirmDeleteCompany();">
                                    </td>
<%
			}
%>
                                  </tr>
                                  <%
		}
%>
                                </table>
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td width="20%"> 
                                <input type="submit" name="DeleteAll" value="Delete All" onclick="this.form.CompanyID.value=-1; return confirmDeleteAllCompanies();">
                              </td>
                              <td width="80%"> 
                                <input type="button" name="New" value="New" onclick="editServiceCompany(this.form, <%= companies.getStarsServiceCompanyCount() %>)">
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
                    <%
	}
	
	if (AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.CONSUMER_INFO_ADMIN_FAQ) ||
		custGroups.length > 0 && !CtiUtilities.isFalse(AuthFuncs.getRolePropValueGroup(custGroups[0], ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ, "false")))
	{
%>
                    <tr> 
                      <td> <b><font color="#0000FF">Customer FAQs:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr> 
                                  <td class="TableCell" width="5%">&nbsp;</td>
                                  <%
		String faqLink = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LINK_FAQ);
		boolean customizedFAQ = StarsUtils.forceNotNone(faqLink).length() > 0;
%>
                                  <td class="TableCell" width="15%"> 
                                    <%
		if (customizedFAQ) {
%>
                                    FAQ Link: 
                                    <%
		} else {
%>
                                    FAQ Subjects: 
                                    <%		}
%>
                                  </td>
                                  <td class="TableCell" width="55%"> 
                                    <%
		if (customizedFAQ) {
%>
                                    <%= faqLink %> 
                                    <%
		}
		else {
			for (int i = 0; i < customerFAQs.getStarsCustomerFAQGroupCount(); i++) {
				StarsCustomerFAQGroup faqGroup = customerFAQs.getStarsCustomerFAQGroup(i);
%>
                                    <%= faqGroup.getSubject() %><br>
                                    <%
			}
		}
%>
                                  </td>
                                  <td width="25%" class="TableCell"> 
                                    <input type="button" name="Edit6" value="Edit" onClick="location.href='CustomerFAQ.jsp'">
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                    </tr>
                    <%
	}
	
	if (AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT) ||
		custGroups.length > 0 && !CtiUtilities.isFalse(AuthFuncs.getRolePropValueGroup(custGroups[0], ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT, "false")))
	{
%>
                    <tr> 
                      <td><b><font color="#0000FF">Interview Questions:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr> 
                                  <td class="TableCell" width="5%">&nbsp;</td>
                                  <td class="TableCell" width="15%">Exit Interview:</td>
                                  <td class="TableCell" width="55%"> 
                                    <%
		for (int i = 0; i < exitQuestions.getStarsExitInterviewQuestionCount(); i++) {
			StarsExitInterviewQuestion question = exitQuestions.getStarsExitInterviewQuestion(i);
			String qStr = (question.getQuestion().length() <= 50) ? question.getQuestion() : question.getQuestion().substring(0,47).concat("...");
%>
                                    <%= qStr %><br>
                                    <%
		}
%>
                                  </td>
                                  <td width="25%" class="TableCell"> 
                                    <input type="button" name="Edit7" value="Edit" onClick="location.href='InterviewQuestion.jsp?Type=Exit'">
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                    </tr>
                    <%
	}
	
	if (AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.CONSUMER_INFO_HARDWARES_THERMOSTAT) ||
		custGroups.length > 0 && !CtiUtilities.isFalse(AuthFuncs.getRolePropValueGroup(custGroups[0], ResidentialCustomerRole.CONSUMER_INFO_HARDWARES_THERMOSTAT, "false")))
	{
%>
                    <tr> 
                      <td><b><font color="#0000FF">Default Thermostat Schedule:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                <%
		for (int i = 0; i < dftThermoSchedules.getStarsThermostatProgramCount(); i++) {
			StarsThermostatTypes type = dftThermoSchedules.getStarsThermostatProgram(i).getThermostatType();
			
			String typeName = "";
			if (type.getType() == StarsThermostatTypes.EXPRESSSTAT_TYPE)
				typeName = "ExpressStat";
			else if (type.getType() == StarsThermostatTypes.COMMERCIAL_TYPE)
				typeName = "Commercial ExpressStat";
			else if (type.getType() == StarsThermostatTypes.ENERGYPRO_TYPE)
				typeName = "EnergyPro";
%>
                                <tr> 
                                  <td width="5%">&nbsp;</td>
                                  <td width="70%"><%= typeName %></td>
                                  <td width="25%"> 
                                    <input type="button" name="Edit2" value="Edit" onclick="location.href = 'ThermSchedule.jsp?type=<%= type.toString() %>'">
                                  </td>
                                </tr>
                                <%
		}
%>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                    </tr>
                    <%
	}
%>
                    <tr> 
                      <td><b><font color="#0000FF">Customer Selection Lists:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="1" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <%
	ArrayList userLists = liteEC.getAllSelectionLists(user);
	for (int i = 0; i < userLists.size(); i++) {
		com.cannontech.common.constants.YukonSelectionList cList = (com.cannontech.common.constants.YukonSelectionList) userLists.get(i);
		if (cList.getUserUpdateAvailable() == null || !cList.getUserUpdateAvailable().equalsIgnoreCase("Y")) continue;
%>
                                <tr> 
                                  <td class="TableCell" width="5%">&nbsp;</td>
                                  <td class="TableCell" width="30%"><%= cList.getListName() %></td>
                                  <td class="TableCell" width="40%"> 
                                    <hr width="90%" align="left">
                                  </td>
                                  <td class="TableCell" width="25%"> 
                                    <input type="button" name="Edit" value="Edit" onclick="location.href='SelectionList.jsp?List=<%= cList.getListName() %>'">
                                    <% if (liteEC.getYukonSelectionList(cList.getListName(), false, false) == null) out.print("(Inherited)"); %>
                                  </td>
                                </tr>
                                <%
	}
%>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                    </tr>
                    <tr> 
                      <td> 
                        <form name="form6" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
                          <b><font color="#0000FF">Operator Logins:</font></b> 
                          <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                  <input type="hidden" name="action" value="DeleteOperatorLogin">
                                  <input type="hidden" name="UserID" value="-1">
                                  <tr> 
                                    <td class="HeaderCell" width="5%">&nbsp;</td>
                                    <td class="HeaderCell" width="30%">Login Name</td>
                                    <td class="HeaderCell" width="40%">Operator 
                                      Group(s)</td>
                                    <td width="10%" class="HeaderCell">&nbsp;</td>
                                    <td width="15%" class="HeaderCell">&nbsp;</td>
                                  </tr>
                                  <%
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	Map userGroupMap = cache.getYukonUserGroupMap();
	
	List userGroups = (List) userGroupMap.get(lYukonUser);
	String groupNames = "";
	for (int i = 0; i < userGroups.size(); i++) {
		com.cannontech.database.data.lite.LiteYukonGroup liteGroup = (com.cannontech.database.data.lite.LiteYukonGroup) userGroups.get(i);
		if (liteGroup.getGroupID() == -1) continue;
		if (groupNames.length() > 0) groupNames += ", ";
		groupNames += liteGroup.getGroupName();
	}
%>
                                  <tr> 
                                    <td class="TableCell" width="5%">&nbsp;</td>
                                    <td class="TableCell" width="30%"><%= lYukonUser.getUsername() %></td>
                                    <td class="TableCell" width="40%"><%= groupNames %></td>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Edit4" value="Edit" onclick="location.href='OperatorLogin.jsp?UserID=<%= lYukonUser.getUserID() %>'">
                                    </td>
                                    <td width="15%" class="TableCell">&nbsp;</td>
                                  </tr>
                                  <tr> 
                                    <td class="TableCell" colspan="5"> 
                                      <hr>
                                    </td>
                                  </tr>
                                  <%
	ArrayList operLoginIDs = liteEC.getOperatorLoginIDs();
	for (int i = 0; i < operLoginIDs.size(); i++) {
		int userID = ((Integer) operLoginIDs.get(i)).intValue();
		if (userID == lYukonUser.getUserID()) continue;
		
		LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser(userID);
		if (liteUser == null) continue;
		
		userGroups = (List) userGroupMap.get(liteUser);
		groupNames = "";
		for (int j = 0; j < userGroups.size(); j++) {
			com.cannontech.database.data.lite.LiteYukonGroup liteGroup = (com.cannontech.database.data.lite.LiteYukonGroup) userGroups.get(j);
			if (liteGroup.getGroupID() == -1) continue;
			if (groupNames.length() > 0) groupNames += ", ";
			groupNames += liteGroup.getGroupName();
		}
%>
                                  <tr> 
                                    <td class="TableCell" width="5%">&nbsp;</td>
                                    <td class="TableCell" width="30%"><%= liteUser.getUsername() %></td>
                                    <td class="TableCell" width="40%"><%= groupNames %></td>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Edit5" value="Edit" onclick="location.href='OperatorLogin.jsp?UserID=<%= liteUser.getUserID() %>'">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="submit" name="Delete" value="Delete" onclick="this.form.UserID.value=<%= liteUser.getUserID() %>; return confirmDeleteOperatorLogin();">
                                    </td>
                                  </tr>
                                  <%
	}
%>
                                </table>
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td width="20%"> 
                                <input type="submit" name="DeleteAll" value="Delete All" onclick="this.form.UserID.value=-1; return confirmDeleteAllOperatorLogins();">
                              </td>
                              <td width="80%"> 
                                <input type="button" name="New" value="New" onclick="location.href='OperatorLogin.jsp'">
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
                    <cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_MANAGE_MEMBERS %>"> 
                    <tr> 
                      <td> 
                        <form name="form2" method="post" action="<%= request.getContextPath() %>/servlet/StarsAdmin">
                          <b><font color="#0000FF">Member Energy Companies:</font></b> 
                          <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                  <input type="hidden" name="action" value="UpdateMemberEnergyCompany">
                                  <input type="hidden" name="MemberID" value="-1">
                                  <input type="hidden" name="LoginID" value="-1">
                                  <tr> 
                                    <td class="HeaderCell" width="5%">&nbsp;</td>
                                    <td class="HeaderCell" width="30%">Company 
                                      Name</td>
                                    <td class="HeaderCell" width="40%">Member 
                                      Login </td>
                                    <td width="10%" class="HeaderCell">&nbsp;</td>
                                    <td width="15%" class="HeaderCell">&nbsp;</td>
                                  </tr>
                                  <%
	ArrayList memberLoginIDs = liteEC.getMemberLoginIDs();
	for (int i = 0; i < members.size(); i++) {
		LiteStarsEnergyCompany member = (LiteStarsEnergyCompany) members.get(i);
%>
                                  <tr> 
                                    <input type="hidden" name="Member" value="<%= member.getLiteID() %>">
                                    <td class="TableCell" width="5%">&nbsp;</td>
                                    <td class="TableCell" width="30%"><%= member.getName() %></td>
                                    <td class="TableCell" width="40%"> 
                                      <select name="MemberLogin">
                                        <option value="-1">(none)</option>
                                        <%
		for (int j = 0; j < member.getOperatorLoginIDs().size(); j++) {
			Integer loginID = (Integer) member.getOperatorLoginIDs().get(j);
			LiteYukonUser login = YukonUserFuncs.getLiteYukonUser(loginID.intValue());
			if (login == null) continue;
			String selected = memberLoginIDs.contains(loginID)? "selected" : "";
%>
                                        <option value="<%= loginID %>" <%= selected %>><%= login.getUsername() %></option>
                                        <%
		}
%>
                                      </select>
                                    </td>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Update" value="Update" onclick="updateMember(this.form, <%= i %>)">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="button" name="Remove" value="Remove" onclick="removeMember(this.form, <%= i %>)">
                                    </td>
                                  </tr>
                                  <%
	}
%>
                                  <tr> 
                                    <td class="TableCell" colspan="5"> 
                                      <hr>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td class="TableCell" width="5%">&nbsp;</td>
                                    <td class="TableCell" width="30%"> 
                                      <select name="NewMember" onchange="updateMemberLoginList(this.form)">
                                        <option value="-1">(Select)</option>
                                        <%
	for (int i = 0; i < memberCandidates.size(); i++) {
		LiteStarsEnergyCompany candidate = (LiteStarsEnergyCompany) memberCandidates.get(i);
%>
                                        <option value="<%= candidate.getLiteID() %>"><%= candidate.getName() %></option>
                                        <%
	}
%>
                                      </select>
                                    </td>
                                    <td class="TableCell" width="40%"> 
                                      <select name="NewMemberLogin">
                                        <option value="-1">(Select)</option>
                                      </select>
                                    </td>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Add" value="Add" onclick="addMember(this.form)">
                                    </td>
                                    <td width="15%" class="TableCell">&nbsp; </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td width="20%"> 
                                <input type="button" name="RemoveAll" value="Remove All" onclick="removeAllMembers(this.form)">
                              </td>
                              <td width="80%">&nbsp; </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
                    </cti:checkProperty> 
                  </table>
                </td>
              </tr>
            </table>
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
