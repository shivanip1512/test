<%@ include file="include/StarsHeader.jsp" %>
<%
	MultiAction actions = (MultiAction) session.getAttribute(ServletUtils.ATT_NEW_ACCOUNT_WIZARD);
	if (actions != null) {
		ActionBase failedAction = actions.getFailedAction();
		if (failedAction != null) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, errorMsg);
			
			if (failedAction instanceof NewCustAccountAction) {
				response.sendRedirect(request.getContextPath() + "/operator/Consumer/New.jsp?Wizard=true");
			}
			else if (failedAction instanceof CreateLMHardwareAction) {
				actions.removeAction( NewCustAccountAction.class );
				response.sendRedirect(request.getContextPath() + "/operator/Consumer/CreateHardware.jsp?Wizard=true");
			}
			else if (failedAction instanceof ProgramSignUpAction) {
				actions.removeAction( NewCustAccountAction.class );
				actions.removeAction( CreateLMHardwareAction.class );
				response.sendRedirect(request.getContextPath() + "/operator/Consumer/Programs.jsp?Wizard=true");
			}
		}
		else {
			session.removeAttribute(ServletUtils.ATT_NEW_ACCOUNT_WIZARD);
		}
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "MainText" align="center"> 
              <% String header = "NEW SIGNUP WIZARD"; %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <span class="ConfirmMsg">Customer account created successfully, 
              would you like to:</span><br>
              <br>
              <table width="300" border="0" cellspacing="0" cellpadding="2">
                <tr> 
                  <td> 
                    <table width="300" border="1" cellspacing="0" cellpadding="2" bgcolor="#CCCCCC" height="40">
                      <tr> 
                        <td height="30"> 
                          <div align="center"><span class="MainText">Edit the 
                            account you just created</span><br>
                            <input type="button" name="Edit" value="Edit" onclick="location.href='Update.jsp'">
                          </div>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
                <tr> 
                  <td> 
                    <div align="center" class="MainText">or</div>
                  </td>
                </tr>
                <tr> 
                  <td> 
                    <table width="300" border="1" cellspacing="0" cellpadding="2" bgcolor="#CCCCCC" height="40">
                      <tr> 
                        <td height="30"> 
                          <div align="center"><span class="MainText">Create another 
                            account</span><br>
                            <input type="button" name="New" value="New" onclick="location.href='New.jsp?Init=true<% if (request.getParameter("Wizard") != null) { %>&Wizard=true<% } %>'">
                          </div>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <br>
              <input type="button" name="Done" value="Done" onclick="location.href = '../Operations.jsp'">
              <p>&nbsp;</p>
              <p>&nbsp;</p>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../WebConfig/yukon/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
