<%@ include file="include/StarsHeader.jsp" %>
<%
	int itemNo = Integer.parseInt(request.getParameter("Item"));
	StarsInventory thermostat = thermostats.getStarsInventory(itemNo);
	StarsThermostatSettings thermoSettings = thermostat.getLMHardware().getStarsThermostatSettings();
%>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function goToSchedule() {
<%
	if (thermoSettings.getStarsThermostatDynamicData() == null) {
%>
	location.href = "ThermSchedule.jsp?Item=<%= itemNo %>";
<%
	} else {
%>
	location.href = "ThermSchedule2.jsp?Item=<%= itemNo %>";
<%
	}
%>
}

function goToManual() {
<%
	if (thermoSettings.getStarsThermostatDynamicData() == null) {
%>
	location.href = "Thermostat.jsp?Item=<%= itemNo %>";
<%
	} else {
%>
	location.href = "Thermostat2.jsp?Item=<%= itemNo %>";
<%
	}
%>
}
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
		  <% String pageName = "NewLabel.jsp?Item=" + itemNo; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = "THERMOSTAT - CHANGE LABEL"; %>
              <%@ include file="include/InfoBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
              
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			    <input type="hidden" name="action" value="UpdateLMHardware">
                <input type="hidden" name="InvID" value="<%= thermostat.getInventoryID() %>">
				<input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/NewLabel.jsp?Item=<%= itemNo %>">
				<input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/NewLabel.jsp?Item=<%= itemNo %>">
                <table width="500" border="0" cellspacing="0" cellpadding="3" align="center" class="TableCell">
                  <tr> 
                    <td align="right">Change thermostat name to: </td>
                    <td> 
                      <input type="text" name="DeviceLabel" value="<%= thermostat.getDeviceLabel() %>">
                    </td>
                  </tr>
                  <tr> 
                    <td align="right"> 
                      <input type="submit" name="Submit" value="Submit">
                    </td>
                    <td> 
                      <input type="reset" name="Reset" value="Reset">
                    </td>
                  </tr>
                </table>
              </form>
              <br>
            </div>
            <table width="200" border="0" cellspacing="5" cellpadding="0" align="center">
              <tr> 
                <td> 
                  <div align="center" style="border:solid 1px #666999; cursor:pointer" onclick="goToSchedule()">
				    <span class="MainText"><%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule") %></span>
				  </div>
                </td>
                <td> 
                  <div align="center" style="border:solid 1px #666999; cursor:pointer" onclick="goToManual()">
				    <span class="MainText"><%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual") %></span>
				  </div>
                </td>
              </tr>
            </table>
            <p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
