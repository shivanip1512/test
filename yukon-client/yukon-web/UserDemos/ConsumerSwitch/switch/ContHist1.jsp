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
		  <% String pageName = "ProgramHist.jsp"; %>
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
                    <div align="center"><b><span class="Main">PROGRAMS - CONTROL 
                      HISTORY </span></b></div>
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
            </div>
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                <td valign="top" bgcolor="#FFFFFF"> 
                  <table width="450" border="0" cellspacing="0" cellpadding="0" align="center">
                    <tr> 
                      <td width="107" valign="top"> 
                        <div align="center"><img src="../AC.gif" width="60" height="59"><br>
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
                              <div align="center">06/27/02 09:25</div>
                            </td>
                            <td class="TableCell"> 
                              <div align="left">2 Hours</div>
                            </td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> 
                              <div align="center">06/27/02 11:02</div>
                            </td>
                            <td class="TableCell"> 
                              <div align="left">2 Hours</div>
                            </td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> 
                              <div align="center">06/16/02 12:15</div>
                            </td>
                            <td class="TableCell"> 
                              <div align="left">3 Hours</div>
                            </td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> 
                              <div align="center">06/03/02 14:05</div>
                            </td>
                            <td class="TableCell"> 
                              <div align="left">2 Hours</div>
                            </td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> 
                              <div align="right">Total:</div>
                            </td>
                            <td class="TableCell"> 
                              <div align="left">9 Hours</div>
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
              <form method="post" action="ProgramHist.jsp">
                <input type="submit" name="Back" value="Back">
              </form>
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
