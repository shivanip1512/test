<%
/* Required predefined variables:
 * invID: int
 * thermostatType: StarsThermostatTypes
 * thermNoStr: String
 */
%>

<script language="JavaScript">
var scheduleNames = new Array(<%= thermSchedules.getStarsThermostatProgramCount() + 1 %>);
scheduleNames[0] = "";
<% for (int i = 0; i < thermSchedules.getStarsThermostatProgramCount(); i++) { %>
scheduleNames[<%= i+1 %>]="<%= thermSchedules.getStarsThermostatProgram(i).getScheduleName() %>";
<% } %>

function selectSchedule(form) {
	form.ScheduleName.value = scheduleNames[ form.ScheduleID.selectedIndex ];
	form.Apply.disabled = (form.ScheduleID.value == -1);
	form.Delete.disabled = (form.ScheduleID.value == -1);
}

function applySchedule(form) {
	form.action.value = "ApplyThermostatSchedule";
	form.submit();
}

function deleteSchedule(form) {
	if (!confirm("Are you sure you want to delete this saved schedule?"))
		return;
	form.action.value = "DeleteThermostatSchedule";
	form.submit();
}
</script>

<form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
  <div align="center">
    <input type="hidden" name="action" value="SaveThermostatSchedule">
    <input type="hidden" name="InvID" value="<%= invID %>">
	<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?<%= thermNoStr %>">
	<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?<%= thermNoStr %>">
    <table width="80%" border="0" cellspacing="0" cellpadding="0" class="MainText">
      <tr> 
        <td align="center">Save the current thermostat schedule, apply a saved 
          schedule to the thermostat, or delete a saved schedule.</td>
      </tr>
    </table>
    <br>
    <table width="50%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td>
          <table width="100%" border="0" cellspacing="0" cellpadding="5" class="TableCell">
            <tr>
              <td width="40%" align="right">Schedule Name:</td>
              <td width="60%"> 
                <input type="text" name="ScheduleName" size="30">
              </td>
            </tr>
            <tr>
              <td width="40%" align="right">&nbsp;</td>
              <td width="60%">
                <select name="ScheduleID" onchange="selectSchedule(this.form)">
                  <option value="-1">&lt;Select a saved schedule&gt;</option>
<%
	for (int i = 0; i < thermSchedules.getStarsThermostatProgramCount(); i++) {
		if (thermSchedules.getStarsThermostatProgram(i).getThermostatType().getType() == thermostatType.getType()) {
			StarsThermostatProgram schedule = thermSchedules.getStarsThermostatProgram(i);
%>
                  <option value="<%= schedule.getScheduleID() %>"><%= schedule.getScheduleName() %></option>
<%
		}
	}
%>
                </select>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
    <table width="50%" border="0" cellpadding="5">
      <tr> 
        <td width="40%" align = "right" class = "TableCell"> 
          <input type="submit" name="Save" value="Save">
        </td>
        <td width="20%" align = "center" class = "TableCell"> 
          <input type="button" name="Apply" value="Apply" onclick="applySchedule(this.form)" disabled>
        </td>
        <td width="40%" align = "left" class = "TableCell">
          <input type="button" name="Delete" value="Delete" onclick="deleteSchedule(this.form)" disabled>
        </td>
      </tr>
    </table>
  </div>
</form>
