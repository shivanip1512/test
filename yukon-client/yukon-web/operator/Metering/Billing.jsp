<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<%java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");%>

<jsp:useBean id="billingBean" class="com.cannontech.billing.mainprograms.BillingBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
<jsp:setProperty name="billingBean" property="endDateStr" value="<%=datePart.format(com.cannontech.util.ServletUtil.getToday())%>"/>
<jsp:setProperty name="billingBean" property="demandDaysPrev" value="30"/>
<jsp:setProperty name="billingBean" property="energyDaysPrev" value="7"/>
<jsp:setProperty name="billingBean" property="fileFormat" value="1"/>
    <%-- intialize bean properties --%>
</jsp:useBean>

<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../../JavaScript/Calendar1-82.js"></SCRIPT>
</head>

<SCRIPT LANGUAGE = "JavaScript">
function jumpPage()
{
	update();
	document.generate.action="/servlet/BillingServlet?";	
	document.generate.method="post";
	location.href ="/Billing.jsp";	
}

function update()
{
	document.MForm.submit();
}

</SCRIPT>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td> 
		<table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr> 
				<td width="102" height="102" background="MeterImage.jpg">&nbsp;</td>
				<td valign="bottom" height="102"> 
					<table width="657" cellspacing="0"  cellpadding="0" border="0">
						<tr> 
							<td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
						</tr>
						<tr> 
							<td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Commercial Metering&nbsp;&nbsp;</td>
							
                <td width="253" valign="middle">&nbsp;</td>
							<td width="58" valign="middle"> 
								<div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
							</td>
							<td width="57" valign="middle"> 
								<div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log Off</a>&nbsp;</span></div>
							</td>
						</tr>
					</table>
				</td>
				<td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
					<div align="center"><br><span class="Main"><b>BILLING</b></span></div>

					<form name = "MForm">
<jsp:setProperty name="billingBean" property="fileFormat" param="format"/>
<jsp:setProperty name="billingBean" property="billingGroupType" param="gType"/>
<jsp:setProperty name="billingBean" property="billingGroup" param="grp"/>
<jsp:setProperty name="billingBean" property="appendToFile" param="append"/>
<jsp:setProperty name="billingBean" property="removeMult" param="mult"/>
<jsp:setProperty name="billingBean" property="demandDaysPrev" param="dDays"/>
<jsp:setProperty name="billingBean" property="energyDaysPrev" param="eDays"/>
<jsp:setProperty name="billingBean" property="endDateStr" param="end"/>
								
					
						<table width="55%" border="0" cellspacing="0" cellpadding="4" height="209" align = "center">
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
																int [] formats = com.cannontech.billing.FileFormatTypes.getValidFormatIDs();	
																int selectedFormat = billingBean.getFileFormat();
																for( int i = 0; i < formats.length; i++ )
																{
																	if( formats[i] == selectedFormat)
																		out.println("<OPTION VALUE=" + formats[i] + " SELECTED>" + com.cannontech.billing.FileFormatTypes.getFormatType(selectedFormat));
																	else
																		out.println("<OPTION VALUE=" + formats[i] + ">" + com.cannontech.billing.FileFormatTypes.getFormatType(formats[i]));
																}%>
															</select>
														</td>
													</tr>
												</table>
												<table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell">
													<tr> 
														<td width="50%" align = "right">Billing End Date:</td>
														<td width="50%">
															<input type="text" name="end" value="<%=datePart.format(billingBean.getEndDate())%>" size = "10">
																<a href="javascript:show_calendar('MForm.end')"
																	onMouseOver="window.status='Pop Calendar';return true;"
																	onMouseOut="window.status='';return true;">
																	<img src="../../Images/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0">
																</a> 
														</td>
													</tr>
												</table>
												<table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell">
													<tr> 
														<td width="50%" align = "right">Demand Days Previous:</td>
														<td width="50%"> 
															<input type="text" name="dDays" value="<%=billingBean.getDemandDaysPrev()%>" size = "5">
														</td>
													</tr>
												</table>
												<table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell" height="9">
													<tr> 
								 						<td width = "50%" align = "right">Energy Days Previous:</td>
														<td width = "50%"> 
															<input type="text" name="eDays" value="<%=billingBean.getEnergyDaysPrev()%>" size = "5">
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
																			int [] groupTypes = com.cannontech.billing.mainprograms.BillingFileDefaults.getValidBillGroupTypeIDs();
																			int selectedGrpType = billingBean.getBillingGroupType();
																																					
																			for( int i = 0; i < groupTypes.length; i++ )
																			{
																				if( groupTypes[i] == selectedGrpType)
																					out.println("<OPTION VALUE=" + groupTypes[i] + " SELECTED>" + com.cannontech.billing.mainprograms.BillingFileDefaults.getBillGroupTypeString(selectedGrpType));
																				else
																					out.println("<OPTION VALUE=" + groupTypes[i] + ">" + com.cannontech.billing.mainprograms.BillingFileDefaults.getBillGroupTypeString(groupTypes[i]));
																			}%>
																		</select>
																	</td>
																</tr>
																<tr> 
																	<td width="50%" align="right">Billing Group:</td>
																	<td width="50%"> 
																		<%
																		String [] groups = billingBean.getValidBillingGroups();	
																		String selectedGrp = billingBean.getBillingGroup();
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
												<input type="submit" name="generate_submit" value="Generate" onclick = "jumpPage()">
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