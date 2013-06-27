<%@ page errorPage="/internalError.jsp" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
        
        <title>${pageDetail.pageTitle}</title>
        <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/normalize.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/layout.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/yukon.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/buttons/css/buttons.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/Icons/silk/icons.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/lib/jQuery/yukon/jquery-ui-1.9.2.custom.css"/>" >
        
        <c:set var="browser" value="${header['User-Agent']}" scope="session"/>
        
        <%-- Include functional-overrides.css last so that, you know, they actually override.  cascade! --%>
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/functional-overrides.css"/>" >

        <!-- Module CSS files from module_config.xml -->
        <c:forEach items="${moduleConfigCss}" var="file">
            <link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Individual files from includeCss tag on the request page -->
        <c:forEach items="${innerContentCss}" var="file">
            <link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Login Group specific style sheets (YukonRoleProperty.STD_PAGE_STYLE_SHEET)-->
        <c:forEach items="${loginGroupCss}" var="file">
            <link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
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
        <c:forEach items="${javaScriptFiles}" var="file">
            <script type="text/javascript" src="<cti:url value="${file}"/>"></script>
        </c:forEach>
    </head>
<body class="<c:out value="${module.moduleName}"/>_module">
<cti:msgScope paths="layout.standard">

<div id="modal_glass" style="display:none;">
    <div class="tint"></div>
    <div class="loading">
        <!-- The lookup for your own custom message would be: $$("#modal_glass .message")[0] -->
        <div class="box message">
            <cti:msg key="yukon.web.components.pageloading"/>
        </div>
    </div>
</div>

<div id="page">
<header class="yukon-header">
    <div class="outer">
        <div class="inner">
            <div class="toolbar">
                <button id="yukon_alert_button" class="action red"><span class="label"></span></button>
                <div class="dropdown">
                    <a href="#" class="button b-user-menu"><i class="icon icon-user"></i><span class="label">${fn:escapeXml(displayName)}</span><span class="toggle"></span></a>
                    <div class="dropdown-slider">
                        <cti:checkRolesAndProperties value="ADMIN_SUPER_USER">
                            <a href="<cti:url value="/adminSetup/user/view?userId=${sessionScope.YUKON_USER.userID}"/>" class="ddm"><i class="icon icon-user-edit"></i><span class="label">User Editor</span></a>
                        </cti:checkRolesAndProperties>
                        <a href="<cti:url value="/user/profile"/>" class="ddm"><i class="icon icon-user"></i><span class="label"><cti:msg2 key="yukon.web.components.button.profile.label"/></span></a>
                        <a href="<cti:url value="/servlet/LoginController?ACTION=LOGOUT"/>" class="ddm"><i class="icon icon-door-open"></i><span class="label"><cti:msg2 key="yukon.web.components.button.logout.label"/></span></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="nav">
        <div class="navOut">
            <nav role="navigation">
                <ul>
                    <li class="logo">
                        <a href="/home">
                            <img src="<cti:url value="/WebConfig/yukon/layout/eaton_logo.png"/>" alt="Home">
                        </a>
                    </li>
                    <cti:outputContent writable="${menuRenderer}"/>
                </ul>
            </nav>
        </div>
    </div>
    <div class="bc-bar">
        <div class="inner clearfix">
            <cti:outputContent writable="${bcRenderer}"/>
        </div>
    </div>
    <div class="page-title-bar">
        <div class="inner clearfix">
	        <c:if test="${not empty pageDetail.pageHeading}">
			    <h1 class="page-heading">
			        ${requestScope['com.cannontech.web.layout.part.headingPrefix']}
			        <spring:escapeBody htmlEscape="true">
			        ${pageDetail.pageHeading}
			        </spring:escapeBody>
			        ${requestScope['com.cannontech.web.layout.part.headingSuffix']}
			    </h1>
			</c:if>
            <div class="page-actions">
                <button id="b-page-actions" class="fr dn"><i class="icon icon-cog"></i><i class="icon icon-bullet-arrow-down"></i></button>
                <cti:outputContent writable="${searchRenderer}" />
                <button id="b-search-results" class="fr dn"><i class="disabled icon icon-resultset-first"></i></button>
            </div>
		</div>
    </div>
</header>

