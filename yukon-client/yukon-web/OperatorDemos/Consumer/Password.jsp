<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
function checkPasswords(form) {
	if (form.Password.value != form.Password2.value) {
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
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
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
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "ADMINISTRATION - CHANGE PASSWORD"; %>
              <%@ include file="InfoSearchBar.jsp" %>
              <br>
<%
	String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
	session.removeAttribute(ServletUtils.ATT_ERROR_MESSAGE);
%>
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			
            <form method="POST" action="/servlet/SOAPClient">
			  <input type="hidden" name="action" value="UpdateLogin">
              <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                <tr> 
                  <td width="100" class="TableCell"> 
                    <div align="right">User Name: </div>
                  </td>
                  <td width="200"> 
                    <input type="text" name="Username" maxlength="14" size="14" value="<%= userLogin.getUsername() %>">
                  </td>
                </tr>
                <tr> 
                  <td width="100" class="TableCell"> 
                    <div align="right">Old Password:</div>
                  </td>
                  <td width="200"> 
                    <input type="text" name="OldPassword" maxlength="14" size="14" value="<%= userLogin.getPassword() %>">
                  </td>
                </tr>
                <tr> 
                  <td width="100" class="TableCell"> 
                    <div align="right">New Password:</div>
                  </td>
                  <td width="200"> 
                    <input type="text" name="Password" maxlength="14" size="14">
                  </td>
                </tr>
                <tr> 
                  <td width="100" class="TableCell"> 
                    <div align="right">Confirm Password:</div>
                  </td>
                  <td width="200"> 
                    <input type="text" name="Password2" maxlength="14" size="14">
                  </td>
                </tr>
              </table>
              <br>
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr>
                  <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="Submit2" value="Submit" onclick="return checkPasswords(this.form)">
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
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
