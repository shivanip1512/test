<%-- A wrapper file for Operator access to commander from the  consumer information pages.
--%>
<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>

<%@ page import="com.cannontech.database.cache.functions.*"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>
<%@ page import="com.cannontech.database.data.point.PointTypes"%>
<%@ page import="com.cannontech.database.db.point.RawPointHistory"%>
<%@ page import="com.cannontech.message.dispatch.message.PointData"%> 
<%@ page import="com.cannontech.database.data.lite.LiteRawPointHistory"%>
<jsp:useBean id="YC_BEAN" class="com.cannontech.yc.bean.YCBean" scope="session"/>

<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	PointData pointData = null;
	
	boolean manual = false;
	if( request.getParameter("manual") != null)
	{	//Force going to the Commander page instead of a page based on the DeviceType
		manual = true;
	}
	boolean lp = false;
	if( request.getParameter("lp") != null)
	{	//Force going to the Load Profile
		lp = true;
	}

	String cmd = request.getParameter("command");
	if (cmd != null)
		YC_BEAN.setCommandString("");
		
	int invNo = -1;
	int deviceID = -1;
	if( request.getParameter("InvNo") != null)
	{
		invNo = Integer.parseInt(request.getParameter("InvNo"));
		StarsInventory starsMCT = inventories.getStarsInventory(invNo);
		deviceID = starsMCT.getDeviceID();
	}
	else
	{
		int i = 1;
	}

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = PAOFuncs.getLiteYukonPAO(deviceID);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<link rel="stylesheet" href="../../WebConfig/yukon/Base.css" type="text/css">
</head>
<body class="Background" leftmargin="0" topmargin="0">
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
          	<%--"redirect" is required by Commander.jsp and for the sake of this wrapper being able to know the InvNo--%>
            <% String redirect = request.getRequestURI()+ "?InvNo=" + invNo;%>
            <%if( manual) redirect = redirect + "&manual";%>
            <%if( lp) redirect = redirect + "&lp";%>
            <% String referrer = request.getRequestURI()+ "?InvNo=" + invNo;%>
            <% String pageName = "CommandInv.jsp?InvNo=" + invNo;%>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
<%
			if( lp )
			{%>
				<%@ include file="../../apps/AdvancedCommander410.jsp"%>
			<%}
			else if (liteYukonPao.getType() == com.cannontech.database.data.pao.DeviceTypes.MCT410IL && !manual)
			{
				%>
				<%@ include file="../../apps/Commander410.jsp"%>
			<%}
			else
			{%>
	 			<%@ include file="../../apps/Commander.jsp"%>
			<%}%>
			
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
