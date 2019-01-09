<%@ page errorPage="/internalError.jsp" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:set var="browser" value="${header['User-Agent']}" scope="session"/>
<c:set var="isIE" value="${fn:contains(browser, 'MSIE') ? 'ie' : 'no-ie'}"/>

<!DOCTYPE html>
<html dir="ltr" class="${module.moduleName}-module ${isIE} no-js">
<head>

<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<meta charset="UTF-8">

<title>${pageDetail.pageTitle}</title>

<link rel="shortcut icon" href="<cti:url value="/resources/favicon.ico"/>" type="image/x-icon">

<%-- Standard CSS Files --%>
<c:forEach items="${standardCssFiles}" var="file">
    <link rel="stylesheet" href="<c:url value="${file}"/>">
</c:forEach>

<%-- Module CSS files from module_config.xml --%>
<c:forEach items="${moduleConfigCss}" var="file">
    <link rel="stylesheet" href="<c:url value="${file}"/>">
</c:forEach>

<%-- Individual files from includeCss tag on the request page --%>
<c:forEach items="${innerContentCss}" var="file">
    <link rel="stylesheet" href="<c:url value="${file}"/>">
</c:forEach>

<%-- Include overrides.css last so that, you know, they actually override.  cascade! --%>
<cti:includeCss link="OVERRIDES" force="true"/>

<%-- Library Script Files --%>
<c:forEach items="${libraryScriptFiles}" var="file">
    <script src="<c:url value="${file}"/>"></script>
</c:forEach>

<tags:jsGlobals/>

<%-- Yukon Script Files --%>
<c:forEach items="${yukonScriptFiles}" var="file">
    <script src="<c:url value="${file}"/>"></script>
</c:forEach>

<c:if test="${!empty info.smartNotificationsEvent}">
    <script src="<c:url value="/resources/js/common/yukon.format.time.js"/>"></script>
    <script src="<c:url value="/resources/js/common/yukon.ui.timeSlider.js"/>"></script>
    <script src="<c:url value="/resources/js/pages/yukon.smart.notifications.js"/>"></script>
</c:if>

<audio id="alert-audio">
    <source src="<cti:url value="/WebConfig/yukon/audio/beep1-shortened.mp3"/>" type="audio/mpeg">
