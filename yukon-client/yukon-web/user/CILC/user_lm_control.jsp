<%@ include file="../include/user_header.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
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
 time = document.timeForm.startTime1.value.split(":");
else if (s == 'stop')
 time = document.timeForm.stopTime1.value.split(":");
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
document.timeForm.startTime1.value = hour + ":" + minute;
document.timeForm.startPtr.style.left = lgMove + smMove;}
else if (s == 'stop') {
document.timeForm.stopTime1.value = hour + ":" + minute;
document.timeForm.stopPtr.style.left = lgMove + smMove;
}
}

function incLoad() {
	var curLoad = Math.floor(parseInt(document.form1.loadKW.value) / 50) * 50;
	curLoad = Math.min(curLoad + 50, 1500);
	document.form1.loadKW.value = curLoad;
}

function decLoad() {
	var curLoad = Math.ceil(parseInt(document.form1.loadKW.value) / 50) * 50;
	curLoad = Math.max(curLoad - 50, 50)
	document.form1.loadKW.value = curLoad;
}

function showStartTime1(){
 
  var s = document.images['startPtr'];
  var curPos = parseInt(s.style.left);
  var hourStr = "0" + Math.floor((curPos - 99 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos - 99 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
  document.timeForm.startTime1.value = hourStr + ":" + minuteStr;
 
}

function showStopTime1(){
 
  var sp = document.images['stopPtr'];
  var curPos = parseInt(sp.style.left);
  var hourStr = "0" + Math.floor((curPos - 99 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos-99 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
   document.timeForm.stopTime1.value = hourStr + ":" + minuteStr;
  
}


function setPixTime() {

(navigator.appName == 'Netscape')?document.images['startPtr'].style.top = -18 +"px":document.images['startPtr'].style.top = -31 + "px";
(navigator.appName == 'Netscape')?document.images['startPtr'].style.left = 27 +"px":document.images['startPtr'].style.left = 27 + "px";
 document.timeForm.startTime1.value = "00:00";
(navigator.appName == 'Netscape')?document.images['stopPtr'].style.top = 66 +"px":document.images['stopPtr'].style.top = 33 + "px";
(navigator.appName == 'Netscape')?document.images['stopPtr'].style.left = 170 +"px":document.images['stopPtr'].style.left = 170 + "px";
document.timeForm.stopTime1.value = "23:50";
}



function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
// -->

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}


//-->
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
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="150" height="102" background="../../WebConfig/yukon/MomWide.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;User Control</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
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
          <td  valign="top" width="150"> 
            <% String pageName = "user_lm_control.jsp"; %>
            <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="600" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
            <p><br>
              <span class="TitleHeader">AUTO CONTROL</span><br>
              <span class="MainText">Please select one of the following Trigger Sets:</span></p>
            <table width="300" border="0" cellpadding="1">
              <tr> 
                <form method="post" action="user_lm_control.jsp">
                  <td> 
                    <div align="center"> 
                      <input type="submit" name="" value="Trigger Set 1">
                    </div>
                  </td>
                </form>
                <form method="post" action="user_lm_control2.jsp">
                  <td> 
                    <div align="center"> 
                      <input type="submit" name="Control Area 2"" value="Trigger Set 2">
                    </div>
                  </td>
                </form>
              </tr>
            </table>
            <p><span class="TableCell">Trigger Set 1 - Currently not controlling<br>
              Your Last Control:<br>07/10/02 12:50 - 07/10/02 16:50</span><br>
            <br>
            </p>
            <table width="600" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td> 
                  <table width="600" border="2" cellspacing="0" cellpadding="5" bgcolor="#FFFFFF">
                    <tr> 
                      <td width="300" valign="top">
                        <div class="TableCell" align="center">Select or enter a new Current Load below:</div>
                        <form name="form1" method="post" action="">
                        <table width="299" border="0" cellspacing="1" cellpadding="5" class="TableCell" align="center">
                          <tr>
                            <td background="../../Images/Parts/MeterBG.jpg" height="269">
                              <table width="150" border="0" cellspacing="0" height="117" align="center">
                                <tr>
                                  <td valign="top" height="65">
                                    <table width="82%" border="0" height="45">
                                      <tr>
                                        <td>
                                          <table width="20" border="0" cellspacing="0">
                                            <tr>
                                              <td><img src="../../Images/Parts/UpArrow.gif" width="19" height="17" onclick="incLoad()"></td>
                                            </tr>
                                            <tr>
                                              <td><img src="../../Images/Parts/DownArrow.gif" width="19" height="17" onclick="decLoad()"></td>
                                            </tr>
                                          </table>
                                        </td>
                                        <td>
                                          <input type="text" name="loadKW" size="8" value="1000">
                                          <font face="Arial, Helvetica, sans-serif" size="1">kW</font>
                                        </td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                                <tr>&nbsp;<br></tr>
                                <tr> 
                                  <td valign="bottom"> 
                                    <table width="89%" cellspacing="0" cellpadding="0" border="2" bordercolor="#999999" bgcolor="#CCCCCC" height="56">
                                      <tr> 
                                        <td valign = "top"> 
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
                            </td>
                          </tr>
                        </table>
                        </form>
                        <div align="center"><br>
                        <form name="form2" method="post" action="">
                          <input type="submit" name="Update" value="Update">
                        </form>
                        </div>
                      </td>
                      <td width="280" valign="top">
                        <div align="center">
                        <p class="TableCell"><b>Your Valid Auto Control Time Range:<br></b>Start Time: 12:00<br>Stop Time: 17:00</p>
                        <div class="TableCell">Drag the Start and Stop sliders to set a <br>new Valid Auto Control Time Range:</div>
                        <form name = "timeForm">
                        <table width="218" border="0" cellspacing="0" height="267">
                          <tr>
                            <td background="../../Images/Parts/GraphBG.gif" height="281">
                              <table width="100%" border="0" height="223">
                                <tr>
                                  <td valign = "top">
                                    <div align = "center"><font face="Arial, Helvetica, sans-serif" size="1">Start</font>
                                    <input id = "startTime1" type="text" name="textfield" size="5" value = "00:00" onchange="moveStartStopPtr('start')">
                                    </div>
                                  </td>
                                </tr>
                                <tr>
                                  <td height=70"><img name= "startPtr" onmousedown = "beginDrag(event,0,0,parseInt(document.timeForm.stopPtr.style.left),27,'showStartTime1()','horizontal','')" src="../../Images/Parts/SliderShortStart.gif" onload= "setPixTime()" style = "position:relative; top:0px; left:0px; cursor:pointer"></td>
                                </tr>
                                <tr>
                                  <td height="70" ><img name= "stopPtr" onmousedown = "beginDrag(event,0,0,170,parseInt(document.timeForm.startPtr.style.left),'showStopTime1()','horizontal','')" src="../../Images/Parts/SliderShortStop.gif" onload= "setPixTime()" style = "position:relative; top:0px; left:0px; cursor:pointer"></td>
                                </tr>
                                <tr>
                                  <td valign = "bottom"> 
                                    <div align = "center"> <font face="Arial, Helvetica, sans-serif" size="1">Stop</font> 
                                    <input id = "stopTime1" type="text" name="textfield" size="5" onchange = "moveStartStopPtr('stop');" value = "23:50">
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        </form>
                        </div>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
            <br>
            </div>
            <p align="center"> 
              <cfform name="Form1" action="submit.cfm" method="Post"></cfform>
            </p>
            <p align="center" class="TableCell">Current Programs<br>
              Click on a Program Name to view the Program details.</p>
            <table width="200" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td> 
                  <div align="center"> 
                    <form name="form3" method="post" action="">
                    <input type="submit" name="Enable All" value="Enable All">
                    </form>
                  </div>
                </td>
                <td> 
                  <div align="center"> 
                    <form name="form4" method="post" action="">
                      <input type="submit" name="Disable All" value="Disable All">
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
                <td height="23" class="TableCell" width="104"><a href="user_lm_programs.jsp">Rooftop Air</a></td>
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
                <td height="23" class="TableCell" width="104">Irrigation Site 2</td>
                <td height="23" class="TableCell" width="100"><font color="#0000FF">Stopped</font></td>
                <td height="23" class="TableCell" width="95">&nbsp;</td>
                <td height="23" class="TableCell" width="101">07/20/01 13:15</td>
                <td height="23" class="TableCell" width="104">07/20/01 15:15</td>
                <td height="23" class="TableCell" width="58">50.0</td>
              </tr>
            </table>
            <p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>
