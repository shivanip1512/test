<%@ include file="StarsHeader.jsp" %>
<%
	String dayStr = request.getParameter("day");
	if (dayStr == null) dayStr = StarsThermoDaySettings.WEEKDAY.toString();
	String modeStr = request.getParameter("mode");
	if (modeStr == null) modeStr = StarsThermoModeSettings.COOL.toString();
	
	String seasonStr = null;
	StarsThermostatSeason summer = null;
	StarsThermostatSeason winter = null;
	StarsThermostatSchedule schedule = null;
	StarsThermostatSeason dftSummer = null;
	StarsThermostatSeason dftWinter = null;
	StarsThermostatSchedule dftSchedule = null;
	
	if (thermoSettings != null) {
		for (int i = 0; i < thermoSettings.getStarsThermostatSeasonCount(); i++) {
			StarsThermostatSeason season = thermoSettings.getStarsThermostatSeason(i);
			if (season.getMode().getType() == StarsThermoModeSettings.COOL_TYPE)
				summer = season;
			else
				winter = season;
			if (season.getMode().toString().equalsIgnoreCase( modeStr )) {
				for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
					if (season.getStarsThermostatSchedule(j).getDay().toString().equalsIgnoreCase( dayStr )) {
						schedule = season.getStarsThermostatSchedule(j);
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
				dftSummer = season;
				if (summer == null) summer = season;
			}
			else {
				dftWinter = season;
				if (winter == null) winter = season;
			}
			if (season.getMode().toString().equalsIgnoreCase( modeStr )) {
				seasonStr = season.getStarsWebConfig().getAlternateDisplayName();
				for (int j = 0; j < season.getStarsThermostatScheduleCount(); j++) {
					if (season.getStarsThermostatSchedule(j).getDay().toString().equalsIgnoreCase( dayStr )) {
						dftSchedule = season.getStarsThermostatSchedule(j);
						if (schedule == null) schedule = dftSchedule;
						break;
					}
				}
			}
		}
	}
%>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../../demostyle.css" type="text/css">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
</script>

<script langauge = "JavaScript">
function updateLayout(hour1, min1, temp1, hour2, min2, temp2, hour3, min3, temp3, hour4, min4, temp4) {
	moveLayer('MovingLayer1', hour1, min1);
	moveTempArrow('arrow1', 'div1', temp1);
	moveLayer('MovingLayer2', hour2, min2);
	moveTempArrow('arrow2', 'div2', temp2);
	moveLayer('MovingLayer3', hour3, min3);
	moveTempArrow('arrow3', 'div3', temp3);
	moveLayer('MovingLayer4', hour4, min4);
	moveTempArrow('arrow4', 'div4', temp4);
}

var scheChanged = false;

function setScheduleChanged() {
	scheChanged = true;
}

function switchSettings(day, mode) {
	var form = document.form1;
	form.REDIRECT.value = "/user/ConsumerStat/stat/ThermSchedule.jsp?day=" + day + "&mode=" + mode;
	form.tempval1.value = document.getElementById('div1').innerHTML.substr(0,2);
	form.tempval2.value = document.getElementById('div2').innerHTML.substr(0,2);
	form.tempval3.value = document.getElementById('div3').innerHTML.substr(0,2);
	form.tempval4.value = document.getElementById('div4').innerHTML.substr(0,2);
	
	if (!scheChanged) {
		if (day == "<%= dayStr %>" && mode == "<%= modeStr %>") return;	// Submit button is clicked without changes
		location = form.REDIRECT.value;
		return;
	}
	
	form.attributes('action').value = "/servlet/UpdateThermostat";
	form.elements('action').value = (scheChanged)? "SaveScheduleChanges" : "";
	form.submit();
}

function setToDefault() {
	var form = document.form1;
	form.time1.value = "<%= timePart.format(dftSchedule.getTime1().toDate()) %>";
	form.time2.value = "<%= timePart.format(dftSchedule.getTime2().toDate()) %>";
	form.time3.value = "<%= timePart.format(dftSchedule.getTime3().toDate()) %>";
	form.time4.value = "<%= timePart.format(dftSchedule.getTime4().toDate()) %>";
	updateLayout(
		<%= dftSchedule.getTime1().getHour() %>,<%= dftSchedule.getTime1().getMinute() %>,<%= dftSchedule.getTemperature1() %>,
		<%= dftSchedule.getTime2().getHour() %>,<%= dftSchedule.getTime2().getMinute() %>,<%= dftSchedule.getTemperature2() %>,
		<%= dftSchedule.getTime3().getHour() %>,<%= dftSchedule.getTime3().getMinute() %>,<%= dftSchedule.getTemperature3() %>,
		<%= dftSchedule.getTime4().getHour() %>,<%= dftSchedule.getTime4().getMinute() %>,<%= dftSchedule.getTemperature4() %>
	);
	setScheduleChanged();
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
<script language="JavaScript" src ="../drag.js">
</script>
<script language="JavaScript" src ="thermostat.js">
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td id="Header" colspan="4" height="74" background="../../Header.gif">&nbsp;</td>
<script language="JavaScript">
	document.getElementById("Header").background = '../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>';
</script>
              </tr>
              <tr> 
				  <td width="265" height="28">&nbsp;</td>
                  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">&nbsp;</td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
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
          <td  valign="top" width="101">
		  <% String pageName = "ThermSchedule.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              
            <div align="center"><br>
              <% String header = ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_THERM_SCHED_TITLE); %>
              <%@ include file="InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
			<form name="form1" method="POST" action="/servlet/SOAPClient">
			  <input type="hidden" name="action" value="UpdateThermostatSchedule">
			  <input type="hidden" name="day" value="<%= dayStr %>">
			  <input type="hidden" name="mode" value="<%= modeStr %>">
			  <input type="hidden" name="REDIRECT" value="/user/ConsumerStat/stat/ThermSchedule.jsp?day=<%= dayStr %>&mode=<%= modeStr %>">
			  <input type="hidden" name="REFERRER" value="/user/ConsumerStat/stat/ThermSchedule.jsp?day=<%= dayStr %>&mode=<%= modeStr %>">
			  <input type="hidden" name="tempval1">
			  <input type="hidden" name="tempval2">
			  <input type="hidden" name="tempval3">
			  <input type="hidden" name="tempval4">
              <table width="80%" border="1" height="411" cellspacing = "0" cellpadding = "2">
                <tr> 
                  <td align = "center"  valign = "bottom" height="84"> 
                    <table width="100%" border="0" class = "TableCell" height="89">
                      <tr> 
                        <td width="48%" valign = "top" height="22"><b>
						  <%= seasonStr.toUpperCase() %> <%= dayStr.toUpperCase() %> SCHEDULE - <%= modeStr.toUpperCase() %>ING
						</b></td>
                        <td width="52%" valign = "top" align = "right" height="22"> 
                          <table width="82%" border="0" height="8" valign = "bottom" >
                            <tr> 
                              <td class = "TableCell" align = "right">
<% if (dayStr.equalsIgnoreCase( StarsThermoDaySettings.WEEKDAY.toString() )) { %>
							  <span class="SchedText">Weekday</span>
<% } else { %>
							  <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.WEEKDAY.toString() %>','<%= modeStr %>')">Weekday</span>
<% } %>&nbsp;&nbsp;
<% if (dayStr.equalsIgnoreCase( StarsThermoDaySettings.SATURDAY.toString() )) { %>
							  <span class="SchedText">Saturday</span>
<% } else { %>
							  <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.SATURDAY.toString() %>','<%= modeStr %>')">Saturday</span>
<% } %>&nbsp;&nbsp;
<% if (dayStr.equalsIgnoreCase( StarsThermoDaySettings.SUNDAY.toString() )) { %>
							  <span class="SchedText">Sunday</span>
<% } else { %>
							  <span class="Clickable" onclick="switchSettings('<%= StarsThermoDaySettings.SUNDAY.toString() %>','<%= modeStr %>')">Sunday</span>
<% } %>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="48%">
                            <table width="140" border="0">
                              <tr> 
                                <td align = "center" width="42"> 
                                  <%
	String imgStrR, seasonStrR, modeStrR;
	if (modeStr.equalsIgnoreCase( StarsThermoModeSettings.COOL.toString() )) {
		imgStrR = dftWinter.getStarsWebConfig().getLogoLocation();
		seasonStrR = dftWinter.getStarsWebConfig().getAlternateDisplayName().toLowerCase();
		modeStrR = StarsThermoModeSettings.HEAT.toString();
	}
	else {
		imgStrR = dftSummer.getStarsWebConfig().getLogoLocation();
		seasonStrR = dftSummer.getStarsWebConfig().getAlternateDisplayName().toLowerCase();
		modeStrR = StarsThermoModeSettings.COOL.toString();
	}
%>
                                  <span class="Clickable"><img src="<%= imgStrR %>" width="40" height="39" border="0" onClick="switchSettings('<%= dayStr %>','<%= modeStrR %>')"></span> 
                                </td>
                                <td align = "center" width="148" class="TableCell" valign="top">Click 
                                  icon to view <%= seasonStrR %> schedule </td>
                              </tr>
                            </table>
                          </td>
                          <td width="52%" align = "right" valign = "top">&nbsp;
<%
	if (dayStr.equalsIgnoreCase( StarsThermoDaySettings.WEEKDAY.toString() )) {
		String checkStr = (String) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_APPLY_TO_WEEKEND);
		if (checkStr == null) checkStr = "";
%>
                            <input type="checkbox" name="ApplyToWeekend" value="true" onclick="setScheduleChanged()" <%= checkStr %>>
                            Apply settings to Saturday<br>
                            and Sunday </td>
<%
	}
