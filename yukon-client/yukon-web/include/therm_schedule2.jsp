<%
/* Required predefined variables:
 * thermoProgram: StarsThermostatProgram
 * curSettings: StarsThermostatDynamicData
 * invID: int
 * invIDs: int[]
 * allTherm: boolean
 * thermNoStr: String
 */
	boolean isOperator = ServerUtils.isOperator(user);
	boolean isRecommended = (invID < 0);

	StarsThermostatProgram dftThermoProgram = null;
	if (!isRecommended) {
		for (int i = 0; i < dftThermoSchedules.getStarsThermostatProgramCount(); i++) {
			if (dftThermoSchedules.getStarsThermostatProgram(i).getThermostatType().getType() == StarsThermostatTypes.ENERGYPRO_TYPE) {
				dftThermoProgram = dftThermoSchedules.getStarsThermostatProgram(i);
				break;
			}
		}
	}
	else {
		dftThermoProgram = SOAPServer.getDefaultEnergyCompany().getStarsDefaultThermostatSchedules().getStarsThermostatProgram(0);
	}

	StarsThermoDaySettings daySetting = null;
	if (request.getParameter("day") != null)
		daySetting = StarsThermoDaySettings.valueOf(request.getParameter("day"));
	if (daySetting == null) {
		if (!isRecommended)
			daySetting = ServletUtils.getCurrentDay();
		else
			daySetting = StarsThermoDaySettings.MONDAY;
	}
	
	StarsThermoModeSettings modeSetting = null;
	if (request.getParameter("mode") != null)
		modeSetting = StarsThermoModeSettings.valueOf(request.getParameter("mode"));
	if (modeSetting == null) {
		if (!isRecommended && curSettings != null)
			modeSetting = curSettings.getMode();
		if (modeSetting == null)
			modeSetting = StarsThermoModeSettings.COOL;
	}
	
	boolean isCooling = modeSetting.getType() == StarsThermoModeSettings.COOL_TYPE;
	String visibleC = isCooling ? "visible" : "hidden";
	String visibleH = isCooling ? "hidden" : "visible";
	
	StarsThermostatSchedule coolSched = null;
	StarsThermostatSchedule heatSched = null;
	StarsThermostatSchedule schedule = null;
	StarsThermostatSchedule dftCoolSched = null;
	StarsThermostatSchedule dftHeatSched = null;
	StarsThermostatSchedule dftSchedule = null;
	
	if (thermoProgram != null) {
		for (int i = 0; i < thermoProgram.getStarsThermostatSeasonCount(); i++) {
			StarsThermostatSeason season = thermoProgram.getStarsThermostatSeason(i);
			if (season.getMode().getType() == StarsThermoModeSettings.COOL_TYPE) {
				for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
					if (season.getStarsThermostatSchedule(j).getDay().getType() == daySetting.getType()) {
						coolSched = season.getStarsThermostatSchedule(j);
						if (isCooling) schedule = coolSched;
						break;
					}
				}
			}
			else {
				for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
					if (season.getStarsThermostatSchedule(j).getDay().getType() == daySetting.getType()) {
						heatSched = season.getStarsThermostatSchedule(j);
						if (!isCooling) schedule = heatSched;
						break;
					}
				}
			}
		}
	}
	
	if (dftThermoProgram != null) {
		for (int i = 0; i < dftThermoProgram.getStarsThermostatSeasonCount(); i++) {
			StarsThermostatSeason season = dftThermoProgram.getStarsThermostatSeason(i);
			if (season.getMode().getType() == StarsThermoModeSettings.COOL_TYPE) {
				for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
					if (season.getStarsThermostatSchedule(j).getDay().getType() == daySetting.getType() ||
						ServletUtils.isWeekday(daySetting) && season.getStarsThermostatSchedule(j).getDay().getType() == StarsThermoDaySettings.WEEKDAY_TYPE)
					{
						dftCoolSched = season.getStarsThermostatSchedule(j);
						if (coolSched == null) coolSched = dftCoolSched;
						if (isCooling) dftSchedule = dftCoolSched;
						break;
					}
				}
			}
			else {
				for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
					if (season.getStarsThermostatSchedule(j).getDay().getType() == daySetting.getType() ||
						ServletUtils.isWeekday(daySetting) && season.getStarsThermostatSchedule(j).getDay().getType() == StarsThermoDaySettings.WEEKDAY_TYPE)
					{
						dftHeatSched = season.getStarsThermostatSchedule(j);
						if (heatSched == null) heatSched = dftHeatSched;
						if (!isCooling) dftSchedule = dftHeatSched;
						break;
					}
				}
			}
		}
		if (schedule == null) schedule = dftSchedule;
	}
	
	char tempUnit = 'F';
	if (curSettings != null && curSettings.getDisplayedTempUnit() != null)
		tempUnit = curSettings.getDisplayedTempUnit().charAt(0);
