<table width="600" border="0" cellspacing="0">
  <tr> 
	<td width="202"> 
	  <table width="200" border="0" cellspacing="0" cellpadding="3">
		<tr> 
		  <td><span class="TitleHeader"><b>Acct #<%= account.getAccountNumber() %></b></span><br>
			<span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
			<!--<%= account.getCompany() %><br> -->
			<%= ServletUtils.formatAddress(propAddr) %><br>
		  </span></td>
		</tr>
	  </table>
	</td>
	<td width="187" valign="top"> 
	  <div align="center"><span class="TitleHeader"><%= header %></span></div>
	</td>
    <td valign="top" width="205" align = "right">&nbsp;</td>
  </tr>
</table>
