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
      <td valign = "bottom" align = "center" width="37%" rowspan = "3" class = "Main"><b><%=header%></b></td>
      <td align = "right" width="31%"><span class="Main"><b> 
        <select name="SearchBy">
          <option value="AccountNumber" selected><span class="Main"><b>Acct #</b></span></option>
          <option value="PhoneNumber"><span class="Main"><b>Phone #</b></span></option>
          <option value="Name"><span class="Main"><b>Name</b></span></option>
        </select>
        <input type="text" name="SearchValue" size = "15">
        <input type="submit" name="Submit2" value="Search">
        </b></span></td>
    </tr>
  </table>
</form>
</body>
</html>
