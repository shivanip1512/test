<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFFF" text="#000000">
<form method="POST" action="/servlet/SOAPClient">
<input type="hidden" name="action" value="SearchCustAccount">
  <table width="100%" border="0" cellpadding = "5">
    <tr> 
      <td width="31%" valign = "top" align = "left"><span class="Main"><b>Acct 
        #<%= account.getAccountNumber() %></b></span><br>
        <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
        <!--<%= account.getCompany() %><br> -->
        <%= primContact.getHomePhone() %><br>
        <%= primContact.getWorkPhone() %></span></td>
      <td valign = "bottom" align = "center" width="37%" rowspan = "3" class = "Main"><b><%=header%></b></td>
      <td align = "right" width="31%"><span class="Main"><b> 
        <select name="SearchBy">
<%
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SEARCHBY );
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
%>
		  <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
	}
%>
        </select>
        <input type="text" name="SearchValue" size = "12">
        <input type="submit" name="Submit2" value="Search">
        </b></span></td>
    </tr>
  </table>
</form>
</body>
</html>
