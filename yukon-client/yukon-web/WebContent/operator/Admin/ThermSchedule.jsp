<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%
	StarsThermostatTypes thermoType = StarsThermostatTypes.valueOf(request.getParameter("type"));
	
	StarsThermostatProgram thermoProgram = null;
	for (int i = 0; i < dftThermoSchedules.getStarsThermostatProgramCount(); i++) {
		if (dftThermoSchedules.getStarsThermostatProgram(i).getThermostatType().getType() == thermoType.getType()) {
			thermoProgram = dftThermoSchedules.getStarsThermostatProgram(i);
			break;
		}
	}
	
	StarsThermostatDynamicData curSettings = null;
	int invID = -1;
	int[] invIDs = new int[0];
	boolean allTherm = false;
	String thermNoStr = "";
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - DEFAULT THERMOSTAT SCHEDULE</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
<%
	if (thermoType.getType() == StarsThermostatTypes.EXPRESSSTAT_TYPE) {
%>
			  <%@ include file="../../include/therm_schedule.jspf" %>
<%
	}
	else if (thermoType.getType() == StarsThermostatTypes.COMMERCIAL_TYPE) {
%>
			  <%@ include file="../../include/therm_schedule1.jspf" %>
<%
	}
	else if (thermoType.getType() == StarsThermostatTypes.ENERGYPRO_TYPE) {
%>
			  <%@ include file="../../include/therm_schedule2.jspf" %>
<%
	}
	else if (thermoType.getType() == StarsThermostatTypes.EXPRESSSTAT_HEATPUMP_TYPE) {
%>
			  <%@ include file="../../include/therm_schedule3.jspf" %>
<%
	}
%>
			  <p align="center" class="MainText">
			    <%@ include file="../../include/copyright.jsp" %>
			  </p>
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
