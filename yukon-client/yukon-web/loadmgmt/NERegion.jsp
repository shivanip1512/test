
<%@ include file="Functions.js" %>
<!-- JavaScript needed for jump menu--->



<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="images/LoadImage.jpg">&nbsp;</td>
		  <td width="1" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="images/Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="353" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp; 
                  Load Management </td>
                <td width="235" valign="middle">&nbsp;</td>
                <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="759" valign="top" bgcolor="#000000"> 
		  <td width="1" bgcolor="#000000" height="1"></td>
		</tr>
        <tr> 
          <td width="759" valign="middle" bgcolor="#FFFFFF">
			<table width="740" border="0" cellspacing="0" cellpadding="0" align="center" >
              <tr>
                <td><form >
                    <div align="right"><span class="MainText"><br>
                      Control Areas:</span> 
                      <select   name="select" onchange="location=this.options[this.selectedIndex].value">
                        <option value="AllControlAreas.jsp">All</option>
                        <option value="NorthernSub.jsp">Northern Substation</option>
                        <option value="AllControlAreas.jsp">Western Substation</option>
                        <option value="NERegion.jsp" selected>NE Region PF</option>
                      </select>
                    </div>
                  </form>
				</td>
              </tr>
            </table>
							  
                  
            <table width="744" border="1" align="center" cellpadding="0" cellspacing="0">
              <tr> 
                      <td>
                        <table width="740" border="0" cellspacing="0" cellpadding="0">
						  <tr class="HeaderCell"> 
							
                      <td width="409">&nbsp;&nbsp;Control Area: NE Region PF</td>
						  </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
				    
            <table width="744" border="1" align="center" cellpadding="2" cellspacing="0">
              <tr valign="top" class="HeaderCell"> 
                <td width="56"> 
                  <div align="center">State</div>
                </td>
                <td width="69"> 
                  <div align="center">Target</div>
                </td>
                <td width="98"> 
                  <div align="center">Current<br>
                    /Projected</div>
                </td>
                <td width="111"> 
                  <div align="center">Date/Time</div>
                </td>
                <td width="89"> 
                  <div align="center">Time Window</div>
                </td>
                <td width="56"> 
                  <div align="center">Priority</div>
                </td>
                <td width="114"> 
                  <div align="center">Graphs</div>
                </td>
                <td width="101"> 
                  <div align="center">Reports</div>
                </td>
              </tr>
              <tr valign="top">
                <td width="56" class="TableCell"><div name = "sub" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'areaMenu')" >Active</div></td>
                <td width="69" class="TableCell">98%</td>
                <td width="98" class="TableCell">97.2%</td>
                <td width="111" class="TableCell">08-24-03 09:03</td>
                <td width="89" class="TableCell">N/A</td>
                <td width="56" class="TableCell">1</td>
                <td width="114" class="TableCell"> 
                  <select name="selectGraph" onChange="location = this.options[this.selectedIndex].value;">
                    <option value="AllControlAreas.jsp">Today's Load</option>
                    <option value="AllControlAreas.jsp"></option>
                  </select>
                </td>
                <td width="101" class="TableCell"> 
                  <select name="select3">
                    <option>Summary</option>
                    <option></option>
                  </select>
                </td>
              </tr>
            </table>
            <br>
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr>
                      <td>
                        <table width="740" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                              
                      <td class="HeaderCell">&nbsp;&nbsp;Programs for NE Region 
                        PF </td>
                            </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                    
            <table width="744" border="1" align="center" cellpadding="2" cellspacing="0">
              <tr valign="top" class="HeaderCell"> 
                <td width="137"> 
                  <div align="center">Program Name</div>
                </td>
                <td width="84"> 
                  <div align="center">Status</div>
                </td>
                <td width="133"> 
                  <div align="center">Start Date/Time</div>
                </td>
                <td width="129"> 
                  <div align="center">Stop Date/Time</div>
                </td>
                <td width="103"> 
                  <div align="center">Current Gear</div>
                </td>
                <td width="52"> 
                  <div align="center">Priority</div>
                </td>
              </tr>
              <tr valign="top"> 
                <td width="137" class="TableCell"><a href= "RGBSub.jsp" class="Link1"> 
                  <div name = "sub" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'progMenu')" > Cap 
                    Bank - Block 1</div>
                  </a> </td>
                <td width="84" class="TableCell"> Active</td>
                <td width="133" class="TableCell">09-06-03 12:16</td>
                <td width="129" class="TableCell">09-06-03 14:44</td>
                <td width="103" class="TableCell">Control</td>
                <td width="52" class="TableCell">1</td>
              </tr>
              <tr valign="top"> 
                <td width="137" class="TableCell"><div name = "sub" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'progMenu')" >Cap Bank - Block 2</div></td>
                <td width="84" class="TableCell"> Inactive</td>
                <td width="133" class="TableCell">09-13-03 11:21</td>
                <td width="129" class="TableCell">09-13-03 13:45</td>
                <td width="103" class="TableCell">Control</td>
                <td width="52" class="TableCell">2</td>
              </tr>
            </table>
            <br>
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="736" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                              
                      <td class="HeaderCell"><span class="HeaderCell">&nbsp;&nbsp;Groups 
                        for Cap Bank - Block 1</span></td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                    
            <table width="740" border="1" align="center" cellpadding="2" cellspacing="0">
              <tr valign="top" class="HeaderCell"> 
                <td width="181"> 
                  <div align="center">Group Name</div>
                </td>
                <td width="94"> 
                  <div align="center">State</div>
                </td>
                <td width="134"> 
                  <div align="center">Start Date/Time</div>
                </td>
                <td width="129"> 
                  <div align="center">Daily Control</div>
                </td>
                <td width="170"> 
                  <div align="center">Month/Season/Year</div>
                </td>
              </tr>
              <tr valign="top"> 
                <td width="181" class="TableCell"> 
                  <div name = "sub" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'groupMenu')" > Cap Bank 
                    Group 1</div>
                </td>
                <td width="94" class="TableCell">Controlling</td>
                <td width="134" class="TableCell"> 09-13-03 10:16</td>
                <td width="129" class="TableCell"> 1hr 15min</td>
                <td width="170" class="TableCell"> 
                  <div align="left">2.5hr / 3.75hr / 3.75hr</div>
                </td>
              </tr>
              <tr valign="top"> 
                <td width="181" class="TableCell"><div name = "sub" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'groupMenu')" >Cap Bank Group 2</div></td>
                <td width="94" class="TableCell">Controlling</td>
                <td width="134" class="TableCell">09-13-03 10:16</td>
                <td width="129" class="TableCell">1hr 15min </td>
                <td width="170" class="TableCell"> 
                  <div align="left">2.5hr / 3.75hr / 3.75hr</div>
                </td>
              </tr>
            </table>
                  <br>
				  <div id="areaMenu" class = "bgmenu" style = "width:130px; left: 214px; top: 216px"> 
                    <div id = "Start1ID" name = "start"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "???">&nbsp;&nbsp;&nbsp;Start</div>
					<div id = "Stop1ID" name = "stop" style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Stop</div>
				<div id = "Triggers1ID" name = "triggers" style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Triggers Change</div>
				<div id = "Daily1ID" name = "daily" style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Daily Time Change</div>
				<div id = "Disable1ID" name = "disable" style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Disable/Waive</div>
				</div>
				<div id="progMenu" class = "bgmenu" style = "width:130px; left: 214px; top: 216px"> 
                    <div id = "Start1ID" name = "start"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "???">&nbsp;&nbsp;&nbsp;Start/Stop</div>
					<div id = "Enable1ID" name = "enable" style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Enable/Disable</div>
				</div>
				<div id="groupMenu" class = "bgmenu" style = "width:130px; left: 214px; top: 216px"> 
                    <div id = "Shed1ID" name = "shed"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "???">&nbsp;&nbsp;&nbsp;Shed</div>
					<div id = "Restore1ID" name = "restore" style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Restore</div>
					<div id = "True1ID" name = "true" style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;True Cycle</div>
					<div id = "Smart1ID" name = "smart" style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Smart Cycle</div>
					<div id = "Disable1ID" name = "disable" style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Disable Group</div>
					<div id = "Confirm1ID" name = "confirm" style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Confirm</div>
				</div>
				</tr>
</table>

          </td>
		  <td width="1" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
</body>
</html>
