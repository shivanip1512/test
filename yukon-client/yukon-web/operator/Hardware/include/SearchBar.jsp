<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFFF" text="#000000">
<form method="POST" action="/servlet/InventoryManager">
<input type="hidden" name="action" value="SearchInventory">
<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Hardware/ResultSet.jsp">
  <table width="100%" border="0" cellpadding = "5" height="69">
    <tr> 
      <td width="33%" valign = "top" align = "left">&nbsp;</td>
      <td valign = "bottom" align = "center" width="33%" rowspan = "3" class = "TitleHeader"><%=header%></td>
      <td align = "right" width="33%"><span class="MainText"><b> 
        <select name="SearchBy">
<%
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY);
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
%>
          <option value="<%= entry.getYukonDefID() %>"><span class="MainText"><b><%= entry.getContent() %></b></span></option>
<%
	}
%>
        </select>
        <input type="text" name="SearchValue" size = "14">
        <input type="submit" name="Submit" value="Search">
        </b></span></td>
    </tr>
  </table>
</form>
</body>
</html>
