<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.util.ImportManagerUtil" %>
<%
	String listName = request.getParameter("List");
	YukonSelectionList list = liteEC.getYukonSelectionList(listName);
	
	Hashtable preprocessedData = (Hashtable) session.getAttribute(ImportManagerUtil.PREPROCESSED_DATA);
	TreeMap valueIDMap = (TreeMap) preprocessedData.get(listName);
	
	boolean updateAvail = (list != null) && list.getUserUpdateAvailable().equalsIgnoreCase("Y")
			|| listName.equals("ServiceCompany") || listName.equals("Substation");
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function setEnabled(form, checked, idx) {
	var radioBtn = eval("form.ListEntry" + idx);
	if (radioBtn != null) {
		radioBtn[0].disabled = !checked;
		radioBtn[1].disabled = !checked;
	}
	
	if (checked && radioBtn != null) {
		var value = radioBtn[0].checked ? radioBtn[0].value : radioBtn[1].value;
		setListEntry(form, value, idx);
	}
	else {
<% if (valueIDMap.size() > 1) { %>
		form.EntryID[idx].disabled = !checked;
		if (form.EntryText != null)
			form.EntryText[idx].disabled = !checked;
<% } else { %>
		form.EntryID.disabled = !checked;
		if (form.EntryText != null)
			form.EntryText.disabled = !checked;
<% } %>
	}
}

function setListEntry(form, value, idx) {
	var isNew = (value == "New");
<% if (valueIDMap.size() > 1) { %>
	form.EntryID[idx].disabled = isNew;
	form.EntryText[idx].disabled = !isNew;
	if (isNew) form.EntryText[idx].focus();
<% } else { %>
	form.EntryID.disabled = isNew;
	form.EntryText.disabled = !isNew;
	if (isNew) form.EntryText.focus();
<% } %>
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jsp" %>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "IMPORT ACCOUNTS - ASSIGN SELECTION LIST"; %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td class="MainText"> 
                    <div align="center">Select or enter a new selection list entry 
                      for each value appeared in the import file.<br>
                      Uncheck the box before the import value if you don't want 
                      to assign any entry to it.</div>
                  </td>
                </tr>
              </table>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager">
			  <input type="hidden" name="action" value="AssignSelectionList">
			  <input type="hidden" name="ListName" value="<%= listName %>">
			  <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportSTARS2.jsp">
              <table width="500" border="1" cellspacing="0" cellpadding="3" align="center" class="MainText">
                <tr> 
                  <td width="5%" class="HeaderCell">&nbsp;</td>
                  <td width="45%" class="HeaderCell">Import Value</td>
                  <td width="50%" class="HeaderCell">Selection List Entry</td>
<% if (updateAvail) { %>
                  <td width="50%" class="HeaderCell">New Entry</td>
<% } %>
                </tr>
<%
	int idx = 0;
	Iterator it = valueIDMap.keySet().iterator();
	while (it.hasNext()) {
		String value = (String) it.next();
		Integer id = (Integer) valueIDMap.get(value);
		boolean enabled = (id != null) && (value.length() > 0 || id.intValue() > 0);
%>
                <input type="hidden" name="ImportValue" value="<%= value %>">
                <tr> 
                  <td width="5%" class="TableCell"> 
                    <input type="checkbox" name="Enabled" value="<%= idx %>" <% if (enabled) { %>checked<% } %>
					  onclick="setEnabled(this.form, this.checked, <%= idx %>)">
                  </td>
                  <td width="45%" class="TableCell"><%= (value.length() > 0)? value : "(Empty Value)" %></td>
                  <td width="50%" class="TableCell" nowrap> 
<% if (updateAvail) { %>
                    <input type="radio" name="ListEntry<%= idx %>" value="" checked <% if (!enabled) { %>disabled<% } %>
					  onclick="setListEntry(this.form, this.value, <%= idx %>)">
<% } %>
                    <select name="EntryID" <% if (!enabled) { %>disabled<% } %>>
<%
		if (list != null) {		
			for (int j = 0; j < list.getYukonListEntries().size(); j++) {
				YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(j);
				String selected = (id != null && entry.getEntryID() == id.intValue())? "selected" : "";
%>
                      <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getEntryText() %></option>
<%
			}
		}
		else if (listName.equals("LoadType")) {
			for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
				StarsApplianceCategory category = categories.getStarsApplianceCategory(j);
				String selected = (id != null && category.getApplianceCategoryID() == id.intValue())? "selected" : "";
%>
                      <option value="<%= category.getApplianceCategoryID() %>" <%= selected %>><%= category.getDescription() %></option>
<%
			}
		}
		else if (listName.equals("ServiceCompany")) {
			for (int j = 0; j < companies.getStarsServiceCompanyCount(); j++) {
				StarsServiceCompany company = companies.getStarsServiceCompany(j);
				String selected = (id != null && company.getCompanyID() == id.intValue())? "selected" : "";
%>
                      <option value="<%= company.getCompanyID() %>" <%= selected %>><%= company.getCompanyName() %></option>
<%
			}
		}
		else if (listName.equals("Substation")) {
			for (int j = 0; j < substations.getStarsSubstationCount(); j++) {
				StarsSubstation sub = substations.getStarsSubstation(j);
				String selected = (id != null && sub.getSubstationID() == id.intValue())? "selected" : "";
%>
                      <option value="<%= sub.getSubstationID() %>" <%= selected %>><%= sub.getSubstationName() %></option>
<%
			}
		}
		else if (listName.equals("LoadGroup")) {
			for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
				StarsApplianceCategory category = categories.getStarsApplianceCategory(j);
				for (int k = 0; k < category.getStarsEnrLMProgramCount(); k++) {
					StarsEnrLMProgram program = category.getStarsEnrLMProgram(k);
					for (int l = 0; l < program.getAddressingGroupCount(); l++) {
						AddressingGroup group = program.getAddressingGroup(l);
						String selected = (id != null && group.getEntryID() == id.intValue())? "selected" : "";
%>
                      <option value="<%= group.getEntryID() %>" <%= selected %>><%= group.getContent() %></option>
<%
					}
				}
			}
		}
		else {
%>
                      <option value="0">(none)</option>
<%
		}
