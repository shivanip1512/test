<%
/* Required predefined variables:
 * thermoSettings: StarsThermostatSettings
 * invID: int
 * invIDs: int[]
 * allTherm: boolean
 * thermNoStr: String
 */
	boolean isOperator = ECUtils.isOperator(user);
	
	int setpoint = 72;
	int coolSetpoint = 72;
	int heatSetpoint = 72;
	boolean hold = false;
	String modeStr = "";
	String fanStr = "";
	StarsThermostatManualEvent lastEvent = null;
	boolean runProgram = false;
	
	if (curSettings != null) {
		if (curSettings.hasCoolSetpoint())
			coolSetpoint = curSettings.getCoolSetpoint();
		if (curSettings.hasHeatSetpoint())
			heatSetpoint = curSettings.getHeatSetpoint();
		hold = curSettings.getSetpointHold();
		if (curSettings.getMode() != null) {
			if (curSettings.getMode().getType() == StarsThermoModeSettings.COOL_TYPE)
				setpoint = coolSetpoint;
			else if (curSettings.getMode().getType() == StarsThermoModeSettings.HEAT_TYPE)
				setpoint = heatSetpoint;
			modeStr = curSettings.getMode().toString();
		}
		if (curSettings.getFan() != null)
			fanStr = curSettings.getFan().toString();
	}
	else {
		if (thermoSettings != null && thermoSettings.getStarsThermostatManualEventCount() > 0) {
			lastEvent = thermoSettings.getStarsThermostatManualEvent(
					thermoSettings.getStarsThermostatManualEventCount() - 1);
			
			runProgram = (lastEvent.getThermostatManualOption().getTemperature() == -1);
			if (!runProgram) {
				setpoint = lastEvent.getThermostatManualOption().getTemperature();
				hold = lastEvent.getThermostatManualOption().getHold();
				if (lastEvent.getThermostatManualOption().getMode() != null)
					modeStr = lastEvent.getThermostatManualOption().getMode().toString();
				if (lastEvent.getThermostatManualOption().getFan() != null)
					fanStr = lastEvent.getThermostatManualOption().getFan().toString();
			}
		}
	}
%>

<script language="JavaScript" src ="<%= request.getContextPath() %>/JavaScript/nav_menu.js">
</script>
<script langauge = "JavaScript">
<!-- 
var lowerLimit = 45;
var upperLimit = 88;

<% if (curSettings != null) { %>
<%	if (curSettings.getLowerCoolSetpointLimit() > 0) { %>
	lowerLimit = <%= curSettings.getLowerCoolSetpointLimit() %>;
<%	} %>
<%	if (curSettings.getUpperHeatSetpointLimit() > 0) { %>
	upperLimit = <%= curSettings.getUpperHeatSetpointLimit() %>;
<%	} %>
<% } %>

var coolSetpoint = <%= coolSetpoint %>;
var heatSetpoint = <%= heatSetpoint %>;
var dftSetpoint = <%= setpoint %>;

function setTemp(curTemp) {
	var mode = document.MForm.mode.value;
<% if (curSettings != null) { %>
	if (mode == "") mode = "<%= curSettings.getMode() %>";
<% } %>
	if (mode == "<%= StarsThermoModeSettings.COOL.toString() %>")
		coolSetpoint = curTemp;
	else if (mode == "<%= StarsThermoModeSettings.HEAT.toString() %>")
		heatSetpoint = curTemp;
	else
		dftSetpoint = curTemp;
}

var timeoutId = -1;

function setChanged() {
	if (timeoutId != -1) {
		clearTimeout(timeoutId);
		timeoutId = setTimeout("location.reload()", 300000);
	}
}

function init() {
	modeChange('<%= modeStr %>');
	fanChange('<%= fanStr %>');
<% if (curSettings != null) { %>
	timeoutId = setTimeout("location.reload()", 60000);
	document.getElementById("CurrentSettings").style.display = "";
<% } else { %>
	document.getElementById("LastSettings").style.display = "";
<% } %>
}

function incTemp() {
	var curTemp = parseInt(document.MForm.tempField.value,10) + 1;
	if (curTemp <= upperLimit) {
		document.MForm.tempField.value = curTemp;
<% if (curSettings != null) { %>
		setTemp(curTemp);
<% } %>
		setChanged();
	}
}

function decTemp() {
	var curTemp = parseInt(document.MForm.tempField.value,10) - 1;
	if (curTemp >= lowerLimit) {
		document.MForm.tempField.value = curTemp;
<% if (curSettings != null) { %>
		setTemp(curTemp);
<% } %>
		setChanged();
	}
}

function validateTemp() {
	var curTemp = parseInt(document.MForm.tempField.value,10);
	if (isNaN(curTemp))
		curTemp = 72;
	else if (curTemp < lowerLimit)
		curTemp = lowerLimit;
	else if (curTemp > upperLimit)
		curTemp = upperLimit;
	document.MForm.tempField.value = curTemp;
<% if (curSettings != null) { %>
	setTemp(curTemp);
<% } %>
}

