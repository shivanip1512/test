<%@ include file="include/StarsHeader.jsp" %>
<%
	String referrer = request.getParameter("REFERRER");
	
	int progNo = 0;
	String progNoStr = request.getParameter("prog");
	if (progNoStr != null) progNo = Integer.parseInt( progNoStr );
	
	StarsLMProgram program = programs.getStarsLMProgram( progNo );
	ControlSummary summary = program.getStarsLMControlHistory().getControlSummary();
	if (summary == null) summary = new ControlSummary();
	
	StarsApplianceCategory category = null;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory appCat = categories.getStarsApplianceCategory(i);
		if (appCat.getApplianceCategoryID() == program.getApplianceCategoryID()) {
			category = appCat;
			break;
		}
	}
%>
<html>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
            <% String pageName = "ProgramHist.jsp"; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              
            <div align="center"><br>
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_PROGRAM_CTRL_SUM, "PROGRAM - CONTROL SUMMARY"); %>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
              <table width="450" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td width="107" height="92"> 
                    <div align="center">
<% if (!category.getStarsWebConfig().getLogoLocation().equals("")) { %>
						<img src="../../../WebConfig/<%= category.getStarsWebConfig().getLogoLocation() %>"><br>
<% } %>
                        <span class="TableCell"><%= program.getProgramName() %></span><br>
                    </div>
                  </td>
                  <td width="343" valign="top" height="92"> 
                    <table width="300" border="0" cellspacing="0" bgcolor="white" cellpadding="2" align="center">
                      <tr valign="top" bgcolor="#CCCCCC"> 
                        <td width="117" class="HeaderCell">Time Frame</td>
                        <td width="95" class="HeaderCell">Total</td>
                      </tr>
                      <tr> 
                        <td height="23" class="TableCell" width="117">Today</td>
                        <td height="23" class="TableCell" width="95"><%= ServletUtils.getDurationString(summary.getDailyTime()) %></td>
                      </tr>
                      <tr> 
                        <td height="23" class="TableCell" width="117">Past Month</td>
                        <td height="23" class="TableCell" width="95"><%= ServletUtils.getDurationString(summary.getMonthlyTime()) %></td>
                      </tr>
                      <tr> 
                        <td height="23" class="TableCell" width="117">Seasonal</td>
                        <td height="23" class="TableCell" width="95"><%= ServletUtils.getDurationString(summary.getSeasonalTime()) %></td>
                      </tr>
                      <tr> 
                        <td height="23" class="TableCell" width="117">Annual</td>
                        <td height="23" class="TableCell" width="95"><%= ServletUtils.getDurationString(summary.getAnnualTime()) %></td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <%
	if (referrer == null) {
%>
              <input type="button" name="Back" value="Back" onclick="history.back()">
              <%
	}
	else {
%>
              <input type="button" name="Back2" value="Back" onClick="document.URL='<%= referrer %>'">
              <%
	}
%>
            </div>
			<p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>