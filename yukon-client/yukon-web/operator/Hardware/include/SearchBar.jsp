<table width="100%" border="0" cellpadding = "5">
  <tr> 
    <td width="50%" valign = "top" align = "left">&nbsp;</td>
    <td align = "right" width="50%"><span class="TitleHeader">
	  <form method="POST" action="<%= request.getContextPath() %>/servlet/InventoryManager">
	    <input type="hidden" name="action" value="SearchInventory">
		<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Hardware/ResultSet.jsp">
        <select name="SearchBy">
          <%
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY);
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
%>
          <option value="<%= entry.getYukonDefID() %>"><%= entry.getContent() %></option>
          <%
	}
%>
        </select>
        <input type="text" name="SearchValue" size = "14">
        <input type="submit" name="Submit" value="Search">
	  </form>
    </span></td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="3">
  <tr>
    <td align="center" class="TitleHeader"><%= header %></td>
  </tr>
</table>
