<%-- A wrapper file for access to commander with a deviceID.
--%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>
<%@ page import="com.cannontech.core.dynamic.PointValueHolder"%> 
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

<jsp:useBean id="YC_BEAN" class="com.cannontech.yc.bean.YCBean" scope="session"/>
<jsp:useBean id="commandDeviceBean" class="com.cannontech.yc.bean.CommandDeviceBean" scope="session"/>
<jsp:setProperty name="YC_BEAN" property="userID" value="<%= lYukonUser.getUserID()%>"/>

<%-- Grab the search criteria --%>
<%
	PointValueHolder pointValueHolder = null;

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
	
	boolean manual = true;
	boolean isMCT4XX = liteYukonPao!=null && com.cannontech.database.data.device.DeviceTypesFuncs.isMCT4XX(liteYukonPao.getType());
	if( !isMCT4XX ) {	//MUST BE Manual...force it
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

<%@ include file="/apps/CommanderMenu.jspf" %>

<cti:standardPage title="Energy Services Operations Center" module="commanderSelect">
	<cti:standardMenu menuSelection="<%= menuSelection %>" />
	
	<script language="JavaScript">
		function setMspCommand(cmd)
		{
		    document.mspCommandForm.command.value = cmd;
		    document.mspCommandForm.submit();
		}
	</script>
    
    <cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
    <cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
	
<% 
	//"redirect" is required by Commander.jsp and for the sake of this wrapper being able to know the deviceID
    String redirect = request.getRequestURI()+ "?deviceID=" + deviceID;
    if(manual) {
    	redirect = redirect + "&manual";
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
	<c:set var="deviceId" scope="page" value="<%=deviceID%>"/>
	<c:set var="isMCT4XX" scope="page" value="<%=isMCT4XX%>"/>
	<c:set var="serialType" scope="page" value="<%=serialType%>"/>
    
    <table border="0">
    
        <tr>
        
        <%-- COMMANDER --%>
        <td>
            <%@ include file="Commander.jspf"%>
        </td>
    
        <%-- SIDE MENU --%>
        <td>
            <div style="width:20px;"></div>
        </td>
        <td valign="top">
	
		<div id="sideMenu">
		
			<div class="commanderHeader">&nbsp;</div>
            
            <div class="titledContainer boxContainer">
            <div class="titleBar boxContainer_titleBar">
                <div class="title boxContainer_title">
                    Navigation
                </div>
            </div>
            
			<div id="commandDevice_content" class="content boxContainer_content">

			<div class="header">Go To...</div>
			
			<!-- Manual side menu section -->
			<c:choose>
				<c:when test="${manual}">
					<div class="sideMenuLink selected">Manual</div>
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
						<a href="${link}" class="Link1">Manual</a>
					</div>
				</c:otherwise>
			</c:choose>

			<c:if test="${isMCT4XX}">
			<div class="sideMenuLink">
						<c:url var="devDetailsUrl" value="/spring/csr/home">
							<c:param name="deviceId" value="${deviceId}" />
						</c:url>
						<a href="${devDetailsUrl}" class="Link1">Device Details</a>
			</div>
			</c:if>
      
			<div class="horizontalRule" ></div>
		
			<!-- Devices side menu section -->
			<div class="header">Devices</div>
			
			<c:forEach items="${YC_BEAN.liteDevices}" var="device">
				<c:if test="${cti:isDeviceSortByGroup(device)}">
					<c:choose>
						<c:when test="${deviceId == device.yukonID}">
							<div class="sideMenuLink selected">${device.paoName}</div>
						</c:when>
						<c:otherwise>
							<div class="sideMenuLink">
								<a href="CommandDevice.jsp?deviceID=${device.yukonID}${manual ? '&manual' : ''}" class="Link1">
									${device.paoName}
								</a>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
		
			<div class="horizontalRule" ></div>
		
			<!-- Load Management menu section -->
			<div class="header">Load Management</div>
			
			<c:forEach items="${YC_BEAN.liteDevices}" var="device">
				<c:if test="${cti:isLoadManagementSortByGroup(device)}">
					<c:choose>
						<c:when test="${deviceId == device.yukonID}">
							<div class="sideMenuLink selected">${device.paoName}</div>
						</c:when>
						<c:otherwise>
							<div class="sideMenuLink">
								<a href="CommandDevice.jsp?deviceID=${device.yukonID}${manual ? '&manual' : ''}" class="Link1">
									${device.paoName}
								</a>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			
			<!-- DCU-205 Serial section -->
			<cti:checkProperty property="CommanderRole.DCU_SA205_SERIAL_MODEL">
				<c:choose>
					<c:when test="${serialType == 'sa205'}">
						<div class="sideMenuLink selected">DCU-205 Serial</div>
					</c:when>
					<c:otherwise>
						<div class="sideMenuLink">					
							<a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa205" class="Link1">
								DCU-205 Serial
							</a>
						</div>
					</c:otherwise>
				</c:choose>
<%
	serialNumbers = YC_BEAN.getSerialNumbers(CommandCategory.STRING_CMD_SA205_SERIAL); 
  	if(serialNumbers != null) {
		for (int i = 0; i < serialNumbers.size(); i++) {
			String sn = (String)serialNumbers.get(i);
			if (serialType.equals("sa205") && sn.equals(serialNum)) {
%>
				<div class="sideMenuLink selected"><%=sn%></div>
<%
			} else {
%>
				<div class="sideMenuLink">
					<a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa205=<%=sn%>" class="Link1">
						<%=sn%>
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
						<div class="sideMenuLink selected">DCU-305 Serial</div>
					</c:when>
					<c:otherwise>
						<div class="sideMenuLink">
							<a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa305" class="Link1">
								DCU-305 Serial
							</a>
						</div>
					</c:otherwise>
				</c:choose>
		
<%
	serialNumbers = YC_BEAN.getSerialNumbers(CommandCategory.STRING_CMD_SA305_SERIAL); 
  	if(serialNumbers != null) {
  		for (int i = 0; i < serialNumbers.size(); i++) {
			String sn = (String)serialNumbers.get(i);
			if (serialType.equals("sa305") && sn.equals(serialNum)) {
%>
				<div class="sideMenuLink selected"><%=sn%></div>
<%
			} else {
%>
				<div class="sideMenuLink">
					<a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa305=<%=sn%>" class="Link1">
						<%=sn%>
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
				<div class="sideMenuLink selected">Expresscom Serial</div>
<%
	} else {
%>
				<div class="sideMenuLink">
			        <a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&xcom" class="Link1">
			        	Expresscom Serial
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
				<div class="sideMenuSubLink selected"><%=sn%></div>
<%
			} else {
%>
				<div class="sideMenuSubLink">
					<a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&xcom=<%=sn%>" class="Link1">
						<%=sn%>
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
				<div class="sideMenuLink selected">Versacom Serial</div>
<%
	} else {
%>
				<div class="sideMenuLink">
			        <a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&vcom" class="Link1">
			        	Versacom Serial
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
				<div class="sideMenuSubLink selected"><%=sn%></div>
<%
			} else {
%>
				<div class="sideMenuSubLink selected">
			        <a href="CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&vcom=<%=sn%>" class="Link1">
			        	<%=sn%>
			        </a>
			    </div>
<%
			}
		}
	}
%>
			</cti:checkProperty>
			
			<div class="horizontalRule" ></div>
		
			<!-- Cap Control menu section -->
			<div class="header">Cap Control</div>
			
			<c:forEach items="${YC_BEAN.liteDevices}" var="device">
				<c:if test="${cti:isCapControlSortByGroup(device)}">
					<c:choose>
						<c:when test="${deviceId == device.yukonID}">
							<div class="sideMenuLink selected">${device.paoName}</div>
						</c:when>
						<c:otherwise>
							<div class="sideMenuLink">
								<a href="CommandDevice.jsp?deviceID=${device.yukonID}${manual ? '&manual' : ''}" class="Link1">
									${device.paoName}
								</a>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			
			</div>    
            </div>
			
		</div>
        
        </td>
        </tr>
        </table>
	
</cti:standardPage>
