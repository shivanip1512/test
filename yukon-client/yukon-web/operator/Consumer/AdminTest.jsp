<%@ include file="include/StarsHeader.jsp" %>
<%	if (!AuthFuncs.checkRoleProperty(lYukonUser, com.cannontech.roles.operator.AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY)
		|| ecSettings == null) {
		response.sendRedirect("../Operations.jsp"); return;
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function confirmDeleteAccount(form) {
	if (form.AcctNo.value == "*")
		return confirm("Are you sure you want to delete all customer accounts?");
	return true;
}

function changeCategory(checkBox, index) {
	var programs, catIDs, progIDs, defProgIDs;
	
	if (checkBox.checked) {
		programs = document.getElementsByName("Program" + index);
		if (programs.length > 0)
			programs[0].checked = true;
		catIDs = document.getElementsByName("CatID");
		progIDs = document.getElementsByName("ProgID");
		defProgIDs = document.getElementsByName("DefProgID");
		catIDs[index].value = checkBox.value;
		progIDs[index].value = defProgIDs[index].value;
	}
	else {
		programs = document.getElementsByName("Program" + index);
		for (i = 0; i < programs.length; i++)
			programs[i].checked = false;
		catIDs = document.getElementsByName("CatID");
		progIDs = document.getElementsByName("ProgID");
		catIDs[index].value = "";
		progIDs[index].value = "";
	}
}

function changeProgram(radioBtn, index) {
	var categories = document.getElementsByName("AppCat");
	var catIDs = document.getElementsByName("CatID");
	var progIDs = document.getElementsByName("ProgID");
	
	if (progIDs[index].value == radioBtn.value) return;	// Nothing is changed
	
	categories[index].checked = true;
	catIDs[index].value = categories[index].value;
	progIDs[index].value = radioBtn.value;
}

function confirmDeleteAppCat() {
	return confirm("If you delete the appliance category, all the programs and customer appliances under this category will also be deleted. Are you sure you want to continue?");
}

function confirmDeleteAllAppCats() {
	return confirm("Are you sure you want to delete all appliance categories, programs, and customer appliances under these categories?");
}

function editServiceCompany(form, compIdx) {
	form.attributes["action"].value = 'Admin_ServiceCompany.jsp?Company=' + compIdx;
	form.action.value = "init";
	form.submit();
}

function confirmDeleteCompany() {
	return confirm("Are you sure you want to delete the service company?");
}

function confirmDeleteAllCompanies() {
	return confirm("Are you sure you want to delete all service companies?");
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
              
            <div align="center">
              <% String header = "ADMINISTRATION"; %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
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
                          <input type="submit" name="Submit" value="Submit" onclick="return confirmDelteAccount(this.form)">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </form>
            <form name="form2" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Import Customer Accounts</td>
                </tr>
                <tr> 
                  <td> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                      <input type="hidden" name="action" value="ImportCustAccounts">
                      <tr> 
                        <td width="75%" class="TableCell">Please enter the file 
                          name: 
                          <input type="text" name="ImportFile" maxlength="100" size="40">
                          <br>
                          <br>
                          <table border="1" cellspacing="0" cellpadding="2" width="300" align="center">
                            <tr> 
                              <td width="100" class="HeaderCell" align = "center">Description</td>
                              <td width="186" class="HeaderCell"> 
                                <div align="center">Program Enrollment</div>
                              </td>
                            </tr>
<%
	int numProgCat = 0;
	int numEnrolledProg = 0;
	
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		if (category.getStarsEnrLMProgramCount() == 0) continue;
%>
                            <tr> 
                              <td width="100" align = "center"><img src="../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>" width="60"><br>
                              </td>
                              <td width="186" align = "center"> 
                                <table width="110" border="0" cellspacing="0" cellpadding="1" align="center">
                                  <input type="hidden" name="CatID">
                                  <input type="hidden" name="ProgID">
                                  <input type="hidden" name="DefProgID" value="<%= category.getStarsEnrLMProgram(0).getProgramID() %>">
                                  <tr> 
                                    <td width="23"> 
                                      <input type="checkbox" name="AppCat" value="<%= category.getApplianceCategoryID() %>" onClick="changeCategory(this, <%= numProgCat %>)">
                                    </td>
                                    <td class="TableCell" nowrap><%= category.getStarsWebConfig().getAlternateDisplayName() %></td>
                                  </tr>
                                </table>
<%
		if (category.getStarsEnrLMProgramCount() > 1) {
			/* If more than one program under this category, show the program list */
%>
                                <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
<%
			for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
				StarsEnrLMProgram prog = category.getStarsEnrLMProgram(j);
				/* Each row is a program in this category */
%>
                                  <tr> 
                                    <td width="37"> 
                                      <div align="right"> 
                                        <input type="radio" name="Program<%= numProgCat %>" value="<%= prog.getProgramID() %>" onClick="changeProgram(this, <%= numProgCat %>)">
                                      </div>
                                    </td>
                                    <td class="TableCell" nowrap><%= ServletUtils.getProgramDisplayNames(prog)[1] %></td>
                                  </tr>
                                  <%
			}	// End of program
%>
                                </table>
<%
		}	// End of program list
%>
                              </td>
                            </tr>
                            <%
		numProgCat++;
	}
