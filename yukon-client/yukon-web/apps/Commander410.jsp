<%@ page import="com.cannontech.message.dispatch.message.PointData"%> 
<%

		java.util.Enumeration enum1 = request.getParameterNames();
		  while (enum1.hasMoreElements()) {
		  	String ele = enum1.nextElement().toString();
			 System.out.println(" --" + ele + "  " + request.getParameter(ele));
		}
		 
		 
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
	document.getElementById('ReadkWhID').disabled = true;
	document.getElementById('ReadDemandID').disabled = true;
	document.getElementById('ReadPeakID').disabled = true;
	document.getElementById('ReadVoltageID').disabled = true;
	document.getElementById('ReadOutageID').disabled = true;
	document.getElementById('ReadOutageHistID').disabled = true;
	document.getElementById('ClearTextID').disabled = true;
	document.getElementById('RefreshID').disabled = true;
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
  <input type="hidden" name="timeOut" value="5000">
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
      <table width="530" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td> 
            <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td align="right" class="TableCell"> 
                  <input type="checkbox" name="updateDB">
                  Update Database</td>
              </tr>
            </table>
            <table border="0" cellspacing="5" cellpadding="0" width="100%" height="100%" class="TableCell" align="center">
              <tr> 
                <td width="100%"> 
                  <table border="1" cellspacing="0" cellpadding="5" width="100%" height="100%" class="TableCell">
                    <tr> 
					  <td width = "2%" rowspan="3" align="left" valign="middle" class="HeaderCell">E<BR>
                        N<BR>
                        E<BR>
                        R<BR>
                        G<BR>
                        Y </td>					
                      <td width="18%" class="HeaderCell" align="center" height="35">CURRENT</td>
                      <td width="35%" align="center" height="35"> 
                        <%pointData = ycBean.getRecentPointData(deviceID, 1, PointTypes.PULSE_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      </td>
                      <td width="35%" align="right" height="35"> 
                        <div id="currEnergyPD"> 
                          <%if( pointData != null){%>
                          <%=format_nv3.format(pointData.getValue())%> 
                          <% } else {%>
                          --- 
                          <%}%>
                        </div>
                      </td>
                      <td width="12%" align="center" height="35" valign="middle"> 
                        <input type="image" onClick="setCommand('getvalue kwh');disableAllButtons()" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButton.gif" id="ReadkWhID" name="ReadkWh" align="middle">
                      </td>
                    </tr>
                    <tr> 
                      <td width="18%" class="HeaderCell" align="center" height="35">PREVIOUS</td>
                      <td width="35%" height="49" align="center"> 
                        <%
						int xID = ycBean.getPointID(deviceID, 1, PointTypes.PULSE_ACCUMULATOR_POINT);
						java.util.TreeMap tempMap = ycBean.getPrevMonthTSToValueMap(xID);
						java.util.Date[] keyArray = ycBean.getPrevDateArray(xID);
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
                      <td width="35%" align="right" height="35"> 
                        <div id="prevEnergyPD"><%=value%> </div>
                      </td>
                      <td width="12%" align="center" height="35" valign="middle">&nbsp; </td>
                    </tr>
                    <tr> 
                      <td width="18%" class="HeaderCell" align="center" height="49">TOTAL ENERGY</td>
                      <td width="35%" height="49" align="center">&nbsp;</td>
                      <td width="35%" align="right" height="49"> 
                        <div id="totalUsage"> 
                          <script>javascript:updatePrevious()</script>
                        </div>
                      </td>
                      <td width="12%" align="center" height="49" valign="middle">&nbsp;</td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr> 
                <td width="90%"> 
                  <table border="0" cellspacing="0" cellpadding="5" width="100%" height="100%" class="TableCell">
                    <tr> 
                      <td width="100%" height="15" class="SubtitleHeader" align="center"></td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr> 
                <td width="50%" valign="bottom"  > 
                  <table border="1" cellspacing="0" cellpadding="5" width="100%" height="100%" class="TableCell">
                    <tr> 
					  <td width = "2%" rowspan="2" align="left" valign="middle" class="HeaderCell">D<BR>
                        E<BR>
                        M<BR>
                        A<BR>
                        N<BR>
                        D </td>					
                      <td width="18%" class="HeaderCell" align="center" height="35">CURRENT</td>
                      <%--                      <%pointData = (PointData)ycBean.getRPHPointData(deviceID, 1, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                      <td width="17%" align="center" height="35"> 
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      </td>
                      <td width="17%" align="right" height="35"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% }%>
                      </td>
					  --%>
                      <td width="35%" align="center" height="35"> 
                        <%pointData = ycBean.getRecentPointData(deviceID, 1, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      </td>
                      <td width="35%" align="right" height="35"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% } else {%>
                        &nbsp; 
                        <%}%>
                      </td>
                      <td width="12%" align="center" height="35" valign="middle"> 
                        <input type="image" onClick="setCommand('getvalue demand');disableAllButtons()" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButton.gif" id="ReadDemandID" name="ReadDemand" align="middle">
                      </td>
                    </tr>
                    <tr> 
                      <td width="18%" class="HeaderCell" align="center" height="35">PEAK</td>
                      <%--                      <td width="17%" align="center" height="35"> 
                        <%pointData = (PointData)ycBean.getRPHPointData(deviceID, 11, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      </td>
                      <td width="17%" align="right" height="35"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% }%>
                      </td>
--%>
                      <td width="35%" align="center" height="35"> 
                        <%pointData = ycBean.getRecentPointData(deviceID, 11, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      </td>
                      <td width="35%" align="right" height="35"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% } else {%>
                        &nbsp; 
                        <%}%>
                      </td>
                      <td width="12%" align="center" height="35" valign="middle"> 
                        <input type="image" onClick="setCommand('getvalue peak');disableAllButtons()" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButton.gif" id="ReadPeakID" name="ReadPeak" align="middle">
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr> 
                <td width="90%"> 
                  <table border="0" cellspacing="0" cellpadding="5" width="100%" height="100%" class="TableCell">
                    <tr> 
                      <td width="100%" height="15" class="SubtitleHeader" align="center"></td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr> 
                <td width="100%"> 
                  <table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%" class="TableCell">
                    <tr> 
                      <td width="68%"> 
                        <table border="1" cellspacing="0" cellpadding="5" width="100%" height="100%" class="TableCell">
                          <tr> 
							<td width = "2%" rowspan="3" align="left" valign="middle" class="HeaderCell">V<BR>
								O<BR>
                              L<BR>
								T<BR>
								A<BR>
								G<BR>
								E </td>						  
                            <td width="18%" class="HeaderCell" align="center" height="35">CURRENT</td>
                            <%--                      <td width="17%" align="center" height="35"> 
	                        <%pointData = (PointData)ycBean.getRPHPointData(deviceID, 4, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
    	                    <%if( pointData != null){%>
        	                <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
            	            <% } else {%>
                	        --- 
                    	    <%}%>
	                      </td>
    	                  <td width="17%" align="right" height="35"> 
        	                <%if( pointData != null){%>
            	            <%=format_nv3.format(pointData.getValue())%> 
                	        <% } else {%>&nbsp;<%}%>
                    	  </td>
--%>
                            <td width="35%" align="center" height="35"> 
                              <%pointData = ycBean.getRecentPointData(deviceID, 4, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                              <%if( pointData != null){%>
                              <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                              <% } else {%>
                              --- 
                              <%}%>
                            </td>
                            <td width="35%" align="right" height="35"> 
                              <%if( pointData != null){%>
                              <%=format_nv3.format(pointData.getValue())%> 
                              <% } else {%>
                              &nbsp; 
                              <%}%>
                            </td>
                            <td width="12%" align="center" height="35" valign="middle">&nbsp;</td>
                          </tr>
                          <tr> 
                            <td width="18%" class="HeaderCell" align="center" height="35">MINIMUM</td>
                            <td width="35%" align="center" height="35"> 
                              <%pointData = (PointData)ycBean.getRecentPointData(deviceID, 15, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                              <%if( pointData != null){%>
                              <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                              <% } else {%>
                              --- 
                              <%}%>
                            </td>
                            <td width="35%" align="right" height="35"> 
                              <%if( pointData != null){%>
                              <%=format_nv3.format(pointData.getValue())%> 
                              <% } else {%>
                              &nbsp; 
                              <%}%>
                            </td>
                            <td width="12%" align="center" rowspan="2" height="35" valign="middle" > 
                              <input type="image" onClick="setCommand('getvalue voltage');disableAllButtons()" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButton.gif" id="ReadVoltageID" name="ReadVoltage" align="middle">
                            </td>
                          </tr>
                          <tr> 
                            <td width="18%" class="HeaderCell" align="center" height="35">MAXIMUM</td>
                            <td width="35%" align="center" height="35"> 
                              <%pointData = (PointData)ycBean.getRecentPointData(deviceID, 14, PointTypes.DEMAND_ACCUMULATOR_POINT);%>
                              <%if( pointData != null){%>
                              <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                              <% } else {%>
                              --- 
                              <%}%>
                            </td>
                            <td width="35%" align="right" height="35"> 
                              <%if( pointData != null){%>
                              <%=format_nv3.format(pointData.getValue())%> 
                              <% } else {%>
                              &nbsp; 
                              <%}%>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr> 
                <td width="90%"> 
                  <table border="0" cellspacing="0" cellpadding="5" width="100%" height="100%" class="TableCell">
                    <tr> 
                      <td width="100%" height="15" class="SubtitleHeader" align="center"></td>
                    </tr>
                  </table>
                </td>
              <tr> 
                <td width="100%"> 
                  <table border="1" cellspacing="0" cellpadding="5" width="100%" height="100%" class="TableCell">
					  <td width = "2%" rowspan="2" align="left" valign="middle" class="HeaderCell">O<BR>
                        U<BR>
                        T<BR>
                        A<BR>
                        G<BR>
                        E </td>
                      <td width="18%" class="HeaderCell" align="center" height="35">BLINK COUNT</td>
                      <td width="35%" align="center" height="35"> 
                        <%pointData = ycBean.getRecentPointData(deviceID, 20, PointTypes.PULSE_ACCUMULATOR_POINT);%>
                        <%if( pointData != null){%>
                        <%=dateTimeFormat.format(pointData.getPointDataTimeStamp())%> 
                        <% } else {%>
                        --- 
                        <%}%>
                      </td>
                      <td width="35%" align="right" height="35"> 
                        <%if( pointData != null){%>
                        <%=format_nv3.format(pointData.getValue())%> 
                        <% } else {%>
                        &nbsp; 
                        <%}%>
                      </td>
                      <td width="12%" align="center" height="35" valign="middle"> 
                        <input type="image" onClick="setCommand('getvalue powerfail & getvalue outage 1');disableAllButtons()" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButton.gif" id="ReadOutageID" name="ReadOutage" align="middle">
                      </td>
                    </tr>
                    <tr> 
                      <td width="18%" class="HeaderCell" align="center" height="35">HISTORY<BR>
                        (last 6)</td>
                      <td width="68%" align="left" height="35" colspan="2"> 
                        <%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_1);%>
                        <%if( pointData != null){%>
                        1.&nbsp;&nbsp;<%=pointData.getStr()%> 
                        <%}%>
                        <%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_2);%>
                        <%if( pointData != null){%>
                        <br>
                        2.&nbsp;&nbsp;<%=pointData.getStr()%> 
                        <%}%>
                        <%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_3);%>
                        <%if( pointData != null){%>
                        <br>
                        3.&nbsp;&nbsp;<%=pointData.getStr()%> 
                        <%}%>
                        <%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_4);%>
                        <%if( pointData != null){%>
                        <br>
                        4.&nbsp;&nbsp;<%=pointData.getStr()%> 
                        <%}%>
                        <%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_5);%>
                        <%if( pointData != null){%>
                        <br>
                        5.&nbsp;&nbsp;<%=pointData.getStr()%> 
                        <%}%>
                        <%pointData = (PointData)ycBean.getRecentPointData(deviceID, -1, PointTypes.OUTAGE_6);%>
                        <%if( pointData != null){%>
                        <br>
                        6.&nbsp;&nbsp;<%=pointData.getStr()%> 
                        <%}%>
                        &nbsp;</td>
                      <td width="12%" align="center" height="35" valign="middle"> 
                        <input type="image" onClick="setCommand('getvalue outage 3 & getvalue outage 5 & getvalue outage 1');disableAllButtons()" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButton.gif" id="ReadOutageHistID" name="ReadOutageHist" align="middle">
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" class="TableCell">
              <tr> 
                <td align="right"><a href='CommandDevice.jsp?deviceID=<%=deviceID%>&manual' name="ManualCommander"  class="Link1">
                  Go To Commander</a></td>
              </tr>
            </table>			
          </td>
        </tr>
      </table>
      <br>
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
          <td class="SubtitleHeader"  valign="top">Command Execution Log</td>
        </tr>
        <tr> 
          <td > 
            <div align="center"> 
              <textarea id="resultText" name="resultText" class="TableCell" readonly="readonly" cols="100" rows="10" wrap="VIRTUAL"><%= ycBean.getResultText()%></textarea>
              <input type="submit" name="ClearText" id="ClearTextID"  value="Clear Results">
              <input type="reset" name="Refresh" id="RefreshID" value="Refresh" onClick="window.location.reload()">
            </div>
          </td>
        </tr>
        <br>
      </table>
    </div>
    <a href="<%=referrer%>&clearids" class="Link3">.</a> </td>
</form>
