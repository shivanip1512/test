<table width="100%" border="0" cellpadding="3">
  <tr> 
    <td width="50%" valign = "top" align = "left"><span class="TitleHeader">Acct 
      #<%= account.getAccountNumber() %></span><br>
      <%
	StringBuffer phoneNo = new StringBuffer();
	if (primContact.getHomePhone().length() > 0)
		phoneNo.append(", ").append(primContact.getHomePhone()).append("(H)");
	if (primContact.getWorkPhone().length() > 0)
		phoneNo.append(", ").append(primContact.getWorkPhone()).append("(W)");
%>
      <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><%= phoneNo.toString() %> 
      </span><br>
      <span class="NavText"><%= ServletUtils.getOneLineAddress(propAddr) %></span><br>
    </td>
    <td align = "right" width="50%"><span class="TitleHeader"> 
	  <form method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
		<input type="hidden" name="action" value="SearchCustAccount">
        <select name="SearchBy">
          <%
	Integer lastOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_SEARCH_OPTION);
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
	  </form>
    </span></td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="3">
  <tr>
    <td align="center" class="TitleHeader"><%= header %></td>
  </tr>
</table>
