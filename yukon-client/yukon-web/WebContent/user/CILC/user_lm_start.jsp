<%@ include file="../include/user_header.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr>
			  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;User Control</td>    
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
		  <% String pageName = "user_lm_time.jsp"; %>
          <%@ include file="include/nav.jsp" %> 
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		  <td width="609" valign="top" bgcolor="#FFFFFF"> 
              <div align="center"> 
                <p align="center"><br>
                  <span class="TitleHeader">TIME-BASED CONTROL</span></p>
                <table width="350" border="0" cellspacing="0" cellpadding="0" align="center">
                  <tr> 
                    <td class="TitleHeader">Start Program</td>
                  </tr>
                </table>
				<form method="submit" action="">
                <table width="350" border="1" cellspacing="0" cellpadding="3" align="center" class="TableCell">
                  <tr> 
                    <td> 
                      <table width="350" border="0" cellspacing="0" cellpadding="3" align="center">
                        <tr valign="top"> 
                          <td class="TableCell" width="104"> 
                            <div align="right">Start Now:<br>
                              <br>
                            </div>
                          </td>
                          <td class="TableCell" width="22"> 
                            <input type="radio" name="radiobutton" value="radiobutton">
                          </td>
                          <td class="TableCell" width="34">&nbsp;</td>
                          <td class="TableCell" width="166">&nbsp;</td>
                        </tr>
                        <tr> 
                          <td class="TableCell" width="104"> 
                            <div align="right">Start at:</div>
                          </td>
                          <td class="TableCell" width="22"> 
                            <input type="radio" name="radiobutton" value="radiobutton">
                          </td>
                          <td class="TableCell" width="34"> 
                            <div align="right">Date: </div>
                          </td>
                          <td class="TableCell" width="166"> 
                            <input type="text" name="textfield222" size="20" maxlength="20">
                          </td>
                        </tr>
                        <tr> 
                          <td class="TableCell" width="104">&nbsp; </td>
                          <td class="TableCell" width="22">&nbsp; </td>
                          <td class="TableCell" width="34"> 
                            <div align="right">Time: </div>
                          </td>
                          <td class="TableCell" width="166"> 
                            <input type="text" name="textfield2223" size="20" maxlength="20">
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
				</form>
                <form method="submit" action="">
                <table width="350" border="0" cellspacing="0" cellpadding="0" align="center">
                  <tr> 
                    <td class="TitleHeader">Stop Program</td>
                  </tr>
                </table>
                <table width="350" border="1" cellspacing="0" cellpadding="3" align="center" class="TableCell">
                  <tr> 
                    <td> 
                      <table width="350" border="0" cellspacing="0" cellpadding="3" align="center">
                        <tr valign="top"> 
                          <td class="TableCell" width="104"> 
                            <div align="right">Manual Stop:<br>
                              <br>
                            </div>
                          </td>
                          <td class="TableCell" width="22"> 
                            <input type="radio" name="radiobutton" value="radiobutton">
                          </td>
                          <td class="TableCell" width="34">&nbsp;</td>
                          <td class="TableCell" width="166">&nbsp;</td>
                        </tr>
                        <tr> 
                          <td class="TableCell" width="104"> 
                            <div align="right">Stop at:</div>
                          </td>
                          <td class="TableCell" width="22"> 
                            <input type="radio" name="radiobutton" value="radiobutton">
                          </td>
                          <td class="TableCell" width="34"> 
                            <div align="right">Date: </div>
                          </td>
                          <td class="TableCell" width="166"> 
                            <input type="text" name="textfield2222" size="20" maxlength="20">
                          </td>
                        </tr>
                        <tr> 
                          <td class="TableCell" width="104">&nbsp; </td>
                          <td class="TableCell" width="22">&nbsp; </td>
                          <td class="TableCell" width="34"> 
                            <div align="right">Time: </div>
                          </td>
                          <td class="TableCell" width="166"> 
                            <input type="text" name="textfield22232" size="20" maxlength="20">
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
				</form>
                <br>
                <table
    width="300" border="0" cellspacing="0" cellpadding="6" align="center">
                  <tr>
				  <form method="submit" action=" user_lm_programs.jsp"> 
                    <td width="150"> 
                      <p align=RIGHT>
                        <input type="submit" name="OK" value="OK"> 
                    </td>
					</form>
					<form method="submit" action="user_lm_programs.jsp"> 
                    <td width="150" valign="TOP"> 
                      <div align="left">
                        <input type="submit" name="Cancel" value="Cancel">
                        </div>
                    </td>
					</form>
                  </tr>
                </table>
                
              </div>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
