<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
<!--
function doReenable(form) {
	form.action.value = "EnableService";
	form.submit();
}

function MM_popupMsg(msg) { //v1.0
  return confirm(msg);
}
//-->
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
		  <% String pageName = "OptForm.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF" align = "center"> 
      		  <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_OPT_OUT, "OPT OUT"); %>
              <%@ include file="include/InfoSearchBar.jsp" %> 
          
            <p><span class="MainText">Please complete the following form to opt out of the program:</span> </p>
            <hr width = "90%">
			<form method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			  <input type="hidden" name="action" value="SendOptOutNotification">
			  <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Programs.jsp">
			  <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/OptOut.jsp">
			  <input type="hidden" name="<%= ServletUtils.CONFIRM_ON_MESSAGE_PAGE %>">
                <table width="500" border="0" cellspacing="0" cellpadding="3" valign="top">
<%
	for (int i = 0; i < exitQuestions.getStarsExitInterviewQuestionCount(); i++) {
		StarsExitInterviewQuestion question = exitQuestions.getStarsExitInterviewQuestion(i);
%>
				  <input type="hidden" name="QID" value="<%= question.getQuestionID() %>">
                  <tr> 
                    <td class="TableCell">
					  <%= question.getQuestion() %>
                    </td>
                  </tr>
                  <tr> 
                    <td> 
                      <div align="left"> 
                        <input type="text" name="Answer" size="80">
                      </div>
                    </td>
                  </tr>
<%
	}
%>
                </table>
              <p align="center"> 
                <input type="submit" name="Submit" value="Submit">
                <input type="button" name="Cancel" value="Cancel" onclick="location='OptOut.jsp'">
              <p>&nbsp;</p></form>
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
