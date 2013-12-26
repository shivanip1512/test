<%@ page errorPage="/internalError.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<!--[if lt IE 7 ]>  <html dir="ltr" class="no-js ie6 ltie7 ltie8 ltie9 ltie10 ltie11"> <![endif]-->
<!--[if IE 7 ]>     <html dir="ltr" class="no-js ie7 ltie8 ltie9 ltie10 ltie11"> <![endif]-->
<!--[if IE 8 ]>     <html dir="ltr" class="no-js ie8 ltie9 ltie10 ltie11"> <![endif]-->
<!--[if IE 9 ]>     <html dir="ltr" class="no-js ie9 ltie10 ltie11"> <![endif]-->
<!--[if IE 10 ]>    <html dir="ltr" class="no-js ie10 ltie11"> <![endif]-->
<!--[if !(IE)]><!--><html dir="ltr" class="no-js"><!--<![endif]-->
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=EDGE" />

        <title>${pageDetail.pageTitle}</title>
        <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/normalize.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/resources/css/lib/animate.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/layout.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/yukon.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/buttons.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/icons.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/lib/jQuery/yukon/jquery-ui-1.9.2.custom.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/lib/jQuery/plugins/tipsy/stylesheets/tipsy.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/lib/jQuery/plugins/spectrum/spectrum.css"/>" >
        
        <c:set var="browser" value="${header['User-Agent']}" scope="session"/>
        
        <%-- Include overrides.css last so that, you know, they actually override.  cascade! --%>
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/overrides.css"/>" >

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
        
        <audio id="alert-audio">
            <source src="/WebConfig/yukon/audio/beep1-shortened.mp3" type="audio/mpeg">
        </audio>
    </head>
<body class="<c:out value="${module.moduleName}"/>_module">
<cti:msgScope paths="layout.standard">

<div id="modal-glass" style="display:none;">
    <div class="tint"></div>
    <div class="loading">
        <div class="box load-message">
            <cti:msg key="yukon.web.components.pageloading"/>
        </div>
    </div>
</div>

<div id="page">
<header class="yukon-header">
    <div class="outer">
        <div class="inner">
            <div class="toolbar">
<%--                 <cti:outputContent writable="${searchRenderer}"/> --%>
                <form accept-charset="ISO-8859-1" enctype="application/x-www-form-urlencoded" method="get" action="/search" class="yukon-search-form">
                    <input type="text" placeholder="<cti:msg2 key='yukon.common.search.placeholder'/>" role="search" name="q" class="search-field">
                </form>
                <cti:button id="yukon-alert-button" data-alert-sound="${alertSounds}" data-alert-flash="${alertFlash}" classes="action dn" label="0"/>
                <cm:dropdown containerCssClass="b-user-menu fl" icon="icon-user" label="${fn:escapeXml(displayName)}" type="button">
                    <cti:checkRolesAndProperties value="ADMIN_SUPER_USER">
                        <cti:url value="/adminSetup/user/view?userId=${sessionScope.YUKON_USER.userID}" var="userEditUrl"/>
                        <cm:dropdownOption icon="icon-user-edit" href="${userEditUrl}">User Editor</cm:dropdownOption>
                    </cti:checkRolesAndProperties>
                    <cti:url value="/user/profile" var="userProfileUrl"/>
                    <cm:dropdownOption icon="icon-user" href="${userProfileUrl}"><cti:msg2 key="yukon.web.components.button.profile.label"/></cm:dropdownOption>
                    <cti:url value="/servlet/LoginController?ACTION=LOGOUT" var="logoutUrl"/>
                    <cm:dropdownOption icon="icon-door-open" href="${logoutUrl}"><cti:msg2 key="yukon.web.components.button.logout.label"/></cm:dropdownOption>
                </cm:dropdown>
            </div>
        </div>
    </div>
    <div class="nav">
        <div class="navOut">
            <nav role="navigation">
                <ul>
                    <li class="logo">
                        <a href="/home"></a>
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
                <c:if test="${canFavorite}">
                    <cti:button id="favButton" 
                        classes="b-favorite" 
                        renderMode="image" 
                        icon="icon-favorite-not" 
                        data-module="${module.moduleName}" 
                        data-name="${info.pageName}" 
                        data-label-args="${labelArgs}" />
                </c:if>
			    <h1 class="page-heading">
			        ${requestScope['com.cannontech.web.layout.part.headingPrefix']}
			        <spring:escapeBody htmlEscape="true">
			        ${pageDetail.pageHeading}
			        </spring:escapeBody>
			        ${requestScope['com.cannontech.web.layout.part.headingSuffix']}
			    </h1>
			</c:if>
            <div class="page-actions">
                <cm:dropdown id="b-page-actions" type="button" containerCssClass="fr dn"/>
                <cti:button id="b-search-results" classes="fr dn" nameKey="searchResults" renderMode="buttonImage" icon="icon-resultset-first-grey"/>
            </div>
		</div>
    </div>
</header>

<section id="content" role="main">

<c:set var="layout" value="${showContextualNavigation ? 'column-4-20' : 'column-24'}"/>
<c:set var="columnNum" value="${showContextualNavigation ? 'two nogutter' : 'one nogutter'}"/>
<div class="${layout} clearfix" style="margin-bottom: 10px;">

<!-- Start Contextual Nav -->
<c:if test="${showContextualNavigation}">
    <div id="LeftColumn" class="column one">
        <jsp:include page="${pageDetail.detailInfoIncludePath}" />
        <div class="contextual-menu vertical-menu">
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
        <div class="page-error">
            <cti:msg2 key="yukon.web.error.noJs"/>
        </div>
    </noscript>
    
    <cti:outputContent writable="${bodyContent}"/>
</div>
</div>

<cti:msg2 key="yukon.web.alerts.heading" var="alertTitle"/>
<tags:simplePopup title="${alertTitle}" id="yukon_alert_popup">
    <div id="alert_body" class="scroll-large"></div>
    <div class="action-area">
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
        <nav>
            <ul>
                <li><a href="/support"><i:inline key=".support"/></a></li>
                <li><a href="/sitemap"><i:inline key=".siteMap"/></a></li>
                <cti:checkRolesAndProperties value="JAVA_WEB_START_LAUNCHER_ENABLED">
                    <li><a href="javascript:void(0);" id="appsLauncher"><i:inline key=".applications"/></a></li>
                    <dialog:inline id="yukonApplicationDialog" okEvent="none" nameKey="applications" on="#appsLauncher"
                        options="{width: 400, 'buttons': [], 'position' : 'relative'}">
                        <c:import url="/jws/applications"/>
                    </dialog:inline>
                </cti:checkRolesAndProperties>
                <cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
                    <li><a href="/support/development/main">Development</a></li>
                </cti:checkGlobalRolesAndProperties>
                <c:if test="${showNM}">
                    <li><a href="${nmUrl}" target="_blank"><i:inline key="yukon.common.networkManager"/></a></li>
                </c:if>
            </ul>
        </nav>
    </div>
    <div class="footer">
        <div class="content clearfix">
            <div class="left">
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
                            <li><cti:msg2 key=".buildInfo" arguments="${buildInfo}" htmlEscapeArguments="false"/></li>
                            <li>${servletPath}</li>
                        </ul>
                    </cti:checkGlobalRolesAndProperties>
                </div>
            </div>
            <div class="right">
                <div class="branding"><a class="footer-logo" href="/home"></a></div>
            </div>
        </div>
    </div>
</footer>
   
</div>
</cti:msgScope>
</body>
</html>