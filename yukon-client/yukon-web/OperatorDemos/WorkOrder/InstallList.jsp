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
                <td width="310" class="Header3">&nbsp;&nbsp;&nbsp;Work Orders</td>
                <td width="235" height = "28" valign="middle">&nbsp;</td>
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
                        
            </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "INSTALLATION LIST"; %><%@ include file="SearchBar.jsp" %>
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
                <table border="1" cellspacing="0" cellpadding="3" >
                  <tr> 
                    <td  class="HeaderCell" >Work Order # </td>
                    <td  class="HeaderCell" >Date</td>
                    <td  class="HeaderCell" >Assigned</td>
                    <td  class="HeaderCell" >Description</td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td  class="TableCell" ><a href="InstallDetail.jsp" class="Link1">12345</a></td>
                    <td  class="TableCell" >05/06/02</td>
                    <td  class="TableCell" >Yes</td>
                    <td  class="TableCell" > 
                      <textarea name="textarea" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                    </td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td  class="TableCell" ><a href="InstallDetail.jsp" class="Link1">67890</a></td>
                    <td  class="TableCell" >05/07/02</td>
                    <td  class="TableCell" >No</td>
                    <td  class="TableCell" > 
                      <textarea name="textarea2" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                    </td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td  class="TableCell" ><a href="InstallDetail.jsp" class="Link1">12345</a></td>
                    <td  class="TableCell" >05/07/02</td>
                    <td  class="TableCell" >No</td>
                    <td  class="TableCell" > 
                      <textarea name="textarea3" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
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
