<%@ include file="StarsHeader.jsp" %>
<%
	String referrer = (String) session.getAttribute(ServletUtils.ATT_REFERRER);
	
	String progNoStr = request.getParameter("prog");
	int progNo = Integer.parseInt( progNoStr );
	
	StarsLMProgram program = programs.getStarsLMProgram( progNo );
	
	StarsApplianceCategory category = null;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory appCat = categories.getStarsApplianceCategory(i);
		if (appCat.getApplianceCategoryID() == program.getApplianceCategoryID()) {
			category = appCat;
			break;
		}
	}
	
	StarsLMControlHistory ctrlHist = program.getStarsLMControlHistory();
%>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../../demostyle.css" type="text/css">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
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
<script language="JavaScript">
	document.getElementById("Header").background = '../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>';
</script>
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
		  <% String pageName = "ProgramHist.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_PROG_CTRL_HIST_TITLE); %>
              <%@ include file="InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
              <br>
            </div>
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                <td valign="top" bgcolor="#FFFFFF"> 
                  <table width="450" border="0" cellspacing="0" cellpadding="0" align="center">
                    <tr> 
                      <td width="107" valign="top"> 
                        <div align="center">
						  <img src="../<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br>
                          <span class="TableCell"><%= program.getProgramName() %></span><br>
                        </div>
                      </td>
                      <td width="343" valign="top"> 
                        <table width="325" border="1" cellspacing="0" cellpadding="3">
                          <tr> 
                            <td class="HeaderCell"> 
                              <div align="center">Begin Date/Time</div>
                            </td>
                            <td class="HeaderCell"> 
                              <div align="left">Duration</div>
                            </td>
                          </tr>
<%
	int totalSec = 0;
	
	for (int i = ctrlHist.getControlHistoryCount() - 1; i >= 0; i--) {
		ControlHistory hist = ctrlHist.getControlHistory(i);
		
		int durationSec = hist.getControlDuration();
		totalSec += durationSec;
%>
                          <tr> 
                            <td class="TableCell"> 
                              <div align="right"><%= histDateFormat.format(hist.getStartDateTime()) %></div>
                            </td>
                            <td class="TableCell"> 
                              <div align="left"><%= ServletUtils.getDurationString(durationSec) %></div>
                            </td>
                          </tr>
<%
	}
%>
                          <tr> 
                            <td class="Main"> 
                              <div align="right">Total:</div>
                            </td>
                            <td class="Main"> 
                              <div align="left"><%= ServletUtils.getDurationString(totalSec) %></div>
                            </td>
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
              <form name="form1" method="get" action="ProgramHist.jsp">
                <input type="submit" name="Back" value="Back">
              </form>
<%
	}
	else {
%>
			  <input type="button" name="Back" value="Back" onclick="document.URL='<%= referrer %>'">
<%
	}
%>
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
