<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<SCRIPT LANGUAGE="JavaScript">

<!-- This script and many more are available free online at -->
<!-- The JavaScript Source!! http://javascript.internet.com -->
<!-- Evaldo Nunes (woodstock@globo.com)  -->

var text = ["<b>CYCLE AC<br> Light, Medium</b> - When controlled, your air conditioning compressor will be interrupted for 10 minutes out of every half hour if you sign up for the light program and interrupted for 15 minutes out of every half hour if you sign up for the medium program.",
			"<b>WATER HEATER<br>4Hr, 8Hr</b> - When controlled, power to your water heater’s heating elements is turned off for up to 4 hours or 8 hours depending on the program you choose. The hot water in the tank will still be available for you to use.<br><br>  <b>ETS</b> - Your Electric Thermal Storage water heater’s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The hot water stored in the tank will supply your hot water needs.",
			"<b>DUAL FUEL <br> Limited 4hr, Unlimited</b> - When controlled, electric power to your home’s heating system will be switched off, and your non-electric heat source will provide for your home’s heating needs. Control is limited to four hours consecutively when signed up for the limited program. While usually limited to a few hours, control could be for an extended period if signed up for the unlimited program.",
			"<b>ETS</b><br>Your Electric Thermal Storage heating system’s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The heat stored will supply your home needs.",
			"<b>POOL PUMP</b><br>When controlled, power to your pool pump is interrupted. Interruptions usually last for 4 hours or less.",
			"<b>HOT TUB</b><br>When controlled, power to your hot tub’s water heating elements are interrupted. Interruptions usually last for four hours or less." ];

function toolTipAppear(event, divId, index, w, h) {

	var coordx = getLeftCoordinate();
	var coordy = getTopCoordinate();
	var source;
	if (window.event)
      source = window.event.srcElement;
    else
      source = event.target;
	
	source.onmouseout = closeToolTip;
	
	
	var element = document.getElementById(divId);
	element.innerHTML = text[index]; 
	element.style.width = w;
	element.style.height = h;
	element.style.left = coordx + 'px';
	element.style.top = coordy + 'px';
	element.style.visibility = 'visible';
	
	
	
function closeToolTip() {
	var element = document.getElementById(divId);
	element.style.visibility = 'hidden';
}
	
	
	
	
	
	function getLeftCoordinate() {
	var x;
	if (window.event) {
		x = window.event.clientX + document.documentElement.scrollLeft + document.body.scrollLeft;
	}
	else {
		x = event.clientX + window.scrollX;
	}
	return x;
}

function getTopCoordinate() {
	var y;
	if (window.event) {
		y = window.event.clientY + document.documentElement.scrollTop + document.body.scrollTop + 20;
	}
	else {
		y = event.clientY + window.scrollY + 20;
	}
	return y;
}
}







