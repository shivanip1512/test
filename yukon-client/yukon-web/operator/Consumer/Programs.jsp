<%@ include file="include/StarsHeader.jsp" %>
<%
	boolean inWizard = request.getParameter("Wizard") != null;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	if (inWizard) {
		programs = new StarsLMPrograms();
		
		MultiAction actions = (MultiAction) session.getAttribute(ServletUtils.ATT_NEW_ACCOUNT_WIZARD);
		if (actions != null) {
			SOAPMessage reqMsg = actions.build(request, session);
			if (reqMsg != null) {
				StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation(reqMsg);
				StarsProgramSignUp progSignUp = reqOper.getStarsProgramSignUp();
				if (progSignUp != null) {
					for (int i = 0; i < progSignUp.getStarsSULMPrograms().getSULMProgramCount(); i++) {
						SULMProgram suProg = progSignUp.getStarsSULMPrograms().getSULMProgram(i);
						StarsLMProgram program = new StarsLMProgram();
						program.setProgramID( suProg.getProgramID() );
						program.setApplianceCategoryID( suProg.getApplianceCategoryID() );
						program.setStatus("Not Enrolled");
						programs.addStarsLMProgram( program );
					}
				}
			}
		}
	}
	
	if (programs == null) programs = new StarsLMPrograms();
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
var text = [
<%
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		String desc = category.getStarsWebConfig().getDescription();
		if (desc.length() == 0) desc = "No Description";
%>
			"<%= desc %>",
<%
	}
%>
			""];

function toolTipAppear(event, divId, index, w, text) {

	var coordx = getLeftCoordinate();
	var coordy = getTopCoordinate();
	var source;
	if (window.event)
      source = window.event.srcElement;
    else
      source = event.target;
	
	source.onmouseout = closeToolTip;
		
	var element = document.getElementById(divId);
	element.innerHTML = text[index]; 
	element.style.width = w;
	element.style.left = coordx + 'px';
	element.style.top = coordy + 'px';
	element.style.visibility = 'visible';
	
function closeToolTip() {
	var element = document.getElementById(divId);
	element.style.visibility = 'hidden';
}
	
function getLeftCoordinate() {
	var x;
	if (window.event) {
		x = window.event.clientX + document.documentElement.scrollLeft + document.body.scrollLeft;
	}
	else {
		x = event.clientX + window.scrollX;
	}
	return x;
}

function getTopCoordinate() {
	var y;
	if (window.event) {
		y = window.event.clientY + document.documentElement.scrollTop + document.body.scrollTop + 20;
	}
	else {
		y = event.clientY + window.scrollY + 20;
	}
	return y;
}
}


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

function confirmSubmit() {
<% if (request.getParameter("Wizard") == null) { %>
	if (!signUpChanged) return false;
	return confirm('Are you sure you would like to modify these program options?');
<% } %>
}

function resendNotEnrolled(form) {
	setNotEnrolled(true);
	form.NotEnrolled.value = "Resend";
	form.submit();
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
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_ENROLLMENT, "PROGRAMS - ENROLLMENT"); %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jsp" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
<% } %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  
              <div align="center"><span class="TableCell">Select the check boxes 
                and corresponding radio button of the programs this customer would 
                like to be enrolled in. </span><br>
                <br>
              </div>
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return confirmSubmit()">
                <input type="hidden" name="action" value="ProgramSignUp">
                <input type="hidden" name="SignUpChanged" value="false">
                <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Programs.jsp">
                <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/Programs.jsp">
				<% if (inWizard) { %><input type="hidden" name="Wizard" value="true"><% } %>
                <table border="1" cellspacing="0" cellpadding="2" width="366">
                  <tr> 
                    <td width="81" class="HeaderCell" align = "center">Description</td>
                    <td width="155" class="HeaderCell"> 
                      <div align="center">Program Enrollment</div>
                    </td>
                    <td width="110" class="HeaderCell"> 
                      <div align="center">Status</div>
                    </td>
                  </tr>
<%
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
		if (category.getStarsEnrLMProgramCount() == 1)	// Use the program display name instead of the category name
			categoryName = ServletUtils.getProgramDisplayNames(category.getStarsEnrLMProgram(0))[0];
%>
                  <tr> 
                    <td width="81" align = "center">
<% if (!category.getStarsWebConfig().getLogoLocation().equals("")) { %>
					  <img id="<%= i %>" src="../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>" onClick = "toolTipAppear(event, 'tool', <%= i %>, 350, text)"><br>
                      <span class = "TableCell">Click for description</span>
<% } else { out.print("&nbsp;"); } %>
					</td>
                    <td width="155" align = "center"> 
                      <table width="110" border="0" cellspacing="0" cellpadding="1" align="center">
						<input type="hidden" name="CatID" value="<% if (program != null) out.print(category.getApplianceCategoryID()); %>">
						<input type="hidden" name="ProgID" value="<% if (program != null) out.print(program.getProgramID()); %>">
						<input type="hidden" name="DefProgID" value="<%= category.getStarsEnrLMProgram(0).getProgramID() %>">
                        <tr> 
                          <td width="23"> 
                            <input type="checkbox" name="AppCat" value="<%= category.getApplianceCategoryID() %>"
						  onclick="changeCategory(this, <%= numProgCat %>)" <% if (program != null) out.print("checked"); %>>
                          </td>
                          <td class="TableCell" nowrap><%= categoryName %></td>
                        </tr>
                      </table>
