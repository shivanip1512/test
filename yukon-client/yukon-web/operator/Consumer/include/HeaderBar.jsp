<form name="restoreForm" method="post" action="<%= request.getContextPath() %>/servlet/StarsAdmin">
  <input type="hidden" name="action" value="RestoreContext">
</form>
<table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
  <tr> 
    <td width="103" height="102" background="../../WebConfig/yukon/ConsumerImage.jpg">&nbsp;</td>
    <td valign="top" height="102" width="656"> 
      <table width="657" cellspacing="0" cellpadding="0" border="0">
	    <tr> 
		  <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
	    </tr>
	    <tr> 
		  <td width="265" height="28" valign="middle" class="PageHeader">&nbsp;&nbsp;&nbsp;Customer 
		    Account Information&nbsp;&nbsp;</td>
		  <td width="" valign="middle">&nbsp;</td>
		  <td width="" valign="middle" align="right">
<% if (user.getAttribute(ServletUtils.ATT_CONTEXT_SWITCHED) != null) {
	LiteStarsEnergyCompany origComp = SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
%>
			<span class="MainText"><a href="" class="Link3" onclick="document.restoreForm.submit(); return false;">Back To <%= origComp.getName() %></a></span>
<% } else { %>
            <span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span> 
<% } %>
		  &nbsp;&nbsp;&nbsp;</td>
		  <td width="57" valign="middle" align="left"> 
			<span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span>
		  </td>
	    </tr>
	  </table>
    </td>
    <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
  </tr>
</table>
