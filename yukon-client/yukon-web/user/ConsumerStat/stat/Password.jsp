<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
var passwdChanged = false;

function setPasswordChanged() {
	passwdChanged = true;
}

function checkPasswords(form) {
	if (passwdChanged && (form.Password.value != form.Password2.value)) {
		alert("The passwords you entered doesn't match, please reenter them");
		return false;
	}
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../../WebConfig/<cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_IMG_CORNER %>"/>">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO %>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height="28">&nbsp;</td>
				  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">&nbsp;</td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
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
          <td  valign="top" width="101">
		  <% String pageName = "Password.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = "ADMINISTRATION - CHANGE LOGIN"; %>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			
            <form method="POST" action="<%=request.getContextPath()%>/servlet/SOAPClient" onsubmit="return checkPasswords(this)">
			  <input type="hidden" name="action" value="UpdateLogin">
			  <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/Password.jsp">
			  <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/Password.jsp">
                <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">New User Name:</div>
                    </td>
                    <td width="200"> 
                      <input type="text" name="Username" maxlength="20" size="20" value="<%= userLogin.getUsername() %>">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">New Password:</div>
                    </td>
                    <td width="200"> 
                      <input type="password" name="Password" maxlength="20" size="20" value="<%= userLogin.getPassword() %>" onchange="setPasswordChanged()">
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
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr>
                  <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="Submit2" value="Submit">
                      </div>
                  </td>
                  <td width="194"> 
                      <div align="left"> 
                        <input type="reset" name="Cancel" value="Cancel">
                      </div>
                  </td>
                </tr>
              </table>
			</form>
            <p>&nbsp;</p>
          </div>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
