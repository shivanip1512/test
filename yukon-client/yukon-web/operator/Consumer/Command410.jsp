<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject"%>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>

<%@ page import="com.cannontech.database.cache.functions.*"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>
<%@ page import="com.cannontech.database.data.point.PointTypes"%>
<%@ page import="com.cannontech.database.db.point.RawPointHistory"%>

<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory starsMCT = inventories.getStarsInventory(invNo);
	int deviceID = starsMCT.getDeviceID();

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = PAOFuncs.getLiteYukonPAO(deviceID);

	//Map Contains key = pointID, value = RawPointHistory
	HashMap dataMap = RPHFuncs.getRecentRPHData(new Integer(deviceID));
	RawPointHistory rph = null;
	
	//MAX VOLTAGE
	//rph = com.cannontech.servlet.CommanderServlet.getRPHData(deviceID, 14, PointTypes.DEMAND_ACCUMULATOR_POINT, dataMap);
	//MIN VOLTAGE
	//rph = com.cannontech.servlet.CommanderServlet.getRPHData(deviceID, 15, PointTypes.DEMAND_ACCUMULATOR_POINT, dataMap);
%>

<jsp:useBean id="YC_BEAN" class="com.cannontech.yc.gui.YC" scope="session">
	<jsp:setProperty name="YC_BEAN" property="*"/>
</jsp:useBean>
<jsp:setProperty name="YC_BEAN" property="*"/>
<jsp:setProperty name="YC_BEAN" property="deviceID" value="<%=deviceID%>"/>

