<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../demostyle.css" type="text/css">
<script langauge = "JavaScript">
<!-- 
function clearText(n) {
	for (var i=0; i<document.MForm.elements.length; i++) {
		if (n == document.MForm.elements[i].name){
			document.MForm.elements[i].value = " ";
			}}}

function isblur(textBox, buttons) {
var rButton;
for (var i = 0; i<buttons.length; i++) {
	if (buttons[i].value == textBox.name)
	{
		rButton = buttons[i];
		break;}
	}
	if (!rButton.checked) {
		textBox.blur();}	
}

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
	if (document.getElementById(mode).style.visibility = 'hidden') {
		document.getElementById(mode).style.visibility = 'visible';
	}
	document.getElementById(document.MForm.mode.value).style.visibility = "hidden";
	document.MForm.mode.value = mode;
}

function fanChange(fan) {
	if (document.getElementById(fan).style.visibility = 'hidden') {
		document.getElementById(fan).style.visibility = 'visible';
	}
	document.getElementById(document.MForm.fan.value).style.visibility = "hidden";
	document.MForm.fan.value = fan;
}
function submitIt() {

document.MForm.msg.src = "ThermImages/TempMsg.gif"
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
              <span class="TableCell"> </span>
              <table width="400" border="0">
                <tr>
                  <td><span class="TableCell">
                    <p>Use the Thermostat - Schedule to set your daily temperatures. 
                      If you would like to temporarily change these temperatures, 
                      use the thermostat below.</p>
                    </span> </td>
                </tr>
              </table>
              <span class="TableCell">
              </span>
<form name="MForm" method="post" action="">
              <div align = "left">
                  <table width="93%" border="0" background="ThermImages/Bkgd.gif" style = "background-repeat:no-repeat" cellspacing = "0" cellpadding = "0" height="246">
                    <tr> 
                      <td width="73%" height="40" > 
                        <table width="91%" border="0" height="215">
                          <tr> 
                            <td width="38%" height="113">&nbsp;</td>
                            <td width="31%" height="113" align = "center"> 
                              <table width="29%" border="0" cellspacing = "0" cellpadding ="0" height="72" >
                                <tr> 
                                  <td > 
                                    <input type="text" name="tempField" maxlength = "3"  class = "tempText1" value = "72" >
                                  </td>
                                  <td width="78%" height="53"> 
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
                              <table width="79%" border="0" cellpadding = "0" cellspacing = "0" height="23">
                                <tr> 
                                  <td><img name = "hold" src="ThermImages/HoldBk.gif" width="45" height="27" onclick = "holdTemp()"><img name = "rst" src="ThermImages/ResetGray.gif" width="51" height="27" onclick = "resetTemp()"></td>
                                </tr>
                              </table>
                            </td>
                            <td width="31%" height="113"><img name = "msg" src="ThermImages/SchedMsg.gif"></td>
                          </tr>
                          <tr> 
                            <td width="38%">&nbsp;</td>
                            <td width="31%"> 
                              <table width="106%" border="0" height="61" cellspacing = "0" cellpadding = "0">
                                <tr> 
                                  <td width="58%"  height="59" valign = "bottom"> 
                                    <table width="35%" border="0" cellpadding = "2" cellspacing = "0" height="41">
                                      <tr> 
                                        <td><img id = "cool" src="ThermImages/Arrow.gif" style = "visibility:visible"></td>
                                        <td><img src="ThermImages/Cool.gif" onclick = "modeChange('cool')" ></td>
                                      </tr>
                                      <tr> 
                                        <td><img id = "heat" src="ThermImages/Arrow.gif" style = "visibility:hidden"></td>
                                        <td><img src="ThermImages/Heat.gif" onclick = "modeChange('heat')"></td>
                                      </tr>
                                      <tr> 
                                        <td><img id = "off1" src="ThermImages/Arrow.gif" style = "visibility:hidden"></td>
                                        <td><img src="ThermImages/Off.gif" onclick = "modeChange('off1')"></td>
                                      </tr>
                                    </table>
                                  </td>
                                  <td width="42%" height="59" valign = "bottom"> 
                                    <table width="34%" border="0" cellpadding = "2" cellspacing = "0">
                                      <tr> 
                                        <td ><img name = "auto" src="ThermImages/Arrow.gif"></td>
                                        <td><img src="ThermImages/Auto.gif" onclick = "fanChange('auto')"></td>
                                      </tr>
                                      <tr> 
                                        <td><img name = "on" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/On.gif" onclick = "fanChange('on')"></td>
                                      </tr>
                                      <tr> 
                                        <td><img name = "off2" src="ThermImages/Arrow.gif" style="visibility:hidden"></td>
                                        <td><img src="ThermImages/Off.gif" onclick = "fanChange('off2')"></td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                            </td>
                            <td width="31%" valign = "bottom" align = "right"> 
                              <table width="84%" border="0" height="45">
                                <tr> 
                                  <td><img src="ThermImages/Submit.gif" width="65" height="40" onclick = "submitIt()"></td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                          <tr> 
                            <td width="38%">&nbsp;</td>
                            <td width="31%">&nbsp;</td>
                            <td width="31%" valign = "bottom" align = "right">&nbsp;</td>
                          </tr>
                        </table>
                      </td>
                      <td width="27%" height="40" class="TableCell">
                        <p>Click the up and down arrows until you reach the temperature 
                          you would like. Then, either click Hold to set this 
                          temperature until Reset is selected, or click Submit 
                          to set this temperature until the next schedule change.</p>
                        <p>You may also change the MODE and FAN settings, then 
                          click Submit.</p>
                        <p>&nbsp;</p>
                        <p>&nbsp;</p>
                      </td>
                    </tr>
                  </table>
                  <input type="hidden" name="holdSetting" value = "off">
                  <input type="hidden" name="resetSetting" value = "off">
				  <input type="hidden" name="mode" value = "cool">
				  <input type="hidden" name="fan" value = "auto">
                </div>
            
              </form>
                
              <p align="center" class="Main"><font face="Arial, Helvetica, sans-serif" size="1">Copyright 
                &copy; 2002, Cannon Technologies, Inc. All rights reserved.</font> 
              </p>
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
