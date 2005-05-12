<%-- A wrapper file for access to commander with a deviceID.
--%>
<%@ include file="../operator/Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>
<%@ page import="com.cannontech.message.dispatch.message.PointData"%> 
<%@ page import="com.cannontech.database.data.lite.LiteRawPointHistory"%>
<%@ page import="com.cannontech.database.cache.functions.*"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>
<%@ page import="com.cannontech.database.data.point.PointTypes"%>
<%@ page import="com.cannontech.database.db.point.RawPointHistory"%>
<cti:checklogin/> 

<script language='JavaScript' src='../JavaScript/ol/overlib_mini.js'></script>
<script language='JavaScript' src='../JavaScript/ol/overlib_anchor_mini.js'></script>
<script language='JavaScript' src='../JavaScript/ol/overlib_centerpopup_mini.js'></script>
<script language='JavaScript' src='../JavaScript/ol/overlib_hideform_mini.js'></script>


<jsp:useBean id="YC_BEAN" class="com.cannontech.yc.bean.YCBean" scope="session"/>
<%-- Grab the search criteria --%>

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
	
	int invNo = -1;	//used for directing to different application starting points
	int deviceID = 0;
	if( request.getParameter("deviceID") != null)
	{
		deviceID = Integer.parseInt(request.getParameter("deviceID"));
	}
	else
	{
		deviceID = YC_BEAN.getDeviceID();
	}

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = PAOFuncs.getLiteYukonPAO(deviceID);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<link rel="stylesheet" href="../WebConfig/yukon/Base.css" type="text/css">
</head>
<body class="Background" leftmargin="0" topmargin="0">
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:200;"></div>
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
				  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
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
            <%if( manual) redirect = redirect + "&manual";%>
            <%if( lp ) redirect = redirect + "&lp";%>
            <% String referrer = request.getRequestURI()+ "?deviceID=" + deviceID;%>            
            <% String pageName = "CommandDevice.jsp?deviceID=" + deviceID;%>
			<table width="101" border="0" cellspacing="0" cellpadding="5">
			  <tr> 
			    <td> 
			      <div align="left">
				    <span class="NavHeader">Devices</span>
			        <table width="100%" border="0" cellspacing="0" cellpadding="0">
			          <tr> 
			            <td width="10"></td>
			            <td style="padding:1"><a href='SelectDevice.jsp' class='Link2'><span class='NavText'>Back to List</span></a></td>
			          </tr>
  					  <tr><td height="3"></td></tr>
			          <% for (int i = 0; i < YC_BEAN.getDeviceIDs().size(); i++)
			          {
			          	int id = ((Integer)YC_BEAN.getDeviceIDs().get(i)).intValue();%>
			          <tr>
					  	<% if (id == deviceID) {%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED, "Bullet.gif")%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'><%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(id)%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=id%>' class='Link2'><span class='NavText'><%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(id)%></span></a></td>
						<%}%>						
			          </tr>
					  <tr><td height="3"></td></tr>
			          <%}%>
			        </table>
			      </div>
			    </td>
			  </tr>
			</table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
<%
			if( lp )
			{%>
				<%@ include file="AdvancedCommander410.jsp"%>
			<%}
			else if (liteYukonPao.getType() == com.cannontech.database.data.pao.DeviceTypes.MCT410IL && !manual)
			{
			%>
				<%@ include file="Commander410.jsp"%>
			<%}
			else
			{%>
	 			<%@ include file="Commander.jsp"%>
			<%}%>
			
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
