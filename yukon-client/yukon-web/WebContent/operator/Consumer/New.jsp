<%@ include file="include/StarsHeader.jsp" %>
<%
	boolean inWizard = request.getParameter("Wizard") != null;
	StarsNewCustomerAccount newAccount = null;
	
	if (request.getParameter("Init") != null) {
		ServletUtils.removeTransientAttributes(session);
		session.removeAttribute(ServletUtils.ATT_NEW_ACCOUNT_WIZARD);
		session.removeAttribute(ServletUtils.ATT_NEW_CUSTOMER_ACCOUNT);
		session.removeAttribute(InventoryManagerUtil.STARS_INVENTORY_TEMP);
	}
	
	if (inWizard) {
		MultiAction actions = (MultiAction) session.getAttribute(ServletUtils.ATT_NEW_ACCOUNT_WIZARD);
		if (actions != null) {
			SOAPMessage reqMsg = actions.getRequestMessage( NewCustAccountAction.class );
			if (reqMsg != null) {
				StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation(reqMsg);
				newAccount = reqOper.getStarsNewCustomerAccount();
			}
		}
	}
	
	if (newAccount == null)
		newAccount = (StarsNewCustomerAccount) session.getAttribute(ServletUtils.ATT_NEW_CUSTOMER_ACCOUNT);
	
	if (newAccount != null)
		account = newAccount.getStarsCustomerAccount();
	else
		account = StarsFactory.newStarsCustomerAccount();
	
	StarsUser login = new StarsUser();
	if (newAccount != null && newAccount.getStarsUpdateLogin() != null) {
		login.setUsername( newAccount.getStarsUpdateLogin().getUsername() );
		login.setPassword( newAccount.getStarsUpdateLogin().getPassword() );
	}
	else {
		login.setUsername( "" );
		login.setPassword( "" );
	}
	
	AdditionalContact[] contacts = new AdditionalContact[3];
	for (int i = 0; i < 3; i++) {
		if (i < account.getAdditionalContactCount())
			contacts[i] = account.getAdditionalContact(i);
		else
			contacts[i] = (AdditionalContact) StarsFactory.newStarsCustomerContact(AdditionalContact.class);
	}
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function copyAddress(form) {
	if (form.CopyAddress.checked) {
		form.BAddr1.value = form.SAddr1.value;
		form.BAddr2.value = form.SAddr2.value;
		form.BCity.value = form.SCity.value;
		form.BState.value = form.SState.value;
		form.BZip.value = form.SZip.value;
		form.BAddr1.disabled = true;
		form.BAddr2.disabled = true;
		form.BCity.disabled = true;
		form.BState.disabled = true;
		form.BZip.disabled = true;
	}
	else {
		form.BAddr1.disabled = false;
		form.BAddr2.disabled = false;
		form.BCity.disabled = false;
		form.BState.disabled = false;
		form.BZip.disabled = false;
	}
}

function validate(form) {
	if (form.AcctNo.value == "") {
		alert("Account # cannot be empty!");
		return false;
	}
	if (form.Username != null && (form.Username.value != "" || form.Password.value != "")) {
		if (form.Username.value == "") {
			alert("Username cannot be empty!");
			return false;
		}
		if (form.Password.value == "") {
			alert("Password cannot be empty!");
			return false;
		}
		if (form.Password.value != form.Password2.value) {
			alert("Passwords don't match, please check the passwords.");
			return false;
		}
	}
	return true;
}

function clearPage() {
<% if (session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_NEW_CUSTOMER_ACCOUNT) != null) { %>
	for (var i = 0; i < document.form1.length; i++) {
		var e = document.form1.elements[i];
		if (e.type == "checkbox") e.checked = false;
		else if (e.type == "select") e.selectedIndex = 0;
		else if (e.type == "text" || e.type == "textarea") e.value = "";
	}
<% } else { %>
	document.form1.reset();
<% } %>
}

