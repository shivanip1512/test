<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

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
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
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
                <td height="20" valign="top" align="center" class = "TableCell1">&nbsp;</td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center">
			 <% String header = "ADD SERIAL NUMBER RANGE"; %>
              <%@ include file="../SearchBar.jsp" %>
              <table width="64%" border="1" cellspacing="0" cellpadding="5" align="center" height="91">
                <tr> 
                  <td align = "left" class = "Main" bgcolor="#CCCCCC"><b>Add Serial 
                    Number Range</b></td>
                </tr>
                <tr> 
                  <td> 
                    <table width="100%" border="0">
                      <tr>
                        <td><span class="TableCell">Device Type</span>: 
                          <select name="select">
                            <option>LCR 5000</option>
                            <option>LCR 4000</option>
                            <option>LCR 3000</option>
                            <option>LCR 2000</option>
                            <option>LCR 1000</option>
                          </select>
                        </td>
                        <td class = "TableCell">From: 
                          <input type="text" name="textfield2" size="10">
                          To: 
                          <input type="text" name="textfield" size="10">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table><br>
              <table width="64%" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="#FFFFFF">
                <tr> 
                  <form name="form3" method="post" action="InventoryDetail.jsp">
				  <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="Input" value="Submit">
                      </div>
                  </td>
				  </form>
				  <form name="form4" method="get" action="InventoryDetail.jsp">
                  <td width="194"> 
                      <div align="left"> 
                        <input type="submit" name="Cancel2" value="Cancel">
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
