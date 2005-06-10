<%
if( request.getParameter("clearids") != null)
{
	YC_BEAN.getRequestMessageIDs().clear();
	com.cannontech.clientutils.CTILogger.info("Clearing the RequestMessageIDs, they aren't coming back!");
}

	java.text.DecimalFormat format_2char = new java.text.DecimalFormat("00");
	
%>
<SCRIPT  LANGUAGE="JavaScript" SRC="../JavaScript/calendar.js"></SCRIPT>
<SCRIPT language="JavaScript">

function promptParameters(channel, cmd)
{
	if (true)
	{
		var captionStr = '';
		var htmlStr = '';
		var bgColor = '#666699';
		
		var isValid = false;
		
		var channelGroup = channel;
		for (var i = 0; i < channelGroup.length; i++)
		{
			if( channelGroup[i].checked)
			{
				captionStr = 'Please select the Load Profile parameters for Channel ' + channelGroup[i].value;
				htmlStr = buildPopupHTML(cmd);				
				isValid = true;
				break;
			}
		}
		
		if( !isValid)
		{
			captionStr = 'Invalid Operation';
			htmlStr = '<td class="columnHeader" align="center">You must select a Channel to perform this action on.</td>';
			bgColor = '#FF0000';
		}

		overlib(	htmlStr,
					STICKY,
					CAPTION, captionStr,
					CLOSECLICK,
					LEFT,
					ANCHOR, "",
					ANCHORALIGN, "LL", "UL",
					ANCHORX, -4,
					ANCHORY, 2,
					BGCOLOR, bgColor,
					BORDER, 2,
					CAPCOLOR, "#FFFFFF",
					CLOSECOLOR, "FFFFFF",
					CAPTIONSIZE, "12px",
					FGCOLOR, "#FFFFFF",
					CELLPAD, 4,
					HAUTO, VAUTO,
					WIDTH, 400,
					HEIGHT, 200
				);
	}
}