function confirmCancel() {
	if (confirm("Are you sure you want to quit from this wizard and discard all changes you've been made?"))
		location.href = "../Operations.jsp";
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jsp" %>
      <script language="JavaScript">setContentChanged(<%= account.getAccountNumber().length() > 0 %>);</script>
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
          <td valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" bgcolor="#FFFFFF" valign = "top" align = "center"> 
            <% String header = "NEW SIGNUP"; %>
            <%@ include file="include/InfoSearchBar2.jsp" %>
            <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			
            <form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient" onSubmit="return validate(this)">
              <input type="hidden" name="action" value="NewCustAccount">
			  <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/NewFinal.jsp">
			  <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
<% if (inWizard) { %>
			  <input type="hidden" name="REDIRECT2" value="<%= request.getContextPath() %>/operator/Consumer/CreateHardware.jsp?Wizard=true">
			  <input type="hidden" name="Wizard" value="true">
<% } %>
              <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td width="300" valign="top"><span class="SubtitleHeader">CUSTOMER CONTACT</span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="SubtitleHeader"> 
                          <div align="right">*Account #:</div>
                        </td>
                        <td width="210" valign="top"> 
                          <table width="191" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="95"> 
                                <input type="text" name="AcctNo" maxlength="40" size="14" value="<%= account.getAccountNumber() %>" onchange="setContentChanged(true)">
                              </td>
                              <td valign="top" class="TableCell" width="95" align="center">&nbsp;&nbsp;Commercial: 
                                <input type="checkbox" name="Commercial" value="true" <% if (account.getIsCommercial()) { %>checked<% } %> onclick="setContentChanged(true)">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
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
                          <div align="right">Last Name:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName" maxlength="30" size="24" value="<%= account.getPrimaryContact().getLastName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName" maxlength="30" size="24" value="<%= account.getPrimaryContact().getFirstName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home #:</div>
                        </td>
<%
	ContactNotification homePhone = ServletUtils.getContactNotification(account.getPrimaryContact(), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
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
	ContactNotification workPhone = ServletUtils.getContactNotification(account.getPrimaryContact(), YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
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
	ContactNotification email = ServletUtils.getContactNotification(account.getPrimaryContact(), YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
	String emailAddr = (email != null)? email.getNotification() : "";
%>
                        <td width="210"> 
                          <input type="text" name="Email" maxlength="50" size="24" value="<%= emailAddr %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
<cti:checkRole roleid="<%= com.cannontech.roles.operator.OddsForControlRole.ROLEID %>">
                      <tr> 
                        <td width="90" class="TableCell">&nbsp;</td>
                        <td width="210"> 
                          <input type="checkbox" name="NotifyControl" value="true" <% if (email != null && !email.getDisabled()) { %>checked<% } %> onclick="setContentChanged(true)">
                          <span class="TableCell">Notify <cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_ODDS_FOR_CONTROL %>" defaultvalue="Odds for Control"/></span></td>
                      </tr>
</cti:checkRole>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="210"> 
                          <textarea name="AcctNotes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= account.getAccountNotes() %></textarea>
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Last Name (2):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName2" maxlength="30" size="24" value="<%= contacts[0].getLastName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name (2):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName2" maxlength="30" size="24" value="<%= contacts[0].getFirstName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home # (2):</div>
                        </td>
<%
	ContactNotification homePhone2 = ServletUtils.getContactNotification(contacts[0], YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
	String homePhoneNo2 = (homePhone2 != null)? homePhone2.getNotification() : "";
%>
                        <td width="210"> 
                          <input type="text" name="HomePhone2" maxlength="14" size="14" value="<%= homePhoneNo2 %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Work # (2):</div>
                        </td>
<%
	ContactNotification workPhone2 = ServletUtils.getContactNotification(contacts[0], YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
	String workPhoneNo2 = (workPhone2 != null)? workPhone2.getNotification() : "";
%>
                        <td width="210"> 
                          <input type="text" name="WorkPhone2" maxlength="14" size="14" value="<%= workPhoneNo2 %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Last Name (3):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName3" maxlength="30" size="24" value="<%= contacts[1].getLastName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name (3):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName3" maxlength="30" size="24" value="<%= contacts[1].getFirstName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home # (3):</div>
                        </td>
<%
	ContactNotification homePhone3 = ServletUtils.getContactNotification(contacts[1], YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
	String homePhoneNo3 = (homePhone3 != null)? homePhone3.getNotification() : "";
%>
                        <td width="210"> 
                          <input type="text" name="HomePhone3" maxlength="14" size="14" value="<%= homePhoneNo3 %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Work # (3):</div>
                        </td>
<%
	ContactNotification workPhone3 = ServletUtils.getContactNotification(contacts[1], YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
	String workPhoneNo3 = (workPhone3 != null)? workPhone3.getNotification() : "";
%>
                        <td width="210"> 
                          <input type="text" name="WorkPhone3" maxlength="14" size="14" value="<%= workPhoneNo3 %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Last Name (4):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName4" maxlength="30" size="24" value="<%= contacts[2].getLastName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name (4):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName4" maxlength="30" size="24" value="<%= contacts[2].getFirstName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home # (4):</div>
                        </td>
<%
	ContactNotification homePhone4 = ServletUtils.getContactNotification(contacts[2], YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
	String homePhoneNo4 = (homePhone4 != null)? homePhone4.getNotification() : "";
%>
                        <td width="210"> 
                          <input type="text" name="HomePhone4" maxlength="14" size="14" value="<%= homePhoneNo4 %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Work # (4):</div>
                        </td>
<%
	ContactNotification workPhone4 = ServletUtils.getContactNotification(contacts[2], YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
	String workPhoneNo4 = (workPhone4 != null)? workPhone4.getNotification() : "";
%>
                        <td width="210"> 
                          <input type="text" name="WorkPhone4" maxlength="14" size="14" value="<%= workPhoneNo4 %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td width="300" valign="top"><span class="SubtitleHeader">SERVICE ADDRESS&nbsp;&nbsp;</span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr1" maxlength="40" size="24" value="<%= account.getStreetAddress().getStreetAddr1() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr2" maxlength="40" size="24" value="<%= account.getStreetAddress().getStreetAddr2() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">City:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SCity" maxlength="30" size="24" value="<%= account.getStreetAddress().getCity() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">County:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SCounty" maxlength="30" size="24" value="<%= account.getStreetAddress().getCounty() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">State:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SState" maxlength="2" size="14" value="<%= account.getStreetAddress().getState() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SZip" maxlength="12" size="14" value="<%= account.getStreetAddress().getZip() %>" onchange="setContentChanged(true)">
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
                          <div align="right">Notes:</div>
                        </td>
                        <td width="210"> 
                          <textarea name="PropNotes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><%= account.getPropertyNotes() %></textarea>
                        </td>
                      </tr>
                    </table>
                    <br>
                    <span class="SubtitleHeader">BILLING ADDRESS</span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell" height="2">&nbsp;</td>
                        <td width="210" height="2" class="TableCell"> 
                          <input type="checkbox" name="CopyAddress" value="true" onclick="copyAddress(this.form);setContentChanged(true);">
                          Same as above</td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr1" maxlength="40" size="24" value="<%= account.getBillingAddress().getStreetAddr1() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr2" maxlength="40" size="24" value="<%= account.getBillingAddress().getStreetAddr2() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">City:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BCity" maxlength="30" size="24" value="<%= account.getBillingAddress().getCity() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">State:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BState" maxlength="2" size="14" value="<%= account.getBillingAddress().getState() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell" height="2"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210" height="2"> 
                          <input type="text" name="BZip" maxlength="12" size="14" value="<%= account.getBillingAddress().getZip() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                    </table>
                    <br>
                    <span class="SubtitleHeader">SERVICE INFORMATION</span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Substation Name: </div>
                        </td>
                        <td width="210"> 
                          <select name="Substation" onchange="setContentChanged(true)">
                            <%
	for (int i = 0; i < substations.getStarsSubstationCount(); i++) {
		StarsSubstation substation = substations.getStarsSubstation(i);
		String selected = (account.getStarsSiteInformation().getSubstation().getEntryID() == substation.getSubstationID()) ? "selected" : "";
%>
                            <option value="<%= substation.getSubstationID() %>" <%= selected %>><%= substation.getSubstationName() %></option>
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
                          <input type="text" name="Feeder" maxlength="20" size="20" value="<%= account.getStarsSiteInformation().getFeeder() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Pole: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="Pole" maxlength="20" size="20" value="<%= account.getStarsSiteInformation().getPole() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Transformer Size: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="TranSize" maxlength="20" size="20" value="<%= account.getStarsSiteInformation().getTransformerSize() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Service Voltage: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="ServVolt" maxlength="20" size="20" value="<%= account.getStarsSiteInformation().getServiceVoltage() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                    </table>
                    <br>
                  </td>
                </tr>
              </table>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN %>">
<%
	com.cannontech.database.data.lite.LiteYukonGroup[] custGroups = liteEC.getResidentialCustomerGroups();
	if (custGroups != null && custGroups.length > 0) {
%>
              <table width="300" border="0" cellspacing="0" cellpadding="1">
                <tr> 
                  <td width="100" class="TableCell">
                    <div align="right">Customer Group: </div>
                  </td>
                  <td width="200">
                    <select name="CustomerGroup" onchange="setContentChanged(true)">
<%		for (int i = 0; i < custGroups.length; i++) { %>
                      <option value="<%= custGroups[i].getGroupID() %>"><%= custGroups[i].getGroupName() %></option>
<%		} %>
                    </select>
                  </td>
                </tr>
                <tr> 
                  <td width="100" class="TableCell"> 
                    <div align="right">User Name: </div>
                  </td>
                  <td width="200"> 
                    <input type="text" name="Username" maxlength="20" size="20" value="<%= login.getUsername() %>" onchange="setContentChanged(true)">
                  </td>
                </tr>
                <tr> 
                  <td width="100" class="TableCell"> 
                    <div align="right">Password:</div>
                  </td>
                  <td width="200"> 
                    <input type="password" name="Password" maxlength="20" size="20" onchange="setContentChanged(true)">
                  </td>
                </tr>
                <tr> 
                  <td width="100" class="TableCell"> 
                    <div align="right">Confirm Password:</div>
                  </td>
                  <td width="200"> 
                    <input type="password" name="Password2" maxlength="20" size="20">
                  </td>
                </tr>
              </table>
              <br>
<%
	}
%>
</cti:checkProperty>
<% if (inWizard) { %>
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td width="40%" align="right"> 
                    <input type="submit" name="Submit" value="Next">
                  </td>
                  <td width="20%" align="center"> 
                    <input type="submit" name="Submit" value="Done">
                  </td>
                  <td width="40%" align="left"> 
                    <input type="button" name="Cancel" value="Cancel" onclick="confirmCancel()">
                  </td>
                </tr>
              </table>
<% } else { %>
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td width="50%" align="right"> 
                    <input type="submit" name="Submit" value="Save">
                  </td>
                  <td width="50%" align="left"> 
                    <input type="button" name="Cancel" value="Cancel" onclick="if (warnUnsavedChanges()) location.href='../Operations.jsp'">
                  </td>
                </tr>
              </table>
<% } %>
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
