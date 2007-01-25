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
	
	boolean manual = false;
	if( request.getParameter("manual") != null) {	//Force going to the Commander page instead of a page based on the DeviceType
		manual = true;
	}
	boolean lp = false;
	if( request.getParameter("lp") != null) {	//Force going to the Load Profile
		lp = true;
	}
	boolean isMCT410 = liteYukonPao!=null && com.cannontech.database.data.device.DeviceTypesFuncs.isMCT410(liteYukonPao.getType());
	if( !isMCT410 ) {	//MUST BE Manual...force it
		manual = true;
	}
		
	Vector serialNumbers;
	String serialNum = "";
	String serialType = "";
	if( request.getParameter("xcom") != null){
		serialNum = request.getParameter("xcom");
		serialType = "xcom";
	} else if( request.getParameter("vcom") != null){
		serialNum = request.getParameter("vcom");
		serialType = "vcom";
	} else if( request.getParameter("sa205") != null){
		serialNum = request.getParameter("sa205");
		serialType = "sa205";
	} else if( request.getParameter("sa305") != null){
		serialNum = request.getParameter("sa305");
		serialType = "sa305";
	}
%>

<cti:standardPage title="Energy Services Operations Center" module="commander">
	<cti:standardMenu/>

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
    if(manual) {
    	redirect = redirect + "&manual";
    }
    if(lp) {
    	redirect = redirect + "&lp";
    }
    String referrer = request.getRequestURI()+ "?deviceID=" + deviceID;
    String pageName = "CommandDevice.jsp?deviceID=" + deviceID;
    if( serialType.length() > 0 ) {
    	redirect = redirect + "&" + serialType + "=" + serialNum;
    	referrer = referrer + "&" + serialType + "=" + serialNum;
    	pageName = pageName + "&" + serialType + "=" + serialNum;
    }
