<html>
<%@ include file="metering_header.jsp" %>
<%@ include file="../../trending_functions.jsp" %>

<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<title>Metering</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../../JavaScript/Calendar1-82.js"></SCRIPT>

<body class="Background" text="#000000" leftmargin="0" topmargin="0" onload = "init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td> 
		<table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr> 
			<td width="102" height="102" background="MeterImage.jpg">&nbsp;</td>
			<td valign="bottom" height="102"> 
				<table width="657" cellspacing="0"  cellpadding="0" border="0">
				<tr> 
                	<td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
				</tr>
				<tr>
					<td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Commercial Metering&nbsp;&nbsp;</td>
					<td width="253" valign="middle">&nbsp;</td>
					<td width="58" valign="middle">
                		<div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
					</td>
					<td width="57" valign="middle"> 
						<div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
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
			<form>
			<td valign="top" width="101"><br>
			<table width="101" border="0" cellspacing="0" cellpadding="5">
			<tr>
			<%@include file="nav.jsp"%>
			</tr>
			</table>
			</td>
			</form>
			<td width="1" bgcolor="#000000"></td>
			<td width="657" valign="top" bgcolor="#FFFFFF">

			<table width="98%" border="0" cellspacing="0" cellpadding="0" height="18">
			<tr>
				<!--<td align = "right"><a name = "top" href = "#marker" style="text-decoration:underlined" class = "TableCell" >More Options</a></td> -->
			</tr>
			</table>

            <% String pageName = "Metering.jsp"; %> 
			<%@include file="../../trending_options.jsp"%>

            <table width="575" border="0" cellspacing="0" cellpadding="0" align="center" height="46">
			<tr> 
				<td><center> 
				<%
				if( graphBean.getGdefid() <= 0 )
				{%>
					<p> No Data Set Selected 
				<%}
				else if( graphBean.getViewType() == TrendModelType.SUMMARY_VIEW )
				{
					graphBean.updateCurrentPane();
					out.println(graphBean.getHtmlString());
				}
				else if( graphBean.getViewType() == TrendModelType.TABULAR_VIEW )
				{
					graphBean.updateCurrentPane();
					out.println(graphBean.getHtmlString());
				}
				else // "graph" is default
				{%>
					<img id = "theGraph" src="/servlet/GraphGenerator?" > 
				<%}
				%>
				<br><font size="-1"><cti:getProperty propertyid="<%= CommercialMeteringRole.TRENDING_DISCLAIMER%>"/></font>
				</center></td>
			</tr>
			</table>
		</td>
	</tr>
	</table>
	</td>
	</tr>
	</table>
	<br>
</body>
</html>
