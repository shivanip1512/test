<%@ include file="../include/user_header.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>"type="text/css">
<script language="JavaScript" src="../../JavaScript/drag.js">
</script>
<script language="JavaScript">
<!--

var browser = new Object();

browser.isNetscape = false;
browser.isMicrosoft = false;
if (navigator.appName.indexOf("Netscape") != -1)
	browser.isNetscape = true;
else if (navigator.appName.indexOf("Microsoft") != -1)
	browser.isMicrosoft = true;


function moveStartStopPtr(s) {
var hour;
var minute;
var time;
if (s == 'start')  
 time = document.rooftop.startTime1.value.split(":");
else if (s == 'stop')
 time = document.rooftop.stopTime1.value.split(":");
if (time.length == 2) {
	hour = parseInt(time[0]);
	minute = parseInt(time[1]);}
else if (time.length == 1){
	hour = parseInt(time[0]);
	minute = 0;}
else{
	hour = 0;
	minute = 0;}
	
if (hour > 23){   
	hour = 23;
	minute = 50;}
else 
if (hour < 0)
	hour = 0;

if (hour == 23 && minute > 50)
	minute = 50;

if (minute < 0 || minute > 59) 
	minute = 0;

var lgMove = 6 * hour + 27;
var smMove = Math.floor(minute/10);

hour = "0" + hour;
minute = "0" + smMove * 10;
minute = minute.substr(minute.length-2, 2);
hour = hour.substr(hour.length-2, 2);

if (s == 'start') {
document.rooftop.startTime1.value = hour + ":" + minute;
document.rooftop.startPtr.style.left = lgMove + smMove;}
else if (s == 'stop') {
document.rooftop.stopTime1.value = hour + ":" + minute;
document.rooftop.stopPtr.style.left = lgMove + smMove;
}
}

function showTempNS()
{
  var a = document.images['arrow'];
  var curPos = parseInt(a.style.top);
  var temp = Math.floor((160 - curPos) / 280 * 70 + 40);
  document.rooftop.temperature.value = temp;
}

function showTempIE()
{
  var a = document.images['arrow'];
  var curPos = parseInt(a.style.top);
  var temp = Math.floor((80 - curPos) / 200 * 70 + 40);
  rooftop.temperature.value = temp;
}


function moveTempPtrNS() {
	var temp = document.rooftop.temperature.value;
	if (temp < 40) {
		temp = 40;
		document.rooftop.temperature.value = 40;}
	else if (temp > 110){
		temp = 110;
		document.rooftop.temperature.value = 110;}
	var curPos = Math.floor(160 - (280 *(temp-40) / 70));
	var a = document.images['arrow'];
	a.style.top = curPos;}


function moveTempPtrIE() {
var temp = rooftop.temperature.value;
if (temp < 40) {
	temp = 40;
	rooftop.temperature.value = 40;}
else if (temp > 110) {
	temp = 110;
	rooftop.temperature.value = 110;}
var curPos = Math.floor(80 - (200 *(temp-40) / 70));
var a = document.images['arrow'];
a.style.top = curPos;
}

function showStartTime1(){
 var s = document.images['startPtr'];
 var curPos = parseInt(s.style.left);
 var hourStr = "0" + Math.floor((curPos - 99 + 72) * 10 / 60);
 hourStr = hourStr.substr(hourStr.length-2, 2);
 var minuteStr = "0" + (curPos - 99 + 72) * 10 % 60;
 minuteStr = minuteStr.substr(minuteStr.length-2, 2);
 document.rooftop.startTime1.value = hourStr + ":" + minuteStr;
}

function showStopTime1(){
 
  var sp = document.images['stopPtr'];
  var curPos = parseInt(sp.style.left);
  var hourStr = "0" + Math.floor((curPos - 99 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos-99 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
  document.rooftop.stopTime1.value = hourStr + ":" + minuteStr;
  }
  
function setPixTemp() {
(navigator.appName == 'Netscape')?document.images['arrow'].style.top =20+"px":document.images['arrow'].style.top = -20 + "px";
(navigator.appName == 'Netscape')?document.rooftop.temperature.value = '75':rooftop.temperature.value='75';

}

function setPixTime() {

(navigator.appName == 'Netscape')?document.images['startPtr'].style.top = -18 +"px":document.images['startPtr'].style.top = -31 + "px";
(navigator.appName == 'Netscape')?document.images['startPtr'].style.left = 27 +"px":document.images['startPtr'].style.left = 27 + "px";
 document.rooftop.startTime1.value = "00:00";
(navigator.appName == 'Netscape')?document.images['stopPtr'].style.top = 66 +"px":document.images['stopPtr'].style.top = 33 + "px";
(navigator.appName == 'Netscape')?document.images['stopPtr'].style.left = 170 +"px":document.images['stopPtr'].style.left = 170 + "px";
document.rooftop.stopTime1.value = "23:50";
}



function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
// -->
//-->
</script>
<script language="JavaScript">
<!--
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}

// -->
</script>
<script language="JavaScript">
<!--

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}
//-->
</script>
<!-- Java script needed for the Calender Function--->
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../../JavaScript/Calendar1-82.js"></SCRIPT>
<SCRIPT Language="Javascript">
function fsub() {
	document.MForm.submit();
}

