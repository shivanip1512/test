<%@ page import="com.cannontech.message.dispatch.message.PointData"%> 
<%
if( request.getParameter("clearids") != null)
{
	ycBean.getRequestMessageIDs().clear();
	com.cannontech.clientutils.CTILogger.info("Clearing the RequestMessageIDs, they aren't coming back!");
}
//set the deviceID of the YCBean
ycBean.setDeviceID(deviceID);
long maxTime = 3000;	//max sleep time will be 3 seconds
long totalTime = 0;
while( ycBean.getPointRegCounter() > 0 && totalTime < maxTime )
{
	try
	{
		//Wait for all pointRegistration messages to be heard back from
		Thread.sleep(100);
		totalTime+=100;
		com.cannontech.clientutils.CTILogger.info("Waiting for pointRegistions to come back!");
	}
	catch (InterruptedException e)
	{
		e.printStackTrace();
	}
}

PointData pointData = null;
%>
<%
String[] defaultKeys = {
	"Read Energy", 
	"Read Energy (update)"
};
String[] defaultValues = {
	"getvalue kwh", 
	"getvalue kwh update"
};
//comands for disconnect meters
String[] disconnectKeys = {
	"Read Energy", 
	"Read Energy (update)", 
	"Read Disconnect Status", 
	"Disconnect Meter", 
	"Connect Meter"
};
String[] disconnectValues = {
	"getvalue kwh", 
	"getvalue kwh update", 
	"getstatus disconnect", 
	"control disconnect", 
	"control connect"
};
%>

<SCRIPT language="JavaScript">
function disableButton(x)
{
	x.disabled = true;
	document.commandForm.submit();
}

function disableAllButtons()
{
	document.commandForm.ReadkWh.disabled = true;
	document.commandForm.ReadDemand.disabled = true;
	document.commandForm.ReadPeak.disabled = true;
	document.commandForm.ReadVoltage.disabled = true;
	document.commandForm.ReadPowerfail.disabled = true;
	document.commandForm.ReadOutage1_2.disabled = true;
	document.commandForm.ReadOutage3_4.disabled = true;
	document.commandForm.ReadOutage5_6.disabled = true;
	document.commandForm.submit();
}

