<%@ include file="StarsHeader.jsp" %>
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
		  <% String pageName = "General.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <table width="600" border="0">
                <tr> 
                  <td width="68%" align="center" valign="top"> <table width="100%" border="0">
                      <tr>
                        <td><table width="400" border="0">
                            <tr>
                              <td width="198" valign="top" class="Main"><strong>WELCOME 
                                TO CONSUMER<br>
                                ENERGY SERVICES!</strong></td>
                              <td width="188"><span class="Main"><b>Acct #<%= account.getAccountNumber() %></b></span><br> 
                                <span class="NavText"><%= primContact.getFirstName() %> 
                                <%= primContact.getLastName() %><br>
                                <!--<%= account.getCompany() %><br> -->
                                <%= propAddr.getStreetAddr1() %>, <%= propAddr.getStreetAddr2() %><br>
                                <%= propAddr.getCity() %>, <%= propAddr.getState() %> 
                                <%= propAddr.getZip() %><br>
                                <%= primContact.getHomePhone() %></span></td>
                            </tr>
                          </table>
                          <hr>
                          <p class="Main">Thank you for participating in our Consumer 
                            Energy Services programs. By participating, you have 
                            helped to optimize our delivery of energy, stabilize 
                            rates, and reduce energy costs. Best of all, you are 
                            saving energy dollars!</p>
                          <p class="Main">This site is designed to help manage 
                            your programs on-line from anywhere with access to 
                            a Web browser. Your current enrollment is displayed 
                            below:</p>
                          </td>
                      </tr>
                      </table> 
                    <td width="32%" valign="top">
<div align="right"><img src="..\Family.jpg" width="168" height="224"> </div></table>
              <table width="235" border="1" cellspacing="0" cellpadding="3" align="center">
              </table>
              <br>
              <table width="300" border="1" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> <table width="290" border="0" cellspacing="5" cellpadding="3" align="center">
                      <tr class="Main"> 
                        <td width="139"> <div align="center"> 
                            <p><img src="EnrolledButton.gif"></p>
                          </div></td>
                        <td width="134"> <div align="center"><img src="ControlButton.gif"></div></td>
                      </tr>
					  
					  <%
	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
		StarsLMProgram program = programs.getStarsLMProgram(i);
		StarsApplianceCategory category = null;
		
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			if (appCat.getApplianceCategoryID() == program.getApplianceCategoryID()) {
				category = appCat;
				break;
			}
		}
%>
                      <tr> 
                        <td width="139"> <div align="center"> <span class="TableCell"><img src="../<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br> 
                            <%= program.getProgramName() %></span> <br>
                          </div></td>
                        <td width="134" class="Main"> <div align="center"><b>You have not<br>
                            been controlled</b>
							<!--<b>You have<br>
                            been controlled</b>-->
							<!--<b>Currently<br>
							controlling</b>-->
							</div></td>
                    </table></td>
                </tr>
              </table>
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