function buildPopupHTML(cmd)
{
	var htmlStr = '';
	if( cmd == 'DataCollection')
	{
		htmlStr += '<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">';
		htmlStr += '  <tr> ';
		htmlStr += '    <td width="50%" class="columnHeader">Date</td>';
		htmlStr += '    <td class="columnHeader" align="center"><div id="hourLabel">Hour</div></td>';
		htmlStr += '    <td class="columnHeader" align="center"><div id="minuteLabel">Minute</div></td>';
		htmlStr += '  </tr><tr>';
		htmlStr += '    <td width="50%" class="main" valign="bottom">';
		htmlStr += '      <input id="cal" type="text" name="lpDate" value="<%= datePart.format(YC_BEAN.getLPDate()) %>" size="8">';
		htmlStr += '		<a href="javascript:openCalendar(document.getElementById(\'cal\'))"';
		htmlStr += '		onMouseOver="window.status=\'Date Calendar\';return true;"';
		htmlStr += '		onMouseOut="window.status=\'\';return true;"> <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a>';
		htmlStr += '	</td>';
		htmlStr += '	<td class="main" valign="bottom" align="center">';
		htmlStr += '	  <select name="hour" id="hourID">';
			for (var i = 0; i < 24; i++) {
				var iStr = i.toString();
				if( i < 10)	{
					iStr = '0' + iStr;
				}
				htmlStr += '		<option value="' + i + '">' + iStr + '</option>';
			}
		htmlStr += '	  </select>';
		htmlStr += '	</td>';
		htmlStr += '	<td class="main" valign="bottom" align="center">';
		htmlStr += '	  <select name="minute" id="minuteID">';
			for (var i = 0; i < 60; i=i+5) {
				var iStr = i.toString();
				if( i < 10)	{
					iStr = '0' + iStr;
				}
				htmlStr += '		<option value="' + i + '">' + iStr + '</option>';
			}
		htmlStr += '	  </select>';
		htmlStr += '	</td>';
		htmlStr += '  </tr><tr>';
		htmlStr += '    <td width="50%" class="columnHeader">&nbsp;</td>';
		htmlStr += '	<td colspan="2" class="main" align="center" height="100" valign="bottom">';
		htmlStr += '	  <a href="javascript:setCommand(\'' + cmd + '\', \'getvalue lp \');disableAllButtons();" style="font-weight:bold" class="Link4">Collect</a>';
		htmlStr += '&nbsp;and archive up to 12 Load Profile intervals, inclusive of the hour and minute selected';
		htmlStr += '	</td>';
		htmlStr += '  </tr>';
		htmlStr += '</table>';
	}
	else if( cmd == 'PeakReport')
	{
		htmlStr += '<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">';
		htmlStr += '  <tr>';
		htmlStr += '	<td class="columnHeader">Date</td>';
		htmlStr += '	<td class="columnHeader"><div id="rateLabel">Rate</div></td>';
		htmlStr += '	<td class="columnHeader"><div id="numDaysLabel">Num Days</div></td>';
		htmlStr += '  </tr>';
		htmlStr += '  <tr>';
		htmlStr += '	<td class="main" valign="bottom">';
		htmlStr += '	  <input id="cal" type="text" name="lpDate" value="<%= datePart.format(YC_BEAN.getLPDate()) %>" size="8">';
		htmlStr += '		<a href="javascript:openCalendar(document.getElementById(\'cal\'))"';
		htmlStr += '		onMouseOver="window.status=\'Date Calendar\';return true;"';
		htmlStr += '		onMouseOut="window.status=\'\';return true;"> <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a>';
		htmlStr += '	</td>';
		htmlStr += '	<td class="main" valign="bottom">';
		htmlStr += '	  <select name="rate" id="rateID">';
		htmlStr += '		<option value="day">Day</option>';
		htmlStr += '		<option value="hour">Hour</option>';
		htmlStr += '		<option value="interval">Interval</option>';
		htmlStr += '	  </select>';
		htmlStr += '	</td>';
		htmlStr += '	<td class="main" valign="bottom">';
		htmlStr += '	  <input type="text" size="3" id="numDaysID" name="numDays" value="5">';
		htmlStr += '	</td>';
		htmlStr += '  </tr><tr>';
 		htmlStr += '    <td width="50%" class="columnHeader">&nbsp;</td>';
		htmlStr += '	<td colspan="2" class="main" align="center" height="100" valign="bottom">';
		htmlStr += '	  <a href="javascript:setCommand(\'' + cmd + '\', \'getvalue lp \');disableAllButtons();" style="font-weight:bold" class="Link4">Read Load Profile Peak Report</a>';
		htmlStr += '	</td>';
		htmlStr += '  </tr>';
		htmlStr += '</table>';
	}	
	
	return htmlStr;
}

function StopPropagation(e)
{
	if (true)
	{
		if (window.event)	// if IE or Safari
			window.event.cancelBubble = true;
		else
			e.stopPropagation();
	}
}


function setCommand(type, cmd)
{
	var channel = ' channel ';
	var channelGrp = commandForm.channel;
	for (var i = 0; i < channelGrp.length; i++)
	{
		if( channelGrp[i].checked)
		{
			channel += channelGrp[i].value;
			break;
		}
	}
	
	if (type == 'DataCollection')
	{
		var offsetGroup = commandForm.channel;
		cmd += channel;
		cmd += " " + document.getElementById('cal').value;
		cmd += " " + document.getElementById('hourID').value;
		cmd += ":" + document.getElementById('minuteID').value;
	}
	else if( type = 'PeakReport' )
	{
		d = new Date(document.getElementById('cal').value);
		d.setTime(d.getTime() + (86400000 * (parseInt(document.getElementById('numDaysID').value - 1) )));	//subtract one to include the full date
		
		cmd += " peak " + document.getElementById('rateID').value;
		cmd += channel;
		cmd += " " + [d.getMonth()+1,d.getDate(),d.getYear()].join('/');
		cmd += " " + document.getElementById('numDaysID').value;
	}
	else
	{
		cmd = "";
	}
	document.commandForm.command.value = cmd;
}

function disableAllButtons()
{
	document.body.style.cursor = 'wait';
	document.getElementById('DataCollectionID').disabled = true;	
	document.getElementById('ArchivedDataID').disabled = true;
	document.getElementById('PeakReportID').disabled = true;
	document.commandForm.lpDate.value=document.getElementById('cal').value;
	document.commandForm.submit();
}