<!-- Begin
function Check(x,y) {
for(i=1;i<=4;i++) {
z = "option" + i ;
document.all[z].src = "http://javascript.internet.com/img/radio-buttons/off.gif" ;
}
document.all[x].src = "http://javascript.internet.com/img/radio-buttons/on.gif"
document.all.action.value = x
}
//  End -->
</script>
</head>
<body bgcolor="#666699" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip"> 
   </div>
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr bgcolor="#FFFFFF"> 
          <td width="102" height="102" background="Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr bgcolor="666699"> 
                <td width="265" height="28">&nbsp;&nbsp;&nbsp;</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="login.jsp" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="#666699" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br> 
              
              <p><b><span class="Main">SIGN UP WIZARD<br>
                </span></b></p>
              <p class = "Main">Select the energy programs to sign-up for</p><form>
                <table width="64%" border="1" height="311" cellspacing = "0">
                  <tr> 
                    <td width="56%" height="103"> 
                      <table width="100%" border="0" height="80">
                      <tr> 
                        <td align = "center" width="15%"> 
                          <p><b><img id = "0" src="AC.gif" width="60" height="59" onclick = "toolTipAppear(event, 'tool', 0, 350, 90)"></b><font face="Arial, Helvetica, sans-serif" size="1"><br>
                            click on icon for a description</font></p>
                        </td>
                        <td width="85%"> 
                          <table width="102%" border="0" height="74">
                            <tr> 
                              <td class = "TableCell"><b> 
                                <input type="checkbox" name="checkbox" value="checkbox">
                                Cycle AC</b></td>
                            </tr>
                            <tr> 
                              <td> 
                                <table class = "TableCell" width="100%" border="0">
                                  <tr> 
                                    <td align = "right" width="30%"> 
                                      <input type="radio" name="radiobuttonAC" value="radiobutton">
                                    </td>
                                    <td width="70%">Light</td>
                                  </tr>
                                  <tr> 
                                    <td align = "right" width="30%"> 
                                      <input type="radio" name="radiobuttonAC" value="radiobutton">
                                    </td>
                                    <td width="70%">Medium</td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                    <td width="44%" height="103"> 
                      <table width="100%" border="0" height="40">
                      <tr> 
                        <td align = "center" width="15%"><img id = "3" src="Electric.gif" width="60" height="59" onclick = "toolTipAppear(event, 'tool', 3, 350, 60)"><font face="Arial, Helvetica, sans-serif" size="1"><br>
                          click on icon for a description</font></td>
                        <td width="85%"> 
                          <table class = "TableCell" width="102%" border="0" height="74">
                            <tr> 
                              <td valign = "top"><b> 
                                <input type="checkbox" name="checkbox5" value="checkbox">
                                ETS</b></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
                <tr> 
                    <td width="56%"> 
                      <table width="100%" border="0" height="82">
                      <tr> 
                        <td align = "center" width="15%"><img id = "1" src="WaterHeater.gif" width="60" height="59" onclick = "toolTipAppear(event, 'tool', 1, 350, 150)" ><font face="Arial, Helvetica, sans-serif" size="1"><br>
                          click on icon for a description</font></td>
                        <td width="85%"> 
                          <table width="102%" border="0" height="74">
                            <tr> 
                              <td class="TableCell"><b> 
                                <input type="checkbox" name="checkbox2" value="checkbox">
                                Water Heater</b></td>
                            </tr>
                            <tr> 
                              <td> 
                                <table class = "TableCell" width="100%" border="0">
                                  <tr> 
                                    <td align = "right" width="30%"> 
                                      <input type="radio" name="radiobuttonWH" value="radiobutton">
                                    </td>
                                    <td width="70%">4 Hours</td>
                                  </tr>
                                  <tr> 
                                    <td align = "right" width="30%"> 
                                      <input type="radio" name="radiobuttonWH" value="radiobutton">
                                    </td>
                                    <td width="70%">8 Hours</td>
                                  </tr>
                                  <tr> 
                                    <td align = "right" width="30%"> 
                                      <input type="radio" name="radiobuttonWH" value="radiobutton">
                                    </td>
                                    <td width="70%">ETS</td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                    <td width="44%" height="77" > 
                      <table width="100%" border="0" height="40">
                      <tr> 
                        <td align = "center" width="15%"><img id="4" src="Pool.gif" width="60" height="59" onclick = "toolTipAppear(event, 'tool', 4, 350, 45)"><font face="Arial, Helvetica, sans-serif" size="1"><br>
                          click on icon for a description</font></td>
                        <td width="85%"> 
                          <table class = "TableCell" width="102%" border="0" height="74">
                            <tr> 
                              <td valign = "top" ><b> 
                                <input type="checkbox" name="checkbox4" value="checkbox">
                                Pool Pump</b></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
                <tr> 
                    <td width="56%" height="79" > 
                      <table width="100%" border="0" height="80">
                        <tr> 
                          <td align = "center" width="15%"> 
                            <p><b><img id = "0" src="DualFuel.gif" width="60" height="59" onClick = "toolTipAppear(event, 'tool', 2, 350, 90)"></b><font face="Arial, Helvetica, sans-serif" size="1"><br>
                              click on icon for a description</font></p>
                          </td>
                          <td width="85%"> 
                            <table width="102%" border="0" height="74">
                              <tr> 
                                <td class = "TableCell"><b> 
                                  <input type="checkbox" name="checkbox3" value="checkbox">
                                  Dual Fuel Space Heater</b></td>
                              </tr>
                              <tr> 
                                <td> 
                                  <table class = "TableCell" width="100%" border="0">
                                    <tr> 
                                      <td align = "right" width="30%"> 
                                        <input type="radio" name="radiobuttonDF" value="radiobutton">
                                      </td>
                                      <td width="70%">Limited</td>
                                    </tr>
                                    <tr> 
                                      <td align = "right" width="30%"> 
                                        <input type="radio" name="radiobuttonDF" value="radiobutton">
                                      </td>
                                      <td width="70%">Unlimited</td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="44%" height="79" > 
                      <table width="100%" border="0">
                      <tr> 
                        <td align = "center" width="15%"><img id="5" src="HotTub.gif" width="60" height="59" onclick ="toolTipAppear(event, 'tool', 5, 350, 45)"><font face="Arial, Helvetica, sans-serif" size="1"><br>
                          click on icon for a description</font></td>
                        <td width="85%"> 
                          <table class = "TableCell" width="102%" border="0" height="74">
                            <tr> 
                              <td valign = "top" ><b> 
                                <input type="checkbox" name="checkbox6" value="checkbox">
                                Hot Tub</b></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table></form>
              
              <br>
            </div>
            <table width="150" border="0" cellspacing="0" cellpadding="3" align="center">
              <tr> 
                <form method="post" action="Wizard4.jsp">
                  <td width="95" valign="top" align="center"> 
                    <input type="submit" name="Submit" value="  Next  ">
                  </td>
                  <td width="98" valign="top" align="center"> 
                    <input type = "button" value="Cancel" name = "cancel" onclick = "goBack()">
                  </td>
                </form>
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
<br>
</body>
</html>