<section id="content" role="main">

<c:set var="layout" value="${showContextualNavigation ? 'column_4_20' : 'column_24'}"/>
<c:set var="columnNum" value="${showContextualNavigation ? 'two nogutter' : 'one nogutter'}"/>
<div class="${layout} clearfix" style="margin-bottom: 10px;">

<!-- Start Contextual Nav -->
<c:if test="${showContextualNavigation}">
    <div id="LeftColumn" class="column one">
        <jsp:include page="${pageDetail.detailInfoIncludePath}" />
        <div class="contextualMenu vertical_menu">
            <cti:outputContent writable="${contextualNavigationMenu}" />
        </div>
        <div class="bumper"></div>
    </div>
</c:if>
<!-- End Contextual Nav -->

<div class="column ${columnNum} main-container">

<%-- FLASH SCOPE MESSAGES --%>
<cti:flashScopeMessages/>
<noscript>
    <div class="page_error">
        <cti:msg2 key="yukon.web.error.noJs"/>
    </div>
</noscript>

<cti:outputContent writable="${bodyContent}"/>
</div>
</div>

<cti:msg2 key="yukon.web.alerts.heading" var="alertTitle"/>
<tags:simplePopup title="${alertTitle}" id="yukon_alert_popup">
    <div id="alert_body" class="scrollingContainer_large"></div>
    <div class="actionArea">
        <button id="yukon_clear_alerts_button"><span class="label"><cti:msg2 key="yukon.web.alerts.clearall"/></span></button>
    </div>
</tags:simplePopup>
<tags:updatedWarning/>
<tags:dataUpdateEnabler/>
<cti:dataUpdaterCallback function="Yukon.Alerts.countUpdated" initialize="true" count="ALERT/COUNT" lastId="ALERT/LASTID"/>

<tags:analyticsTrackPage/>
<%-- <tags:feedback/> TODO: uncomment when ready for feedback --%>
</section>

<footer id="yukon-footer" class="yukon-footer">
    <div class="utility">
        <div class="footerNav">
            <ul>
                <li><a href="/support"><cti:msg2 key=".support"/></a></li>
                <li><a href="/support/sitemap"><cti:msg2 key=".siteMap"/></a></li>
                <cti:checkRolesAndProperties value="JAVA_WEB_START_LAUNCHER_ENABLED">
                    <li><a href="javascript:void(0);" id=appsLauncher><cti:msg2 key=".applications"/></a></li>
                    <dialog:inline id="yukonApplicationDialog" okEvent="none" nameKey="applications" on="#appsLauncher"
                        options="{width: 400, 'buttons': [], 'position' : 'relative'}">
                        <c:import url="/jws/applications"/>
                    </dialog:inline>
                </cti:checkRolesAndProperties>
                <cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
                    <li><a href="/support/development/uiDemos/main">Development</a></li>
                </cti:checkGlobalRolesAndProperties>
            </ul>
        </div>
    </div>
    <div class="footer">
        <div class="footerNav clearfix">
            <div class="wrapper">
                <div class="legal">
                    <p class="copyright"><cti:msg2 key=".copyright"/></p>
                    <ul>
                        <li><cti:msg2 key=".yukonVersion" arguments="${yukonVersion}"/></li>
                        <li><cti:msg2 key=".generatedAt" /><cti:formatDate type="FULL" value="${currentTime}"/></li>
                        <c:if test="${not empty energyCompanyName}">
                            <li><cti:msg2 key=".energyCompany" arguments="${energyCompanyName}"/></li>
                        </c:if>
                        <li><cti:msg2 key=".username" arguments="${username}"/></li>
                        <cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">
                            <li><cti:msg2 key=".moduleName" arguments="${info.moduleName}" />, <cti:msg2 key=".pageName" arguments="${info.pageName}" /></li>
                        </cti:checkGlobalRolesAndProperties>
                    </ul>
                    <cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
                        <ul>
                            <li><cti:msg2 key=".buildInfo" arguments="${buildInfo}"/></li>
                            <li>${servletPath}</li>
                        </ul>
                    </cti:checkGlobalRolesAndProperties>
                </div>
            </div>
        </div>
    </div>
</footer>
   
</div>
</cti:msgScope>
</body>
</html>