<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.servlet.StarsAdmin" %>
<%@ page import="com.cannontech.user.UserUtils" %>
<%
	int userID = -1;
	if (request.getParameter("UserID") != null)
		userID = Integer.parseInt(request.getParameter("UserID"));
	
	LiteYukonUser liteUser = null;
	if (userID != -1)
		liteUser = com.cannontech.database.cache.functions.YukonUserFuncs.getLiteYukonUser(userID);
	else
		liteUser = new LiteYukonUser(-1, "", "", UserUtils.STATUS_ENABLED);
	
	String checked = liteUser.getStatus().equalsIgnoreCase(UserUtils.STATUS_ENABLED)? "checked" : "";
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
		alert("Username cannot be empty!");
		return false;
	}
	if (form.Password.value == "") {
		alert("Password cannot be empty!");
		return false;
	}
	if (form.Password.value != form.Password2.value) {
		alert("The passwords you entered don't match, please enter them again");
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
              <span class="TitleHeader">ADMINISTRATION - OPERATOR LOGIN</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="return validate(this)">
			  <input type="hidden" name="action" value="UpdateOperatorLogin">
			  <input type="hidden" name="UserID" value="<%= userID %>">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Operator Login</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <%	if (userID == -1) { %>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Operator 
                          Group: </td>
                        <td width="75%" class="TableCell"> 
                          <select name="OperatorGroup">
<%
		com.cannontech.database.data.lite.LiteYukonGroup[] operGroups = liteEC.getWebClientOperatorGroups();
		for (int i = 0; i < operGroups.length; i++) {
%>
                            <option value="<%= operGroups[i].getGroupID() %>"><%= operGroups[i].getGroupName() %></option>
<%
		}
%>
                          </select>
                        </td>
                      </tr>
<%	} %>
                      <tr>
                        <td width="25%" align="right" class="TableCell">&nbsp;</td>
                        <td width="75%" class="TableCell">
                          <input type="checkbox" name="Status" value="true" <%= checked %>>
                          Login Enabled</td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Username:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="Username" value="<%= liteUser.getUsername() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Password:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="Password" name="Password">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Confirm 
                          Password :</td>
                        <td width="75%" class="TableCell"> 
                          <input type="Password" name="Password2" value="">
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
