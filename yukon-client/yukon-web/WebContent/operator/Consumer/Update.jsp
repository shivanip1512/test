<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.xml.serialize.StarsSelectionListEntry" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function setSameAsAbove(form) {
	var checked = form.CopyAddress.checked;
	form.BAddr1.disabled = checked;
	form.BAddr2.disabled = checked;
	form.BCity.disabled = checked;
	form.BState.disabled = checked;
	form.BZip.disabled = checked;
}

function copyAddress(form) {
	setSameAsAbove(form);
	if (form.CopyAddress.checked) {
		form.BAddr1.value = form.SAddr1.value;
		form.BAddr2.value = form.SAddr2.value;
		form.BCity.value = form.SCity.value;
		form.BState.value = form.SState.value;
		form.BZip.value = form.SZip.value;
	}
	else {
		form.BAddr1.value = "";
		form.BAddr2.value = "";
		form.BCity.value = "";
		form.BState.value = "";
		form.BZip.value = "";
		form.BAddr1.focus();
	}
}

function validate(form) {
	if (form.AcctNo.value == "") {
		alert("Account # cannot be empty!");
		return false;
	}
	return true;
}

function deleteAccount(form) {
	if (confirm("Are you sure you want to delete this account?")) {
<%
	if (programs.getStarsLMProgramCount() > 0 && inventories.getStarsInventoryCount() > 0) {
%>
		if (confirm('This customer has control devices attached. If you want to disable them, click "Ok", otherwise click "Cancel".'))
			form.DisableReceivers.value = "true";
<%
	}
%>
		form.action.value='DeleteCustAccount';
		form.submit();
	}
}

