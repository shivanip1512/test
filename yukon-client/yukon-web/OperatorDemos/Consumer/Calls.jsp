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
                <td colspan="5" height="74" background="../Header.gif">&nbsp;</td>
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
		  <% String pageName = "Calls.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> <% String header = "ACCOUNT - CALL TRACKING"; %><%@ include file="InfoSearchBar.jsp" %>
             
              <br>
              <form name="form1" method="post" action="">
                <table width="600" border="1" cellspacing="0" align="center" cellpadding="2">
                  <tr> 
                    <td class="HeaderCell" width="79">Date</td>
                    <td class="HeaderCell" width="82">Type</td>
                    <td class="HeaderCell" width="82">User ID</td>
                    <td class="HeaderCell" width="238">Description</td>
                    <td class="HeaderCell" width="87">Status</td>
                  </tr>
                  <tr> 
                    <td class="TableCell" width="79">02/15/01</td>
                    <td class="TableCell" width="82">Credit</td>
                    <td class="TableCell" width="82">SRD</td>
                    <td class="TableCell" width="238"> 
                      <textarea name="textarea" rows="2 wrap="soft" cols="30"></textarea>
                    </td>
                    <td class="TableCell" width="87">Approved</td>
                  </tr>
                  <tr> 
                    <td class="TableCell" width="79">11/23/01</td>
                    <td class="TableCell" width="82">General</td>
                    <td class="TableCell" width="82">SRD</td>
                    <td class="TableCell" width="238"> 
                      <textarea name="textarea2" rows="2 wrap="soft" cols="30"></textarea>
                    </td>
                    <td class="TableCell" width="87">Denied</td>
                  </tr>
                  <tr> 
                    <td class="TableCell" width="79">06/01/02</td>
                    <td class="TableCell" width="82">General</td>
                    <td class="TableCell" width="82">NCM</td>
                    <td class="TableCell" width="238"> 
                      <textarea name="textarea3" rows="2 wrap="soft" cols="30"></textarea>
                    </td>
                    <td class="TableCell" width="87">Submitted</td>
                  </tr>
                </table>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="Save2" value="Save">
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
            </div>
            <p align="center">&nbsp;</p>
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
