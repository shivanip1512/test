<form name="restoreForm" method="post" action="<%= request.getContextPath() %>/servlet/StarsAdmin">
  <input type="hidden" name="action" value="RestoreContext">
</form>
<table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
  <tr> 
    <td width="102" height="102" background="../../WebConfig/yukon/WorkImage.jpg">&nbsp;</td>
    <td valign="bottom" height="102"> 
	  <table width="657" cellspacing="0" cellpadding="0" border="0">
	    <tr> 
		  <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
	    </tr>
	    <tr> 
		  <td width="310" height="28" class="PageHeader">&nbsp;&nbsp;&nbsp;Work Orders</td>
		  <td width="" valign="middle">&nbsp;</td>
		  <td width="" valign="middle" align="right"> 
			<span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span>
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