<% if( request.getParameter("execute") != null){%>
	<jsp:setProperty name="YC_BEAN" property="*"/>
	<jsp:forward page="&lt%= request.getContextPath() %&gt/servlet/CommanderServlet?InvNo=<%=invNo%>"/>
	<%return;
	}%>

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

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<!--<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">-->
</head>
<body class="Background" leftmargin="0" topmargin="0"> 
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
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <%@ include file="include/HeaderBar.jsp" %>
    </td>
  </tr>
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="102" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101"> 
            <% String pageName = "CommandMeter.jsp?InvNo=" + invNo;%>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
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
              <form name="commandForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet">
              <input type="hidden" name="deviceID" value="<%=deviceID%>">
              <input type="hidden" name="command" value="">
              <input type="hidden" name="timeOut" value="8000">
              <input id="redirect" type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
              <input id="referrer" type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
              <table width="530" border="0" cellspacing="0" cellpadding="3">
                <tr> 
                  <td width="40%" class="SubtitleHeader" align="right"> Meter Name:</td>
                  <td width="60%" class="TableCell"><%=liteYukonPao.getPaoName()%></td>
                </tr>
			  </table>
                <table width="530" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td class="SubtitleHeader">METER DATA</td>
				  </tr>
				</table>
                <table width="530" border="1" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td>
                      <table border="0" cellspacing="5" cellpadding="0" width="100%" height="40" class="TableCell">
                        <tr>
							<%rph = com.cannontech.servlet.CommanderServlet.getRPHData(deviceID, 1, PointTypes.PULSE_ACCUMULATOR_POINT, dataMap);%>
                          <td width="40%">Last Read:
						    <br><input type="text" name="frozenKwh" size="15%" value="<%if(rph == null)%>N/A<%else%><%=rph.getValue().doubleValue()%>">
						    &nbsp;<%if( rph != null){%>(<%=dateTimeFormat.format(rph.getTimeStamp().getTime())%>)<%}%>
						  </td>	
                          <td width="40%">Current Read:
						    <br><input type="text" name="currentKwh" size="15%">
						  </td>
                          <td align="center" width="19%"><u>Read Meter</u>
							<br><input type="submit" name="ReadkWh" value="kWh" onClick="setCommand('getvalue kwh');disableAllButtons()">
                          </td>
						</tr>
                        <tr>
	                        <%rph = com.cannontech.servlet.CommanderServlet.getRPHData(deviceID, 1, PointTypes.DEMAND_ACCUMULATOR_POINT, dataMap);%>
                          <td width="40%">Last Demand:
						    <br><input type="text" name="frozenDemand" size="15%" value="<%if(rph == null)%>N/A<%else %><%=rph.getValue().doubleValue()%>">
						    &nbsp;<%if( rph != null){%>(<%=dateTimeFormat.format(rph.getTimeStamp().getTime())%>)<%}%>
						  </td>	
                          <td width="40%">Current Demand:
	  						<br><input type="text" name="currentDemand" size="15%">
						  </td>
                          <td align="center" width="19%">
                            <br><input type="submit" name="ReadDemand" value="Demand" onClick="setCommand('getvalue demand');disableAllButtons()">
                          </td>
						</tr>
                        <tr>
	                        <%rph = com.cannontech.servlet.CommanderServlet.getRPHData(deviceID, 11, PointTypes.DEMAND_ACCUMULATOR_POINT, dataMap);%>
                          <td width="40%">Last Peak: <br>
                            <input type="text" name="frozenPeak" size="15%" value="<%if(rph == null)%>N/A<%else %><%=rph.getValue().doubleValue()%>">
							&nbsp;<%if( rph != null){%>(<%=dateTimeFormat.format(rph.getTimeStamp().getTime())%>)<%}%>
						  </td>	
                          <td width="40%">Current Peak:
						    <br><input type="text" name="currentPeak" size="15%">
						  </td>
                          <td align="center" width="19%">
                            <br><input type="submit" name="ReadPeak" value="Peak" onClick="setCommand('getvalue peak');disableAllButtons()">
                          </td>
						</tr>
                        <tr>
	                        <%rph = com.cannontech.servlet.CommanderServlet.getRPHData(deviceID, 4, PointTypes.DEMAND_ACCUMULATOR_POINT, dataMap);%>
                          <td width="40%">Last Voltage:
						    <br><input type="text" name="frozenVoltage" size="15%" value="<%if(rph == null)%>N/A<%else %><%=rph.getValue().doubleValue()%>">
						    &nbsp;<%if( rph != null){%>(<%=dateTimeFormat.format(rph.getTimeStamp().getTime())%>)<%}%>
						  </td>	
                          <td width="40%">Current Voltage:
						    <br><input type="text" name="currentVoltage" size="15%">
						  </td>
                          <td align="center" width="19%">
                            <br><input type="submit" name="ReadVoltage" value="Voltage" onClick="setCommand('getvalue voltage');disableAllButtons()">
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
                        	<%rph = com.cannontech.servlet.CommanderServlet.getRPHData(deviceID, 20, PointTypes.DEMAND_ACCUMULATOR_POINT, dataMap);%>
                          <td width="80%" height="100%">Blink Count:
						    <input type="text" name="currentKwh" size="5%" value="<%if(rph == null)%>N/A<%else%><%=rph.getValue().doubleValue()%>">&nbsp;&nbsp;
                            <input type="submit" name="ReadPowerfail" value="Get Outage" onClick="setCommand('getvalue powerfail');disableAllButtons()">
                          </td>
                          <td align="center" valign="bottom" width="19%" height="100%"><u>Read Outage</u>
                          </td>
						</tr>
                        <tr>
                          <td width="80%" height="100%">
	  						<input type="text" name="outage1_2" size="68%">
						  </td>
                          <td valign="middle" align="center" width="19%" height="100%"> 
                            <input type="submit" name="ReadOutage1_2" value="1 - 2" onClick="setCommand('getvalue outage 1');disableAllButtons()">
                          </td>
						</tr>
                        <tr>
                          <td width="80%" height="100%">
	  						<input type="text" name="outage3_4" size="68%" >
						  </td>
                          <td valign="middle" align="center" width="19%" height="100%"> 
                            <input type="submit" name="ReadOutage3_4" value="3 - 4" onClick="setCommand('getvalue outage 3');disableAllButtons()">
                          </td>
						</tr>
                        <tr>
                          <td width="80%" height="100%">
	  						<input type="text" name="outage5_6" size="68%">
						  </td>
                          <td valign="middle" align="center" width="19%" height="100%"> 
                            <input type="submit" name="ReadOutage5_6" value="5 - 6" onClick="setCommand('getvalue outage 5');disableAllButtons()">
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
                      String tempCommand = YC_BEAN.getCommandString().replaceAll("noqueue", "").trim();
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
                        <textarea id="resultText" name="resultText" class="TableCell" readonly="readonly" cols="100" rows="10" wrap="VIRTUAL"><%= YC_BEAN.getResultText()%></textarea>
                        <input type="submit" name="clearText" value="Clear Results">
                        <input type="reset" name="refresh" value="Refresh" onClick="window.location.reload()">
                      </div>
                    </td>
                  </tr>
                  <br>
              </table>
              </form>              
              <br>
            </div>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
