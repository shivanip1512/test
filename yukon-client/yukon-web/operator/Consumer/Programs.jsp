<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
<!--
var text = [
<%
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
%>
			"<%= category.getStarsWebConfig().getDescription() %>",
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




function doReenable(form) {
	form.action.value = "EnableService";
	form.submit();
}

function MM_popupMsg(msg) { //v1.0
  alert(msg);
}
//-->


function changeCategory(checkbox, index) {
	form = checkbox.form;
	if (checkbox.checked) {
		radioBtns = eval("form.Program" + index);
		if (radioBtns != null)
			radioBtns[0].checked = true;
		form.CatID[index].value = checkbox.value;
		form.ProgID[index].value = form.DefProgID[index].value;
	}
	else {
		radioBtns = eval("form.Program" + index);
		if (radioBtns != null)
			for (i = 0; i < radioBtns.length; i++)
				radioBtns[i].checked = false;
		form.CatID[index].value = "";
		form.ProgID[index].value = "";
	}
}

function changeProgram(radioBtn, index) {
	form = radioBtn.form;
	form.AppCat[index].checked = true;
	form.CatID[index].value = form.AppCat[index].value;
	form.ProgID[index].value = radioBtn.value;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip" style="width: 1003px; height: 20px"></div>
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
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
		  <% String pageName = "Programs.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "PROGRAMS - ENROLLMENT"; %>
              <%@ include file="InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
             
              <div align="center"><span class="TableCell">Select the check boxes 
                and corresponding radio button of the programs this customer would 
                like to be enrolled in. </span><br>
                <br>
              </div>
			  <form name="form1" method="post" action="/servlet/SOAPClient">
			  <input type="hidden" name="action" value="ProgramSignUp">
			  <input type="hidden" name="REDIRECT" value="/operator/Consumer/Programs.jsp">
			  <input type="hidden" name="REFERRER" value="/operator/Consumer/Programs.jsp">
              <table border="1" cellspacing="0" cellpadding="3" width="366">
                <tr>
                  <td width="83" class="HeaderCell" align = "center">Description</td>
                  <td width="132" class="HeaderCell"> 
                    <div align="center">Program Enrollment</div>
                  </td>
                  <td width="125" class="HeaderCell"> 
                    <div align="center">Status</div>
                  </td>
                </tr>
<%
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
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
%>
                <tr>
                    <td width="83" align = "center"><img id="<%= i %>" src="<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" onClick = "toolTipAppear(event, 'tool', <%= i %>, 350, text)"><br>
                      <span class = "TableCell">Click for description</span></td>
                  <td width="132" align = "center"> 
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="23"> 
						  <input type="checkbox" name="AppCat" value="<%= category.getApplianceCategoryID() %>"
						  onclick="changeCategory(this, <%= i %>)" <% if (program != null) out.print("checked"); %>>
						  <input type="hidden" name="CatID" value="<% if (program != null) out.print(category.getApplianceCategoryID()); %>">
						  <input type="hidden" name="ProgID" value="<% if (program != null) out.print(program.getProgramID()); %>">
						  <input type="hidden" name="DefProgID" value="<%= category.getStarsEnrLMProgram(0).getProgramID() %>">
                        </td>
                        <td width="84" class="TableCell"><%= category.getStarsWebConfig().getAlternateDisplayName() %></td>
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
							<input type="radio" name="Program<%= i %>" value="<%= prog.getProgramID() %>" onclick="changeProgram(this, <%= i %>)"
							<% if (program != null && prog.getProgramID() == program.getProgramID()) out.print("checked"); %>>
                          </div>
                        </td>
                        <td width="70" class="TableCell"><%= prog.getStarsWebConfig().getAlternateDisplayName() %></td>
                      </tr>
<%
			}	// End of program
%>
                    </table>
<%
		}	// End of program list
%>
                  </td>
                  <td width="125" valign="middle" class="TableCell"> 
                    <div align="center"><%= programStatus %></div>
                  </td>
                </tr>
<%
	}
%>
              </table>
             
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="Submit" value="Submit" onClick="MM_popupMsg('Are you sure you would like to modify these program options?')">
                      </div>
                    </td>
                    <td width="194"> 
                      <div align="left"> 
                        <input type="reset" name="Cancel2" value="Cancel">
                      </div>
                    </td>
                  </tr>
                </table>
              </form>
			  
                <p align="center" class="MainHeader"><b>Program History </b> 
                <table width="300" border="1" cellspacing="0" align="center" cellpadding="3">
                  <tr> 
                    <td class="HeaderCell" width="100" >Date</td>
                    <td class="HeaderCell" width="100" >Type - Duration</td>
                    <td class="HeaderCell" width="100" >Program</td>
                  </tr>
<%
	ServletUtils.ProgramHistory[] progHist = (ServletUtils.ProgramHistory[]) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_LM_PROGRAM_HISTORY);
	if (progHist == null) {
		progHist = ServletUtils.createProgramHistory( programs );
		user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_LM_PROGRAM_HISTORY, progHist);
	}
	
	for (int i = progHist.length - 1; i >= 0 && i >= progHist.length - 3; i--) {
%>
                  <tr> 
                    <td class="TableCell" width="100" ><%= datePart.format(progHist[i].getDate()) %></td>
                    <td class="TableCell" width="100" ><%= progHist[i].getAction() %>
					<% if (progHist[i].getDuration() != null) { %> - <%= progHist[i].getDuration() %><% } %>
					</td>
                    <td class="TableCell" width="100" >
<%
		String[] progNames = progHist[i].getPrograms();
		for (int j = 0; j < progNames.length; j++) {
%>
					<%= progNames[j] %><br>
<%
		}
%>
					</td>
                  </tr>
<%
	}
%>
                </table>
                <br>
             
             </div>
            </td>
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
