<%
	int[] invIDs = (int[]) session.getAttribute(ServletUtils.ATT_THERMOSTAT_INVENTORY_IDS);
	
	boolean isOperator = StarsUtils.isOperator(user);
	String scheduleLabel = (isOperator)? AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_SCHED, "Schedule") :
			AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule");
	String manualLabel = (isOperator)? AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_MANUAL, "Manual") :
			AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual");
%>

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
				alert("You've selected more than one type of thermostats. You can only set the same type of thermostats at a time.");
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
	
	document.form1.REDIRECT.value = "ThermSchedule.jsp?AllTherm";
	document.form1.submit();
}

function goToManual() {
	var thermType = checkThermostatType();
	if (thermType == null) return;
	
	document.form1.REDIRECT.value = "Thermostat.jsp?AllTherm";
	document.form1.submit();
}
</script>

              <form name="form1" method="post" action="">
			    <input type="hidden" name="REDIRECT" value="">
                <span class="MainText"> Select the thermostats you want to set:</span><br>
                <br>
                <table width="200" border="0" cellspacing="3" cellpadding="0" align="center">
                  <tr> 
                    <td width="32"> 
                      <div align="right" class="TableCell"> 
                        <input type="checkbox" name="All" value="true" onClick="selectAll(this.checked)">
                      </div>
                    </td>
                    <td width="256" class="TableCell">All</td>
                  </tr>
<%
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
                        <input type="checkbox" name="InvID" value="<%= thermostat.getInventoryID() %>" onClick="selectSingle(this.form)" <%= checked %>>
                        <input type="hidden" name="type" value="<%= thermostat.getLMHardware().getStarsThermostatSettings().getStarsThermostatProgram().getThermostatType().toString() %>">
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
                      <div align="center" style="border:solid 1px #666999; cursor:pointer" onClick="goToSchedule()"> 
                        <span class="MainText"><%= scheduleLabel %></span> 
                      </div>
                    </td>
                    <td> 
                      <div align="center" style="border:solid 1px #666999; cursor:pointer" onClick="goToManual()"> 
                        <span class="MainText"><%= manualLabel %></span> 
                      </div>
                    </td>
                  </tr>
                </table>
              </form>