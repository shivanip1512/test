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
		  <% String pageName = "FAQ.jsp"; %>
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
                    <div align="center"><b><span class="Main">QUESTIONS - FAQ</span></b></div>
                  </td>
                  <td valign="top" width="205" align = "right"><%@ include file="Notice.jsp" %> 
                  
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
              <b><span class="Main"><br>
              </span></b> 
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <form>
                    <td valign="top" class="Main"> 
                      <div align="center"> 
                        <p class="Main" align="left"><b><a name="Top"></a>PROGRAMS</b></p>
                        <div align="center"></div>
                        <ul>
                          <li> 
                            <div align="left"><a href="#Programs1">How long does 
                              it take for my program to become effective after 
                              adding or changing a program?</a></div>
                          </li>
                          <li>
                            <div align="left"><a href="#Programs2">How do I find 
                              out more about my program or other programs?</a></div>
                          </li>
                          <li>
                            <div align="left"><a href="#Programs3">Can I sign 
                              up for more than one program?</a></div>
                          </li>
                        </ul>
                        <p class="Main" align="left"><b>THERMOSTAT CONTROL</b></p>
                      </div>
                      <ul>
                        <li> 
                          <div align="left"><a href="#Therm1">Can I control my 
                            thermostat even if I do not know my current settings?</a></div>
                        </li>
                        <li> 
                          <div align="left"><a href="#Therm2">What do the Fan 
                            settings do?</a></div>
                        </li>
                        <li>
<div align="left"><a href="#Therm3">Does the utility 
                            company have access to my thermostat?</a></div>
                        </li>
                      </ul>
                      <div align="center"> 
                        <p class="Main" align="left"><b>SAVINGS</b></p>
                        
                        <ul>
                          <li> 
                            <div align="left"><a href="#Savings1">How much credit 
                              do I receive if I opt out while contolling?</a></div>
                          </li>
                        </ul>
                        <hr>
                        <p class="Main" align="left"><b>PROGRAMS</b></p>
                        <p class="Main" align="left"><b><i><a name="Programs1"></a>How 
                          long does it take for my program to become effective 
                          after adding or changing a program?</i></b></p>
                        <p class="Main" align="left">Immediately! You can take 
                          advantage of energy savings the moment you decide to. 
                          Just make your selection on the &quot;Programs - Add/Change&quot; 
                          page, click the submit button, and select Yes at the 
                          prompt.<br>
                          <a href="#Top">[back to top]</a></p>
                        <div align="left"><b><i><a name="Programs2"></a>How do 
                          I find out more about my program or other programs?</i></b><br>
                          <br>
                          Go to the &quot;Programs - Add/Change&quot; page and 
                          click the Program Details button. You will find all 
                          of the information you need here regarding the program, 
                          amount of control, and savings.<br>
                          <a href="#Top">[back to top]</a> <br>
                          <br>
                        </div>
                         
                            
                        <div align="left"><b><i><a name="Programs3"></a>Can I 
                          sign up for more than one program?</i></b></div>
                        <div align="left"><br>
                          Certainly! The more programs you enroll in, the more 
                          energy savings you will receive.<br>
                          <a href="#Top">[back to top]</a> <br>
                          <br>
                        </div>
                        <hr>
                        <p align="left"><b>THERMOSTAT CONTROL</b><br>
                        </p>
                        <p align="left"><b><i><a name="Therm1"></a>Can I control 
                          my thermostat even if I do not know my current settings?</i></b><br>
                          <br>
                          Yes. You may select the temperature change (up or down) 
                          in degrees without knowing the current temperature or 
                          simply set a new specific temperature. If pre-cooling, 
                          you may also select a new specific temperature or select 
                          the number of degress to decrease in temperature.<br>
                          <a href="#Top">[back to top]</a><br>
                        </p>
                        <p align="left"><b><i><a name="Therm2"></a>What does the 
                          Fan setting do?</i></b><br>
                          <br>
                          The fan setting controls the operation of the fan.<br>
                          Auto - the fan runs only as necessary to maintain the 
                          current temperature settings. <br>
                          On - the fan runs continuously. <br>
                          Off - the fan does not run. <br>
                          <a href="#Top">[back to top]</a></p>
                        <p align="left"><b><i><a name="Therm3"></a>Does the utility 
                          company have access to my thermostat?</i></b><br>
                          <br>
                          The utility only has access to your thermostat for control 
                          based on the programs you have signed up for. When not 
                          being controlled, you have complete control of your 
                          thermostat.<br>
                          <a href="#Top">[back to top]</a><br>
                        </p>
                        <hr>
                        <p align="left"><b>Savings</b> </p>
                        <p align="left"><b><i><a name="Savings1"></a>How much 
                          credit do I receive if I opt out while contolling?</i></b><br>
                          <br>
                          You will receive credit for the protion of time you 
                          were controlled.<br>
                          <a href="#Top">[back to top]</a> </p>
                        </div>
                    </td>
                  </form>
                  <form name="form1" method="get" action="">
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
