<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
</head>

<body bgcolor="#666699" text="#000000" leftmargin="0" topmargin="0" link="#000000" vlink="#000000" alink="#000000">
<table width=100% border="0" cellspacing="0" cellpadding="0" align="center">
  <tr> 
    <td bgcolor="#666699" height="284"> 
      <table width="100%" height="50" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            
          <td width="34%" valign="top" class="TableCell2" height="49"><font size="1">Cannon 
            Technologies, Inc.<br clear="ALL">
            8301 Golden Valley Road, Suite 300<br>
            Golden Valley, MN 55427<br>
            800-827-7966</font><i><br>
              </i></td>
            
          <td width="32%" valign="bottom" height="49"> </td>
            
          <td width="34%" valign="top" height="49"> 
            <div align="right"><img src="Powered_by_Yukon(small)(white_on_readmeter_color).gif" width="139" height="29"></div>
            </td>
          </tr>
        </table>
       <br> 
      <table width="687" border="1" cellspacing="0" cellpadding="2" align="center" bgcolor="#FFFFFF" height = "433">
        <tr> 
          <td align = "center"> 
            <table  border="0"  cellpadding = "4" height="445" width="636">
              <tr> 
                <td colspan = "2" align = "center" height="51" valign = "bottom"> 
                  <table width="65%" border="0" height="43">
                    <tr> 
                      <td width="34%" align = "center"><span class="TableCell"><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#999999"><b><font face="Arial, Helvetica, sans-serif" color="#666666" size="4">Welcome 
                        to</font></b></font> </span></td>
                      <td width="66%">&nbsp;</td>
                    </tr>
                    <tr> 
                      <td colspan = "2" align = "center"><span class="TableCell"><b><font size="4">Consumer 
                        Energy Services!</font></b></span> </td>
                    </tr>
                  </table>
                  <hr width = "100%">
                </td>
              </tr>
              <tr> 
                <td width="407" valign = "top" class = "Main"> 
                  <p><span><b>Consumer Energy Services</b> provides customer specific 
                    access for everything from program signup to remote access 
                    of real-time energy information. This means not only more 
                    information at your fingertips to help you realize cost savings, 
                    but also the convenience of remote access to energy controls. 
                    In addition, since these services require less energy usage, you play an active role in helping to preserve our 
                    natural environment and it's resources. </span></p> 
					<div align = "center"><b>Sign up today to take advantage of 
                    these features and start your energy savings!</b></div><br>
                 
                  <table width="367" border="1" cellspacing="0" cellpadding="1"  height="186" class = "Main">
                    <tr> 
                      <td rowspan = "3" width="555" bgcolor="#FFFFFF" height="102" valign="top" ><br> 
                        <div align="center"><b>SIGN IN</b><br>
                          If you are currently enrolled in a program, enter your 
                          account number and password.</div>
                        <form name="form1" method="post" action="/servlet/SOAPClient">
                     	<input type="hidden" name="action" value="UserLogin">
				  		<input type="hidden" name="REDIRECT" value="/user/ConsumerStat/stat/General.jsp">
				  		<input type="hidden" name="REFERRER" value="/user/ConsumerStat/login.jsp">
                          <table width="200" border="0" cellspacing="0" cellpadding="3" align="center">
                            <tr> 
                              <td width="83" class = "Main"> 
                                <div align="right">Username:</div>
                              </td>
                              <td width="117" valign="bottom"> 
                                <input type="text" name="USERNAME">
                              </td>
                            </tr>
                            <tr> 
                              <td width="83" class = "Main"> 
                                <div align="right">Password:</div>
                              </td>
                              <td width="117"> 
                                <input type="password" name="PASSWORD">
                              </td>
                            </tr>
                            <tr> 
                              <td width="83">&nbsp;</td>
                              <td width="117"> 
                                <div align="left"><font face="Arial, Helvetica, sans-serif" size="2"> 
                                  <input type="submit" name="Submit2" value="Submit">
                                  </font></div>
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                      <td width="555" valign = "bottom" bgcolor="#FFFFFF"class = "TableCell" height="25" align = "center"><img src="YourLogo.gif" width="202" height="58"></td>
                    </tr>
                    <tr>
                      <td width="555" bgcolor="#FFFFFF" class = "TableCell3">
