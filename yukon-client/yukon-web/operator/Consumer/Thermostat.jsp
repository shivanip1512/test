<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	StarsThermostatSettings thermoSettings = null;
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
		for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
			if (inventories.getStarsInventory(i).getInventoryID() == invID) {
				thermoSettings = inventories.getStarsInventory(i).getLMHardware().getStarsThermostatSettings();
				break;
			}
		}
	}
	else {
		// Set a single thermostat
		int thermNo = Integer.parseInt(request.getParameter("InvNo"));
		StarsInventory thermostat = inventories.getStarsInventory(thermNo);
		thermoSettings = thermostat.getLMHardware().getStarsThermostatSettings();
		invID = thermostat.getInventoryID();
		thermNoStr = "InvNo=" + thermNo;
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
		  <% String pageName = "Thermostat.jsp?" + thermNoStr; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF" bordercolor="#333399"> 
            <div align="center"> 
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_THERM_MANUAL, "THERMOSTAT - MANUAL"); %>
              <%@ include file="include/InfoSearchBar.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
              <%@ include file="../../include/therm_manual.jsp" %>
			  <p align="center" class="MainText">
			    <% int crStartYear = 2003; %><%@ include file="../../include/copyright.jsp" %>
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
<div align="center"></div>
</body>
</html>
