<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.servlet.StarsAdmin" %>
<% if (!AuthFuncs.checkRoleProperty(lYukonUser, com.cannontech.roles.operator.AdministratorRole.ADMIN_CREATE_ENERGY_COMPANY)) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
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
                        <td width="15%" align="right" class="TableCell" valign="top">Step 
                          1:</td>
                        <td width="85%" class="TableCell"> <span class="ConfirmMsg">Enter 
                          the energy company name:</span> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                            <tr> 
                              <td width="20%" align="right">Company Name:</td>
                              <td width="80%"> 
                                <input type="text" name="CompanyName" size="30">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell" valign="top">Step 
                          2:</td>
                        <td width="85%" class="TableCell"><span class="ConfirmMsg">Create 
                          a default group for operators and a default group for 
                          residential customers in DBEditor. Enter the group names 
                          below: </span> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                            <tr> 
                              <td width="20%" align="right">Operator Group:</td>
                              <td width="80%"> 
                                <input type="text" name="OperatorGroup" size="30">
                              </td>
                            </tr>
                            <tr> 
                              <td width="20%" align="right">Customer Group:</td>
                              <td width="80%"> 
                                <input type="text" name="CustomerGroup" size="30">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell" valign="top">Step 
                          3:</td>
                        <td width="85%" class="TableCell"><span class="ConfirmMsg">Create 
                          a default operator login (A default login not only belongs 
                          to the operator group you just created, but also has 
                          the privilege to edit energy company configuration):</span> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                            <tr> 
                              <td width="20%" align="right">Username:</td>
                              <td width="80%"> 
                                <input type="text" name="Username" size="30">
                              </td>
                            </tr>
                            <tr> 
                              <td width="20%" align="right">Password:</td>
                              <td width="80%"> 
                                <input type="password" name="Password" size="30">
                              </td>
                            </tr>
                            <tr> 
                              <td width="20%" align="right">Confirm Password:</td>
                              <td width="80%"> 
                                <input type="password" name="PasswordC" size="30">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell" valign="top">Step 
                          4:</td>
                        <td width="85%" class="ConfirmMsg"><span class="ConfirmMsg">Create 
                          a second operator login (with only the privileges you 
                          defined in the operator group) (OPTIONAL):</span> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                            <tr> 
                              <td width="20%" align="right">Username:</td>
                              <td width="80%"> 
                                <input type="text" name="Username2" size="30">
                              </td>
                            </tr>
                            <tr> 
                              <td width="20%" align="right">Password:</td>
                              <td width="80%"> 
                                <input type="password" name="Password2" size="30">
                              </td>
                            </tr>
                            <tr> 
                              <td width="20%" align="right">Confirm Password:</td>
                              <td width="80%"> 
                                <input type="password" name="Password2C" size="30">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell" valign="top"> 
                          Step 5:</td>
                        <td width="85%" class="TableCell"> 
                          <p class="ConfirmMsg">After the energy company is created, 
                            you should:</p>
                          <ol>
                            <li>Open DBEditor, find the operator login you just 
                              created, edit properties of the EnergyCompany role 
                              under the &quot;Yukon&quot; category.</li>
                            <li>Assign a default route to the energy company (more 
                              details later).</li>
                            <li>Assign direct programs to the operator login.</li>
                            <li>Log off and re-login with the operator login you 
                              just created. Click the &quot;Config Energy Company&quot; 
                              button under &quot;Administration&quot;, and edit 
                              the energy company settings. The email address of 
                              the energy company must be set in order for password 
                              request to work.</li>
                          </ol>
                        </td>
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
                    <input type="button" name="Done" value="Done" onclick="location.href='AdminTest.jsp'">
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
