<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	String[] invIDStrs = request.getParameterValues("InvID");
	if (invIDStrs != null) {
		int[] invIDs = new int[invIDStrs.length];
		for (int i = 0; i < invIDs.length; i++)
			invIDs[i] = Integer.parseInt(invIDStrs[i]);
		
		Arrays.sort(invIDs);
		session.setAttribute(ServletUtils.ATT_THERMOSTAT_INVENTORY_IDS, invIDs);
		
		String redirect = request.getParameter(ServletUtils.ATT_REDIRECT);
		if (redirect != null) {
			response.sendRedirect(redirect);
			return;
		}
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
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
		  <% String pageName = "AllTherm.jsp"; %>
          <%@ include file="include/Nav.jspf" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "THERMOSTAT - APPLY TO ALL"; %>
              <%@ include file="include/InfoSearchBar.jspf" %>
              <%@ include file="../../include/therm_all.jspf" %>
              <p>&nbsp;</p>
              </div>
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
