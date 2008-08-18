<%-- A wrapper file for Operator access to commander from the  consumer information pages.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>
<%@ page import="com.cannontech.core.dynamic.PointValueHolder"%> 
<%@ page import="com.cannontech.message.dispatch.message.PointData"%> 
<%@ page import="com.cannontech.database.data.lite.LiteRawPointHistory"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>
<%@ page import="com.cannontech.database.data.point.PointTypes"%>
<%@ page import="com.cannontech.database.db.point.RawPointHistory"%>

<jsp:useBean id="YC_BEAN" class="com.cannontech.yc.bean.YCBean" scope="session"/>
<jsp:setProperty name="YC_BEAN" property="userID" value="<%= lYukonUser.getUserID()%>"/>

<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	PointValueHolder pointValueHolder = null;
	
	String cmd = request.getParameter("command");
	if (cmd != null)
		YC_BEAN.setCommandString("");
		
	int invNo = -1;
	int deviceID = PAOGroups.INVALID;
	if( request.getParameter("InvNo") != null)
	{
		invNo = Integer.parseInt(request.getParameter("InvNo"));
		StarsInventory starsMCT = inventories.getStarsInventory(invNo);
		deviceID = starsMCT.getDeviceID();
	}
	//else	//SN NOT SURE WHY THIS WAS HERE 20050913
	//{
		//int i = 1;
	//}

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);

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
	boolean isMCT4XX = liteYukonPao!=null && com.cannontech.database.data.device.DeviceTypesFuncs.isMCT410(liteYukonPao.getType());
	if( !isMCT4XX )
	{	//MUST BE Manual...force it
		manual = true;
	}

	String serialNum = "";
	String serialType = "";
	if( request.getParameter("xcom") != null){
		serialNum = request.getParameter("xcom");
		serialType = "xcom";
	}
	else if( request.getParameter("vcom") != null){
		serialNum = request.getParameter("vcom");
		serialType = "vcom";
	}
	else if( request.getParameter("sa205") != null){
		serialNum = request.getParameter("sa205");
		serialType = "sa205";
	}
	else if( request.getParameter("sa305") != null){
		serialNum = request.getParameter("sa305");
		serialType = "sa305";
	}	
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<link rel="stylesheet" href="../../WebConfig/yukon/Base.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/yukon/styles/commanderStyles.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/yukon/styles/YukonGeneralStyles.css" type="text/css">
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
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
            <% if( serialType.length() > 0 )
            {
            	redirect = redirect + "&" + serialType + "=" + serialNum;
            	referrer = referrer + "&" + serialType + "=" + serialNum;
            	pageName = pageName + "&" + serialType + "=" + serialNum;
            }
            %>
            <%@ include file="include/Nav.jspf" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td bgcolor="#FFFFFF">
				<c:choose>
					<c:when test="${lp}">
						<%@ include file="../../apps/AdvancedCommander410.jspf"%>
					</c:when>
					<c:when test="${isMCT4XX && !manual}">
						<%@ include file="../../apps/Commander410.jspf"%>
					</c:when>
					<c:otherwise>
						<%@ include file="../../apps/Commander.jspf"%>
					</c:otherwise>
				</c:choose>
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
