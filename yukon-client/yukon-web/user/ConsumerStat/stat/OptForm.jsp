<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../../demostyle.css" type="text/css">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
</script>

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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td id="Header" colspan="4" height="74" background="../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>">&nbsp;</td>
<script language="JavaScript">
	document.getElementById("Header").background = '../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>';
</script>
              </tr>
              <tr> 
                  <td width="265" height="28">&nbsp;</td>
				  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">&nbsp;</td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
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
            <% String pageName = "OptOut.jsp"; %>
            <%@ include file="Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>

		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              <div align="center"> 
                <br>
                
              <div align="center"> 
              <% String header = ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_OPT_OUT_TITLE); %>
              <%@ include file="InfoBar.jsp" %>
                <table width="600" border="0" cellpadding="0" cellspacing="0">
                  <tr> 
                    <td> 
                      <hr>
                    </td>
                  </tr>
                </table>
                <p class="Main">Please complete the following form to opt out 
                  of your program:</p>
                  <form method="post" action="/servlet/SOAPClient">
					<input type="hidden" name="action" value="SendExitAnswers">
					<input type="hidden" name="REDIRECT" value="/user/ConsumerStat/stat/General.jsp">
					<input type="hidden" name="REFERRER" value="/user/ConsumerStat/stat/OptOut.jsp">
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
                </form>
				  
                <p><span class="TableCell">* This field must be completed.</span></p>
                <p><br>
                </p>
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
