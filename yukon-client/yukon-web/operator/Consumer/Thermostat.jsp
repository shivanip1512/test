<%@ include file="StarsHeader.jsp" %>
<%
	StarsThermostatManualEvent lastEvent = null;
	boolean useDefault = false;
	if (thermoSettings.getStarsThermostatManualEventCount() > 0) {
		lastEvent = thermoSettings.getStarsThermostatManualEvent(
				thermoSettings.getStarsThermostatManualEventCount() - 1);
	}
	else {
		lastEvent = dftThermoSettings.getStarsThermostatManualEvent(
				dftThermoSettings.getStarsThermostatManualEventCount() - 1);
		useDefault = true;
	}
	StarsThermoModeSettings mode = lastEvent.getThermostatManualOption().getMode();
	String modeStr = (mode != null) ? mode.toString() : "";
	StarsThermoFanSettings fan = lastEvent.getThermostatManualOption().getFan();
	String fanStr = (fan != null) ? fan.toString() : "";
%>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
</script>

<script langauge = "JavaScript">
<!-- 
var browser = new Object();

browser.isNetscape = false;
browser.isMicrosoft = false;
if (navigator.appName.indexOf("Netscape") != -1)
	browser.isNetscape = true;
else if (navigator.appName.indexOf("Microsoft") != -1)
	browser.isMicrosoft = true;

function checkTemp(n, type) {
if (n.value >10) {
confirm("Are you sure you would like to " + type + " the temperature " + n.value + " degrees?");
}
}

function incTemp() {
	var curTemp = parseInt(document.MForm.tempField.value) + 1;
	if (curTemp <= 88)
		document.MForm.tempField.value = curTemp;
}

function decTemp() {
	var curTemp = parseInt(document.MForm.tempField.value) - 1;
	if (curTemp >= 45)
	document.MForm.tempField.value = curTemp;
}

function holdTemp() {
	if (document.MForm.holdSetting.value == "off") {
			document.MForm.hold.src = "ThermImages/HoldGray.gif";
			document.MForm.holdSetting.value = "on";
			document.MForm.resetSetting.value = "on";
			document.MForm.rst.src = "ThermImages/ResetBk.gif";
			document.MForm.msg.src = "ThermImages/HoldMsg.gif";
			}
}

