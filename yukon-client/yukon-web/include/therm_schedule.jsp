<%
/* Required predefined variables:
 * thermoProgram: StarsThermotatProgram
 * invID: int
 * invIDs: int[]
 * allTherm: boolean
 * thermNoStr: String
 */
	boolean isOperator = com.cannontech.stars.util.ECUtils.isOperator(user);
	boolean isRecommended = (invID < 0);
	
	StarsThermostatProgram dftThermoProgram = null;
	if (!isRecommended) {
		for (int i = 0; i < dftThermoSchedules.getStarsThermostatProgramCount(); i++) {
			if (dftThermoSchedules.getStarsThermostatProgram(i).getThermostatType().getType() == StarsThermostatTypes.EXPRESSSTAT_TYPE) {
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
		if (!isRecommended) {
			daySetting = ServletUtils.getCurrentDay();
			if (ServletUtils.isWeekday(daySetting))
				daySetting = StarsThermoDaySettings.WEEKDAY;
		}
		else
			daySetting = StarsThermoDaySettings.WEEKDAY;
	}
	
	StarsThermoModeSettings modeSetting = null;
	if (request.getParameter("mode") != null)
		modeSetting = StarsThermoModeSettings.valueOf(request.getParameter("mode"));
	if (modeSetting == null)
		modeSetting = StarsThermoModeSettings.COOL;
	
	boolean isCooling = (modeSetting.getType() == StarsThermoModeSettings.COOL_TYPE);
	String visibleC = isCooling ? "visible" : "hidden";
	String visibleH = isCooling ? "hidden" : "visible";
	char tempUnit = 'F';
	
	StarsThermostatSchedule coolSched = null;
	StarsThermostatSchedule heatSched = null;
	StarsThermostatSchedule schedule = null;
	StarsThermostatSchedule dftSchedule = null;
	
	if (thermoProgram != null) {
		for (int i = 0; i < thermoProgram.getStarsThermostatSeasonCount(); i++) {
			StarsThermostatSeason season = thermoProgram.getStarsThermostatSeason(i);
			if (season.getMode().getType() == StarsThermoModeSettings.COOL_TYPE) {
				for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
					if (season.getStarsThermostatSchedule(j).getDay().getType() == daySetting.getType()) {
						coolSched = season.getStarsThermostatSchedule(j);
						break;
					}
				}
			}
			else {
				for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
					if (season.getStarsThermostatSchedule(j).getDay().getType() == daySetting.getType()) {
						heatSched = season.getStarsThermostatSchedule(j);
						break;
					}
				}
			}
		}
		if (isCooling)
			schedule = coolSched;
		else
			schedule = heatSched;
	}
	
	if (dftThermoProgram != null) {
		for (int i = 0; i < dftThermoProgram.getStarsThermostatSeasonCount(); i++) {
			StarsThermostatSeason season = dftThermoProgram.getStarsThermostatSeason(i);
			if (season.getMode().getType() == StarsThermoModeSettings.COOL_TYPE) {
				for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
					if (season.getStarsThermostatSchedule(j).getDay().getType() == daySetting.getType()) {
						StarsThermostatSchedule sched = season.getStarsThermostatSchedule(j);
						if (coolSched == null) coolSched = sched;
						if (isCooling) dftSchedule = sched;
						break;
					}
				}
			}
			else {
				for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
					if (season.getStarsThermostatSchedule(j).getDay().getType() == daySetting.getType()) {
						StarsThermostatSchedule sched = season.getStarsThermostatSchedule(j);
						if (heatSched == null) heatSched = sched;
						if (!isCooling) dftSchedule = sched;
						break;
					}
				}
			}
		}
		if (schedule == null) schedule = dftSchedule;
	}
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

function setChanged() {
	changed = true;
}

