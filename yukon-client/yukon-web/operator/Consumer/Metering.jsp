<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>

<SCRIPT  LANGUAGE="JavaScript" SRC="../../JavaScript/calendar.js"></SCRIPT>
<%@ include file="../../include/trending_functions.jsp" %>

<html>
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<body class="Background" text="#000000" leftmargin="0" topmargin="0" onload = "init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td> 
		<table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr> 
			<td width="102" height="102" background="../../WebConfig/yukon/ConsumerImage.jpg">&nbsp;</td>
				<td valign="bottom" height="102"> 
				<table width="657" cellspacing="0"  cellpadding="0" border="0">
				<tr> 
					<td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
				</tr>
				<tr> 
					<td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer Account Information&nbsp;&nbsp;</td>
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
			<td  valign="top" width="101">
			<% String pageName = "Metering.jsp"; %>
			<%@ include file="include/Nav.jsp" %>
			</td>
			</form>
			<td width="1" bgcolor="#000000"></td>
			<td width="657" valign="top" bgcolor="#FFFFFF">
				<% String header = "METERING - INTERVAL DATA"; %>
				<%@include file="../../include/trending_options.jsp"%>
            <table width="575" border="0" cellspacing="0" cellpadding="0" align="center" height="46">
			<tr> 
				<td><center> 
				<%
				if( graphBean.getGdefid() <= 0 )
				{%>
					<p> No Data Set Selected 
				<%}
				else if( graphBean.getViewType() == GraphRenderers.SUMMARY )
				{
					graphBean.updateCurrentPane();
					out.println(graphBean.getHtmlString());
				}
				else if( graphBean.getViewType() == GraphRenderers.TABULAR)
				{%>
					<%@ include file="../../include/trending_tabular.jsp" %>					
					<%
				}				
				else // "graph" is default
				{%>
					<img id = "theGraph" src="<%= request.getContextPath() %>/servlet/GraphGenerator?" > 
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
</body>
</html>