<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../demostyle.css" type="text/css">
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
                <td colspan="4" height="74" background="../../Header.gif">&nbsp;</td>
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
		  <% String pageName = "Enrollment.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <table width="600" border="0" cellspacing="0">
                <tr>
                  <td width="202">
                    <table width="200" border="0" cellspacing="0" cellpadding="3">
                      <tr>
                        <td><span class="Main"><b>Acct #<%= account.getAccountNumber() %></b></span><br>
                          <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
                          <!--<%= account.getCompany() %><br> -->
                          <%= propAddr.getStreetAddr1() %>, <%= propAddr.getStreetAddr2() %><br>
                          <%= propAddr.getCity() %>, <%= propAddr.getState() %> <%= propAddr.getZip() %><br>
                          <%= primContact.getHomePhone() %></span></td>
                      </tr>
                    </table>
                    
                  </td>
                  <td width="187" valign="top"> 
                    <div align="center"><b><span class="Main">PROGRAMS - ENROLLMENT</span></b></div>
                  </td>
                  <td valign="top" width="205">
                  
                  </td>
                </tr>
              </table>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
              
              </div>
            <table width="90%" border="0" align = "center">
              <tr>
                <td align = "center"><span class="Main"> </span><span class="TableCell"> 
                  Select the check boxes and corresponding radio button of the 
                  programs you would like to be enrolled in.<br> <br></span><b><span class="Main"></span></b><b>
				  <form action = "ProgramDetails.jsp">
                  <input type="submit" name="Details" value="Program Details"></form>
                
                  <table border="1" cellspacing="0" cellpadding="3">
                    <tr align = "center"> 
                      <td width="143" class="HeaderCell"> 
                        <div align="center">Program Enrollment</div>
                      </td>
                      <td width="132" class="HeaderCell"> 
                        <div align="center">Status</div>
                      </td>
                    </tr>
                    <tr> 
                      <td width="143"> 
                        <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td width="23"> 
                              <input type="checkbox" name="checkbox2" value="checkbox" checked>
                            </td>
                            <td width="84" class="TableCell">Cycle AC</td>
                          </tr>
                        </table>
                        <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td width="37"> 
                              <div align="right"> 
                                <input type="radio" name="radiobutton" value="radiobutton" checked>
                              </div>
                            </td>
                            <td width="70" class="TableCell">Medium</td>
                          </tr>
                          <tr> 
                            <td width="37"> 
                              <div align="right"> 
                                <input type="radio" name="radiobutton" value="radiobutton">
                              </div>
                            </td>
                            <td width="70" class="TableCell">Light</td>
                          </tr>
                        </table>
                      </td>
                      <td width="132" valign="top" class="TableCell"> 
                        <div align="center">In Service</div>
                      </td>
                    </tr>
                    <tr> 
                      <td width="143"> 
                        <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td width="23"> 
                              <input type="checkbox" name="checkbox22" value="checkbox" checked>
                            </td>
                            <td width="84" class="TableCell">Setback</td>
                          </tr>
                        </table>
                        <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td width="37"> 
                              <div align="right"> 
                                <input type="radio" name="radiobutton" value="radiobutton">
                              </div>
                            </td>
                            <td width="70" class="TableCell">4 Degrees</td>
                          </tr>
                          <tr> 
                            <td width="37"> 
                              <div align="right"> 
                                <input type="radio" name="radiobutton" value="radiobutton">
                              </div>
                            </td>
                            <td width="70" class="TableCell">2 Degrees</td>
                          </tr>
                        </table>
                      </td>
                      <td width="132" valign="top" class="TableCell"> 
                        <div align="center">Out of Service</div>
                      </td>
                    </tr>
                  </table>
                  </b>
                  <br><form action = "ChangeForm.jsp"><table width="50%" border="0">
                    <tr>
                      <td align = "right">
                        <input type="submit" name="Submit" value="Submit">
                      </td>
                      <td>
                        <input type="button" name="Cancel" value="Cancel">
                      </td>
                    </tr>
                  </table></form>
              
                </td>
              </tr>
            </table>
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
