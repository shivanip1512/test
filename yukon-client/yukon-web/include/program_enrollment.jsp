<%
	boolean isOperator = ServerUtils.isOperator(user);
%>

<script language="JavaScript">
var numEnrolledProg = 0;

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
		
		if (numEnrolledProg == 0)
			document.getElementById("NotEnrolled").checked = false;
		numEnrolledProg++;
	}
	else {
		programs = document.getElementsByName("Program" + index);
		for (i = 0; i < programs.length; i++)
			programs[i].checked = false;
		catIDs = document.getElementsByName("CatID");
		progIDs = document.getElementsByName("ProgID");
		catIDs[index].value = "";
		progIDs[index].value = "";
		
		numEnrolledProg--;
		if (numEnrolledProg == 0)
			document.getElementById("NotEnrolled").checked = true;
	}
	
	setSignUpChanged();
}

function changeProgram(radioBtn, index) {
	var categories = document.getElementsByName("AppCat");
	var catIDs = document.getElementsByName("CatID");
	var progIDs = document.getElementsByName("ProgID");
	
	if (progIDs[index].value == radioBtn.value) return;	// Nothing is changed
	
	if (!categories[index].checked) {
		if (numEnrolledProg == 0)
			document.getElementById("NotEnrolled").checked = false;
		numEnrolledProg++;
	}
	
	categories[index].checked = true;
	catIDs[index].value = categories[index].value;
	progIDs[index].value = radioBtn.value;
	setSignUpChanged();
}

function setNotEnrolled(userAction) {
	if (userAction) {
		var categories = document.getElementsByName("AppCat");
		for (catIdx = 0; catIdx < categories.length; catIdx++) {
			if (categories[catIdx].checked) {
				categories[catIdx].checked = false;
				changeCategory(categories[catIdx], catIdx);
			}
		}
	}
	document.getElementById("NotEnrolled").checked = true;
}

function resendNotEnrolled(form) {
	setNotEnrolled(true);
	form.NotEnrolled.value = "Resend";
	form.submit();
}

function confirmSubmit(form) {
<% if (request.getParameter("Wizard") == null) { %>
	if (!signUpChanged) return false;
	return confirm('Are you sure you would like to modify these program options?');
<% } %>
}
</script>

            <table width="90%" border="0" align = "center">
              <tr>
                <td align = "center"><span class="TableCell">
<% if (isOperator) { %>
				  Select the check boxes and corresponding radio button of the programs this 
				  customer would like to be enrolled in.
<% } else { %>
				  <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_DESC_ENROLLMENT %>"/> 
<% } %>
                  <br>
                  <br>
                  </span> 
                  <form method="post" action="<%=request.getContextPath()%>/servlet/SOAPClient" onsubmit="return confirmSubmit(this)">
				    <input type="hidden" name="action" value="ProgramSignUp">
				    <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
				    <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
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
	boolean savingsIconExists = false;
	boolean controlIconExists = false;
	boolean envrnmtIconExists = false;
	
	int numProgCat = 0;
	int numEnrolledProg = 0;
	
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		if (category.getStarsEnrLMProgramCount() == 0) continue;
		
		StarsLMProgram program = null;
		String programStatus = "Not Enrolled";
		
		for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
			StarsLMProgram prog = programs.getStarsLMProgram(j);
			if (prog.getApplianceCategoryID() == category.getApplianceCategoryID()) {
				program = prog;
				programStatus = program.getStatus();
				numEnrolledProg++;
				break;
			}
		}
		
		String categoryName = category.getStarsWebConfig().getAlternateDisplayName();
		String categoryDesc = category.getStarsWebConfig().getDescription();
		if (category.getStarsEnrLMProgramCount() == 1) {	// Use the program display name and description instead
			categoryName = ServletUtils.getProgramDisplayNames(category.getStarsEnrLMProgram(0))[0];
			categoryDesc = category.getStarsEnrLMProgram(0).getStarsWebConfig().getDescription();
		}
		
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
                              <td width="15%" valign="top"> <img src="<%= request.getContextPath() %>/Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>"
								<% if (category.getStarsWebConfig().getLogoLocation().equals("")) { %>style="display:none"<% } %>> 
                              </td>
                              <td width="3%" valign="top"> 
                                <input type="checkbox" name="AppCat" value="<%= category.getApplianceCategoryID() %>" <%= checkBoxDisabled %>
								onclick="changeCategory(this, <%= numProgCat %>)" <% if (program != null) { %>checked<% } %>>
                              <td width="82%" valign="top"> 
                                <table width="100%" border="0" cellspacing="3" cellpadding="0">
                                  <input type="hidden" name="CatID" value="<% if (program != null) out.print(category.getApplianceCategoryID()); %>">
                                  <input type="hidden" name="ProgID" value="<% if (program != null) out.print(program.getProgramID()); %>">
                                  <input type="hidden" name="DefProgID" value="<%= category.getStarsEnrLMProgram(0).getProgramID() %>">
                                  <tr> 
                                    <td width="95%" class="SubtitleHeader"><%= categoryName %></td>
                                  </tr>
                                  <tr> 
                                    <td width="95%" class="TableCell"><%= categoryDesc %></td>
                                  </tr>
                                </table>
                                <table width="100%" border="0" cellspacing="0" cellpadding="1">