%>

<script language="JavaScript" src ="<%= request.getContextPath() %>/JavaScript/nav_menu.js">
</script>
<script language="JavaScript" src ="<%= request.getContextPath() %>/JavaScript/drag.js">
</script>
<script language="JavaScript" src ="<%= request.getContextPath() %>/JavaScript/thermostat2.js">
</script>
<script language = "JavaScript">
// Set global variable in thermostat2.js
thermMode = '<%= isCooling ? "C" : "H" %>';
tempUnit = '<%= tempUnit %>';
<%	if (curSettings != null) {
		if (curSettings.getLowerCoolSetpointLimit() > 0) {
%>
	lowerLimit = <%= curSettings.getLowerCoolSetpointLimit() %>;
<%		}
		if (curSettings.getUpperHeatSetpointLimit() > 0) {
%>
	upperLimit = <%= curSettings.getUpperHeatSetpointLimit() %>;
<%		}
	}
%>

function updateLayout(hour1, min1, temp1C, temp1H, hour2, min2, temp2C, temp2H, hour3, min3, temp3C, temp3H, hour4, min4, temp4C, temp4H) {
	moveLayer(1, hour1, min1);
	moveTempArrows(1, temp1C, temp1H);
	showTemp(1);
	moveLayer(2, hour2, min2);
	moveTempArrows(2, temp2C, temp2H);
	showTemp(2);
	moveLayer(3, hour3, min3);
	moveTempArrows(3, temp3C, temp3H);
	showTemp(3);
	moveLayer(4, hour4, min4);
	moveTempArrows(4, temp4C, temp4H);
	showTemp(4);
}

var changed = false;
var timeoutId = -1;

function setChanged() {
	changed = true;
	if (timeoutId != -1) {
		clearTimeout(timeoutId);
		timeoutId = setTimeout("location.reload()", 300000);
	}
}

function prepareSubmit(form) {
	form.tempval1.value = document.getElementById('temp1').innerHTML.substr(0,2);
	form.tempval2.value = document.getElementById('temp2').innerHTML.substr(0,2);
	form.tempval3.value = document.getElementById('temp3').innerHTML.substr(0,2);
	form.tempval4.value = document.getElementById('temp4').innerHTML.substr(0,2);
	changed = false;
<%	if (curSettings != null) { %>
	document.getElementById("PromptMsg").style.display = "";
<%	} %>
}

function switchSettings(day, mode) {
	var form = document.form1;
	form.REDIRECT.value = "<%= request.getRequestURI() %>?<%= thermNoStr %>&day=" + day + "&mode=" + mode;
	if (changed && confirm('You have made changes to the thermostat schedule. Click "Ok" to submit these changes before leaving the page, or click "Cancel" to discard them.'))
	{
		var form = document.form1;
		prepareSubmit(form);
		form.submit();
		return;
	}
	location.href = form.REDIRECT.value;
}

