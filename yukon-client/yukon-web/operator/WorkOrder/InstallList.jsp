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
                <td width="310" class="PageHeader">&nbsp;&nbsp;&nbsp;Work Orders</td>
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
                        
            </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "INSTALLATION LIST"; %>
              <%@ include file="SearchBar.jsp" %>
              <p><span class="Main">Display</span>:&nbsp; 
                <select name="select">
                  <option>All</option>
                  <option>Open</option>
                  <option>Scheduled</option>
                </select>
                &nbsp; 
                <input type="submit" name="Submit" value="Submit">
                <br>
                <br>
                <span class="Main">Click on an Install Order # to view the install 
                order details.</span> 
              <form method="post" action="InstallDetail.jsp">
                <table border="1" cellspacing="0" cellpadding="3" width="626" height="186" >
                  <tr> 
                    <td  class="HeaderCell" width="79" >Install Order # </td>
                    <td  class="HeaderCell" width="55" >Date/Time</td>
                    <td  class="HeaderCell" width="49" >Type</td>
                    <td  class="HeaderCell" width="49" >Status</td>
                    <td  class="HeaderCell" width="44"  >By Who</td>
                    <td  class="HeaderCell" width="60"  >Assigned</td>
                    <td  class="HeaderCell" width="232"  >Description</td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td  class="TableCell" width="79"  ><a href="InstallDetail.jsp" class="Link1">12345</a></td>
                    <td  class="TableCell" width="55"  >05/06/02</td>
                    <td  class="TableCell" width="49"  >Install</td>
                    <td  class="TableCell" width="49"  >Complete</td>
                    <td  class="TableCell" width="44"  >eah</td>
                    <td  class="TableCell" width="60"  >XYZ Company</td>
                    <td  class="TableCell" width="232"  > 
                      <textarea name="textarea" rows="2" wrap="soft" cols="30" class = "TableCell"></textarea>
                    </td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td  class="TableCell" width="79"  ><a href="InstallDetail.jsp" class="Link1">67890</a></td>
                    <td  class="TableCell" width="55"  >05/07/02</td>
                    <td  class="TableCell" width="49"  >Install</td>
                    <td  class="TableCell" width="49"  >Complete</td>
                    <td  class="TableCell" width="44"  >eah</td>
                    <td  class="TableCell" width="60"  >&nbsp;</td>
                    <td  class="TableCell" width="232" > 
                      <textarea name="textarea2" rows="2" wrap="soft" cols="30" class = "TableCell"></textarea>
                    </td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td  class="TableCell" width="79" ><a href="InstallDetail.jsp" class="Link1">12345</a></td>
                    <td  class="TableCell" width="55" >05/07/02</td>
                    <td  class="TableCell" width="49" >Install</td>
                    <td  class="TableCell" width="49" >Complete</td>
                    <td  class="TableCell" width="44" >rst</td>
                    <td  class="TableCell" width="60" >XYZ Company</td>
                    <td  class="TableCell" width="232" > 
                      <textarea name="textarea3" rows="2" wrap="soft" cols="30" class = "TableCell"></textarea>
                    </td>
                  </tr>
                </table>
              </form>
              <p>&nbsp;</p>
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
