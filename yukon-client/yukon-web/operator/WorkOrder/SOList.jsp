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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> <b> 
              <% String header = "SERVICE ORDER LIST"; %>
              <%@ include file="SearchBar.jsp" %>
              </b> 
              <p><span class="Main">Display</span>:&nbsp; 
                <select name="select">
                  <option>All</option>
                  <option>Open</option>
                  <option>Scheduled</option>
                </select>
                &nbsp; 
                <input type="submit" name="" value="Submit">
                <br>
                <br>
                <span class="Main">Click on a Service Order # to view the service 
                order details.</span></p>
              <form method="post" action="WorkOrder.jsp">
                
                <table border="1" cellspacing="0" cellpadding="3" width="625" height="182">
                  <tr> 
                    <td  class="HeaderCell" width="90" >Service Order 
                      # </td>
                    <td  class="HeaderCell" width="58" >Date/Time</td>
                    <td  class="HeaderCell" width="46" >Type</td>
                    <td  class="HeaderCell" width="54" >Status</td>
                    <td  class="HeaderCell" width="47" >By Who</td>
                    <td  class="HeaderCell" width="53" >Assigned</td>
                    <td  class="HeaderCell" width="219" >Description</td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td class="TableCell" width="90" ><a href="WorkOrder.jsp" class="Link1">12345</a></td>
                    <td class="TableCell" width="58" >05/06/02</td>
                    <td class="TableCell" width="46" >Install</td>
                    <td class="TableCell" width="54" >Complete</td>
                    <td class="TableCell" width="47" >eah</td>
                    <td class="TableCell" width="53" >XYZ Company</td>
                    <td class="TableCell" width="219"> 
                      <textarea name="textarea" rows="2" wrap="soft" cols="28" class = "TableCell"></textarea>
                    </td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td class="TableCell" width="90" ><a href="WorkOrder.jsp" class="Link1">67890</a></td>
                    <td  class="TableCell" width="58" >05/07/02</td>
                    <td  class="TableCell" width="46" >Install</td>
                    <td  class="TableCell" width="54" >Complete</td>
                    <td  class="TableCell" width="47" >eah</td>
                    <td  class="TableCell" width="53" >&nbsp;</td>
                    <td class="TableCell" width="219" > 
                      <textarea name="textarea2" rows="2" wrap="soft" cols="28" class = "TableCell"></textarea>
                    </td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td  class="TableCell" width="90"><a href="WorkOrder.jsp" class="Link1">12345</a></td>
                    <td  class="TableCell" width="58">05/07/02</td>
                    <td  class="TableCell" width="46">Install</td>
                    <td  class="TableCell" width="54">Complete</td>
                    <td  class="TableCell" width="47">rst</td>
                    <td class="TableCell" width="53">XYZ Company</td>
                    <td  class="TableCell" width="219"> 
                    <textarea name="textarea3" rows="2" wrap="soft" cols="28" class = "TableCell"></textarea>
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
