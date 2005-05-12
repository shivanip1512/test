<%

		java.util.Enumeration enum1 = request.getParameterNames();
		  while (enum1.hasMoreElements()) {
		  	String ele = enum1.nextElement().toString();
			 System.out.println(" --" + ele + "  " + request.getParameter(ele));
		}
		 
		 
if( request.getParameter("clearids") != null)
{
	YC_BEAN.getRequestMessageIDs().clear();
	com.cannontech.clientutils.CTILogger.info("Clearing the RequestMessageIDs, they aren't coming back!");
}
//set the deviceID of the YC_BEAN
YC_BEAN.setDeviceID(deviceID);
long maxTime = 3000;	//max sleep time will be 3 seconds
long totalTime = 0;
while( YC_BEAN.getPointRegCounter() > 0 && totalTime < maxTime )
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

function updatePrevious()
{
	var currVal = document.getElementById('currEnergyPD').innerHTML;
	document.getElementById('prevEnergyPD').innerHTML = formatNum(document.getElementById('prevSelect').value);
	
	var selected = document.getElementById('prevSelect').options[document.getElementById('prevSelect').selectedIndex].text;
	var prevVal = document.getElementById('prevEnergyPD').innerHTML;
	currVal = currVal.replace(/^\s*/,"").replace(/\s*$/,"");
	prevVal = prevVal.replace(/^\s*/,"").replace(/\s*$/,"");
	
	if( currVal == "---" || prevVal == "---")
	{
		document.getElementById('totalUsage').innerHTML = "---";
		if( prevVal == "---")
		{
			document.getElementById('prevEnergyPD').innerHTML = "---";
		}
	}
	else
	{
		document.getElementById('totalUsage').innerHTML = formatNum(currVal - prevVal);
	}
}

function formatNum(valuein)
{ 
	valuein = "" + Math.round( parseFloat(valuein) * 1000 ) / 1000 ;

	if (valuein=="NaN") valuein = "0";
	decimalpos = valuein.indexOf(".");
	if (decimalpos==-1)	//add a decimal if not exist
	{
		decimalpos = valuein.length;
		valuein = valuein + ".";
	}
	valuein = valuein + "000";	//pad with zeros if needed.
	valuein = valuein.substring(0, decimalpos+4);	//truncate the extra zeros
	return valuein;
}


