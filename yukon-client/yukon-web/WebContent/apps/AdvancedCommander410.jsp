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
function setCommand(cmd)
{
	var offset;
	var offsetGroup = commandForm.type;
	for (var i = 0; i < offsetGroup.length; i++)
	{
		if( offsetGroup[i].checked)
		{
			cmd = cmd + offsetGroup[i].value;
		}
	}
	cmd = cmd + " " + document.commandForm.startDate.value;
	cmd = cmd + " " + document.commandForm.startHour.value;
	cmd = cmd + ":" + document.commandForm.startMin.value;
	document.commandForm.command.value = cmd;
}

function disableAllButtons()
{
	document.body.style.cursor = 'wait';
	document.getElementById('readLPID').disabled = true;
	document.commandForm.submit();
}

</SCRIPT>
          <form name="commandForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet">
            <input type="hidden" name="deviceID" value="<%=deviceID%>">
            <input type="hidden" name="pointID" value="<%=YC_BEAN.getPointID()%>">
            <input type="hidden" name="command" value="">
            <input type="hidden" name="timeOut" value="8000">
			<input type="hidden" name="updateDB" value="true">
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
						  <td height="19" bgcolor="888888" class="tableHeader" align="center">Please select a time period to review</td>
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
						</tr>
						<tr>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
						  <td>
							<table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
							  <tr> 
								<td class="main" width="47%" valign="bottom" align="right">Start Date:
									  <input id="startCal" type="text" name="startDate" value="<%= datePart.format(YC_BEAN.getLPStartDate()) %>" size="8">
									  <a href="javascript:openCalendar(document.getElementById('commandForm').startCal)"
									  onMouseOver="window.status='Start Date Calendar';return true;"
									  onMouseOut="window.status='';return true;"> <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a>
								</td>			  
								<td class="main" width="20%" valign="bottom" align="right">HR
									<select name="startHour" id="startHourID">
									  <%for (int i = 0; i < 24; i++)
									  {%>
										<option value="<%=format_2char.format(i)%>"><%=format_2char.format(i)%></option>
									<%} %>
									</select>								  
								</td>
								<td class="main" width="1%" valign="bottom" align="right" height="29">&nbsp;</td>								  
								<td class="main" width="20%" valign="bottom" align="left">MIN
									<select name="startMin" id="startMinID">
									  <%for (int i = 0; i < 12; i++)
									  {%>
										<option value="<%=format_2char.format(i*5)%>"><%=format_2char.format(i*5)%></option>
									<%} %>
									</select>
								</td>
							  </tr>
							  <tr> 
								<td class="main">&nbsp;</td>
							  </tr>
							  <tr> 
								<td class="main">&nbsp;</td>
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
						  <td height="19" bgcolor="888888" class="tableHeader" align="center">Please select a Point to collect Load Profile On</td>
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>		  
						</tr>
						<tr>
						  <td width="6" background="<%=request.getContextPath()%>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
						  <td>
							<table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
							  <tr> 
								<td> 
								  <table class="TableCell" align="center" width="33%">
							<%
								com.cannontech.database.data.lite.LitePoint [] litePoints = 
									com.cannontech.database.cache.functions.PAOFuncs.getLitePointsForPAObject(deviceID);
								if( litePoints != null)
								{
									boolean first = true;
									for (int i = 0; i < litePoints.length; i++)
									{
										if( litePoints[i].getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND)
										{%>
										<tr> 
										  <td class="main" width="20%"> 
											<input id = "type" type="radio" name="type" value="1" <%=(YC_BEAN.getPointID()==litePoints[i].getPointID())? "checked":""%> onClick="document.commandForm.pointID.value=<%=litePoints[i].getPointID()%>;">
											<%=litePoints[i].getPointName()%> </td>
										</tr>
										<%}
										else if( litePoints[i].getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND )
										{%>
										<tr> 
										  <td width="20%"> 
											<input id = "type" type="radio" name="type" value="4" <%=(YC_BEAN.getPointID()==litePoints[i].getPointID())? "checked":""%> onClick="document.commandForm.pointID.value=<%=litePoints[i].getPointID()%>;">
											<%=litePoints[i].getPointName()%> </td>
										</tr>
										<%}
									}
								}%>
								  </table>
								</td>
								<td class="main" width="30%" align="center" height="35" valign="middle">Pressing 'GO' will collect and archive 
                                  (up to) 12 intervals, inclusive of the hour and minute selected 
                                  <input type="image" onClick="setCommand('getvalue lp channel ');disableAllButtons();" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButton.gif" id="readLPID" name="readLP" align="middle">
								</td>
							  </tr>
							  <tr>
								<td colspan="2" height="19" class="main" align="center" bgcolor="EEEEEE">
									<a href="javascript:disableAllButtons();" style="font-weight:bold" class="Link4">Refresh</a>&nbsp;Tabular Data View (using above settings)</td></tr>
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
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_left.gif" width="6" height="50"></td>
						  <td height="19" bgcolor="888888" class="tableHeader" align="center">Data currently stored in Database:<BR><%=liteYukonPao.getPaoName()%> - <%=com.cannontech.database.cache.functions.PointFuncs.getPointName(YC_BEAN.getPointID())%>
							&nbsp;[<%=datePart.format(YC_BEAN.getLPStartDate())%>]<BR><a href="javascript:disableAllButtons();" class="Link3">Refresh</a>&nbsp;Tabular Data View (using above settings)
						  </td>
						  <td width="6" height="19"><img src="<%=request.getContextPath()%>/WebConfig/yukon/Header_right.gif" width="6" height="50"></td>		  
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
											<td width="25%" class="columnHeader" align="center">Timestamp</td>
											<td width="25%"class="columnHeader" align="center">Value</td>
											<td width="25%" class="columnHeader" align="center">Timestamp</td>
											<td width="25%"class="columnHeader" align="center">Value</td>
								  		  </tr>
										    <%java.util.Vector lrphVector = YC_BEAN.getRPHData(YC_BEAN.getPointID());
											if( lrphVector != null){
												int half = lrphVector.size()/2;
												int vecSize = lrphVector.size();
												int doneSize = half + (lrphVector.size()%2);
												
												LiteRawPointHistory lrph;
												for( int i = 0; i < doneSize; i++)
												{
												  lrph = (LiteRawPointHistory)lrphVector.get(i);
												  %>
												  <tr <%=(i%2==1)? "bgcolor='EEEEEE'":""%>> 
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
<%--					<tr align="center"> 
					  <td class="SubtitleHeader"  valign="top">Command Execution Log</td>
					</tr>
					<tr> 
					  <td > 
						<div align="center"> 
						  <textarea id="resultText" name="resultText" class="TableCell" readonly="readonly" cols="100" rows="10" wrap="VIRTUAL"><%= YC_BEAN.getResultText()%></textarea>
						  <input type="submit" name="clearText" id="clearTextID"  value="Clear Results">
						  <input type="reset" name="refresh" id="refreshID" value="Refresh" onClick="window.location.reload()">
						</div>
					  </td>
					</tr>
--%>					
                </table>
                <br>
              </div>
          </form>