function prepareSubmit(form) {
	form.tempval1.value = document.getElementById('temp1').innerHTML.substr(0,2);
	form.tempval2.value = document.getElementById('temp2').innerHTML.substr(0,2);
	form.tempval3.value = document.getElementById('temp3').innerHTML.substr(0,2);
	form.tempval4.value = document.getElementById('temp4').innerHTML.substr(0,2);
	changed = false;
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
	
	var temp1C, temp1H, temp2C, temp2H, temp3C, temp3H, temp4C, temp4H;
	if (<%= isCooling %>) {
		temp1C = <%= dftSchedule.getTemperature1() %>;
		temp2C = <%= dftSchedule.getTemperature2() %>;
		temp3C = <%= dftSchedule.getTemperature3() %>;
		temp4C = <%= dftSchedule.getTemperature4() %>;
		temp1H = temp2H = temp3H = temp4H = null;
	}
	else {
		temp1H = <%= dftSchedule.getTemperature1() %>;
		temp2H = <%= dftSchedule.getTemperature2() %>;
		temp3H = <%= dftSchedule.getTemperature3() %>;
		temp4H = <%= dftSchedule.getTemperature4() %>;
		temp1C = temp2C = temp3C = temp4C = null;
	}
	
	updateLayout(
		<%= dftSchedule.getTime1().getHour() %>,<%= dftSchedule.getTime1().getMinute() %>,temp1C,temp1H,
		<%= dftSchedule.getTime2().getHour() %>,<%= dftSchedule.getTime2().getMinute() %>,temp2C,temp2H,
		<%= dftSchedule.getTime3().getHour() %>,<%= dftSchedule.getTime3().getMinute() %>,temp3C,temp3H,
		<%= dftSchedule.getTime4().getHour() %>,<%= dftSchedule.getTime4().getMinute() %>,temp4C,temp4H
	);
	setChanged();
}

function saveSchedule() {
	if (changed && confirm("You have made changes to the thermostat schedule. Would you like to save those changes first?")) {
		var form = document.form1;
<%
	int lastSlashPos = request.getRequestURI().lastIndexOf("/");
%>
		form.REDIRECT.value = "<%= request.getRequestURI().substring(0, lastSlashPos)%>/SavedSchedules.jsp?<%= thermNoStr %>";
		prepareSubmit(form);
		form.submit();
	}
	else {
		location.href = "SavedSchedules.jsp?<%= thermNoStr %>"
	}
}

function init() {
	updateLayout(
		<%= schedule.getTime1().getHour() %>,<%= schedule.getTime1().getMinute() %>,<%= coolSched.getTemperature1() %>,<%= heatSched.getTemperature1() %>,
		<%= schedule.getTime2().getHour() %>,<%= schedule.getTime2().getMinute() %>,<%= coolSched.getTemperature2() %>,<%= heatSched.getTemperature2() %>,
		<%= schedule.getTime3().getHour() %>,<%= schedule.getTime3().getMinute() %>,<%= coolSched.getTemperature3() %>,<%= heatSched.getTemperature3() %>,
		<%= schedule.getTime4().getHour() %>,<%= schedule.getTime4().getMinute() %>,<%= coolSched.getTemperature4() %>,<%= heatSched.getTemperature4() %>
	);
	document.getElementById('MovingLayer1').style.display = "";
	document.getElementById('MovingLayer2').style.display = "";
	document.getElementById('MovingLayer3').style.display = "";
	document.getElementById('MovingLayer4').style.display = "";
	
	if (document.getElementById('Default') != null) {
<% if (isOperator) { %>
		document.getElementById('Default').value = '<cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_RECOMMENDED_SETTINGS_BUTTON %>"/>';
<% } else { %>
		document.getElementById('Default').value = '<cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_RECOMMENDED_SETTINGS_BUTTON %>"/>';
<% } %>
	}
}
</script>

			<form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/<%= (isRecommended)?"StarsAdmin":"SOAPClient" %>" onsubmit="prepareSubmit(this)">
			  <input type="hidden" name="action" value="UpdateThermostatSchedule">
			  <input type="hidden" name="InvID" value="<%= invID %>">
