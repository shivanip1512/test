<script language="JavaScript" src="<%= request.getContextPath() %>/JavaScript/change_monitor.js"></script>

<table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
  <tr> 
    <td width="102" height="102" background="../../WebConfig/yukon/AdminImage.jpg">&nbsp;</td>
    <td valign="bottom" height="102"> 
	  <table width="657" cellspacing="0" cellpadding="0" border="0">
	    <tr> 
		  <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
	    </tr>
	    <tr> 
		  <td width="265" height="28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Administration</td>
		  <td width="253" valign="middle">&nbsp;</td>
		  <td width="" valign="middle" align="right" nowrap>
<% if (session.getAttribute(ServletUtils.ATT_CONTEXT_SWITCHED) == null) { %>
            <span class="MainText"><a href="../Operations.jsp" class="Link3" onclick="return warnUnsavedChanges();">Home</a></span> 
<% } %>
		  &nbsp;&nbsp;&nbsp;</td>
		  <td width="" valign="middle" nowrap> 
<% if (session.getAttribute(com.cannontech.common.constants.LoginController.SAVED_YUKON_USERS) != null
		&& liteEC.getParent() != null) { %>
		    <span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3" onclick="return warnUnsavedChanges();">Back to <%= liteEC.getParent().getName() %></a>&nbsp;&nbsp;&nbsp;</span>
<% } else { %>
			<span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3" onclick="return warnUnsavedChanges();">Log Off</a>&nbsp;</span>
<% } %>
		  </td>
	    </tr>
	  </table>
    </td>
    <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
  </tr>
</table>
