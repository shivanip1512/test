<%@ include file="StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	String referrer = (String) session.getAttribute(ServletUtils.ATT_REFERRER);
	
	int progNo = 0;
	String progNoStr = request.getParameter("prog");
	if (progNoStr != null) progNo = Integer.parseInt( progNoStr );
	
	StarsLMProgram program = programs.getStarsLMProgram( progNo );
	
	StarsCtrlHistPeriod period = StarsCtrlHistPeriod.ALL;
	String periodStr = request.getParameter("Period");
	if (periodStr != null) period = StarsCtrlHistPeriod.valueOf( periodStr );
	
	StarsLMControlHistory ctrlHist = ServletUtils.getControlHistory( program.getStarsLMControlHistory(), period, tz );
	
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
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
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
                <td width="265" height="28" class="PageHeader">&nbsp;&nbsp;&nbsp;Customer 
                  Account Information</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle"> 
                  <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                </td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
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
            <% String pageName = "ContHist.jsp"; %>
            <%@ include file="Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_CONTROL_HISTORY, "PROGRAMS - CONTROL HISTORY"); %>
              <%@ include file="InfoSearchBar.jsp" %>
             
              <br>
            </div>
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                <td valign="top" bgcolor="#FFFFFF"> 
                  <table width="450" border="0" cellspacing="0" cellpadding="0" align="center">
                    <tr> 
                      <td width="107" valign="top"> 
                        <div align="center"> <img src="../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br>
                          <span class="TableCell"><%= program.getProgramName() %></span><br>
                        </div>
						<br>
                      </td>
                      <td width="343" valign="top"> 
                        <table width="325" border="1" cellspacing="0" cellpadding="3">
                          <tr> 
                            <td class="HeaderCell" width="110">Start</td>
                            <td class="HeaderCell" width="110"> Stop</td>
                            <td class="HeaderCell" width="79"> Duration</td>
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
                            <td class="TableCell" width="110"><%= histDateFormat.format(hist.getStartDateTime()) %></td>
                            <td class="TableCell" width="110"><%= histDateFormat.format(stopTime) %></td>
                            <td class="TableCell" width="79"><%= ServletUtils.getDurationString(durationSec) %></td>
                          </tr>
                          <%
	}
%>
                          <tr> 
                            <td class="TableCell" colspan="2"> 
                              <div align="right">Total:</div>
                            </td>
                            <td class="TableCell" width="79"><%= ServletUtils.getDurationString(totalSec) %></td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
            <div align="center"> 
<%
	if (referrer == null) {
%>
              <input type="button" name="Back" value="Back" onclick="history.back()">
<%
	}
	else {
%>
			  <input type="button" name="Back" value="Back" onclick="location.href='<%= referrer %>'">
<%
	}
%>
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
