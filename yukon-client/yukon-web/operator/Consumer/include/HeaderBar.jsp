<form name="restoreForm" method="post" action="<%= request.getContextPath() %>/servlet/StarsAdmin">
  <input type="hidden" name="action" value="RestoreContext">
</form>
<table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
  <tr> 
    <td width="103" height="102" background="../../WebConfig/yukon/ConsumerImage.jpg">&nbsp;</td>
    <td valign="top" height="102" width="656"> 
      <table width="657" cellspacing="0" cellpadding="0" border="0">
	    <tr> 
		  <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
	    </tr>
	    <tr> 
		  <td width="265" height="28" valign="middle" class="PageHeader">&nbsp;&nbsp;&nbsp;Customer 
		    Account Information&nbsp;&nbsp;</td>
		  <td width="253" valign="middle">&nbsp;</td>
<% if (session.getAttribute(ServletUtils.ATT_CONTEXT_SWITCHED) == null) { %>
		  <td width="58" valign="middle" align="right">
            <span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span> 
		  &nbsp;&nbsp;&nbsp;</td>
<% } else { %>
		  <td width="58" valign="middle">&nbsp;</td>
<% } %>
<% if (session.getAttribute(com.cannontech.common.constants.LoginController.SAVED_YUKON_USERS) != null
	&& liteEC.getParent() != null) {
%>
		  <td width="" valign="middle" nowrap> 
		    <span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Back to <%= liteEC.getParent().getName() %></a></span>
		  &nbsp;&nbsp;&nbsp;</td>
<% } else { %>
		  <td width="57" valign="middle"> 
			<span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span>
		  </td>
<% } %>
	    </tr>
	  </table>
    </td>
    <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
  </tr>
</table>
