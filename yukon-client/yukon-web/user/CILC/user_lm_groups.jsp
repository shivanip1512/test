<%@ include file="../include/user_header.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="150" height="102" background="../../WebConfig/yukon/MomWide.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr>
			  <td width="265" height="28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;User Control</td>  
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="150"> 
		  <% String pageName = "user_lm_control.jsp"; %>
          <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		    <td width="600" valign="top" bgcolor="#FFFFFF"> 
              <div align="center">
                <div align="center"> 
                  <p class="TitleHeader"><br>AUTO CONTROL GROUPS</p>
                  <p align="center" class="MainText">BUILDING 1 TOTALS</p>
                  <table width="400" border="1" cellspacing="0" bgcolor="white" cellpadding="2" align="center">
                    <tr valign="top"> 
                      <td width="117" class="HeaderCell">Time Frame</td>
                      <td width="95" class="HeaderCell">Total</td>
                    </tr>
                    <tr> 
                      <td height="23" class="TableCell" width="117">Today</td>
                      <td height="23" class="TableCell" width="95">4 Hr</td>
                    </tr>
                    <tr> 
                      <td height="23" class="TableCell" width="117">Past Month</td>
                      <td height="23" class="TableCell" width="95">7 Hr, 42 Min</td>
                    </tr>
                    <tr> 
                      <td height="23" class="TableCell" width="117">Seasonal</td>
                      <td height="23" class="TableCell" width="95">32 Hr, 42 Min</td>
                    </tr>
                    <tr> 
                      <td height="23" class="TableCell" width="117">Annual</td>
                      <td height="23" class="TableCell" width="95">47 Hr, 42 Min</td>
                    </tr>
                  </table>
                  <p align="center" class="MainText">Complete History</p>
				  <form method="post" action"user_lm_groups.jsp">
                  <table width="150" border="0" cellspacing="0" cellpadding="0" align="center">
                    <tr> 
                      <td width="106"> 
                        <center>
                          <select name="select">
                            <option>Past Day</option>
                            <option>Past Week</option>
                            <option>Past Month</option>
                          </select>
                        </center>
                      </td>
                      <td width="44">
<input type="submit" name="Submit" value="GO">
                      </td>
                    </tr>
                  </table>
				  </form>
                  <br>
                  <table width="600" border="1" cellspacing="0" bgcolor="white" cellpadding="2" align="center">
                    <tr valign="top"> 
                      <td width="201" class="HeaderCell">Control Date/Time</td>
                      <td width="85" class="HeaderCell">Control Type</td>
                      <td width="78" class="HeaderCell">Daily</td>
                      <td width="80" class="HeaderCell">Monthly</td>
                      <td width="73" class="HeaderCell">Seasonal</td>
                      <td width="75" class="HeaderCell">Annual</td>
                    </tr>
                    <tr> 
                      <td height="23" class="TableCell" width="201">07/14/01 12:30 
                        - 07/14/01 16:30</td>
                      <td height="23" class="TableCell" width="85">Cycle 100%</td>
                      <td height="23" class="TableCell" width="78">4 Hr</td>
                      <td height="23" class="TableCell" width="80">7 Hr, 42 Min</td>
                      <td height="23" class="TableCell" width="73">32 Hr, 42 Min</td>
                      <td height="23" class="TableCell" width="75">47 Hr, 42 Min</td>
                    </tr>
                    <tr> 
                      <td height="23" class="TableCell" width="201">07/10/01 13:13 
                        - 07/10/01 15:00</td>
                      <td height="23" class="TableCell" width="85"> Shed 100%</td>
                      <td height="23" class="TableCell" width="78">1 Hr, 42 Min</td>
                      <td height="23" class="TableCell" width="80">3 Hr, 42 Min</td>
                      <td height="23" class="TableCell" width="73">27 Hr, 42 Min</td>
                      <td height="23" class="TableCell" width="75">43 Hr, 42 Min</td>
                    </tr>
                    <tr> 
                      <td height="23" class="TableCell" width="201"> 07/01/01 
                        14:04 - 07/01/01 16:04</td>
                      <td height="23" class="TableCell" width="85"> Cycle 50%</td>
                      <td height="23" class="TableCell" width="78">2 Hr</td>
                      <td height="23" class="TableCell" width="80">2 Hr</td>
                      <td height="23" class="TableCell" width="73">26 Hr</td>
                      <td height="23" class="TableCell" width="75">42 Hr</td>
                    </tr>
                  </table>
                  <p align="center" class="TableCell"><a href="user_lm_programs.jsp" class="Link1">Back to Rooftop Air</a> </p><br>
                </div>
                </div>
              </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
</body>
</html>
