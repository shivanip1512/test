<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
   
 function goBack() {
 document.location = "CreateWizard.jsp";
 
 } 



  //End hiding script -->
  </SCRIPT>
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="BlueHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="blueLink">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="blueLink">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
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
          <td  valign="top" width="101"><% String pageName = "CreateWizard.jsp"; %><%@ include file="include/Nav.jsp" %></td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><% String header = "CREATE NEW ACCOUNT CONTACT(S)"; %><%@ include file="include/InfoSearchBar.jsp" %>
         
			  <form>
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                  <tr> 
                    <td width="300" valign="top" bgcolor="#FFFFFF"><span class="SubtitleHeader">CONTACT (Primary)</span> 
                      <hr>
                      <table width="300" border="0" cellspacing="0" cellpadding="1" align="center" height="135">
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">Last Name:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="textfield32" maxlength="30" size="24">
                          </td>
                        </tr>
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">First Name:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="textfield22" maxlength="30" size="24">
                          </td>
                        </tr>
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">Home #:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="textfield243" maxlength="14" size="14">
                          </td>
                        </tr>
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">Work #:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="textfield252" maxlength="14" size="14">
                          </td>
                        </tr>
                      </table>
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center" height="177">
                        <tr> 
                          <td><br>
                            <span class="SubtitleHeader">CONTACT 2</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Last Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield322" maxlength="30" size="24">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">First Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield222" maxlength="30" size="24">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Home #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield2432" maxlength="14" size="14">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Work #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield2522" maxlength="14" size="14">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> <span class="SubtitleHeader">CONTACT 3</span> 
                      <hr>
                      <table width="300" border="0" cellspacing="0" cellpadding="1" align="center" height="135">
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">Last Name:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="textfield323" maxlength="30" size="24">
                          </td>
                        </tr>
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">First Name:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="textfield223" maxlength="30" size="24">
                          </td>
                        </tr>
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">Home #:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="textfield2433" maxlength="14" size="14">
                          </td>
                        </tr>
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">Work #:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="textfield2523" maxlength="14" size="14">
                          </td>
                        </tr>
                      </table>
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center" height="177">
                        <tr> 
                          <td><br>
                            <span class="SubtitleHeader">CONTACT 4</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Last Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield3222" maxlength="30" size="24">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">First Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield2222" maxlength="30" size="24">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Home #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield24322" maxlength="14" size="14">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Work #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield25222" maxlength="14" size="14">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
              
              <br>
              
              <table width="150" border="0">
                <tr>
                  <td align = "center" > 
                    <input type="submit" name="Submit" value="Save">
                  </td>
                  <td> 
                    <input type="button" name="Submit2" value="Cancel" onclick = "goBack()">
                  </td>
                </tr>
              </table><br>
              </form>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
