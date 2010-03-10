<%@ page errorPage="/internalError.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<cti:outputDoctype levels="${info.htmlLevel}, strict"/>
<html>
    <head>
        <title>${pageDetail.pageTitle}</title>           
        <!-- Layout CSS files -->
        <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/extjs/resources/css/reset.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/BaseStyles.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/extjs/resources/css/ext-all.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/extjs/resources/css/xtheme-gray.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/CannonStyle.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/StandardStyles.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>" >
        
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

<div id="MainContainer" class="${showContextualNavigation ? "StandardWithNavLayout" : "StandardLayout"}">

<div id="ContentWrapper">
<div id="Content">
<c:if test="${not showContextualNavigation and not empty pageDetail.pageHeading}">
    <h2 class="standardPageHeading">${requestScope['com.cannontech.web.layout.part.headingFavorites']} ${pageDetail.pageHeading}</h2>
</c:if>
<cti:outputContent writable="${bodyContent}"/>
</div> <!-- Content -->
</div> <!-- ContentWrapper -->

<!-- Start Left -->
<c:if test="${showContextualNavigation}">
<div id="LeftColumn">
<div class="innertube">
<div id="detailAdditionalInfo">
<c:if test="${not empty pageDetail.pageHeading}">
    <h2 class="standardPageHeading">${requestScope['com.cannontech.web.layout.part.headingFavorites']} ${pageDetail.pageHeading}</h2>
</c:if>
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
<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">module=${info.moduleName}, page=${info.pageName} |</cti:checkGlobalRolesAndProperties>
<cti:msg key="yukon.web.layout.standard.yukonVersion" arguments="${yukonVersion}"/> | 
<cti:msg key="yukon.web.layout.standard.copyright"/> | 
Generated at <cti:formatDate type="FULL" value="${currentTime}"/>
</div>
</div>

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

</body>
</html>