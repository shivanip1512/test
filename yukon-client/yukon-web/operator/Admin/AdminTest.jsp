<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.util.CtiUtilities" %>
<%@ page import="com.cannontech.roles.consumer.ResidentialCustomerRole" %>
<%	if (!AuthFuncs.checkRoleProperty(lYukonUser, com.cannontech.roles.operator.AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY)
		|| ecSettings == null) {
		response.sendRedirect("../Operations.jsp"); return;
	}
%>
<%
	com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany liteEnergyCompany = SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
	com.cannontech.database.data.lite.LiteYukonGroup liteCustGroup = liteEnergyCompany.getResidentialCustomerGroup();
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
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
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/AdminImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Administration</td>
                  
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="return confirmDeleteAccount(this)">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Delete Customer Accounts</td>
                </tr>
                <tr> 
                  <td> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
					  <input type="hidden" name="action" value="DeleteCustAccounts">
                      <tr> 
                        <td width="75%" class="TableCell">Please enter the account 
                          # to be deleted (* to delete all accounts): 
                          <input type="text" name="AcctNo" maxlength="40" size="14">
                        </td>
                        <td width="25%">
                          <input type="submit" name="Submit" value="Submit">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </form>
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
<%		if (!category.getStarsWebConfig().getLogoLocation().equals("")) { %>
									  <img src="../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>">
<%		} %>
									</td>
                                    <td width="60%" class="TableCell" valign="top"><%= category.getDescription() %> 
<%		if (!category.getStarsWebConfig().getAlternateDisplayName().equals( category.getDescription() )) { %>
                                      (<%= category.getStarsWebConfig().getAlternateDisplayName() %>) 
<%		} %>
                                      <table width="100%" border="0" cellspacing="3" cellpadding="0">
<%
		for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
			StarsEnrLMProgram program = category.getStarsEnrLMProgram(j);
			String[] dispNames = program.getStarsWebConfig().getAlternateDisplayName().split(",");
			String progAlias = "";
			if (dispNames.length > 0) {
				progAlias += "(";
				if (dispNames[0].trim().length() > 0) {
					progAlias += dispNames[0];
					if (dispNames.length > 1 && dispNames[1].trim().length() > 0)
						progAlias += " / ";
				}
				if (dispNames.length > 1 && dispNames[1].trim().length() > 0)
					progAlias += dispNames[1];
				progAlias += ")";
			}
%>
                                        <tr> 
                                          <td width="15" class="TableCell">&nbsp;</td>
                                          <td width="458" class="TableCell"><%= program.getProgramName() %> 
                                            <%= progAlias %></td>
                                        </tr>
                                        <%		} %>
                                      </table>
                                    </td>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Edit" value="Edit" onclick="location.href='ApplianceCategory.jsp?Category=<%= i %>'">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="submit" name="Delete" value="Delete" onclick="this.form.AppCatID.value=<%= category.getApplianceCategoryID() %>; return confirmDeleteAppCat();">
                                    </td>
                                  </tr>
                                  <%	} %>
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
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Edit" value="Edit" onclick="editServiceCompany(this.form, <%= i %>)">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="submit" name="Delete" value="Delete" onclick="this.form.CompanyID.value=<%= company.getCompanyID() %>; return confirmDeleteCompany();">
                                    </td>
                                  </tr>
<%		} %>
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
                                <input type="button" name="New" value="New" onclick="location.href='ServiceCompany.jsp?Company=<%= companies.getStarsServiceCompanyCount() %>'">
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
<%
	}
	
	if (AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.CONSUMER_INFO_ADMIN_FAQ) ||
		!CtiUtilities.isFalse(AuthFuncs.getRolePropValueGroup(liteCustGroup, ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ, "false")))
	{
%>
                    <tr> 
                      <td width="75%"> 
                        <form name="form5" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
                          <b><font color="#0000FF">Customer FAQs:</font></b> 
                          <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                  <tr> 
<%
		String faqLink = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LINK_FAQ);
		boolean customizedFAQ = ServerUtils.forceNotNone(faqLink).length() > 0;
%>
                                    <td class="TableCell" width="15%">
                                      <% if (customizedFAQ) { %>FAQ Link:<% } else { %>FAQ Subjects:<% } %>
									</td>
                                    <td class="TableCell" width="60%">
