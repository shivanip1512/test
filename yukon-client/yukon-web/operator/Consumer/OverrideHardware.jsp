<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory inventory = inventories.getStarsInventory(invNo);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function validate(form) {
	if (form.StartDate.value == "" || form.EndDate.value == "") {
		alert("The start and end date cannot be empty");
		return false;
	}
	return true;
}
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
		  <% String pageName = "OverrideHardware.jsp?InvNo=" + invNo; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_HARDWARE_OVERRIDING, "HARDWARE - OVERRIDING"); %>
              <%@ include file="include/InfoSearchBar.jsp" %>
              
			  <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
			    <input type="hidden" name="action" value="OverrideLMHardware">
				<input type="hidden" name="InvID" value="<%= inventory.getInventoryID() %>">
			    <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Inventory.jsp?InvNo=<%= invNo %>">
			    <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/ConfigHardware.jsp?InvNo=<%= invNo %>">
                <table width="350" border="1" cellspacing="0" cellpadding="5" bgcolor="#CCCCCC" align="center">
                  <tr> 
                    <td align="center"> 
                      <p class="HeaderCell">Temporarily <cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_OVERRIDE %>" defaultvalue="override"/> 
                        the hardware</p>
                      <table width="300" border="0" cellspacing="0" cellpadding="3" class="TableCell2">
                        <tr> 
                          <td align="right" width="120">Start Date:</td>
                          <td width="168"> 
                            <input type="text" name="StartDate" id="StartDate" size="14" value="<%= datePart.format(new Date()) %>">
                            <a href="javascript:openCalendar(document.getElementById('StartDate'))"
							  onMouseOver="window.status='Start Date Calendar';return true;"
							  onMouseOut="window.status='';return true;"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="absmiddle" border="0"> 
                            </a>
						  </td>
                        </tr>
                        <tr> 
                          <td align="right" width="120">End Date (midnight of):</td>
                          <td width="168"> 
                            <input type="text" name="EndDate" id="EndDate" size="14" value="<%= datePart.format(new Date()) %>">
                            <a href="javascript:openCalendar(document.getElementById('EndDate'))"
							  onMouseOver="window.status='Start Date Calendar';return true;"
							  onMouseOut="window.status='';return true;"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="absmiddle" border="0"> 
                            </a>
						  </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="350" border="0" cellspacing="0" cellpadding="5">
                  <tr>
                    <td align="right">
                      <input type="submit" name="Submit" value="Submit">
                    </td>
                    <td>
                      <input type="button" name="Cancel" value="Cancel" onclick="history.back()">
                    </td>
                  </tr>
                </table>
              </form>
            <p>&nbsp;</p></div>
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
