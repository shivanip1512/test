<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.EnergyCompanyFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.YukonUserFuncs" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany" %>
<%@ page import="com.cannontech.stars.web.servlet.StarsAdmin" %>
<%
	LiteStarsEnergyCompany liteEC = SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/AdminImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Administration</td>
                  
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
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?LoginFailed=true">
                <table width="40%" border="0" cellspacing="0" cellpadding="5" class="MainText">
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
                    <td>
<%		if (memberLogin != null) { %>
					  <a href="" class="Link1" onclick="memberLogin(<%= memberLogin.getUserID() %>); return false;"><%= member.getName() %></a>
<%		} else { %>
					  <%= member.getName() %>
<%		} %>
					</td>
                  </tr>
<%
	}
%>
                </table>
              </form>
            </div>
			
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
