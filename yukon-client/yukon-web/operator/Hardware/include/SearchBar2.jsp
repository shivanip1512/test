<table width="100%" border="0" cellpadding = "5">
  <tr> 
    <td width="50%" valign = "top" align = "left">&nbsp;</td>
    <td align = "right" width="50%"><span class="TitleHeader">
	  <form method="POST" action="">
        <select name="SearchBy">
<%
{
	Integer lastInvOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_INVENTORY_SEARCH_OPTION);
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY);
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_METER_NO) continue;
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_DEVICE_NAME
			&& liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) == null) continue;
		String selectedStr = (lastInvOption != null && entry.getYukonDefID() == lastInvOption.intValue()) ? "selected" : "";
%>
          <option value="<%= entry.getYukonDefID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
	}
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