%>
	
	<c:set var="manual" scope="page" value="<%=manual%>"/>
	<c:set var="lp" scope="page" value="<%=lp%>"/>
	<c:set var="deviceId" scope="page" value="<%=deviceID%>"/>
	<c:set var="isMCT410" scope="page" value="<%=isMCT410%>"/>
	<c:set var="serialType" scope="page" value="<%=serialType%>"/>
	
	<div class="mainFull">
	
		<!-- Side menu -->
		<div class="sideMenu">
			<div class="sideMenuSectionHeader">Go To...</div>
			
			<!-- Manual side menu section -->
			<c:choose>
				<c:when test="${manual}">
					<div>
						<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
						<span class="sideMenuTextSelected">Manual</span>
					</div>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${!empty param.InvNo}">
							<c:set var="link" scope="page" value="${pageContext.request.contextPath}/operator/Consumer/CommandInv.jsp?InvNo=${param.InvNo}&manual&command=null"/> 
						</c:when>
						<c:otherwise>
							<c:set var="link" scope="page" value="${pageContext.request.contextPath}/apps/CommandDevice.jsp?deviceID=${deviceId}&manual&command=null"/> 
						</c:otherwise>
					</c:choose>
					<div class="sideMenuLink">
						<a href="${link}" class="Link2">
							<span class="sideMenuText">Manual</span>
						</a>
					</div>
				</c:otherwise>
			</c:choose>

			<!-- MCT410 Custom side menu section -->
			<c:choose>
				<c:when test="${!(lp || manual)}">
					<div>
						<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
						<span class="sideMenuTextSelected">MCT410 Custom</span>
					</div>
				</c:when>
				<c:when test="${isMCT410}">
					<c:choose>
						<c:when test="${!empty param.InvNo}">
							<c:set var="link" scope="page" value="${pageContext.request.contextPath}/operator/Consumer/CommandInv.jsp?InvNo=${param.InvNo}&command=null"/> 
						</c:when>
						<c:otherwise>
							<c:set var="link" scope="page" value="${pageContext.request.contextPath}/apps/CommandDevice.jsp?deviceID=${deviceId}&command=null"/> 
						</c:otherwise>
					</c:choose>
					<div class="sideMenuLink">
						<a href="${link}" class="Link2">
							<span class="sideMenuText">MCT410 Custom</span>
						</a>
					</div>
				</c:when>
				<c:otherwise>
					<div>
						<span class="sideMenuTextDisabled">MCT410 Custom</span>
					</div>
				</c:otherwise>
			</c:choose>
			<!-- MCT410 Profile side menu section -->
			<c:choose>
				<c:when test="${lp}">
					<div>
						<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
						<span class="sideMenuTextSelected">MCT410 Profile</span>
					</div>
				</c:when>
				<c:when test="${isMCT410}">
					<c:choose>
						<c:when test="${!empty param.InvNo}">
							<c:set var="link" scope="page" value="${pageContext.request.contextPath}/operator/Consumer/CommandInv.jsp?InvNo=${param.InvNo}&lp"/> 
						</c:when>
						<c:otherwise>
							<c:set var="link" scope="page" value="${pageContext.request.contextPath}/apps/CommandDevice.jsp?deviceID=${deviceId}&lp"/> 
						</c:otherwise>
					</c:choose>
					<div class="sideMenuLink">
						<a href="${link}" class="Link2">
							<span class="sideMenuText">MCT410 Profile</span>
						</a>
					</div>
				</c:when>
				<c:otherwise>
					<div>
						<span class="sideMenuTextDisabled">MCT410 Profile</span>
					</div>
				</c:otherwise>
			</c:choose>
			
			<!-- Select Device link -->
			<div class="sideMenuLink">
				<a href="SelectDevice.jsp" class="Link2">
					<span class="sideMenuText">Select Device</span>
				</a>
			</div>
		
			<!-- Devices side menu section -->
			<div class="sideMenuSectionHeader">Devices</div>
			
			<c:forEach items="${YC_BEAN.liteDevices}" var="device">
				<c:if test="${cti:isDeviceSortByGroup(device)}">
					<c:choose>
						<c:when test="${deviceId == device.yukonID}">
							<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
							<span class="sideMenuTextSelected">${device.paoName}</span>
						</c:when>
						<c:otherwise>
							<div class="sideMenuDevice">
								<a href="CommandDevice.jsp?deviceID=${device.yukonID}${manual ? '&manual' : ''}" class="Link2">
									<span class="sideMenuText">${device.paoName}</span>
								</a>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
				<br/>
			</c:forEach>
		
			<!-- Load Management menu section -->
			<div class="sideMenuSectionHeader">Load Management</div>
			
			<c:forEach items="${YC_BEAN.liteDevices}" var="device">
				<c:if test="${cti:isLoadManagementSortByGroup(device)}">
					<c:choose>
						<c:when test="${deviceId == device.yukonID}">
							<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
							<span class="sideMenuTextSelected">${device.paoName}</span>
						</c:when>
						<c:otherwise>
							<a href="CommandDevice.jsp?deviceID=${device.yukonID}${manual ? '&manual' : ''}" class="Link2">
								<span class="sideMenuText">${device.paoName}</span>
							</a>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			
			<!-- DCU-205 Serial section -->
			<cti:checkProperty property="CommanderRole.DCU_SA205_SERIAL_MODEL">
				<c:choose>
					<c:when test="${serialType == 'sa205'}">
						<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
						<span class="sideMenuTextSelected">DCU-205 Serial</span>
					</c:when>
					<c:otherwise>
						<a href="CommandDevice.jsp?deviceID=${device.yukonID}&manual&sa205" class="Link2">
							<span class="sideMenuText">DCU-205 Serial</span>
						</a>
					</c:otherwise>
				</c:choose>
<%
	serialNumbers = YC_BEAN.getSerialNumbers(CommandCategory.STRING_CMD_SA205_SERIAL); 
  	if(serialNumbers != null) {
		for (int i = 0; i < serialNumbers.size(); i++) {
			String sn = (String)serialNumbers.get(i);
			if (serialType.equals("sa205") && sn.equals(serialNum)) {
%>
				<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
				<span class="sideMenuTextSelected"><%=sn%></span>
<%
			} else {
%>
				<div class="sideMenuLink">
					<a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa205=<%=sn%>" class="Link2">
						<span class="sideMenuText"><%=sn%></span>
					</a>
				</div>
<%				
			}
		}
	}
%>
			</cti:checkProperty>
			
			<!-- DCU-305 Serial section -->
			<cti:checkProperty property="CommanderRole.DCU_SA305_SERIAL_MODEL">
				<c:choose>
					<c:when test="${serialType == 'sa305'}">
						<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
						<span class="sideMenuTextSelected">DCU-305 Serial</span>
					</c:when>
					<c:otherwise>
						<a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>}&manual&sa305" class="Link2">
							<span class="sideMenuText">DCU-305 Serial</span>
						</a>
					</c:otherwise>
				</c:choose>
		
