<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFFF" text="#000000">
<form method="POST" action="<%= request.getContextPath() %>/servlet/WorkOrderManager">
<input type="hidden" name="action" value="SearchWorkOrder">
<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/WorkOrder/SearchResults.jsp">
  <table width="100%" border="0" cellpadding = "5">
    <tr> 
      <td width="50%" valign = "top" align = "left">&nbsp;</td>
      <td align = "right" width="50%"><span class="TitleHeader"> 
        <select name="SearchBy">
          <%
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY);
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
%>
          <option value="<%= entry.getYukonDefID() %>"><%= entry.getContent() %></option>
          <%
	}
%>
        </select>
        <input type="text" name="SearchValue" size = "15">
        <input type="submit" name="Submit" value="Search">
        </span></td>
    </tr>
  </table>
  <table width="100%" border="0" cellspacing="0" cellpadding="3">
    <tr>
      <td align="center" class="TitleHeader"><%= header %></td>
    </tr>
  </table>
</form>
</body>
</html>
