<!- This is setup to run with OPERATOR side only, as of now anyway 8/24/04-->
<html>
<%@ include file="../operator/Consumer/include/StarsHeader.jsp" %>
<!-- Java script needed for the Calender Function--->
<SCRIPT  LANGUAGE="JavaScript" SRC="../JavaScript/calendar.js"></SCRIPT>
<head>

<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css"> 
</head>
<body class="Background" leftmargin="0" topmargin="0">
<META NAME="robots" CONTENT="noindex, nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
function loadTarget(form)
{
	var extGroup = form.ext;
	for (var i = 0; i < extGroup.length; i++)
	{
		if( extGroup[i].checked)
		{
			if( extGroup[i].value == 'png')
			{
				form.target = '_blank';
				form.REDIRECT.value = '../analysis/reporting_png.jsp';
			}
			else
			{
				form.target = "";
			}
		}
	}
}
</script>
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../WebConfig/yukon/AdminImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Reporting</td>
                <td width="253" valign="middle">&nbsp;</td>
				<td width="58" valign="middle">
             	  <div align="center"><span class="MainText"><a href="../operator/Operations.jsp" class="Link3">Home</a></span></div>
				</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
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
            <% String pageName = "reporting.jsp"; %>
<!--            <%@ include file="../operator/Consumer/include/Nav.jsp" %>-->
          </td>
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF" > 
            <table width="575" border="0" align="center" cellpadding="4" cellspacing="0">
              <tr> 
                <td width="80%" valign="top" align="center"> 
                  <div> <br>
                    <br>
                    <br>
                    <form id="MForm" name="MForm" method="POST" action="<%=request.getContextPath()%>/servlet/ReportGenerator?" onSubmit="loadTarget(document.MForm)">
                      <!-- THE EXTRA INPUT TYPES ARE MAKING THE DOWNLOAD DIALOG APPEAR TWO TIMES -->
                      <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
                      <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                      <input type="hidden" name="ACTION" value="DownloadReport">
                      <table width="538" border="0" cellspacing="2" cellpadding="0">
                        <tr> 
                          <td class = "TableCell" width="50%" valign = "top" align="left" height="100%"> 
                            <table width="100%" border="1" cellspacing="0" cellpadding="0" class="TableCell">
                              <tr> 
                                <td> 
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class = "TableCell" height="19">
                                    <tr> 
                                      <td class="HeaderCell" align="center">Please 
                                        select a Report to Generate</td>
                                    </tr>
                                    <tr> 
                                      <td> 
                                        <table class="TableCell" align="center" width="33%">
                                          <%
                                  for( int j = 0; j < ReportTypes.getGroupToTypeMap()[ReportTypes.STARS_REPORTS_GROUP].length; j++ ){%>
                                          <tr> 
                                            <td width="20%"> 
                                              <input id = "type" type="radio" name="type" value="<%=ReportTypes.getGroupToTypeMap()[ReportTypes.STARS_REPORTS_GROUP][j]%>"
                                      <% if( j == 0){%>
                                         checked=true
                                      <%}
                                      //Below is the ending tag for radio input%>
                                      >
                                              <%=ReportTypes.getReportName(ReportTypes.getGroupToTypeMap()[ReportTypes.STARS_REPORTS_GROUP][j])%> 
                                            </td>
                                          </tr>
                                          <%}%>
                                        </table>
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
                          <td width="50%" height="100%" valign="top"> 
                            <table width="100%" border="1" cellspacing="0" cellpadding="0" class="TableCell">
                              <tr> 
                                <td> 
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell" height="111%">
                                    <tr> 
                                      <td class="HeaderCell" align="center" colspan="3">Options</td>
                                    </tr>
                                    <tr> 
                                      <td>&nbsp;</td>
                                    </tr>
                                    <tr height="100"> 
                                      <td width="47%" valign="top" align="right"><font face="Arial, Helvetica, sans-serif" size="1">Start Date:</font> 
                                        <input id="startCal" type="text" name="start" value="<%= datePart.format(ServletUtil.getToday()) %>" size="8">
                                        <a href="javascript:openCalendar(document.getElementById('MForm').startCal)"
						                      onMouseOver="window.status='Start Date Calendar';return true;"
            						          onMouseOut="window.status='';return true;"> 
                                        <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"> 
                                        </a></td>
                                      <td>&nbsp;&nbsp;</td>
                                      <td width="47%" valign="top"><font face="Arial, Helvetica, sans-serif" size="1">Stop Date:</font> 
                                        <input id="stopCal" type="text" name="stop" value="<%= datePart.format(ServletUtil.getTomorrow()) %>" size="8">
                                        <a href="javascript:openCalendar(document.getElementById('MForm').stopCal)"
						                      onMouseOver="window.status='Stop Date Calendar';return true;"
            						          onMouseOut="window.status='';return true;"> 
                                        <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"> 
                                        </a> </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
                          <td width="50%" height="100%" valign="top"> 
                            <table width="100%" border="1" cellspacing="0" cellpadding="0" class="TableCell">
                              <tr> 
                                <td> 
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                    <tr class="HeaderCell"> 
                                      <td class="HeaderCell" align="center" colspan="3">Generate Report</td>
                                    </tr>
                                    <tr> 
                                      <td width="47%"> 
                                        <table class="TableCell" align="right">
                                          <tr> 
                                            <td align="center"><input type="radio" name="ext" value="png" checked=true>PNG</td>
                                          </tr>
                                          <tr> 
                                            <td align="center"><input type="radio" name="ext" value="pdf">PDF</td>
                                          </tr>
                                        </table>
                                      </td>
                                      <td>&nbsp;</td>
                                      <td width="47%"> 
                                        <table class="TableCell" align="left">
                                          <tr> 
                                            <td align="center"> 
                                              <input type="image" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButton.gif" name="View" border="0" alt="View" align="middle"">
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
                      </table>
                    </form>
                  </div>
                </td>
              </tr>
            </table>
            <br>
          </td>
        </tr>
      </table>
</table>
</body>
</html>