<%
	serialNumbers = YC_BEAN.getSerialNumbers(CommandCategory.STRING_CMD_SA305_SERIAL); 
  	if(serialNumbers != null) {
  		for (int i = 0; i < serialNumbers.size(); i++) {
			String sn = (String)serialNumbers.get(i);
			if (serialType.equals("sa305") && sn.equals(serialNum)) {
%>
				<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
				<span class="sideMenuTextSelected"><%=sn%></span>
<%
			} else {
%>
				<div class="sideMenuLink">
					<a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa305=<%=sn%>" class="Link2">
						<span class="sideMenuText"><%=sn%></span>
					</a>
				</div>
<%				
			}
		}
	}
%>
			</cti:checkProperty>
				
			<!-- Expresscom Serial section -->
			<cti:checkProperty property="CommanderRole.EXPRESSCOM_SERIAL_MODEL">
<% 
	if (serialType.equals("xcom") ) {
%>
				<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
				<span class="sideMenuTextSelected">Expresscom Serial</span>
<%
	} else {
%>
				<div class="sideMenuLink">
			        <a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&xcom" class="Link2">
			        	<span class="sideMenuText">Expresscom Serial</span>
			        </a>
			    </div>
<%
	}
	serialNumbers = YC_BEAN.getSerialNumbers(CommandCategory.STRING_CMD_EXPRESSCOM_SERIAL); 
	if(serialNumbers != null) {
  		for (int i = 0; i < serialNumbers.size(); i++) {
			String sn = (String)serialNumbers.get(i);
			if (serialType.equals("xcom") && sn.equals(serialNum)) {
%>
				<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
				<span class="sideMenuTextSelected"><%=sn%></span>
<%
			} else {
%>
				<div class="sideMenuLink">
					<a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&xcom=<%=sn%>" class="Link2">
						<span class="sideMenuText"><%=sn%></span>
					</a>
				</div>
<%
			}
		}
	}
%>
			</cti:checkProperty>
		
			<!-- Versacom Serial section -->
			<cti:checkProperty property="CommanderRole.VERSACOM_SERIAL_MODEL">
		
<% 
	if (serialType.equals("vcom") ) {
%>
				<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
				<span class="sideMenuTextSelected">Versacom Serial</span>
<%
	} else {
%>
				<div class="sideMenuLink">
			        <a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&vcom" class="Link2">
			        	<span class="sideMenuText">Versacom Serial</span>
			        </a>
			    </div>
<%
	}
	serialNumbers = YC_BEAN.getSerialNumbers(CommandCategory.STRING_CMD_VERSACOM_SERIAL); 
  	if(serialNumbers != null) {
  		for (int i = 0; i < serialNumbers.size(); i++) {
			String sn = (String)serialNumbers.get(i);
			if (serialType.equals("vcom") && sn.equals( serialNum)) {
%>
				<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
				<span class="sideMenuTextSelected"><%=sn%></span>
<%
			} else {
%>
				<div class="sideMenuLink">
			        <a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&vcom=<%=sn%>" class="Link2">
			        	<span class="sideMenuText"><%=sn%></span>
			        </a>
			    </div>
<%
			}
		}
	}
%>
			</cti:checkProperty>
		
			<!-- Cap Control menu section -->
			<div class="sideMenuSectionHeader">Cap Control</div>
			
			<c:forEach items="${YC_BEAN.liteDevices}" var="device">
				<c:if test="${cti:isCapControlSortByGroup(device)}">
					<c:choose>
						<c:when test="${deviceId == device.yukonID}">
							<img src="../WebConfig/<cti:getProperty property="WebClientRole.NAV_BULLET_SELECTED"/>" width="9" height="9">
							<span class="sideMenuTextSelected">${device.paoName}</span>
						</c:when>
						<c:otherwise>
							<div class="sideMenuLink">
								<a href="CommandDevice.jsp?deviceID=${device.yukonID}${manual ? '&manual' : ''}" class="Link2">
									<span class="sideMenuText">${device.paoName}</span>
								</a>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
				<br/>
			</c:forEach>
			
		</div>
	
	
		<div class="commandInclude">
		<table>
			<tr>
			<c:choose>
				<c:when test="${lp}">
					<%@ include file="AdvancedCommander410.jspf"%>
				</c:when>
				<c:when test="${isMCT410 && !manual}">
					<%@ include file="Commander410.jspf"%>
				</c:when>
				<c:otherwise>
					<%@ include file="Commander.jspf"%>
				</c:otherwise>
			</c:choose>
			</tr>
		</table>
		</div>
	</div>
	
	<div style="clear:both"></div>
	
</cti:standardPage>
