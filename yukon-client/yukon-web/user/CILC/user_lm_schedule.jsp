<%@ include file="../include/user_header.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<!-- Java script needed for the Calender Function--->
<script language="JavaScript" src="../../JavaScript/drag.js">
</script>
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../../JavaScript/Calendar1-82.js"></SCRIPT>
<SCRIPT Language="Javascript">
<!--

var browser = new Object();

browser.isNetscape = false;
browser.isMicrosoft = false;
if (navigator.appName.indexOf("Netscape") != -1)
	browser.isNetscape = true;
else if (navigator.appName.indexOf("Microsoft") != -1)
	browser.isMicrosoft = true;



function fsub() {
	document.Rooftop.submit();
}

function TryCallFunction() {
	var sd = document.Rooftop.mydate1.value.split("-");
	document.Rooftop.iday.value = sd[1];
	document.Rooftop.imonth.value = sd[0];
	document.Rooftop.iyear.value = sd[2];
}

function Today() {
	var dd = new Date();
	return((dd.getMonth()+1) + "/" + dd.getDate() + "/" + dd.getFullYear());
}

function MM_popupMsg(msg) { //v1.0
  alert(msg);
}
//-->
</SCRIPT>
<script language="JavaScript">
<!--
<!--
<!--

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

var lgMove = 6 * hour + 9;
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






function showStartTime1(){
 var s = document.images['startPtr'];
 var curPos = parseInt(s.style.left);
 var hourStr = "0" + Math.floor((curPos - 81 + 72) * 10 / 60);
 hourStr = hourStr.substr(hourStr.length-2, 2);
 var minuteStr = "0" + (curPos - 81 + 72) * 10 % 60;
 minuteStr = minuteStr.substr(minuteStr.length-2, 2);
 document.rooftop.startTime1.value = hourStr + ":" + minuteStr;
}

