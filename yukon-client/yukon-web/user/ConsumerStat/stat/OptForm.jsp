<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
<!--
function MM_popupMsg(msg) { //v1.0
  alert(msg);
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
            <% String pageName = "OptOut.jsp"; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <br>
              <div align="center"> 
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_OPT_OUT, "PROGRAMS - OPT OUT"); %>
              <%@ include file="include/InfoBar.jsp" %>
                <table width="600" border="0" cellpadding="0" cellspacing="0">
                  <tr> 
                    <td> 
                      <hr>
                    </td>
                  </tr>
                </table>
                <p class="MainText">Please complete the following form to <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_OPT_OUT_VERB %>"/> 
				your program:</p>
                  <form method="post" action="<%=request.getContextPath()%>/servlet/SOAPClient">
					<input type="hidden" name="action" value="SendOptOutNotification">
					<input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/General.jsp">
					<input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/OptOut.jsp">
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
                  </p>
                </form>
                <p><span class="TableCell">* This field must be completed.</span></p>
                <p><br></p>
              </div>
            </div>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<div align="center"></div>
</body>
</html>
