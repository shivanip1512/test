<%
/* Required predefined variables:
 * thermoSettings: StarsThermostatSettings
 * invID: int
 * invIDs: int[]
 * allTherm: boolean
 * thermNoStr: String
 */
	boolean isOperator = ServerUtils.isOperator(user);
	
	StarsThermostatManualEvent lastEvent = null;
	int lastTemp = 72;
	String modeStr = "";
	String fanStr = "";
	boolean runProgram = false;
	
	if (thermoSettings != null && thermoSettings.getStarsThermostatManualEventCount() > 0) {
		lastEvent = thermoSettings.getStarsThermostatManualEvent(
				thermoSettings.getStarsThermostatManualEventCount() - 1);
		
		StarsThermoModeSettings mode = lastEvent.getThermostatManualOption().getMode();
		if (mode != null) modeStr = mode.toString();
		StarsThermoFanSettings fan = lastEvent.getThermostatManualOption().getFan();
		if (fan != null) fanStr = fan.toString();
		runProgram = (lastEvent.getThermostatManualOption().getTemperature() == -1);
		if (!runProgram) lastTemp = lastEvent.getThermostatManualOption().getTemperature();
	}
	
%>

<script language="JavaScript" src ="<%= request.getContextPath() %>/JavaScript/nav_menu.js">
</script>
<script langauge = "JavaScript">
<!-- 
function checkTemp(n, type) {
	if (n.value >10)
		confirm("Are you sure you would like to " + type + " the temperature " + n.value + " degrees?");
}

function incTemp() {
	var curTemp = parseInt(document.MForm.tempField.value,10) + 1;
	if (curTemp <= 88)
		document.MForm.tempField.value = curTemp;
}

function decTemp() {
	var curTemp = parseInt(document.MForm.tempField.value,10) - 1;
	if (curTemp >= 45)
	document.MForm.tempField.value = curTemp;
}

function holdTemp() {
	if (document.MForm.holdSetting.value == "off") {
		document.MForm.hold.src = "<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/HoldGray.gif";
		document.MForm.holdSetting.value = "on";
		document.MForm.resetSetting.value = "on";
		document.MForm.rst.src = "<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ResetBk.gif";
		document.MForm.msg.src = "<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/HoldMsg.gif";
	}
}

function resetTemp() {
	if ( document.MForm.resetSetting.value == "on") {
		document.MForm.hold.src = "<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/HoldBk.gif";
		document.MForm.holdSetting.value = "off";
		document.MForm.resetSetting.value = "off";
		document.MForm.rst.src = "<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ResetGray.gif";
		document.MForm.msg.src = "<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/SchedMsg.gif";
	}
}

function modeChange(mode) {
	if (document.getElementById(mode).style.visibility == "hidden") {
		if (document.MForm.mode.value != "")
			document.getElementById(document.MForm.mode.value).style.visibility = "hidden";
		document.getElementById(mode).style.visibility = 'visible';
		document.MForm.mode.value = mode;
		
		if(mode == '<%= StarsThermoModeSettings.HEAT.toString() %>')
			document.MForm.tempField.style.color = "#FF0000";
		else if (mode == '<%= StarsThermoModeSettings.COOL.toString() %>')
			document.MForm.tempField.style.color = "#003399";
		else if (mode == '<%= StarsThermoModeSettings.OFF.toString() %>')
			document.MForm.tempField.style.color = "#CCCCCC";
	}
	else {
		document.getElementById(mode).style.visibility = "hidden";
		document.MForm.mode.value = "";
		document.MForm.tempField.style.color = "#003366";
	}
}

function fanChange(fan) {
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
}

function submitIt() {
	document.MForm.submit();
}

function validateTemp(e) {
	var key;
	var val;
	if (browser.isNetscape)
		key = String.fromCharCode(e.which);
	else if (browser.isMicrosoft)
		key = String.fromCharCode(e.keyCode);
	
	var text = document.MForm.tempField.value + key;
	if (text.length == 2) {
		if (Number(key) || Number(key) == 0){
			val = parseInt(document.MForm.tempField.value + key,10);
			
			if (val < 45)
				document.MForm.tempField.value = 45;
			else if (val > 88)
				document.MForm.tempField.value = 88;
		}
		else {
			document.MForm.tempField.value = 72;
		}
	}
}
//-->
</script>

              <div align = "left"> 
                <table border="0" width="93%">
                  <tr> 
                    <td width="25">&nbsp;</td>