function showStopTime1(){
 
  var sp = document.images['stopPtr'];
  var curPos = parseInt(sp.style.left);
  var hourStr = "0" + Math.floor((curPos - 81 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos-81 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
  document.rooftop.stopTime1.value = hourStr + ":" + minuteStr;
  
}
function setPixTime() {
(navigator.appName == 'Netscape')?document.images['startPtr'].style.top = -9 +"px":document.images['startPtr'].style.top = -9 + "px";
 document.rooftop.startTime1.value = "00:00";
(navigator.appName == 'Netscape')?document.images['stopPtr'].style.top = -8 +"px":document.images['stopPtr'].style.top = -9 + "px";
 document.rooftop.stopTime1.value = "23:50";
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
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="150"> 
		  <% String pageName = "user_lm_time.jsp"; %>
          <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          
		    <td width="600" valign="top" bgcolor="#FFFFFF"> 
              <div align="center"><br>
              <span class="TitleHeader">TIME BASED CONTROL - ROOFTOP AIR</span><br>
                <div align="center"><br>
                <table width="600" border="0" cellspacing="5" cellpadding="0">
                  <tr>
					  <form method="submit" action="">
                      <td width="215" valign="top"> 
                        
                      <div align="center"> 
                        <div class="TableCell"> <br>
                          To disable the schedules, select Disable:</div>
                      </div>
                        
						<table width="200" border="0" cellspacing="0" cellpadding="5" align="center">
                          <tr> 
                            <td width="110" class="TableCell"> 
                              <div align="right">
                                <b>Disable</b>
                              </div>
                            </td>
                            <td width="70"> 
                              <div align="left"> 
                                <input type="checkbox" name="checkbox" value="checkbox">
                              </div>
                            </td>
                          </tr>
                        </table>
                        
                      <div align="center" class="TableCell"><br>
                        Run schedules on the following days:</div>
						
                        <table width="200" border="0" cellspacing="0" cellpadding="5">
                          <tr> 
                            <td width="119" class="TableCell"> 
                              <div align="right">Monday </div>
                            </td>
                            <td width="66"> <b> 
                              <input type="checkbox" name="checkbox8" value="checkbox">
                              </b></td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              <div align="right">Tuesday </div>
                            </td>
                            <td width="66"> <b> 
                              <input type="checkbox" name="checkbox9" value="checkbox">
                              </b></td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              <div align="right">Wednesday</div>
                            </td>
                            <td width="66"> <b> 
                              <input type="checkbox" name="checkbox10" value="checkbox">
                              </b></td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              <div align="right">Thursday</div>
                            </td>
                            <td width="66"> <b> 
                              <input type="checkbox" name="checkbox11" value="checkbox">
                              </b></td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              <div align="right">Friday </div>
                            </td>
                            <td width="66"> <b> 
                              <input type="checkbox" name="checkbox12" value="checkbox">
                              </b></td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              <div align="right">Saturday </div>
                            </td>
                            <td width="66"> <b> 
                              <input type="checkbox" name="checkbox13" value="checkbox">
                              </b></td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              <div align="right">Sunday </div>
                            </td>
                            <td width="66"> <b> 
                              <input type="checkbox" name="checkbox14" value="checkbox">
                              </b></td>
                          </tr>
                          <tr> 
                            <td width="119" class="TableCell"> 
                              <div align="right">Control Holidays</div>
                            </td>
                            <td width="66"> <b> 
                              <input type="checkbox" name="checkbox15" value="checkbox">
                              </b></td>
                          </tr>
                        </table>
						
                    </td>
					</form>
					  <form name="rooftop">
                      <td width="369" valign="top"> 
                        <div class="TableCell"> 
                          <div align="center">Select either Start Now or Start 
                            at and select a Data and Time:</div>
                        </div>
						<table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
						
                          <tr> 
                            <td> 
                              <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
                                <tr valign="top"> 
                                  <td width="85" class="TableCell"> 
                                    <div align="right"><b>Start Now: </b></div>
                                  </td>
                                  <td width="25"> 
                                    <input type="radio" name="radiobutton" value="radiobutton">
                                    <br>
                                  </td>
                                  <td width="36">&nbsp;</td>
                                  <td width="179">&nbsp;</td>
                                </tr>
                                <tr> 
                                  <td width="85" class="TableCell"> 
                                    <div align="right"><b>Start at:</b></div>
                                  </td>
                                  <td width="25"> 
                                    <input type="radio" name="radiobutton" value="radiobutton" checked>
                                  </td>
                                  <td width="36" class="TableCell"> 
                                    <div align="right">Date: </div>
                                  </td>
                                  <td width="179"> 
                                    <input type="text" name="date" value="<%= datePart.format(graphBean.getStartDate()) %>" size="8">
                                    <a href="javascript:show_calendar('rooftop.date')"
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;"> <img src="../../Images/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a></td>
                                </tr>
                                <tr> 
                                  <td width="85" height="85">&nbsp; </td>
                                  <td width="25" height="85">&nbsp;</td>
                                  <td width="36" height="85" class="TableCell"> 
                                    <div align="right">Time: </div>
                                  </td>
                                  <td width="179" background="../../Images/Parts/StartStopBG.gif" height="85" valign="top"> 
                                    <table width="100" border="0" cellspacing="0" height="40" align="center">
                                      <tr> 
                                        <td valign = "top" align = "center"> 
                                          <div>
                                            <input type="text" name="startTime1" value="00:00" size="5" onchange = "moveStartStopPtr('start')">
                                          </div>
                                        </td>
                                      </tr>
                                    </table>
                                   <img name = "startPtr" onload = "setPixTime()" onmousedown = "beginDrag(event,0,0,parseInt(document.rooftop.stopPtr.style.left),9,'showStartTime1()','horizontal','')" src="../../Images/Parts/SliderShort.gif" width="17" height="19" style = "position:relative; top:0px; left:9px; cursor:pointer;" ><br>
                                   
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
						<div class="TableCell"> 
                          <div align="center">Select either Manual Stop or Stop 
                            at and select a Data and Time:</div>
                        </div>
                        <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" class="TableCell" bgcolor="#FFFFFF">
                          <tr> 
                            <td height="145"> 
                              <table width="350" border="0" cellspacing="0" cellpadding="3" align="center">
                                <tr valign="top"> 
                                  <td width="81"> 
                                    <div align="right" class="TableCell"><b>Manual Stop: </b></div>
                                  </td>
                                  <td width="17">  
                                    <input type="radio" name="radiobutton2" value="radiobutton">
                                    <br>
                                  </td>
                                  <td width="34">&nbsp;</td>
                                  <td width="170">&nbsp;</td>
                                </tr>
                                <tr> 
                                  <td width="81"> 
                                    <div align="right" class="TableCell"><b>Stop at:</b></div>
                                  </td>
                                  <td width="17">  
                                    <input type="radio" name="radiobutton2" value="radiobutton" checked>
                                  </td>
                                  <td width="34"> 
                                    <div align="right" class="TableCell">Date: </div>
                                  </td>
                                  <td width="170"> 
                                    <input type="text" name="date2" value="<%= datePart.format(graphBean.getStartDate()) %>" size="8">
                                    <a href="javascript:show_calendar('rooftop.date2')"
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;"> <img src="../../Images/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a></td>
                                </tr>
                                <tr> 
                                  <td width="81" height="85">&nbsp; </td>
                                  <td width="17" height="85">&nbsp;</td>
                                  <td width="34" height="85"> 
                                    <div align="right" class="TableCell">Time: </div>
                                  </td>
                                  <td width="170" background="../../Images/Parts/StartStopBG.gif" height="85" valign="top"> 
                                   
                                      <table width="100" border="0" cellspacing="0" height="40" align = "center">
                                        <tr> 
                                          <td align = "center" valign = "top"> 
                                       
                                              <input type="text" name="stopTime1" value="23:50" size="5" onchange = "moveStartStopPtr('stop')">
                                           
                                          </td>
                                        </tr>
                                      </table>
                                      <img name = "stopPtr" src="../../Images/Parts/SliderShort.gif" width="17" height="19" onmousedown = "beginDrag(event,0,0,152,parseInt(document.rooftop.startPtr.style.left),'showStopTime1()','horizontal','')" onload = "setPixTime()" style = "position:relative; top:0px; left:152px; cursor:pointer;"><br>
                                    
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
						
                      </td>
					  </form>
                    </tr>
                  </table>
				  
                  <table width="100" border="0" cellspacing="0">
                    <tr> 
					<form name="form1" method="post" action="">
                      <td> 
                        <div align="center">
                          <input type="submit" name="Update" value="Update">
                        
                      </div>
                      </td>
					  </form>
                    </tr>
                  </table>
                  <hr>
                  <p class="MainText">Click on a Load Group Name to view the Load Group details.</p>
                </div>
                <table width="600" border="1" cellspacing="0" bgcolor="white" cellpadding="2" align="center">
                  <tr valign="top"> 
                    <td width="112" class="HeaderCell">Load Group Name</td>
                    <td width="85" class="HeaderCell">Status</td>
                    <td width="197" class="HeaderCell">Last Control</td>
                    <td width="96" class="HeaderCell">Monthly Control</td>
                    <td width="78" class="HeaderCell">Approx. Reduction</td>
                  </tr>
                  <tr> 
                    <td height="23" class="TableCell" width="112"><a href="user_lm_time_groups.jsp">Building 1</a></td>
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
                
              <p align="center" class="SubtitleHeader"><a href="user_lm_time.jsp" class="Link1">Back to schedule list</a><br>
                <br>
              </p>
               </div>
              </td>
		  
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
</body>
</html>
