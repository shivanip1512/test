<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="InventoryImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height="28" class="PageHeader">&nbsp;&nbsp;&nbsp;Hardware 
                  Inventory </td>
                <td width="235" height = "30" valign="middle">&nbsp;</td>
                <form method="post" action="../Operations.jsp">
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
                </form>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
            <table width="101" border="0" cellspacing="0" cellpadding="0" height="200">
              <tr> 
                <td height="20" valign="top" align="center" class = "TableCell1"><br><a class = "Link3" href ="AddSN.jsp">Add 
                  SN Range</a> </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "INVENTORY DETAIL"; %>
              <%@ include file="../SearchBar.jsp" %>
              <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                <tr>
				<form name="form6" method="get" action=""> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><span class="MainHeader"><b>DEVICE</b></span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Serial #:</div>
                                </td>
                                <td width="210"> 
                                  <select name="select2">
                                    <option>12345</option>
                                    <option>67890</option>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right"> Type:</div>
                                </td>
                                <td width="210"> 
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
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Installed:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield32232" maxlength="30" size="24">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Alt Tracking #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield32232" maxlength="30" size="24">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Date Received:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield32234" maxlength="30" size="24">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Voltage:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="textfield32236" maxlength="30" size="24">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Service Company:</div>
                                </td>
                                <td width="210"> 
                                  <select name="select6">
                                    <option>ACME Company</option>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Notes:</div>
                                </td>
                                <td width="210"> 
                                  <textarea name="textarea2" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                                </td>
                              </tr>
                            </table>
                           </td>
                        </tr>
                      </table>
                  </td>
				  </form>
				  <form name="form7" method="post" action="">
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td><span class="MainHeader"><b>LAST KNOWN LOCATION</b></span>
<hr>
                            <table width="300" border="0" cellspacing="0">
                              <tr>
                                <td class="TableCell" width="67">Location:</td>
                                <td width="229"> 
                                  <select name="select4">
                                    <option>Customer</option>
                                    <option>Warehouse</option>
                                    </select>
                                </td>
                              </tr>
                            </table>
                            <table width="300" border="0" cellspacing="0" cellpadding="3" align="center">
                              <tr> 
                                <td class="TableCell"> Account # 12345 </td>
                              </tr>
                              <tr> 
                                <td class="TableCell"> Acme Company </td>
                              </tr>
                              <tr> 
                                <td class="TableCell"> Last Name, First Name</td>
                              </tr>
                              <tr> 
                                <td class="TableCell"> Home #: 800-555-1212 </td>
                              </tr>
                              <tr> 
                                <td class="TableCell"> Work #: 800-555-2121 </td>
                              </tr>
                            </table>
                            <table width="300" border="0" cellspacing="0" cellpadding="3" align="center">
                              <tr> 
                                <td class="TableCell"> 1234 Main Street, Floor 
                                  3 </td>
                              </tr>
                              <tr> 
                                <td class="TableCell"> Golden Valley, MN 55427</td>
                              </tr>
                              <tr> 
                                <td class="TableCell"> Map # 3A </td>
                              </tr>
                              </table>
                          </td>
                        </tr>
                      </table>
                      
                    </td>
				  </form>
                </tr>
              </table>
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr> 
                  <form name="form3" method="post" action="InventoryDetail.jsp">
				  <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="" value="Save">
                      </div>
                  </td>
				  </form>
				  <form name="form4" method="get" action="">
                  <td width="194"> 
                      <div align="left"> 
                        <input type="reset" name="Cancel2" value="Cancel">
                      </div>
                  </td>
				    </form>
                </tr>
              </table><br>
              </div>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
