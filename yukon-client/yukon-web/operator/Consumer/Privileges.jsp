<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
</script>
</head>

<body bgcolor="#666699" leftmargin="0" topmargin="0" link="#FFFFFF" vlink="#FFFFFF" alink="#FFFFFF">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr bgcolor="#FFFFFF"> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="3" border="0">
              <tr> 
                <td id="Header" colspan="4" height="72" background="../Header.gif">&nbsp;</td>
<script language="JavaScript">
	document.getElementById("Header").background = '../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>';
</script>
              </tr>
              <tr bgcolor="666699"> 
                <form method="get" action="">
                  <td width="265" height = "30" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
                </form>
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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="#666699" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101"> 
            <% String pageName = "Privileges.jsp"; %>
            <%@ include file="Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><% String header = "ADMINISTRATION - CUSTOMER PRIVILEGES"; %><%@ include file="InfoSearchBar.jsp" %> 
            
              <p align="center" class="MainHeader">Select which of the following functionality this 
                customer will have access to:</p>
              <table width="500" border="0" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td width="171" class="TableCell"> 
                    <div align="right"> 
                      <input type="checkbox" name="checkbox" value="checkbox">
                    </div>
                  </td>
                  <td width="317" class="TableCell">Change account information</td>
                </tr>
                <tr> 
                  <td width="171" class="TableCell"> 
                    <div align="right"> 
                      <input type="checkbox" name="checkbox2" value="checkbox">
                    </div>
                  </td>
                  <td width="317" class="TableCell">Self Directed Control</td>
                </tr>
                <tr> 
                  <td width="171" class="TableCell"> 
                    <div align="right"> 
                      <input type="checkbox" name="checkbox3" value="checkbox">
                    </div>
                  </td>
                  <td width="317" class="TableCell">Metering</td>
                </tr>
                <tr> 
                  <td width="171" class="TableCell"> 
                    <div align="right"> 
                      <input type="checkbox" name="checkbox4" value="checkbox">
                    </div>
                  </td>
                  <td width="317" class="TableCell">Touch tone phone</td>
                </tr>
                <tr> 
                  <td width="171" class="TableCell"> 
                    <div align="right"> 
                      <input type="checkbox" name="checkbox" value="checkbox">
                    </div>
                  </td>
                  <td width="317" class="TableCell">Opt out of programs permanently</td>
                </tr>
                <tr> 
                  <td width="171" class="TableCell"> 
                    <div align="right"> 
                      <input type="checkbox" name="checkbox2" value="checkbox">
                    </div>
                  </td>
                  <td width="317" class="TableCell">Opt out of all programs temporarily</td>
                </tr>
                <tr> 
                  <td width="171" class="TableCell"> 
                    <div align="right"> 
                      <input type="checkbox" name="checkbox3" value="checkbox">
                    </div>
                  </td>
                  <td width="317" class="TableCell">E-mail notification of control</td>
                </tr>
              </table>
              <p></p>
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr>
				<form name="form1" method="get" action="Privileges.jsp"> 
                    <td width="160"> 
                      <div align="right"> 
                        <input type="submit" name="Submit2" value="Submit">
                      </div>
                  </td>
				  </form>
                  <form name="form1" method="get" action="">
                    <td width="220"> 
                      <div align="left"> 
                        <input type="reset" name="Cancel" value="Cancel">
                      </div>
                  </td>
				  </form>
                </tr>
              </table>
              <p>&nbsp;</p>
              </div>
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
