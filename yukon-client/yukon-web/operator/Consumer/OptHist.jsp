<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
<!--
function confirmSubmit(form) { //v1.0
  if (form.OptOutPeriod.value == 0) return false;
  return confirm('Are you sure you would like to temporarily opt out of all programs?');
}
//-->
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
          <td  valign="top" width="101">
		  <% String pageName = "OptOut.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_OPT_OUT, "PROGRAMS - OPT OUT") + " HISTORY"; %>
              <%@ include file="include/InfoSearchBar.jsp" %>
			  
              <table width="360" border="1" cellspacing="0" align="center" cellpadding="3">
                  <tr> 
                    <td class="HeaderCell" width="100">Date</td>
                    <td class="HeaderCell" width="154">Type - Duration</td>
                    <td class="HeaderCell" width="100">Program</td>
                  </tr>
<%
	if (programHistory != null) {
		for (int i = programHistory.getStarsLMProgramEventCount() - 1; i >= 0; i--) {
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
              <p align="center" class="Subtext"><br>
              <table width="150" border="0" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td align="center">
                    <input type="button" name="Back" value="Back" onclick="location='OptOut.jsp'">
                  </td>
                </tr>
              </table>
              <br>
            </div>
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
