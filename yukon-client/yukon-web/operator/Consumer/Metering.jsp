<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>

<SCRIPT  LANGUAGE="JavaScript" SRC="../../JavaScript/calendar.js"></SCRIPT>
<%@ include file="../../include/trending_functions.jsp" %>

<html>
<head>
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<%	graphBean.setGdefid( 
		(request.getParameter("gdefid") == null
		? -1 : Integer.parseInt(request.getParameter("gdefid"))) );

	graphBean.setPage( 
		(request.getParameter("page") == null
		? 1 : Integer.parseInt(request.getParameter("page"))) );
%>
<body class="Background" text="#000000" leftmargin="0" topmargin="0" onload = "init()">
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
					<img id = "theGraph" src="<%= request.getContextPath() %>/servlet/GraphGenerator?action=EncodeGraph" > 
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