<%
	String scheduleStr = (isOperator)? AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_SCHED, "Schedule") :
			AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule");
%>
                    <td width="" class="TableCell">Please use the thermostat 
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
				<input type="hidden" name="mode" value="">
				<input type="hidden" name="fan" value="">
				<input type="hidden" name="RunProgram" value="false">
                <div align = "left">
				  <table width="93%" border="0" background="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/Bkgd.gif" style = "background-repeat:no-repeat" cellspacing = "0" cellpadding = "0" height="246">
                    <tr> 
                      <td width="73%" height="40" > 
                        <table width="91%" border="0" height="100">
                          <tr> 
                            <td width="38%" height="113">&nbsp;</td>
                            <td width="31%" height="113" align = "center" valign="bottom"> 
                              <table width="18%" border="0" cellspacing = "0" cellpadding ="0" height="60" >
                                <tr> 
                                  <td width="52%" height="53"> 
                                    <input type="text" name="tempField" maxlength="2" class="tempText1" value="<%= lastTemp %>" onkeypress="validateTemp(event)">
                                  </td>
                                  <td width="48%" height="53"> 
                                    <table width="41%" border="0" cellspacing = "0" cellpadding = "0">
                                      <tr> 
                                        <td><img class="Clickable" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/UpBk.gif" width="21" height="21" onclick = "incTemp()"></td>
                                      </tr>
                                      <tr> 
                                        <td><img class="Clickable" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/DownBk.gif" width="21" height="21" onclick = "decTemp()"></td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                              <table width="79%" border="0" cellpadding = "0" cellspacing = "0">
                                <tr> 
                                  <td>
                                    <input type="checkbox" name="hold" value="true">
                                    <img src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/Hold.gif" width="34" height="9"></td>
                                </tr>
                              </table>
                            </td>
                            <td width="31%" height="113" valign="bottom"><br>
                              <table width="100" height="80" border="0">
                                <tr>
                                  <td class="TableCell" valign="top" bordercolor="#FFFFFF"><b>Last 
                                    Settings:</b><br>
<% if (lastEvent == null) { %>
									(None)
<% } else { %>
									Date: <%= datePart.format(lastEvent.getEventDateTime()) %><br>
<%	if (runProgram) { %>
									Run Program
<%	} else { %>
									Temp: <%= lastTemp %>&deg 
									<% if (lastEvent.getThermostatManualOption().getHold()) { %>(Hold)<% } %><br>
                                    Mode: <%= modeStr %><br>
                                    Fan: <%= fanStr %>
<%	} %>
<% } %>
								  </td>
                                </tr>
                              </table>
                              
                            </td>
                          </tr>
                        </table>
                        <table width="91%" border="0" height="100">
                          <tr> 
                            <td width="31"></td>
                            <td width="133" valign="bottom"><img class="Clickable" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/Run.gif" onClick="document.MForm.RunProgram.value='true';submitIt()"></td>
                            <td width="124"> 
                              <table width="100%" border="0" height="56" cellspacing = "0" cellpadding = "0">
                                <tr> 
                                  <td width="60%"  height="59" valign = "bottom"> 
                                    <table width="35%" border="0" cellpadding = "2" cellspacing = "0" height="41">
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.COOL.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.COOL.toString() %>" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/Cool.gif"></td>
                                      </tr>
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.HEAT.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.HEAT.toString() %>" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/Heat.gif"></td>
                                      </tr>
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.OFF.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.OFF.toString() %>" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/Off.gif"></td>
                                      </tr>
                                    </table>
                                  </td>
                                  <td width="40%" height="59" valign = "bottom"> 
                                    <table width="34%" border="0" cellpadding = "2" cellspacing = "0">
                                      <tr onClick="fanChange('<%= StarsThermoFanSettings.AUTO.toString() %>')"> 
                                        <td ><img id="<%= StarsThermoFanSettings.AUTO.toString() %>" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/Auto.gif"></td>
                                      </tr>
                                      <tr onClick="fanChange('<%= StarsThermoFanSettings.ON.toString() %>')"> 
                                        <td><img id="<%= StarsThermoFanSettings.ON.toString() %>" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/On.gif"></td>
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
                                  <td><img class="Clickable" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/Submit.gif" onClick = "submitIt()"></td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                      <td width="27%" height="40" class="TableCell" valign="top"> 
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
