<%@ include file="StarsHeader.jsp" %>
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
-->
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

<body class="Background" leftmargin="0" topmargin="0" onload="">
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
		  <% String pageName = "WeekdayS.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              
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
                    <div align="center"><b><span class="Main"> THERMOSTAT - SCHEDULE</span></b></div>
                  </td>
                  <td valign="top" width="205" align = "right"><%@ include file="Notice.jsp" %> 
                    
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
              <br>
              <table width="80%" border="1" height="411" cellspacing = "0" cellpadding = "2">
                <tr> 
                  <td align = "center"  valign = "bottom" height="84"> 
                    <table width="100%" border="0" class = "TableCell" height="89">
                      <tr> 
                        <td width="53%" valign = "top" height="22"><b>SUMMER SATURDAY 
                          SCHEDULE - COOLING</b></td>
                        <td width="47%" valign = "top" align = "right" height="22"> 
                          <table width="82%" border="0" height="8" valign = "bottom" >
                            <tr> 
                              <td class = "TableCell" align = "right"><font color="#666699"><a class = "Link1" href = "WeekdayS.jsp">Weekday</a>&nbsp;&nbsp;Saturday</font>&nbsp;&nbsp;<a class = "Link1" href = "SundayS.jsp">Sunday</a></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="53%"> 
                          <table width="75%" border="0">
                            <tr>
                              <td class = "TableCell" align = "right" width="49%">Summer 
                                Start Date: </td>
                              <td width="51%"> 
                                <input type="text" name="" size = "8" value = "06/01/02">
                              </td>
                            </tr>
                            <tr>
                              <td class = "TableCell" align = "right" width="49%">Winter 
                                Start Date:</td>
                              <td width="51%"> 
                                <input type="text" name="" size = "8" value = "10/15/02">
                              </td>
                            </tr>
                          </table>
                        </td>
                        <td width="47%" align = "right" valign = "bottom">
                          <table width="114" border="0" height="66">
                            <tr>
                              <td height="17" align = "center"><a href = "SaturdayW.jsp"><img src="Winter.gif" width="40" height="39" border="0"></a></td>
                            </tr>
                            <tr>
                              <td class = "TableCell" align = "center">Click icon to view winter schedule</td>
                            </tr>
                          </table>
                          
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
                <tr> 
                  <td align = "center"> 
                      <table width="48%" border="0" height="33">
                        <tr> 
                        <td class = "TableCell" width="20%" height="4" align = "right" >Slide 
                          black arrows and thermometers to change temperatures 
                          and start times for each interval. </td>
                      </tr>
                    </table><br>
                    
                    <img src="TempBG.gif" style ="position:relative;">
                    <div id="MovingLayer1" style="position:absolute; width:25px; height:162px; left:309px; z-index:1; top: 408px"> 
                      <table width="100%" border="0">
                        <tr> 
                          <td>&nbsp;</td>
                          <td> 
                            <div id = "div1" class = "TableCell3">72&deg</div>
                            <img src="Thermometer.gif" style = "position:relative; left:-5px" width="16" height="131"  onMouseDown = "beginDrag(event,0,0,parseInt(document.getElementById('MovingLayer2').style.left)-3,200,'showTimeWake()','horizontal', 'MovingLayer1')"> 
                            <img id = "arrow1" src="Arrow.gif" style = "position:relative; left:-11px; top:-100px" onMouseDown = "beginDrag(event,-135,-35,0,0,'showTemp1()','vertical')"> 
                          </td>
                        </tr>
                      </table>
                    </div>
                    <div id="MovingLayer2" style="position:absolute; width:21px; height:162px; left:354px; z-index:1; top: 408px"> 
                      <table width="100%" border="0">
                        <tr> 
                          <td>&nbsp;</td>
                          <td> 
                            <div id = "div2" class = "TableCell3">72&deg</div>
                            <img src="Thermometer.gif" style = "position:relative; left:-5px" width="16" height="131"  onMouseDown = "beginDrag(event,0,0,parseInt(document.getElementById('MovingLayer3').style.left)-3,parseInt(document.getElementById('MovingLayer1').style.left)+3,'showTimeLeave()','horizontal', 'MovingLayer2')"> 
                            <img id = "arrow2" src="Arrow.gif" style = "position:relative; left:-9px; top:-100px"  onMouseDown = "beginDrag(event,-135,-35,0,0,'showTemp2()','vertical')"> 
                          </td>
                        </tr>
                      </table>
                    </div>
                    <div id="MovingLayer3" style="position:absolute;  width:21px; height:162px; left:507px; z-index:1; top: 408px"> 
                      <table width="100%" border="0">
                        <tr> 
                          <td>&nbsp;</td>
                          <td> 
                            <div id = "div3" class = "TableCell3">72&deg</div>
                            <img src="Thermometer.gif" style = "position:relative; left:-5px" width="16" height="131"  onMouseDown = "beginDrag(event,0,0,parseInt(document.getElementById('MovingLayer4').style.left)-3,parseInt(document.getElementById('MovingLayer2').style.left)+3,'showTimeReturn()','horizontal', 'MovingLayer3')"> 
                            <img id = "arrow3" src="Arrow.gif" style = "position:relative; left:-9px; top:-100px"  onMouseDown = "beginDrag(event,-135,-35,0,0,'showTemp3()','vertical')"> 
                          </td>
                        </tr>
                      </table>
                    </div>
                    <div id="MovingLayer4" style="position:absolute;  width:21px; height:162px; left:578px; z-index:1; top: 408px"> 
                      <table width="100%" border="0">
                        <tr> 
                          <td>&nbsp;</td>
                          <td> 
                            <div id = "div4" class = "TableCell3">72&deg</div>
                            <img src="Thermometer.gif" style = "position:relative; left:-5px" width="16" height="131"  onMouseDown = "beginDrag(event,0,0,629,parseInt(document.getElementById('MovingLayer3').style.left)+3,'showTimeSleep()','horizontal', 'MovingLayer4')"> 
                            <img id = "arrow4" src="Arrow.gif" style = "position:relative; left:-9px; top:-100px;"  onMouseDown = "beginDrag(event,-135,-35,0,0,'showTemp4()','vertical')"> 
                          </td>
                        </tr>
                      </table>
                    </div><br><br>
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
                          <input id = "time1" type="text"  size = "4" value = "06:00" name="text" onchange="Javascript:setChanged();timeChange(this, 'MovingLayer1', 'time2', null);">
                        </td>
                        <td class = "TableCell" width="25%"> Start At: 
                          <input id = "time2" type="text"  size = "4" value = "08:30" name="text2" onchange="Javascript:setChanged();timeChange(this, 'MovingLayer2', 'time3', 'time1');">
                        </td>
                        <td class = "TableCell" width="25%"> <span class = "Main"> 
                          </span> Start At: 
                          <input id = "time3" type="text"  size = "4" value = "17:00" name="text3" onchange="Javascript:setChanged();timeChange(this, 'MovingLayer3', 'time4', 'time2');">
                        </td>
                        <td width="25%" class = "TableCell"> Start At: 
                          <input id = "time4" type="text"  size = "4" value = "21:00" name="text4" onchange="Javascript:setChanged();timeChange(this, 'MovingLayer4', null, 'time3');">
                        </td>
                      </tr>
                    </table><noscript><table width="100%" border="0" class = "TableCell">
  <tr>
                        <td>Temp: <input id="temp1" type="text" size = "3" name="temp1" onchange="setChanged()" value = "72"></td>
                        <td>Temp: <input id="temp2" type="text" size = "3" name="temp2" onchange="setChanged()" value = "72"></td>
                        <td>Temp: <input id="temp3" type="text" size = "3" name="temp3" onchange="setChanged()" value = "72"></td>
                        <td>Temp: <input id="temp4" type="text" size = "3" name="temp4" onchange="setChanged()" value = "72"></td>
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
                  <form name="form1" method="post" action="">
                    <td width="44%" align = "right" class = "TableCell" > 
                      <input type="button" name="Submit" value="Submit">
                    </td>
                    <td width="56%" align = "left" class = "TableCell"> 
                      <input type="submit" name="Submit2" value="Default Settings">
                    </td>
                  </form>
                </tr>
              </table>
              <p align="center" class="Main"><font face="Arial, Helvetica, sans-serif" size="1">Copyright 
                &copy; 2002, Cannon Technologies, Inc. All rights reserved.</font></p>
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
