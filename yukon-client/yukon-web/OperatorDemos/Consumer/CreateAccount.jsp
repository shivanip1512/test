<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
  
  
 function goBack() {
 document.location = "CreateWizard.jsp";
 
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
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                  
                <td width="265" height = "28" class="BlueHeader" valign="middle" align="left">&nbsp;</td>
                  
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "CREATE NEW ACCOUNT - GENERAL"; %>
              <%@ include file="SearchBar.jsp" %>
              <br>
              <form>
              <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> <span class="MainHeader"><b>CUSTOMER 
                    CONTACT</b></span> 
                    <hr>
                      <table width="301" border="0" cellspacing="0" cellpadding="1" align="center" height="252">
                        <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Account #:</div>
                        </td>
                        <td width="210" valign="top"> 
                          <table width="200" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="113"> 
                                <input type="text" name="AcctNo" maxlength="40" size="14" value="">
                              </td>
                              <td valign="top" class="TableCell" width="87"> Commercial: 
                                <input type="checkbox" name="Commercial" value="true">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Company: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="Company" maxlength="30" size="24" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Last Name:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName" maxlength="30" size="24" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName" maxlength="30" size="24" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home #:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="HomePhone" maxlength="14" size="14" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Work #:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="WorkPhone" maxlength="14" size="14" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="210"> 
                          <textarea name="AcctNotes" rows="2 wrap="soft" cols="22"></textarea>
                        </td>
                      </tr>
                    </table>
                    <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td><br>
                          <span class="MainHeader"><b>SERVICE INFORMATION</b></span> 
                          <hr>
                          <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Substation Name: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Substation" maxlength="30" size="24" value="">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Feeder: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Feeder" maxlength="20" size="24" value="">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Pole: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Pole" maxlength="20" size="24" value="">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Transformer Size: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="TranSize" maxlength="20" size="24" value="">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Service Voltage: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="ServVolt" maxlength="20" size="24" value="">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td width="300" valign="top" bgcolor="#FFFFFF"><span class="MainHeader"><b>SERVICE 
                    ADDRESS</b></span> 
                    <hr>
                      <table width="300" border="0" cellspacing="0" cellpadding="1" align="center" height="252">
                        <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr1" maxlength="30" size="24" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr2" maxlength="30" size="24" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">City:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SCity" maxlength="30" size="24" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">State:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SState" maxlength="2" size="14" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SZip" maxlength="12" size="14" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Map #:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="MapNo" maxlength="12" size="14" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="210"> 
                          <textarea name="SiteNotes" rows="2 wrap="soft" cols="22"></textarea>
                        </td>
                      </tr>
                    </table>
                    <b><br>
                    <span class="MainHeader">BILLING ADDRESS</span></b> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell">&nbsp;</td>
                        <td width="210"> 
                          <input type="button" name="Same" value="Same as Above" onClick="copyAddress(this.form)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr1" maxlength="30" size="24" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr2" maxlength="30" size="24" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">City:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BCity" maxlength="30" size="24" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">State:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BState" maxlength="2" size="14" value="">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BZip" maxlength="12" size="14" value="">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table></form>
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
              </table>
            </div>
            <p align="center">&nbsp;</p>
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
