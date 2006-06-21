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
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
		  <% String pageName = "NewLabel.jsp?Item=" + itemNo; %>
          <%@ include file="include/Nav.jspf" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = "THERMOSTAT - CHANGE LABEL"; %>
              <%@ include file="include/InfoBar.jspf" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
              
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
                      <input type="submit" name="Submit" value="Save">
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
                  <div align="center" style="border:solid 1px #666999; cursor:pointer" onclick="location.href = 'ThermSchedule.jsp?Item=<%= itemNo %>'">
				    <span class="MainText"><%= DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED) %></span>
				  </div>
                </td>
                <td> 
                  <div align="center" style="border:solid 1px #666999; cursor:pointer" onclick="location.href = "Thermostat.jsp?Item=<%= itemNo %>"">
				    <span class="MainText"><%= DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL) %></span>
				  </div>
                </td>
              </tr>
            </table>
            <p>&nbsp;</p>
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