<%
		if (category.getStarsEnrLMProgramCount() > 1) {
			/* If more than one program under this category, show the program list */
%>
                      <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
<%
			for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
				StarsEnrLMProgram enrProg = category.getStarsEnrLMProgram(j);
				String checkStr = "";
				if (program != null && enrProg.getProgramID() == program.getProgramID())
					checkStr = "checked";
				/* Each row is a program in this category */
%>
                        <tr> 
                          <td width="37"> 
                            <div align="right"> 
                              <input type="radio" name="Program<%= numProgCat %>" value="<%= enrProg.getProgramID() %>"
							  onclick="changeProgram(this, <%= numProgCat %>)" <%= checkStr %>>
                            </div>
                          </td>
                          <td class="TableCell" nowrap><%= ServletUtils.getProgramDisplayNames(enrProg)[1] %></td>
                        </tr>
<%
			}	// End of program
%>
                      </table>
<%
		}	// End of program list
%>
                    </td>
                    <td width="110" valign="middle" class="TableCell"> 
                      <div align="center"><%= programStatus %></div>
                    </td>
                  </tr>
<%
		numProgCat++;
	}
	
	if (numProgCat > 0) {
%>
                  <tr> 
                    <td width="81" align = "center">&nbsp;</td>
                    <td width="155" align = "center"> 
                      <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td width="23"> 
                            <input type="checkbox" id="NotEnrolled" name="NotEnrolled" value="true" onclick="setNotEnrolled(true)"
						    <% if (numEnrolledProg == 0) out.print("checked"); %>>
                          </td>
                          <td width="84" class="TableCell">Not Enrolled</td>
                        </tr>
                      </table>
                    </td>
                    <td width="110" valign="middle" class="TableCell"> 
                      <div align="center">
					    <input type="button" id="Resend" value="Resend" onclick="resendNotEnrolled(this.form)"
						<% if (numEnrolledProg > 0) out.print("disabled=\"true\""); %>>
					  </div>
                    </td>
                  </tr>
<%
	}
%>
                </table>
<script language="JavaScript">numEnrolledProg = <%= numEnrolledProg %>;</script>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="186"> 
                      <div align="right"> 
<% if (request.getParameter("Wizard") == null) { %>
                        <input type="submit" name="Submit" value="Submit">
<% } else { %>
                        <input type="submit" name="Done" value="Done">
<% } %>
                      </div>
                    </td>
                    <td width="194"> 
                      <div align="left"> 
<% if (request.getParameter("Wizard") == null) { %>
                        <input type="reset" name="Reset" value="Reset">
<% } else { %>
                        <input type="button" name="Cancel" value="Cancel" onclick="location.href = '../Operations.jsp'">
<% } %>
                      </div>
                    </td>
                  </tr>
                </table>
              </form>
<% if (request.getParameter("Wizard") == null) { %>
              <p align="center" class="SubtitleHeader">Program History
              <table width="366" border="1" cellspacing="0" align="center" cellpadding="3">
                <tr> 
                  <td class="HeaderCell" width="100" >Date</td>
                  <td class="HeaderCell" width="154" >Type - Duration</td>
                  <td class="HeaderCell" width="100" >Program</td>
                </tr>
<%
	if (programHistory != null) {
		int eventCnt = programHistory.getStarsLMProgramEventCount();
		for (int i = eventCnt - 1; i >= 0 && i >= eventCnt - 3; i--) {
			StarsLMProgramEvent event = programHistory.getStarsLMProgramEvent(i);
			
			String durationStr = "";
			if (event.hasDuration()) {
				if (event.getDuration() >= 12) {
					int numDays = (int) (event.getDuration() / 24.0 + 0.5);
					durationStr = numDays + " Day";
					if (numDays > 1) durationStr += "s";
				}
				else {
					durationStr = event.getDuration() + " Hour";
					if (event.getDuration() > 1) durationStr += "s";
				}
			}
			
			String scheduledStr = "";
			if (event.hasDuration() && event.getEventDateTime().after(new Date()))
				scheduledStr = "(Scheduled)";
			
			String progNames = "";
			for (int j = 0; j < event.getProgramIDCount(); j++) {
				for (int k = 0; k < categories.getStarsApplianceCategoryCount(); k++) {
					StarsApplianceCategory appCat = categories.getStarsApplianceCategory(k);
					boolean foundProgram = false;
					
					for (int l = 0; l < appCat.getStarsEnrLMProgramCount(); l++) {
						StarsEnrLMProgram enrProg = appCat.getStarsEnrLMProgram(l);
						if (enrProg.getProgramID() == event.getProgramID(j)) {
							progNames += enrProg.getProgramName() + "<br>";
							foundProgram = true;
							break;
						}
					}
					
					if (foundProgram) break;
				}
			}
			if (progNames.equals("")) continue;
%>
                <tr> 
                  <td class="TableCell" width="100" ><%= datePart.format(event.getEventDateTime()) %></td>
                  <td class="TableCell" width="154" ><%= event.getEventAction() %> 
                    <% if (event.hasDuration()) { %>- <%= durationStr %><% } %>
					<%= scheduledStr %>
                  </td>
                  <td class="TableCell" width="100" ><%= progNames %></td>
                </tr>
<%
		}
	}
%>
              </table>
<% } %>
              <br>
            </div>
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