function setToDefault() {
	var form = document.form1;
	form.time1.value = "<%= ampmTimeFormat.format(dftSchedule.getTime1().toDate()) %>";
	form.time2.value = "<%= ampmTimeFormat.format(dftSchedule.getTime2().toDate()) %>";
	form.time3.value = "<%= ampmTimeFormat.format(dftSchedule.getTime3().toDate()) %>";
	form.time4.value = "<%= ampmTimeFormat.format(dftSchedule.getTime4().toDate()) %>";
<%
	boolean dftSkip1 = (dftSchedule.getTemperature1() == -1);
	boolean dftSkip2 = (dftSchedule.getTemperature2() == -1);
	boolean dftSkip3 = (dftSchedule.getTemperature3() == -1);
	boolean dftSkip4 = (dftSchedule.getTemperature4() == -1);
	int dftTemp1 = (dftSkip1)? 72 : dftSchedule.getTemperature1();
	int dftTemp2 = (dftSkip2)? 72 : dftSchedule.getTemperature2();
	int dftTemp3 = (dftSkip3)? 72 : dftSchedule.getTemperature3();
	int dftTemp4 = (dftSkip4)? 72 : dftSchedule.getTemperature4();
%>	
	var temp1C, temp1H, temp2C, temp2H, temp3C, temp3H, temp4C, temp4H;
	if (<%= isCooling %>) {
		temp1C = <%= dftTemp1 %>;
		temp2C = <%= dftTemp2 %>;
		temp3C = <%= dftTemp3 %>;
		temp4C = <%= dftTemp4 %>;
		temp1H = temp2H = temp3H = temp4H = null;
	}
	else {
		temp1H = <%= dftTemp1 %>;
		temp2H = <%= dftTemp2 %>;
		temp3H = <%= dftTemp3 %>;
		temp4H = <%= dftTemp4 %>;
		temp1C = temp2C = temp3C = temp4C = null;
	}
	updateLayout(
		<%= dftSchedule.getTime1().getHour() %>,<%= dftSchedule.getTime1().getMinute() %>,temp1C,temp1H,
		<%= dftSchedule.getTime2().getHour() %>,<%= dftSchedule.getTime2().getMinute() %>,temp2C,temp2H,
		<%= dftSchedule.getTime3().getHour() %>,<%= dftSchedule.getTime3().getMinute() %>,temp3C,temp3H,
		<%= dftSchedule.getTime4().getHour() %>,<%= dftSchedule.getTime4().getMinute() %>,temp4C,temp4H
	);
	
	toggleThermostat(1, true);
	toggleThermostat(2, <%= !dftSkip2 %>);
	toggleThermostat(3, <%= !dftSkip3 %>);
	toggleThermostat(4, <%= !dftSkip4 %>);
	setChanged();
}

