<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function validate(form) {
	if (form.action.value == "OptOutProgram" && (form.StartDate.value == "" || form.EndDate.value == "")) {
		alert("The start and end date cannot be empty");
		return false;
	}
	return true;
}
</script>
<script language="JavaScript" src="<%= request.getContextPath() %>/JavaScript/calendar.js">
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
		  <% String pageName = "OptOut.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_OPT_OUT, "PROGRAMS - OPT OUT"); %>
              <%@ include file="include/InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  
              <table width="550" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="MainText" align="center"><cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_DESC_OPT_OUT %>"/>
                  </td>
                </tr>
              </table>
			  <br>
			<form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
			  <input type="hidden" name="action" value="OptOutProgram">
			  <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Programs.jsp">
			  <input type="hidden" name="REDIRECT2" value="<%=request.getContextPath()%>/operator/Consumer/OptForm.jsp">
			  <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/OptOut.jsp">
                <table width="350" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC" align="center">
                  <tr> 
                  <td> 
                      <div align="center"> 
                        <p class="HeaderCell">Temporarily <cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_OPT_OUT_VERB %>" defaultvalue="opt out of"/> 
                          all programs</p>
                        <table width="300" border="0" cellspacing="0" cellpadding="3" class="TableCell2">
                          <tr> 
                            <td align="right" width="120">Start Date:</td>
                            <td width="168"> 
                              <input type="text" name="StartDate" id="StartDate" size="14" value="<%= datePart.format(new Date()) %>">
                              <a href="javascript:openCalendar(document.getElementById('StartDate'))"
					    onMouseOver="window.status='Start Date Calendar';return true;"
						onMouseOut="window.status='';return true;"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="absmiddle" border="0"> 
                              </a> </td>
                          </tr>
                          <tr> 
                            <td align="right" width="120">End Date (midnight of):</td>
                            <td width="168"> 
                              <input type="text" name="EndDate" id="EndDate" size="14" value="<%= datePart.format(new Date()) %>">
                              <a href="javascript:openCalendar(document.getElementById('EndDate'))"
					    onMouseOver="window.status='Start Date Calendar';return true;"
						onMouseOut="window.status='';return true;"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="absmiddle" border="0"> 
                              </a> </td>
                          </tr>
                        </table>
                        <input type="submit" name="Submit" value="Submit">
                      </div>
                    </td>
                </tr>
              </table>
              <p align="center" class="Subtext"><br>
              <table width="366" border="0" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td align="right" width="143"> 
                    <input type="submit" value="Re-enable" onclick="this.form.action.value='ReenableProgram'"
					 <% if (programs.getStarsLMProgramCount() == 0) out.print("disabled"); %>>
                  </td>
                  <td width="211"> 
<%
	String disabled = "disabled";
	if (programHistory != null && programHistory.getStarsLMProgramEventCount() > 0) {
		StarsLMProgramEvent lastEvent = programHistory.getStarsLMProgramEvent(programHistory.getStarsLMProgramEventCount() - 1);
		if (lastEvent.hasDuration() && lastEvent.getEventDateTime().after(new Date()))
			disabled = "";
	}
%>
                    <input type="submit" value="Cancel Scheduled" onclick="this.form.action.value='CancelScheduledOptOut'" <%= disabled%>>
                  </td>
                </tr>
              </table>
			</form>
              <br>
              <form name="form2" method="post" action="OptHist.jsp">
                <p align="center" class="SubtitleHeader">Program History
                <table width="366" border="1" cellspacing="0" align="center" cellpadding="3">
                  <tr> 
                    <td class="HeaderCell" width="75">Date</td>
                    <td class="HeaderCell" width="120">Type - Duration</td>
                    <td class="HeaderCell" width="145">Program</td>
                  </tr>
<%
	if (programHistory != null) {
		int eventCnt = programHistory.getStarsLMProgramEventCount();
		for (int i = eventCnt - 1; i >= 0 && i >= eventCnt - 5; i--) {
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
                    <td class="TableCell" width="75" ><%= datePart.format(event.getEventDateTime()) %></td>
                    <td class="TableCell" width="120" ><%= event.getEventAction() %> 
                      <% if (event.hasDuration()) { %>
                      - <%= durationStr %> 
                      <% } %>
                      <%= scheduledStr %> </td>
                    <td class="TableCell" width="145" ><%= progNames %></td>
                  </tr>
<%
		}
	}
%>
                </table>
<%
	if (programHistory != null && programHistory.getStarsLMProgramEventCount() > 5) {
%>
                <table width="300" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td> 
                      <div align="right">
                        <input type="button" name="More" value="More" onclick="location='ProgHist.jsp'">
                      </div>
                    </td>
                  </tr>
                </table>
<%
	}
%>
              </form>
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