<%
		if (category.getStarsEnrLMProgramCount() == 1) {
			/* If only one program under this category, only show the description button */
%>
                                  <tr> 
                                    <td width="3%">&nbsp;</td>
                                    <td class="TableCell">&nbsp;</td>
                                    <td class="TableCell" width="17%" align="right" valign="top"> 
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
                                    <td colspan="4" background="<%= request.getContextPath() %>/Images/Icons/dot.gif" height="8"></td>
                                  </tr>
                                  <tr> 
                                    <td width="3%" valign="top"> 
                                      <input type="radio" name="Program<%= numProgCat %>" value="<%= prog.getProgramID() %>" <%= radioBtnDisabled %>
									  onclick="changeProgram(this, <%= numProgCat %>)" <%= checked %>>
                                    </td>
                                    <td> 
                                      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                        <tr> 
                                          <td width="70%" class="SubtitleHeader"><%= ServletUtils.getProgramDisplayNames(prog)[1] %></td>
                                          <td width="30%"> 
<% if (!progIcons[0].equals("")) {
	savingsIconExists = true;
%>
                                            <img src="<%= request.getContextPath() %>/Images/Icons/<%= progIcons[0] %>"> 
<% } %>
<% if (!progIcons[1].equals("")) {
	controlIconExists = true;
%>
                                            <img src="<%= request.getContextPath() %>/Images/Icons/<%= progIcons[1] %>"> 
<% } %>
<% if (!progIcons[2].equals("")) {
	envrnmtIconExists = true;
%>
                                            <img src="<%= request.getContextPath() %>/Images/Icons/<%= progIcons[2] %>"> 
<% } %>
                                          </td>
                                        </tr>
                                        <tr> 
                                          <td colspan="2"><%= prog.getStarsWebConfig().getDescription() %></td>
                                        </tr>
                                      </table>
                                    </td>
                                    <td class="TableCell" width="17%" align="right" valign="top"> 
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
		numProgCat++;
	}
	
	if (isOperator && numProgCat > 0) {
%>
                      <tr> 
                        <td width="90%"> 
                          
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td width="15%">&nbsp;</td>
                  <td width="3%"> 
                    <input type="checkbox" id="NotEnrolled" name="NotEnrolled" value="true" onclick="setNotEnrolled(true)"
								<% if (numEnrolledProg == 0) { %>checked<% } %>>
                  <td>
                    <table width="100%" border="0" cellspacing="0" cellpadding="1">
                      <tr>
                        <td width="83%" class="SubtitleHeader">Not Enrolled</td>
                        <td width="17%" align="right"> 
                          <input type="button" id="Resend" value="Resend" onclick="resendNotEnrolled(this.form)"
                          <% if (numEnrolledProg > 0) { %>disabled="true"<% } %>>
                        </td>
                      </tr>
                    </table>
                    
                  </td>
                </tr>
              </table>
						</td>
                        <td width="10%" valign="top" class="TableCell">&nbsp;</td>
					  </tr>
<%
	}
%>
                    </table>
<script language="JavaScript">numEnrolledProg = <%= numEnrolledProg %>;</script>
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
<% if (savingsIconExists || controlIconExists || envrnmtIconExists) { %>
                  <table width="320" border="1" cellpadding="3" cellspacing="0">
                    <tr> 
                      <td><b class="TableCell">&nbsp;&nbsp;The following symbols 
                        represent:</b> 
                        <table width="100%" border="0" cellpadding="0">
<% if (savingsIconExists) { %>
                          <tr> 
                            <td width="10%"><img src="<%= request.getContextPath() %>/Images/Icons/$$Sm.gif" ></td>
                            <td width="90%" class="TableCell">Savings: More dollar 
                              signs means more savings!</td>
                          </tr>
<% } %>
<% if (controlIconExists) { %>
                          <tr> 
                            <td width="10%"><img src="<%= request.getContextPath() %>/Images/Icons/ThirdSm.gif"></td>
                            <td width="90%" class="TableCell">Percent of Control</td>
                          </tr>
<% } %>
<% if (envrnmtIconExists) { %>
                          <tr> 
                            <td width="10%"><img src="<%= request.getContextPath() %>/Images/Icons/Tree2Sm.gif"></td>
                            <td width="90%" class="TableCell">Environment: More 
                              trees means a healthier environment.</td>
                          </tr>
<% } %>
                        </table>
                      </td>
                    </tr>
                  </table>
<% } %>
                </td>
              </tr>
            </table>
            