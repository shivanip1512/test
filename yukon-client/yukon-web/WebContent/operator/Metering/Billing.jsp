<html>
<%@ include file="include/billing_header.jsp" %>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<SCRIPT  LANGUAGE="JavaScript" SRC="../../JavaScript/calendar.js"></SCRIPT>
</head>

<SCRIPT LANGUAGE = "JavaScript">
function jumpPage()
{
	update();
	document.generate.action="<%=request.getContextPath()%>/servlet/BillingServlet?";	
	document.generate.method="post";
	location.href ="/Billing.jsp";	
}

function update()
{
	document.MForm.submit();
}

function updateDemandDate()
{
 document.getElementById('DEMANDSTART').innerHTML = "xx";
}
</SCRIPT>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td> 
		<table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr> 
				<td width="102" height="102" background="../../WebConfig/yukon/MeterImage.jpg">&nbsp;</td>
				<td valign="bottom" height="102"> 
					<table width="657" cellspacing="0"  cellpadding="0" border="0">
						<tr> 
							<td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
						</tr>
						<tr> 
							<td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Commercial Metering&nbsp;&nbsp;</td>
							
                <td width="253" valign="middle">&nbsp;</td>
							<td width="58" valign="middle"> 
								<div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
							</td>
							<td width="57" valign="middle"> 
								<div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
							</td>
						</tr>
					</table>
				</td>
				<td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
			<tr> 
				<td width="101" bgcolor="#000000" height="1"></td>
				<td width="1" bgcolor="#000000" height="1"></td>
				<td width="657" bgcolor="#000000" height="1"></td>
				<td width="1" bgcolor="#000000" height="1"></td>
			</tr>
			<tr> 
				<td  valign="top" width="101"></td>
				<td width="1" bgcolor="#000000"></td>
				<td width="657" valign="top" bgcolor="#FFFFFF"> 
					<div align="center"><br><span class="TitleHeader">BILLING</span></div>

					<form name = "MForm">
<jsp:setProperty name="BILLING_BEAN" property="fileFormat" param="format"/>
<jsp:setProperty name="BILLING_BEAN" property="billGroupType" param="gType"/>
<jsp:setProperty name="BILLING_BEAN" property="billGroup" param="grp"/>
<jsp:setProperty name="BILLING_BEAN" property="appendToFile" param="append"/>
<jsp:setProperty name="BILLING_BEAN" property="removeMult" param="mult"/>
<jsp:setProperty name="BILLING_BEAN" property="demandDaysPrev" param="dDays"/>
<jsp:setProperty name="BILLING_BEAN" property="energyDaysPrev" param="eDays"/>
<jsp:setProperty name="BILLING_BEAN" property="endDateStr" param="end"/>
							
						<table width="65%" border="0" cellspacing="0" cellpadding="4" height="209" align = "center">
							<tr> 
								<td height="162" colspan = "3"> 
									<table width="100%" border="1" cellspacing="0" cellpadding="3" height="148" align = "center">
										<tr> 
											<td height="35" width="67%"> 
												<table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell">
													<tr> 
														<td width="50%" align = "right" >File Format: </td>
														<td width="50%"> 
															<select name = "format" onchange="update()">
																<% /* Fill in the possible file format types*/
																int [] formats = FileFormatTypes.getValidFormatIDs();	
																int selectedFormat = billingBean.getFileFormat();
																for( int i = 0; i < formats.length; i++ )
																{
																	if( formats[i] == selectedFormat)
																		out.println("<OPTION VALUE=" + formats[i] + " SELECTED>" + FileFormatTypes.getFormatType(selectedFormat));
																	else
																		out.println("<OPTION VALUE=" + formats[i] + ">" + FileFormatTypes.getFormatType(formats[i]));
																}%>
															</select>
														</td>
													</tr>
												</table>
												<table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell">
													<tr> 
														<td width="50%" align = "right">Billing End Date:</td>
														<td width="50%">
														    <input id="cal" type="text" name="end" value="<%= datePart.format(billingBean.getEndDate()) %>" size="8" onChange="update()">
											                  <A HREF="javascript:openCalendar(document.getElementById('MForm').cal, 74, 0)"
											                    onMouseOver="window.status='End Date Calendar';return true;"
											                    onMouseOut="window.status='';return true;">
											                    <IMG SRC="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" WIDTH="20" HEIGHT="15" ALIGN="ABSMIDDLE" BORDER="0">
											                  </A>
														</td>
													</tr>
												</table>
												<table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell">
													<tr> 
														<td width="50%" align = "right">Demand Days Previous:</td>
														<td width="50%"> 
															<input type="text" name="dDays" value="<%=billingBean.getDemandDaysPrev()%>" size = "8" onChange="update()">
														</td>