<%		if (customizedFAQ) { %>
                                      <%= faqLink %>
<%		} else { %>
									  <ul>
<%			for (int i = 0; i < customerFAQs.getStarsCustomerFAQGroupCount(); i++) {
				StarsCustomerFAQGroup faqGroup = customerFAQs.getStarsCustomerFAQGroup(i);
%>
                                        <li><%= faqGroup.getSubject() %></li>
<%			} %>
                                      </ul>
<%		} %>
                                    </td>
                                    <td width="25%" class="TableCell"> 
                                      <input type="button" name="Edit" value="Edit" onclick="location.href='CustomerFAQ.jsp'">
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
<%
	}
	
	if (AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT) ||
		!CtiUtilities.isFalse(AuthFuncs.getRolePropValueGroup(liteCustGroup, ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT, "false")))
	{
%>
                    <tr> 
                      <td> <b><font color="#0000FF">Interview Questions:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr> 
                                  <td class="TableCell" width="15%">Exit Interview:</td>
                                  <td class="TableCell" width="60%"> 
                                    <ul>
<%
		for (int i = 0; i < exitQuestions.getStarsExitInterviewQuestionCount(); i++) {
			StarsExitInterviewQuestion question = exitQuestions.getStarsExitInterviewQuestion(i);
			String qStr = (question.getQuestion().length() <= 50) ? question.getQuestion() : question.getQuestion().substring(0,47).concat("...");
%>
                                      <li><%= qStr %></li>
<%		} %>
                                    </ul>
                                  </td>
                                  <td width="25%" class="TableCell"> 
                                    <input type="button" name="Edit" value="Edit" onclick="location.href='InterviewQuestion.jsp?Type=Exit'">
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
		!CtiUtilities.isFalse(AuthFuncs.getRolePropValueGroup(liteCustGroup, ResidentialCustomerRole.CONSUMER_INFO_HARDWARES_THERMOSTAT, "false")))
	{
%>
                    <tr>
                      <td><b><font color="#0000FF">Default Thermostat Schedule:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                          <tr>
                            <td>
                              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
<%
		for (int i = 0; i < allDftThermoSettings.length; i++) {
			StarsThermostatTypes type = allDftThermoSettings[i].getThermostatType();
			String typeName = "";
			String url = "";
			if (type.getType() == StarsThermostatTypes.EXPRESSSTAT_TYPE) {
				typeName = "ExpressStat";
				url = "ThermSchedule.jsp";
			}
			else if (type.getType() == StarsThermostatTypes.COMMERCIAL_TYPE) {
				typeName = "Commercial ExpressStat";
				url = "ThermSchedule1.jsp";
			}
			else if (type.getType() == StarsThermostatTypes.ENERGYPRO_TYPE) {
				typeName = "EnergyPro";
				url = "ThermSchedule2.jsp";
			}
%>
                                <tr> 
                                  <td width="5%">&nbsp;</td>
                                  <td width="70%"><%= typeName %></td>
                                  <td width="25%"> 
                                    <input type="button" name="Edit2" value="Edit" onclick="location.href = '<%= url %>'">
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
	ArrayList userLists = liteEnergyCompany.getAllSelectionLists(user);
	for (int i = 0; i < userLists.size(); i++) {
		com.cannontech.common.constants.YukonSelectionList cList = (com.cannontech.common.constants.YukonSelectionList) userLists.get(i);
		if (cList.getUserUpdateAvailable() == null || !cList.getUserUpdateAvailable().equalsIgnoreCase("Y")) continue;
		
		StarsCustSelectionList list = (StarsCustSelectionList) selectionListTable.get( cList.getListName() );
		if (list == null) continue;
%>
                                <tr> 
                                  <td class="TableCell" width="5%">&nbsp;</td>
                                  <td class="TableCell" width="70%"><%= list.getListName() %></td>
                                  <td class="TableCell"> 
                                    <input type="button" name="Edit" value="Edit" onclick="location.href='SelectionList.jsp?List=<%= list.getListName() %>'">
                                  </td>
                                </tr>
                                <%	} %>
                              </table>
                            </td>
                          </tr>
                        </table>
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
	ArrayList operLoginIDs = liteEnergyCompany.getOperatorLoginIDs();
	for (int i = 0; i < operLoginIDs.size(); i++) {
		int userID = ((Integer) operLoginIDs.get(i)).intValue();
		if (userID == lYukonUser.getUserID()) continue;
		
		LiteYukonUser liteUser = com.cannontech.database.cache.functions.YukonUserFuncs.getLiteYukonUser(userID);
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
                  </table>
                </td>
              </tr>
            </table>
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
