<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
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
		  <% String pageName = "OptForm.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF" align = "center"> 
      		  <% String header = "OPT OUT"; %>
              <%@ include file="InfoSearchBar.jsp" %> 
          
            <p><span class="Main">Please complete the following form to opt out 
              of the program:</span> </p>
            <hr width = "90%">
          
			<form method="post" action="/servlet/SOAPClient">
			  <input type="hidden" name="action" value="SendExitAnswers">
			  <input type="hidden" name="REDIRECT" value="/OperatorDemos/Consumer/Programs.jsp">
			  <input type="hidden" name="REFERRER" value="/OperatorDemos/Consumer/OptOut.jsp">
                <table width="500" border="0" cellspacing="0" cellpadding="3" valign="top">
<%
	StarsGetExitInterviewQuestionsResponse questions = (StarsGetExitInterviewQuestionsResponse) operator.getAttribute(ServletUtils.ATT_EXIT_INTERVIEW_QUESTIONS);
	for (int i = 0; i < questions.getStarsExitInterviewQuestionCount(); i++) {
		StarsExitInterviewQuestion question = questions.getStarsExitInterviewQuestion(i);
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
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>  
<br>
</body>
</html>
