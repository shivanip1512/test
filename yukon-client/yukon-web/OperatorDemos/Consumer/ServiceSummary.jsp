<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
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
		  <% String pageName = "ServiceSummary.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
              <div align="center"><% String header = "WORK ORDERS - SERVICE HISTORY"; %><%@ include file="InfoSearchBar.jsp" %><br>
              <br>
            
            </div>
                
              <table width="550" border="1" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td width="62" class="HeaderCell">Order # </td>
                    <td width="100" class="HeaderCell">Date/Time</td>
                    <td width="116" class="HeaderCell">Action</td>
                    <td width="56" class="HeaderCell">Status</td>
                    <td width="174" class="HeaderCell">Notes</td>
                  </tr>
                  <tr valign="top"> 
                    <form name="form3" method="get" action="SOHistory.jsp">
                      <td width="62" class="TableCell">12345<br>
                        <input type="submit" name="Details" value="Details">
                        </td>
                    </form>
                    <td width="100" class="TableCell">04/02/02 12:32</td>
                    <td width="116" class="TableCell">Temporary Disabled</td>
                    <td width="56" class="TableCell">Complete</td>
                    <form name="form4" method="" action="">
                      <td width="174"> 
                        <textarea name="textarea" rows="2 wrap="soft" cols="24"></textarea>
                      </td>
                    </form>
                  </tr>
                  <tr valign="top"> 
                    <form name="form3" method="get" action="SOHistory.jsp">
                      <td width="62" class="TableCell">67890<br>
                        <input type="submit" name="Details2" value="Details">
                        </td>
                    </form>
                    <td width="100" class="TableCell">03/14/02 14:46 </td>
                    <td width="116" class="TableCell">Future Activation </td>
                    <td width="56" class="TableCell">Complete</td>
                    <form name="form4" method="" action="">
                      <td width="174"> 
                        <textarea name="textarea" rows="2 wrap="soft" cols="24"></textarea>
                      </td>
                    </form>
                  </tr>
                </table>
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
