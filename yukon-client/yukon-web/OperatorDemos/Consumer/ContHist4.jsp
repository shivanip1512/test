<%
	String backURL = null;
	String appNoStr = request.getParameter("AppNo");
	if (appNoStr != null)
		backURL = "Appliance.jsp?AppNo=" + appNoStr;
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0" link="#FFFFFF" vlink="#FFFFFF" alink="#FFFFFF">
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
		  <% String pageName = "ContHist4.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
		  <div align="center"><% String header = "APPLIANCES - WATER HEATER"; %><%@ include file="InfoSearchBar.jsp" %><br>
           
              <br>
            </div> 
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                <td valign="top" bgcolor="#FFFFFF"> 
                  <table width="450" border="0" cellspacing="0" cellpadding="0" align="center">
                    <tr> 
                      <td width="107"> 
                        <div align="center"><img src="WaterHeater.gif" width="60" height="59"><br>
                          <br>
                        </div>
                      </td>
                      <td width="343" valign="top"> 
                        <table width="325" border="0" cellspacing="0" cellpadding="3">
                          <tr> 
                            <td class="HeaderCell"> 
                              <div align="center">Begin Date/Time</div>
                            </td>
                            <td class="HeaderCell"> 
                              <div align="left">Duration</div>
                            </td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> 
                              <div align="center">No Control</div>
                            </td>
                            <td class="TableCell"> 
                              <div align="left">---- </div>
                            </td>
                          </tr>
                          <tr> 
                            <td class="Main"> 
                              <div align="right">Total:</div>
                            </td>
                            <td class="Main"> 
                              <div align="left">---- </div>
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
	if (backURL == null) {
%>
              <form name="form1" method="get" action="WH.jsp">
                <input type="submit" name="Back" value="Back">
              </form>
<%
	}
	else {
%>
				<input type="button" name="Back" value="Back" onclick="document.URL='<%= backURL %>'">
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
<p>&nbsp;</p>
<p><br>
</p>
</body>
</html>