function init() {
<%	
	boolean skip1 = (schedule.getTemperature1() == -1);
	if (skip1) schedule.setTime1(dftSchedule.getTime1());
	int ct1 = (skip1)? (dftSkip1? 72:dftCoolSched.getTemperature1()) : coolSched.getTemperature1();
	int ht1 = (skip1)? (dftSkip1? 72:dftHeatSched.getTemperature1()) : heatSched.getTemperature1();
	boolean skip2 = (schedule.getTemperature2() == -1);
	if (skip2) schedule.setTime2(dftSchedule.getTime2());
	int ct2 = (skip2)? (dftSkip2? 72:dftCoolSched.getTemperature2()) : coolSched.getTemperature2();
	int ht2 = (skip2)? (dftSkip2? 72:dftHeatSched.getTemperature2()) : heatSched.getTemperature2();
	boolean skip3 = (schedule.getTemperature3() == -1);
	if (skip3) schedule.setTime3(dftSchedule.getTime3());
	int ct3 = (skip3)? (dftSkip3? 72:dftCoolSched.getTemperature3()) : coolSched.getTemperature3();
	int ht3 = (skip3)? (dftSkip3? 72:dftHeatSched.getTemperature3()) : heatSched.getTemperature3();
	boolean skip4 = (schedule.getTemperature4() == -1);
	if (skip4) schedule.setTime4(dftSchedule.getTime4());
	int ct4 = (skip4)? (dftSkip4? 72:dftCoolSched.getTemperature4()) : coolSched.getTemperature4();
	int ht4 = (skip4)? (dftSkip4? 72:dftHeatSched.getTemperature4()) : heatSched.getTemperature4();
%>
	updateLayout(
		<%= schedule.getTime1().getHour() %>,<%= schedule.getTime1().getMinute() %>,<%= ct1 %>,<%= ht1 %>,
		<%= schedule.getTime2().getHour() %>,<%= schedule.getTime2().getMinute() %>,<%= ct2 %>,<%= ht2 %>,
		<%= schedule.getTime3().getHour() %>,<%= schedule.getTime3().getMinute() %>,<%= ct3 %>,<%= ht3 %>,
		<%= schedule.getTime4().getHour() %>,<%= schedule.getTime4().getMinute() %>,<%= ct4 %>,<%= ht4 %>
	);
	
	// The wake time cannot be turned off
	toggleThermostat(1, true);
	toggleThermostat(2, <%= !skip2 %>);
	toggleThermostat(3, <%= !skip3 %>);
	toggleThermostat(4, <%= !skip4 %>);

<% if (isOperator) { %>
	document.getElementById('Default').value = '<cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_RECOMMENDED_SETTINGS_BUTTON %>"/>';
<% } else { %>
	document.getElementById('Default').value = '<cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_RECOMMENDED_SETTINGS_BUTTON %>"/>';
<% } %>

<% if (curSettings != null) { %>
	timeoutId = setTimeout("location.reload()", 60000);
<% } %>
}
</script>

			<div id="PromptMsg" class="ConfirmMsg" style="display:none">Sending 
			  thermostat settings to gateway, please wait...</div>
			
            <form name="form1" method="POST" action="<%=request.getContextPath()%>/servlet/<%= (isRecommended)?"StarsAdmin":"SOAPClient" %>" onsubmit="prepareSubmit(this)">
			  <input type="hidden" name="action" value="UpdateThermostatSchedule">
			  <input type="hidden" name="InvID" value="<%= invID %>">
<% for (int i = 0; i < invIDs.length; i++) { %>
			  <input type="hidden" name="InvIDs" value="<%= invIDs[i] %>">
<% } %>
			  <input type="hidden" name="type" value="<%= StarsThermostatTypes.ENERGYPRO.toString() %>">
			  <input type="hidden" name="day" value="<%= daySetting.toString() %>">
			  <input type="hidden" name="mode" value="<%= modeSetting.toString() %>">
			  <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?<%= thermNoStr %>&day=<%= daySetting.toString() %>&mode=<%= modeSetting.toString() %>">
			  <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?<%= thermNoStr %>&day=<%= daySetting.toString() %>&mode=<%= modeSetting.toString() %>">
			  <input type="hidden" name="tempval1">
			  <input type="hidden" name="tempval2">
			  <input type="hidden" name="tempval3">
			  <input type="hidden" name="tempval4">
              <table width="80%" border="0" cellspacing="0" cellpadding="0">
                <tr>
<% if (allTherm) { %>
                    <td align="right" class="MainText"><a href="AllTherm.jsp" class="Link1">Change 
                      Selected Thermostats</a></td>
<% } else if (curSettings != null) {
		if (!ServletUtils.isGatewayTimeout(curSettings.getLastUpdatedTime())) {
%>
                  <td align="right" class="TitleHeader">Last Updated Time: <%= histDateFormat.format(curSettings.getLastUpdatedTime()) %></td>
<%	} else { %>
                  <td align="right" class="ErrorMsg">Last Updated Time: <%= (curSettings.getLastUpdatedTime() != null)? histDateFormat.format(curSettings.getLastUpdatedTime()) : "N/A" %></td>
<%	}
	}
