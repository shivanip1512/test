<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
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
		  <% String pageName = "ProgramHist.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><% String header = "PROGRAMS - CONTROL HISTORY"; %><%@ include file="InfoSearchBar.jsp" %><br>
             
              <br>
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr bgcolor="#FFFFFF"> 
                  <td width="162" class="HeaderCell"> 
                    <div align="center">Enrolled Programs</div>
                  </td>
                  <td width="332" class="HeaderCell"> 
                    <div align="center">Recent Control History <br>
                      (since midnight yesterday)</div>
                  </td>
                  <td width="332" class="HeaderCell"> 
                    <div align="center">Control History Summary</div>
                  </td>
                  <td width="332" class="HeaderCell">
                    <div align="center">Complete Control History</div>
                  </td>
                </tr>
<%
	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
		StarsLMProgram program = programs.getStarsLMProgram(i);
		StarsApplianceCategory category = null;
		
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			if (appCat.getApplianceCategoryID() == program.getApplianceCategoryID()) {
				category = appCat;
				break;
			}
		}
%>
                <tr bgcolor="#FFFFFF"> 
                  <td width="162"> 
                    <div align="center"> <img src="<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br>
					  <span class="TableCell"><%= program.getProgramName() %></span>
					</div>
                  </td>
                  <td width="332" valign="top"> 
                    <table width="200" border="0" cellspacing="0" cellpadding="3" align="center">
                      <tr> 
                        <td width="220" class="TableCell"> Begin Date/Time </td>
                        <td width="93" class="TableCell"> Duration </td>
                      </tr>
<%
		StarsLMControlHistory ctrlHistToday = ServletUtils.getTodaysControlHistory( program.getStarsLMControlHistory() );
		if (ctrlHistToday.getControlHistoryCount() == 0) {
%>
                      <tr> 
                        <td width="219" class="TableCell">No Control </td>
                        <td width="94" class="TableCell">---- </td>
                      </tr>
                      <tr> 
                        <td width="219" class="TableCell"> 
                          <div align="right">Total: </div>
                        </td>
                        <td width="94" class="TableCell">---- </td>
                      </tr>
<%
		}
		else {
			int totalSec = 0;
			for (int j = 0; j < ctrlHistToday.getControlHistoryCount(); j++) {
				ControlHistory hist = ctrlHistToday.getControlHistory(j);
				
				int durationSec = hist.getControlDuration();
				totalSec += durationSec;
%>
                      <tr> 
                        <td width="220" class="TableCell"><%= histDateFormat.format(hist.getStartDateTime()) %></td>
                        <td width="93" class="TableCell"><%= ServletUtils.getDurationString(durationSec) %></td>
                      </tr>
<%
			}
%>
                      <tr> 
                        <td width="220" class="TableCell"> 
                          <div align="right">Total:</div>
                        </td>
                        <td width="93" class="TableCell"><%= ServletUtils.getDurationString(totalSec) %></td>
                      </tr>
<%
		}
%>
                    </table>
                  </td>
<%
		ControlSummary summary = program.getStarsLMControlHistory().getControlSummary();
%>
                  <td width="332" valign="top"> 
                    <table width="150" border="0" cellspacing="0" bgcolor="white" cellpadding="2" align="center">
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
                  <td width="332"> 
					<form method="POST" action="/servlet/SOAPClient">
					<input type="hidden" name="action" value="GetLMCtrlHist">
					<input type="hidden" name="Group" value="<%= program.getGroupID() %>">
					<input type="hidden" name="prog" value="<%= i %>">
					<input type="hidden" name="REDIRECT" value="/OperatorDemos/Consumer/ContHist.jsp">
					<input type="hidden" name="REFERRER" value="ProgramHist.jsp">
                    <table width="100" border="0" cellspacing="0" cellpadding="3" align="center">
                      <tr> 
					    <td width="180" valign="top" align="center"> 
						  <select name="Period">
							<option value="PastWeek">Past Week</option>
							<option value="PastMonth">Past Month </option>
							<option value="All">All</option>
						  </select>
						</td>
                      </tr>
                      <tr> 
                          <td width="180" valign="top" align="center"> 
                            <input type="submit" name="Input2" value="View">
                          </td>
                      </tr>
                    </table>
					</form>
                  </td>
                </tr>
<%
	}
%>
              </table>
            </div>
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
