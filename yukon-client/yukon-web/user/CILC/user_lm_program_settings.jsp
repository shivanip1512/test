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
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
		  <% String pageName = "user_lm_time.jsp"; %>
          <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
		    <td width="600" valign="top" bgcolor="#FFFFFF"> 
              <div align="center">
                <div align="center"> 
                  <p align="center" class="TitleHeader"><br>TIME BASED - PROGRAM SETTINGS</p>
				  <form method="post" action="user_lm_program_settings.jsp">
                  <table width="400" border="1" cellspacing="0" cellpadding="5" align="center">
                    <tr> 
                      <td  class="TableCell"> 
                        <table width="400" border="0" cellspacing="0" cellpadding="3" align="center">
                          <tr> 
                            <td class="TableCell" width="185"> 
                              <div align="right">kW Capacity:</div>
                            </td>
                            <td class="TableCell" width="203"> 
                              <input type="text" name="textfield222" size="10">
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr> 
                      <td  class="TableCell"><b>Valid for the following days of 
                        the week: </b> 
                        <table width="400" border="0" cellspacing="5" cellpadding="0">
                          <tr> 
                            <td class="TableCell"> 
                              <input type="checkbox" name="checkbox" value="checkbox">
                              Monday </td>
                            <td class="TableCell"> 
                              <input type="checkbox" name="checkbox2" value="checkbox">
                              Tuesday </td>
                            <td class="TableCell"> 
                              <input type="checkbox" name="checkbox3" value="checkbox">
                              Wednesday </td>
                            <td class="TableCell"> 
                              <input type="checkbox" name="checkbox4" value="checkbox">
                              Thursday </td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> 
                              <input type="checkbox" name="checkbox5" value="checkbox">
                              Friday </td>
                            <td class="TableCell"> 
                              <input type="checkbox" name="checkbox6" value="checkbox">
                              Saturday </td>
                            <td class="TableCell"> 
                              <input type="checkbox" name="checkbox7" value="checkbox">
                              Sunday </td>
                            <td class="TableCell"> 
                              <input type="checkbox" name="checkbox42" value="checkbox">
                              Holidays Valid</td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr> 
                      <td  class="TableCell"><b>Valid for the following daily 
                        times: </b> 
                        <table width="400" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                          <tr> 
                            <td width="123" align="right"> 
                              <input type="radio" name="radiobutton" value="radiobutton">
                            </td>
                            <td width="277" class="TableCell">Always Valid</td>
                          </tr>
                          <tr> 
                            <td width="123" align="right"> 
                              <input type="radio" name="radiobutton" value="radiobutton">
                            </td>
                            <td width="277" class="TableCell">Time Windows</td>
                          </tr>
                        </table>
                        <table width="400" border="0" cellspacing="0" cellpadding="3">
                          <tr> 
                            <td width="116" class="TableCell">&nbsp; </td>
                            <td width="95" class="TableCell">From </td>
                            <td width="171" class="TableCell">To </td>
                          </tr>
                          <tr> 
                            <td width="116" class="TableCell"> 
                              <div align="right">Time Window 1:</div>
                            </td>
                            <td width="95" class="TableCell"> 
                              <input type="text" name="textfield2223" size="10">
                            </td>
                            <td width="171" class="TableCell"> 
                              <input type="text" name="textfield22233" size="10">
                            </td>
                          </tr>
                          <tr> 
                            <td width="116" class="TableCell"> 
                              <div align="right">Time Window 2:</div>
                            </td>
                            <td width="95" class="TableCell"> 
                              <input type="text" name="textfield22232" size="10">
                            </td>
                            <td width="171" class="TableCell"> 
                              <input type="text" name="textfield22234" size="10">
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
				  </form>
                  <table
    width="300" border="0" cellspacing="0" cellpadding="6" align="center">
                    <tr>
					  <form method="post" action="user_lm_programs.jsp"> 
                      <td width="150"> 
                        <div align="right">
                          <input type="submit" name="OK" value="OK">
                        </div>
                      </td>
					  </form>
					  <form method="post" action="user_lm_programs.jsp">
                      <td width="150" valign="TOP"> 
                        <div align="left">
                          <input type="submit" name="Cancel" value="Cancel">
                        </div>
                      </td>
					  </form>
                    </tr>
                  </table>
                  <p>&nbsp;</p>
                  <p>&nbsp;</p>
                </div>
                </div>
              </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
</body>
</html>
