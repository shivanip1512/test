<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	StarsUser login = userLogin;
	if (login == null) {
		login = new StarsUser();
		login.setUsername("");
		login.setPassword("");
		login.setStatus(StarsLoginStatus.ENABLED);
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function validate(form) {
	if (form.Username.value == "" && form.Password.value != "") {
		alert("Username cannot be empty");
		return false;
	}
	if (form.Password.value == "" && form.Username.value != "" && form.Status.checked) {
		alert("Password cannot be empty");
		return false;
	}
	if (form.Password.value != form.Password2.value) {
		alert("The passwords you entered don't match, please enter them again");
		return false;
	}
}

function generatePassword(form) {
	// Generate a random password w/ the length of 6, consists of letters and digits
	var pwChars = new Array(6);
	for (i = 0; i < 6; i++) {
		var rand = parseInt(Math.random() * 62, 10);
		if (rand < 10)
			pwChars[i] = 48 + rand;	// 48 is ascii for '0'
		else if (rand < 36)
			pwChars[i] = 65 + rand - 10;	// 65 is ascii for 'A'
		else
			pwChars[i] = 97 + rand - 36;	// 97 is ascii for 'a'
	}
	var passwd = String.fromCharCode(pwChars[0], pwChars[1], pwChars[2], pwChars[3], pwChars[4], pwChars[5]);
	if (confirm('The new password is "' + passwd + '". Would you like to save it?')) {
		form.Password.value = passwd;
		form.Password2.value = passwd;
		form.submit();
	}
}

function deleteLogin(form) {
	if (!confirm("Are you sure you want to delete the user login?"))
		return;
	form.Username.value = "";
	form.Password.value = "";
	form.Password2.value = "";
	form.submit();
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
          <td  valign="top" width="101">
		  <% String pageName = "Password.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_CHANGE_LOGIN, "ADMINISTRATION - CHANGE LOGIN"); %>
              <%@ include file="include/InfoSearchBar.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			
            <form method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
			  <input type="hidden" name="action" value="UpdateLogin">
			  <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Password.jsp">
			  <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/Password.jsp">
                <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Customer Group: </div>
                    </td>
                    <td width="200"> 
                      <select name="CustomerGroup">
<%
	if (login.getUsername().length() > 0) {
		String groupID = login.hasGroupID()? String.valueOf(login.getGroupID()) : "";
		String groupName = login.hasGroupID()? AuthFuncs.getGroup(login.getGroupID()).getGroupName() : "(none)";
%>
                        <option value="<%= groupID %>"><%= groupName %></option>
<%
	}
	else {
		com.cannontech.database.data.lite.LiteYukonGroup[] custGroups = liteEC.getResidentialCustomerGroups();
		for (int i = 0; i < custGroups.length; i++) {
%>
                        <option value="<%= custGroups[i].getGroupID() %>"><%= custGroups[i].getGroupName() %></option>
<%
		}
	}
%>
                      </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right"></div>
                    </td>
                    <td width="200" class="TableCell"> 
                      <input type="checkbox" name="Status" value="<%= StarsLoginStatus.ENABLED.toString() %>" <% if (login.getStatus().getType() == StarsLoginStatus.ENABLED_TYPE) { %>checked<% } %>>
                      Login Enabled </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">New User Name: </div>
                    </td>
                    <td width="200"> 
                      <input type="text" name="Username" maxlength="20" size="20" value="<%= login.getUsername() %>">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">New Password:</div>
                    </td>
                    <td width="200"> 
                      <input type="password" name="Password" maxlength="20" size="20">
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
                  <tr> 
                    <td width="100" class="TableCell">&nbsp;</td>
                    <td width="200"> 
                      <input type="button" name="GenPasswd" value="Generate Password" onclick="generatePassword(this.form)">
                    </td>
                  </tr>
                </table>
              <br>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="40%" align="right"> 
                      <input type="submit" name="Submit" value="Save">
                    </td>
                    <td width="20%" align="center"> 
                      <input type="reset" name="Reset" value="Reset">
                    </td>
                    <td width="40%">
                      <input type="button" name="Delete" value="Delete" onclick="deleteLogin(this.form)" <% if (userLogin == null) { %>disabled<% } %>>
                    </td>
                  </tr>
                </table>
			</form>
              <p>&nbsp;</p>
              </div>
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