%>
                      </tr>
                    </table>
                  </td>
                </tr>
                <tr> 
                    <td align = "center"> 
                      <table width="500" border="0">
                        <tr> 
                          <td class = "TableCell" width="84%" height="4" align = "right" > 
                            <div align="center">This page allows you to set your 
                              ongoing temperature schedule. Simply slide the black 
                              arrows and thermometers to change temperatures and 
                              start times for each interval. To make temperary 
                              changes, click the <b>Manual</b> button to the right.</div>
                          </td>
                          <td class = "TableCell" width="16%" height="4" align = "right" valign="top" > 
                            <input type="button" name="Manual" value="Manual" onClick="location='Thermostat.jsp'">
                          </td>
                        </tr>
                      </table>
                      <br>
                    
                    <img src="TempBG.gif" style ="position:relative;">
                    <div id="MovingLayer1" style="position:absolute; width:25px; height:162px; left:309px; z-index:1; top: 400px">
                      <table width="100%" border="0">
                        <tr> 
                          <td>&nbsp;</td>
                          <td> 
                            <div id="div1" class="TableCell3" onchange="setScheduleChanged()"><%= schedule.getTemperature1() %>&deg</div>
                            <img src="Thermometer.gif" style = "position:relative; left:-5px" width="16" height="131"  onMouseDown = "setScheduleChanged();beginDrag(event,0,0,parseInt(document.getElementById('MovingLayer2').style.left)-3,200,'showTimeWake()','horizontal', 'MovingLayer1')"> 
                            <img id="arrow1" src="Arrow.gif" style = "position:relative; left:-11px; top:-100px" onMouseDown = "setScheduleChanged();beginDrag(event,-135,-35,0,0,'showTemp1()','vertical')"> 
                          </td>
                        </tr>
                      </table>
                    </div>
                    <div id="MovingLayer2" style="position:absolute; width:21px; height:162px; left:354px; z-index:2; top: 400px">
                      <table width="100%" border="0">
                        <tr> 
                          <td>&nbsp;</td>
                          <td> 
                            <div id="div2" class="TableCell3" onchange="setScheduleChanged()"><%= schedule.getTemperature2() %>&deg</div>
                            <img src="Thermometer.gif" style = "position:relative; left:-5px" width="16" height="131"  onMouseDown = "setScheduleChanged();beginDrag(event,0,0,parseInt(document.getElementById('MovingLayer3').style.left)-3,parseInt(document.getElementById('MovingLayer1').style.left)+3,'showTimeLeave()','horizontal', 'MovingLayer2')"> 
                            <img id="arrow2" src="Arrow.gif" style = "position:relative; left:-9px; top:-100px"  onMouseDown = "setScheduleChanged();beginDrag(event,-135,-35,0,0,'showTemp2()','vertical')"> 
                          </td>
                        </tr>
                      </table>
                    </div>
                    <div id="MovingLayer3" style="position:absolute;  width:21px; height:162px; left:507px; z-index:3; top: 400px">
                      <table width="100%" border="0">
                        <tr> 
                          <td>&nbsp;</td>
                          <td> 
                              <div id="div3" class="TableCell3" onchange="setScheduleChanged()"><%= schedule.getTemperature3() %>&deg</div>
                            <img src="Thermometer.gif" style = "position:relative; left:-5px" width="16" height="131"  onMouseDown = "setScheduleChanged();beginDrag(event,0,0,parseInt(document.getElementById('MovingLayer4').style.left)-3,parseInt(document.getElementById('MovingLayer2').style.left)+3,'showTimeReturn()','horizontal', 'MovingLayer3')"> 
                            <img id="arrow3" src="Arrow.gif" style = "position:relative; left:-9px; top:-100px"  onMouseDown = "setScheduleChanged();beginDrag(event,-135,-35,0,0,'showTemp3()','vertical')"> 
                          </td>
                        </tr>
                      </table>
                    </div>
                    <div id="MovingLayer4" style="position:absolute;  width:21px; height:162px; left:578px; z-index:4; top: 400px">
                      <table width="100%" border="0">
                        <tr> 
                          <td>&nbsp;</td>
                          <td> 
                            <div id="div4" class="TableCell3" onchange="setScheduleChanged()"><%= schedule.getTemperature4() %>&deg</div>
                            <img src="Thermometer.gif" style = "position:relative; left:-5px" width="16" height="131"  onMouseDown = "setScheduleChanged();beginDrag(event,0,0,629,parseInt(document.getElementById('MovingLayer3').style.left)+3,'showTimeSleep()','horizontal', 'MovingLayer4')"> 
                            <img id="arrow4" src="Arrow.gif" style = "position:relative; left:-9px; top:-100px;"  onMouseDown = "setScheduleChanged();beginDrag(event,-135,-35,0,0,'showTemp4()','vertical')"> 
                          </td>
                        </tr>
                      </table>
                    </div><br><br>
					