<% for (int i = 0; i < invIDs.length; i++) { %>
			  <input type="hidden" name="InvIDs" value="<%= invIDs[i] %>">
<% } %>
			  <input type="hidden" name="type" value="<%= StarsThermostatTypes.EXPRESSSTAT.toString() %>">
			  <input type="hidden" name="day" value="<%= daySetting.toString() %>">
			  <input type="hidden" name="mode" value="<%= modeSetting.toString() %>">
			  <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?<%= thermNoStr %>&day=<%= daySetting.toString() %>&mode=<%= modeSetting.toString() %>">
			  <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?<%= thermNoStr %>&day=<%= daySetting.toString() %>&mode=<%= modeSetting.toString() %>">
			  <input type="hidden" name="tempval1">
			  <input type="hidden" name="tempval2">
			  <input type="hidden" name="tempval3">
			  <input type="hidden" name="tempval4">
<%	if (allTherm) { %>
              <table width="80%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                    <td align="right" class="MainText"><a href="AllTherm.jsp" class="Link1">Change 
                      Selected Thermostats</a></td>
                </tr>
              </table>
<%	} %>
              <table width="80%" border="1" cellspacing = "0" cellpadding = "2">
                <tr> 
                    <td align = "center"  valign = "bottom" class = "Background"> 
                      <table width="478" border="0" height="8" valign = "bottom" >
                        <tr> 
                          <td class = "TableCell1" align = "left" width="54%"> 
                            <% if (daySetting.getType() == StarsThermoDaySettings.WEEKDAY_TYPE) { %>
                            <b><span class="Header2">Weekday</span></b> 
                            <% } else { %>
                            <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.WEEKDAY.toString() %>', '<%= modeSetting.toString() %>')">Weekday</span> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.SATURDAY_TYPE) { %>
                            <b><span class="Header2">Saturday</span> </b>
                            <% } else { %>
                            <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.SATURDAY.toString() %>', '<%= modeSetting.toString() %>')">Saturday</span> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.SUNDAY_TYPE) { %>
                            <b><span class="Header2">Sunday</span></b> 
                            <% } else { %>
                            <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.SUNDAY.toString() %>', '<%= modeSetting.toString() %>')">Sunday</span> 
                            <% } %>
                          <td class = "Background" align = "right" width="46%"> 
<%
	String visible = (daySetting.getType() == StarsThermoDaySettings.WEEKDAY_TYPE)? "visible" : "hidden";
	String checked = (String) session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_APPLY_TO_WEEKEND);
	if (checked == null || isRecommended) checked = "";
