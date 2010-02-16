<%@ page errorPage="/internalError.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<cti:outputDoctype levels="${info.htmlLevel}, strict"/>
<html>
    <head>
        <title><c:out value="${title}"/></title>           
        <!-- Layout CSS files -->
        <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/extjs/resources/css/reset.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/BaseStyles.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/extjs/resources/css/ext-all.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/extjs/resources/css/xtheme-gray.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/CannonStyle.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/StandardStyles.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/dropdown.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/dropdown_vimeoStyle.css"/>" >
        
        <!-- Module CSS files from module_config.xml -->
        <c:forEach items="${moduleConfigCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Individual files from includeCss tag on the request page -->
        <c:forEach items="${innerContentCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Login Group specific style sheets (WebClientRole.STD_PAGE_STYLE_SHEET)-->
        <c:forEach items="${loginGroupCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Consolidated Script Files -->
        <c:forEach items="${javaScriptFiles}" var="file"><script type="text/javascript" src="<cti:url value="${file}"/>"></script>
        </c:forEach>
        
    </head>
<body class="<c:out value="${module.moduleName}"/>_module">
<div id="Header">
    <div class="stdhdr_left"><div id="TopLeftLogo"></div><div id="TopLeftLogo2"></div></div>
    <div class="stdhdr_right"><img src="<cti:theme key="yukon.web.layout.standard.upperrightlogo" default="/WebConfig/yukon/YukonBW.gif" url="true"/>" alt=""></div>
    <div class="stdhdr_clear"></div>
</div>
<cti:outputContent writable="${menuRenderer}"/>


				
<c:if test="${empty leftSideContextualMenuOptionsProducer}">
			
	<c:if test="${not empty heading}">
	    <h2 class="standardPageHeading">${requestScope['com.cannontech.web.layout.part.headingFavorites']} ${heading}</h2>
	</c:if>

	<div id="Content">
		<c:if test="${empty standardPageActionsSelectMenu}">
	   		&nbsp;<cti:outputContent writable="${standardPageActionsSelectMenu}"/>
	    </c:if>
		    
		<cti:outputContent writable="${bodyContent}"/>
	</div> <!-- Content -->

</c:if>

<c:if test="${not empty leftSideContextualMenuOptionsProducer}">
		   		
	<table class="standardPageContent">
	
		<tr>
			<td class="leftSideContextualMenuTopper">
			
				<div style="font-weight:bold;font-size: 12px;">
				
					Account: #1261236
				
					<div style="padding-top:2px;font-weight:normal;padding-left:5px;font-size:10px;">
						Pixel Spacebag<br>
						MKPPK, Inc.<br>
						555-123-4567 (H)<br>
						612-867-5309 (W)<br>
						250 5th St E<br>
						Apt #999<br>
						Saint Paul, MN 55101
					</div>
					
				</div>
				
			</td>
		
			<td rowspan="2">
				<div id="Content">
					
					<c:if test="${not empty heading}">
					    <h2 class="standardPageHeading">${requestScope['com.cannontech.web.layout.part.headingFavorites']} ${heading}</h2>
					</c:if>
				
					<cti:outputContent writable="${bodyContent}"/>
				</div>
			</td>
		</tr>
		
		<tr>
			<td class="leftSideContextualMenu">
			
				<ul>
					<c:forEach var="menuOption" items="${leftSideContextualMenuOptionsProducer.menuOptions}">
					
						<li <c:if test="${menuOption.id == leftSideContextualMenuOptionsProducer.selectedMenuId}">class="selected"</c:if>>
							<span><a href="${menuOption.url}"><cti:msg key="${menuOption.menuText}"/></a></span>
						</li>
					
					</c:forEach>
				</ul>
			
			</td>
		
		</tr>
		
		<tr>
		
			<td class="leftSideContextualMenuBottom">
				&nbsp;
			</td>
		
			<td>
			
				<cti:msg key="yukon.web.alerts.heading" var="alertTitle"/>
				<ct:simplePopup title="${alertTitle}" id="alertContent" onClose="javascript:alert_closeAlertWindow();">
				    <div id="alertBody"></div>
				    <div style="padding-top: 5px">
				    <table cellspacing="0" width="100%" >
				        <tr>
				            <td align="left"><ct:stickyCheckbox id="alert_autoPopup" defaultValue="false"><cti:msg key="yukon.web.alerts.autopopup"/></ct:stickyCheckbox></td>
				            <td align="right"><input type="button" value="<cti:msg key="yukon.web.alerts.clearall"/>" onclick="javascript:alert_clearAlert();"></td>
				        </tr>
				    </table>
				    </div>
				
				</ct:simplePopup>
				<ct:dataUpdateEnabler/>
				<cti:dataUpdaterCallback function="alert_handleCountUpdate" initialize="true" count="ALERT/COUNT" lastId="ALERT/LASTID"/>

			</td>
		</tr>
		
	</table>
	
</c:if>

<div id="CopyRight">
	<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">module=${info.moduleName}, page=${info.pageName} |</cti:checkGlobalRolesAndProperties>
	<cti:msg key="yukon.web.layout.standard.yukonVersion" arguments="${yukonVersion}"/> | 
	<cti:msg key="yukon.web.layout.standard.copyright"/> | 
	Generated at <cti:formatDate type="FULL" value="${currentTime}"/>
</div>



</body>
</html>