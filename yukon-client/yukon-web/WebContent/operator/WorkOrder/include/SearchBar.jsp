<table width="100%" border="0" cellpadding = "5">
  <tr> 
    <td width="50%" valign = "top" align = "left">&nbsp;</td>
    <td align = "right" width="50%"><span class="TitleHeader">
	  <form method="POST" action="<%= request.getContextPath() %>/servlet/WorkOrderManager">
	    <input type="hidden" name="action" value="SearchWorkOrder">
		<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/WorkOrder/SearchResults.jsp">
        <select name="SearchBy">
          <%
	Integer lastSrvcOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_SERVICE_SEARCH_OPTION);
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY);
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
		String selectedStr = (lastSrvcOption != null && entry.getYukonDefID() == lastSrvcOption.intValue()) ? "selected" : "";
%>
          <option value="<%= entry.getYukonDefID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
          <%
	}
%>
        </select>
        <input type="text" name="SearchValue" size = "15">
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
