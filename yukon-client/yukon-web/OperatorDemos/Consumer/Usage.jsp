<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../Consumer/Calendar1-82.js"></SCRIPT>
<link rel="stylesheet" href="../demostyle.css" type="text/css">
</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../Consumer/ConsumerImage.jpg">&nbsp;</td>
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
          <td width="1" height="102" bgcolor="#000000"><img src="../Consumer/VerticalRule.gif" width="1"></td>
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
        <tr> <form>
          <td  valign="top" width="101">
		  <% String pageName = "Usage.jsp"; %>
          <%@ include file="Nav.jsp" %>
          </td>
          </form>
          <td width="1" bgcolor="#000000"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"><% String header = "METERING - USAGE"; %><%@ include file="InfoSearchBar.jsp" %><br>
             </div>
            <table width="610" border="0" cellspacing="0" cellpadding="2" align="center">
              <tr> 
                <td width="0"> </td>
                <td width="273" valign="top"> <form name = "MForm"> 
                  <table width="280" border="2" cellspacing="0" cellpadding="3">
                    <tr bgcolor="#CCCCCC"> 
                      <td width="99"> <font face="Arial, Helvetica, sans-serif" size="1">Start 
                          Date:</font><br>
                         <input type="text" name="start" value="04/04/01" size="8">
                          <a href="javascript:show_calendar('MForm.start')"
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;"> <img src="../Consumer/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a>
                        </td>
                        <td width="84"> <font face="Arial, Helvetica, sans-serif" size="1">Time 
                          Period:<br>
                          </font> 
                          <select name="period">
                            <option>1 Day</option>
                            <option>3 Days</option>
                            <option>1 Week</option>
                            <option>1 Month</option>
                          </select>
                        </td>
                        <td width="69"> 
                          <center>
                            <input type="image" src="../Consumer/GoButton.gif" name="image" border="0">
                          </center>
                        </td>
                      </tr>
                  </table></form>
                </td>
                <td width="347"> 
                  <table width="338" valign="top" cellpadding="0" cellspacing="0">
                    <tr>
                      <td width="85" valign="top"> 
                        <div align="center">
                          <input type="submit" name="Graph" value="Graph">
                        </div>
                      </td><form method="get" action="UsageSummary.jsp">
                      <td width="251" valign="top">
                        <input type="submit" name="Summary" value="Summary">
                      </td></form>
                    </tr>
                  </table>
                  <table width="340" border="0" cellspacing="0" cellpadding="3">
                    <tr> 
                      <td align="right">&nbsp; </td>
                    </tr>
                  </table>
                </td>
                </tr>
            </table>
            <p align="center"><img src="TOU.gif"></p>
          </td>
        <td width="1" bgcolor="#000000"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>

</html>
