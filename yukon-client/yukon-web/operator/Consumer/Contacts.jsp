<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	ArrayList contactTypeList = YukonListFuncs.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_ID_CONTACT_TYPE).getYukonListEntries();
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
          <td  valign="top" width="101">
		  <% String pageName = "Contacts.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"><% String header = "ACCOUNT - CONTACTS"; %>
			<%@ include file="include/InfoSearchBar.jsp" %> 
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %></div>
              
			<form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			  <input type="hidden" name="action" value="UpdateContacts">
			  <input type="hidden" name="ContactID" value="<%= primContact.getContactID() %>">
              <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr>
                  <td><span class="SubtitleHeader">CONTACT (PRIMARY)</span>
                    <hr>
                    <table width="610" border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td>
                          <table width="300" border="0" cellspacing="0" cellpadding="2" align="center" class="TableCell">
                            <tr>
                              <td width="100" align="right">Last Name:</td>
                              <td width="192">
                                <input type="text" name="LastName" size="24" value="<%= primContact.getLastName() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                            <tr>
                              <td width="100" align="right">First Name:</td>
                              <td width="192">
                                <input type="text" name="FirstName" size="24" value="<%= primContact.getFirstName() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                          </table>
                        </td>
                        <td>
                          <table width="300" border="0" cellspacing="0" cellpadding="2" align="center" class="TableCell">
<%
	for (int i = 0; i < primContact.getContactNotificationCount(); i++) {
		ContactNotification contNotif = primContact.getContactNotification(i);
%>
                            <tr>
                              <td width="146" align="right">
                                <select name="NotifCat" onchange="setContentChanged(true)">
                                  <option value="0">(none)</option>
<%
		for (int j = 0; j < contactTypeList.size(); j++) {
			YukonListEntry entry = (YukonListEntry) contactTypeList.get(j);
			String selected = (entry.getEntryID() == contNotif.getNotifCatID()) ? "selected" : "";
%>
                                  <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getEntryText() %></option>
<%
		}
%>
                                </select>
                              </td>
                              <td width="146">
                                <input type="text" name="Notification" size="24" value="<%= contNotif.getNotification() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
<%
	}
	for (int i = primContact.getContactNotificationCount(); i < 4; i++) {
%>
                            <tr>
                              <td width="146" align="right">
                                <select name="NotifCat" onchange="setContentChanged(true)">
                                  <option value="0">(none)</option>
<%
		for (int j = 0; j < contactTypeList.size(); j++) {
			YukonListEntry entry = (YukonListEntry) contactTypeList.get(j);
%>
                                  <option value="<%= entry.getEntryID() %>"><%= entry.getEntryText() %></option>
<%
		}
%>
                                </select>
                              </td>
                              <td width="146">
                                <input type="text" name="Notification" size="24" onchange="setContentChanged(true)">
                              </td>
                            </tr>
<%
	}
%>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <table width="400" border="0" cellspacing="0" cellpadding="2" align="center">
                      <tr>
                        <td align="center">
                          <input type="submit" name="Command" value="Update">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </form> 
<%
	for (int i = 0; i < account.getAdditionalContactCount(); i++) {
		AdditionalContact contact = account.getAdditionalContact(i);
%>
			<form method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			  <input type="hidden" name="action" value="UpdateContacts">
			  <input type="hidden" name="ContactID" value="<%= contact.getContactID() %>">
              <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr>
                  <td><span class="SubtitleHeader">CONTACT</span>
				    <hr>
                    <table width="610" border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td>
                          <table width="300" border="0" cellspacing="0" cellpadding="2" align="center" class="TableCell">
                            <tr>
                              <td width="100" align="right">Last Name:</td>
                              <td width="192">
                                <input type="text" name="LastName" size="24" value="<%= contact.getLastName() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                            <tr>
                              <td width="100" align="right">First Name:</td>
                              <td width="192">
                                <input type="text" name="FirstName" size="24" value="<%= contact.getFirstName() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                          </table>
                        </td>
                        <td>
                          <table width="300" border="0" cellspacing="0" cellpadding="2" align="center" class="TableCell">
<%
		for (int j = 0; j < contact.getContactNotificationCount(); j++) {
			ContactNotification contNotif = contact.getContactNotification(j);
%>
                            <tr>
                              <td width="146" align="right">
                                <select name="NotifCat" onchange="setContentChanged(true)">
                                  <option value="0">(none)</option>
<%
			for (int k = 0; k < contactTypeList.size(); k++) {
				YukonListEntry entry = (YukonListEntry) contactTypeList.get(k);
				String selected = (entry.getEntryID() == contNotif.getNotifCatID()) ? "selected" : "";
%>
                                  <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getEntryText() %></option>
<%
			}
%>
                                </select>
                              </td>
                              <td width="146">
                                <input type="text" name="Notification" size="24" value="<%= contNotif.getNotification() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
<%
		}
		for (int j = contact.getContactNotificationCount(); j < 4; j++) {
%>
                            <tr>
                              <td width="146" align="right">
                                <select name="NotifCat" onchange="setContentChanged(true)">
                                  <option value="0">(none)</option>
<%
			for (int k = 0; k < contactTypeList.size(); k++) {
				YukonListEntry entry = (YukonListEntry) contactTypeList.get(k);
%>
                                  <option value="<%= entry.getEntryID() %>"><%= entry.getEntryText() %></option>
<%
			}
%>
                                </select>
                              </td>
                              <td width="146">
                                <input type="text" name="Notification" size="24" onchange="setContentChanged(true)">
                              </td>
                            </tr>
<%
		}
%>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <table width="400" border="0" cellspacing="0" cellpadding="2" align="center">
                      <tr> 
                        <td align="right"> 
                          <input type="submit" name="Command" value="Update">
                        </td>
                        <td> 
                          <input type="submit" name="Command" value="Delete" onclick="return confirm('Are you sure you want to delete this contact?')">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </form> 
<%
	}
%>
			<form method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			  <input type="hidden" name="action" value="UpdateContacts">
			  <input type="hidden" name="ContactID" value="-1">
              <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr>
                  <td><span class="SubtitleHeader">NEW CONTACT</span> 
                    <hr>
                    <table width="610" border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td>
                          <table width="300" border="0" cellspacing="0" cellpadding="2" align="center" class="TableCell">
                            <tr>
                              <td width="100" align="right">Last Name:</td>
                              <td width="192">
                                <input type="text" name="LastName" size="24" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                            <tr>
                              <td width="100" align="right">First Name:</td>
                              <td width="192">
                                <input type="text" name="FirstName" size="24" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                          </table>
                        </td>
                        <td>
                          <table width="300" border="0" cellspacing="0" cellpadding="2" align="center" class="TableCell">
<%
	for (int i = 0; i < 4; i++) {
%>
                            <tr> 
                              <td width="146" align="right"> 
                                <select name="NotifCat" onchange="setContentChanged(true)">
                                  <option value="0">(none)</option>
<%
		for (int j = 0; j < contactTypeList.size(); j++) {
			YukonListEntry entry = (YukonListEntry) contactTypeList.get(j);
%>
                                  <option value="<%= entry.getEntryID() %>"><%= entry.getEntryText() %></option>
<%
		}
%>
                                </select>
                              </td>
                              <td width="146"> 
                                <input type="text" name="Notification" size="24" onchange="setContentChanged(true)">
                              </td>
                            </tr>
<%
	}
%>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <table width="400" border="0" cellspacing="0" cellpadding="2" align="center">
                      <tr> 
                        <td align="center"> 
                          <input type="submit" name="Command" value="New">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </form> 
            <p>&nbsp;</p>
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