<script language="JavaScript">
updateLayout(
	<%= schedule.getTime1().getHour() %>,<%= schedule.getTime1().getMinute() %>,<%= schedule.getTemperature1() %>,
	<%= schedule.getTime2().getHour() %>,<%= schedule.getTime2().getMinute() %>,<%= schedule.getTemperature2() %>,
	<%= schedule.getTime3().getHour() %>,<%= schedule.getTime3().getMinute() %>,<%= schedule.getTemperature3() %>,
	<%= schedule.getTime4().getHour() %>,<%= schedule.getTime4().getMinute() %>,<%= schedule.getTemperature4() %>
);
</script>

                    <table width="100%" border="0" height="27">
                      <tr> 
                        <td width="25%" class = "TableCell" align = "center"><span class = "Main"><b>Wake</b> 
                          </span></td>
                        <td class = "TableCell" width="25%" align = "center"><span class = "Main"><b>Leave</b></span> 
                        </td>
                        <td class = "TableCell" width="25%" align = "center"><span class = "Main"><b>Return</b> 
                          </span></td>
                        <td width="25%" class = "TableCell" align = "center"><span class = "Main"><b>Sleep</b></span></td>
                      </tr>
                      <tr> 
                        <td width="25%" class = "TableCell"> Start At: 
                          <input id="time1" type="text" size="5" value="<%= timePart.format(schedule.getTime1().toDate()) %>" name="time1" onchange="Javascript:setScheduleChanged();timeChange(this, 'MovingLayer1', 'time2', null);">
                        </td>
                        <td class = "TableCell" width="25%"> Start At: 
                          <input id="time2" type="text" size="5" value="<%= timePart.format(schedule.getTime2().toDate()) %>" name="time2" onchange="Javascript:setScheduleChanged();timeChange(this, 'MovingLayer2', 'time3', 'time1');">
                        </td>
                        <td class = "TableCell" width="25%"> Start At: 
                          <input id="time3" type="text" size="5" value="<%= timePart.format(schedule.getTime3().toDate()) %>" name="time3" onchange="Javascript:setScheduleChanged();timeChange(this, 'MovingLayer3', 'time4', 'time2');">
                        </td>
                        <td width="25%" class = "TableCell"> Start At: 
                          <input id="time4" type="text" size="5" value="<%= timePart.format(schedule.getTime4().toDate()) %>" name="time4" onchange="Javascript:setScheduleChanged();timeChange(this, 'MovingLayer4', null, 'time3');">
                        </td>
                      </tr>
                    </table><noscript><table width="100%" border="0" class = "TableCell">
  <tr>
                        <td>Temp: <input id="temp1" type="text" size="3" name="temp1" onchange="setScheduleChanged()" value="<%= schedule.getTemperature1() %>"></td>
                        <td>Temp: <input id="temp2" type="text" size="3" name="temp2" onchange="setScheduleChanged()" value="<%= schedule.getTemperature2() %>"></td>
                        <td>Temp: <input id="temp3" type="text" size="3" name="temp3" onchange="setScheduleChanged()" value="<%= schedule.getTemperature3() %>"></td>
                        <td>Temp: <input id="temp4" type="text" size="3" name="temp4" onchange="setScheduleChanged()" value="<%= schedule.getTemperature4() %>"></td>
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
                    </noscript></td>
                </tr>
              </table><br>
              <table width="75%" border="0">
                <tr>
                    <td width="36%" align = "right" class = "TableCell" > 
                      <input type="submit" name="Submit" value="Submit" onclick="switchSettings('<%= dayStr %>','<%= modeStr %>')">
                  </td>
                    <td width="64%" align = "left" class = "TableCell"> 
                      <input type="button" id="Default" value="Recommended Settings" onclick="setToDefault()">
<script language="JavaScript">
	document.getElementById("Default").value = '<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_REC_SET_BTN %>"/>';
</script>
                  </td>
                </tr>
              </table>
			</form>
              <p align="center" class="Main"><font face="Arial, Helvetica, sans-serif" size="1">Copyright 
                &copy; 2003, Cannon Technologies, Inc. All rights reserved.<br>
                <br>
                </font></p>
                </div>
			
          </td>
		  
		  
        <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<br>
<div align="center"></div>
</body>
</html>
