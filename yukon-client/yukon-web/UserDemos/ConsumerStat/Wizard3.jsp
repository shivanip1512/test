<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<SCRIPT LANGUAGE="JavaScript">

<!-- This script and many more are available free online at -->
<!-- The JavaScript Source!! http://javascript.internet.com -->
<!-- Evaldo Nunes (woodstock@globo.com)  -->

var text = ["<b>CYCLE AC <br>Light, Medium</b> - When controlled, your air conditioning compressor will be interrupted for 10 minutes out of every half hour if you sign up for the light program and interrupted for 15 minutes out of every half hour if you sign up for the medium program.",
			"<b>SETBACK <br>2&deg, 4&deg</b> - When controlled, your air conditioning compressor will be set back 2 degrees or 4 degrees depending on the program you sign up for. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature." ];

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







</script>
</head>
<body bgcolor="#666699" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip" style="left: -50px"> </div>
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
          <td width="1" height="102" bgcolor="#000000"><img src="stat/VerticalRule.gif" width="1"></td>
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
                <table width="64%" border="1"cellspacing = "0">
                  <tr> 
                    <td width="50%"> 
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
                    <td width="50%">
                      <table width="100%" border="0" height="80">
                        <tr> 
                          <td align = "center" width="15%"> 
                            <p><b><img id = "0" src="SetBack.gif" width="60" height="59" onClick = "toolTipAppear(event, 'tool', 1, 350, 90)"></b><font face="Arial, Helvetica, sans-serif" size="1"><br>
                              click on icon for a description</font></p>
                          </td>
                          <td width="85%"> 
                            <table width="102%" border="0" height="74">
                              <tr> 
                                <td class = "TableCell"><b> 
                                  <input type="checkbox" name="checkbox2" value="checkbox">
                                  SetBack</b></td>
                              </tr>
                              <tr> 
                                <td> 
                                  <table class = "TableCell" width="100%" border="0">
                                    <tr> 
                                      <td align = "right" width="30%"> 
                                        <input type="radio" name="radiobuttonSB" value="radiobutton">
                                      </td>
                                      <td width="70%">2 degrees</td>
                                    </tr>
                                    <tr> 
                                      <td align = "right" width="30%"> 
                                        <input type="radio" name="radiobuttonSB" value="radiobutton">
                                      </td>
                                      <td width="70%">4 degrees</td>
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
