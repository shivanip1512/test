<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int thermNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory thermostat = inventories.getStarsInventory(thermNo);
	StarsThermostatDynamicData curSettings = thermostat.getLMHardware().getStarsThermostatSettings().getStarsThermostatDynamicData();
	
	int invID = thermostat.getInventoryID();
	StarsThermostatTypes thermostatType = thermostat.getLMHardware().getStarsThermostatSettings().getStarsThermostatProgram().getThermostatType();
	String thermNoStr = "InvNo=" + thermNo;
	
	if (curSettings != null && ServletUtils.isGatewayTimeout(curSettings.getLastUpdatedTime())) {
		if (request.getParameter("OmitTimeout") != null)
			session.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_OMIT_GATEWAY_TIMEOUT, "true");
		
		if (session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_OMIT_GATEWAY_TIMEOUT) == null) {
			session.setAttribute(ServletUtils.ATT_REFERRER, request.getRequestURI() + "?" + thermNoStr);
			response.sendRedirect( "Timeout.jsp" );
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
		  <% String pageName = "SavedSchedules.jsp?" + thermNoStr; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              
            <div align="center"> 
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_THERM_SAVED_SCHED, "THERMOSTAT - SAVED SCHEDULES"); %>
              <%@ include file="include/InfoSearchBar.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  <%@ include file="../../include/saved_schedules.jsp" %>
              <p align="center" class="MainText">&nbsp; </p>
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