function init() {
<%
	if ((propAddr.getStreetAddr1().trim().length() > 0
		|| propAddr.getStreetAddr2().trim().length() > 0
		|| propAddr.getCity().trim().length() > 0
		|| propAddr.getState().trim().length() > 0
		|| propAddr.getZip().trim().length() > 0)
		&& propAddr.getStreetAddr1().equals(billAddr.getStreetAddr1())
		&& propAddr.getStreetAddr2().equals(billAddr.getStreetAddr2())
		&& propAddr.getCity().equals(billAddr.getCity())
		&& propAddr.getState().equals(billAddr.getState())
		&& propAddr.getZip().equals(billAddr.getZip()))
	{
%>
	document.form1.CopyAddress.checked = true;
	setSameAsAbove(document.form1);
<%
	}
%>
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
		  <% String pageName = "Update.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
            <% String header = "ACCOUNT - GENERAL"; %>
            <%@ include file="include/InfoSearchBar.jsp" %>
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			<% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			</div>
			
			<form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
			<input type="hidden" name="action" value="UpdateCustAccount">
			<input type="hidden" name="DisableReceivers" value="false">
            <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> <span class="SubtitleHeader">CUSTOMER CONTACT</span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="SubtitleHeader"> 
                          <div align="right">*Account #:</div>
                        </td>
                        <td width="210" valign="top"> 
                          <table width="200" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="113"> 
                                <input type="text" name="AcctNo" maxlength="40" size="14" value="<%= account.getAccountNumber() %>" onchange="setContentChanged(true)">
                              </td>
                              <td valign="top" class="TableCell" width="87"> Commercial: 
                                <input type="checkbox" name="Commercial" value="true" disabled onchange="setContentChanged(true)"
                                  <% if (account.getIsCommercial()) { %>checked<% } %>>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
<% if (account.getIsCommercial()) { %>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Company: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="Company" maxlength="30" size="24" value="<%= account.getCompany() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Customer #: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="Customer #" maxlength="30" size="24" value="<%= account.getCustomerNumber().compareTo("(none)") != 0 ? account.getCustomerNumber() : "" %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
<% } %>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Last Name:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home #:</div>
                        </td>
<%
	ContactNotification homePhone = ServletUtils.getContactNotification(primContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
	String homePhoneNo = (homePhone != null)? homePhone.getNotification() : "";
%>
                        <td width="210"> 
                          <input type="text" name="HomePhone" maxlength="14" size="14" value="<%= homePhoneNo %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Work #:</div>
                        </td>
<%
	ContactNotification workPhone = ServletUtils.getContactNotification(primContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
	String workPhoneNo = (workPhone != null)? workPhone.getNotification() : "";
%>
                        <td width="210"> 
                          <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= workPhoneNo %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">e-mail Address:</div>
                        </td>
<%
	ContactNotification email = ServletUtils.getContactNotification(primContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
	String emailAddr = (email != null)? email.getNotification() : "";
%>
                        <td width="210"> 
                          <input type="text" name="Email" maxlength="50" size="24" value="<%= emailAddr %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
<cti:checkRole roleid="<%= com.cannontech.roles.operator.OddsForControlRole.ROLEID %>">
					  <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right"></div>
                        </td>
                        <td width="210"> 
                          <input type="checkbox" name="NotifyControl" value="true" <% if (email != null && !email.getDisabled()) { %>checked<% } %> onchange="setContentChanged(true)">
                          <span class="TableCell">Notify <cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_ODDS_FOR_CONTROL %>" defaultvalue="odds for control"/></span></td>
                      </tr>
</cti:checkRole>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="210"> 
                          <textarea name="AcctNotes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= account.getAccountNotes().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                        </td>
                      </tr>
                    </table>
                    <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td><br>
                          <span class="SubtitleHeader">SERVICE INFORMATION</span> 
                          <hr>
                          <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Rate Schedule: </div>
                              </td>
                              <td width="210"> 
								<select name="RateSchedule" onchange="setContentChanged(true)">
<%	StarsCustSelectionList rateSchedList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_RATE_SCHEDULE );%>
	<option value="<%= 0 %>" <%= "(none)" %>><%= "(none)" %></option>
<%	for (int j = 0; j < rateSchedList.getStarsSelectionListEntryCount(); j++) {
		StarsSelectionListEntry entry = rateSchedList.getStarsSelectionListEntry(j);
		
		String selected = (entry.getEntryID() == account.getRateScheduleID())? "selected" : "";
%>
		<option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
<%
	}
%>
								</select>
                              </td>
                            </tr>
                            
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Substation Name: </div>
                              </td>
                              <td width="210"> 
								<select name="Substation" onchange="setContentChanged(true)">
<%
	for (int i = 0; i < substations.getStarsSubstationCount(); i++) {
		StarsSubstation substation = substations.getStarsSubstation(i);
		String selectedStr = (substation.getSubstationID() == siteInfo.getSubstation().getEntryID()) ? "selected" : "";
%>
								  <option value="<%= substation.getSubstationID() %>" <%= selectedStr %>><%= substation.getSubstationName() %></option>
<%
	}
%>
								</select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Feeder: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Feeder" maxlength="20" size="24" value="<%= siteInfo.getFeeder() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Pole: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Pole" maxlength="20" size="24" value="<%= siteInfo.getPole() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Transformer Size: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="TranSize" maxlength="20" size="24" value="<%= siteInfo.getTransformerSize() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Service Voltage: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="ServVolt" maxlength="20" size="24" value="<%= siteInfo.getServiceVoltage() %>" onchange="setContentChanged(true)">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td width="300" valign="top" bgcolor="#FFFFFF"><span class="SubtitleHeader">SERVICE ADDRESS</span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr1" maxlength="40" size="24" value="<%= propAddr.getStreetAddr1() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr2" maxlength="40" size="24" value="<%= propAddr.getStreetAddr2() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">City:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SCity" maxlength="30" size="24" value="<%= propAddr.getCity() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
					  
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">State:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SState" maxlength="2" size="14" value="<%= propAddr.getState() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SZip" maxlength="12" size="14" value="<%= propAddr.getZip() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Map #:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="PropNo" maxlength="12" size="14" value="<%= account.getPropertyNumber() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
					  <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">County:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SCounty" maxlength="30" size="24" value="<%= propAddr.getCounty() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="210"> 
                          <textarea name="PropNotes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= account.getPropertyNotes().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                        </td>
                      </tr>
                    </table>
                    <br>
                    <span class="SubtitleHeader">BILLING ADDRESS</span>
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right"> </div>
                        </td>
                        <td width="210" class="TableCell"> 
                          <input type="checkbox" name="CopyAddress" value="true" onClick="copyAddress(this.form)" onchange="setContentChanged(true)">
                          Same as above</td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr1" maxlength="40" size="24" value="<%= billAddr.getStreetAddr1() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr2" maxlength="40" size="24" value="<%= billAddr.getStreetAddr2() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">City:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BCity" maxlength="30" size="24" value="<%= billAddr.getCity() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">State:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BState" maxlength="2" size="14" value="<%= billAddr.getState() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BZip" maxlength="12" size="14" value="<%= billAddr.getZip() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                    </table>
                    
                  </td>
              </tr>
            </table>
            <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr> 
                  <td width="42%"> 
                    <div align="right"> 
                      <input type="submit" name="Save" value="Save">
                    </div>
                  </td>
                  <td width="15%"> 
                    <div align="center"> 
                      <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
                    </div>
                  </td>
                  <td width="43%">
                    <div align="left"> 
                      <input type="button" name="Delete" value="Delete" onclick="deleteAccount(this.form)">
                    </div>
				  </td>
                </tr>
            </table>
			</form>
            <p align="center">&nbsp;</p>
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
