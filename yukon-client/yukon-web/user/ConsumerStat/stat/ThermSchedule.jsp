<%@ include file="include/StarsHeader.jsp" %>
<%
	StarsThermostatProgram thermoProgram = null;
	StarsThermostatDynamicData curSettings = null;
	int invID = 0;
	int[] invIDs = new int[0];
	
	boolean allTherm = request.getParameter("AllTherm") != null;
	String thermNoStr = "AllTherm";
	
	if (allTherm) {
		// Set multiple thermostats
		invIDs = (int[]) session.getAttribute(ServletUtils.ATT_THERMOSTAT_INVENTORY_IDS);
		if (invIDs == null) {
			response.sendRedirect("AllTherm.jsp");
			return;
		}
		
		invID = invIDs[0];
		for (int i = 0; i < thermostats.getStarsInventoryCount(); i++) {
			if (thermostats.getStarsInventory(i).getInventoryID() == invID) {
				thermoProgram = thermostats.getStarsInventory(i).getLMHardware().getStarsThermostatSettings().getStarsThermostatProgram();
				break;
			}
		}
	}
	else {
		// Set a single thermostat
		int thermNo = Integer.parseInt(request.getParameter("Item"));
		StarsInventory thermostat = thermostats.getStarsInventory(thermNo);
		thermoProgram = thermostat.getLMHardware().getStarsThermostatSettings().getStarsThermostatProgram();
		curSettings = thermostat.getLMHardware().getStarsThermostatSettings().getStarsThermostatDynamicData();
		invID = thermostat.getInventoryID();
		thermNoStr = "Item=" + thermNo;
	}
	
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
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
		  <% String pageName = "ThermSchedule.jsp?" + thermNoStr; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_THERM_SCHED, "THERMOSTAT - SCHEDULE"); %>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
<%
	StarsThermostatTypes thermoType = thermoProgram.getThermostatType();
	if (thermoType.getType() == StarsThermostatTypes.EXPRESSSTAT_TYPE) {
%>
			  <%@ include file="../../../include/therm_schedule.jsp" %>
<%
	}
	else if (thermoType.getType() == StarsThermostatTypes.COMMERCIAL_TYPE) {
%>
			  <%@ include file="../../../include/therm_schedule1.jsp" %>
<%
	}
	else if (thermoType.getType() == StarsThermostatTypes.ENERGYPRO_TYPE) {
%>
			  <%@ include file="../../../include/therm_schedule2.jsp" %>
<%
	}
%>
			  <p align="center" class="MainText">
			    <% int crStartYear = 2003; %><%@ include file="../../../include/copyright.jsp" %>
			  </p>
              <p align="center" class="MainText">&nbsp; </p>
            </div>
			
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