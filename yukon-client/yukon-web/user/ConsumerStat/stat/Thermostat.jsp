<%@ include file="include/StarsHeader.jsp" %>
<%
	StarsThermoSettings thermoSettings = null;
	int invID = 0;
	int[] invIDs = null;
	
	int itemNo = Integer.parseInt(request.getParameter("Item"));
	if (itemNo == -1) {
		// Setup for multiple thermostats
		String[] invIDStrs = request.getParameterValues("InvID");
		if (invIDStrs != null) {
			invIDs = new int[invIDStrs.length];
			for (int i = 0; i < invIDs.length; i++)
				invIDs[i] = Integer.parseInt(invIDStrs[i]);
			
			Arrays.sort(invIDs);
			session.setAttribute(ServletUtils.ATT_THERMOSTAT_INVENTORY_IDS, invIDs);
		}
		else {
			invIDs = (int[]) session.getAttribute(ServletUtils.ATT_THERMOSTAT_INVENTORY_IDS);
			if (invIDs == null) {
				response.sendRedirect("AllTherm.jsp");
				return;
			}
		}
	}
	else {
		// Setup for a single thermostat
		StarsInventory thermostat = thermostats.getStarsInventory(itemNo);
		thermoSettings = thermostat.getLMHardware().getStarsThermostatSettings();
		invID = thermostat.getInventoryID();
	}
	
	StarsDefaultThermostatSettings dftThermoSettings = null;
	for (int i = 0; i < allDftThermoSettings.length; i++) {
		if (allDftThermoSettings[i].getThermostatType().getType() == StarsThermostatTypes.EXPRESSSTAT_TYPE) {
			dftThermoSettings = allDftThermoSettings[i];
			break;
		}
	}
	
	StarsThermostatManualEvent lastEvent = null;
	boolean useDefault = false;
	if (thermoSettings != null && thermoSettings.getStarsThermostatManualEventCount() > 0) {
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
	
	boolean runProgram = (lastEvent.getThermostatManualOption().getTemperature() == -1);
%>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

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
		document.MForm.hold.src = "../../../Images/ThermImages/HoldGray.gif";
		document.MForm.holdSetting.value = "on";
		document.MForm.resetSetting.value = "on";
		document.MForm.rst.src = "../../../Images/ThermImages/ResetBk.gif";
		document.MForm.msg.src = "../../../Images/ThermImages/HoldMsg.gif";
	}
}

