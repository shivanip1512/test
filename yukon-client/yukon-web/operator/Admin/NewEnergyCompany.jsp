<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonGroup" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.stars.web.servlet.StarsAdmin" %>
<%
	if (!AuthFuncs.checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_CREATE_ENERGY_COMPANY)) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	ArrayList yukonGroups = null;
	synchronized (cache) {
		List groups = cache.getAllYukonGroups();
		Collections.sort(groups, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
		yukonGroups = new ArrayList(groups);
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
	if (form.Username.value == "") {
		alert("Username of default operator login cannot be empty");
		return false;
	}
	if (form.Password.value != form.PasswordC.value) {
		alert("Passwords of default operator login don't match, please enter them again");
		return false;
	}
	if (form.Password2.value != form.Password2C.value) {
		alert("Passwords of second operator login don't match, please enter them again");
		return false;
	}
	return true;
}

function appendString(str1, str2) {
	var i = str1.length - 1;
	while (i >= 0) {
		if (str1.charAt(i) != ' ' && str1.charAt(i) != '\t') break;
		i--;
	}
	
	if (i < 0)
		str1 = str2;
	else if (str1.charAt(i) == ',')
		str1 = str1.substr(0, i+1) + str2;
	else
		str1 = str1.substr(0, i+1) + "," + str2;
	
	return str1;
}

function addOperatorGroup(form) {
	if (form.OperGroupList.value == "") return;
	form.OperatorGroup.value = appendString(form.OperatorGroup.value, form.OperGroupList.value);
}

function addCustomerGroup(form) {
	if (form.CustGroupList.value == "") return;
	form.CustomerGroup.value = appendString(form.CustomerGroup.value, form.CustGroupList.value);
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
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - CREATE ENERGY COMPANY</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="return validate(this)">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Create Energy Company</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <input type="hidden" name="action" value="NewEnergyCompany">
                      <tr> 
                        <td width="10%" align="right" class="TableCell" valign="top">Step 
                          1:</td>
                        <td width="90%" class="TableCell"> <span class="ConfirmMsg">Enter 
                          the energy company information:</span> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                            <tr> 
                              <td width="25%" align="right">Company Name:</td>
                              <td width="75%"> 
                                <input type="text" name="CompanyName" size="30">
                                <span class="ErrorMsg">*</span> </td>
                            </tr>
<% if (!ECUtils.isDefaultEnergyCompany(liteEC)) { %>
                            <tr>
                              <td width="25%" align="right">&nbsp;</td>
                              <td width="75%">
                                <input type="checkbox" name="AddMember" value="true">
                                Add as a member of the current energy company</td>
                            </tr>
<% } %>
                            <tr> 
                              <td width="25%" align="right">Email:</td>
                              <td width="75%"> 
                                <input type="text" name="Email" size="30">
                                <font color="#FF0000">(Required for password request)</font> 
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="10%" align="right" class="TableCell" valign="top">Step 
                          2:</td>
                        <td width="90%" class="TableCell"><span class="ConfirmMsg">Create 
                          one or more login groups for operators and residential 
                          customers (optional) in DBEditor. Enter the group names 
                          below, separated by comma (or select from the list of 
                          all login groups):</span> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                            <tr> 
                              <td width="25%" align="right">Operator Groups:</td>
                              <td width="75%"> 
                                <input type="text" name="OperatorGroup" size="30">
                                <span class="ErrorMsg">*</span></td>
                            </tr>
                            <tr> 
                              <td width="25%" align="right">&nbsp;</td>
                              <td width="75%"> 
                                <select name="OperGroupList">
                                  <%
	for (int i = 0; i < yukonGroups.size(); i++) {
		LiteYukonGroup group = (LiteYukonGroup) yukonGroups.get(i);
%>
                                  <option value="<%= group.getGroupName() %>"><%= group.getGroupName() %></option>
                                  <%
	}
%>
                                </select>
                                <input type="button" name="AddOperGrp" value="Add" onClick="addOperatorGroup(this.form)">
                              </td>
                            </tr>
                            <tr> 
                              <td width="25%" align="right">Res. Customer Groups:</td>
                              <td width="75%"> 
                                <input type="text" name="CustomerGroup" size="30">
                              </td>
                            </tr>
                            <tr> 
                              <td width="25%" align="right">&nbsp;</td>
                              <td width="75%"> 
                                <select name="CustGroupList">
                                  <%
	for (int i = 0; i < yukonGroups.size(); i++) {
		LiteYukonGroup group = (LiteYukonGroup) yukonGroups.get(i);
%>
                                  <option value="<%= group.getGroupName() %>"><%= group.getGroupName() %></option>
                                  <%
	}
%>
                                </select>
                                <input type="button" name="AddCustGrp" value="Add" onClick="addCustomerGroup(this.form)">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="10%" align="right" class="TableCell" valign="top">Step 
                          3:</td>
                        <td width="90%" class="TableCell"><span class="ConfirmMsg">Create 
                          a default operator login (A default login not only belongs 
                          to the first operator group you just created, but also 
                          has the privilege to edit energy company configuration):</span> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                            <tr> 
                              <td width="25%" align="right">Username:</td>
                              <td width="75%"> 
                                <input type="text" name="Username" size="30">
                                <span class="ErrorMsg">*</span> </td>
                            </tr>
                            <tr> 
                              <td width="25%" align="right">Password:</td>
                              <td width="75%"> 
                                <input type="password" name="Password" size="30">
                                <span class="ErrorMsg">*</span> </td>
                            </tr>
                            <tr> 
                              <td width="25%" align="right">Confirm Password:</td>
                              <td width="75%"> 
                                <input type="password" name="PasswordC" size="30">
                                <span class="ErrorMsg">*</span> </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="10%" align="right" class="TableCell" valign="top">Step 
                          4:</td>
                        <td width="90%" class="ConfirmMsg"><span class="ConfirmMsg">Create 
                          a second operator login (with only the privileges defined 
                          in the first operator group you just created):</span> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                            <tr> 
                              <td width="25%" align="right">Username:</td>
                              <td width="75%"> 
                                <input type="text" name="Username2" size="30">
                              </td>
                            </tr>
                            <tr> 
                              <td width="25%" align="right">Password:</td>
                              <td width="75%"> 
                                <input type="password" name="Password2" size="30">
                              </td>
                            </tr>
                            <tr> 
                              <td width="25%" align="right">Confirm Password:</td>
                              <td width="75%"> 
                                <input type="password" name="Password2C" size="30">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr>
                        <td width="10%" align="right" class="TableCell" valign="top">Step 
                          5:</td>
                        <td width="90%" class="ConfirmMsg">Select a default route 
                          for the energy company (If this doesn't apply to you, 
                          leave it &quot;(none)&quot;): 
                          <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                            <tr> 
                              <td width="25%" align="right">Default Route:</td>
                              <td width="75%"> 
                                <select name="Route">
                                  <option value="<%= LiteStarsEnergyCompany.INVALID_ROUTE_ID %>">(none)</option>
<%
	LiteYukonPAObject[] routes = PAOFuncs.getAllLiteRoutes();
	for (int i = 0; i < routes.length; i++) {
%>
                                  <option value="<%= routes[i].getYukonID() %>"><%= routes[i].getPaoName() %></option>
<%
	}
%>
                                </select>
                                <span class="ErrorMsg">*</span> 
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="10%" align="right" class="TableCell" valign="top"> 
                          Step 6:</td>
                        <td width="90%" class="ConfirmMsg">After the energy company 
                          is created, login as the default operator created above, 
                          and edit the energy company settings by clicking the 
                          &quot;Config Energy Company&quot; button on the home 
                          page.</td>
                      </tr>
                      <tr> 
                        <td width="10%" align="right" class="TableCell" valign="top">&nbsp;</td>
                        <td width="90%" class="MainText"><span class="ErrorMsg">* 
                          Required fields</span></td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                  <td width="290" align="right"> 
                    <input type="submit" name="Submit" value="Submit">
                  </td>
                  <td width="205"> 
                    <input type="reset" name="Reset" value="Reset">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Back" value="Back" onclick="location.href='../Operations.jsp'">
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
