<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="Calendar1-82.js"></SCRIPT>
<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers

  
function goBack() {
 document.location = "CreateWizard.jsp";
 }

  //End hiding script -->
  </SCRIPT>
</head>
<body class="Background" leftmargin="0" topmargin="0" onload = "javascript:MForm.date.value = getCurrentDateFormatted();">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
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
          <td width="1" height="102" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
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
          <td  valign="top" width="101"><% String pageName = "CreateWizard.jsp"; %><%@ include file="Nav.jsp" %></td>
          <td width="1" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "Main" align="center"><% String header = "CREATE NEW APPLIANCE"; %><%@ include file="InfoSearchBar.jsp" %><br>
           
              <form name = "MForm" onload >
                <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                  <tr>
                    <td colspan = "2"  class="TableCell"> <span class="MainHeader"><b>APPLIANCE 
                      INFORMATION</b></span> 
                      <hr>
                      </td>
                    
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Description: </div>
                    </td>
                    <td width="200"> 
                      <select name="Category">
                        <option>Air Conditioner</option>
                        <option>Water Heater</option>
                      </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Manufacturer:</div>
                    </td>
                    <td width="200"> 
                      <select name="select3">
                        <option>Century</option>
                      </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Year Manufactured:</div>
                    </td>
                    <td width="200"> 
                      <input type="text" name="textfield243" maxlength="14" size="14">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Location:</div>
                    </td>
                    <td width="200"> 
                      <select name="select4">
                        <option>Basement</option>
                      </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Service Company:</div>
                    </td>
                    <td width="200"> 
                      <input type="text" name="textfield532" maxlength="40" size="30">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Notes:</div>
                    </td>
                    <td width="200"> 
                      <textarea name="notes" rows="2 wrap="soft" cols="24"></textarea>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="150" border="0">
                <tr>
                  <td align = "center" > 
                    <input type="submit" name="Submit" value="Save">
                  </td>
                  <td> 
                    <input type="button" name="Submit2" value="Cancel" onclick = "goBack()">
                  </td>
                </tr>
              </table><br>
              </form>
            </div>
          
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
