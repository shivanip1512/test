<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFFF" text="#000000">
<form method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
<input type="hidden" name="action" value="SearchCustAccount">
  <table width="100%" border="0" cellpadding = "5">
    <tr> 
      <td width="33%" valign = "top" align = "left"><span class="TitleHeader">Acct #<%= account.getAccountNumber() %></span><br>
        <span class="NavText"><%= primContact.getFirstName() %>&nbsp;<%= primContact.getLastName() %><br>
        <!--<%= account.getCompany() %><br> -->
        <%= primContact.getHomePhone() %><br>
        <%= primContact.getWorkPhone() %></span></td>
      <td valign = "bottom" align = "center" width="33%" rowspan = "3" class = "TitleHeader"><%=header%></td>
      <td align = "right" width="33%"><span class="TitleHeader">
        <select name="SearchBy">
<%
	Integer lastOption = (Integer) user.getAttribute(ServletUtils.ATT_LAST_SEARCH_OPTION);
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE );
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
		String selectedStr = (lastOption != null && entry.getEntryID() == lastOption.intValue()) ? "selected" : "";
%>
		  <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
	}
%>
        </select>
        <input type="text" name="SearchValue" size="14">
        <input type="submit" name="Search" value="Search">
		<cti:checkProperty propertyid="<%= ConsumerInfoRole.SUPER_OPERATOR %>">
		  <input type="submit" name="Refresh" value="Refresh" onclick="javascript:this.form.action.value='ReloadCustAccount'">
		</cti:checkProperty>
        </span></td>
    </tr>
  </table>
</form>
</body>
</html>
