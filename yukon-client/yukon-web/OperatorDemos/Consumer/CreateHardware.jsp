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
 
function getCurrentDateFormatted() {
	
	var strDate;
	var myDate = new Date();
	
	var month = myDate.getMonth() + 1;
	var day = myDate.getDate();
	var year = myDate.getFullYear();
	
	if (month < 10)
		month = "0" + month;
	if (day < 10)
		day = "0" + day;
	
	strDate = month+"/"+day+"/"+year;
	
	return strDate;
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
            <div class = "Main" align="center">
              <% String header = "CREATE NEW HARDWARE"; %>
              <%@ include file="InfoSearchBar.jsp" %>
              <br>
             
              <form name = "MForm" onload >
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                  <tr> 
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td valign="top"><span class="MainHeader"><b>DEVICE</b></span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Type: </div>
                                </td>
                                <td width="200"> 
                                  <select name="DeviceType">
                                   <option>LCR1000</option>
								   <option>LCR2000</option>
								   <option>LCR3000</option>
								   <option>LCR4000</option>
								   <option>LCR5000</option>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Serial #: </div>
                                </td>
                                <td width="200"> 
                                  <!--
                                <select name="select">
                                  <option>12345</option>
                                  <option>67890</option>
                                </select>
-->
                                  <input type="text" name="SerialNumber" maxlength="30" size="24" value="">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Alt Tracking #: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="AltTrackNumber" maxlength="30" size="24" value="">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Receive Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="ReceiveDate" maxlength="30" size="24" value="">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Remove Date: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="RemoveDate" maxlength="30" size="24" value="">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Voltage: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="Voltage" maxlength="30" size="24" value="">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Status: </div>
                                </td>
                                <td width="200"> 
                                  <select name="select6">
                                    <option>Available</option>
                                    <option>Temp Unavail</option>
                                    <option>Unavailable</option>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Notes: </div>
                                </td>
                                <td width="200"> 
                                  <textarea name="Notes" rows="2 wrap="soft" cols="24"></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td valign="top"><span class="MainHeader"><b>INSTALL</b></span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Date Installed: </div>
                                </td>
                                <td width="200"> 
                                  <input type="text" name="InstallDate" maxlength="30" size="24" value="">
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Service Company: </div>
                                </td>
                                <td width="200"> 
                                  <select name="ServiceCompany">
                                    <option>Johnson Electric</option>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Location: </div>
                                </td>
                                <td width="200"> 
                                  <select name="select4">
                                    <option>Outside North</option>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="100" class="TableCell"> 
                                  <div align="right">Notes: </div>
                                </td>
                                <td width="200"> 
                                  <textarea name="notes" rows="2 wrap="soft" cols="24"></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <div align="center"><br>
                        <br>
                      </div>
                      
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
