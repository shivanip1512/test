<%@ include file="StarsHeader.jsp" %>
<%
	StarsLMHardwareHistory hwHist = null;
	if (inventories.getStarsLMHardwareCount() > 0) {
		StarsLMHardware hw = inventories.getStarsLMHardware(0);
		hwHist = hw.getStarsLMHardwareHistory();
	}
	
	String programStatus = "Not Enrolled";
	if (hwHist != null && hwHist.getLMHardwareEventCount() > 0) {
		LMHardwareEvent event = hwHist.getLMHardwareEvent(0);
		if (event.getEventAction().equals("Activation Completed"))
			programStatus = "In Service";
		else
			programStatus = "Out of Service";
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
<!--
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




function doReenable(form) {
	form.action.value = "EnableService";
	form.submit();
}

function MM_popupMsg(msg) { //v1.0
  alert(msg);
}
//-->
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip"></div>
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
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
		  <% String pageName = "Programs.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "PROGRAMS - ENROLLMENT"; %>
              <%@ include file="InfoSearchBar.jsp" %>
             
              <div align="center"><span class="TableCell">Select the check boxes 
                and corresponding radio button of the programs this customer would 
                like to be enrolled in. </span><br>
                <br>
              </div>
              <table border="1" cellspacing="0" cellpadding="3" width="366" height="321">
                <tr>
                  <td width="83" class="HeaderCell" align = "center">Description</td>
                  <td width="132" class="HeaderCell"> 
                    <div align="center">Program Enrollment</div>
                  </td>
                  <td width="125" class="HeaderCell"> 
                    <div align="center">Status</div>
                  </td>
                </tr>
                <tr>
                  <td width="83" align = "center"><img id = "0" src="AC.gif" width="60" height="59" onclick = "toolTipAppear(event, 'tool', 0, 350, 90)"> 
                    <br>
                    <span class = "TableCell">Click 
                    Above</span></td>
                  <td width="132" align = "center"> 
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="23"> 
                          <input type="checkbox" name="ApplianceCategory" value="checkbox" checked>
                        </td>
                        <td width="84" class="TableCell">Cycle AC</td>
                      </tr>
                    </table>
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="37"> 
                          <div align="right"> 
                            <input type="radio" name="Program0" value="radiobutton" checked>
                          </div>
                        </td>
                        <td width="70" class="TableCell">Medium</td>
                      </tr>
                      <tr> 
                        <td width="37"> 
                          <div align="right"> 
                            <input type="radio" name="Program0" value="radiobutton">
                          </div>
                        </td>
                        <td width="70" class="TableCell">Light</td>
                      </tr>
                    </table>
                  </td>
                  <td width="125" valign="top" class="TableCell"> 
                    <div align="center"><%= programStatus %></div>
                  </td>
                </tr>
                <tr>
                  <td width="83" align = "center"><img src="WaterHeater.gif" width="60" height="59" onclick = "toolTipAppear(event, 'tool', 1, 350, 150)" ><br>
                    <span class = "TableCell">Click 
                    Above</span></td>
                  <td width="132"> 
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="23"> 
                          <input type="checkbox" name="ApplianceCategory2" value="checkbox" checked>
                        </td>
                        <td width="84" class="TableCell">Water Heater</td>
                      </tr>
                    </table>
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="37"> 
                          <div align="right"> 
                            <input type="radio" name="Program1" value="radiobutton" checked>
                          </div>
                        </td>
                        <td width="70" class="TableCell">8 Hours</td>
                      </tr>
                      <tr> 
                        <td width="37"> 
                          <div align="right"> 
                            <input type="radio" name="Program1" value="radiobutton">
                          </div>
                        </td>
                        <td width="70" class="TableCell">4 Hours</td>
                      </tr>
                      <tr> 
                        <td width="37"> 
                          <div align="right"> 
                            <input type="radio" name="Program1" value="radiobutton">
                          </div>
                        </td>
                        <td width="70" class="TableCell">ETS</td>
                      </tr>
                    </table>
                  </td>
                  <td width="125" valign="top" class="TableCell"> 
                    <div align="center"><%= programStatus %></div>
                  </td>
                </tr>
                <tr>
                  <td width="83" align = "center"><img src="DualFuel.gif" width="60" height="59" onClick = "toolTipAppear(event, 'tool', 2, 350, 120)"><br>
                    <span class = "TableCell">Click 
                    Above</span></td>
                  <td width="132"> 
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="23"> 
                          <input type="checkbox" name="ApplianceCategory2" value="checkbox">
                        </td>
                        <td width="84" class="TableCell">Duel Fuel</td>
                      </tr>
                    </table>
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="37"> 
                          <div align="right"> 
                            <input type="radio" name="Program2" value="radiobutton">
                          </div>
                        </td>
                        <td width="70" class="TableCell">Unlimited</td>
                      </tr>
                      <tr> 
                        <td width="37"> 
                          <div align="right"> 
                            <input type="radio" name="Program2" value="radiobutton">
                          </div>
                        </td>
                        <td width="70" class="TableCell">Limited</td>
                      </tr>
                    </table>
                  </td>
                  <td width="125" valign="top" class="TableCell"> 
                    <div align="center">Not Enrolled</div>
                  </td>
                </tr>
                <tr>
                  <td width="83" align = "center"><img src="Electric.gif" width="60" height="59"  onclick = "toolTipAppear(event, 'tool', 3, 350, 60)"><br>
                    <span class = "TableCell">Click 
                    Above</span></td>
                  <td width="132"> 
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="23"> 
                          <input type="checkbox" name="ApplianceCategory2" value="checkbox">
                        </td>
                        <td width="84" class="TableCell">Electrical Heat ETS</td>
                      </tr>
                    </table>
                  </td>
                  <td width="125" valign="top" class="TableCell"> 
                    <div align="center">Not Enrolled</div>
                  </td>
                </tr>
                <tr>
                  <td width="83" align = "center"><img src="HotTub.gif" width="60" height="59" onclick ="toolTipAppear(event, 'tool', 5, 350, 45)"><br>
                    <span class = "TableCell">Click 
                    Above</span></td>
                  <td width="132"> 
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="23"> 
                          <input type="checkbox" name="ApplianceCategory2" value="checkbox">
                        </td>
                        <td width="84" class="TableCell">Hot Tub</td>
                      </tr>
                    </table>
                  </td>
                  <td width="125" valign="top" class="TableCell"> 
                    <div align="center">Not Enrolled</div>
                  </td>
                </tr>
                <tr>
                  <td width="83" align = "center"><img src="Pool.gif" width="60" height="59" onclick = "toolTipAppear(event, 'tool', 4, 350, 45)"><br>
                    <span class = "TableCell">Click 
                    Above</span></td>
                  <td width="132"> 
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="23"> 
                          <input type="checkbox" name="ApplianceCategory2" value="checkbox">
                        </td>
                        <td width="84" class="TableCell">Pool Pump</td>
                      </tr>
                    </table>
                  </td>
                  <td width="125" valign="top" class="TableCell"> 
                    <div align="center">Not Enrolled</div>
                  </td>
                </tr>
              </table>
             
              <form name="form1" method="post" action="ChangeForm.jsp">
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="Submit" value="Submit" onClick="MM_popupMsg('Are you sure you would like to modify these program options?')">
                      </div>
                    </td>
                    <td width="194"> 
                      <div align="left"> 
                        <input type="reset" name="Cancel2" value="Cancel">
                      </div>
                    </td>
                  </tr>
                </table>
                <p align="center" class="MainHeader"><b>Program History </b> 
                <table width="300" border="1" cellspacing="0" align="center" cellpadding="3">
                  <tr> 
                    <td class="HeaderCell" width="100" >Date</td>
                    <td class="HeaderCell" width="100" >Type - Duration</td>
                    <td class="HeaderCell" width="100" >Program</td>
                  </tr>
                  <tr> 
                    <td class="TableCell" width="100" >07/15/02</td>
                    <td class="TableCell" width="100" >Temp Opt Out - 3 Days</td>
                    <td class="TableCell" width="100" >Cycle AC<br>
                      Water Heater </td>
                  </tr>
                  <tr> 
                    <td class="TableCell" width="100" >12/25/01</td>
                    <td class="TableCell" width="100" >Temp Opt Out - 1 Day</td>
                    <td class="TableCell" width="100" >Cycle AC<br>
                      Water Heater </td>
                  </tr>
                  <tr> 
                    <td class="TableCell" width="100" >08/20/01</td>
                    <td class="TableCell" width="100" >Signup</td>
                    <td class="TableCell" width="100" >Cycle AC</td>
                  </tr>
                  <tr> 
                    <td class="TableCell" width="100" >02/12/01</td>
                    <td class="TableCell" width="100" >Signup</td>
                    <td class="TableCell" width="100" >Water Heater</td>
                  </tr>
<!--
<%
	boolean optOut = false;
	Calendar startCal = Calendar.getInstance();
	Calendar stopCal = Calendar.getInstance();
	
	for (int i = 0; i < hwHist.getLMHardwareEventCount(); i++) {
		LMHardwareEvent event = hwHist.getLMHardwareEvent(i);
		if (event.getYukonDefinition().equals( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_FUTUREACTIVATION )
			|| event.getYukonDefinition().equals( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_COMPLETED )) {
			optOut = true;
			stopCal.setTime( event.getEventDateTime() );
		}
		else if (event.getYukonDefinition().equals( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_TEMPTERMINATION ) && optOut) {
			optOut = false;
			startCal.setTime( event.getEventDateTime() );
			int duration = stopCal.get(Calendar.DATE) - startCal.get(Calendar.DATE);
			
			String durStr = String.valueOf(duration);
			if (duration > 1)
				durStr += " Days";
			else
				durStr += " Day";
%>
                <tr> 
                  <td class="TableCell"><%= dateFormat.format(event.getEventDateTime()) %></td>
                  <td class="TableCell"><%= durStr %></td>
                </tr>
<%
		}
		else {
			optOut = false;
		}
	}
%>
-->
                </table>
                <br>
              </form>
             
             </div>
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
