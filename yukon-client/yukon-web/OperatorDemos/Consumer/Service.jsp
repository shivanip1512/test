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
            <table width="657" cellspacing="0"  cellpadding="-" border="0">
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
		  <% String pageName = "Service.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"><% String header = "WORK ORDERS - SERVICE REQUEST"; %><%@ include file="InfoSearchBar.jsp" %><br>
             
              <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                  <tr> 
                    <td valign="top" bgcolor="#FFFFFF"> 
                      <form name="form5" method="" action="">
                        <table width="400" border="0" cellspacing="0" cellpadding="1" align="center">
                          <tr> 
                            <td width="100" class="TableCell"> 
                              <div align="right">Service Order #:</div>
                            </td>
                            <td width="210"> 
                              <input type="text" name="textfield22332" maxlength="2" size="14">
                            </td>
                          </tr>
                          <tr> 
                            <td width="100" class="TableCell"> 
                              <div align="right">Date Reported:</div>
                            </td>
                            <td width="210"> 
                              <input type="text" name="textfield2234" maxlength="2" size="14">
                            </td>
                          </tr>
                          <tr> 
                            <td width="100" class="TableCell"> 
                              <div align="right">Service Type:</div>
                            </td>
                            <td width="210"> 
                              <select name="select">
                                <option>Service Call</option>
                              </select>
                            </td>
                          </tr>
                          <tr> 
                            <td width="100" class="TableCell"> 
                              <div align="right">Notes: </div>
                            </td>
                            <td width="210"> 
                              <textarea name="textarea2" rows="2 wrap="soft" cols="25"></textarea>
                            </td>
                          </tr>
                        </table>
                      </form>
                      <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                        <tr> 
                          <form name="form3" method="get" action="Service.jsp">
                            
                          <td width="169"> 
                            <div align="right"> 
                                <input type="submit" name="Send" value="Send">
                              </div>
                            </td>
                          </form>
                          <form name="form4" method="" action="">
                            
                          <td width="211"> 
                            <div align="left"> 
                                <input type="reset" name="Cancel2" value="Cancel">
                              </div>
                            </td>
                          </form>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <div align="center"><br>
              </div>
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
