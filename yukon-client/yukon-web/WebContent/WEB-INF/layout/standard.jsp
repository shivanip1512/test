<%@ page errorPage="/internalError.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<cti:outputDoctype levels="${info.htmlLevel}, strict"/>
<html>
    <head>
        <title>${pageDetail.pageTitle}</title>           
        <!-- Layout CSS files -->
        <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/reset.css"/>" >
        
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/CannonStyle.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/StandardStyles.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/yukonUIToolkit/yukonUiToolkit.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/lib/jQuery/yukon/jquery-ui-1.8.16.custom.css"/>" >
        
        
        <%-- Include functional-overrides.css last so that, you know, they actually override.  cascade! --%>
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/functional-overrides.css"/>" >

        <!-- Module CSS files from module_config.xml -->
        <c:forEach items="${moduleConfigCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Individual files from includeCss tag on the request page -->
        <c:forEach items="${innerContentCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Login Group specific style sheets (WebClientRole.STD_PAGE_STYLE_SHEET)-->
        <c:forEach items="${loginGroupCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Some prerequisite globals -->
        <script type="text/javascript">
        var YG = {
                PHONE: {
                    FORMATS: <cti:msg2 key="yukon.common.phoneNumberFormatting.formats"/>
                }
        };
        </script>
        <!-- Consolidated Script Files -->
        <c:forEach items="${javaScriptFiles}" var="file"><script type="text/javascript" src="<cti:url value="${file}"/>"></script>
        </c:forEach>
    </head>
<body class="<c:out value="${module.moduleName}"/>_module">

<div id="modal_glass" style="display:none;">
    <div class="tint"></div>
    <div class="loading">
        <!-- The lookup for your own custom message would be: $$("#modal_glass .message")[0] -->
        <div class="box message">
            <cti:msg key="yukon.web.components.pageloading"/>
        </div>
    </div>
</div>

<div id="Header">
    <div class="stdhdr_left"><div id="TopLeftLogo"></div><div id="TopLeftLogo2"></div></div>
    <div class="stdhdr_right"><img src="<cti:theme key="yukon.web.layout.standard.upperrightlogo" default="/WebConfig/yukon/YukonBW.gif" url="true"/>" alt=""></div>
    <div class="stdhdr_clear"></div>
</div>
<cti:outputContent writable="${menuRenderer}"/>

<tags:busyBox/><div id="MainContainer" class="${showContextualNavigation ? "StandardWithNavLayout" : "StandardLayout"}">

<div id="ContentWrapper">
<div id="Content">
<c:if test="${not empty pageDetail.pageHeading}">
    <h2 class="standardPageHeading">
    	${requestScope['com.cannontech.web.layout.part.headingPrefix']}
    	<spring:escapeBody htmlEscape="true">
    	${pageDetail.pageHeading}
    	</spring:escapeBody>
    	${requestScope['com.cannontech.web.layout.part.headingSuffix']}
    </h2>
</c:if>

<%-- FLASH SCOPE MESSAGES --%>
<cti:flashScopeMessages/>

<cti:outputContent writable="${bodyContent}"/>
</div> <!-- Content -->
</div> <!-- ContentWrapper -->

<!-- Start Left -->
<c:if test="${showContextualNavigation}">
<div id="LeftColumn">
<div class="innertube">
<div id="detailAdditionalInfo">
<div id="detailAdditionalInfoBlock">
<jsp:include page="${pageDetail.detailInfoIncludePath}"/>
</div>
</div>
<div class="contextualMenu">
<cti:outputContent writable="${contextualNavigationMenu}"/>
</div>
</div>

</div>
</c:if>
<!-- End Left -->

<div id="CopyRight">
    <ul class="pipes">
        <li>
            <cti:msg key="yukon.web.layout.standard.copyright"/>
        </li>
    </ul>
    <br>
    <ul class="pipes">
        <li>
            <cti:msg key="yukon.web.layout.standard.yukonVersion" arguments="${yukonVersion}"/>
        </li>
        <li>
        	<cti:msg key="yukon.web.layout.standard.generatedAt" /><cti:formatDate type="FULL" value="${currentTime}"/>
        </li>
        
        <c:if test="${not empty energyCompanyName}">
        	<li>
            	<cti:msg key="yukon.web.layout.standard.energyCompany" arguments="${energyCompanyName}"/>
        	</li>
        </c:if>
       	
       	<li>
        	<cti:msg key="yukon.web.layout.standard.username" arguments="${username}"/>
    	</li>
    </ul>
    <br>
    <ul class="pipes">
    	<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">
            <li>
                <cti:msg key="yukon.web.layout.standard.moduleName" arguments="${info.moduleName}" />, <cti:msg key="yukon.web.layout.standard.pageName" arguments="${info.pageName}" />
            </li>
        </cti:checkGlobalRolesAndProperties>
        <cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
            <li>
                <cti:msg key="yukon.web.layout.standard.buildInfo" arguments="${buildInfo}"/>
            </li>
        </cti:checkGlobalRolesAndProperties>
    </ul>
</div>
</div>

<cti:msg key="yukon.web.alerts.heading" var="alertTitle"/>
<tags:simplePopup title="${alertTitle}" id="alertContent" onClose="javascript:alert_closeAlertWindow();">
    <div id="alertBody"></div>
    <div style="padding-top: 5px">
    <table cellspacing="0" width="100%" >
        <tr>
            <td align="left"><tags:stickyCheckbox id="alert_autoPopup" defaultValue="false"><cti:msg key="yukon.web.alerts.autopopup"/></tags:stickyCheckbox></td>
            <td align="right"><input type="button" value="<cti:msg key="yukon.web.alerts.clearall"/>" onclick="javascript:alert_clearAlert();"></td>
        </tr>
    </table>
    </div>

</tags:simplePopup>
<tags:updatedWarning/>
<tags:dataUpdateEnabler/>
<cti:dataUpdaterCallback function="alert_handleCountUpdate" initialize="true" count="ALERT/COUNT" lastId="ALERT/LASTID"/>

<tags:analyticsTrackPage/>

</body>
</html>