<!--														<td id="DEMANDSTART" width="30%" align="left"><%=datePart.format(billingBean.getDemandStartDate())%></td>-->
													</tr>
												</table>
												<table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell" height="9">
													<tr> 
								 						<td width = "50%" align = "right">Energy Days Previous:</td>
														<td width = "50%"> 
															<input type="text" name="eDays" value="<%=billingBean.getEnergyDaysPrev()%>" size = "8">
														</td>
													</tr>
												</table>
												<table width="100%" border="0" cellspacing="0" cellpadding="3" height="10" class = "TableCell">
													<tr> 
														<td width="50%" align = "right" >
															<input type="checkbox" name="append" 
																<%if(billingBean.getAppendToFile())
																	out.println("checked>");
																else
																	out.println(">");%>
															<input type="hidden" name="append" value="false">
														</td>
														<td width="50%">Append to File </td>
													</tr>
													<tr> 
														<td width="50%" align = "right"> 
															<input type="checkbox" name="mult"
																<%if(billingBean.getRemoveMult())
																	out.println("checked>");
																else
																	out.println(">");%>
															<input type="hidden" name="mult" value="false">
														</td>
														<td width="50%">Remove Multiplier </td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr> 
								<td width="72%" valign = "top"> 
									<table width="100%" border="1" cellspacing="0" cellpadding="4" height="60" align = "center">
										<tr> 
											<td height="35" width="67%"> 
												<table width="100%" border="0" cellspacing="0" cellpadding="0" height="10" class = "TableCell">
													<tr> 
														<td width="50%" align = "right" colspan = "2"> 
															<table width="100%" border="0" cellspacing="0" cellpadding="1" class = "TableCell">
																<tr> 
																	<td width="50%" align="right">Billing Group Type:</td>
																	<td width="50%"> 
																		<select name = "gType" onchange="update()">
																			<% /* Fill in the possible file format types*/
																			int [] groupTypes = DeviceMeterGroup.getValidBillGroupTypeIDs();
																			
																			int selectedGrpType = billingBean.getBillGroupType();
																																					
																			for( int i = 0; i < groupTypes.length; i++ )
																			{
																				if( groupTypes[i] == selectedGrpType)
																					out.println("<OPTION VALUE=" + groupTypes[i] + " SELECTED>" + BillingFileDefaults.getBillGroupTypeDisplayString(groupTypes[i]));
																				else
																					out.println("<OPTION VALUE=" + groupTypes[i] + ">" + BillingFileDefaults.getBillGroupTypeDisplayString(groupTypes[i]));
																			}%>
																		</select>
																	</td>
																</tr>
																<tr> 
																	<td width="50%" align="right">Billing Group:</td>
																	<td width="50%"> 
																		<%
																		String [] groups = billingBean.getValidBillGroups();	
																		String selectedGrp = billingBean.getBillGroup();
																		%>
																		<select name = "grp" size="3" width="50%">
																			<% /* Fill in the possible file format types*/
																			for( int i = 0; i < groups.length; i++ )
																			{
																				if( groups[i].equalsIgnoreCase(selectedGrp))
																					out.println("<OPTION VALUE=" + groups[i] + " SELECTED>" + groups[i]);
																				else
																					out.println("<OPTION VALUE=" + groups[i] + ">" + groups[i]);
																			}%>
																		</select>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
								</form>
								<form name = "generate">
								<td width="28%" align = "center"  valign = "top"> 
									<table width="100%" border="1" cellspacing="0" cellpadding="8" height = "100%">
										<tr>
											<td bgcolor ="#CCCCCC">
												<input type="submit" name="generate" value="Generate" onclick = "jumpPage()">
											</td>
										</tr>
									</table>
								</td>
								</form>
							</tr>
						</table>
				<p align="center">&nbsp;</p>
				<p align="center">&nbsp;</p>
				</td>
				<td width="1" bgcolor="#000000"></td>
			</tr>
		</table>
	</td>
</tr>
</table>
<br>
</body>
</html>