%>
                </tr>
              </table>
              <table width="80%" border="1" cellspacing = "0" cellpadding = "2">
                <tr> 
                    <td align = "center"  valign = "bottom" class = "Background"> 
                      <table width="478" border="0" height="8" valign = "bottom" cellpadding="0" cellspacing="0" >
                        <tr> 
                          <td class = "TableCell1" align = "left" width="50%"> 
                            <% if (daySetting.getType() == StarsThermoDaySettings.MONDAY_TYPE) { %>
                            <b><span class="Header2">Mon</span></b> 
                            <% } else { %>
                            <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.MONDAY.toString() %>', '<%= modeSetting.toString() %>')">Mon</span> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.TUESDAY_TYPE) { %>
                            <b><span class="Header2">Tue</span></b> 
                            <% } else { %>
                            <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.TUESDAY.toString() %>', '<%= modeSetting.toString() %>')">Tue</span> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.WEDNESDAY_TYPE) { %>
                            <b><span class="Header2">Wed</span></b> 
                            <% } else { %>
                            <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.WEDNESDAY.toString() %>', '<%= modeSetting.toString() %>')">Wed</span> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.THURSDAY_TYPE) { %>
                            <b><span class="Header2">Thu</span></b> 
                            <% } else { %>
                            <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.THURSDAY.toString() %>', '<%= modeSetting.toString() %>')">Thu</span> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.FRIDAY_TYPE) { %>
                            <b><span class="Header2">Fri</span></b> 
                            <% } else { %>
                            <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.FRIDAY.toString() %>', '<%= modeSetting.toString() %>')">Fri</span> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.SATURDAY_TYPE) { %>
                            <b><span class="Header2">Sat</span> </b> 
                            <% } else { %>
                            <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.SATURDAY.toString() %>', '<%= modeSetting.toString() %>')">Sat</span> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.SUNDAY_TYPE) { %>
                            <b><span class="Header2">Sun</span></b> 
                            <% } else { %>
                            <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.SUNDAY.toString() %>', '<%= modeSetting.toString() %>')">Sun</span> 
                            <% } %>
                          <td class = "Background" align = "right" width="50%"> 
<%
	String weekdaysChecked = (String) session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_APPLY_TO_WEEKDAYS);
	if (weekdaysChecked == null || isRecommended) weekdaysChecked = "";
	String weekendChecked = (String) session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_APPLY_TO_WEEKEND);
	if (weekendChecked == null || isRecommended) weekendChecked = "";
%>
                            <input type="checkbox" name="ApplyToWeekdays" value="true" <%= weekdaysChecked %>>
                            <span class="TableCell1">Apply to weekdays </span> 
                            <input type="checkbox" name="ApplyToWeekend" value="true" <%= weekendChecked %>>
                            <span class="TableCell1">Apply to weekends </span> 
						  </td>
                        </tr>
                      </table>
                    </td>
                </tr>
                <tr> 
                    <td align = "center"> 
<% if (!isRecommended) { %>
                      <table width="478" border="0">
                        <tr> 
                          <td class = "TableCell" width="71%" height="4" align = "center" valign="middle" > 
                            <div align="left"> 
                              1) Select Cooling or Heating.<br>
                              2) Slide thermometers to change start times.<br>
                              3) Adjust your cooling or heating temperatures.<br>
<%
	String instrLink = (isOperator)? AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LINK_THERM_INSTRUCTIONS) :
			AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LINK_THERM_INSTRUCTIONS);
	String target = "target='instructions'";
	if (ServerUtils.forceNotNone(instrLink).length() == 0) {
		instrLink = "Instructions.jsp";
		target = "";
	}
	String manualLink = "Thermostat2.jsp?" + thermNoStr;