<%
	String formMethod = null;
	String formAction = null;
	if (session.getAttribute("ENROLLMENT_PROGRAMS_THERMOSTAT") != null) {
		formMethod = "get";
		formAction = "ProgramsNew.jsp";
	}
	else {
		formMethod = "post";
		formAction = "/servlet/SOAPClient";
	}
%>
					  <form method="<%= formMethod %>" action="<%= formAction %>">
<%
	if (session.getAttribute("ENROLLMENT_PROGRAMS_THERMOSTAT") == null) {
%>
					    <input type="hidden" name="action" value="GetEnrPrograms">
						<input type="hidden" name="CompanyID" value="1">
						<input type="hidden" name="REDIRECT" value="/user/ConsumerStat/ProgramsNew.jsp">
						<input type="hidden" name="REFERRER" value="/user/ConsumerStat/login.jsp">
<%
	}
%>
                        <table width="154" border="0" cellspacing="0" cellpadding="10" align="center" class = "Main" height="97">
                          <tr> 
                            <td> 
                              <div align="center"><b>Sign up today!</b></div><br>
                              <div align="center">Click below to view our program 
                                and sign up.</div><br>
                              <div align="center"> <font face="Arial, Helvetica, sans-serif" size="2">
                              <input type="submit" name="Programs" value="Programs">
                              </font></div> 
                            </td>
                          </tr>
                        </table></form>
                      </td>
                    </tr>
                  </table><br>
                  <div align = "left"> If you need help or have forgotten your 
                    password, click <a href="mailto:info@cannontech.com">here</a>. 
                  </div>
                </td>
                <td width="151" valign = "top" class = "Main"> <span><b><font face="Arial, Helvetica, sans-serif" size="2">Use 
                  these features to:</font></b></span><br>
                  <table width="100%" border="0" cellpadding = "1" cellspacing = "1">
                    <tr> 
                      <td><font size="4" color="#FF9900">&#149;</font></td>
                      <td class = "Main"><span>Access your account</span></td>
                    </tr>
                    <tr> 
                      <td><font color="#FF9900" size="4">&#149;</font></td>
                      <td class = "Main"><span >Sign up for a program</span></td>
                    </tr>
                    <tr> 
                      <td><font color="#FF9900" size="4">&#149;</font></td>
                      <td class = "Main"><span>See history and likelihood of control</span></td>
                    </tr>
                    <tr> 
                      <td><font size="4" color="#FF9900">&#149;</font></td>
                      <td class = "Main"><span>Temporarily opt out of a Program</span></td>
                    </tr>
                    <tr> 
                      <td><font color="#FF9900" size="4">&#149;</font></td>
                      <td class = "Main"><span>Change to a different program</span></td>
                    </tr>
                  </table>
                  <p><span><b>For your convenience</b>, the system records any 
                    changes you make and transfers this information directly to 
                    your customer billing. </span></p>
                  <table width="130" border="0" cellspacing="0" cellpadding="3">
                    <tr> 
                      <td> 
                        <div align="center" class="Main"> Brought to you by: </div>
                      </td>
                    </tr>
                    <tr> 
                      <td> 
                        <div align="center"><img src="allianceLogo.gif" width="108" height="63"></div>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr> 
    <td bgcolor="#666699" height="13" align = "center" valign = "top" class = "TableCell2"> 
      Back to:&nbsp;<a class = "Link2" href="../../login.jsp">demo.cannontech.com</a> 
    </td>
  </tr>
  </table>
<div align="center" class="TableCell2"></div>
</body>

</html>
