<%@ include file="lm_header.jsp" %>

<%
if( request.getParameter("submit") != null )
{
	lmSession.setRefreshRate(LMSession.REF_SECONDS_PEND);
	return;
}

String cmd = request.getParameter("cmd");
String itemid = request.getParameter("itemid");

if( cmd == null || itemid == null )
{
	CTILogger.warn(
		"No command or item has been selected, redirecting request to: " + lmSession.DEF_REDIRECT );
	response.sendRedirect( lmSession.DEF_REDIRECT );
	return;
}

WebCmdMsg cmdMsg = LMCmdMsgFactory.createCmdMsg( cmd, new Integer(itemid), null, lcCache );

%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" type="text/css" href="css/lm.css">
<script langauge="Javascript" src="../JavaScript/calendar.js"></script>
<script language="JavaScript" src="../JavaScript/drag.js"></script>

<script language="JavaScript">

function update()
{
	//validate the inputs first
	if( document.cmdForm.startdate != null && document.cmdForm.stopdate != null
		&& !document.cmdForm.stopbutton[0].checked //see if the Manual Stop option is checked
		&& !validateDate(
			document.cmdForm.startdate.value + ' ' + document.cmdForm.startTime1.value,
			document.cmdForm.stopdate.value + ' ' +	document.cmdForm.stopTime1.value) )
	{
		alert('The Start date/time must be before the Stop date/time');
		return false;
	}
	
	/* Post to the actual servlet to do the work (must do first to ensure the command gets out) */
	document.cmdForm.attributes["action"].value = "/servlet/LCConnectionServlet";
	document.cmdForm.submit();
	
	/* Post to ourself lastly */
	document.cmdForm.attributes["action"].value = "lmcmd.jsp?submit=true";
	document.cmdForm.submit();
	
	self.close();
	opener.location.reload(true);
	return true;
}

var browser = new Object();
browser.isNetscape = false;
browser.isMicrosoft = false;
if (navigator.appName.indexOf("Netscape") != -1)
	browser.isNetscape = true;
else if (navigator.appName.indexOf("Microsoft") != -1)
	browser.isMicrosoft = true;

function moveStartStopPtr(s) 
{
	var hour, minute, time;

	if (s == 'start')  
	 time = document.cmdForm.startTime1.value.split(":");
	else if (s == 'stop')
	 time = document.cmdForm.stopTime1.value.split(":");

	if (time.length == 2)
	{
		hour = parseInt(time[0], 10);
		minute = parseInt(time[1], 10);
	}
	else if (time.length == 1)
	{
		hour = parseInt(time[0], 10);
		minute = 0;
	}
	else
	{
		hour = 0;
		minute = 0;
	}

		
	if (hour > 23){   
		hour = 23;
		minute = 50;}
	else if (hour < 0)
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
	
	if (s == 'start')
	{
		document.cmdForm.startTime1.value = hour + ":" + minute;
		document.cmdForm.startPtr.style.left = lgMove + smMove;
	}
	else if (s == 'stop')
	{
		document.cmdForm.stopTime1.value = hour + ":" + minute;
		document.cmdForm.stopPtr.style.left = lgMove + smMove;
	}

}

function showStartTime1()
{
	var s = document.images['startPtr'];
	var curPos = parseInt(s.style.left, 10);
	var hourStr = "0" + Math.floor((curPos - 81 + 72) * 10 / 60);
	hourStr = hourStr.substr(hourStr.length-2, 2);
	var minuteStr = "0" + (curPos - 81 + 72) * 10 % 60;
	minuteStr = minuteStr.substr(minuteStr.length-2, 2);
	document.cmdForm.startTime1.value = hourStr + ":" + minuteStr;
}

