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
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
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
                        <table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
              <tr> 
                <td height="20" valign="top" align="center">&nbsp; </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <span class="Main">
                <b>INVENTORY</b></span>
              <form name="form1" method="post" action="InventoryDetail.jsp">
                <table width="250" border="0" cellspacing="0" cellpadding="5" align="center">
                  <tr> 
                    <td class="TableCell">Search for device by serial #</td>
                  </tr>
                  <tr> 
                    <td> 
                      <select name="select">
                        <option>12345</option>
                        <option>67890</option>
                      </select>
                      &nbsp; 
                      <input type="submit" name="" value="Search">
                    </td>
                  </tr>
                </table>
                <table width="250" border="0" cellspacing="0" align="center">
                  <tr> 
                    <td> <br>
                      <hr>
                    </td>
                  </tr>
                </table>
              </form>
			  <form name="form1" method="post" action="Inventory.jsp">
              <table width="250" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td class="TableCell">Add device serial # range:</td>
                </tr>
                <tr> 
                  <td><span class="TableCell">Device Type</span>&nbsp; 
                    <select name="select2">
                      <option>LCR 5000</option>
                    </select>
                  </td>
                </tr>
                <tr> 
                  <td> 
                    <table width="240" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                        <td class="TableCell">From</td>
                        <td class="TableCell">To</td>
                      </tr>
                      <tr> 
                        <td> 
                          <input type="text" name="textfield" size="14">
                        </td>
                        <td> 
                          <input type="text" name="textfield2" size="14">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
                <tr> 
                  <td> 
                    <input type="submit" name="" value="Submit">
                  </td>
                </tr>
              </table>
			  </form>
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
