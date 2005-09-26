<%@ page import="com.cannontech.database.data.lite.LitePoint"%>
<%
if( request.getParameter("clearids") != null)
{
	YC_BEAN.getRequestMessageIDs().clear();
	com.cannontech.clientutils.CTILogger.info("Clearing the RequestMessageIDs, they aren't coming back!");
}

	java.text.DecimalFormat format_2char = new java.text.DecimalFormat("00");

LitePoint kwLP = null;
LitePoint voltageLP = null;

String channel_1Str = "Channel 1 (kW Profile)";
String channel_4Str = "Channel 4 (Volt Profile)";
int channel_1ID = PointTypes.SYS_PID_SYSTEM;
int channel_4ID = PointTypes.SYS_PID_SYSTEM;

LitePoint [] litePoints = com.cannontech.database.cache.functions.PAOFuncs.getLitePointsForPAObject(deviceID);
if( litePoints != null)
{
	for (int i = 0; i < litePoints.length; i++)
	{
		if( litePoints[i].getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND)
		{
			kwLP = litePoints[i];
			channel_1Str = kwLP.getPointName();
			channel_1ID = kwLP.getPointID();
		}
		else if( litePoints[i].getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND )
		{
			voltageLP = litePoints[i];
			channel_4Str = voltageLP.getPointName();
			channel_4ID = voltageLP.getPointID();
		}
	}
}
%>
<SCRIPT language="JavaScript">

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


function submitCommand(type, ptID, chnl)
{
	var cmd = "";
	if (type == 'DataCollection')
	{
		cmd += "getvalue lp channel " + chnl;
		cmd += " " + document.getElementById('DCcal').value;
		cmd += " " + document.getElementById('hourID').value;
		cmd += ":" + document.getElementById('minuteID').value;
		document.commandForm.lpDate.value=document.getElementById('DCcal').value;
	}
	else if( type == 'PeakReport' )
	{
		d = new Date(document.getElementById('PRcal').value);
		d.setTime(d.getTime() + (86400000 * (parseInt(document.getElementById('numDaysID').value - 1) )));	//subtract one to include the full date
		
		cmd += "getvalue lp peak " + document.getElementById('rateID').value;
		cmd += " channel " + chnl;
		cmd += " " + [d.getMonth()+1,d.getDate(),d.getYear()].join('/');
		cmd += " " + document.getElementById('numDaysID').value;
		document.commandForm.lpDate.value=document.getElementById('PRcal').value;
	}
	else if( type == 'LoadRPHData')
	{
		cmd = "";
		document.commandForm.lpDate.value=document.getElementById('RPHcal').value;
	}

	document.commandForm.action.value = type;	
	document.commandForm.command.value = cmd;
	document.commandForm.pointID.value = ptID;
	
	disableAllButtons();
	document.commandForm.submit();
}

