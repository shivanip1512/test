<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
<!--
function confirmSubmit(form) { //v1.0
	if (form.StartDate.value == "" || form.EndDate.value == "") {
		alert("The start and end date cannot be empty");
		return false;
	}
	return confirm('Are you sure you would like to temporarily <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_OPT_OUT_VERB %>" defaultvalue="opt out of"/> all programs?');
}
//-->
</script>
<script language="JavaScript" src="<%= request.getContextPath() %>/JavaScript/calendar.js">
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
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_OPT_OUT, "PROGRAMS - OPT OUT");%>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
		   
              <p><table width="500" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="MainText" align="center"><cti:getProperty propertyid="<%=ResidentialCustomerRole.WEB_DESC_OPT_OUT %>"/> 
                  </td>
                </tr>
              </table>
              <br>
              <cti:checkNoProperty propertyid="<%= ResidentialCustomerRole.HIDE_OPT_OUT_BOX %>"> 
              <form method="post" action="<%=request.getContextPath()%>/servlet/SOAPClient" onSubmit="return confirmSubmit(this)">
                <input type="hidden" name="action" value="OptOutProgram">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/user/ConsumerStat/stat/General.jsp">
                <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                <input type="hidden" name="<%= ServletUtils.CONFIRM_ON_MESSAGE_PAGE %>">
<% if (exitQuestions != null && exitQuestions.getStarsExitInterviewQuestionCount() > 0) { %>
                <input type="hidden" name="<%= ServletUtils.NEED_MORE_INFORMATION %>">
                <input type="hidden" name="REDIRECT2" value="<%= request.getContextPath() %>/user/ConsumerStat/stat/OptForm.jsp">
<% } %>
                <table width="300" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr>
                    <td align="right" width="120">Start Date:</td>
                    <td width="168"> 
                      <input type="text" name="StartDate" id="StartDate" size="14" value="<%= datePart.format(new Date()) %>">
                      <a href="javascript:openCalendar(document.getElementById('StartDate'))"
					    onMouseOver="window.status='Start Date Calendar';return true;"
						onMouseOut="window.status='';return true;">
					    <img src="<%= request.getContextPath() %>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="absmiddle" border="0">
					  </a> 
                    </td>
                  </tr>
                  <tr>
                    <td align="right" width="120">Duration:</td>
                    <td width="168"> 
                      <select name="Duration">
<%
	StarsCustSelectionList optOutList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
	for (int i = 0; i < optOutList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = optOutList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getYukonDefID() %>"><%= entry.getContent() %></option>
<%
	}
%>
                      </select>
                    </td>
                  </tr>
                </table>
                <br>
                <input type="submit" name="Submit" value="Submit">
              </form>
              </cti:checkNoProperty> </div>
            <p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
