<%
	String programStatus = "In Service";
	if (session.getAttribute("PROGRAM_STATUS") != null)
		programStatus = (String) session.getAttribute("PROGRAM_STATUS");
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
function doReenable(form) {
	form.action.value = "EnableService";
	form.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
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
                  <td width="265" height = "28" class="BlueHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="blueLink">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="blueLink">Log 
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
		  <% String pageName = "Programs.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "PROGRAMS - ADD/CHANGE"; %>
              <%@ include file="InfoSearchBar.jsp" %>
             
              <br>
               
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <form method="POST" action="/servlet/SOAPClient">
				    <input type="hidden" name="action" value="DisableService">
                    <td width="304" valign="top"> 
                      <div align="center"> 
                        <p class="TableCell">This customer would like to be notified 
                          of control by<br>
                          e-mail at info@cannontech.com</p>
                      </div>
                      <table width="200" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC" align="center">
                        <tr> 
                          <td> 
                            <div align="center"> 
                              <p class="HeaderCell">Temporarily opt out of all 
                                programs </p>
                            </div>
                            <table width="180" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="180" align="center"> 
                                  <select name="OptOutPeriod">
                                    <option value="0">&lt;none&gt;</option>
                                    <option value="1">One Day</option>
                                    <option value="2">Two Days</option>
                                    <option value="3">Three Days</option>
                                    <option value="7">One Week</option>
                                    <option value="14">Two Weeks</option>
                                  </select>
                                </td>
                                <td width="180" align="center">
                                  <input type="submit" name="Submit" value="Submit">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <br>
                      <p align="center" class="MainHeader"><b>Temporary Opt Out History 
                        </b> 
                      <table width="200" border="1" cellspacing="0" align="center" cellpadding="3">
                        <tr> 
                          <td class="HeaderCell">Date</td>
                          <td class="HeaderCell">Duration</td>
                        </tr>
                        <tr> 
                          <td class="TableCell">12/15/00</td>
                          <td class="TableCell">3 Days</td>
                        </tr>
                        <tr> 
                          <td class="TableCell">07/17/01</td>
                          <td class="TableCell">1 Day</td>
                        </tr>
                      </table>
                      <br>
                      <table width="150" border="0" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td align="center"> 
                            <input type="submit" name="Re-enable" value="Re-enable" onclick="doReenable(this.form)">
                          </td>
                        </tr>
                      </table>
                      <p>&nbsp;</p>
                      <p>&nbsp;</p>
                    </td>
                  </form>
                  <form name="form1" method="get" action="">
                    <td width="296" valign="top"> 
                      <div align="center"><span class="TableCell">Select the check 
                        boxes and corresponding radio button of the programs this 
                        customer would like to be enrolled in. </span><br>
                      </div>
                      <table border="1" cellspacing="0" cellpadding="3" align="right">
                        <tr> 
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
                                  <input type="checkbox" name="ApplianceCategory" value="checkbox" checked>
                                </td>
                                <td width="84" class="TableCell">Cycle AC</td>
                              </tr>
                            </table>
                            <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="37"> 
                                  <div align="right"> 
                                    <input type="radio" name="Program0" value="radiobutton" checked>
                                  </div>
                                </td>
                                <td width="70" class="TableCell">Medium</td>
                              </tr>
                              <tr> 
                                <td width="37"> 
                                  <div align="right"> 
                                    <input type="radio" name="Program0" value="radiobutton">
                                  </div>
                                </td>
                                <td width="70" class="TableCell">Light</td>
                              </tr>
                            </table>
                          </td>
                          <td width="132" valign="top" class="TableCell"> 
                            <div align="center"><%= programStatus %></div>
                          </td>
                        </tr>
                        <tr> 
                          <td width="143"> 
                            <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="23"> 
                                  <input type="checkbox" name="ApplianceCategory" value="checkbox" checked>
                                </td>
                                <td width="84" class="TableCell">Water Heater</td>
                              </tr>
                            </table>
                            <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="37"> 
                                  <div align="right"> 
                                    <input type="radio" name="Program1" value="radiobutton" checked>
                                  </div>
                                </td>
                                <td width="70" class="TableCell">8 Hours</td>
                              </tr>
                              <tr> 
                                <td width="37"> 
                                  <div align="right"> 
                                    <input type="radio" name="Program1" value="radiobutton">
                                  </div>
                                </td>
                                <td width="70" class="TableCell">4 Hours</td>
                              </tr>
                              <tr> 
                                <td width="37"> 
                                  <div align="right"> 
                                    <input type="radio" name="Program1" value="radiobutton">
                                  </div>
                                </td>
                                <td width="70" class="TableCell">ETS</td>
                              </tr>
                            </table>
                          </td>
                          <td width="132" valign="top" class="TableCell"> 
                            <div align="center"><%= programStatus %></div>
                          </td>
                        </tr>
                        <tr> 
                          <td width="143"> 
                            <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="23"> 
                                  <input type="checkbox" name="ApplianceCategory" value="checkbox">
                                </td>
                                <td width="84" class="TableCell">Duel Fuel</td>
                              </tr>
                            </table>
                            <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="37"> 
                                  <div align="right"> 
                                    <input type="radio" name="Program2" value="radiobutton">
                                  </div>
                                </td>
                                <td width="70" class="TableCell">Unlimited</td>
                              </tr>
                              <tr> 
                                <td width="37"> 
                                  <div align="right"> 
                                    <input type="radio" name="Program2" value="radiobutton">
                                  </div>
                                </td>
                                <td width="70" class="TableCell">Limited</td>
                              </tr>
                            </table>
                          </td>
                          <td width="132" valign="top" class="TableCell"> 
                            <div align="center">Not Enrolled</div>
                          </td>
                        </tr>
                        <tr> 
                          <td width="143"> 
                            <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="23"> 
                                  <input type="checkbox" name="ApplianceCategory" value="checkbox">
                                </td>
                                <td width="84" class="TableCell">Electrical Heat 
                                  ETS</td>
                              </tr>
                            </table>
                          </td>
                          <td width="132" valign="top" class="TableCell"> 
                            <div align="center">Not Enrolled</div>
                          </td>
                        </tr>
                        <tr> 
                          <td width="143"> 
                            <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="23"> 
                                  <input type="checkbox" name="ApplianceCategory" value="checkbox">
                                </td>
                                <td width="84" class="TableCell">Hot Tub</td>
                              </tr>
                            </table>
                          </td>
                          <td width="132" valign="top" class="TableCell"> 
                            <div align="center">Not Enrolled</div>
                          </td>
                        </tr>
                        <tr> 
                          <td width="143"> 
                            <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="23"> 
                                  <input type="checkbox" name="ApplianceCategory" value="checkbox">
                                </td>
                                <td width="84" class="TableCell">Pool Pump</td>
                              </tr>
                            </table>
                          </td>
                          <td width="132" valign="top" class="TableCell"> 
                            <div align="center">Not Enrolled</div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </form>
                </tr>
              </table>
              <br>
            </div>
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