function disableAllButtons()
{
	document.body.style.cursor = 'wait';
	document.getElementById('DCChannel1ID').disabled = true;
	document.getElementById('DCChannel4ID').disabled = true;

	document.getElementById('PRChannel1ID').disabled = true;
	document.getElementById('PRChannel4ID').disabled = true;
	
	document.getElementById('LoadRPH1ID').disabled = true;
	document.getElementById('LoadRPH4ID').disabled = true;
}
</SCRIPT>
          <form name="commandForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet">
            <input type="hidden" name="deviceID" value="<%=deviceID%>">
            <input type="hidden" name="lpDate" value="<%= datePart.format(YC_BEAN.getLPDate()) %>">
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
                    <td width="100%" class="SubtitleHeader" align="center"><%=liteYukonPao.getPaoName()%></td>
                  </tr>
                </table>
                <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			    <% if (YC_BEAN.getErrorMsg().length() > 0 ) out.write("<span class=\"ErrorMsg\">" + YC_BEAN.getErrorMsg() + "</span><br>"); %>
                <table width="600" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td> 
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
		                        <td class="tableHeader"">Profile Peak Summary Reports</td>
							  </tr>					
						    </table>
						  </td>
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
					    </tr>
                        <tr> 
                          <td height="30" bgcolor="888888" ></td>
                          <td height="30" bgcolor="888888" class="tableHeader"> 
                  			<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
                    		  <tr> 
                      			<td class="main">
								  <select name="rate" id="rateID">
                          			<option value="day">Peak Daily Usage</option>
                          			<option value="hour">Peak Hour Usage</option>
                          			<option value="interval">Peak Interval</option>
                        		  </select>
                      			</td>
                      			<td class="columnHeader">Start Date: 
                        		  <input id="PRcal" type="text" name="PRlpDate" value="<%= datePart.format(YC_BEAN.getLPDate()) %>" size="8">
									<a href="javascript:openCalendar(document.getElementById('PRcal'))"
									onMouseOver="window.status='Date Calendar';return true;"
									onMouseOut="window.status='';return true;"> <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a>
                      			</td>
                      			<td class="columnHeader">Days:
                        		  <input type="text" size="1" id="numDaysID" name="numDays" value="5">
                      			</td>
					  			<td class="main" valign="bottom" align="right"> 
								<a href="javascript:submitCommand('PeakReport',<%= channel_1ID %>, 1);" style="font-weight:bold;height:20px" class="Link3" id="PRChannel1ID"
										onMouseOver="window.status='Read (from the meter) the selected Profile Peak Report for Channel 1';return true;"
										onMouseOut="window.status='';return true;"><%= channel_1Str%></a><br>
								</td>
                    		  </tr>
                  			</table>
                		  </td>
                		  <td height="30" bgcolor="888888" ></td>
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
							  <tr colspan="3"> 
							    <td class="columnHeader">Profile Peak Report - <%= channel_1Str %></td>
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
							    <td class="main" <%=(bg?"bgcolor='EEEEEE'":"")%>> <%=tempStr.substring(beginIndex, endIndex)%> 
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
							  <tr> 
							    <td class="columnHeader">Profile Peak Report - <%= channel_4Str %></td>
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
							    <td class="main" <%=(bg?"bgcolor='EEEEEE'":"")%>> <%=tempStr.substring(beginIndex, endIndex)%> 
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
						  <td align="right"><span class="NavText">
							<a href="javascript:document.commandForm.action.value='SavePeakReport';document.commandForm.submit();" class="Link1" id="PRChannel4ID"
								onMouseOver="window.status='Save Displayed Peak Reports to File';return true;"
										onMouseOut="window.status='';return true;">Save to File</a></span>
						  </td>
						</tr>
					  </table>					  								  
					  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						<tr> 
						  <td><br><br>
						  </td>
						</tr>
					  </table>					  								  
					  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						<tr> 
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
						  <td height="19" bgcolor="888888" class="tableHeader">
							<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						      <tr>
							    <td class="tableHeader"">Profile Data Collection</td>
							  </tr>
							</table>
						  </td>
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>		  
						</tr>
						<tr> 
                          <td height="30" bgcolor="888888" ></td>
                          <td height="30" bgcolor="888888" class="tableHeader"> 
							<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
							  <tr>
							    <td class="columnHeader">Date: 
							      <input id="DCcal" type="text" name="DClpDate" value="<%= datePart.format(YC_BEAN.getLPDate()) %>" size="8">
									<a href="javascript:openCalendar(document.getElementById('DCcal'))"
									onMouseOver="window.status='Date Calendar';return true;"
									onMouseOut="window.status='';return true;"> <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a>
								</td>
								<td class="columnHeader" align="center">Hour
								  <select name="hour" id="hourID">
								  <%
								for (int i = 0; i < 24; i++) {
									String iStr = String.valueOf(i);
									if( i < 10)	{
										iStr = "0" + iStr;
									}%>
											<option value="<%=i%>"><%=iStr%></option>
								<%}%>
								  </select>
								</td>
								<td class="columnHeader" align="center">Min
								  <select name="minute" id="minuteID">
								  <%
								for (int i = 0; i < 60; i=i+5) {
									String iStr = String.valueOf(i);
									if( i < 10)	{
										iStr = "0" + iStr;
									}%>
											<option value="<%=i%>"><%=iStr%></option>
								<%}%>			  
								  </select>
								</td>
								<td class="main" valign="bottom" align="right"> 
								  <a href="javascript:submitCommand('DataCollection', <%=channel_1ID %>, 1);" style="font-weight:bold;height:20px" class="Link3" id="DCChannel1ID"
  										onMouseOver="window.status='Read (from the meter) and Archive (to the database) the Profile data for Channel 1';return true;"
										onMouseOut="window.status='';return true;"><%= channel_1Str %></a><br>
							      <a href="javascript:submitCommand('DataCollection', <%= channel_4ID %>, 4);" style="font-weight:bold;height:20px" class="Link3" id="DCChannel4ID"
  										onMouseOver="window.status='Read (from the meter) and Archive (to the database) the Profile data for Channel 4';return true;"
										onMouseOut="window.status='';return true;"><%= channel_4Str %></a>
								</td>			
							  </tr>
							</table>
                		  </td>
                		  <td height="30" bgcolor="888888" ></td>
              			</tr>						
						<tr>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
						  <td>
							<table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
							  <tr colspan="3"> 
							    <td class="columnHeader">Recent Profile Data Collection - <%= channel_1Str%></td>
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
							  <tr> 
							    <td class="columnHeader">Recent Profile Data Collection - <%= channel_4Str%></td>
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
						  <td width="6" height="9"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
						  <td background="<%=request.getContextPath()%>/WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
						  <td width="6" height="9"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
					    </tr>						
					  </table>
					  
					  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						<tr> 
						  <td><br><br>
						  </td>
						</tr>
					  </table>					  								  
					  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						<tr> 
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
						  <td height="19" bgcolor="888888" class="tableHeader">
							<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						      <tr>
							    <td class="tableHeader"">Profile Archived Data Display</td>
							  </tr>
							</table>
						  </td>
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>		  
						</tr>
						<tr> 
                          <td height="30" bgcolor="888888" ></td>
                          <td height="30" bgcolor="888888" class="tableHeader"> 
							<table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
							  <tr>
								<td class="columnHeader" align="right">Date 
							      <input id="RPHcal" type="text" name="RPHlpDate" value="<%= datePart.format(YC_BEAN.getLPDate()) %>" size="8">
									<a href="javascript:openCalendar(document.getElementById('RPHcal'))"
									onMouseOver="window.status='Date Calendar';return true;"
									onMouseOut="window.status='';return true;"> <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a>
								</td>
								<td width="40%" class="main" valign="bottom" align="right"> 
								  <a href="javascript:submitCommand('LoadRPHData', <%= channel_1ID%>, -1);" style="font-weight:bold;height:20px" class="Link3" id="LoadRPH1ID"
  										onMouseOver="window.status='Retrieve (from the database) the archived data values for Channel 1';return true;"
										onMouseOut="window.status='';return true;"><%= channel_1Str %></a><br>
							      <a href="javascript:submitCommand('LoadRPHData', <%= channel_4ID %>, -1);" style="font-weight:bold;height:20px" class="Link3" id="LoadRPH4ID"
  										onMouseOver="window.status='Retrieve (from the database) the archived data values for Channel 4';return true;"
										onMouseOut="window.status='';return true;"><%= channel_4Str %></a>
								</td>			
							  </tr>
							</table>
                		  </td>
                		  <td height="30" bgcolor="888888" ></td>
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
										  <tr>
										    <td colspan="4" class="columnHeader" align="center"><%=liteYukonPao.getPaoName()%> - <%=(YC_BEAN.getPointID() == PointTypes.SYS_PID_SYSTEM?"Undefined Point":com.cannontech.database.cache.functions.PointFuncs.getPointName(YC_BEAN.getPointID()))%>
						        			</td>										
										  </tr>
								  		  <tr> 
											<td width="25%" class="columnHeader" align="center" bgcolor="EEEEEE">Timestamp</td>
											<td width="25%" class="columnHeader" align="center" bgcolor="EEEEEE">Value</td>
											<td width="25%" class="columnHeader" align="center" bgcolor="EEEEEE">Timestamp</td>
											<td width="25%" class="columnHeader" align="center" bgcolor="EEEEEE">Value</td>
								  		  </tr>
										    <%java.util.Vector lrphVector = YC_BEAN.getCurrentRPHVector();
											if( lrphVector != null && !lrphVector.isEmpty()){
												int half = lrphVector.size()/2;
												int vecSize = lrphVector.size();
												int doneSize = half + (lrphVector.size()%2);
												
												LiteRawPointHistory lrph;
												lrph = (LiteRawPointHistory)lrphVector.get(0);	//use the first entry to load the decimal format
												java.text.DecimalFormat df = new java.text.DecimalFormat("#0.000");
												LitePointUnit lpu = PointFuncs.getPointUnit(lrph.getPointID());
												if( lpu != null){
												  df.setMaximumFractionDigits(lpu.getDecimalPlaces());
												  df.setMinimumFractionDigits(lpu.getDecimalPlaces());
												}
												
												for( int i = 0; i < doneSize; i++)
												{
												  lrph = (LiteRawPointHistory)lrphVector.get(i);
												  %>
												  <tr <%=!(i%2==0)? "bgcolor='EEEEEE'":""%>> 
													<td width="25%" align="center" class="main"><%=dateTimeFormat.format(new java.util.Date(lrph.getTimeStamp()))%> 
													</td>
													<td width="25%" align="center" class="main"><%=df.format(lrph.getValue())%> 
													</td>
													<%
												  if( half > 0 ){
												    lrph = (LiteRawPointHistory)lrphVector.get(vecSize - half);
												    half--;%>
													<td width="25%" align="center" class="main"><%=dateTimeFormat.format(new java.util.Date(lrph.getTimeStamp()))%> 
													</td>
													<td width="25%" align="center" class="main"><%=df.format(lrph.getValue())%> 
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
	                  <table height="20" width="95%" align="center" border="0" cellspacing="0" cellpadding="0" class="TableCell">
	              	    <tr><td height="10"></td></tr>
              			<tr> 
   			  			  <td align="center" rowspan="2"><input type="reset" name="refresh" value="Refresh" onClick="window.location.reload()"></td>
						<% String gotoLink = "";
					  	if (request.getParameter("InvNo") != null){	//we came from the Customer Account page
				          gotoLink =  request.getContextPath()+"/operator/Consumer/CommandInv.jsp?InvNo="+invNo+"&manual&command=null";
				        } else {
				          gotoLink = request.getContextPath()+"/apps/CommandDevice.jsp?deviceID="+deviceID+"&manual&command=null";
				        }%>
		   			  	  <td align="center"><a href='<%=gotoLink%>' name="manualCommander"  class="Link1">Commander: Manual</a></td>
					    </tr>	
		                <tr>
					  	<%if (request.getParameter("InvNo") != null){	//we came from the Customer Account page
				          gotoLink =  request.getContextPath()+"/operator/Consumer/CommandInv.jsp?InvNo="+invNo+"&command=null";
				        } else {
				          gotoLink = request.getContextPath()+"/apps/CommandDevice.jsp?deviceID="+deviceID+"&command=null";
				        }
		   				if ((lp || manual ) && isMCT410) {%>
						  <td align="center"><a href='<%= gotoLink%>' name="advCommander410"  class="Link1">Commander: MCT410Custom</a></td>
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