function resetTemp() {
	if ( document.MForm.resetSetting.value == "on") {
			document.MForm.hold.src = "ThermImages/HoldBk.gif";
			document.MForm.holdSetting.value = "off";
			document.MForm.resetSetting.value = "off";
			document.MForm.rst.src = "ThermImages/ResetGray.gif";
			document.MForm.msg.src = "ThermImages/SchedMsg.gif";
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
		document.MForm.tempField.style.color = "#000000";
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
if (browser.isNetscape) {
	key = String.fromCharCode(e.which);
}
 else if (browser.isMicrosoft) {
   key = String.fromCharCode(e.keyCode);
}

var text = document.MForm.tempField.value + key;
if (text.length == 2) {
	if (Number(key) || Number(key) == 0){
		val = parseInt(document.MForm.tempField.value + key);
		
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

</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td id="Header" colspan="4" height="74" background="../Header.gif">&nbsp;</td>
<script language="JavaScript">
	document.getElementById("Header").background = '../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>';
</script>
              </tr>
              <tr> 
				  <td width="265" height="28" valign="middle" class="Header3">&nbsp;&nbsp;&nbsp;Customer 
                  Account Information&nbsp;&nbsp;</td>
                  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">
				  	<div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
				  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
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
		  <% String pageName = "Thermostat.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF" bordercolor="#333399"> 
            <div align="center"> 
              <% String header = "THERMOSTAT - MANUAL"; %>
              <%@ include file="InfoSearchBar.jsp" %>
              <% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  <div align = "left">
                <table width="632" border="0" height="47">
                  <tr>
                    <td width="25">&nbsp;</td>
                    <td width="597"><span class="TableCell"> 
                      <p>Use the Thermostat - Schedule (clickable at left) to 
                        set your temperature schedule. If you would like to temporarily 
                        change these temperatures, use the thermostat below. </p>
                      </span></td>
                  </tr>
                </table>
              </div>
              <form name="MForm" method="post" action="/servlet/SOAPClient">
			  <input type="hidden" name="action" value="UpdateThermostatOption">
			  <input type="hidden" name="REDIRECT" value="/operator/Consumer/Thermostat.jsp">
			  <input type="hidden" name="REFERRER" value="/operator/Consumer/Thermostat.jsp">
			  <input type="hidden" name="mode" value="">
			  <input type="hidden" name="fan" value="">
              <div align = "left">
                  <table width="93%" border="0" background="ThermImages/Bkgd.gif" style = "background-repeat:no-repeat" cellspacing = "0" cellpadding = "0" height="246">
                    <tr> 
                      <td width="73%" height="40" > 
                        <table width="91%" border="0" height="100">
                          <tr> 
                            <td width="38%" height="113">&nbsp;</td>
                            <td width="31%" height="113" align = "center" valign="bottom"> 
                              <table width="18%" border="0" cellspacing = "0" cellpadding ="0" height="60" >
                                <tr> 
                                  <td width="52%" height="53"> 
                                    <input type="text" name="tempField" maxlength="2" class="tempText1" style.color="#CCCCCC" value="<%= lastEvent.getThermostatManualOption().getTemperature() %>" onkeypress="validateTemp(event)">
                                  </td>
                                  <td width="48%" height="53"> 
                                    <table width="41%" border="0" cellspacing = "0" cellpadding = "0">
                                      <tr> 
                                        <td><img src="ThermImages/UpBk.gif" width="21" height="21" onclick = "incTemp()"></td>
                                      </tr>
                                      <tr> 
                                        <td><img src="ThermImages/DownBk.gif" width="21" height="21" onclick = "decTemp()"></td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                              <table width="79%" border="0" cellpadding = "0" cellspacing = "0">
                                <tr> 
                                  <td>
                                    <input type="checkbox" name="hold" value="true">
                                    <img src="ThermImages/Hold.gif" width="34" height="9"></td>
                                </tr>
                              </table>
                            </td>
                            <td width="31%" height="113" valign="bottom"><br>
                              <table width="100" height="80" border="0">
                                <tr>
                                  <td class="TableCell" valign="top" bordercolor="#FFFFFF">Last 
                                    Settings:<br>
<%
	if (useDefault) out.write("(None)");
	else {
%>									
									Temperature: <%= lastEvent.getThermostatManualOption().getTemperature() %>&deg 
									<% if (lastEvent.getThermostatManualOption().getHold()) out.print("(HOLD)"); %><br>
                                    Mode: <%= modeStr %><br>
                                    Fan: <%= fanStr %>
<%
	}
%>
								  </td>
                                </tr>
                              </table>
                              
                            </td>
                          </tr>
                        </table>  <table width="91%" border="0" height="100">
                          <tr> 
                            <td width="42%">&nbsp;</td>
                            <td width="32%"> 
                              <table width="106%" border="0" height="56" cellspacing = "0" cellpadding = "0">
                                <tr> 
                                  <td width="53%"  height="59" valign = "bottom"> 
                                    <table width="35%" border="0" cellpadding = "2" cellspacing = "0" height="41">
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.COOL.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.COOL.toString() %>" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/Cool.gif"></td>
                                      </tr>
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.HEAT.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.HEAT.toString() %>" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/Heat.gif"></td>
                                      </tr>
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.OFF.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.OFF.toString() %>" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/Off.gif"></td>
                                      </tr>
                                    </table>
                                  </td>
                                  <td width="47%" height="59" valign = "bottom"> 
                                    <table width="34%" border="0" cellpadding = "2" cellspacing = "0">
                                      <tr onClick="fanChange('<%= StarsThermoFanSettings.AUTO.toString() %>')"> 
                                        <td ><img id="<%= StarsThermoFanSettings.AUTO.toString() %>" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/Auto.gif"></td>
                                      </tr>
                                      <tr onClick="fanChange('<%= StarsThermoFanSettings.ON.toString() %>')"> 
                                        <td><img id="<%= StarsThermoFanSettings.ON.toString() %>" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/On.gif"></td>
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
                            <td width="26%" valign = "middle" align = "center"> 
                              <div align="left"></div>
                              <table width="84%" border="0" height="45">
                                <tr> 
                                  <td><img src="ThermImages/Submit.gif" width="65" height="40" onClick = "submitIt()"></td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                      <td width="27%" height="40" class="TableCell"> 
                        <p>Click the up and down arrows until reaching the temperature 
                          you would like. This temperature will be set until the 
                          next schedule change. Check <b>HOLD</b> to maintain 
                          this setting across schedule changes.</p>
                        <p>You may also change the <b>MODE</b> and <b>FAN</b> 
                          settings by clicking the selection.</p>
                        <p> BE SURE TO CLICK <b>SUBMIT</b> WHEN DONE!!</p>
                        <p>&nbsp;</p>
                      </td>
                    </tr>
                  </table>
                </div>
              </form>
                
              <p align="center" class="Main"><font face="Arial, Helvetica, sans-serif" size="1">Copyright 
                &copy; 2003, Cannon Technologies, Inc. All rights reserved.</font> 
              </p>
              <p align="center" class="Main">&nbsp; </p>
                </div>
			
          </td>
		  
		  
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<br>
<div align="center"></div>
</body>
</html>