</audio>
</head>
<body>
    <cti:msgScope paths="layout.standard">
    
    <cti:csrfToken var="csrfToken"/>
    <input type="hidden" id="ajax-csrf-token" name="com.cannontech.yukon.request.csrf.token" value="${csrfToken}">
        
        <div id="modal-glass" style="display: none;">
            <div class="tint"></div>
            <div class="loading">
                <div class="box load-message">
                    <cti:msg key="yukon.web.components.pageloading"/>
                </div>
            </div>
        </div>
        
        <div class="yukon-page">
            <header class="yukon-header">
                <div class="toolbar-outer">
                    <div class="toolbar-inner">
                        <div class="toolbar">
                            <form accept-charset="ISO-8859-1" enctype="application/x-www-form-urlencoded" method="get"
                                action="<cti:url value="/search"/>" class="yukon-search-form">
                                <input type="text" placeholder="<cti:msg2 key='yukon.common.search.placeholder'/>"
                                    role="search" name="q" class="search-field">
                            </form>
                            <cti:button data-alert-sound="${alertSounds}" data-alert-flash="${alertFlash}"
                                data-popup="#yukon_alert_popup" data-popup-toggle=""
                                classes="yukon-alert-button action dn" label="0"/>
                            <cm:dropdown triggerClasses="b-user-menu fl" icon="icon-user"
                                label="${fn:escapeXml(displayName)}" type="button">
                                <cti:checkRolesAndProperties value="ADMIN_SUPER_USER">
                                    <cti:url var="url" value="/admin/users/${sessionScope.YUKON_USER.userID}"/>
                                    <cm:dropdownOption icon="icon-user-edit" href="${url}">
                                        <cti:msg2 key="yukon.common.user.editor"/>
                                    </cm:dropdownOption>
                                </cti:checkRolesAndProperties>
                                <cti:url value="/user/profile" var="userProfileUrl"/>
                                <cm:dropdownOption icon="icon-user" href="${userProfileUrl}">
                                    <cti:msg2 key="yukon.web.components.button.profile.label"/>
                                </cm:dropdownOption>
                                <cti:url value="/servlet/LoginController/logout" var="logoutUrl"/>
                                <cm:dropdownOption icon="icon-door-open" href="${logoutUrl}">
                                    <cti:msg2 key="yukon.web.components.button.logout.label"/>
                                </cm:dropdownOption>
                            </cm:dropdown>
                        </div>
                    </div>
                </div>
                <div class="nav-outer">
                    <div class="nav-inner">
                        <nav role="navigation">
                            <ul class="menus">
                                <li class="menu logo"><a class="menu-title" href="${homeUrl}"></a></li>
                                <cti:outputContent writable="${menuRenderer}"/>
                            </ul>
                        </nav>
                    </div>
                </div>
                <div class="bars">
                    <div class="bc-bar">
                        <div class="inner clearfix">
                            <cti:outputContent writable="${bcRenderer}"/>
                        </div>
                    </div>
                    <div class="page-title-bar">
                        <div class="inner clearfix">
                            <c:if test="${not empty pageDetail.pageHeading}">
                                <c:if test="${canFavorite}">
                                    <cti:button classes="b-favorite" id="favButton" renderMode="image"
                                        icon="icon-favorite-not" data-module="${module.moduleName}"
                                        data-name="${info.pageName}" data-label-args="${labelArgs}"/>
                                </c:if>
                                <h1 class="page-heading">
                                    ${requestScope['com.cannontech.web.layout.part.headingPrefix']}
                                    ${fn:escapeXml(pageDetail.pageHeading)}
                                    ${requestScope['com.cannontech.web.layout.part.headingSuffix']}</h1>
                            </c:if>
                            <div class="page-actions">
                                <cm:dropdown id="b-page-actions" type="button" triggerClasses="fr dn">
                                    <cti:displayForPageEditModes modes="EDIT,VIEW">
                                        <c:if test="${!empty info.smartNotificationsEvent}">
                                            <cm:dropdownOption key="yukon.web.modules.smartNotifications.actionLink" icon="icon-email-open" data-popup="#subscriptionPopup"/>
                                        </c:if>
                                    </cti:displayForPageEditModes>
                                </cm:dropdown>
                                <cti:button id="b-search-results" classes="fr dn" nameKey="searchResults"
                                    renderMode="buttonImage" icon="icon-resultset-first-gray"/>
                            </div>
                        </div>
                    </div>
                </div>
            </header>
            
            <section class="yukon-content" role="main" <c:if test="${widePage}">class="wide"</c:if>>
                
                <c:set var="layout" value="${showContextualNavigation ? 'column-4-20' : 'column-24'}"/>
                <c:set var="columnNum" value="${showContextualNavigation ? 'two nogutter' : 'one nogutter'}"/>
                <div class="${layout} clearfix" style="margin-bottom: 10px;">
                    
                    <!-- Start Contextual Nav -->
                    <c:if test="${showContextualNavigation}">
                        <div id="LeftColumn" class="column one">
                            <jsp:include page="${pageDetail.detailInfoIncludePath}"/>
                            <div class="vertical-menu">
                                <cti:outputContent writable="${contextualNavigationMenu}"/>
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
                <cti:url var="url" value="/common/alert/view"/>
                <div id="yukon_alert_popup" data-url="${url}"
                    data-title="${alertTitle}" data-height="400" data-width="600"></div>
                <tags:updatedWarning/>
                <tags:dataUpdateEnabler/>
                <cti:dataUpdaterCallback function="yukon.alerts.countUpdated" initialize="true" count="ALERT/COUNT"
                    lastId="ALERT/LASTID"/>
                
                <tags:analyticsTrackPage/>
                <%-- <tags:feedback/> TODO: uncomment when ready for feedback --%>
            </section>
            
            <footer id="yukon-footer" class="yukon-footer">
                <div class="utility">
                    <nav>
                        <ul>
                            <li><a href="<cti:url value="/support"/>"><i:inline key=".support"/></a></li>
                            <li><a href="<cti:url value="/sitemap"/>"><i:inline key=".siteMap"/></a></li>
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
                                <p class="copyright">
                                    <jsp:useBean id="now" class="java.util.Date"/>
                                    <fmt:formatDate var="year" value="${now}" pattern="yyyy"/>
                                    <cti:msg2 key=".copyright" arguments="${year}"/>
                                </p>
                                <ul>
                                    <li><cti:msg2 key=".yukonVersion" arguments="${yukonVersion}"/></li>
                                    <li><cti:msg2 key=".generatedAt"/> <cti:formatDate type="FULL"
                                            value="${currentTime}"/></li>
                                    <c:if test="${not empty energyCompanyName}">
                                        <li><cti:msg2 key=".energyCompany" arguments="${energyCompanyName}"/></li>
                                    </c:if>
                                    <li><cti:msg2 key=".username" arguments="${username}"/></li>
                                    <cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">
                                        <li><cti:msg2 key=".moduleName" arguments="${info.moduleName}"/>, <cti:msg2
                                                key=".pageName" arguments="${info.pageName}"/></li>
                                    </cti:checkGlobalRolesAndProperties>
                                </ul>
                                <cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
                                    <ul>
                                        <li><cti:msg2 key=".buildInfo" arguments="${buildInfo}"
                                                htmlEscapeArguments="false"/></li>
                                        <li>${servletPath}</li>
                                        <li><a href="<cti:url value="/dev"/>">Development</a></li>
                                    </ul>
                                </cti:checkGlobalRolesAndProperties>
                            </div>
                        </div>
                        <div class="right">
                            <div class="branding">
                                <a class="footer-logo" href="${homeUrl}"></a>
                            </div>
                        </div>
                    </div>
                </div>
            </footer>
            
        </div>
    </cti:msgScope>
    <c:if test="${!empty info.smartNotificationsEvent}">
       <cti:url var="subscriptionUrl" value="/notifications/subscription/existingPopup/${info.smartNotificationsEvent}">
            <c:forEach var="parameter" items="${info.smartNotificationsParameters}">
                <cti:param name="${parameter.key}" value="${parameter.value}"/>
            </c:forEach>
       </cti:url>
       <div id="subscriptionPopup" class="dn js-smart-notifications-popup" data-url="${subscriptionUrl}" 
        data-width="600" data-load-event="yukon:notifications:load"
        data-title="<cti:msg2 key="yukon.web.modules.smartNotifications.popup.title"/>"></div>
    </c:if>
</body>
</html>