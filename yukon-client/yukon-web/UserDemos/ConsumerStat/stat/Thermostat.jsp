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
                    
                  <div align="left"></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
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
		  <% String pageName = "Thermostat.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
          
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
                    <div align="center"><b><span class="Main">PROGRAMS - THERMOSTAT</span></b></div>
                  </td>
                  <td valign="top" width="205"> 
                    <form name="form2" method="post" action="../../../login.jsp">
                      <div align="right"> 
                        <input type="submit" name="Log Off" value="Log Off">
                      </div>
                    </form>
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
              <br>
              <table width="600" border="0" cellspacing="0">
                  <tr>
                    
                  <td width="107" valign="top" class="Main"><br>
                    You may select a specific Temp Setpoint (actual temperature) 
                    or select how many degrees you would like the temperature 
                    to change by. If pre-cooling, also specify the duration in 
                    hours. Then click Set.</td>
                    <td width="489"> 
                      <table width="480" height="275"border="0" cellspacing="0" cellpadding="0" align="center" background="t-stat.jpg">
                        <tr> 
                        <td>&nbsp;<br><br></td>
                        </tr>
                        <tr> 
                          <td> 
                            <table width="454" border="0" cellspacing="0" cellpadding="0">
                              <tr> 
                                <td> 
                                  <table width="454" border="0" cellspacing="0" cellpadding="0">
                                    <tr> 
                                      <td width="153">&nbsp;</td>
                                      <td width="187"> 
                                        <table width="185" border="0" cellspacing="0" cellpadding="0" align="center">
                                          <tr> 
                                            <td width="24"> 
                                              <input type="radio" name="radiobutton" value="radiobutton">
                                            </td>
                                            <td width="103" class="NavText">Temp 
                                              Setpoint</td>
                                            <td width="38"> 
                                              <input type="text" name="textfield2" size="4">
                                            </td>
                                            <td width="20" class="TableCell">&deg;F</td>
                                          </tr>
                                          <tr> 
                                            <td width="24"> 
                                              <input type="radio" name="radiobutton" value="radiobutton">
                                            </td>
                                            <td width="103" class="NavText">Temp 
                                              Change (+/-)</td>
                                            <td width="38"> 
                                              <input type="text" name="textfield2" size="4">
                                            </td>
                                            <td width="20" class="TableCell">&deg;F</td>
                                          </tr>
                                        </table>
                                      </td>
                                      <td width="114">&nbsp;</td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
                          <td> 
                            <table width="454" border="0" cellspacing="0" cellpadding="0" height="15">
                              <tr> 
                                <td width="28">&nbsp;</td>
                                <td width="54" bgcolor="#CCCCCC" valign="top"> 
                                  <div align="right">
                                    <select name="select2">
                                      <option>On</option>
                                      <option>Auto</option>
                                      <option>Off</option>
                                    </select>
                                  </div>
                                </td>
                                <td width="372">&nbsp;</td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
                          <td> 
                            <table width="454" border="0" cellspacing="0" cellpadding="0">
                              <tr> 
                                <td width="153">&nbsp;</td>
                                <td width="187"> 
                                  <table width="185" border="0" cellspacing="0" cellpadding="0" align="center">
                                    <tr> 
                                      <td width="24"> 
                                        <input type="radio" name="radiobutton" value="radiobutton">
                                      </td>
                                      <td width="103" class="NavText">Temp Setpoint</td>
                                      <td width="39"> 
                                        <input type="text" name="textfield3" size="4">
                                      </td>
                                      <td width="19" class="TableCell">&deg;F</td>
                                    </tr>
                                    <tr> 
                                      <td width="24"> 
                                        <input type="radio" name="radiobutton" value="radiobutton">
                                      </td>
                                      <td width="103" class="NavText">Temp Decrease</td>
                                      <td width="39"> 
                                        <input type="text" name="textfield22" size="4">
                                      </td>
                                      <td width="19" class="TableCell">&deg;F</td>
                                    </tr>
                                  </table>
                                </td>
                                <td width="114">&nbsp;</td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
                          <td></td>
                        </tr>
                        <tr> 
                          <td> 
                            <table width="454" border="0" cellspacing="0" cellpadding="0">
                              <tr> 
                                <td width="153">&nbsp;</td>
                                <td width="187"> 
                                  <div align="right"><span class="NavText">Duration:</span> 
                                    <input type="text" name="textfield222" size="4">
                                    <span class="NavText">Hrs.</span></div>
                                </td>
                                <td width="114">&nbsp;</td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
                          <td>&nbsp;</td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <p align="center" class="Main">&nbsp;</p>
                </div>
			
          </td>
		  
		  
        <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<br>
<div align="center"></div>
</body>
</html>
