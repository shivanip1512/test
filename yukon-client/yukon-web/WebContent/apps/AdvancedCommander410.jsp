<%@ page import="com.cannontech.message.dispatch.message.PointData"%> 

<%-- A wrapper file for access to commander with a deviceID.
--%>
<%@ include file="../operator/Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>

<%@ page import="com.cannontech.database.cache.functions.*"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>
<%@ page import="com.cannontech.database.data.point.PointTypes"%>
<%@ page import="com.cannontech.database.db.point.RawPointHistory"%>
<%@ page import="com.cannontech.database.data.lite.LiteRawPointHistory"%>
<jsp:useBean id="YC_BEAN" class="com.cannontech.yc.bean.YCBean" scope="session"/>
<%
if( request.getParameter("clearids") != null)
{
	YC_BEAN.getRequestMessageIDs().clear();
	com.cannontech.clientutils.CTILogger.info("Clearing the RequestMessageIDs, they aren't coming back!");
}

	java.text.DecimalFormat format_2char = new java.text.DecimalFormat("00");
	
	int deviceID = 0;
	if( request.getParameter("deviceID") != null)
	{
		deviceID = Integer.parseInt(request.getParameter("deviceID"));
	}
	else
	{
		deviceID = YC_BEAN.getDeviceID();
	}
	//set the deviceID of the YC_BEAN
	YC_BEAN.setDeviceID(deviceID);

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = PAOFuncs.getLiteYukonPAO(deviceID);
%>
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
<!-- Java script needed for the Calender Function--->
<SCRIPT  LANGUAGE="JavaScript" SRC="../JavaScript/calendar.js"></SCRIPT>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/yukon/Base.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../WebConfig/yukon/MeterImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Commander&nbsp;&nbsp;</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle"> 
                  <div align="center"><span class="MainText"><a href="../operator/Operations.jsp" class="Link3">Home</a></span></div>
                </td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
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
            <%--"redirect" is required by Commander.jsp and for the sake of this wrapper being able to know the deviceID--%>
            <% String redirect = request.getRequestURI()+ "?deviceID=" + deviceID;%>
            <% String referrer = request.getRequestURI()+ "?deviceID=" + deviceID;%>
            <% String pageName = "CommandDevice.jsp?deviceID=" + deviceID;%>
            <table width="101" border="0" cellspacing="0" cellpadding="5">
              <tr> 
                <td> 
                  <div align="left"> <span class="NavHeader">Devices</span> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                        <td width="10"></td>
                        <td style="padding:1"><a href='SelectDevice.jsp' class='Link2'><span class='NavText'>Back to List</span></a></td>
                      </tr>
                      <% for (int i = 0; i < YC_BEAN.getDeviceIDs().size(); i++)
			          {
			          	int id = ((Integer)YC_BEAN.getDeviceIDs().get(i)).intValue();%>
                      <tr> 
                        <td width="10"></td>
                        <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=id%>' class='Link2'><span class='NavText'><%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(id)%></span></a></td>
                      </tr>
                      <%}%>
                    </table>
                  </div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
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
	                  <table width="95%" align="center" border="0" cellspacing="0" cellpadding="0">
				  		<tr> 
						  <td align="right"><a href='CommandDevice.jsp?deviceID=<%=deviceID%>' class='Link4'><span class='NavText'>Back To&nbsp;<%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(deviceID)%></span></a></td>
				  		</tr>
					  </table>								  
					  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
						<tr> 
						  <td width="6" height="19"><img src="../WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
						  <td height="19" bgcolor="888888" class="tableHeader" align="center">Please select a time period to review</td>
						  <td width="6" height="19"><img src="../WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
						</tr>
						<tr>
						  <td width="6" background="../WebConfig/yukon/Side_left.gif">&nbsp;</td>
						  <td>
							<table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
							  <tr> 
								<td class="main" width="47%" valign="bottom" align="right">Start Date:
									  <input id="startCal" type="text" name="startDate" value="<%= datePart.format(YC_BEAN.getStartDate()) %>" size="8">
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
						  <td width="6" background="../WebConfig/yukon/Side_right.gif">&nbsp;</td>
						</tr>
						<tr>
						  <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
						  <td background="../WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
						  <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
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
						  <td width="6" height="19"><img src="../WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
						  <td height="19" bgcolor="888888" class="tableHeader" align="center">Please select a Point to collect Load Profile On</td>
						  <td width="6" height="19"><img src="../WebConfig/yukon/Header_right.gif" width="6" height="19"></td>		  
						</tr>
						<tr>
						  <td width="6" background="../WebConfig/yukon/Side_left.gif">&nbsp;</td>
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
								<td colspan="2" height="19" class="main" align="center">
									<a href="javascript:disableAllButtons();" style="font-weight:bold" class="Link4">Refresh</a>&nbsp;Tabular View (using above settings)</td></tr>
						   </table>
						  </td>
						  <td width="6" background="../WebConfig/yukon/Side_right.gif">&nbsp;</td>
						</tr>
						<tr>
						  <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
						  <td background="../WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
						  <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
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
						  <td width="6" height="19"><img src="../WebConfig/yukon/Header_left.gif" width="6" height="36"></td>
						  <td height="19" bgcolor="888888" class="tableHeader" align="center">Data currently stored in Database:&nbsp;&nbsp;<%=liteYukonPao.getPaoName()%> - <%=com.cannontech.database.cache.functions.PointFuncs.getPointName(YC_BEAN.getPointID())%>
												&nbsp;[<%=datePart.format(YC_BEAN.getStartDate())%>]<BR>
												<a href="javascript:disableAllButtons();" class="Link3">Refresh</a>
												  &nbsp;Tabular View (using above settings)</td>
						  <td width="6" height="19"><img src="../WebConfig/yukon/Header_right.gif" width="6" height="36"></td>		  
						</tr>
						<tr>
						  <td width="6" background="../WebConfig/yukon/Side_left.gif">&nbsp;</td>
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
						  <td width="6" background="../WebConfig/yukon/Side_right.gif">&nbsp;</td>
						</tr>
						<tr>
						  <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
						  <td background="../WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
						  <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
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
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>