function disableAllButtons()
{
	document.body.style.cursor = 'wait';
	document.getElementById('readEnergyID').style.cursor = 'wait';
	document.getElementById('readEnergyID').href="javascript:;";
	document.getElementById('readEnergyID').disabled = true;
	
	document.getElementById('readDemandID').style.cursor = 'wait';
	document.getElementById('readDemandID').href="javascript:;";
	document.getElementById('readDemandID').disabled = true;

	document.getElementById('readVoltageID').style.cursor = 'wait';
	document.getElementById('readVoltageID').href="javascript:;";
	document.getElementById('readVoltageID').disabled = true;

	document.getElementById('readOutageID').style.cursor = 'wait';
	document.getElementById('readOutageID').href="javascript:;";
	document.getElementById('readOutageID').disabled = true;
	
	if( document.getElementById('readDiscID') )
	{
		document.getElementById('controlConnID').style.cursor = 'wait';
		document.getElementById('controlConnID').href="javascript:;";
		document.getElementById('controlConnID').disabled = true;

		document.getElementById('controlDiscID').style.cursor = 'wait';
		document.getElementById('controlDiscID').href="javascript:;";
		document.getElementById('controlDiscID').disabled = true;
	
		document.getElementById('readDiscID').style.cursor = 'wait';
		document.getElementById('readDiscID').href="javascript:;";
		document.getElementById('readDiscID').disabled = true;
	}		

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
  <input id="redirect" type="hidden" name="REDIRECT" value="<%= redirect%>">
  <input id="referrer" type="hidden" name="REFERRER" value="<%= referrer%>">
  <td width="657" valign="top" bgcolor="#FFFFFF"> 
    <div align="center"> 
      <% String header = "MCT 410 - CONTROL COMMANDS"; %>
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
            <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td align="right" class="TableCell"> 
                  <input type="checkbox" name="updateDB">
                  Update Database</td>
              </tr>
            </table>
            <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td width="6" height="19"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
                <td height="95%"  bgcolor="888888" class="tableHeader">Energy</td>
                <td height="95%" bgcolor="888888" class="crumbs" style="font-weight:bold" align="right"> <a class="Link3" href="javascript:setCommand('getvalue kwh');disableAllButtons()" id="readEnergyID" name="readEnergy"
				onMouseOver="window.status='Read Energy';return true;"
                onMouseOut="window.status='';return true;">Read</a></td>
                <td width="6" height="19"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
              </tr>
              <tr> 
                <td width="6" background="<%= request.getContextPath() %>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
                <td colspan="2"> 
                  <table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
                    <tr> 
                      <td width="20%" class="columnHeader">Current</td>
                      <td width="35%"class="main" align="center"> 
                        <%pointData = YC_BEAN.getRecentPointData(deviceID, 1, PointTypes.PULSE_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      </td>
                      <td width="35%" class="main" align="right"> 
                        <div id="currEnergyPD"> 
                          <%if( pointData != null){%>
                          <%=format_nv3.format(pointData.getValue())%> 
                          <% } else {%>
                          --- 
                          <%}%>
                        </div>
                      </td>
                    </tr>
                    <tr bgcolor="EEEEEE"> 
                      <td class="columnHeader">Previous</td>
                      <td class="main" align="center"> 
                        <%
						int xID = YC_BEAN.getPointID(deviceID, 1, PointTypes.PULSE_ACCUMULATOR_POINT);
						java.util.TreeMap tempMap = YC_BEAN.getPrevMonthTSToValueMap(xID);
						java.util.Date[] keyArray = YC_BEAN.getPrevDateArray(xID);
						String value = "---";
						if( keyArray.length > 0) { 
							//save the initial value string to set in the value TD below
							value = format_nv3.format(tempMap.get(keyArray[keyArray.length-1]));
						%>
                        <select onChange="updatePrevious()"  name="prevSelect" id="prevSelect">
                          <%for (int i = keyArray.length -1; i>= 0; i--)
						{%>
                          <option value="<%=format_nv3.format(tempMap.get(keyArray[i]))%>"><%=dateTimeFormat.format(keyArray[i])%></option>
                          <%} %>
                        </select>
                        <% }else {%>
                        <select onChange="updatePrevious()"  name="prevSelect" id="prevSelect">
                          <option value="---">---</option>
                        </select>
                        <%}%>
                      </td>
                      <td class="main" align="right">
                        <div id="prevEnergyPD"><%=value%></div>
                      </td>
                    </tr>
                    <tr> 
                      <td class="columnHeader">Usage</td>
                      <td class="main">&nbsp;</td>
                      <td class="main" align="right"> 
                        <div id="totalUsage"> 
                          <script>javascript:updatePrevious()</script>
                        </div>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="6" background="<%= request.getContextPath() %>/WebConfig/yukon/Side_right.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="6" height="9"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
                <td colspan="2" background="<%= request.getContextPath() %>/WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
                <td width="6" height="9"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
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
                <td width="6" height="19"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
                <td height="95%"  bgcolor="888888" class="tableHeader">Demand</td>
                <td height="95%" bgcolor="888888" class="crumbs" style="font-weight:bold" align="right"> <a class="Link3" href="javascript:setCommand('getvalue peak & getvalue demand');disableAllButtons()" id="readDemandID" name="readDemand"
			  onMouseOver="window.status='Read Demand and Peak Demand';return true;"
              onMouseOut="window.status='';return true;">Read</a></td>
                <td width="6" height="19"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
              </tr>
              <tr> 
                <td width="6" background="<%= request.getContextPath() %>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
                <td colspan="2"> 
                  <table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
                    <tr> 
                      <td width="20%" class="columnHeader">Current</td>
                      <td width="35%"class="main" align="center"> 
                        <%pointData = YC_BEAN.getRecentPointData(deviceID, 1, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      <td width="35%" class="main" align="right"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% } else {%>
                        &nbsp; 
                        <%}%>
                      </td>
                    </tr>
                    <tr bgcolor="EEEEEE"> 
                      <td class="columnHeader">Peak</td>
                      <td class="main" align="center"> 
                        <%pointData = YC_BEAN.getRecentPointData(deviceID, 11, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      </td>
                      <td class="main" align="right"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% } else {%>
                        &nbsp; 
                        <%}%>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="6" background="<%= request.getContextPath() %>/WebConfig/yukon/Side_right.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="6" height="9"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
                <td colspan="2" background="<%= request.getContextPath() %>/WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
                <td width="6" height="9"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
              </tr>
            </table>
            <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td> <br>
                </td>
              </tr>
            </table>
            <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td width="6" height="19"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
                <td height="95%"  bgcolor="888888" class="tableHeader">Voltage</td>
                <td height="95%" bgcolor="888888" class="crumbs" style="font-weight:bold" align="right"> <a class="Link3" href="javascript:setCommand('getvalue voltage & getvalue demand');disableAllButtons()" id="readVoltageID" name="readVoltage"
			  onMouseOver="window.status='Read Current, Minimum, and Maximum Voltage';return true;"
              onMouseOut="window.status='';return true;">Read</a></td>
                <td width="6" height="19"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
              </tr>
              <tr> 
                <td width="6" background="<%= request.getContextPath() %>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
                <td colspan="2"> 
                  <table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
                    <tr> 
                      <td width="20%" class="columnHeader">Current</td>
                      <td width="35%"class="main" align="center"> 
                        <%pointData = YC_BEAN.getRecentPointData(deviceID, 4, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      <td width="35%" class="main" align="right"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% } else {%>
                        &nbsp; 
                        <%}%>
                      </td>
                    </tr>
                    <tr bgcolor="EEEEEE"> 
                      <td class="columnHeader">Minimum</td>
                      <td class="main" align="center"> 
                        <%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, 15, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      </td>
                      <td class="main" align="right"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% } else {%>
                        &nbsp; 
                        <%}%>
                      </td>
                    </tr>
                    <tr> 
                      <td width="20%" class="columnHeader">Maximum</td>
                      <td width="35%"class="main" align="center"> 
                        <%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, 14, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      <td width="35%" class="main" align="right"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% } else {%>
                        &nbsp; 
                        <%}%>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="6" background="<%= request.getContextPath() %>/WebConfig/yukon/Side_right.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="6" height="9"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
                <td colspan="2" background="<%= request.getContextPath() %>/WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
                <td width="6" height="9"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
              </tr>
            </table>
            <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td> <br>
                </td>
              </tr>
            </table>
            <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td width="6" height="19"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
                <td height="95%"  bgcolor="888888" class="tableHeader">Outage</td>
                <td height="95%" bgcolor="888888" class="crumbs" style="font-weight:bold" align="right"> <a class="Link3" href="javascript:setCommand('getvalue powerfail & getvalue outage 1 & getvalue outage 3 & getvalue outage 5');disableAllButtons()" id="readOutageID" name="readOutage"
			  onMouseOver="window.status='Read Current Blink Count, Last 6 Outages';return true;"
              onMouseOut="window.status='';return true;">Read</a></td>
                <td width="6" height="19"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
              </tr>
              <tr> 
                <td width="6" background="<%= request.getContextPath() %>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
                <td colspan="2"> 
                  <table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
                    <tr> 
                      <td width="20%" class="columnHeader">Blink Count</td>
                      <td width="35%"class="main" align="center"> 
                        <%pointData = YC_BEAN.getRecentPointData(deviceID, 20, PointTypes.PULSE_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      <td width="35%" class="main" align="right"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% } else {%>
                        &nbsp; 
                        <%}%>
                      </td>
                    </tr>
                    <tr bgcolor="EEEEEE"> 
                      <td class="columnHeader">&nbsp;</td>
                      <td colspan="2" class="main">&nbsp; 
					</tr>
                    <tr> 
                      <td class="columnHeader">History (Last 6)</td>
                      <td colspan="2" class="main">1.&nbsp;&nbsp; 
                        <%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_1);%>
                        <%if( pointData != null){%>
                        <%=pointData.getStr()%> 
                        <%} else {%>
                        ---
                        <%}%>
                      </td>
                    </tr>
                    <tr bgcolor="EEEEEE"> 
                      <td class="columnHeader">&nbsp;</td>
                      <td colspan="2" class="main">2.&nbsp;&nbsp; 
                        <%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_2);%>
                        <%if( pointData != null){%>
                        <%=pointData.getStr()%> 
                        <%} else {%>
                        ---
                        <%}%>
                      </td>
                    </tr>
                    <tr> 
                      <td class="columnHeader">&nbsp;</td>
                      <td colspan="2" class="main">3.&nbsp;&nbsp; 
                        <%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_3);%>
                        <%if( pointData != null){%>
                        <%=pointData.getStr()%> 
                        <%} else {%>
                        ---
                        <%}%>
                      </td>
                    </tr>
                    <tr bgcolor="EEEEEE"> 
                      <td class="columnHeader">&nbsp;</td>
                      <td colspan="2" class="main">4.&nbsp;&nbsp; 
                        <%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_4);%>
                        <%if( pointData != null){%>
                        <%=pointData.getStr()%> 
                        <%} else{%>
                        ---
                        <%}%>
                      </td>
                    </tr>
                    <tr> 
                      <td class="columnHeader">&nbsp;</td>
                      <td colspan="2" class="main">5.&nbsp;&nbsp; 
                        <%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_5);%>
                        <%if( pointData != null){%>
                        <%=pointData.getStr()%> 
                        <%} else {%>
                        ---
                        <%}%>
                      </td>
                    </tr>
                    <tr bgcolor="EEEEEE"> 
                      <td class="columnHeader">&nbsp;</td>
                      <td colspan="2" class="main">6.&nbsp;&nbsp; 
                        <%pointData = (PointData)YC_BEAN.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_6);%>
                        <%if( pointData != null){%>
                        <%=pointData.getStr()%> 
                        <%} else {%>
                        ---
                        <%}%>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="6" background="<%= request.getContextPath() %>/WebConfig/yukon/Side_right.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="6" height="9"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
                <td colspan="2" background="<%= request.getContextPath() %>/WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
                <td width="6" height="9"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
              </tr>
            </table>
			
			
            <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td> <br>
                </td>
              </tr>
            </table>
			<% int discAddress = YC_BEAN.getMCT410DisconnectAddress(deviceID); 
			if( discAddress >= 0 ) {
			%>
            <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td width="6" height="19"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
                <td height="95%"  bgcolor="888888" class="tableHeader">Disconnect</td>
                <td height="95%" bgcolor="888888" class="crumbs" align="right" style="font-weight:bold">
				<a class="Link3" href="javascript:setCommand('control connect');disableAllButtons()" id="controlConnID" name="controlConn"
				  onMouseOver="window.status='Control Connect';return true;"
    	          onMouseOut="window.status='';return true;">Connect</a>
				&nbsp;&nbsp;
                <a class="Link3" href="javascript:setCommand('control disconnect');disableAllButtons()" id="controlDiscID" name="controlDisc"
				  onMouseOver="window.status='Control Disconnect';return true;"
	              onMouseOut="window.status='';return true;">Disconnect</a>
				&nbsp;&nbsp;
                <a class="Link3" href="javascript:setCommand('getstatus disconnect');disableAllButtons()" id="readDiscID" name="readDisc"
				  onMouseOver="window.status='Read Disconnect Status';return true;"
        	      onMouseOut="window.status='';return true;">Read</a></td>
                <td width="6" height="19"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
              </tr>
              <tr> 
                <td width="6" background="<%= request.getContextPath() %>/WebConfig/yukon/Side_left.gif">&nbsp;</td>
                <td colspan="2"> 
                  <table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
                    <tr> 
                      <td width="20%" class="columnHeader">Status</td>
                      <td width="35%"class="main" align="center"> 
                        <%pointData = YC_BEAN.getRecentPointData(deviceID, 1, PointTypes.STATUS_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      <td width="35%" class="main" align="right"> 
                        <%if( pointData != null){%>
                        <%=pointData.getStr()%> 						
                        <%//format_nv3.format(pointData.getValue())%> 
                        <% } else {%>
                        &nbsp; 
                        <%}%>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="6" background="<%= request.getContextPath() %>/WebConfig/yukon/Side_right.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="6" height="9"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
                <td colspan="2" background="<%= request.getContextPath() %>/WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
                <td width="6" height="9"><img src="<%= request.getContextPath() %>/WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
              </tr>
            </table>
			<%}%>			
            <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" class="TableCell">
              <tr> 
              	<% if (request.getParameter("InvNo") != null)	//we came from the Customer Account page
              	{%>
	                <td align="right"><a href='<%= request.getContextPath() %>/operator/Consumer/CommandInv.jsp?InvNo=<%=invNo%>&manual&command=null' name="manualCommander"  class="Link1">Go To Manual Commander</a></td>
              	<%}
              	else {%>
	                <td align="right"><a href='<%= request.getContextPath() %>/apps/CommandDevice.jsp?deviceID=<%=deviceID%>&manual&command=null' name="manualCommander"  class="Link1">Go To Manual Commander</a></td>
               <%}%>
              </tr>
              <tr> 
              	<% if (request.getParameter("InvNo") != null)	//we came from the Customer Account page
              	{%>
	                <td align="right"><a href='<%= request.getContextPath() %>/operator/Consumer/CommandInv.jsp?InvNo=<%=invNo%>&lp' name="advCommander410"  class="Link1">Go To Load Profile Options</a></td>
              	<%}
              	else {%>
	                <td align="right"><a href='<%= request.getContextPath() %>/apps/CommandDevice.jsp?deviceID=<%=deviceID%>&lp' name="advCommander410"  class="Link1">Go To Load Profile Options</a></td>
               <%}%>
             </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
      </table>
    </div>
    <a href="<%=referrer%>&clearids" class="Link3">.</a> </td>
</form>
