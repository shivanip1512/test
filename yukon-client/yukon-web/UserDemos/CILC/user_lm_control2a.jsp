<%@ include file="user_header.jsp" %>
<%@ include file="user_trendingheader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript" src ="drag.js">
</script>
<script language="JavaScript">
<!--

function showTempNS()
{
  var a = document.getElementById('arrow');
  var curPos = parseInt(a.style.top);
  var temp = Math.floor((160 - curPos) / 280 * 70 + 40);
  document.getElementById('Temperature').value = temp;
}

function showTempIE()
{
var a = document.getElementById('arrow');
  var curPos = parseInt(a.style.top);
  var temp = Math.floor((80 - curPos) / 200 * 70 + 40);
  document.getElementById('Temperature').value = temp;


}

function showStartTime1(){
  //var startPtr = (navigator.appName=='Netscape')?document.startTimeLayer1:startTimeLayer1;
  var s = document.getElementById("startPtr");
  var curPos = parseInt(s.style.left);
  
  var hourStr = "0" + Math.floor((curPos - 99 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos - 99 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
  document.getElementById("startTime").value = hourStr + ":" + minuteStr;
 // document.rooftop.startTime1.value = hourStr + ":" + minuteStr;
}

function showStopTime1(){
  //var s = (navigator.appName=='Netscape')?document.stopTimeLayer1:stopTimeLayer1;
  var sp = document.getElementById("stopPtr");
  var curPos = parseInt(sp.style.left);
  var hourStr = "0" + Math.floor((curPos - 99 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos-99 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
   document.getElementById("stopTime").value = hourStr + ":" + minuteStr;
  //document.rooftop.stopTime1.value = hourStr + ":" + minuteStr;
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
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="Calendar1-82.js"></SCRIPT>
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
<script language="JavaScript">
<!--
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->
</script>
</head>
<body class="Background" leftmargin="0" topmargin="0" onLoad="MM_preloadImages('OnWhite.jpg','OffWhite.jpg','TodayWhite.jpg')">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="Mom.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;User 
                  Control</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
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
            <% String pageName = "user_lm_control.jsp"; %>
            <%@ include file="nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="650" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <p><br>
                <b class="Main">AUTO CONTROL</b></p>
              <p align="center" class="Main">Please select one of the following 
                Control Areas:</p>
              <table width="100%" border="0" cellpadding="1">
                <tr> 
                  <form method="post" action="user_lm_control.jsp">
                    <td> 
                      <div align="center"> 
                        <input type="submit" name="tab" value="Control Area 1">
                      </div>
                    </td>
                  </form>
                </tr>
              </table>
              <p><span class="TableCell">CONTROL AREA 2 - Currently not controlling 
                <br>
                Your Last Control:<br>
                07/10/01 12:50 - 07/10/01 16:50</span><br>
                <br>
              </p>
              <table width="513" border="2" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF" height="116" >
                <tr> 
                  <td> 
                    <table width="542" border="1" cellspacing="5" cellpadding="0" align="center" height="120" >
                      <tr>
                        <td width="251" valign="middle">
                          <div align="center"> 
                            <div class="TableCell">Current Temperature: 70 &deg&nbsp;</div>
                          </div>
                          </td>
                        <td width="284" valign="middle">
                          <p align="center" class="TableCell">Your Valid Auto 
                            Control Time Range:<br>
                          </p>
                      </tr>
                      <tr> 
					  <form>
                        <td width="251" valign="middle"> 
                          <div height = "50" align="center" class = "TableCell">
                         <div><b>Begin Control At:</b> 
                              <input type="text" name="textfield2" size = "3" value = "75">
                              <sup>&deg</sup> </div>
                          </div>
                        </td></form>
						<form>
                        <td width="284" valign="middle"> 
                          <div align="center" class="TableCell"> Start Time:
                            <input type="text" name="textfield" size = "5" value="12:00">
                          </div>
                          <div align="center" class="TableCell">Stop Time:
                            <input type="text" name="textfield3" size = "5" value = "12:00">
                          </div></td></form>
                          </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <p>
                <input type="submit" name="Submit" value="Update">
                <br>
              </p>
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
                <td height="23" class="TableCell" width="104"><a href="user_lm_programs.jsp">Rooftop 
                  Air </a></td>
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
                <td height="23" class="TableCell" width="104">Irrigation Site 
                  1</td>
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
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>
