<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%
	StarsGetEnrollmentProgramsResponse categories = (StarsGetEnrollmentProgramsResponse)
			session.getAttribute("ENROLLMENT_PROGRAMS_THERMOSTAT");
	if (categories == null) {
		response.sendRedirect("/UserDemos/ConsumerStat/login.jsp"); return;
	}
%>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<SCRIPT LANGUAGE="JavaScript">

<!-- This script and many more are available free online at -->
<!-- The JavaScript Source!! http://javascript.internet.com -->
<!-- Evaldo Nunes (woodstock@globo.com)  -->

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


<!-- Begin
function Check(x,y) {
for(i=1;i<=4;i++) {
z = "option" + i ;
document.all[z].src = "http://javascript.internet.com/img/radio-buttons/off.gif" ;
}
document.all[x].src = "http://javascript.internet.com/img/radio-buttons/on.gif"
document.all.action.value = x
}
//  End -->

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
<body bgcolor="#666699" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip" style="left: -50px"> </div>
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr bgcolor="#FFFFFF"> 
          <td width="102" height="102" background="Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr bgcolor="666699"> 
                <td width="265" height="28">&nbsp;&nbsp;&nbsp;</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="login.jsp" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src="stat/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="#666699" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
		    <form method="post" action="Wizard4.jsp">
			<input type="hidden" name="AcctNo" value="<%= request.getParameter("AcctNo") %>">
            <div align="center"><br> 
              <p><b><span class="Main">SIGN UP WIZARD<br>
                </span></b></p>
              <p class = "Main">Select the energy programs to sign-up for</p>
                <table width="64%" border="1" cellspacing = "0">
<%
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		if (i % 2 == 0) {	// Two programs in a row
%>
                  <tr>
<%
		}
		/* Table of program */
%>
                    <td width="50%"> 
                      <table width="100%" border="0" height="80">
                      <tr> 
                        <td align = "center" width="15%"> 
                          <p><b><img id="<%= i %>" src="<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59" onclick = "toolTipAppear(event, 'tool', <%= i %>, 350, text)"></b><br>
                            <font face="Arial, Helvetica, sans-serif" size="1">click on icon for a description</font></p>
                        </td>
                        <td width="85%" valign="top"> 
                          <table width="102%" border="0" height="74">
                            <tr> 
                              <td class = "TableCell" valign="top"><b> 
                                <input type="checkbox" name="AppCat" value="<%= category.getApplianceCategoryID() %>" onclick="changeCategory(this, <%= i %>)">
                                <%= category.getStarsWebConfig().getAlternateDisplayName() %></b></td>
								<input type="hidden" name="CatID" value="">
								<input type="hidden" name="ProgID" value="">
								<input type="hidden" name="DefProgID" value="<%= category.getStarsEnrLMProgram(0).getProgramID() %>">
                            </tr>
<%
		if (category.getStarsEnrLMProgramCount() > 1) {
			/* If more than one program under this category, show the program list */
%>
                            <tr> 
                              <td> 
                                <table class = "TableCell" width="100%" border="0">
<%
			for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
				StarsEnrLMProgram program = category.getStarsEnrLMProgram(j);
				/* Each row is a program in this category */
%>
                                  <tr> 
                                    <td align = "right" width="30%"> 
                                      <input type="radio" name="Program<%= i %>" value="<%= program.getProgramID() %>" onclick="changeProgram(this, <%= i %>)">
                                    </td>
                                    <td width="70%"><%= program.getStarsWebConfig().getAlternateDisplayName() %></td>
                                  </tr>
<%
			}	// End of program
%>
								</table>
                              </td>
                            </tr>
<%
		}	// End of program list
%>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
<%
		if (i % 2 == 1) {
%>
                </tr>
<%
		}
	}	// Enf of all programs
	
	if (categories.getStarsApplianceCategoryCount() % 2 == 1) {
		/* If number of programs is odd, fill in the last table cell */
%>
                  <td width="50%">&nbsp;</td>
				</tr>
<%
	}
%>
<!--
                        <tr> 
                          <td align = "center" width="15%"> 
                            <p><b><img id = "0" src="AC.gif" width="60" height="59" onclick = "toolTipAppear(event, 'tool', 0, 350, 90)"></b><font face="Arial, Helvetica, sans-serif" size="1"><br>
                              click on icon for a description</font></p>
                          </td>
                          <td width="85%"> 
                            <table width="102%" border="0" height="74">
                              <tr> 
                                <td class = "TableCell"><b> 
                                  <input type="checkbox" name="checkbox" value="checkbox">
                                  Cycle AC</b></td>
                              </tr>
                              <tr> 
                                <td> 
                                  <table class = "TableCell" width="100%" border="0">
                                    <tr> 
                                      <td align = "right" width="30%"> 
                                        <input type="radio" name="radiobuttonAC" value="radiobutton">
                                      </td>
                                      <td width="70%">Light</td>
                                    </tr>
                                    <tr> 
                                      <td align = "right" width="30%"> 
                                        <input type="radio" name="radiobuttonAC" value="radiobutton">
                                      </td>
                                      <td width="70%">Medium</td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="50%">
                      <table width="100%" border="0" height="80">
                        <tr> 
                          <td align = "center" width="15%"> 
                            <p><b><img id = "0" src="SetBack.gif" width="60" height="59" onClick = "toolTipAppear(event, 'tool', 1, 350, 90)"></b><font face="Arial, Helvetica, sans-serif" size="1"><br>
                              click on icon for a description</font></p>
                          </td>
                          <td width="85%"> 
                            <table width="102%" border="0" height="74">
                              <tr> 
                                <td class = "TableCell"><b> 
                                  <input type="checkbox" name="checkbox2" value="checkbox">
                                  SetBack</b></td>
                              </tr>
                              <tr> 
                                <td> 
                                  <table class = "TableCell" width="100%" border="0">
                                    <tr> 
                                      <td align = "right" width="30%"> 
                                        <input type="radio" name="radiobuttonSB" value="radiobutton">
                                      </td>
                                      <td width="70%">2 degrees</td>
                                    </tr>
                                    <tr> 
                                      <td align = "right" width="30%"> 
                                        <input type="radio" name="radiobuttonSB" value="radiobutton">
                                      </td>
                                      <td width="70%">4 degrees</td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
-->
              </table>
              <br>
            </div>
            <table width="150" border="0" cellspacing="0" cellpadding="3" align="center">
              <tr> 
                  <td width="95" valign="top" align="center"> 
                    <input type="submit" name="Submit" value="  Next  ">
                  </td>
                  <td width="98" valign="top" align="center"> 
                    <input type = "button" value="Cancel" name = "cancel" onclick = "history.back()">
                  </td>
              </tr>
            </table>
            </form>
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
