<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
<br>
<table width="450" border="1" align="center" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
  <tr> 
    <td colspan = "2" align = "center" height="51" valign = "bottom"><img src="PGELogo.gif"> 
      <table width="400" border="0" height="43">
        <tr> 
          <td width="34%" align = "center"><span class="TableCell"><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#999999"><b><font face="Arial, Helvetica, sans-serif" color="#666666" size="4">Welcome 
            to</font></b></font> </span></td>
          <td width="66%">&nbsp;</td>
        </tr>
        <tr> 
          <td colspan = "2" align = "center"><span class="TableCell"><b><font size="4">Energy 
            Services Operations Center!</font></b></span> </td>
        </tr>
      </table>
      
    </td>
  </tr>
  <tr> 
    <td width="407" valign = "top" class = "Main"> <br> 
<%
	String errorMsg = (String) session.getAttribute(com.cannontech.stars.util.ServletUtils.ATT_ERROR_MESSAGE);
	session.removeAttribute(com.cannontech.stars.util.ServletUtils.ATT_ERROR_MESSAGE);
%>
	  <div align="center"><% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %></div>
	
      <table width="367"  height="186" border="0" align="center" cellpadding="0" cellspacing="0" class = "Main">
        <tr> 
          <td rowspan = "3" width="555" bgcolor="#FFFFFF" height="102" valign="top" ><br> 
            <div align="center"><b>SIGN IN</b><br>
              Please enter your account number and password below.</div>
            <form name="form1" method="post" action="/servlet/SOAPClient">
              <input type="hidden" name="action" value="OperatorLogin">
			  <input type="hidden" name="REDIRECT" value="/OperatorDemos/Operations.jsp">
			  <input type="hidden" name="REFERRER" value="/LoginOp.jsp">
              <table width="250" border="0" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td width="83" class = "Main"> <div align="right"> User Name</div></td>
                  <td width="117" valign="bottom"> <input type="text" name="USERNAME"> 
                  </td>
                </tr>
                <tr> 
                  <td width="83" class = "Main"> <div align="right">Password:</div></td>
                  <td width="117"> <input type="password" name="PASSWORD"> 
                  </td>
                </tr>
                <tr> 
                  <td width="83">&nbsp;</td>
                  <td width="117"> <div align="left">
                      <input type="submit" name="Submit2" value="Submit">
                      </div></td>
                </tr>
              </table>
            </form></td>
        </tr>
        </table>
      <div align="center"> If you need help or have forgotten your password, click 
        <a href="mailto:waterheatercontrolpilot@pgn.com">here</a>. <br>
        <br>
      </div></td>
  </tr>
</table>
<br>
<div align="center" class="TableCell2"><img src="YukonLogo.gif" width="139" height="29"></div>
</body>

</html>