function resetTemp() {
	if ( document.MForm.resetSetting.value == "on") {
		document.MForm.hold.src = "../../../Images/ThermImages/HoldBk.gif";
		document.MForm.holdSetting.value = "off";
		document.MForm.resetSetting.value = "off";
		document.MForm.rst.src = "../../../Images/ThermImages/ResetGray.gif";
		document.MForm.msg.src = "../../../Images/ThermImages/SchedMsg.gif";
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
if (browser.isNetscape) {
	key = String.fromCharCode(e.which);
}
 else if (browser.isMicrosoft) {
   key = String.fromCharCode(e.keyCode);
}

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

</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../../WebConfig/<cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_IMG_CORNER %>"/>">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
				  <td width="265" height="28">&nbsp;</td>
                  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">&nbsp;</td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
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
		  <% String pageName = "Thermostat.jsp?Item=" + itemNo; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF" bordercolor="#333399"> 
            <div align="center"><br>
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_THERM_MANUAL, "THERMOSTAT - MANUAL"); %>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  
              <div align = "left"> 
                <table border="0" width="93%">
                  <tr> 
                    <td width="25">&nbsp;</td>
                    <td width="576" class="TableCell">Please use the thermostat 
                      below to temporarily change your current settings. To adjust 
                      your thermostat's programming, please click the <%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule") %> 
                      link in the pop-up menu of the thermostat.</td>
                  </tr>
<%	if (itemNo == -1) { %>
                  <tr> 
                    <td width="25">&nbsp;</td>
                    <td width="576" class="MainText" align="right"><a href="AllTherm.jsp" class="Link1">Change 
                      Selected Thermostats</a></td>
                  </tr>
<%	} %>
                </table>
              </div>
			  <form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/SOAPClient">
				<input type="hidden" name="action" value="UpdateThermostatOption">
				<input type="hidden" name="InvID" value="<%= invID %>">
<%	if (invIDs != null) {
		for (int i = 0; i < invIDs.length; i++) {
%>
				<input type="hidden" name="InvIDs" value="<%= invIDs[i] %>">
<%		}
	}
%>
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?Item=<%= itemNo %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?Item=<%= itemNo %>">
				<input type="hidden" name="mode" value="">
				<input type="hidden" name="fan" value="">
				<input type="hidden" name="RunProgram" value="false">
                <div align = "left">
				  <table width="93%" border="0" background="../../../Images/ThermImages/Bkgd.gif" style = "background-repeat:no-repeat" cellspacing = "0" cellpadding = "0" height="246">
                    <tr> 
                      <td width="73%" height="40" > 
                        <table width="91%" border="0" height="100">
                          <tr> 
                            <td width="38%" height="113">&nbsp;</td>
                            <td width="31%" height="113" align = "center" valign="bottom"> 
                              <table width="18%" border="0" cellspacing = "0" cellpadding ="0" height="60" >
                                <tr> 
                                  <td width="52%" height="53"> 
                                    <input type="text" name="tempField" maxlength="2" class="tempText1" value="<%= (runProgram)? 72 : lastEvent.getThermostatManualOption().getTemperature() %>" onkeypress="validateTemp(event)">
                                  </td>
                                  <td width="48%" height="53"> 
                                    <table width="41%" border="0" cellspacing = "0" cellpadding = "0">
                                      <tr> 
                                        <td><img class="Clickable" src="../../../Images/ThermImages/UpBk.gif" width="21" height="21" onclick = "incTemp()"></td>
                                      </tr>
                                      <tr> 
                                        <td><img class="Clickable" src="../../../Images/ThermImages/DownBk.gif" width="21" height="21" onclick = "decTemp()"></td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                              <table width="79%" border="0" cellpadding = "0" cellspacing = "0">
                                <tr> 
                                  <td>
                                    <input type="checkbox" name="hold" value="true">
                                    <img src="../../../Images/ThermImages/Hold.gif" width="34" height="9"></td>
                                </tr>
                              </table>
                            </td>
                            <td width="31%" height="113" valign="bottom"><br>
                              <table width="100" height="80" border="0">
                                <tr>
                                  <td class="TableCell" valign="top" bordercolor="#FFFFFF"><b>Last 
                                    Settings:</b><br>
<%	if (useDefault) out.write("(None)");
	else {
%>
									Date: <%= datePart.format(lastEvent.getEventDateTime()) %><br>
<%		if (lastEvent.getThermostatManualOption().getTemperature() == -1) { %>
									Run Program
<%		} else { %>
									Temp: <%= lastEvent.getThermostatManualOption().getTemperature() %>&deg 
									<% if (lastEvent.getThermostatManualOption().getHold()) out.print("(Hold)"); %><br>
                                    Mode: <%= modeStr %><br>
                                    Fan: <%= fanStr %>
<%		}
	}
%>
								  </td>
                                </tr>
                              </table>
                              
                            </td>
                          </tr>
                        </table>
                        <table width="91%" border="0" height="100">
                          <tr> 
                            <td width="31"></td>
                            <td width="133" valign="bottom"><img class="Clickable" src="../../../Images/ThermImages/Run.gif" onClick="document.MForm.RunProgram.value='true';submitIt()"></td>
                            <td width="124"> 
                              <table width="100%" border="0" height="56" cellspacing = "0" cellpadding = "0">
                                <tr> 
                                  <td width="60%"  height="59" valign = "bottom"> 
                                    <table width="35%" border="0" cellpadding = "2" cellspacing = "0" height="41">
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.COOL.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.COOL.toString() %>" src="../../../Images/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="../../../Images/ThermImages/Cool.gif"></td>
                                      </tr>
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.HEAT.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.HEAT.toString() %>" src="../../../Images/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="../../../Images/ThermImages/Heat.gif"></td>
                                      </tr>
                                      <tr onClick="modeChange('<%= StarsThermoModeSettings.OFF.toString() %>')"> 
                                        <td><img id="<%= StarsThermoModeSettings.OFF.toString() %>" src="../../../Images/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="../../../Images/ThermImages/Off.gif"></td>
                                      </tr>
                                    </table>
                                  </td>
                                  <td width="40%" height="59" valign = "bottom"> 
                                    <table width="34%" border="0" cellpadding = "2" cellspacing = "0">
                                      <tr onClick="fanChange('<%= StarsThermoFanSettings.AUTO.toString() %>')"> 
                                        <td ><img id="<%= StarsThermoFanSettings.AUTO.toString() %>" src="../../../Images/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="../../../Images/ThermImages/Auto.gif"></td>
                                      </tr>
                                      <tr onClick="fanChange('<%= StarsThermoFanSettings.ON.toString() %>')"> 
                                        <td><img id="<%= StarsThermoFanSettings.ON.toString() %>" src="../../../Images/ThermImages/ArrowBlue.gif" style="visibility:hidden"></td>
                                        <td><img class="Clickable" src="../../../Images/ThermImages/On.gif"></td>
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
                                  <td><img class="Clickable" src="../../../Images/ThermImages/Submit.gif" onClick = "submitIt()"></td>
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
                
              <p align="center" class="MainText"><font face="Arial, Helvetica, sans-serif" size="1">Copyright 
                &copy; 2003, Cannon Technologies, Inc. All rights reserved.</font> 
              </p>
              <p align="center" class="MainText">&nbsp; </p>
                </div>
			
          </td>
		  
		  
        <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<br>
<div align="center"></div>
</body>
</html>
