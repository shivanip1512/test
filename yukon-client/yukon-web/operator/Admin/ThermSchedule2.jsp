<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%
	StarsDefaultThermostatSettings thermoSettings = null;
	for (int i = 0; i < allDftThermoSettings.length; i++) {
		if (allDftThermoSettings[i].getThermostatType().getType() == StarsThermostatTypes.ENERGYPRO_TYPE) {
			thermoSettings = allDftThermoSettings[i];
			break;
		}
	}
	
	StarsDefaultThermostatSettings dftThermoSettings = SOAPServer.getDefaultEnergyCompany().getStarsDefaultThermostatSettings()[0];

	String dayStr = request.getParameter("day");
	if (dayStr == null) dayStr = StarsThermoDaySettings.MONDAY.toString();
	StarsThermoDaySettings daySetting = StarsThermoDaySettings.valueOf(dayStr);
	
	String modeStr = request.getParameter("mode");
	if (modeStr == null) modeStr = StarsThermoModeSettings.COOL.toString();
	
	boolean isCooling = modeStr.equalsIgnoreCase( StarsThermoModeSettings.COOL.toString() );
	String visibleC = isCooling ? "visible" : "hidden";
	String visibleH = isCooling ? "hidden" : "visible";
	String tempUnit = "F";
	
	String seasonStr = null;
	StarsThermostatSchedule coolSched = null;
	StarsThermostatSchedule heatSched = null;
	StarsThermostatSchedule schedule = null;
	StarsThermostatSchedule dftCoolSched = null;
	StarsThermostatSchedule dftHeatSched = null;
	StarsThermostatSchedule dftSchedule = null;
	
	if (thermoSettings != null) {
		for (int i = 0; i < thermoSettings.getStarsThermostatSeasonCount(); i++) {
			StarsThermostatSeason season = thermoSettings.getStarsThermostatSeason(i);
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
	
	if (dftThermoSettings != null) {
		for (int i = 0; i < dftThermoSettings.getStarsThermostatSeasonCount(); i++) {
			StarsThermostatSeason season = dftThermoSettings.getStarsThermostatSeason(i);
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
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript" src ="../../JavaScript/nav_menu.js">
</script>
<script language="JavaScript" src ="../../JavaScript/drag.js">
</script>
<script language="JavaScript" src ="../../JavaScript/thermostat2.js">
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
	form.REDIRECT.value = "<%= request.getRequestURI() %>?day=" + day + "&mode=" + mode;
	if (changed &&
		confirm('You have made changes to the thermostat schedule. Click "Ok" to submit these changes before leaving the page, or click "Cancel" to discard them.'))
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
	
	toggleThermostat(1, true);
	toggleThermostat(2, true);
	toggleThermostat(3, true);
	toggleThermostat(4, true);
	setChanged();
}

function init() {
<%	
	boolean skip1 = (schedule.getTemperature1() == -1);
	if (skip1) schedule.setTime1(dftSchedule.getTime1());
	int ct1 = (skip1)? dftCoolSched.getTemperature1() : coolSched.getTemperature1();
	int ht1 = (skip1)? dftHeatSched.getTemperature1() : heatSched.getTemperature1();
	boolean skip2 = (schedule.getTemperature2() == -1);
	if (skip2) schedule.setTime2(dftSchedule.getTime2());
	int ct2 = (skip2)? dftCoolSched.getTemperature2() : coolSched.getTemperature2();
	int ht2 = (skip2)? dftHeatSched.getTemperature2() : heatSched.getTemperature2();
	boolean skip3 = (schedule.getTemperature3() == -1);
	if (skip3) schedule.setTime3(dftSchedule.getTime3());
	int ct3 = (skip3)? dftCoolSched.getTemperature3() : coolSched.getTemperature3();
	int ht3 = (skip3)? dftHeatSched.getTemperature3() : heatSched.getTemperature3();
	boolean skip4 = (schedule.getTemperature4() == -1);
	if (skip4) schedule.setTime4(dftSchedule.getTime4());
	int ct4 = (skip4)? dftCoolSched.getTemperature4() : coolSched.getTemperature4();
	int ht4 = (skip4)? dftHeatSched.getTemperature4() : heatSched.getTemperature4();
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
	
	document.getElementById('Default').value = '<cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_RECOMMENDED_SETTINGS_BUTTON %>"/>';
}
</script>

<script language="JavaScript">
<!--
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
// -->
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/AdminImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
				  
                <td width="265" height="28" valign="middle" class="PageHeader">&nbsp;&nbsp;&nbsp;Administration</td>
                  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">
				  	<div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
				  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - DEFAULT THERMOSTAT SCHEDULE</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  
			<form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/StarsAdmin" onsubmit="prepareSubmit(this)">
			  <input type="hidden" name="action" value="UpdateThermostatSchedule">
			  <input type="hidden" name="InvID" value="<%= thermoSettings.getInventoryID() %>">
			  <input type="hidden" name="type" value="<%= StarsThermostatTypes.ENERGYPRO.toString() %>">
			  <input type="hidden" name="day" value="<%= dayStr %>">
			  <input type="hidden" name="mode" value="<%= modeStr %>">
			  <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?day=<%= dayStr %>&mode=<%= modeStr %>">
			  <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?day=<%= dayStr %>&mode=<%= modeStr %>">
			  <input type="hidden" name="tempval1">
			  <input type="hidden" name="tempval2">
			  <input type="hidden" name="tempval3">
			  <input type="hidden" name="tempval4">
              <table width="80%" border="1" cellspacing = "0" cellpadding = "2">
                <tr> 
                    <td align = "center"  valign = "bottom" class = "Background" > 
                      <table width="478" border="0" height="8" valign = "bottom" cellpadding="0" cellspacing="0" >
                        <tr> 
                          <td class = "TableCell1" align = "left" width="50%"> 
                            <% if (daySetting.getType() == StarsThermoDaySettings.MONDAY_TYPE) { %>
                            <b><span class="Header2">Mon</span></b> 
                            <% } else { %>
                            <a href="" class="Link2" onClick="switchSettings('<%= StarsThermoDaySettings.MONDAY.toString() %>', '<%= modeStr %>'); return false;">Mon</a> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.TUESDAY_TYPE) { %>
                            <b><span class="Header2">Tue</span></b> 
                            <% } else { %>
                            <a href="" class="Link2" onClick="switchSettings('<%= StarsThermoDaySettings.TUESDAY.toString() %>', '<%= modeStr %>'); return false;">Tue</a> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.WEDNESDAY_TYPE) { %>
                            <b><span class="Header2">Wed</span></b> 
                            <% } else { %>
                            <a href="" class="Link2" onClick="switchSettings('<%= StarsThermoDaySettings.WEDNESDAY.toString() %>', '<%= modeStr %>'); return false;">Wed</a> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.THURSDAY_TYPE) { %>
                            <b><span class="Header2">Thu</span></b> 
                            <% } else { %>
                            <a href="" class="Link2" onClick="switchSettings('<%= StarsThermoDaySettings.THURSDAY.toString() %>', '<%= modeStr %>'); return false;">Thu</a> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.FRIDAY_TYPE) { %>
                            <b><span class="Header2">Fri</span></b> 
                            <% } else { %>
                            <a href="" class="Link2" onClick="switchSettings('<%= StarsThermoDaySettings.FRIDAY.toString() %>', '<%= modeStr %>'); return false;">Fri</a> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.SATURDAY_TYPE) { %>
                            <b><span class="Header2">Sat</span> </b> 
                            <% } else { %>
                            <a href="" class="Link2" onClick="switchSettings('<%= StarsThermoDaySettings.SATURDAY.toString() %>', '<%= modeStr %>'); return false;">Sat</a> 
                            <% } %>
                            &nbsp;&nbsp; 
                            <% if (daySetting.getType() == StarsThermoDaySettings.SUNDAY_TYPE) { %>
                            <b><span class="Header2">Sun</span></b> 
                            <% } else { %>
                            <a href="" class="Link2" onClick="switchSettings('<%= StarsThermoDaySettings.SUNDAY.toString() %>', '<%= modeStr %>'); return false;">Sun</a> 
                            <% } %>
                          <td class = "Background" align = "right" width="50%"> 
                            <input type="checkbox" name="ApplyToWeekdays" value="true">
                            <span class="TableCell1">Apply to weekdays </span> 
                            <input type="checkbox" name="ApplyToWeekend" value="true">
                            <span class="TableCell1">Apply to weekends </span> 
                          </td>
						</tr>
                      </table>
                    </td>
                </tr>
                <tr> 
                    <td align = "center"> 
                      <table width="175" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td width="68"> 
                            <div align="right"><span class="TableCell"><font color="#003366">Cooling</font> 
                              <img src="../../Images/ThermImages/BlueArrow.gif"> 
                              </span> </div>
                          </td>
                          <td width="31"><span class="TableCell"> 
                            <input type="radio" name="radiobutton" value="radiobutton" <% if (isCooling) { %>checked<% } else { %>onclick="switchSettings('<%= dayStr %>', '<%= StarsThermoModeSettings.COOL.toString() %>')"<% } %>>
                            </span></td>
						  <td width="20"> 
                            <input type="radio" name="radiobutton" value="radiobutton" <% if (!isCooling) { %>checked<% } else { %>onclick="switchSettings('<%= dayStr %>', '<%= StarsThermoModeSettings.HEAT.toString() %>')"<% } %>>
                          </td>
                          <td width="56"><img src="../../Images/ThermImages/RedArrow.gif"> 
                            <span class="TableCell"><font color="FF0000">Heating</font></span></td>
                        </tr>
                      </table>
                      <table width="478" height="186" background="../../Images/ThermImages/TempBG2.gif" style="background-repeat: no-repeat" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td width="50"> 
                            <div id="MovingLayer1" style="position:relative; width:30px; height:162px; left:0px; z-index:1; top: 5px" onMouseDown = "beginDrag(event,0,0,getRightBound(1),getLeftBound(1),'showTimeWake()','horizontal','MovingLayer1');setChanged()"> 
                              <table border="0">
                                <tr align="center"> 
                                  <td colspan="2"> 
                                    <div id="temp1" class="TableCell2" onChange="setChanged()"><%= schedule.getTemperature1() %>&deg;<%= tempUnit %></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td align="center" colspan="2"> <img src="../../Images/ThermImages/ThermW.gif" width="16"> 
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="50%"> 
                                    <div id="div1C" align="left" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow1C" src="../../Images/ThermImages/BlueArrow.gif" <% if (isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(1)','vertical','div1C');setChanged()"<% } %> style="visibility:<%= visibleC %>"><br>
                                      <img id="arrow1C_Gray" src="../../Images/ThermImages/GrayArrowL.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleH %>"> 
                                    </div>
                                  </td>
                                  <td width="50%"> 
                                    <div id="div1H" align="right" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow1H" src="../../Images/ThermImages/RedArrow.gif" <% if (!isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(1)','vertical','div1H');setChanged()"<% } %> style="visibility:<%= visibleH %>"><br>
                                      <img id="arrow1H_Gray" src="../../Images/ThermImages/GrayArrowR.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleC %>"> 
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </td>
                          <td width="50"> 
                            <div id="MovingLayer2" style="position:relative; width:30px; height:162px; left:0px; z-index:2; top: 5px" onMouseDown = "beginDrag(event,0,0,getRightBound(2),getLeftBound(2),'showTimeLeave()','horizontal','MovingLayer2');setChanged()"> 
                              <table border="0">
                                <tr align="center"> 
                                  <td colspan="2"> 
                                    <div id="temp2" class="TableCell2" onChange="setChanged()"><%= schedule.getTemperature2() %>&deg;<%= tempUnit %></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td align="center" colspan="2"> <img src="../../Images/ThermImages/ThermL.gif" width="16"> 
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="50%"> 
                                    <div id="div2C" align="left" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow2C" src="../../Images/ThermImages/BlueArrow.gif" <% if (isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(2)','vertical','div2C');setChanged()"<% } %> style="visibility:<%= visibleC %>"><br>
                                      <img id="arrow2C_Gray" src="../../Images/ThermImages/GrayArrowL.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleH %>"> 
                                    </div>
                                  </td>
                                  <td width="50%"> 
                                    <div id="div2H" align="right" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow2H" src="../../Images/ThermImages/RedArrow.gif" <% if (!isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(2)','vertical','div2H');setChanged()"<% } %> style="visibility:<%= visibleH %>"><br>
                                      <img id="arrow2H_Gray" src="../../Images/ThermImages/GrayArrowR.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleC %>"> 
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </td>
                          <td width="50"> 
                            <div id="MovingLayer3" style="position:relative; width:30px; height:162px; left:0px; z-index:3; top: 5px" onMouseDown = "beginDrag(event,0,0,getRightBound(3),getLeftBound(3),'showTimeReturn()','horizontal','MovingLayer3');setChanged()"> 
                              <table border="0">
                                <tr align="center"> 
                                  <td colspan="2"> 
                                    <div id="temp3" class="TableCell2" onChange="setChanged()"><%= schedule.getTemperature3() %>&deg;<%= tempUnit %></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td align="center" colspan="2"> <img src="../../Images/ThermImages/ThermR.gif" width="16" height="131"> 
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="50%"> 
                                    <div id="div3C" align="left" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow3C" src="../../Images/ThermImages/BlueArrow.gif" <% if (isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(3)','vertical','div3C');setChanged()"<% } %> style="visibility:<%= visibleC %>"><br>
                                      <img id="arrow3C_Gray" src="../../Images/ThermImages/GrayArrowL.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleH %>"> 
                                    </div>
                                  </td>
                                  <td width="50%"> 
                                    <div id="div3H" align="right" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow3H" src="../../Images/ThermImages/RedArrow.gif" <% if (!isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(3)','vertical','div3H');setChanged()"<% } %> style="visibility:<%= visibleH %>"><br>
                                      <img id="arrow3H_Gray" src="../../Images/ThermImages/GrayArrowR.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleC %>"> 
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </td>
                          <td> 
                            <div id="MovingLayer4" style="position:relative; width:30px; height:162px; left:0px; z-index:4; top: 5px" onMouseDown = "beginDrag(event,0,0,getRightBound(4),getLeftBound(4),'showTimeSleep()','horizontal','MovingLayer4');setChanged()"> 
                              <table border="0">
                                <tr align="center"> 
                                  <td colspan="2"> 
                                    <div id="temp4" class="TableCell2" onChange="setChanged()"><%= schedule.getTemperature4() %>&deg;<%= tempUnit %></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td align="center" colspan="2"> <img src="../../Images/ThermImages/ThermS.gif" width="16" height="131"> 
                                  </td>
                                </tr>
                                <tr> 
                                  <td width="50%"> 
                                    <div id="div4C" align="left" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow4C" src="../../Images/ThermImages/BlueArrow.gif" <% if (isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(4)','vertical','div4C');setChanged()"<% } %> style="visibility:<%= visibleC %>"><br>
                                      <img id="arrow4C_Gray" src="../../Images/ThermImages/GrayArrowL.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleH %>"> 
                                    </div>
                                  </td>
                                  <td width="50%"> 
                                    <div id="div4H" align="right" style="position:relative; left:0px; top:-115px"> 
                                      <img id="arrow4H" src="../../Images/ThermImages/RedArrow.gif" <% if (!isCooling) { %>onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'showTemp(4)','vertical','div4H');setChanged()"<% } %> style="visibility:<%= visibleH %>"><br>
                                      <img id="arrow4H_Gray" src="../../Images/ThermImages/GrayArrowR.gif" width="10" height="10" style="position:relative; top:-15px; visibility:<%= visibleC %>"> 
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </td>
                        </tr>
                      </table>
                      <table width="100%" border="0" height="27">
                        <tr>
					  	  <td width="10%">
                            <div align="right">
                              <input type="checkbox" id="WakeEnabled" onClick="toggleThermostat(1, this.checked);setChanged();" disabled name="checkbox">
                            </div>
                          </td> 
                          <td class = "TableCell" align = "left" width="15%"><span class = "TitleHeader">Wake (W)</span></td>
                          <td width="10%">
                            <div align="right">
                              <input type="checkbox" id="LeaveEnabled" onClick="toggleThermostat(2, this.checked);setChanged();" name="checkbox2">
                            </div>
                          </td>
						  <td class = "TableCell" align = "left" width="15%"><span class = "TitleHeader">Leave (L)</span></td>
                          <td width="10%">
                            <div align="right">
                              <input type="checkbox" id="ReturnEnabled" onClick="toggleThermostat(3, this.checked);setChanged();" name="checkbox3">
                            </div>
                          </td>
						  <td class = "TableCell" align = "left" width="15%"><span class = "TitleHeader">Return (R)</span></td>
                          <td width="10%">
                            <div align="right">
                              <input type="checkbox" id="SleepEnabled" onClick="toggleThermostat(4, this.checked);setChanged();" name="checkbox4">
                            </div>
                          </td>
						  <td class = "TableCell" align = "left" width="15%"><span class = "TitleHeader">Sleep (S)</span></td>
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
                            <input id="time2" type="text" size="8" name="time2" onchange="Javascript:setChanged();timeChange(this,2);">
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
                  <td width="35%" align = "right" class = "TableCell"> 
                    <input type="submit" name="Submit" value="Submit">
                  </td>
                  <td width="50%" align = "left" class = "TableCell"> 
                    <input type="button" id="Default" value="Recommended Settings" onclick="setToDefault()">
                  </td>
                  <td width="15%" align = "right" class = "TableCell"> 
                    <input type="button" name="Done" value="Done" onclick="location.href='AdminTest.jsp'">
                  </td>
                </tr>
              </table>
              </form>
              <p align="center" class="MainText"><font face="Arial, Helvetica, sans-serif" size="1">Copyright 
                &copy; 2003, Cannon Technologies, Inc. All rights reserved.</font> 
              </p>
              <p align="center" class="MainText">&nbsp; </p>
            </div>
			
          </td>
		  
		  
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<br>
</body>
</html>
