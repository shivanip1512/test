<html>

<link id="CssLink" rel="stylesheet" href="../demostyle.css" type="text/css">
<% if (ecWebSettings.getURL().length() > 0) { %>
	<script language="JavaScript">document.getElementById("CssLink").href = "../<%= ecWebSettings.getURL() %>";</script>
<% } %>

<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../Consumer/Calendar1-82.js"></SCRIPT>
<SCRIPT Language="Javascript">
function fsub() {                                          
	document.MForm.submit();
}

function TryCallFunction() {
	var sd = document.MForm.mydate1.value.split("-");
	document.MForm.iday.value = sd[1];
	document.MForm.imonth.value = sd[0];
	document.MForm.iyear.value = sd[2];
}

function Today() {
	var dd = new Date();
	return((dd.getMonth()+1) + "/" + dd.getDate() + "/" + dd.getFullYear());
}
</SCRIPT>

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
                <td id="Header" colspan="4" height="74" background="../Header.gif">&nbsp;</td>
<% if (ecWebSettings.getLogoLocation().length() > 0) { %>
	<script language="JavaScript">document.getElementById("Header").background = "../<%= ecWebSettings.getLogoLocation() %>";</script>
<% } %>
              </tr>
              <tr> 
                <td width="265" height="28" class="Header3">&nbsp;&nbsp;&nbsp;Customer 
                  Account Information</td>
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
		  <% String pageName = "UsageSummary.jsp"; %>
          <%@ include file="Nav.jsp" %>
            </td>
          </form>
          <td width="1" bgcolor="#000000"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"><% String header = "METERING - USAGE"; %><%@ include file="InfoSearchBar.jsp" %>
              <hr></div>
            <table width="610" border="0" cellspacing="0" cellpadding="2" align="center">
              <tr> 
                <td width="18"> </td>
                <td width="280" valign="top"> 
                  <table width="280" border="2" cellspacing="0" cellpadding="3">
                    <tr bgcolor="#CCCCCC"> 
                      <td width="99" class="NavText">Start 
                          Date:<br>
                          <input type="text" name="start" value="04/04/01" size="8">
                          <a href="javascript:show_calendar('MForm.start')"
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;"> <img src="../Consumer/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a> 
                        </td>
                        <td width="84" class="NavText">Time Period:<br>
                          <br>
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
                  </table>
                </td>
                <td width="340"> 
                  <table width="338" valign="top" cellpadding="0" cellspacing="0">
                    <tr><form method="get" action="Usage.jsp">
                      <td width="85" valign="top"> 
                        <div align="center">
                          <input type="submit" name="Graph" value="Graph">
                        </div>
                      </td></form>
                      <td width="251" valign="top">
                        <input type="submit" name="Summary" value="Summary">
                      </td>
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
            <p align="center" class="Main">&nbsp;</p>
            <table width="300" border="1" cellspacing="0" align="center" cellpadding="3">
              <tr> 
                <td class="HeaderCell" width="87">Time Use Rate</td>
                <td class="HeaderCell" width="107">Time</td>
                <td class="HeaderCell" width="80">kWh</td>
              </tr>
              <tr> 
                <td width="87" class="TableCell">1</td>
                <td width="107" class="TableCell">21:00 - 06:00</td>
                <td width="80" class="TableCell">1</td>
              </tr>
              <tr> 
                <td width="87" class="TableCell">2</td>
                <td width="107" class="TableCell">06:00 - 10:00</td>
                <td width="80" class="TableCell">4.5</td>
              </tr>
              <tr> 
                <td width="87" class="TableCell">3</td>
                <td width="107" class="TableCell">10:00 - 17:00</td>
                <td width="80" class="TableCell">3.5</td>
              </tr>
              <tr> 
                <td width="87" class="TableCell">4</td>
                <td width="107" class="TableCell">17:00 - 21:00</td>
                <td width="80" class="TableCell">6.2</td>
              </tr>
            </table>
            <p align="center">&nbsp;</p>
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
