<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFFF" text="#000000">
<form method="POST" action="/servlet/SOAPClient">
<input type="hidden" name="action" value="SearchCustAccount">
  <table width="100%" border="0" cellpadding = "5" height="69">
    <tr> 
      <td width="31%" valign = "top" align = "left">&nbsp;</td>
      <td valign = "bottom" align = "center" width="37%" rowspan = "3" class = "TitleHeader"><%=header%></td>
      <td align = "right" width="31%"><span class="TitleHeader"> 
        <select name="SearchBy">
          <option value="AccountNumber" selected>Acct #</option>
          <option value="PhoneNumber">Phone #</option>
          <option value="Name">Name</option>
		  <option value="SerialNumber">Serial #</option>
		  <option value="WorkOrderNumber">Order #</option>
        </select>
        <input type="text" name="SearchValue" size = "15">
        <input type="submit" name="Submit2" value="Search">
        </span></td>
    </tr>
  </table>
</form>
</body>
</html>
