<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function setRedirect(form) {
	if (form.Period.value == '<%= StarsCtrlHistPeriod.NONE.toString() %>')
		form.action = 'Summary.jsp';
	else
		form.action = 'ContHist.jsp';
}
</script>
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
		  <% String pageName = "ProgramHist.jsp"; %>
          <%@ include file="include/Nav.jspf" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_CONTROL_HISTORY); %>
              <%@ include file="include/InfoBar.jspf" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <br>
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr bgcolor="#FFFFFF"> 
                  <td width="150" class="HeaderCell"> 
                    <div align="center">Enrolled Programs</div>
                  </td>
                  <td width="262" class="HeaderCell"> 
                    <div align="center">Today's <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_CONTROL %>" defaultvalue="control" format="capital"/> History</div>
                  </td>
                  <td width="180" class="HeaderCell"> 
                    <div align="center">Complete <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_CONTROL %>" defaultvalue="control" format="capital"/> History</div>
                  </td>
                </tr>
<%
	for (int i = 0; i < starsLMPermissionBean.getStarsEnrolledLMPrograms().getStarsLMProgramCount(); i++) {
		StarsLMProgram program = starsLMPermissionBean.getStarsEnrolledLMPrograms().getStarsLMProgram(i);
		StarsApplianceCategory category = null;
		
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			if (appCat.getApplianceCategoryID() == program.getApplianceCategoryID()) {
				category = appCat;
				break;
			}
		}
		
		// New enrollment, opt out, and control history tracking
   		//-------------------------------------------------------------------------------
		List<Integer> inventoryIds = partialOptOutMap.get(program.getProgramID());
		String programStatus = program.getStatus();
		boolean partialOutOfService = false;
		if(inventoryIds != null && inventoryIds.size() > 0 && program.getStatus().equalsIgnoreCase(ServletUtils.OUT_OF_SERVICE)) { 
			programStatus = "Partially out of service.  Still active for " + inventoryIds.size() + (inventoryIds.size() == 1 ? " device." : " devices.");
			partialOutOfService = true;
		}
		//-------------------------------------------------------------------------------
%>
                <tr bgcolor="#FFFFFF"> 
                  <td width="150"> 
                    <div align="center">
<% if (!category.getStarsWebConfig().getLogoLocation().equals("")) { %>
					  <img src="../../../WebConfig/<%= category.getStarsWebConfig().getLogoLocation() %>"><br>
                      <% } %>
                      <span class="TableCell"><%= program.getProgramName() %></span><br>
                    </div>
                  </td>
                  <td width="262" valign="top"> 
                    <%
		if (program.getStatus().equalsIgnoreCase(ServletUtils.OUT_OF_SERVICE)) {
%>
                    <div align="center" class="TableCell"><%=programStatus%></div>
<%
		}
		else {
			if(partialOutOfService) { %>
		    	<div align="center" class="TableCell"><%=programStatus%></div>
<%			}
%>
                    <table width="200" border="0" cellspacing="0" cellpadding="3" align="center">
                      <tr> 
                        <td width="61" class="TableCell"> 
                          <div align="left">Start</div>
                        </td>
                        <td width="61" class="TableCell">Stop</td>
                        <td width="60" class="TableCell">Duration</td>
                      </tr>
<%
			StarsLMControlHistory ctrlHistToday = ServletUtils.getControlHistory( program, appliances, StarsCtrlHistPeriod.PASTDAY,liteEC );
			if (ctrlHistToday.getControlHistoryCount() == 0) {
%>
                      <tr> 
                        <td width="61" class="TableCell">No <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_CONTROL %>" defaultvalue="control" format="capital"/></td>
                        <td width="61" class="TableCell"></td>
                        <td width="60" class="TableCell">----</td>
                      </tr>
                      <tr> 
                        <td width="61" class="TableCell"></td>
                        <td width="61" class="TableCell">
                          <div align="right">Total:</div>
                        </td>
                        <td width="60" class="TableCell">----</td>
                      </tr>
<%
			}
			else {
				int totalSec = 0;
				for (int j = 0; j < ctrlHistToday.getControlHistoryCount(); j++) {
					ControlHistory hist = ctrlHistToday.getControlHistory(j);
					
					int durationSec = hist.getControlDuration();
					totalSec += durationSec;
					Date stopTime = new Date(hist.getStartDateTime().getTime() + durationSec * 1000);
%>
                      <tr> 
                        <td width="61" class="TableCell"><%= timePart.format(hist.getStartDateTime()) %></td>
                        <td width="61" class="TableCell"><%= timePart.format(stopTime) %></td>
                        <td width="60" class="TableCell"><%= ServletUtils.getDurationFromSeconds(durationSec) %></td>
                      </tr>
<%
				}
%>
                      <tr> 
                        <td width="61" class="TableCell"> 
                          <div align="right"></div>
                        </td>
                        <td width="61" class="TableCell">
                          <div align="right">Total:</div>
                        </td>
                        <td width="60" class="TableCell"><%= ServletUtils.getDurationFromSeconds(totalSec) %></td>
                      </tr>
<%
			}
%>
                    </table>
<%
		}
%>
                  </td>
                  <td width="180"> 
                    <form method="POST" action="ContHist.jsp">
                      <input type="hidden" name="prog" value="<%= i %>">
                      <table width="100" border="0" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td width="180" valign="top" align="center"> 
                            <select name="Period" onchange="setRedirect(this.form)">
							  <option value="<%= StarsCtrlHistPeriod.PASTWEEK.toString() %>">Past Week</option>
							  <option value="<%= StarsCtrlHistPeriod.PASTMONTH.toString() %>">Past Month </option>
							  <option value="<%= StarsCtrlHistPeriod.ALL.toString() %>">All</option>
                              <option value="<%= StarsCtrlHistPeriod.NONE.toString() %>">Summary</option>
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
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