%>
                    </select>
                  </td>
<% if (updateAvail) { %>
                  <td width="50%" class="TableCell" nowrap> 
                    <input type="radio" name="ListEntry<%= idx %>" value="New" <% if (!enabled) { %>disabled<% } %>
					  onclick="setListEntry(this.form, this.value, <%= idx %>)">
<%
		String entryText = value;
		if (id != null && id.intValue() > 0) {
			if (list != null) {
				for (int j = 0; j < list.getYukonListEntries().size(); j++) {
					YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(j);
					if (entry.getEntryID() == id.intValue()) {
						entryText = entry.getEntryText();
						break;
					}
				}
			}
			else if (listName.equals("ServiceCompany")) {
				for (int j = 0; j < companies.getStarsServiceCompanyCount(); j++) {
					StarsServiceCompany company = companies.getStarsServiceCompany(j);
					if (company.getCompanyID() == id.intValue()) {
						entryText = company.getCompanyName();
						break;
					}
				}
			}
		}
%>
                    <input type="text" name="EntryText" value="<%= entryText %>" size="14" disabled>
                  </td>
<% } %>
                </tr>
<%
		idx++;
	}
%>
              </table>
              <table width="300" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td width="50%" align="right"> 
                    <input type="submit" name="Submit" value="Save">
                  </td>
                  <td width="50%"> 
                    <input type="button" name="Cancel" value="Cancel" onclick="location.href='ImportSTARS2.jsp'">
                  </td>
                </tr>
              </table>
            </form>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
