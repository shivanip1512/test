<%@ include file="../include/user_header.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<!-- Java script needed for the Calender Function--->
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
else if (s == 'start2')  
 time = document.timeForm.startTime2.value.split(":");
else if (s == 'stop2')
 time = document.timeForm.stopTime2.value.split(":");
 
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

var lgMove = 6 * hour + 10;
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
document.timeForm.stopPtr.style.left = lgMove + smMove;}
else if (s == 'start2') {
document.timeForm.startTime2.value = hour + ":" + minute;
document.timeForm.startPtr2.style.left = lgMove + smMove;}
else if (s == 'stop2') {
document.timeForm.stopTime2.value = hour + ":" + minute;
document.timeForm.stopPtr2.style.left = lgMove + smMove;
}
}




function showStartTime1(){
 
  var s = document.images['startPtr'];
  var curPos = parseInt(s.style.left);
  var hourStr = "0" + Math.floor((curPos - 82 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos - 82 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
  document.timeForm.startTime1.value = hourStr + ":" + minuteStr;
 
}


function showStartTime2(){
 
  var s = document.images['startPtr2'];
  var curPos = parseInt(s.style.left);
  var hourStr = "0" + Math.floor((curPos - 82 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos - 82 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
  document.timeForm.startTime2.value = hourStr + ":" + minuteStr;
 
}

function showStopTime1(){
 
  var sp = document.images['stopPtr'];
  var curPos = parseInt(sp.style.left);
  var hourStr = "0" + Math.floor((curPos - 82 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos-82 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
   document.timeForm.stopTime1.value = hourStr + ":" + minuteStr;
  
}
function showStopTime2(){
 
  var sp = document.images['stopPtr2'];
  var curPos = parseInt(sp.style.left);
  var hourStr = "0" + Math.floor((curPos - 82 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos-82 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
   document.timeForm.stopTime2.value = hourStr + ":" + minuteStr;
  
}
function setPixTime() {

(navigator.appName == 'Netscape')?document.images['startPtr'].style.top = -13 +"px":document.images['startPtr'].style.top = -12 + "px";
(navigator.appName == 'Netscape')?document.images['startPtr2'].style.top = -13 +"px":document.images['startPtr2'].style.top = -13 + "px";

(navigator.appName == 'Netscape')?document.images['stopPtr'].style.top = -9 +"px":document.images['stopPtr'].style.top = -8 + "px";
(navigator.appName == 'Netscape')?document.images['stopPtr2'].style.top = -9 +"px":document.images['stopPtr2'].style.top = -9 + "px";
 
 document.timeForm.startTime1.value = "00:00";
 document.timeForm.startTime2.value = "00:00";
 document.timeForm.stopTime1.value = "23:50";
 document.timeForm.stopTime2.value = "23:50";
}





function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

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
          <td valign="top" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr>
			  <td width="265" height="28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;User Control</td>  
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
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
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
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
              <p align="center" class="TitleHeader"><br>AUTO CONTROL - ROOFTOP AIR</p>
                <div align="center"> 
                <table width="600" border="1" cellspacing="0" cellpadding="5" bgcolor="#FFFFFF">
                  <tr> 
                      
                    <td width="200" valign="top" height="299"> 
                      <p align="center" class="SubtitleHeader">Valid on the following days:</p>
                      <form method="post" action="">
                      <table width="200" border="0" cellspacing="0" cellpadding="5">
                          <tr> 
                            <td width="119" class="TableCell"> 
                            <div align="right">Monday </div>
                            </td>
                            <td width="66">  
                              <input type="checkbox" name="checkbox8" value="checkbox">
                          </td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell">    
                            <div align="right">
							Tuesday
							</div>
                            </td>
                            <td width="66">  
                              <input type="checkbox" name="checkbox9" value="checkbox">
                          </td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              
                            <div align="right">
							Wednesday
							</div>
                            </td>
                            <td width="66">  
                              <input type="checkbox" name="checkbox10" value="checkbox">
                          </td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              
                            <div align="right">
							Thursday
							</div>
                            </td>
                            <td width="66">  
                              <input type="checkbox" name="checkbox11" value="checkbox">
                          </td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              
                            <div align="right">
							Friday 
							</div>
                            </td>
                            <td width="66">  
                              <input type="checkbox" name="checkbox12" value="checkbox">
                          </td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              
                            <div align="right">
							Saturday
							</div>
                            </td>
                            <td width="66">  
                              <input type="checkbox" name="checkbox13" value="checkbox">
                          </td>
                          </tr>
                          <tr> 
                            <td width="119"> 
                              
                            <div align="right" class="TableCell">
							Sunday 
							</div>
                            </td>
                            <td width="66">  
                              <input type="checkbox" name="checkbox14" value="checkbox">
                          </td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              
                            <div align="right">
							Holidays 
							</div>
                            </td>
                            <td width="66">  
                              <input type="checkbox" name="checkbox15" value="checkbox">
                          </td>
                          </tr>
                        </table>
						</form>
                      </td>
                      
                    <td width="399" valign="top" height="299"> 
                      <div align="center"><span class="SubtitleHeader">Valid for the following daily times:</span></div>
					   <form method="post" action="">	
					  <table width="400" border="0" cellspacing="0" cellpadding="0">
                          <tr valign="top"> 
                            <td width="189" align="right"> 
                              <input type="radio" name="radiobutton" value="radiobutton">
                            </td>
                            
                          <td width="211" class="TableCell">Always Valid</td>
                          </tr>
                          <tr> 
                            
                          <td width="189" align="right"> 
                            <input type="radio" name="radiobutton" value="radiobutton">
                          </td>
                            
                          <td width="211" class="TableCell">Time Windows</td>
                          </tr>
                        </table>
						</form>
						<form name="timeForm">
                        <table width="375" border="0" cellspacing="0" cellpadding="3" height="165">
                          <tr> 
                            <td width="102" height="81"> 
                              <div align="right">
                              <p class="TableCell">Time Window 1:</p>
                            </div>
                            </td>
							
                            <td width="261" class = "TableCell" background="../../WebConfig/yukon/Parts/StartStopBG2.gif" height="70" valign="middle">
                              <table width="99%" border="0"height="63">
                                <tr> 
                                  <td width="14%"><font face="Arial, Helvetica, sans-serif" size="1">Start</font></td>
                                  <td width="12%"> 
                                    <input type="text" name="startTime1" size = "4" value = "00:00" onchange = "moveStartStopPtr('start')">
                                  </td>
                                  <td width="74%"><img name = "startPtr" src="../../WebConfig/yukon/Parts/SliderShort.gif" onMouseDown = "beginDrag(event,0,0,parseInt(document.timeForm.stopPtr.style.left),10,'showStartTime1()','horizontal','');" onLoad = "setPixTime()" style = "position:relative; top:-20px; left:10px; cursor:pointer;"></td>
                                </tr>
                                <tr> 
                                  <td width="14%"><font face="Arial, Helvetica, sans-serif" size="1">Stop</font></td>
                                  <td width="12%"> 
                                    <input type="text" name="stopTime1" size = "4" value = "23:50" onchange = "moveStartStopPtr('stop')">
                                  </td>
                                  <td width="74%"><img name = "stopPtr" onMouseDown = "beginDrag(event,0,0,153,parseInt(document.timeForm.startPtr.style.left),'showStopTime1()','horizontal','');" onLoad = "setPixTime()" src="../../WebConfig/yukon/Parts/SliderShort.gif" style = "position:relative; top:-13px; left:153px; cursor:pointer;"></td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                          <tr> 
                            <td width="102" height="83"> 
                              <div align="right">
                              <p class="TableCell">Time Window 2:</p>
                            </div>
                            </td>
                            <td width="261" background="../../WebConfig/yukon/Parts/StartStopBG2.gif" height="83" valign="middle">
                              <table width="99%" border="0" height="63">
                                <tr> 
                                  <td width="14%"><font face="Arial, Helvetica, sans-serif" size="1">Start</font></td>
                                  <td width="12%"> 
                                    <input type="text" name="startTime2" size = "4" value = "00:00" onchange = "moveStartStopPtr('start2')">
                                  </td>
                                  <td width="74%"><img name = "startPtr2" onMouseDown = "beginDrag(event,0,0,parseInt(document.timeForm.stopPtr2.style.left),10,'showStartTime2()','horizontal','');"  onLoad = "setPixTime()" src="../../WebConfig/yukon/Parts/SliderShort.gif" style="position:relative; top:-13px; left:10px; cursor:pointer;" ></td>
                                </tr>
                                <tr> 
                                  <td width="14%"><font face="Arial, Helvetica, sans-serif" size="1">Stop</font></td>
                                  <td width="12%"> 
                                    <input type="text" name="stopTime2" size = "4" value = "23:50" onchange = "moveStartStopPtr('stop2')">
                                  </td>
                                  <td width="74%"><img name = "stopPtr2" src="../../WebConfig/yukon/Parts/SliderShort.gif" onMouseDown = "beginDrag(event,0,0,153,parseInt(document.timeForm.startPtr2.style.left),'showStopTime2()','horizontal','');" onLoad = "setPixTime()" style = "position:relative; top:-9px; left:152px; cursor:pointer;"></td>
                                </tr>
                              </table>
                            </td>
							
                          </tr>
                        </table>
						</form>
                        <table width="350" border="0" cellspacing="5" cellpadding="0" align="center">
                          <tr> 
                            <td width="224"> 
                              
                            <div align="right"> 
                              <span class="TableCell"><b>Approximate controllable 
                                kW capacity:</b></span>
                            </div>
                            </td>
                            <td width="61"> 
                              <input type="text" name="textfield22222" size="10">
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                <br>
              </div>
                
              <table width="100" border="0" cellspacing="0">
                <tr> 
                  <form name="form1" method="post" action="">
                    <td> 
                      <div align="center"> 
                        <input type="submit" name="Update2" value="Update">
                      </div>
                    </td>
                  </form>
                </tr>
              </table>
              <p align="center" class="MainText">Click on a Load Group Name to view the Load Group details.</p>
              <table width="600" border="1" cellspacing="0" bgcolor="white" cellpadding="2" align="center">
                  <tr valign="top"> 
                    <td width="112" class="HeaderCell">Load Group Name</td>
                    <td width="85" class="HeaderCell">Status</td>
                    <td width="197" class="HeaderCell">Last Control</td>
                    <td width="96" class="HeaderCell">Monthly Control</td>
                    <td width="78" class="HeaderCell">Approx. Reduction</td>
                  </tr>
                  <tr> 
                    <td height="23" class="TableCell" width="112"><a href="user_lm_groups.jsp">Building 1</a></td>
                    <td height="23" class="TableCell" width="85">Active</td>
                    <td height="23" class="TableCell" width="197">07/14/01 14:13 - current</td>
                    <td height="23" class="TableCell" width="96">8 Hours</td>
                    <td height="23" class="TableCell" width="78">100.0</td>
                  </tr>
                  <tr> 
                    <td height="23" class="TableCell" width="112">Building 2</td>
                    <td height="23" class="TableCell" width="85">Disabled</td>
                    <td height="23" class="TableCell" width="197">07/10/01 14:13 - 07/10/01 16:13 </td>
                    <td height="23" class="TableCell" width="96">0 Hours</td>
                    <td height="23" class="TableCell" width="78">0.0</td>
                  </tr>
                  <tr> 
                    <td height="23" class="TableCell" width="112">Building 3</td>
                    <td height="23" class="TableCell" width="85">Active</td>
                    <td height="23" class="TableCell" width="197">07/14/01 16:30 - current</td>
                    <td height="23" class="TableCell" width="96">2 Hours</td>
                    <td height="23" class="TableCell" width="78">250.0</td>
                  </tr>
                  <tr> 
                    <td height="23" class="TableCell" width="112">Building 4</td>
                    <td height="23" class="TableCell" width="85">Not Active</td>
                    <td height="23" class="TableCell" width="197">07/01/01 14:00 - 07/01/01 16:00 </td>
                    <td height="23" class="TableCell" width="96">2 Hours</td>
                    <td height="23" class="TableCell" width="78">200.0</td>
                  </tr>
                </table>
                
              <p align="center" class="SubtitleHeader"><a href="user_lm_control.jsp" class="Link1">Back to Control Area</a></p>
            </div><br>
              </td>
		  
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
</body>
</html>
