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
          <td  valign="top" width="101">
		  <% String pageName = "Thermostat.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"><% String header = "HARDWARE - THERMOSTAT"; %><%@ include file="InfoSearchBar.jsp" %><br>
             
            </div>
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                <form name="form1" method="get" action="">
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
                                <select name="select5">
                                  <option>LCR 1000</option>
                                  <option>LCR 2000</option>
                                  <option>LCR 3000</option>
                                  <option>LCR 4000</option>
                                  <option>LCR 5000</option>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Serial #: </div>
                              </td>
                              <td width="200"> 
                                <select name="select">
                                  <option>12345</option>
                                  <option>67890</option>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Alt Tracking #: </div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="textfield32232" maxlength="30" size="24">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Receive Date: </div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="textfield32234" maxlength="30" size="24">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Remove Date: </div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="textfield32235" maxlength="30" size="24">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Voltage: </div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="textfield32236" maxlength="30" size="24">
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
                                <textarea name="textarea" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                </form>
                <form name="form1" method="get" action="">
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
                                <input type="text" name="textfield3223" maxlength="30" size="24">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Service Company: </div>
                              </td>
                              <td width="200"> 
                                <select name="select3">
                                  <option>ACME Company</option>
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
                                <textarea name="notes" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <div align="center"><br>
                      <span class="MainHeader"> Hardware History</span></div>
                    <table width="250" border="1" cellspacing="0" cellpadding="3" align="center">
                      <tr> 
                        <td width="104" class="HeaderCell">Date</td>
                        <td width="100" class="HeaderCell">Action</td>
                      </tr>
                      <tr valign="top"> 
                        <td width="104" class="TableCell" bgcolor="#FFFFFF">11/02/99</td>
                        <td width="100" class="TableCell" bgcolor="#FFFFFF">Install</td>
                      </tr>
                      <tr valign="top"> 
                        <td width="104" class="TableCell" bgcolor="#FFFFFF">07/30/00</td>
                        <td width="100" class="TableCell" bgcolor="#FFFFFF">Reconfigure</td>
                      </tr>
                      <tr valign="top"> 
                        <td width="104" class="TableCell" bgcolor="#FFFFFF">04/05/01</td>
                        <td width="100" class="TableCell" bgcolor="#FFFFFF">Repair</td>
                      </tr>
                    </table>
                    
                  </td>
                </form>
              </tr>
            </table>
            <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
              <tr> 
                <form name="form1" method="get" action="Thermostat.jsp">
                  <td width="186"> 
                    <div align="right"> 
                      <input type="submit" name="Submit2" value="Submit">
                    </div>
                  </td>
                </form>
                <form name="form1" method="get" action="">
                  <td width="194"> 
                    <div align="left"> 
                      <input type="reset" name="Cancel2" value="Cancel">
                    </div>
                  </td>
                </form>
              </tr>
            </table>
            <hr>
            <div align="center"><br>
              <b><span class="MainHeader">Appliance Summary</span></b><br>
               
              <table width="350" border="1" cellspacing="0" cellpadding="3">
                <tr> 
                  <td width="104" class="HeaderCell">Appliance Type </td>
                  <td width="100" class="HeaderCell">Status</td>
                  <td width="120" class="HeaderCell">Enrolled Programs</td>
                </tr>
                <tr valign="top"> 
                  <td width="104" class="TableCell" bgcolor="#FFFFFF">Air Conditioner</td>
                  <td width="100" class="TableCell" bgcolor="#FFFFFF">Out of Service</td>
                  <td width="120" bgcolor="#FFFFFF"> 
                    <div align="center"><img src="AC.gif" width="60" height="59"></div>
                  </td>
                </tr>
              </table>
              &nbsp;</div>
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
