<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList" %>
<%@ page import="com.cannontech.common.constants.YukonListEntry" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany" %>
<%@ page import="com.cannontech.stars.web.servlet.ImportManager" %>
<%
	String listName = request.getParameter("List");
	boolean newList = request.getParameter("New") != null;
	
	Hashtable preprocessedData = (Hashtable) session.getAttribute(ImportManager.PREPROCESSED_DATA);
	TreeMap valueIDMap = (TreeMap) preprocessedData.get(listName);
	
	LiteStarsEnergyCompany ec = SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
	YukonSelectionList list = ec.getYukonSelectionList(listName);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function setEnabled(form, checked, idx) {
<% if (valueIDMap.size() > 1) { %>
	if (form.EntryText != null)
		form.EntryText[idx].disabled = !checked;
	else
		form.EntryID[idx].disabled = !checked;
<% } else { %>
	if (form.EntryText != null)
		form.EntryText.disabled = !checked;
	else
		form.EntryID.disabled = !checked;
<% } %>
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
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
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
			  <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportAccount2.jsp">
              <table width="500" border="1" cellspacing="0" cellpadding="3" align="center" class="MainText">
                <tr> 
                  <td width="5%" class="HeaderCell">&nbsp;</td>
                  <td width="45%" class="HeaderCell">Import Value</td>
                  <td width="50%" class="HeaderCell">Selection List Entry</td>
                </tr>
<%
	int idx = 0;
	Iterator it = valueIDMap.entrySet().iterator();
	while (it.hasNext()) {
		Map.Entry valueIDPair = (Map.Entry) it.next();
		String value = (String) valueIDPair.getKey();
		Integer id = (Integer) valueIDPair.getValue();
		boolean enabled = (id != null) && (value.length() > 0 || id.intValue() > 0);
%>
                <input type="hidden" name="ImportValue" value="<%= value %>">
                <tr> 
                  <td width="5%" class="TableCell">
                    <input type="checkbox" name="Enabled" value="<%= idx %>" <% if (enabled) out.write("checked"); %>
					  onclick="setEnabled(this.form, this.checked, <%= idx++ %>)">
                  </td>
                  <td width="45%" class="TableCell"><%= (value.length() > 0)? value : "(Empty Value)" %></td>
                  <td width="50%" class="TableCell"> 
<%
		if (newList) {
			String entryText = value;
			if (id != null) {
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
                    <input type="text" name="EntryText" value="<%= entryText %>" <% if (!enabled) out.write("disabled"); %>>
<%
		}
		else {
%>
                    <select name="EntryID" <% if (!enabled) out.write("disabled"); %>>
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
			else if (listName.equals("ApplianceType")) {
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
<%
		}	// if (newList)
%>
                  </td>
                </tr>
<%
	}
%>
              </table>
              <table width="300" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td width="50%" align="right"> 
                    <input type="submit" name="Submit" value="Save">
                  </td>
                  <td width="50%"> 
                    <input type="button" name="Cancel" value="Cancel" onclick="location.href='ImportAccount2.jsp'">
                  </td>
                </tr>
              </table>
            </form>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
