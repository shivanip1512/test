<table width="100%" border="0" cellpadding="3">
  <tr> 
    <td width="50%" valign = "top" align = "left"><span class="TitleHeader">Acct 
      #<%= account.getAccountNumber() %></span><br>
      <%
	StringBuffer contPhoneNo = new StringBuffer();
	ContactNotification contHomePhone = ServletUtils.getContactNotification(primContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
	if (contHomePhone != null)
		contPhoneNo.append(", ").append(contHomePhone.getNotification()).append("(H)");
	ContactNotification contWorkPhone = ServletUtils.getContactNotification(primContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
	if (contWorkPhone != null)
		contPhoneNo.append(", ").append(contWorkPhone.getNotification()).append("(W)");
%>
      <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><%= contPhoneNo.toString() %> 
      </span><br>
      <span class="NavText"><%= ServletUtils.getOneLineAddress(propAddr) %></span><br>
    </td>
    <td align = "right" width="50%"><span class="TitleHeader"> 
	  <form method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
		<input type="hidden" name="action" value="SearchCustAccount">
        <select name="SearchBy">
          <%
	Integer lastAcctOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_ACCOUNT_SEARCH_OPTION);
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE );
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
		String selectedStr = (lastAcctOption != null && entry.getEntryID() == lastAcctOption.intValue()) ? "selected" : "";
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
