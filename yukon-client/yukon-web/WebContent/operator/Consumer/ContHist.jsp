<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int progNo = 0;
	String progNoStr = request.getParameter("prog");
	if (progNoStr != null) progNo = Integer.parseInt( progNoStr );
	
	StarsLMProgram program = programs.getStarsLMProgram( progNo );
	
	StarsCtrlHistPeriod period = StarsCtrlHistPeriod.ALL;
	String periodStr = request.getParameter("Period");
	if (periodStr != null) period = StarsCtrlHistPeriod.valueOf( periodStr );
	
	StarsLMControlHistory ctrlHist = ServletUtils.getControlHistory( program, appliances, period, liteEC, lYukonUser, account.getAccountID() );
	
	StarsApplianceCategory category = null;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		if (categories.getStarsApplianceCategory(i).getApplianceCategoryID() == program.getApplianceCategoryID()) {
			category = categories.getStarsApplianceCategory(i);
			break;
		}
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
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
            <% String pageName = "ContHist.jsp"; %>
            <%@ include file="include/Nav.jspf" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_CONTROL_HISTORY); %>
              <%@ include file="include/InfoSearchBar.jspf" %>
             
              <br>
            </div>
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                <td valign="top" bgcolor="#FFFFFF"> 
                  <table width="450" border="0" cellspacing="0" cellpadding="0" align="center">
                    <tr> 
                      <td width="50" valign="top"> 
                        <div align="center">
<% if (!category.getStarsWebConfig().getLogoLocation().equals("")) { %>
						  <img src="../../WebConfig/<%= category.getStarsWebConfig().getLogoLocation() %>"><br>
<% } %>
                          <span class="TableCell"><%= program.getProgramName() %></span><br>
                        </div>
						<br>
                      </td>
                      <td width="400" valign="top"> 
                        <table width="400" border="1" cellspacing="0" cellpadding="3">
                          <tr> 
                            <td class="HeaderCell" width="200">Program Event Start</td>
                            <td class="HeaderCell" width="200">Control Time For This Account</td>
                          </tr>
                          <%
	int totalSec = 0;
	
	for (int i = ctrlHist.getControlHistoryCount() - 1; i >= 0 ; i--) {
		ControlHistory hist = ctrlHist.getControlHistory(i);
		
		int durationSec = hist.getControlDuration();
		totalSec += durationSec;
		Date stopTime = new Date(hist.getStartDateTime().getTime() + durationSec * 1000);
%>
                          <tr> 
                            <td class="TableCell" width="200"><cti:formatDate value="<%=hist.getStartDateTime()%>" type="BOTH"/></td>
                            <td class="TableCell" width="200"><%= ServletUtils.getDurationFromSeconds(durationSec) %></td>
                          </tr>
                          <%
	}
%>
                          <tr> 
                            <td class="TableCell"> 
                              <div align="right">Total:</div>
                            </td>
                            <td class="TableCell" width="200"><%= ServletUtils.getDurationFromSeconds(totalSec) %></td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
            <div align="center"> 
              <input type="button" name="Back" value="Back" onclick="history.back()">
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