%>
                          </table>
                          <br>
                        </td>
                        <td width="25%" valign="top"> 
                          <input type="submit" name="Submit2" value="Submit">
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
                        <form name="form6" method="post" action="Admin_EnergyCompany.jsp">
                          <input type="hidden" name="action" value="init">
                          <b><font color="#0000FF">Energy Company:</font></b> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td width="5%">&nbsp;</td>
                              <td width="70%"><%= energyCompany.getCompanyName() %></td>
                              <td width="25%"> 
                                <input type="submit" name="Edit" value="Edit">
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
                                    <td width="15%" class="TableCell" align="center"><img src="../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>" width="60"></td>
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
                                      <input type="button" name="Edit" value="Edit" onClick="location.href='Admin_ApplianceCategory.jsp?Category=<%= i %>'">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="submit" name="Delete" value="Delete" onClick="this.form.AppCatID.value=<%= category.getApplianceCategoryID() %>; return confirmDeleteAppCat();">
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
                                <input type="submit" name="DeleteAll" value="Delete All" onClick="this.form.AppCatID.value=-1; return confirmDeleteAllAppCats();">
                              </td>
                              <td width="80%"> 
                                <input type="button" name="New" value="New" onClick="location.href='Admin_ApplianceCategory.jsp?Category=<%= categories.getStarsApplianceCategoryCount() %>'">
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
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
                                      <input type="button" name="Edit" value="Edit" onClick="editServiceCompany(this.form, <%= i %>)">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="submit" name="Delete" value="Delete" onClick="this.form.CompanyID.value=<%= company.getCompanyID() %>; return confirmDeleteCompany();">
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
                                <input type="submit" name="DeleteAll" value="Delete All" onClick="this.form.CompanyID.value=-1; return confirmDeleteAllCompanies();">
                              </td>
                              <td width="80%"> 
                                <input type="button" name="New" value="New" onClick="location.href='Admin_ServiceCompany.jsp?Company=<%= companies.getStarsServiceCompanyCount() %>'">
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
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
<%	if (customizedFAQ) { %>
                                      <%= faqLink %>
<%	} else { %>
									  <ul>
<%		for (int i = 0; i < customerFAQs.getStarsCustomerFAQGroupCount(); i++) {
			StarsCustomerFAQGroup faqGroup = customerFAQs.getStarsCustomerFAQGroup(i);
%>
                                        <li><%= faqGroup.getSubject() %></li>
<%		} %>
                                      </ul>
<%	} %>
                                    </td>
                                    <td width="25%" class="TableCell"> 
                                      <input type="button" name="Edit" value="Edit" onClick="location.href='Admin_CustomerFAQ.jsp'">
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
                                      <%	} %>
                                    </ul>
                                  </td>
                                  <td width="25%" class="TableCell"> 
                                    <input type="button" name="Edit" value="Edit" onClick="location.href='Admin_InterviewQuestion.jsp?Type=Exit'">
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_HARDWARES_THERMOSTAT %>">
                    <tr>
                      <td><b><font color="#0000FF">Default Thermostat Settings: 
                        </font></b> 
                        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                          <tr> 
                            <td width="5%">&nbsp;</td>
                            <td width="70%">Default Thermostat Schedule</td>
                            <td width="25%"> 
                              <input type="button" name="Edit" value="Edit" onclick="location.href = 'Admin_ThermSchedule.jsp'">
                            </td>
                          </tr>
                        </table>
                        </td>
                    </tr>
</cti:checkProperty>
                    <tr> 
                      <td><b><font color="#0000FF">Customer Selection Lists:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
<%
	com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany ec = SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
	for (int i = 0; i < ec.getAllSelectionLists().size(); i++) {
		com.cannontech.common.constants.YukonSelectionList cList =
				(com.cannontech.common.constants.YukonSelectionList) ec.getAllSelectionLists().get(i);
		if (cList.getUserUpdateAvailable() == null || !cList.getUserUpdateAvailable().equalsIgnoreCase("Y")) continue;
		
		StarsCustSelectionList list = (StarsCustSelectionList) selectionListTable.get( cList.getListName() );
		if (list == null) continue;
%>
                                <tr> 
                                  <td class="TableCell" width="5%">&nbsp;</td>
                                  <td class="TableCell" width="70%"><%= list.getListName() %></td>
                                  <td class="TableCell"> 
                                    <input type="button" name="Edit" value="Edit" onClick="location.href='Admin_SelectionList.jsp?List=<%= list.getListName() %>'">
                                  </td>
                                </tr>
                                <%	} %>
                              </table>
                            </td>
                          </tr>
                        </table>
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
