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
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
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
		    <td width="609" valign="top" bgcolor="#FFFFFF"> 
              <div align="center">
                <div align="center">
                  <p class="TitleHeader">AUTO CONTROL - CONTROL AREA 1 SETTINGS<br>
                  </p>
                </div>
                <table width="600" border="0" cellspacing="0" cellpadding="0" align="center">
                  <tr> 
                    <td width="600">
					  <form  method="submit" action="">
                      <table border="1" cellspacing="0" cellpadding="3" align="center" width="400">
                        <tr> 
                          <td width="178"> 
                            <div align="right" class="TableCell">
                              New Threshold:
                            </div>
                          </td>
                          <td width="204"> 
                            <table width="200" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                              <tr> 
                                <td width="70"> 
                                  <div align="right">&nbsp;</div>
                                </td>
                                <td width="130"> 
                                  <input type="text" name="textfield23" size="8">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
                          <td width="178"> 
                            <div align="right" class="TableCell">New Valid Auto Control Time:</div>
                          </td>
                          <td width="204"> 
                            <table width="200" border="0" cellspacing="0" cellpadding="0">
                              <tr> 
                                <td width="70"> 
                                  <div align="right" class="TableCell">Start Time:&nbsp;</div>
                                </td>
                                <td width="130"> 
                                  <input type="text" name="textfield2" size="8">
                                </td>
                              </tr>
                              <tr> 
                                <td width="70"> 
                                  <div align="right" class="TableCell">Stop Time:&nbsp;</div>
                                </td>
                                <td width="130"> 
                                  <input type="text" name="textfield22" size="8">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
                          <td width="178"> 
                            <div align="right" class="TableCell">Auto Control:</div>
                          </td>
                          <td width="204"> 
                            <table width="200" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                              <tr> 
                                <td width="70"> 
                                  <div align="right">&nbsp;</div>
                                </td>
                                <td width="130"> 
                                  <select name="select2">
                                    <option>On</option>
                                    <option>Off</option>
                                    <option>Off Today Only</option>
                                  </select>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
					  </form>
                      <div align="center"><br>
                        <table width="200" border="0" cellspacing="0" cellpadding="5">
                          <tr> 
						  <form method="post" action="user_lm_control.jsp">
                            <td> 
                              <div align="right">
                                <input type="submit" name="Back" value="Back">
                              </div>
                            </td>
							</form>
							<form method="post" action="user_lm_control.jsp">
                            <td> 
                              <div align="left">
                                <input type="submit" name="Submit" value="Update">
                              </div>
                            </td>
							</form>
                          </tr>
                        </table>
                      </div>
                    </td>
                  </tr>
                </table>
                <a href="user_ee.jsp" class="Link1"><br>
                <br>
                </a></div>
              </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
</body>
</html>