function setCommand(cmd)
{
	document.commandForm.command.value = cmd;
}
</SCRIPT>
          <form name="commandForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet">
          <input type="hidden" name="deviceID" value="<%=deviceID%>">
          <input type="hidden" name="command" value="">
          <input type="hidden" name="timeOut" value="8000">
          <input id="redirect" type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
          <input id="referrer" type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "MCT 410 - CONTROL COMMANDS"; %>
              <br>
              <table width="100%" border="0" cellspacing="0" cellpadding="3">
                <tr> 
                  <td align="center" class="TitleHeader"><%= header %></td>
                </tr>
              </table>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <table width="530" border="0" cellspacing="0" cellpadding="3">
                <tr> 
                  <td width="100%" class="SubtitleHeader" align="center"> Meter Name:&nbsp;<u><%=liteYukonPao.getPaoName()%></u>
                  </td>
                </tr>
			  </table>
                <table width="530" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td class="SubtitleHeader" width="50%">METER DATA</td>
					<td class="TableCell" align="right" width="50%">
					  <input type="checkbox" name="updateDB">Write To Database
					</td>
				  </tr>
				</table>
                <table width="530" border="1" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td>
                      <table border="0" cellspacing="5" cellpadding="0" width="100%" height="40" class="TableCell">
                        <tr>
                        	<%pointData = (PointData)ycBean.getRPHPointData(deviceID, 1, PointTypes.PULSE_ACCUMULATOR_POINT);%>
                          <td width="40%">Last Read:
						    <br><input type="text" name="frozenKwh" size="15%" value="<%if(pointData == null)%>N/A<%else%><%=format_nv3.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>	
                        	<%pointData = ycBean.getRecentPointData(deviceID, 1, PointTypes.PULSE_ACCUMULATOR_POINT);%>
                          <td width="40%">Current Read:
						    <br><input type="text" name="currentKwh" size="15%" value="<%if(pointData == null)%>N/A<%else%><%=format_nv3.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>
                          <td align="center" width="19%"><u>Read Meter</u>
							<br><input type="submit" name="ReadkWh" value="kWh" onClick="setCommand('getvalue kwh');disableAllButtons()">
                          </td>
						</tr>
                        <tr>
                        	<%pointData = (PointData)ycBean.getRPHPointData(deviceID, 1, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                          <td width="40%">Last Demand:
						    <br><input type="text" name="frozenDemand" size="15%" value="<%if(pointData == null)%>N/A<%else %><%=format_nv3.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>	
                        	<%pointData = ycBean.getRecentPointData(deviceID, 1, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                          <td width="40%">Current Demand:
	  						<br><input type="text" name="currentDemand" size="15%" value="<%if(pointData == null)%>N/A<%else %><%=format_nv3.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>
                          <td align="center" width="19%">
                            <br><input type="submit" name="ReadDemand" value="Demand" onClick="setCommand('getvalue demand');disableAllButtons()">
                          </td>
						</tr>
                        <tr>
                        	<%pointData = (PointData)ycBean.getRPHPointData(deviceID, 11, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                          <td width="40%">Last Peak: <br>
                            <input type="text" name="frozenPeak" size="15%" value="<%if(pointData == null)%>N/A<%else %><%=format_nv3.format(pointData.getValue())%>">
							&nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>	
                        	<%pointData = ycBean.getRecentPointData(deviceID, 11, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                          <td width="40%">Current Peak:
						    <br><input type="text" name="currentPeak" size="15%" value="<%if(pointData == null)%>N/A<%else %><%=format_nv3.format(pointData.getValue())%>">
							&nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>
                          <td align="center" width="19%">
                            <br><input type="submit" name="ReadPeak" value="Peak" onClick="setCommand('getvalue peak');disableAllButtons()">
                          </td>
						</tr>
					  </table>
					  <hr width="95%">
					  <table border="0" cellspacing="5" cellpadding="0" width="100%" height="40" class="TableCell">
                        <tr>
                        	<%pointData = (PointData)ycBean.getRPHPointData(deviceID, 4, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                          <td width="40%">Last Voltage:
						    <br><input type="text" name="frozenVoltage" size="15%" value="<%if(pointData == null)%>N/A<%else %><%=format_nv3.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>	
                        	<%pointData = ycBean.getRecentPointData(deviceID, 4, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                          <td width="40%">Current Voltage:
						    <br><input type="text" name="currentVoltage" size="15%" value="<%if(pointData == null)%>N/A<%else %><%=format_nv3.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>
                          <td align="center" width="19%">
                            <br><input type="submit" name="ReadVoltage" value="Voltage" onClick="setCommand('getvalue voltage');disableAllButtons()">
                          </td>
						</tr>
                        <tr>
                        	<%pointData = (PointData)ycBean.getRecentPointData(deviceID, 15, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                          <td width="40%">Min Voltage:
						    <br><input type="text" name="minVoltage" size="15%" value="<%if(pointData == null)%>N/A<%else%><%=format_nv3.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>	
                        	<%pointData = (PointData)ycBean.getRecentPointData(deviceID, 14, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                          <td width="40%">Max Voltage:
						    <br><input type="text" name="maxVoltage" size="15%" value="<%if(pointData == null)%>N/A<%else%><%=format_nv3.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>
                          <td align="center" width="19%"><br>
                          </td>
						</tr>
					  </table>
                    </td>
                  </tr>
                </table>
				<br>
				<table width="530" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
		            <td class="SubtitleHeader">OUTAGE
				    </td>
				  </tr>
				</table>
				<table width="530" border="1" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td>
                      <table border="0" cellspacing="5" cellpadding="0" width="100%" height="40" class="TableCell">
                        <tr>
                        	<%pointData = ycBean.getRecentPointData(deviceID, 20, PointTypes.PULSE_ACCUMULATOR_POINT);%>
                          <td width="80%" height="100%">Blink Count:
						    <input type="text" name="currentKwh" size="5%" value="<%if(pointData == null)%>N/A<%else%><%=pointData.getValue()%>">&nbsp;&nbsp;
                            <input type="submit" name="ReadPowerfail" value="Get Outage" onClick="setCommand('getvalue powerfail');disableAllButtons()">
                          </td>
						</tr>
					  </table>
					  <hr width="95%" >
					  <table border="0" cellspacing="5" cellpadding="0" width="100%" height="40" class="TableCell">
                        <tr>
                        	<%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_1);%>
                          <td width="40%">Outage 1:
						    <br><input type="text" name="outage1" size="15%" value="<%if(pointData == null)%>N/A<%else%><%=format_nsec.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>	
                        	<%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_2);%>
                          <td width="40%">Outage 2:
						    <br><input type="text" name="outage2" size="15%" value="<%if(pointData == null)%>N/A<%else%><%=format_nsec.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>
                          <td valign="middle" align="center" width="19%" height="100%"><u>Read Outage</u>
                            <br><input type="submit" name="ReadOutage1_2" value="1 - 2" onClick="setCommand('getvalue outage 1');disableAllButtons()">
                          </td>
						</tr>
                        <tr>
                        	<%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_3);%>
                          <td width="40%">Outage 3:
						    <br><input type="text" name="outage3" size="15%" value="<%if(pointData == null)%>N/A<%else%><%=format_nsec.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>	
                        	<%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_4);%>
                          <td width="40%">Outage 4:
						    <br><input type="text" name="outage4" size="15%" value="<%if(pointData == null)%>N/A<%else%><%=format_nsec.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>
                          <td valign="middle" align="center" width="19%" height="100%"> 
                            <br><input type="submit" name="ReadOutage3_4" value="3 - 4" onClick="setCommand('getvalue outage 3');disableAllButtons()">
                          </td>
						</tr>
                        <tr>
                        	<%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_5);%>
                          <td width="40%">Outage 5:
						    <br><input type="text" name="outage1" size="15%" value="<%if(pointData == null)%>N/A<%else%><%=format_nsec.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>	
                        	<%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_6);%>
                          <td width="40%">Outage 6:
						    <br><input type="text" name="outage2" size="15%" value="<%if(pointData == null)%>N/A<%else if(pointData.getValue()< 0)%>undefined<%else%><%=format_nsec.format(pointData.getValue())%>">
						    &nbsp;<%if( pointData != null){%>(<%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%>)<%}%>
						  </td>
                          <td valign="middle" align="center" width="19%" height="100%"> 
                            <br><input type="submit" name="ReadOutage5_6" value="5 - 6" onClick="setCommand('getvalue outage 5');disableAllButtons()">
                          </td>
						</tr>
                      </table>
                    </td>
                  </tr>
                </table>				
              <table width="530" border="0" cellspacing="0" cellpadding="3">
<%--                  <tr> 
                    <td width="30%" class="SubtitleHeader" align="right">Execute Command :</td>
                    <td width="70%"> 
                      <select name="command">
                        <%
                      String tempCommand = ycBean.getCommandString().replaceAll("noqueue", "").trim();
                      com.cannontech.common.util.KeysAndValues keysAndVals = 
						new com.cannontech.common.util.KeysAndValues(defaultKeys, defaultValues);
                      if( DeviceTypesFuncs.isDisconnectMCT(liteYukonPao.getType()))
						keysAndVals = new com.cannontech.common.util.KeysAndValues(disconnectKeys, disconnectValues);
                      
					  for (int i = 0; i < keysAndVals.getKeys().length; i++)
                  	  {
                  	    out.print("<OPTION value='" + keysAndVals.getValues()[i] + "' ");
                  	  	if( keysAndVals.getValues()[i].equalsIgnoreCase(tempCommand))
                  	  		out.print("SELECTED");
               	  		out.println(">" + keysAndVals.getKeys()[i] + "</option>");
                 	  }%>
                      </select>
                      <input type="submit" name="execute" value="Execute" onClick="disableButton(this)">
                    </td>
                  </tr>
--%>
                  <tr align="center"> 
                    <td class="SubtitleHeader" colspan="2" valign="top">Command Execution Log</td>
                  </tr>
                  <tr>
                    <td colspan="2"> 
                      <div align="center"> 
                        <textarea id="resultText" name="resultText" class="TableCell" readonly="readonly" cols="100" rows="10" wrap="VIRTUAL"><%= ycBean.getResultText()%></textarea>
                        <input type="submit" name="clearText" value="Clear Results">
                        <input type="reset" name="refresh" value="Refresh" onClick="window.location.reload()">
                      </div>
                    </td>
                  </tr>
                  <br>
              </table>
              <br>
            </div>
			<a href="<%= request.getRequestURI()%>?InvNo=<%=invNo %>&clearids" class="Link3">.</a>
          </td>
          </form>