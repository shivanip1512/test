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
            <table width="657" cellspacing="0" cellpadding="0" border="0">
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
            <% String pageName = "Features.jsp"; %>
            <%@ include file="Nav.jsp" %>
		  </td>	
          <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              <div align="center"> 
                <br>
                <div align="center"> 
                  <p class="Main"><b>ACCOUNT</b></p>
                  
                <p class="Main"><b>Welcome to Consumer Energy Services</b><br>
                  </p>
                  <table width="450" border="0" cellspacing="0">
                    <tr>
					<form method="post" action="TOU.jsp"> 
                      <td width="222"> 
                        <div align="center"> 
                          <input type="submit" name="" value="Monitor Your Usage">
                          <br>
                          &nbsp; </div>
                      </td>
					  </form>
					  <form method="post" action="Features.jsp">
                      <td width="224"> 
                        <div align="center"> 
                          <input type="submit" name="" value="Manage Your Programs">
                          <br>
                          &nbsp; </div>
                      </td>
					  </form>
                    </tr>
                  </table>
                </div>
                
              <table width="450" border="0" cellspacing="0" align="center">
                <tr> 
                  <td width="334" valign="top"> 
                    <table width="200" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                        <td class="MainHeader"><b>ACCOUNT INFORMATION </b> 
                          <hr>
                          <table width="200" border="0" cellspacing="0" cellpadding="3" align="center">
                            <tr> 
                              <td class="TableCell"> Account # <%= account.getAccountNumber() %></td>
                            </tr>
                            <tr> 
                              <td class="TableCell"> <%= account.getCompany() %></td>
                            </tr>
                            <tr> 
                              <td class="TableCell"> <%= primContact.getFirstName() %> 
                                <%= primContact.getLastName() %></td>
                            </tr>
                            <tr> 
                              <td class="TableCell"> Home #: <%= primContact.getHomePhone() %> 
                              </td>
                            </tr>
                            <tr> 
                              <td class="TableCell"> Work #: <%= primContact.getWorkPhone() %> 
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <br>
                  </td>
                  <td width="262" valign="top"> 
                    <table width="200" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                        <td class="MainHeader"><b>SERVICE ADDRESS</b> 
                          <hr>
                          <table width="200" border="0" cellspacing="0" cellpadding="3" align="center">
                            <tr> 
                              <td class="TableCell"> <%= propAddr.getStreetAddr1() %>, 
                                <%= propAddr.getStreetAddr2() %> </td>
                            </tr>
                            <tr> 
                              <td class="TableCell"> <%= propAddr.getCity() %>, 
                                <%= propAddr.getState() %> <%= propAddr.getZip() %></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <br>
                    <table width="200" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                        <td class="MainHeader"><b>BILLING ADDRESS </b> 
                          <hr>
                          <table width="200" border="0" cellspacing="0" cellpadding="3" align="center">
                            <tr> 
                              <td class="TableCell"> <%= billAddr.getStreetAddr1() %>, 
                                <%= billAddr.getStreetAddr2() %> </td>
                            </tr>
                            <tr> 
                              <td class="TableCell"> <%= billAddr.getCity() %>, 
                                <%= billAddr.getState() %> <%= billAddr.getZip() %></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <div align="center"> 
                  <p><br>
				  <form method="post" action="../Password.jsp">
                    <input type="submit" name="Submit" value="Update Password">
			      </form>
                  <p>&nbsp;</p>
                </div>
    </div>
			
          </td>
		  
		  
        <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<br>
<p align="center" class="TableCell2">&copy; 2002 Cannon Technologies, Inc. 
  All rights reserved. </p>
<div align="center"></div>
</body>
</html>