function TryCallFunction() {
	var sd = document.MForm.mydate1.value.split("-");
	document.MForm.iday.value = sd[1];
	document.MForm.imonth.value = sd[0];
	document.MForm.iyear.value = sd[2];
}

function Today() {
	var dd = new Date();
	return((dd.getMonth()+1) + "/" + dd.getDate() + "/" + dd.getFullYear());
}

</SCRIPT>
<script language="JavaScript">
<!--
function MM_popupMsg(msg) { //v1.0
  alert(msg);
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
          <td width="150" height="102" background="../../WebConfig/yukon/MomWide.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height="28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;User Control</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
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
          <td  valign="top" width="150"> 
            <% String pageName = "user_lm_control.jsp"; %>
            <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="600" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <p><br>
                <span class="TitleHeader">AUTO CONTROL</span><br>
                <span class="MainText">Please select one of the following Trigger Sets:</span></p>
              <table width="100%" border="0" cellpadding="1">
                <tr> 
                  <form method="post" action="user_lm_control.jsp">
                    <td> 
                      <div align="center"> 
                        <input type="submit" name="tab" value="Trigger Set 1">
                      </div>
                    </td>
                  </form>
                </tr>
              </table>
              <p><span class="TableCell">Trigger Set 2 - Currently not controlling 
                <br>
                Your Last Control:<br>
                07/10/02 12:50 - 07/10/02 16:50</span><br>
                <br>
              </p><form name="rooftop" method="post" action="">

              <table width="600" border="2" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
                <tr> 
                  <td> 
                    <table width="550" border="0" cellspacing="5" cellpadding="0" align="center">
                      <tr> 
                        <td width="251" valign="top"> 
                          <div align="center"> 
                            <div class="TableCell">Drag the slider to set a new 
                              Control Temperature:&nbsp;</div>
							
							
                            <table width="200" border="0" cellspacing="5" cellpadding="0">
                              <tr> 
                                <td bgcolor="#FFFFFF" align="center" valign="middle"> 
                                  <div align="center"> 
                                    <p class="TableCell">Current Temperature: 
                                      70&deg; 
                                    <table width="150" border="0" cellspacing="0" height="295" align="left">
                                      <tr> 
                                        <td height="295" width="74"> 

                                          <div id="tempDiv" align="center" class="TableCell"><b>Begin 
                                            Control at:<br>
                                            <input id = "temperature" type="text" value="75" size="3" onchange = "(navigator.appName == 'Netscape')?moveTempPtrNS():moveTempPtrIE()" >
                                            &nbsp;<sup>&deg</sup> </b></div>
                                        </td>
                                        <td height="295" width="72" background = "../../WebConfig/yukon/Parts/ThermometerBG.jpg"> 
                                          <img name = "arrow" id= "arrow" onmousedown = "(navigator.appName == 'Netscape')?beginDrag(event,-120,160,0,0,'showTempNS()','vertical',''):beginDrag(event,-120,80,0,0,'showTempIE()','vertical','');" src="../../WebConfig/yukon/Parts/ThermSlider.gif"  onload = "setPixTemp()" style = "position:relative; top:0px; left:8px; cursor:pointer"> 
                                        </td>
                                      </tr>
                                    </table>
                                  </div>
                                  <br>
                                </td>
                              </tr>
                              <tr> 
                                <td bgcolor="#FFFFFF" width = "25%" align="right" valign="middle" bordercolor="#000000"> 
                                    <table width="82%" cellspacing="0" cellpadding="0" border="2" bordercolor="#999999" bgcolor="#CCCCCC" height="66">
                                      <tr> 
                                      <td> 
                                          <table width="100%"  bgcolor="#CCCCCC" bordercolor="#999999" height="61">
                                            <tr align = "center"> 
                                              <td width="33%"> 
                                                <input type="radio" name="radiobutton" value="on" >
                                            </td>
                                              <td width="33%"> 
                                                <input type="radio" name="radiobutton" value="off" checked>
                                            </td>
                                              <td width="33%"> 
                                                <input type="radio" name="radiobutton" value="today">
                                            </td>
                                          </tr>
                                          <tr class = "TableCell2" align = "center"> 
                                              <td width="31%" valign = "top">On</td>
                                              <td width="33%" valign = "top">Off</td>
                                              <td width="36%">Today Only</td>
                                          </tr>
                                        </table>
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </div>
                        </td>
                        <td width="284" valign="top"> 
                          <p align="center" class="TableCell">Your Valid Auto 
                            Control Time Range:<br>
                            Start Time: 12:00<br>
                            Stop Time: 17:00</p>
                          <div align="center" class="TableCell"> Drag the Start 
                            and Stop sliders to set a <br>
                            new Valid Auto Control Time Range: </div>
                          <div align="center">
                           
                           
                            <table width="218" border="0" cellspacing="0" height="267">
                              <tr> 
                                <td background="../../WebConfig/yukon/Parts/GraphBG.gif" height="281"> 
                                  <table width="100%" border="0" height="223">
                                    <tr> 
                                      <td valign = "top"> 
                                          <div align = "center"><font face="Arial, Helvetica, sans-serif" size="1">Start</font> 
                                            <input id = "startTime1" type="text" name="textfield" size="5" value = "00:00" onchange="moveStartStopPtr('start')">
                                        </div>
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td height=70"><img name= "startPtr" onmousedown = "beginDrag(event,0,0,parseInt(document.rooftop.stopPtr.style.left),27,'showStartTime1()','horizontal','')" src="../../WebConfig/yukon/Parts/SliderShortStart.gif" onload= "setPixTime()" style = "position:relative; top:0px; left:0px; cursor:pointer"></td>
                                    </tr>
                                    <tr> 
                                      <td height="70" ><img name= "stopPtr" onmousedown = "beginDrag(event,0,0,170,parseInt(document.rooftop.startPtr.style.left),'showStopTime1()','horizontal','')" src="../../WebConfig/yukon/Parts/SliderShortStop.gif" onload= "setPixTime()" style = "position:relative; top:0px; left:0px; cursor:pointer"></td>
                                    </tr>
                                    <tr> 
                                      <td valign = "bottom"> 
                                          <div align = "center"> <font face="Arial, Helvetica, sans-serif" size="1">Stop 
                                            </font> 
                                            <input id = "stopTime1" type="text" name="textfield" size="5" onchange = "moveStartStopPtr('stop');" value = "23:50">
                                        </div>
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
							
                           
                          </div>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table></form>
               <form>
                <input type="submit" name="Submit" value="Update">
				</form>
                <br>
              
              </div>
            <p align="center" class="TableCell">Current Programs<br>
              Click on a Program Name to view the Program details.</p>
            <table width="200" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td> 
                  <div align="center"> 
                    <form name="form2" method="post" action="">
                      <input type="submit" name="Submit2" value="Enable All">
                    </form>
                  </div>
                </td>
                <td> 
                  <div align="center"> 
                    <form name="form3" method="post" action="">
                      <input type="submit" name="Submit3" value="Disable All">
                    </form>
                  </div>
                </td>
              </tr>
            </table>
            <br>
            <table width="600" border="1" cellspacing="0" bgcolor="white" cellpadding="2" align="center">
              <tr valign="top"> 
                <td width="104" class="HeaderCell">Program Name</td>
                <td width="100" class="HeaderCell">Status</td>
                <td width="95" class="HeaderCell">Control Rate</td>
                <td width="101" class="HeaderCell">Last Start Time</td>
                <td width="104" class="HeaderCell">Last Stop Time</td>
                <td width="58" class="HeaderCell">Approx. Reduction</td>
              </tr>
              <tr> 
                <td height="23" class="TableCell" width="104"><a href="user_lm_programs.jsp">Rooftop Air </a></td>
                <td height="23" class="TableCell" width="100"><font color="#FF0000">Inactive</font></td>
                <td height="23" class="TableCell" width="95">25% Off Cycle</td>
                <td height="23" class="TableCell" width="101">06/23/01 12:00</td>
                <td height="23" class="TableCell" width="104">06/23/01 15:45</td>
                <td height="23" class="TableCell" width="58">400.0</td>
              </tr>
              <tr> 
                <td height="23" class="TableCell" width="104">Large Motors</td>
                <td height="23" class="TableCell" width="100">Disabled</td>
                <td height="23" class="TableCell" width="95">&nbsp;</td>
                <td height="23" class="TableCell" width="101">06/23/01 14:00</td>
                <td height="23" class="TableCell" width="104">06/23/01 16:00</td>
                <td height="23" class="TableCell" width="58">425.0</td>
              </tr>
              <tr> 
                <td height="23" class="TableCell" width="104">Irrigation Site 1</td>
                <td height="23" class="TableCell" width="100"><font color="#00CC00">Running</font></td>
                <td height="23" class="TableCell" width="95">4 Hours</td>
                <td height="23" class="TableCell" width="101">07/23/01 12:03</td>
                <td height="23" class="TableCell" width="104">07/23/01 16:03</td>
                <td height="23" class="TableCell" width="58">50.0</td>
              </tr>
              <tr> 
                <td height="23" class="TableCell" width="104">Irrigation Site 
                  2</td>
                <td height="23" class="TableCell" width="100"><font color="#0000FF">Stopped</font></td>
                <td height="23" class="TableCell" width="95">&nbsp;</td>
                <td height="23" class="TableCell" width="101">07/20/01 13:15</td>
                <td height="23" class="TableCell" width="104">07/20/01 15:15</td>
                <td height="23" class="TableCell" width="58">50.0</td>
              </tr>
            </table>
            <p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>
