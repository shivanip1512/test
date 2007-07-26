<%-- A wrapper file for access to commander with a deviceID.
--%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>
<%@ page import="com.cannontech.message.dispatch.message.PointData"%> 
<%@ page import="com.cannontech.database.data.lite.LiteRawPointHistory"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>
<%@ page import="com.cannontech.database.data.point.PointTypes"%>
<%@ page import="com.cannontech.database.db.point.RawPointHistory"%>
<%@ page import="com.cannontech.database.db.command.CommandCategory"%>
<%@ page import="com.cannontech.roles.application.CommanderRole"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache"%>
<%@ page import="com.cannontech.yc.bean.CommandDeviceBean"%>

<%
    java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");
    java.text.DecimalFormat format_nv3 = new java.text.DecimalFormat("#0.000");
    java.text.DecimalFormat format_nsec = new java.text.DecimalFormat("#0 secs");
    LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
    String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "");
%>

<cti:checklogin/> 

<jsp:useBean id="YC_BEAN" class="com.cannontech.yc.bean.YCBean" scope="session"/>
<jsp:setProperty name="YC_BEAN" property="userID" value="<%= lYukonUser.getUserID()%>"/>

<%-- Grab the search criteria --%>
<%
	PointData pointData = null;

	int invNo = -1;	//used for directing to different application starting points
	int deviceID = PAOGroups.INVALID;
	if( request.getParameter("deviceID") != null)
	{
		deviceID = Integer.parseInt(request.getParameter("deviceID"));
        if( YC_BEAN.getDeviceID() != deviceID) {
            YC_BEAN.setDeviceID(deviceID);
            session.removeAttribute("CustomerDetail"); //delete this for now, we'll figure out a way to store per meter later
            session.removeAttribute("ServLocDetail"); //delete this for now, we'll figure out a way to store per meter later
        }
	} else {
		deviceID = YC_BEAN.getDeviceID();
	}

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = null;
    if (deviceID >= 0)
        liteYukonPao = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
	
	boolean lp = false;
	if( request.getParameter("lp") != null) {	//Force going to the Load Profile
		lp = true;
	}
	boolean isMCT4XX = liteYukonPao!=null && com.cannontech.database.data.device.DeviceTypesFuncs.isMCT4XX(liteYukonPao.getType());
		
%>

<cti:standardPage title="Energy Services Operations Center" module="amr">
	<cti:standardMenu/>
    <cti:includeCss link="/WebConfig/yukon/styles/commanderStyles.css"/>
    <cti:includeCss link="/WebConfig/yukon/Base.css"/>
  

	<script  language="JavaScript" src="../JavaScript/calendar.js"></script>
	<script language="JavaScript">
		function setMspCommand(cmd)
		{
		    document.mspCommandForm.command.value = cmd;
		    document.mspCommandForm.submit();
		}
	</script>
	
<% 
	//"redirect" is required by Commander.jsp and for the sake of this wrapper being able to know the deviceID
    String redirect = request.getRequestURI()+ "?deviceID=" + deviceID;
    if(lp) {
    	redirect = redirect + "&lp";
    }
    String referrer = request.getRequestURI()+ "?deviceID=" + deviceID;
%>
	
	<c:set var="lp" scope="page" value="<%=lp%>"/>
	<c:set var="deviceId" scope="page" value="<%=deviceID%>"/>
	<c:set var="isMCT4XX" scope="page" value="<%=isMCT4XX%>"/>
	
		
			<c:choose>
				<c:when test="${lp}">
					<%@ include file="AdvancedCommander410.jspf"%>
				</c:when>
				<c:when test="${isMCT4XX}">
					<%@ include file="Commander410.jspf"%>
				</c:when>
			</c:choose>
		
	
</cti:standardPage>