function modeChange(mode) {
	if (mode == "") return;
	var disableFlag = false;
	
	if (document.getElementById(mode).style.visibility == "hidden") {
		if (document.MForm.mode.value != "")
			document.getElementById(document.MForm.mode.value).style.visibility = "hidden";
		document.getElementById(mode).style.visibility = 'visible';
		document.MForm.mode.value = mode;
		
		if (mode == '<%= StarsThermoModeSettings.COOL.toString() %>') {
			document.MForm.tempField.style.color = "#003399";
		}
		else if (mode == '<%= StarsThermoModeSettings.HEAT.toString() %>') {
			document.MForm.tempField.style.color = "#FF0000";
		}
		else {
			document.MForm.tempField.style.color = "#CCCCCC";
			disableFlag = true;
		}
	}
	else {
		document.getElementById(mode).style.visibility = "hidden";
		document.MForm.mode.value = "";
		document.MForm.tempField.style.color = "#003366";
	}
	
	document.MForm.tempField.readOnly = disableFlag;
	document.getElementById("IncTemp").disabled = disableFlag;
	document.getElementById("DecTemp").disabled = disableFlag;

	setChanged();
}

function fanChange(fan) {
	if (fan == "") return;
	
	if (document.getElementById(fan).style.visibility == "hidden") {
		if (document.MForm.fan.value != "")
			document.getElementById(document.MForm.fan.value).style.visibility = "hidden";
		document.getElementById(fan).style.visibility = 'visible';
		document.MForm.fan.value = fan;
	}
	else {
		document.getElementById(fan).style.visibility = "hidden";
		document.MForm.fan.value = "";
	}
	
	setChanged();
}

function submitIt() {
	prepareSubmit();
	document.MForm.submit();
}

function prepareSubmit() {
<% if (curSettings != null) { %>
	document.getElementById("PromptMsg").style.display = "";
<% } %>
}
//-->
</script>

			  <div id="PromptMsg" align="center" class="ConfirmMsg" style="display:none">Sending 
			    thermostat settings to gateway, please wait...</div>
			  
              <div align = "left"> 
                <table border="0" width="93%">
                  <tr> 
                    <td width="25">&nbsp;</td>
<%
	String scheduleStr = (isOperator)? AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_SCHED, "Schedule") :
			AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule");
%>
                    <td width="576" class="TableCell">Please use the thermostat 
                      below to temporarily change your current settings. To adjust 
                      your thermostat's programming, please click the <%= scheduleStr %> 
                      link in the pop-up menu of the thermostat.</td>
                  </tr>
<% if (allTherm) { %>
                  <tr> 
                    <td width="25">&nbsp;</td>
                    <td width="576" class="MainText" align="right"><a href="AllTherm.jsp" class="Link1">Change 
                      Selected Thermostats</a></td>
                  </tr>
<% } %>
                </table>
              </div>
			  <form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/SOAPClient">
				<input type="hidden" name="action" value="UpdateThermostatOption">
				<input type="hidden" name="InvID" value="<%= invID %>">
<% for (int i = 0; i < invIDs.length; i++) { %>
				<input type="hidden" name="InvIDs" value="<%= invIDs[i] %>">
<% } %>
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?<%= thermNoStr %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?<%= thermNoStr %>">
				<input type="hidden" name="<%= ServletUtils.CONFIRM_ON_MESSAGE_PAGE %>">
				<input type="hidden" name="mode" value="">
				<input type="hidden" name="fan" value="">
				<input type="hidden" name="RunProgram" value="false">
            	<div align = "left">
                  <table width="93%" border="0" cellspacing="0" cellpadding="0" height="246">
                    <tr> 
                      <td width="73%" height="40" background="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/Bkgd.gif" style="background-repeat:no-repeat"> 
                        <table width="91%" border="0" height="100">
                          <tr> 
                            <td width="38%" height="113">&nbsp;</td>
                            <td width="27%" height="113" align = "center" valign="bottom"> 
                              <table width="18%" border="0" cellspacing = "0" cellpadding ="0" height="60" >
                                <tr> 
                                  <td width="52%" height="53"> 
                                    <input type="text" name="tempField" maxlength="2" class="tempText1" value="<%= setpoint %>" onBlur="validateTemp()" onChange="setChanged()">
                                  </td>
                                  <td width="48%" height="53"> 
                                    <table width="41%" border="0" cellspacing = "0" cellpadding = "0">
                                      <tr> 
                                        <td><img class="Clickable" id="IncTemp" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/UpBk.gif" width="21" height="21" onclick = "incTemp()"></td>
                                      </tr>
                                      <tr> 
                                        <td><img class="Clickable" id="DecTemp" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/DownBk.gif" width="21" height="21" onclick = "decTemp()"></td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                              <table width="79%" border="0" cellpadding = "0" cellspacing = "0">
                                <tr> 
                                  <td>
                                    <input type="checkbox" name="hold" value="true" <%= (hold)? "checked" : "" %> onclick="setChanged()">
                                    <img src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/Hold.gif" width="34" height="9"></td>
                                </tr>
                              </table>
                            </td>
                            <td width="35%" height="113" valign="bottom"><br>
                              <table width="130" height="80" border="0">
                                <tr> 
                                  <td class="TableCell" valign="top" bordercolor="#FFFFFF"> 
