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
          <td width="102" height="102" background="WorkImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" class="BlueHeader">&nbsp;&nbsp;&nbsp;Work Orders</td>
                <td width="235" height = "28" valign="middle">&nbsp;</td>
                <form method="post" action="../Operations.jsp">
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="blueLink">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="blueLink">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
                </form>
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
                        
            <table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
              <tr> 
                <td class="TableCellWhite"><br>
                  Search:</td>
              </tr>
              <tr> 
                <td> 
                  <select name="select3">
                    <option>Serial #</option>
                    <option>Order #</option>
					<option>Address</option>
                  </select>
                </td>
              </tr>
              <tr> 
                <td> 
                  <input type="text" name="textfield3" size="10">
                </td>
              </tr>
              <tr> <form method="post" action="InstallDetail.jsp">
                <td> 
                  <input type="submit" name="" value="Search">
                </td></form>
              </tr>
              <tr> 
                <td height="20" valign="top" align="center">&nbsp; </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> <br>
              <span class="Main"><b>INSTALLATION LIST</b></span> 
              <p><span class="Main">Display</span>:&nbsp; 
                <select name="select">
                  <option>All</option>
                  <option>Open</option>
                  <option>Scheduled</option>
                </select>
                &nbsp; 
                <input type="submit" name="Submit" value="Submit">
              </p>
              <form method="post" action="InstallDetail.jsp">
                <table width="550" border="1" cellspacing="0" cellpadding="3">
                  <tr> 
                    <td width="84" class="HeaderCell">Work Order # </td>
                    <td width="61" class="HeaderCell">Date</td>
                    <td width="62" class="HeaderCell">Assigned</td>
                    <td width="225" class="HeaderCell">Description</td>
                    <td width="76" class="HeaderCell">Details</td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td width="84" class="TableCell">12345</td>
                    <td width="61" class="TableCell">05/06/02</td>
                    <td width="62" class="TableCell">Yes</td>
                    <td width="225" class="TableCell"> 
                      <textarea name="textarea" rows="2 wrap="soft" cols="24"></textarea>
                    </td>
                    <td width="76" class="TableCell"> 
                      <input type="submit" name="" value="Details">
                    </td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td width="84" class="TableCell">67890</td>
                    <td width="61" class="TableCell">05/07/02</td>
                    <td width="62" class="TableCell">No</td>
                    <td width="225" class="TableCell"> 
                      <textarea name="textarea2" rows="2 wrap="soft" cols="24"></textarea>
                    </td>
                    <td width="76" class="TableCell"> 
                      <input type="submit" name="" value="Details">
                    </td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td width="84" class="TableCell">12345</td>
                    <td width="61" class="TableCell">05/07/02</td>
                    <td width="62" class="TableCell">No</td>
                    <td width="225" class="TableCell"> 
                      <textarea name="textarea3" rows="2 wrap="soft" cols="24"></textarea>
                    </td>
                    <td width="76" class="TableCell"> 
                      <input type="submit" name="" value="Details">
                    </td>
                  </tr>
                </table>
              </form>
              <p>&nbsp;</p>
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