function showStopTime1()
{ 
  var sp = document.images['stopPtr'];
  var curPos = parseInt(sp.style.left, 10);
  var hourStr = "0" + Math.floor((curPos - 81 + 72) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + (curPos-81 + 72) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
  document.cmdForm.stopTime1.value = hourStr + ":" + minuteStr;
  
}
function setStartPixTime()
{
	(navigator.appName == 'Netscape')
	? document.images['startPtr'].style.top = -9 +"px"
	: document.images['startPtr'].style.top = -9 + "px";

	moveStartStopPtr('start');
}

function setStopPixTime()
{
	(navigator.appName == 'Netscape')
	? document.images['stopPtr'].style.top = -8 +"px"
	: document.images['stopPtr'].style.top = -9 + "px";
	
	moveStartStopPtr('stop');
}

</script>
</head>


<body leftmargin="0" topmargin="0" bgcolor="#FFFFFF">
	<div align="center">

	<form name="cmdForm" method="post" >
		<input type="hidden" name="cmd" value="<%= cmd %>" >
		<input type="hidden" name="itemid" value="<%= itemid %>" >
		
		
    <div class="confMsg"><BR><%= cmdMsg.getHTMLTextMsg() %></div>
		<BR>
<%
	if( ILCCmds.PROG_START.equals(cmd) || ILCCmds.AREA_START_PROGS.equals(cmd) )
	{
		LMProgramBase prg = (LMProgramBase)lcCache.getProgram( new Integer(itemid) );
%>
	<div class="TableCell"> 
	  <div align="center">Select either Start Now or Start at and select a Data and Time:</div>
	</div>
    <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
		  
            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Start gear: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179">
				  <select name="gearnum">
				<% 	java.util.List gearList = 
							( prg instanceof IGearProgram
								? ((IGearProgram)prg).getDirectGearVector()
								: new java.util.Vector(0) );
						
						for( int i = 0; i < gearList.size(); i++ )
						{
				%>
						<option value=<%= i+1 %> <%= (i == 0 ? "selected" : "") %> >
							<%= gearList.get(i).toString() %>
						</option>
				<%		}
				%>
			  </select>
			  
			  </td>
            </tr>
		  
            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Start now: </b></div>
              </td>
              <td width="25"> 
                <input type="radio" name="startbutton" value="startnow" checked>
                <br>
              </td>
              <td width="36">&nbsp;</td>
              <td width="179">&nbsp;</td>
            </tr>
			
            <tr> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Start at: </b></div>
              </td>
              <td width="25"> 
                <input type="radio" name="startbutton" value="startat">
              </td>
              <td width="36" class="TableCell"> 
                <div align="right">Date: </div>
              </td>
              <td width="179"> 
                <input type="text" name="startdate" value="<%= LCUtils.DATE_FORMATTER.format(new java.util.Date()) %>" size="8">
                <a href="javascript:openCalendar(cmdForm.startdate)"
						onMouseOver="window.status='Start Date Calendar';return true;"
						onMouseOut="window.status='';return true;"> 
						<img src="<%=request.getContextPath()%>/Images/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a></td>
            </tr>
<!--
/*
javascript:show_calendar('cmdForm.date')
javascript:show_calendar('cmdForm.date', [Month from 0 (Jan) to 11 (Dec)], [4-digit Year], 
						[Format like 'MM/DD/YYYY'], 'POPUP', sPopUpLoc );
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;">
*/
-->			
            <tr> 
              <td width="85" height="85">&nbsp; </td>
  	            <td width="25" height="85">&nbsp;</td>
              <td width="36" height="85" class="TableCell"> 
                <div align="right">Time: </div>
              </td>
              <td width="179" background="<%=request.getContextPath()%>/Images/Parts/StartStopBG.gif" height="85" valign="top"> 
                <table width="100" border="0" cellspacing="0" height="40" align="center">
                  <tr> 
                    <td valign = "top" align = "center"> 
                      <div> 
                        <input type="text" name="startTime1" value="<%= LCUtils.TIME_FORMATTER.format(new java.util.Date()) %>" size="5" onChange = "moveStartStopPtr('start')">
                      </div>
                    </td>
                  </tr>
                </table>
                <img name = "startPtr" onload = "setStartPixTime()" 
					onMouseDown = "beginDrag(event,0,0,152,9,'showStartTime1()','horizontal','')"
					src="<%=request.getContextPath()%>/Images/Parts/SliderShort.gif" width="17" height="19"
					style = "position:relative; top:0px; left:9px; cursor:pointer;" ><br>
              </td>
            </tr>
			
          </table>
        </td>
      </tr>
    </table>
	<BR>
<%	
	}
	
	if( ILCCmds.PROG_START.equals(cmd) || ILCCmds.PROG_STOP.equals(cmd) || ILCCmds.AREA_START_PROGS.equals(cmd) )
	{
%>
	<div class="TableCell"> 
	  <div align="center">Select either of the following stop options:</div>
	</div>	
    <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" class="TableCell" bgcolor="#FFFFFF">
      <tr> 
        <td height="145"> 
          <table width="350" border="0" cellspacing="0" cellpadding="3" align="center">
            <tr valign="top"> 
              <td width="81"> 
                <div align="right" class="TableCell"><b><%= (ILCCmds.PROG_STOP.equals(cmd) ? "Stop now: " : "Manual stop: ") %></b></div>
              </td>
              <td width="17"> 
                <input type="radio" name="stopbutton"
				value="<%= (ILCCmds.PROG_STOP.equals(cmd) ? "stopnow" : "stopmanual") %>"
				<%= (ILCCmds.PROG_STOP.equals(cmd) ? "checked" : "") %>>
                <br>
              </td>
              <td width="34">&nbsp;</td>
              <td width="170">&nbsp;</td>
            </tr>
            <tr> 
              <td width="81">
                <div align="right" class="TableCell"><b>Stop at: </b></div>
              </td>
              <td width="17"> 
                <input type="radio" name="stopbutton" 
				value="stopat"
				<%= (ILCCmds.PROG_START.equals(cmd) ? "checked" : "") %>>
              </td>
              <td width="34"> 
                <div align="right" class="TableCell">Date: </div>
              </td>
              <td width="170"> 
<%
 //add 4 hours worth of millis = 14400000
 java.util.Date stpDate = new java.util.Date();
 stpDate.setTime( stpDate.getTime() + 14400000 );
%>
                <input type="text" name="stopdate" value="<%= LCUtils.DATE_FORMATTER.format( stpDate ) %>" size="8">
                <a href="javascript:openCalendar(cmdForm.stopdate)"
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;">
						<img src="<%=request.getContextPath()%>/Images/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a></td>
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
                      <input type="text" name="stopTime1" value="<%= LCUtils.TIME_FORMATTER.format( stpDate ) %>" size="5" onChange = "moveStartStopPtr('stop')">
                    </td>
                  </tr>
                </table>
                <img name = "stopPtr" src="../../Images/Parts/SliderShort.gif" width="17" height="19"
				onMouseDown = "beginDrag(event,0,0,152,9,'showStopTime1()','horizontal','')"
				onLoad = "setStopPixTime()" style = "position:relative; top:0px; left:152px; cursor:pointer;"><br>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
<%
	}

	if( ILCCmds.GRP_SHED.equals(cmd) )
	{
%>

<table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Shed time: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179"> 
                <select name="duration">
                  <%
				  		for( int i = 0; i < LCUtils.SHED_STRS.length; i++ )
						{
				%>
                  <option value="<%= LCUtils.SHED_STRS[i] %>" <%= (i == 0 ? "selected" : "") %> > 
                  <%= LCUtils.SHED_STRS[i] %> </option>
                  <%		
				  		}
				%>
                </select>
              </td>
            </tr>
			
            <tr valign="top"> 
              <td width="85">&nbsp;</td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179"><div align="right" class="TableCell"><b>Note: Ripple switches ignore this setting</b></div></td>
            </tr>
			
          </table>
        </td>
      </tr>
    </table>

<%
	}

	if( ILCCmds.GRP_TRUE_CY.equals(cmd) || ILCCmds.GRP_SMRT_CY.equals(cmd) )
	{
%>

<table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Cycle %: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179"> 
                <select name="cyclepercent">
                  <%
				  		for( int i = 1; i <= 100; i++ )
						{
				%>				
                  <option value="<%= i %>" <%= (i == 50 ? "selected" : "") %> > 
                  <%= i %> </option>
                  <%		
				  		}
				%>
                </select>
              </td>
            </tr>

            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Period length: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179"> 
                <select name="duration">
                <%
				  		for( int i = 1; i <= 60; i++ )
						{
				%>				
                  <option value="<%= i + (i == 1 ? " minute" : " minutes") %>" <%= (i == 30 ? "selected" : "") %> > 
                  <%= i + (i == 1 ? " minute" : " minutes") %> </option>
                <%		
				  		}
				%>
                </select>
              </td>
            </tr>
			
            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Period count: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179"> 
                <select name="periodcnt">
                  <%
				  		for( int i = 1; i < 49; i++ )
						{
				%>				
                  <option value="<%= i %>" <%= (i == 8 ? "selected" : "") %> > 
                  <%= i %> </option>
                  <%		
				  		}
				%>
                </select>
              </td>
            </tr>

			
          </table>
        </td>
      </tr>
    </table>

<%
	}

	if( ILCCmds.AREA_TRIG_CHG.equals(cmd) )
	{	
		LMControlArea cntrlArea = (LMControlArea)lcCache.getControlArea( new Integer(itemid) );
		for( int i = 0; i < cntrlArea.getTriggerVector().size(); i++ )
		{
			LMControlAreaTrigger trigger = (LMControlAreaTrigger)cntrlArea.getTriggerVector().get(i);
%>
	<div class="TableCell"> 
	  <div align="center">Trigger #<%= i+1 %> settings</div>
	</div>	
	<table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
            <tr> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>New threshold: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179">
			  	<input type="text" name="newthreshold" value="<%= trigger.getThreshold() %>" size="10">
              </td>
            </tr>

            <tr> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>New restore offset: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179">
			  	<input type="text" name="newrestore" value="<%= trigger.getMinRestoreOffset() %>" size="10">
              </td>
            </tr>

          </table>
        </td>
      </tr>
    </table>
	<br>
<%
		}
	}

	if( ILCCmds.AREA_DAILY_CHG.equals(cmd) )
	{
		LMControlArea cntrlArea = (LMControlArea)lcCache.getControlArea( new Integer(itemid) );
		int winStartInt = LCUtils.decodeStartWindow(cntrlArea);
		int winStopInt = LCUtils.decodeStopWindow(cntrlArea);

		String winStart = (winStartInt == LMControlArea.INVAID_INT ? "" : CtiUtilities.decodeSecondsToTime(winStartInt) );
		String winStop = (winStopInt == LMControlArea.INVAID_INT ? "" : CtiUtilities.decodeSecondsToTime(winStopInt) );
%>
	<div class="TableCell"> 
	  <div align="center">Select the new window Start and Stop times:</div>
	</div>
    <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
		  
            <tr valign="top"> 
            <tr> 
              <td width="100" class="TableCell"> 
                <div align="right"><b>New start time: </b></div>
              </td>
              <td width="179" background="<%=request.getContextPath()%>/Images/Parts/StartStopBG.gif" height="85" valign="top"> 
                <table width="100" border="0" cellspacing="0" height="40" align="center">
                  <tr> 
                    <td valign = "top" align = "center"> 
                      <div> 
                        <input type="text" name="startTime1" value="<%= winStart %>" size="5" onChange = "moveStartStopPtr('start')">
                      </div>
                    </td>
                  </tr>
                </table>
                <img name = "startPtr" onload = "setStartPixTime()" 
					onMouseDown = "beginDrag(event,0,0,parseInt(document.cmdForm.stopPtr.style.left, 10),9,'showStartTime1()','horizontal','')"
					src="<%=request.getContextPath()%>/Images/Parts/SliderShort.gif" width="17" height="19"
					style = "position:relative; top:0px; left:9px; cursor:pointer;" ><br>
              </td>
            </tr>

            <tr valign="top"> 
            <tr> 
              <td width="100" class="TableCell"> 
                <div align="right"><b>New stop time: </b></div>
              </td>
              <td width="179" background="<%=request.getContextPath()%>/Images/Parts/StartStopBG.gif" height="85" valign="top"> 
                <table width="100" border="0" cellspacing="0" height="40" align="center">
                  <tr> 
                    <td valign = "top" align = "center"> 
                      <div> 
                        <input type="text" name="stopTime1" value="<%= winStop %>" size="5" onChange = "moveStartStopPtr('stop')">
                      </div>
                    </td>
                  </tr>
                </table>
                <img name = "stopPtr" onload = "setStopPixTime()" 
					onMouseDown = "beginDrag(event,0,0,152,parseInt(document.cmdForm.startPtr.style.left, 10),'showStopTime1()','horizontal','')"
					src="<%=request.getContextPath()%>/Images/Parts/SliderShort.gif" width="17" height="19"
					style = "position:relative; top:0px; left:9px; cursor:pointer;" ><br>
              </td>
            </tr>

          </table>
        </td>
      </tr>
    </table>
	<BR>
<%	
	}

	if( ILCCmds.AREA_START_PROGS.equals(cmd) )
	{
		LMControlArea cntrlArea = (LMControlArea)lcCache.getControlArea( new Integer(itemid) );
%>
	<div class="TableCell"> 
	  <div align="center">Select the programs you want to start:</div>
	</div>	
    <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" class="TableCell" bgcolor="#FFFFFF">
      <tr> 
        <td height="145"> 
          <table width="350" border="1" cellspacing="0" cellpadding="3" align="center">

		  <tr valign="top" class="HeaderCell"> 
			<td width="20"><div align="center">
				<input type="checkbox" name="allChks" value="true" onClick="this.form.submit()">
				Selected</div>
			</td>
			<td width="167"><div align="center">Program</div></td>
			<td width="34"><div align="center">Gear</div></td>
			<td width="81"><div align="center">State</div></td>
		  </tr>

<%
		java.util.List progList = cntrlArea.getLmProgramVector();
		java.util.Collections.sort( 
				progList,
				ProgramTableModel.PROGRAM_NAME_COMPARATOR );

		for( int i = 0; i < progList.size(); i++)
		{
			LMProgramBase prg = (LMProgramBase)progList.get(i);
%>
            <tr valign="top"> 
              <td width="20">
				<input type="checkbox" name="progid" value=<%= prg.getYukonID() %> onClick="this.form.submit()" >
              </td>
              <td width="167">
                <div class="TableCell">
				<%= LCUtils.getProgramValueAt(prg, ProgramTableModel.PROGRAM_NAME) %>
				</div>
              </td>
              <td width="34"> 
                <div align="right" class="TableCell">
                <select name="duration">
                <%
					for( int j = 1; j <= 8; j++ )
					{
				%>				
                  <option value="<%= j %>" <%= (j == 1 ? "selected" : "") %> > 
	                  <%= j %>
				  </option>
                <%		
					}
				%>
                </select></div>			
              </td>
              <td width="81">
                <div class="TableCell">
				  <%= LCUtils.getProgramValueAt(prg, ProgramTableModel.CURRENT_STATUS) %>
				</div>
			  </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
<%
		}
	}
%>


	  <BR>
		<input type="submit" name="Submit2" value="Ok" class="defButton" onclick = "return update()">
		<input type="submit" name="Submit" value="Cancel" class="defButton" onclick = "self.close()">
	</form>
	</div>

</body>
</html>