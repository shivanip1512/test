<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function selectAll(checked) {
	var checkboxes = document.getElementsByName("InvID");
	for (i = 0; i < checkboxes.length; i++)
		checkboxes[i].checked = checked;
}

function selectSingle(form) {
	var checkboxes = document.getElementsByName("InvID");
	var allChecked = true;
	for (i = 0; i < checkboxes.length; i++)
		if (!checkboxes[i].checked) {
			allChecked = false;
			break;
		}
	form.All.checked = allChecked;
}

function checkThermostatType() {
	var checkboxes = document.getElementsByName("InvID");
	var types = document.getElementsByName("type");
	var thermType = null;
	
	for (i = 0; i < checkboxes.length; i++) {
		if (checkboxes[i].checked) {
			if (thermType != null && thermType != types[i].value) {
				alert("You've selected more than one type of thermostats. You can only change settings for the same type of thermostats at a time.");
				return null;
			}
			thermType = types[i].value;
		}
	}
	
	return thermType;
}

function goToSchedule() {
	var thermType = checkThermostatType();
	if (thermType == null) return;
	
	if (thermType == "<%= StarsThermostatTypes.EXPRESSSTAT.toString() %>")
		document.form1.attributes["action"].value = "ThermSchedule.jsp?Item=-1";
	else if (thermType == "<%= StarsThermostatTypes.COMMERCIAL.toString() %>")
		document.form1.attributes["action"].value = "ThermSchedule1.jsp?Item=-1";
	else if (thermType == "<%= StarsThermostatTypes.ENERGYPRO.toString() %>")
		document.form1.attributes["action"].value = "ThermSchedule2.jsp?Item=-1";
	document.form1.submit();
}

function goToManual() {
	var thermType = checkThermostatType();
	if (thermType == null) return;
	
	if (thermType == "<%= StarsThermostatTypes.EXPRESSSTAT.toString() %>")
		document.form1.attributes["action"].value = "Thermostat.jsp?Item=-1";
	else if (thermType == "<%= StarsThermostatTypes.ENERGYPRO.toString() %>")
		document.form1.attributes["action"].value = "Thermostat2.jsp?Item=-1";
	document.form1.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="selectSingle(document.form1)">
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
		  <% String pageName = "AllTherm.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = "THERMOSTAT - APPLY TO ALL"; %>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
              
              <form name="form1" method="post" action="">
                <span class="MainText"> Select the thermostats you want to control:</span><br>
                <table width="200" border="0" cellspacing="3" cellpadding="0" align="center">
                  <tr> 
                    <td width="32"> 
                      <div align="right" class="TableCell"> 
                        <input type="checkbox" name="All" value="true" onclick="selectAll(this.checked)">
                      </div>
                    </td>
                    <td width="256" class="TableCell">All</td>
                  </tr>
<%
	int[] invIDs = (int[]) session.getAttribute(ServletUtils.ATT_THERMOSTAT_INVENTORY_IDS);
	
	for (int i = 0; i < thermostats.getStarsInventoryCount(); i++) {
		StarsInventory thermostat = thermostats.getStarsInventory(i);
		String checked = (invIDs != null && Arrays.binarySearch(invIDs, thermostat.getInventoryID()) >= 0)? "checked" : "";
		String label = thermostat.getDeviceLabel();
		if (label.equals(""))
			label= thermostat.getLMHardware().getManufacturerSerialNumber();
%>
                  <tr> 
                    <td width="32"> 
                      <div align="right"> 
                        <input type="checkbox" name="InvID" value="<%= thermostat.getInventoryID() %>" onclick="selectSingle(this.form)" <%= checked %>>
                        <input type="hidden" name="type" value="<%= thermostat.getLMHardware().getStarsThermostatSettings().getThermostatType().toString() %>">
                      </div>
                    </td>
                    <td width="256" class="TableCell"><%= label %></td>
                  </tr>
<%
	}
%>
                </table>
                <br>
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
              </form>
            </div>
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
