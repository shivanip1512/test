<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.EnergyCompanyFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.YukonUserFuncs" %>
<%@ page import="com.cannontech.stars.web.servlet.StarsAdmin" %>
<%
	if (request.getParameter("failed") != null)
		errorMsg = "Failed to log into member energy company";
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function memberLogin(userID) {
	document.form1.UserID.value = userID;
	document.form1.submit();
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
              <span class="TitleHeader">ADMINISTRATION - MEMBER MANAGEMENT</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <br>
              <table width="80%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td align="center" class="MainText">Click on a member's name 
                    to log into the member energy company (If a member's name 
                    is not clickable, you haven't assigned a login to the member 
                    yet).</td>
                </tr>
              </table>
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/StarsAdmin">
			    <input type="hidden" name="action" value="MemberLogin">
				<input type="hidden" name="UserID" value="-1">
                <table width="50%" border="0" cellspacing="0" cellpadding="5" class="MainText">
                  <%
	for (int i = 0; i < liteEC.getChildren().size(); i++) {
		LiteStarsEnergyCompany member = (LiteStarsEnergyCompany) liteEC.getChildren().get(i);
		LiteYukonUser memberLogin = null;
		for (int j = 0; j < liteEC.getMemberLoginIDs().size(); j++) {
			LiteYukonUser login = YukonUserFuncs.getLiteYukonUser(((Integer)liteEC.getMemberLoginIDs().get(j)).intValue());
			if (EnergyCompanyFuncs.getEnergyCompany(login).getEnergyCompanyID() == member.getLiteID()) {
				memberLogin = login;
				break;
			}
		}
%>
                  <tr> 
                    <td align="center"> 
                      <% if (memberLogin != null) { %>
                      <a href="" class="Link1" onclick="memberLogin(<%= memberLogin.getUserID() %>); return false;"><%= member.getName() %></a> 
                      <% } else { %>
                      <%= member.getName() %> 
                      <% } %>
                    </td>
                  </tr>
<%
	}
%>
                </table>
              </form>
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
