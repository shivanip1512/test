<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
var signUpChanged = false;
function setSignUpChanged() {
	signUpChanged = true;
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
	
	setSignUpChanged();
}

function changeProgram(radioBtn, index) {
	var categories = document.getElementsByName("AppCat");
	var catIDs = document.getElementsByName("CatID");
	var progIDs = document.getElementsByName("ProgID");
	
	if (progIDs[index].value == radioBtn.value) return;	// Nothing is changed
	
	categories[index].checked = true;
	catIDs[index].value = categories[index].value;
	progIDs[index].value = radioBtn.value;
	setSignUpChanged();
}

function confirmSubmit(form) {
	if (!signUpChanged) return false;
	return confirm('Are you sure you would like to modify these program options?');
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
		  <% String pageName = "Enrollment.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_ENROLLMENT, "PROGRAMS - ENROLLMENT"); %>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
              
              </div>
            <table width="90%" border="0" align = "center">
              <tr>
                <td align = "center"><span class="TableCell"><cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_DESC_ENROLLMENT %>"/> 
                  <br>
                  <br>
                  </span> 
                  <form method="post" action="<%=request.getContextPath()%>/servlet/SOAPClient" onsubmit="return confirmSubmit(this)">
				  <input type="hidden" name="action" value="ProgramSignUp">
				  <input type="hidden" name="SignUpChanged" value="false">
				  <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/Enrollment.jsp">
				  <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/Enrollment.jsp">
                    <table border="1" cellspacing="0" cellpadding="3" width="95%">
                      <tr align = "center"> 
                        <td width="90%" class="HeaderCell"> 
                          <div align="center">Program Enrollment</div>
                      </td>
                        <td width="10%" class="HeaderCell"> 
                          <div align="center">Status</div>
                      </td>
                    </tr>
<%
	for (int i = 0, idx = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		if (category.getStarsEnrLMProgramCount() == 0) continue;
		
		StarsLMProgram program = null;
		String programStatus = "Not Enrolled";
		
		for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
			StarsLMProgram prog = programs.getStarsLMProgram(j);
			if (prog.getApplianceCategoryID() == category.getApplianceCategoryID()) {
				program = prog;
				programStatus = program.getStatus();
				break;
			}
		}
		
		String categoryName = category.getStarsWebConfig().getAlternateDisplayName();
		if (category.getStarsEnrLMProgramCount() == 1)	// Use the program display name instead of the category name
			categoryName = ServletUtils.getProgramDisplayNames(category.getStarsEnrLMProgram(0))[0];
		
		String checkBoxDisabled = "";
		String radioBtnDisabled = "";
		if (AuthFuncs.checkRoleProperty( lYukonUser, ResidentialCustomerRole.DISABLE_PROGRAM_SIGNUP )) {
			checkBoxDisabled = "disabled";
			if (program == null) radioBtnDisabled = "disabled";
		}
%>
                        <tr> 
                        <td width="90%"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="10%" valign="top"> <img src="../../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>"
								<% if (category.getStarsWebConfig().getLogoLocation().equals("")) { %>style="display:none"<% } %>> 
                              </td>
                              <td width="85%"> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="1">
                                  <input type="hidden" name="CatID" value="<% if (program != null) out.print(category.getApplianceCategoryID()); %>">
                                  <input type="hidden" name="ProgID" value="<% if (program != null) out.print(program.getProgramID()); %>">
                                  <input type="hidden" name="DefProgID" value="<%= category.getStarsEnrLMProgram(0).getProgramID() %>">
                                  <tr valign="top"> 
                                    <td width="5%"> 
                                      <input type="checkbox" name="AppCat" value="<%= category.getApplianceCategoryID() %>" <%= checkBoxDisabled %>
									  onclick="changeCategory(this, <%= idx %>)" <% if (program != null) { %>checked<% } %>>
                                    </td>
                                    <td width="95%"><span class="SubtitleHeader"><%= categoryName %></span></td>
                                  </tr>
                                  <tr> 
                                    <td width="5%">&nbsp;</td>
                                    <td width="95%"><span class="TableCell"><%= category.getStarsWebConfig().getDescription() %></span></td>
                                  </tr>
                                </table>
                                <table width="100%" border="0" cellspacing="0" cellpadding="1">
                                  <%
		if (category.getStarsEnrLMProgramCount() == 1) {
			/* If only one program under this category, only show the description button */
%>
                                  <tr valign="top"> 
                                    <td width="9%">&nbsp;</td>
                                    <td class="TableCell" width="49%">&nbsp;</td>
                                    <td class="TableCell" width="22%">&nbsp;</td>
                                    <td class="TableCell" width="20%" align="right"> 
                                      <input type="button" name="Details" value="Details" onclick="location.href='ProgramDetails.jsp?Cat=<%= i %>'">
                                    </td>
                                  </tr>
                                  <%
		} else {
			/* If more than one program under this category, show the program list */
%>
                                  <%
			for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
				StarsEnrLMProgram prog = category.getStarsEnrLMProgram(j);
				String checked = "";
				if (program != null && prog.getProgramID() == program.getProgramID())
					checked = "checked";
				String[] progIcons = ServletUtils.getImageNames(prog.getStarsWebConfig().getLogoLocation());
				/* Each row is a program in this category */
%>
                                  <tr> 
                                    <td colspan="4" background="../../../Images/Icons/dot.gif" height="8"></td>
                                  </tr>
                                  <tr valign="top"> 
                                    <td width="9%" align="right"> 
                                      <input type="radio" name="Program<%= idx %>" value="<%= prog.getProgramID() %>" <%= radioBtnDisabled %>
									  onclick="changeProgram(this, <%= idx %>)" <%= checked %>>
                                    </td>
                                    <td class="TableCell" width="49%"><%= ServletUtils.getProgramDisplayNames(prog)[1] %> 
                                    </td>
                                    <td class="TableCell" width="22%" align="right"> 
                                      <% if (!progIcons[0].equals("")) { %>
                                      <img src="../../../Images/Icons/<%= progIcons[0] %>"> 
                                      <% } %>
                                      <% if (!progIcons[1].equals("")) { %>
                                      <img src="../../../Images/Icons/<%= progIcons[1] %>"> 
                                      <% } %>
                                      <% if (!progIcons[2].equals("")) { %>
                                      <img src="../../../Images/Icons/<%= progIcons[2] %>"> 
                                      <% } %>
                                    </td>
                                    <td class="TableCell" width="20%" align="right"> 
                                      <input type="button" name="Details" value="Details" onclick="location.href='ProgramDetails.jsp?Cat=<%= i %>&Prog=<%= j %>'">
                                    </td>
                                  </tr>
                                  <%
			}	// End of program
		}	// End of program list
%>
                                </table>
                            </tr>
                          </table>
                        </td>
                        <td width="10%" valign="top" class="TableCell"> 
                          <div align="center"><%= programStatus %></div>
                        </td>
                      </tr>
<%
		idx++;
	}
%>
                    </table>
                    <br>
                    <table width="50%" border="0">
                      <tr> 
                        <td align = "right"> 
                          <input type="submit" name="Submit" value="Submit">
                        </td>
                        <td> 
                          <input type="reset" name="Reset" value="Reset">
                        </td>
                      </tr>
                    </table>
                  </form>
                </td>
              </tr>
            </table>
            <p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