<% if (curSettings != null) { %>
                                    <div id="CurrentSettings" style="display:none"> 
<%
	String unit = "F";
	if (curSettings.getDisplayedTempUnit() != null)
		unit = curSettings.getDisplayedTempUnit().substring(0,1);
	String displayTemp = "(Unknown)";
	if (curSettings.hasDisplayedTemperature())
		displayTemp = curSettings.getDisplayedTemperature() + "&deg;" + unit;
%>
                                      <span class="TitleHeader">Room: <%= displayTemp %></span><br>
<%
	for (int i = 0; i < curSettings.getInfoStringCount(); i++) {
		String infoString = (String) curSettings.getInfoString(i);
%>
                                      <%= infoString %><br>
<%	} %>
                                    </div>
<% } %>
                                    <div id="LastSettings" style="display:none"> 
                                      <b>Last Settings:</b><br>
<% if (lastEvent == null) { %>
                                      (None) 
<% } else { %>
                                      Time: <%= histDateFormat.format(lastEvent.getEventDateTime()) %><br>
<%	if (runProgram) { %>
                                      Run Program 
<%	} else { %>
                                      Temp: <%= setpoint %>&deg; 
                                      <% if (hold) { %>(HOLD)<% } %><br>
                                      Mode: <%= modeStr %><br>
                                      Fan: <%= fanStr %> 
<%	} %>
<% } %>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <table width="91%" border="0" height="100">
                          <tr> 
                            <td width="31"></td>
                            <td width="133" valign="bottom"><img class="Clickable" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/Run.gif" onClick="document.MForm.RunProgram.value='true';submitIt()"></td>
                            <td width="124"> 
                              <table width="100%" border="0" height="56" cellspacing = "0" cellpadding = "0">
                                <tr> 
                                  <td width="60%"  height="59" valign = "bottom"> 
                                    <table width="35%" border="0" cellpadding = "2" cellspacing = "0" height="41">
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.COOL.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.COOL.toString() %>" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/Cool.gif"></td>
                                      </tr>
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.HEAT.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.HEAT.toString() %>" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/Heat.gif"></td>
                                      </tr>
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.OFF.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.OFF.toString() %>" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/Off.gif"></td>
                                      </tr>
                                    </table>
                                  </td>
                                  <td width="40%" height="59" valign = "bottom"> 
                                    <table width="34%" border="0" cellpadding = "2" cellspacing = "0">
                                      <tr onClick="fanChange('<%= StarsThermoFanSettings.AUTO.toString() %>')"> 
                                        <td ><img id="<%= StarsThermoFanSettings.AUTO.toString() %>" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/Auto.gif"></td>
                                      </tr>
                                      <tr onClick="fanChange('<%= StarsThermoFanSettings.ON.toString() %>')"> 
                                        <td><img id="<%= StarsThermoFanSettings.ON.toString() %>" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/On.gif"></td>
                                      </tr>
                                      <tr> 
                                        <td height="14"></td>
                                        <td></td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                            </td>
                            <td width="100" valign = "middle" align = "center"> 
                              <div align="left"></div>
                              <table width="84%" border="0" height="45">
                                <tr> 
                                  <td><img class="Clickable" src="<%=request.getContextPath()%>/WebConfig/yukon/ThermImages/Submit.gif" onClick = "submitIt()"></td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                      <td width="27%" height="40" class="TableCell" valign="top"> 
<% if (curSettings != null) { %>
<%	if (!ServletUtils.isGatewayTimeout(curSettings.getLastUpdatedTime())) { %>
                        <p class="TitleHeader">Last Updated Time: <br>
                          <%= histDateFormat.format(curSettings.getLastUpdatedTime()) %></p>
<%	} else { %>
                        <p class="ErrorMsg">Last Updated Time: 
						  <%= (curSettings.getLastUpdatedTime() != null)? "<br>" + histDateFormat.format(curSettings.getLastUpdatedTime()) : "N/A" %></p>
<%	} %>
<% } %>
                        <p><b>1)</b> Select the new temperature to maintain until 
                          the next program scheduled change. Check <b>HOLD</b> 
                          to maintain this setting across program changes. <br>
                          <b>2)</b> Adjust <b>MODE</b> and <b>FAN</b> settings. 
                          <br>
                          <b>3)</b> Click <b>SUBMIT</b>.</p>
                        <p><b><i>or</i></b><br>
                          Click <b>RUN PROGRAM</b> to revert to your thermostat 
                          program. </p>
                        <p>&nbsp;</p>
                      </td>
                    </tr>
                  </table>
                </div>
              </form>
