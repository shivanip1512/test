<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">

</head>

<body class="Background" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip" style="width: 1003px; height: 20px"></div>
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
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "PROGRAMS - ODDS FOR CONTROL"; %>
              <%@ include file="InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
             
              <div align="center"><span class="Main">Check the appropriate 
                odds for control for each program.</span><br>
              </div>
			  <form name="form1" method="post" action="/servlet/SOAPClient">
                <table border="1" cellspacing="0" cellpadding="3" width="366">
                  <tr> 
                    <td width="244" class="HeaderCell"> 
                      <div align="center">Program Enrollment</div>
                    </td>
                    <td width="104" class="HeaderCell"> 
                      <div align="center">Odds for Control</div>
                    </td>
                  </tr>
                  
                </table>
             	<table border="1" cellspacing="0" cellpadding="3" width="366">
                  <tr> 
                    <td width="285" valign="middle" class="TableCell"> 
                      <div align="center">
                        <table width="350" border="0" cellspacing="0" cellpadding="0">
                          <tr>
                            <td width="88"><img src="AC.gif" width="60" height="59"></td>
                            <td width="268">
                              <table width="280" border="0" cellspacing="0" cellpadding="3">
                                <tr> 
                                  <td width="187" class="TableCell">AC Program 
                                    1</td>
                                  <td width="81"> 
                                    <select name="select2">
                                      <option>Likely</option>
                                      <option>Unlikely</option>
                                    </select>
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="187" class="TableCell">AC Program 
                                    2</td>
                                  <td width="81"> 
                                    <select name="select">
                                      <option>Likely</option>
                                      <option>Unlikely</option>
                                    </select>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        
                      </div>
                    </td>
                  </tr>
                  </table>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="Submit" value="Submit">
                      </div>
                    </td>
                    <td width="194"> 
                      <div align="left"> 
                        <input type="reset" name="Cancel2" value="Cancel">
                      </div>
                    </td>
                  </tr>
                </table>
              </form>
			  
                
              <p align="center" class="MainHeader"><br>
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
