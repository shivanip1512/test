<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFFF" text="#000000">
  <table width="600" border="0" cellspacing="0">
	<tr> 
	  <td width="202"> 
		<table width="200" border="0" cellspacing="0" cellpadding="3">
		  <tr> 
			<td><span class="Main"><b>Acct #<%= account.getAccountNumber() %></b></span><br>
			  <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
			  <!--<%= account.getCompany() %><br> -->
			  <%= ServletUtils.getFormattedAddress(propAddr) %><br>
			  </span></td>
		  </tr>
		</table>
	  </td>
	  <td width="187" valign="top"> 
		<div align="center"><b><span class="Main"><%= header %></span></b></div>
	  </td>
	  
    <td valign="top" width="205" align = "right">&nbsp;</td>
	</tr>
  </table>
</body>
</html>
