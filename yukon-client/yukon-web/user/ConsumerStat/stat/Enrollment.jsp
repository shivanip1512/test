<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="CssLink" rel="stylesheet" href="../../demostyle.css" type="text/css">
<% if (ecWebSettings.getURL().length() > 0) { %>
	<script language="JavaScript">document.getElementById("CssLink").href = "../../<%= ecWebSettings.getURL() %>";</script>
<% } %>

<script language="JavaScript">
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
	form.SignUpChanged.value = "true";
}

function changeProgram(radioBtn, index) {
	form = radioBtn.form;
	form.AppCat[index].checked = true;
	form.CatID[index].value = form.AppCat[index].value;
	form.ProgID[index].value = radioBtn.value;
	form.SignUpChanged.value = "true";
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td id="Header" colspan="4" height="74" background="../../Header.gif">&nbsp;</td>
<% if (ecWebSettings.getLogoLocation().length() > 0) { %>
	<script language="JavaScript">document.getElementById("Header").background = "../../<%= ecWebSettings.getLogoLocation() %>";</script>
<% } %>
              </tr>
              <tr> 
                  <td width="265" height="28">&nbsp;</td>
				  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">&nbsp;</td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../../login.jsp" class="Link3">Log 
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
		  <% String pageName = "Enrollment.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = "PROGRAMS - ENROLLMENT"; %>
              <%@ include file="InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              
              </div>
            <table width="90%" border="0" align = "center">
              <tr>
                <td align = "center"><span class="Main"> </span><span class="TableCell"> 
                  Select the check boxes and corresponding radio button of the 
                  programs you would like to be enrolled in.<br> <br></span><b><span class="Main"></span></b>
                  <input type="button" value="Program Details" onclick="location='ProgramDetails.jsp'">
                
				<form method="post" action="/servlet/SOAPClient">
				  <input type="hidden" name="action" value="ProgramSignUp">
				  <input type="hidden" name="SignUpChanged" value="false">
				  <input type="hidden" name="REDIRECT" value="/user/ConsumerStat/stat/Enrollment.jsp">
				  <input type="hidden" name="REFERRER" value="/user/ConsumerStat/stat/Enrollment.jsp">
                  <table border="1" cellspacing="0" cellpadding="3">
                    <tr align = "center"> 
                      <td width="175" class="HeaderCell"> 
                        <div align="center">Program Enrollment</div>
                      </td>
                      <td width="100" class="HeaderCell"> 
                        <div align="center">Status</div>
                      </td>
                    </tr>
<%
	boolean showNotification = false;
	
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		if (category.getStarsEnrLMProgramCount() == 0) continue;
		
		StarsLMProgram program = null;
		String programStatus = "Not Enrolled";
		
		for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
			StarsLMProgram prog = programs.getStarsLMProgram(j);
			if (prog.getApplianceCategoryID() == category.getApplianceCategoryID()) {
				program = prog;
				StarsLMProgramHistory progHist = program.getStarsLMProgramHistory();
				programStatus = program.getStatus();
				break;
			}
		}
%>
                        <tr> 
                          <td width="175"><table width="185" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                              <td><img src="../<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"></td>
                              <td><table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                                  <tr> 
                                    <td width="23"> <input type="checkbox" name="AppCat" value="<%= category.getApplianceCategoryID() %>"
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
				String checkStr = "";
				if (program != null && prog.getProgramID() == program.getProgramID()) {
					checkStr = "checked";
					// Check whether we should show the notification box
					if (prog.getChanceOfControlID() != com.cannontech.common.util.CtiUtilities.NONE_ID)
						showNotification = true;
				}
				/* Each row is a program in this category */
%>
                                  <tr> 
                                    <td width="37"> <div align="right"> 
                                        <input type="radio" name="Program<%= i %>" value="<%= prog.getProgramID() %>" onclick="changeProgram(this, <%= i %>)" <%= checkStr %>>
                                      </div></td>
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
                            </tr>
                          </table> 
                          
                        </td>
                          <td width="100" valign="middle" class="TableCell"> 
                            <div align="center"><%= programStatus %></div>
                          </td>
                        </tr>
<%
	}
%>
                  </table>
                    <p>
<%
	if (showNotification) {
%> 
                     <table width="295" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC" >   
                       <tr>    
                         <td height="58"> <p align="center" class="TableCell1">    
                             <input type="checkbox" name="NotifyControl" value="true"
							   <% if (primContact.getEmail().getEnabled()) out.print("checked"); %>>
                             <span class="TableCell3"> I would like to be notified 
                             by e-mail the day of control.<br>   
                             My e-mail address is:<br>   
                             <input type="text" name="Email" maxlength="50" size="30" value="<%= primContact.getEmail().getNotification() %>">
                             </span></p></td>   
                       </tr>   
                     </table>
<%
	}
%>
					<p>
                    <table width="50%" border="0">
                    <tr>
                      <td align = "right">
                        <input type="submit" name="Submit" value="Submit">
                      </td>
                      <td>
                        <input type="button" name="Cancel" value="Cancel">
                      </td>
                    </tr>
                  </table>
              </form>
                </td>
              </tr>
            </table>
            <p>&nbsp;</p>
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
