<%@ include file="StarsHeader.jsp" %>
<%
	StarsThermostatManualOption thermOption = thermoSettings.getStarsThermostatManualOption();
	if (thermOption == null)
		thermOption = dftThermoSettings.getStarsThermostatManualOption();
%>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../demostyle.css" type="text/css">
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
		document.getElementById(document.MForm.mode.value).style.visibility = "hidden";
		document.getElementById(mode).style.visibility = 'visible';
		document.MForm.mode.value = mode;
		
		if(mode == '<%= StarsThermoModeSettings.HEAT.toString() %>') {
		document.MForm.tempField.style.color = "#FF0000";
		}
		else if (mode == '<%= StarsThermoModeSettings.COOL.toString() %>'){
		document.MForm.tempField.style.color = "#003366";
		}
		else{ 
		document.MForm.tempField.style.color = "#CCCCCC";
		}
	}
}

function fanChange(fan) {
	if (document.getElementById(fan).style.visibility == "hidden") {
		document.getElementById(document.MForm.fan.value).style.visibility = "hidden";
		document.getElementById(fan).style.visibility = 'visible';
		document.MForm.fan.value = fan;
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
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../Header.gif">&nbsp;</td>
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
		  <% String pageName = "Thermostat.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF" bordercolor="#333399"> 
            <div align="center"><br>
              <table width="600" border="0" cellspacing="0">
                <tr> 
                  <td width="202"> 
                    <table width="200" border="0" cellspacing="0" cellpadding="3">
                      <tr> 
                        <td><span class="Main"><b>Acct #<%= account.getAccountNumber() %></b></span><br>
                          <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
                          <!--<%= account.getCompany() %><br> -->
                          <%= propAddr.getStreetAddr1() %>, <%= propAddr.getStreetAddr2() %><br>
                          <%= propAddr.getCity() %>, <%= propAddr.getState() %> <%= propAddr.getZip() %><br>
                          <%= primContact.getHomePhone() %></span></td>
                      </tr>
                    </table>
                  </td>
                  <td width="187" valign="top"> 
                    <div align="center"><b><span class="Main">THERMOSTAT - MANUAL</span></b></div>
                  </td>
                  <td valign="top" width="205" align = "right"> <%@ include file="Notice.jsp" %>
                    
                  </td>
                </tr>
              </table>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
              <span class="TableCell"> </span><div align = "left">
                <table width="632" border="0" height="47">
                  <tr>
                    <td width="25">&nbsp;</td>
                    <td width="597"><span class="TableCell"> 
                      <p>Use the Thermostat - Schedule (clickable at left) to 
                        set your daily temperatures. If you would like to temporarily 
                        change these temperatures, use the thermostat below. </p>
                      </span></td>
                  </tr>
                </table>
              </div>
              <span class="TableCell">
              </span>
<form name="MForm" method="post" action="/servlet/SOAPClient">
			  <input type="hidden" name="action" value="UpdateThermostatOption">
			  <input type="hidden" name="holdSetting" value = "off">
			  <input type="hidden" name="resetSetting" value = "off">
			  <input type="hidden" name="mode" value="<%= thermOption.getMode() %>">
			  <input type="hidden" name="fan" value="<%= thermOption.getFan() %>">
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
                                    <input type="text" name="tempField" maxlength="2" class="tempText1" value="<%= thermOption.getTemperature() %>" onkeypress="validateTemp(event)">
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
                                    <input type="checkbox" name="hold" value="true" <% if (thermOption.getHold()) out.print("checked"); %>>
                                    <img src="ThermImages/Hold.gif" width="34" height="9"></td>
                                </tr>
                              </table>
                            </td>
                            <td width="31%" height="113" valign="middle"><br>
                              <table width="100" border="0">
                                <tr>
                                  <td class="TableCell" bordercolor="#FFFFFF">Last 
                                    setting:<br>
                                    Temperature: <%= thermOption.getTemperature() %>&deg; 
									<% if (thermOption.getHold()) out.print("(HOLD)"); %><br>
                                    Mode: <%= thermOption.getMode() %><br>
                                    Fan: <%= thermOption.getFan() %></td>
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
                                      <tr> 
                                        <td><img id="<%= StarsThermoModeSettings.COOL.toString() %>" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/Cool.gif" onClick="modeChange('<%= StarsThermoModeSettings.COOL.toString() %>')" ></td>
                                      </tr>
                                      <tr> 
                                        <td><img id="<%= StarsThermoModeSettings.HEAT.toString() %>" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/Heat.gif" onClick = "modeChange('<%= StarsThermoModeSettings.HEAT.toString() %>')"></td>
                                      </tr>
                                      <tr> 
                                        <td><img id="<%= StarsThermoModeSettings.OFF.toString() %>" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/Off.gif" onClick = "modeChange('<%= StarsThermoModeSettings.OFF.toString() %>')"></td>
                                      </tr>
                                    </table>
                                  </td>
                                  <td width="47%" height="59" valign = "bottom"> 
                                    <table width="34%" border="0" cellpadding = "2" cellspacing = "0">
                                      <tr> 
                                        <td ><img id = "auto" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/Auto.gif" onClick = "fanChange('auto')"></td>
                                      </tr>
                                      <tr> 
                                        <td><img id = "on" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/On.gif" onClick = "fanChange('on')"></td>
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
                        <p>Click the up and down arrows until you reach the temperature 
                          you would like. This temperature will be set until the 
                          next schedule change. Check <b>HOLD</b> to maintain this setting 
                          across schedule changes.</p>
                        <p>You may also change the <b>MODE</b> and <b>FAN</b> settings.</p>
                        <p> BE SURE TO CLICK <b>SUBMIT</b> WHEN DONE!!</p>
                        <p>&nbsp;</p>
                      </td>
                    </tr>
                  </table>
                </div>
              </form>
<script language="JavaScript">
	modeChange('<%= thermOption.getMode() %>');
	fanChange('<%= thermOption.getFan() %>');
</script>
                
              <p align="center" class="Main"><font face="Arial, Helvetica, sans-serif" size="1">Copyright 
                &copy; 2002, Cannon Technologies, Inc. All rights reserved.</font> 
              </p>
              <p align="center" class="Main">&nbsp; </p>
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