%>
                            <span style="visibility:<%= visible %>"> 
                            <input type="checkbox" name="ApplyToWeekend" value="true" <%= checked %>>
                            <span class="TableCell1">Apply settings to Saturday 
                            and Sunday </span></span> 
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
%>
                              <a class="Link1" href="<%= instrLink %>" <%= target %>>Click 
                              for hints and details</a>.
                            </div>
                          </td>
                          <td class = "TableCell" width="29%" height="4" align = "left" valign="top" > 
                            <i>Make temporary adjustments to your heating and 
                            cooling system<a class="Link1" href="Thermostat.jsp?<%= thermNoStr %>"> 
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
                      <table width="478" height="186" background="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/TempBG2.gif" style="background-repeat: no-repeat" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td width="50">
                            <div id="MovingLayer1" style="position:relative; width:30px; height:162px; left:0px; z-index:1; top:5px; display:none" onMouseDown = "beginDrag(event,0,0,getRightBound(1),getLeftBound(1),'showTimeWake()','horizontal','MovingLayer1');setChanged()">
                              <table border="0">
                                <tr align="center"> 
                                  <td colspan="2"> 
                                    <div id="temp1" class="TableCell2" onChange="setChanged()"><%= schedule.getTemperature1() %>&deg;<%= tempUnit %></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td align="center" colspan="2"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ThermW.gif" width="16" height="131"> 
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
                            <div id="MovingLayer2" style="position:relative; width:30px; height:162px; left:0px; z-index:2; top:5px; display:none" onMouseDown = "beginDrag(event,0,0,getRightBound(2),getLeftBound(2),'showTimeLeave()','horizontal','MovingLayer2');setChanged()"> 
                              <table border="0">
                                <tr align="center"> 
                                  <td colspan="2"> 
                                    <div id="temp2" class="TableCell2" onChange="setChanged()"><%= schedule.getTemperature2() %>&deg;<%= tempUnit %></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td align="center" colspan="2"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/ThermImages/ThermL.gif" width="16" height="131"> 
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
                            <div id="MovingLayer3" style="position:relative; width:30px; height:162px; left:0px; z-index:3; top:5px; display:none" onMouseDown = "beginDrag(event,0,0,getRightBound(3),getLeftBound(3),'showTimeReturn()','horizontal','MovingLayer3');setChanged()"> 
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
                            <div id="MovingLayer4" style="position:relative; width:30px; height:162px; left:0px; z-index:4; top:5px; display:none" onMouseDown = "beginDrag(event,0,0,getRightBound(4),getLeftBound(4),'showTimeSleep()','horizontal','MovingLayer4');setChanged()"> 
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
					  	  <td width="10%">&nbsp;</td> 
                          <td class = "TableCell" align = "left" width="15%"><span class = "TitleHeader">Wake (W)</span></td>
                          <td width="10%">&nbsp;</td>
						  <td class = "TableCell" align = "left" width="15%"><span class = "TitleHeader">Leave (L)</span></td>
                          <td width="10%">&nbsp;</td>
						  <td class = "TableCell" align = "left" width="15%"><span class = "TitleHeader">Return (R)</span></td>
                          <td width="10%">&nbsp;</td>
						  <td class = "TableCell" align = "left" width="15%"><span class = "TitleHeader">Sleep (S)</span></td>
                        </tr>
                        <tr> 
                          <td class = "TableCell" width="10%">
                            <div align="right">Start At:</div>
                          </td>
						  <td class = "TableCell" width="15%">  
                            <input id="time1" type="text" size="8" value="<%= ampmTimeFormat.format(schedule.getTime1().toDate()) %>" name="time1" onchange="Javascript:setChanged();timeChange(this,1);">
                          </td>
                          <td class = "TableCell" width="10%">
                            <div align="right">Start At: </div>
                          </td>
						  <td class = "TableCell" width="15%"> 
                            <input id="time2" type="text" size="8" value="<%= ampmTimeFormat.format(schedule.getTime2().toDate()) %>" name="time2" onchange="Javascript:setChanged();timeChange(this,2);">
                          </td>
                          <td class = "TableCell" width="10%">
                            <div align="right">Start At: </div>
                          </td>
						  <td class = "TableCell" width="15%"> 
                            <input id="time3" type="text" size="8" value="<%= ampmTimeFormat.format(schedule.getTime3().toDate()) %>" name="time3" onchange="Javascript:setChanged();timeChange(this,3);">
                          </td>
                          <td class = "TableCell" width="10%">
                            <div align="right">Start At: </div>
                          </td>
						  <td class = "TableCell" width="15%"> 
                            <input id="time4" type="text" size="8" value="<%= ampmTimeFormat.format(schedule.getTime4().toDate()) %>" name="time4" onchange="Javascript:setChanged();timeChange(this,4);">
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
              <table width="80%" border="0" cellpadding="5">
                <tr>
                  <td width="85%" align = "center" class = "TableCell"> 
                    <input type="submit" name="Submit" value="Submit">
<% if (!isRecommended) { %>
<% if (!allTherm) { %>
                    <input type="button" value="Save/Apply Schedule" onclick="saveSchedule()">
                    <p>
<% } %>
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
