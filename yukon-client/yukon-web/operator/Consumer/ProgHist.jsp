<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

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
		  <% String pageName = "ProgHist.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "PROGRAM HISTORY"; %>
              <%@ include file="include/InfoSearchBar.jsp" %>
              <br>
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
			if (event.hasDuration())
				durationStr = ServletUtils.getDurationFromHours(event.getDuration());
			
			String scheduledStr = "";
			if (event.hasDuration() && event.getEventDateTime().after(new Date()))
				scheduledStr = "(Scheduled)";
			
			String progNames = "";
			for (int j = 0; j < event.getProgramIDCount(); j++) {
				for (int k = 0; k < programs.getStarsLMProgramCount(); k++) {
					if (programs.getStarsLMProgram(k).getProgramID() == event.getProgramID(j)) {
						progNames += programs.getStarsLMProgram(k).getProgramName() + "<br>";
						break;
					}
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
                    <input type="button" name="Back" value="Back" onclick="history.back()">
                  </td>
                </tr>
              </table>
              <br>
            </div>
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