%>
                              <a class="Link1" href="<%= instrLink %>" <%= target %>>Click 
                              for hints and details</a>.
                            </div>
                          </td>
                          <td class = "TableCell" width="29%" height="4" align = "left" valign="top" > 
                            <i>Make temporary adjustments to your heating and 
                            cooling system<a class="Link1" href="<%= manualLink %>"> 
                            here</a>.</i> </td>
                        </tr>
                      </table>
<% } %>
                      <table width="175" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td width="68"> 
                            <div align="right"><span class="TableCell"><font color="#003366">Cooling</font> 
                              <img src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/BlueArrow.gif"> 
                              </span> </div>
                          </td>
                          <td width="31"><span class="TableCell"> 
                            <input type="radio" name="radiobutton" value="radiobutton" <% if (isCooling) { %>checked<% } else { %>onclick="switchSettings('<%= daySetting.toString() %>', '<%= StarsThermoModeSettings.COOL.toString() %>')"<% } %>>
                            </span></td>
						  <td width="20"> 
                            <input type="radio" name="radiobutton" value="radiobutton" <% if (!isCooling) { %>checked<% } else { %>onclick="switchSettings('<%= daySetting.toString() %>', '<%= StarsThermoModeSettings.HEAT.toString() %>')"<% } %>>
                          </td>
                          <td width="56"><img src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/RedArrow.gif"> 
                            <span class="TableCell"><font color="FF0000">Heating</font></span></td>
                        </tr>
                      </table>
                      <table width="478" height="186" background="../../../WebConfig/yukon/ThermImages/TempBG2.gif" style="background-repeat: no-repeat" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td width="50">
                            <div id="MovingLayer1" style="position:relative; width:30px; height:162px; left:0px; z-index:1; top:5px; display:none" onMouseDown="beginDrag(event,0,0,getRightBound(1),getLeftBound(1),'showTimeWake()','horizontal','MovingLayer1');setChanged();">
                              <table border="0">
                                <tr align="center"> 
                                  <td colspan="2"> 
                                    <div id="temp1" class="TableCell2" onChange="setChanged()"><%= schedule.getTemperature1() %>&deg;<%= tempUnit %></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td align="center" colspan="2"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ThermW.gif" width="16"> 
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="50%"> 
                                    <div id="div1C" align="left" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow1C" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/BlueArrow.gif" <% if (isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(1)','vertical','div1C');setChanged()"<% } %> style="visibility:<%= visibleC %>"><br>
                                      <img id="arrow1C_Gray" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/GrayArrowL.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleH %>"> 
                                    </div>
                                  </td>
                                  <td width="50%"> 
                                    <div id="div1H" align="right" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow1H" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/RedArrow.gif" <% if (!isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(1)','vertical','div1H');setChanged()"<% } %> style="visibility:<%= visibleH %>"><br>
                                      <img id="arrow1H_Gray" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/GrayArrowR.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleC %>"> 
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </td>
                          <td width="50">
                            <div id="MovingLayer2" style="position:relative; width:30px; height:162px; left:0px; z-index:2; top:5px; display:none" onMouseDown="beginDrag(event,0,0,getRightBound(2),getLeftBound(2),'showTimeLeave()','horizontal','MovingLayer2');setChanged();"> 
                              <table border="0">
                                <tr align="center"> 
                                  <td colspan="2"> 
                                    <div id="temp2" class="TableCell2" onChange="setChanged()"><%= schedule.getTemperature2() %>&deg;<%= tempUnit %></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td align="center" colspan="2"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ThermL.gif" width="16"> 
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="50%"> 
                                    <div id="div2C" align="left" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow2C" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/BlueArrow.gif" <% if (isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(2)','vertical','div2C');setChanged()"<% } %> style="visibility:<%= visibleC %>"><br>
                                      <img id="arrow2C_Gray" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/GrayArrowL.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleH %>"> 
                                    </div>
                                  </td>
                                  <td width="50%"> 
                                    <div id="div2H" align="right" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow2H" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/RedArrow.gif" <% if (!isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(2)','vertical','div2H');setChanged()"<% } %> style="visibility:<%= visibleH %>"><br>
                                      <img id="arrow2H_Gray" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/GrayArrowR.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleC %>"> 
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </td>
                          <td width="50">
                            <div id="MovingLayer3" style="position:relative; width:30px; height:162px; left:0px; z-index:3; top:5px; display:none" onMouseDown="beginDrag(event,0,0,getRightBound(3),getLeftBound(3),'showTimeReturn()','horizontal','MovingLayer3');setChanged();"> 
                              <table border="0">
                                <tr align="center"> 
                                  <td colspan="2"> 
                                    <div id="temp3" class="TableCell2" onChange="setChanged()"><%= schedule.getTemperature3() %>&deg;<%= tempUnit %></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td align="center" colspan="2"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ThermR.gif" width="16" height="131"> 
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="50%"> 
                                    <div id="div3C" align="left" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow3C" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/BlueArrow.gif" <% if (isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(3)','vertical','div3C');setChanged()"<% } %> style="visibility:<%= visibleC %>"><br>
                                      <img id="arrow3C_Gray" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/GrayArrowL.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleH %>"> 
                                    </div>
                                  </td>
                                  <td width="50%"> 
                                    <div id="div3H" align="right" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow3H" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/RedArrow.gif" <% if (!isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(3)','vertical','div3H');setChanged()"<% } %> style="visibility:<%= visibleH %>"><br>
                                      <img id="arrow3H_Gray" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/GrayArrowR.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleC %>"> 
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </td>
                          <td width="50">
                            <div id="MovingLayer4" style="position:relative; width:30px; height:162px; left:0px; z-index:4; top:5px; display:none" onMouseDown="beginDrag(event,0,0,getRightBound(4),getLeftBound(4),'showTimeSleep()','horizontal','MovingLayer4');setChanged();"> 
                              <table border="0">
                                <tr align="center"> 
                                  <td colspan="2"> 
                                    <div id="temp4" class="TableCell2" onChange="setChanged()"><%= schedule.getTemperature4() %>&deg;<%= tempUnit %></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td align="center" colspan="2"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ThermS.gif" width="16" height="131"> 
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="50%"> 
                                    <div id="div4C" align="left" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow4C" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/BlueArrow.gif" <% if (isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(4)','vertical','div4C');setChanged()"<% } %> style="visibility:<%= visibleC %>"><br>
                                      <img id="arrow4C_Gray" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/GrayArrowL.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleH %>"> 
                                    </div>
                                  </td>
                                  <td width="50%"> 
                                    <div id="div4H" align="right" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow4H" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/RedArrow.gif" <% if (!isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(4)','vertical','div4H');setChanged()"<% } %> style="visibility:<%= visibleH %>"><br>
                                      <img id="arrow4H_Gray" src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/GrayArrowR.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleC %>"> 
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </td>
                          <td>&nbsp;</td>
                        </tr>
                      </table>
                      <table width="100%" border="0" height="27">
                        <tr> 
                          <td width="10%" class="MainText"> 
                            <div align="right">
                              <input type="checkbox" id="WakeEnabled" onclick="toggleThermostat(1, this.checked);setChanged();" disabled>
                            </div>
                          </td>
                          <td class = "TitleHeader" align = "left" width="15%"> 
                            Wake (W) </td>
                          <td width="10%" class="MainText">
                            <div align="right">
                              <input type="checkbox" id="LeaveEnabled" onclick="toggleThermostat(2, this.checked);setChanged();">
                            </div>
                          </td>
                          <td class = "TitleHeader" align = "left" width="15%"> 
                            Leave (L) </td>
                          <td width="10%" class="MainText">
                            <div align="right">
                              <input type="checkbox" id="ReturnEnabled" onclick="toggleThermostat(3, this.checked);setChanged();">
                            </div>
                          </td>
                          <td class = "TitleHeader" align = "left" width="15%"> 
                            Return (R) </td>
                          <td width="10%" class="MainText">
                            <div align="right">
                              <input type="checkbox" id="SleepEnabled" onclick="toggleThermostat(4, this.checked);setChanged();">
                            </div>
                          </td>
                          <td class = "TitleHeader" align = "left" width="15%"> 
                            Sleep (S) </td>
                        </tr>
                        <tr> 
                          <td class = "TableCell" width="10%"> 
                            <div align="right">Start At:</div>
                          </td>
                          <td class = "TableCell" width="15%"> 
                            <input id="time1" type="text" size="8" name="time1" onchange="Javascript:setChanged();timeChange(this,1);">
                          </td>
                          <td class = "TableCell" width="10%"> 
                            <div align="right">Start At: </div>
                          </td>
                          <td class = "TableCell" width="15%"> 
                            <input id="time2" type="text" size="8" name="time2" onChange="Javascript:setChanged();timeChange(this,2);">
                          </td>
                          <td class = "TableCell" width="10%"> 
                            <div align="right">Start At: </div>
                          </td>
                          <td class = "TableCell" width="15%"> 
                            <input id="time3" type="text" size="8" name="time3" onchange="Javascript:setChanged();timeChange(this,3);">
                          </td>
                          <td class = "TableCell" width="10%"> 
                            <div align="right">Start At: </div>
                          </td>
                          <td class = "TableCell" width="15%"> 
                            <input id="time4" type="text" size="8" name="time4" onchange="Javascript:setChanged();timeChange(this,4);">
                          </td>
                        </tr>
                      </table>
					  <noscript>
					  <table width="100%" border="0" class = "TableCell">
					    <tr>
                          <td class = "TableCell" width="10%"> 
                            <div align="right">Temp: </div>
                          </td>
						  <td width="15%"> 
                            <input id="temp1" type="text" size="3" name="temp1" onchange="setChanged()" value="<%= schedule.getTemperature1() %>">
                          </td>
                          <td class = "TableCell" width="10%"> 
                            <div align="right">Temp: </div></td>
						  <td width="15%"> 
                            <input id="temp2" type="text" size="3" name="temp2" onchange="setChanged()" value="<%= schedule.getTemperature2() %>">
                          </td>
                          <td class = "TableCell" width="10%"> 
                            <div align="right">Temp: </div>
                          </td>
						  <td width="15%"> 
                            <input id="temp3" type="text" size="3" name="temp3" onchange="setChanged()" value="<%= schedule.getTemperature3() %>">
                          </td>
                          <td class = "TableCell" width="10%"> 
                            <div align="right">Temp: </div></td>
						  <td width="15%"> 
                            <input id="temp4" type="text" size="3" name="temp4" onchange="setChanged()" value="<%= schedule.getTemperature4() %>">
                          </td>
						</tr>
					  </table>
                    <div class = "TableCell" align = "left">
                      <table width="100%" border="0">
                        <tr>
                          <td>
                            <div class = "TableCell" align = "left">Currently 
                              your browser's security settings are not set to 
                              support scripting. Please enter all information 
                              manually or change security settings.</div>
                           </td>
                        </tr>
                      </table>
                    </div>
                    </noscript>
                  </td>
                </tr>
              </table><br>
              <table width="80%" border="0">
                <tr>
                  <td width="85%" align = "center" class = "TableCell" > 
                    <input type="submit" name="Submit" value="Submit">
<% if (!isRecommended) { %>
                    <input type="button" id="Default" value="Recommended Settings" onclick="setToDefault()">
<% } %>
                  </td>
<% if (isRecommended) { %>
				  <td width="15%" align = "right" class = "TableCell">
				    <input type="button" name="Back" value="Back" onclick="location.href='AdminTest.jsp'">
				  </td>
<% } %>
                </tr>
              </table>
            </form>
              