function enableFields(form)
{
	if(document.getElementById("lpActionArchiveID").checked)
	{
		form.hour.disabled = true;
		document.getElementById('hourLabel').disabled = true;
		form.minute.disabled = true;
		document.getElementById('minuteLabel').disabled = true;
		form.rate.disabled = true;
		document.getElementById('rateLabel').disabled = true;
		form.numDays.disabled= true;
		document.getElementById('numDaysLabel').disabled = true;
	}
	else if( document.getElementById("lpActionCollectionID").checked)
	{
		form.hour.disabled = false;
		document.getElementById('hourLabel').disabled = false;
		form.minute.disabled = false;
		document.getElementById('minuteLabel').disabled = false;
		form.rate.disabled = true;
		document.getElementById('rateLabel').disabled = true;		
		form.numDays.disabled= true;
		document.getElementById('numDaysLabel').disabled = true;
	}
	else if( document.getElementById("lpActionReportID").checked)
	{
		form.hour.disabled = true;
		document.getElementById('hourLabel').disabled = true;
		form.minute.disabled = true;
		document.getElementById('minuteLabel').disabled = true;
		form.rate.disabled = false;
		document.getElementById('rateLabel').disabled = false;
		form.numDays.disabled= false;
		document.getElementById('numDaysLabel').disabled = false;
	}
}
</SCRIPT>
          <form name="commandForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet">
            <input type="hidden" name="deviceID" value="<%=deviceID%>">
            <input type="hidden" name="pointID" value="<%=YC_BEAN.getPointID()%>">
            <input type="hidden" name="command" value="">
            <input type="hidden" name="timeOut" value="8000">
			<input type="hidden" name="updateDB" value="true">
            <input type='hidden' name='action' value=''>
            <input id="redirect" type="hidden" name="REDIRECT" value="<%= redirect%>">
            <input id="referrer" type="hidden" name="REFERRER" value="<%= referrer%>">
            <td width="657" valign="top" bgcolor="#FFFFFF"> 
              <div align="center"> 
                <% String header = "MCT 410 - ADVANCED CONTROL COMMANDS"; %>
                <br>
                <table width="100%" border="0" cellspacing="0" cellpadding="3">
                  <tr> 
                    <td align="center" class="TitleHeader"><%= header %></td>
                  </tr>
                  <tr> 
                    <td width="100%" class="SubtitleHeader" align="center"> Meter Name:&nbsp;<u><%=liteYukonPao.getPaoName()%></u> </td>
                  </tr>
                </table>
                <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			    <% if (YC_BEAN.getErrorMsg().length() > 0 ) out.write("<span class=\"ErrorMsg\">" + YC_BEAN.getErrorMsg() + "</span><br>"); %>
                <table width="530" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td> 
	                  <table height="20" width="95%" align="center" border="0" cellspacing="0" cellpadding="0">
				  		<tr> 
			              	<% if (request.getParameter("InvNo") != null)	//we came from the Customer Account page
			              	{%>
							  <td align="center"><a href='<%=request.getContextPath()%>/operator/Consumer/CommandInv.jsp?InvNo=<%=invNo%>' class='Link1'><span class='NavText'>Back To:&nbsp;<%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(deviceID)%></span></a></td>
			              	<%}
			              	else {%>
							  <td align="center"><a href='<%=request.getContextPath()%>/apps/CommandDevice.jsp?deviceID=<%=deviceID%>' class='Link1'><span class='NavText'>Back To:&nbsp;<%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(deviceID)%></span></a></td>
			               <%}%>
				  		</tr>
					  </table>
					  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						<tr> 
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
						  <td height="19" bgcolor="888888" class="tableHeader">
							<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						      <tr>
							    <td class="tableHeader"">Load Profile Channel Selection</td>
							  </tr>
							</table>
						  </td>
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>		  
						</tr>
						<tr>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
						  <td>
							<table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
							  <tr> 
								<td class="columnHeader" height="22" align="center">Channel</td>
							  </tr>
							  <tr>
								<td class="main" valign="top">
								 <table class="TableCell" width="100%" align="center">
							<%
								com.cannontech.database.data.lite.LitePoint [] litePoints = 
									com.cannontech.database.cache.functions.PAOFuncs.getLitePointsForPAObject(deviceID);
								if( litePoints != null)
								{
									boolean found = false;
									com.cannontech.database.data.lite.LitePoint kwLP = null;
									com.cannontech.database.data.lite.LitePoint voltageLP = null;
									for (int i = 0; i < litePoints.length; i++)
									{
										if( litePoints[i].getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND)
										{
											kwLP = litePoints[i];
										}
										else if( litePoints[i].getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND )
										{
											voltageLP = litePoints[i];
										}
									}%>
										<tr> 
										  <td class="main" width="20%" align="center">
										  <%
										  if( kwLP != null)
										  {%> 
											<input id = "channelID" type="radio" name="channel" value="1" <%=( kwLP.getPointID() == YC_BEAN.getPointID()? "checked":"")%> onClick="document.commandForm.pointID.value=<%=kwLP.getPointID()%>;"><%= kwLP.getPointName()%>
										  <%}else{%>
											<input id = "channelID" type="radio" name="channel" value="1" <%=( voltageLP == null || voltageLP.getPointID() != YC_BEAN.getPointID()? "checked":"")%> onClick="document.commandForm.pointID.value=<%=PointTypes.SYS_PID_SYSTEM%>;">Channel 1 (kW-LP)
										  <%}%>
										  </td>
										  <td class="main" width="20%" align="center"> 
										  <%if( voltageLP != null)
										  {%>
											<input id = "channelID" type="radio" name="channel" value="4" <%=(voltageLP.getPointID() == YC_BEAN.getPointID()? "checked":"")%> onClick="document.commandForm.pointID.value=<%=voltageLP.getPointID()%>;"><%=voltageLP.getPointName()%>
										  <%} else {%>
											<input id = "channelID" type="radio" name="channel" value="4" onClick="document.commandForm.pointID.value=<%=PointTypes.SYS_PID_SYSTEM%>;">Channel 4 (Voltage-LP)
										  <%}%>
										  </td>
										</tr>
								<%}%>
								
								  </table>
								</td>							  
							  </tr>
						   </table>
						  </td>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_right.gif">&nbsp;</td>
						</tr>
						<tr>
						  <td width="6" height="9"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
						  <td background="<%=request.getContextPath()%>/WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
						  <td width="6" height="9"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
						</tr>
					  </table>
					  					  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						<tr> 
						  <td><br>
						  </td>
						</tr>
					  </table>					  								  
					  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						<tr> 
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
						  <td height="19" bgcolor="888888" class="tableHeader">
							<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						      <tr>
							    <td class="tableHeader"">Load Profile Peak Reporting</td>
								<td class="tableHeader" align="right">
								  <a href="javascript:promptParameters(document.commandForm.channel, 'PeakReport');" style="font-weight:bold" class="Link3" id="PeakReportID" name="PeakReport"
									  onMouseOver="window.status='Read the Load Profile peak report for the channel selected';return true;"
									  onMouseOut="window.status='';return true;">Read</a>
								</td>
							  </tr>
							</table>
						  </td>
						  
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>		  
						</tr>
						<tr>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
						  <td>
							<table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
							<tr>
						    <td>
							<table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">

							  
							  </table>
							  </td>
							  </tr>
							  <tr colspan="3" style="text-decoration:underline"> 
							    <td class="columnHeader">Load Profile Peak Report - Channel 1</td>
							  </tr>
							<%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND, PointTypes.LP_PEAK_REPORT);%>							  
							<%if( pointData != null){
								boolean bg = true;
								String tempStr = pointData.getStr();
								int beginIndex = 0;
								int endIndex = tempStr.indexOf("\n");
								while( endIndex > 0)
								{%>
							  <tr>
							    <td class="main" <%=(bg?"bgcolor='EEEEEE'":"")%>>
									<%=tempStr.substring(beginIndex, endIndex)%>
									<%
									tempStr = tempStr.substring(endIndex+1);
									endIndex = tempStr.indexOf("\n");
									bg = !bg;
									%>
							    </td>
							  </tr>
								<%}
	 						} else {%>
						  	  <tr>
							    <td class="main">---</td>
  							  </tr>
							<%}%>
							  <tr> 
							    <td class="columnHeader">&nbsp;</td>
							  </tr>
							  <tr style="text-decoration:underline"> 
							    <td class="columnHeader">Load Profile Peak Report - Channel 4</td>
							  </tr>
							<%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND, PointTypes.LP_PEAK_REPORT);%>
							<%if( pointData != null){
								boolean bg = true;
								String tempStr = pointData.getStr();
								int beginIndex = 0;
								int endIndex = tempStr.indexOf("\n");
								while( endIndex > 0)
								{%>
							  <tr>
							    <td class="main" <%=(bg?"bgcolor='EEEEEE'":"")%>>
									<%=tempStr.substring(beginIndex, endIndex)%>
									<%
									tempStr = tempStr.substring(endIndex+1);
									endIndex = tempStr.indexOf("\n");
									bg = !bg;
									%>
							    </td>
							  </tr>
								<%}
							} else {%>
							  <tr>
							    <td class="main">---</td>
  						  	  </tr>
							<%}%>
						    </table>
						  </td>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_right.gif">&nbsp;</td>
						</tr>
						<tr>
						  <td width="6" height="9"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
						  <td background="<%=request.getContextPath()%>/WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
						  <td width="6" height="9"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
						</tr>
					  </table>
					  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						<tr> 
						  <td><br>
						  </td>
						</tr>
					  </table>					  								  
					  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						<tr> 
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
						  <td height="19" bgcolor="888888" class="tableHeader">
							<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						      <tr>
							    <td class="tableHeader"">Load Profile Data Collection</td>
								<td class="tableHeader" align="right">
								  <a href="javascript:promptParameters(document.commandForm.channel, 'DataCollection');" style="font-weight:bold" class="Link3" id="DataCollectionID" name="DataCollection"
									  onMouseOver="window.status='Collect and Archive Load Profile data for the channel selected';return true;"
									  onMouseOut="window.status='';return true;">Collect</a>
								</td>
							  </tr>
							</table>
						  </td>
						  
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>		  
						</tr>
						<tr>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
						  <td>
							<table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
							  <tr colspan="3" style="text-decoration:underline"> 
							    <td class="columnHeader">Recently Archived Load Profile Data - Channel 1</td>
							  </tr>
							
							<%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND, PointTypes.LP_ARCHIVED_DATA);%>
							<%if( pointData != null){
								boolean bg = true;
								String tempStr = pointData.getStr();
								int beginIndex = 0;
								int endIndex = tempStr.indexOf("\n");
								while( endIndex > 0)
								{%>
							  <tr>
							    <td class="main" <%=(bg?"bgcolor='EEEEEE'":"")%>>
									<%=tempStr.substring(beginIndex, endIndex)%>
									<%
									tempStr = tempStr.substring(endIndex+1);
									endIndex = tempStr.indexOf("\n");
									bg = !bg;
									%>
							    </td>
							  </tr>
								<%}
	 						} else {%>
						  	  <tr>
							    <td class="main">---</td>
  							  </tr>
							<%}%>
							  <tr> 
							    <td class="columnHeader">&nbsp;</td>
							  </tr>
							  <tr colspan="3" style="text-decoration:underline"> 
							    <td class="columnHeader">Recently Archived Load Profile Data - Channel 4</td>
							  </tr>
							<%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND, PointTypes.LP_ARCHIVED_DATA);%>
							<%if( pointData != null){
								boolean bg = true;
								String tempStr = pointData.getStr();
								int beginIndex = 0;
								int endIndex = tempStr.indexOf("\n");
								while( endIndex > 0)
								{%>
							  <tr>
							    <td class="main" <%=(bg?"bgcolor='EEEEEE'":"")%>>
									<%=tempStr.substring(beginIndex, endIndex)%>
									<%
									tempStr = tempStr.substring(endIndex+1);
									endIndex = tempStr.indexOf("\n");
									bg = !bg;
									%>
							    </td>
							  </tr>
								<%}
	 						} else {%>
						  	  <tr>
							    <td class="main">---</td>
  							  </tr>
  							<%}%>
							  <tr> 
							    <td class="columnHeader">&nbsp;</td>
							  </tr>
						    </table>
						  </td>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_right.gif">&nbsp;</td>
						</tr>
						<tr> 
						  <td height="19" bgcolor="888888" ></td>
						  <td height="19" bgcolor="888888" class="tableHeader">
							<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						      <tr>
						        <td class="tableHeader" >Load Profile Archived Data Display<BR><%=liteYukonPao.getPaoName()%> - <%=(YC_BEAN.getPointID() == PointTypes.SYS_PID_SYSTEM?"Undefined Point":com.cannontech.database.cache.functions.PointFuncs.getPointName(YC_BEAN.getPointID()))%>
						        </td>
						        <td class="tableHeader" align="right">
								  <input id="cal" type="text" name="lpDate" value="<%= datePart.format(YC_BEAN.getLPDate()) %>" size="8">
									<a href="javascript:openCalendar(document.getElementById('cal'))"
									onMouseOver="window.status='Date Calendar';return true;"
									onMouseOut="window.status='';return true;"> <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a>
									<a href="javascript:document.commandForm.action.value='LoadRPHData';disableAllButtons();" class="Link3" id="ArchivedDataID" 
										onMouseOver="window.status='Refresh the archived data view for the date and channel selected';return true;"
										onMouseOut="window.status='';return true;">View</a>
								</td>
							  </tr>
							</table>
						  </td>
						  <td height="19" bgcolor="888888" ></td>		  
						</tr>						
						<tr>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
						  <td>
							<table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">	    
							  <tr> 
							    <td> 						  
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class = "TableCell" height="19">
							  		<tr>
							  		  <td colspan="4"> 
										<table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
								  		  <tr style="text-decoration:underline"> 
											<td width="25%" class="columnHeader" align="center">Timestamp</td>
											<td width="25%"class="columnHeader" align="center">Value</td>
											<td width="25%" class="columnHeader" align="center">Timestamp</td>
											<td width="25%"class="columnHeader" align="center">Value</td>
								  		  </tr>
										    <%java.util.Vector lrphVector = YC_BEAN.getCurrentRPHVector();
											if( lrphVector != null){
												int half = lrphVector.size()/2;
												int vecSize = lrphVector.size();
												int doneSize = half + (lrphVector.size()%2);
												
												LiteRawPointHistory lrph;
												for( int i = 0; i < doneSize; i++)
												{
												  lrph = (LiteRawPointHistory)lrphVector.get(i);
												  %>
												  <tr <%=!(i%2==1)? "bgcolor='EEEEEE'":""%>> 
													<td width="25%" align="center" class="main"><%=dateTimeFormat.format(new java.util.Date(lrph.getTimeStamp()))%> 
													</td>
													<td width="25%" align="center" class="main"><%=format_nv3.format(lrph.getValue())%> 
													</td>
													<%
												  if( half > 0 ){
												    lrph = (LiteRawPointHistory)lrphVector.get(vecSize - half);
												    half--;%>
													<td width="25%" align="center" class="main"><%=dateTimeFormat.format(new java.util.Date(lrph.getTimeStamp()))%> 
													</td>
													<td width="25%" align="center" class="main"><%=format_nv3.format(lrph.getValue())%> 
													</td>
													<%}
												  else{%>
													<td width="25%" align="center" class="main">&nbsp;</td>
													<td width="25%" align="center" class="main">&nbsp;</td>
													<%}%>
												  </tr>
												<%}
											  }%>
								  		</table>
							    	  </td>
							  		</tr>
								  </table>
                          		</td>
	                          </tr>
							
							</table>
						  </td>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_right.gif">&nbsp;</td>
						</tr>
						<tr>
						  <td width="6" height="9"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
						  <td background="<%=request.getContextPath()%>/WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
						  <td width="6" height="9"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
						</tr>
					  </table>
	                  <table height="20" width="95%" align="center" border="0" cellspacing="0" cellpadding="0">
				  		<tr> 
			              	<% if (request.getParameter("InvNo") != null)	//we came from the Customer Account page
			              	{%>
							  <td align="center"><a href='<%=request.getContextPath()%>/operator/Consumer/CommandInv.jsp?InvNo=<%=invNo%>' class='Link1'><span class='NavText'>Back To:&nbsp;<%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(deviceID)%></span></a></td>
			              	<%}
			              	else {%>
							  <td align="center"><a href='<%=request.getContextPath()%>/apps/CommandDevice.jsp?deviceID=<%=deviceID%>' class='Link1'><span class='NavText'>Back To:&nbsp;<%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(deviceID)%></span></a></td>
			               <%}%>
				  		</tr>
					  </table>								  
                    </td>
                  </tr>
                  <tr> 
                    <td>&nbsp;</td>
                  </tr>
                </table>
                <br>
              </div>
          </form>