<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
  
 function goBack() {
  location = "Wizard3.jsp"
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
          <td width="102" height="102" background="Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height="28">&nbsp;&nbsp;&nbsp;</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="login.jsp" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src="stat/VerticalRule.gif" width="1"></td>
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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <form method="post" action="/servlet/SOAPClient" onsubmit="return checkPassword(this)">
			  <input type="hidden" name="action" value="ProgramSignUp">
			  <input type="hidden" name="CompanyID" value="1">
			  <input type="hidden" name="AcctNo" value="<%= request.getParameter("AcctNo") %>">
<%
	String[] catIDs = request.getParameterValues("CatID");
	String[] progIDs = request.getParameterValues("ProgID");
	if (progIDs != null)
		for (int i = 0; i < progIDs.length; i++) {
%>
			  <input type="hidden" name="CatID" value="<%= catIDs[i] %>">
			  <input type="hidden" name="ProgID" value="<%= progIDs[i] %>">
<%
		}
%>
			  <input type="hidden" name="REDIRECT" value="/UserDemos/ConsumerStat/login.jsp">
			  <input type="hidden" name="REFERRER" value="/UserDemos/ConsumerStat/ProgramsNew.jsp">
			  <div align="center"><br>
              <b><span class="Main">SIGN UP WIZARD<br>
              <br>
              </span></b> 
              <div align="center" class="Main"> </div>
             
                <table width="44%" border="1" cellspacing = "0" cellpadding = "3" height="140" >
                  <tr> 
                    <td class = "Main" ><b>User ID and Password</b></td>
                  </tr>
                  <tr> 
                    <td> 
                      <table width="100%" border="0">
                        <tr> 
                          <td align = "right" class = "TableCell" width="38%">User ID:</td>
                          <td width="62%"> 
                            <input type="text" name="UserName">
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr> 
                    <td> 
                      <table width="100%" border="0">
                        <tr> 
                          <td align = "right" class = "TableCell" width="38%">Password:</td>
                          <td width="62%"> 
                            <input type="password" name="Password">
                          </td>
                        </tr>
                        <tr> 
                          <td align = "right" class = "TableCell" width="38%">Confirm Password:</td>
                          <td width="62%"> 
                            <input type="password" name="Password2">
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table><br>
                <table width="150" border="0" cellspacing = "0" cellpadding = "0">
                  <tr>
                    <td align = "center">
                      <input type="submit" name="Submit" value="Submit">
                    </td>
                    <td>
                      <input type="button" name="cancel" value="Cancel" onclick="history.back()">
                    </td>
                  </tr>
                </table>
                <p>&nbsp;</p>
               
              </div>
            </form>
      
          </td>
          <td width="1" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
