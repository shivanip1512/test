<%@ page errorPage="/internalError.jsp" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:set var="browser" value="${header['User-Agent']}" scope="session"/>
<c:set var="isIE" value="${fn:contains(browser, 'MSIE') ? 'ie' : 'no-ie'}"/>

<!DOCTYPE html>
<html dir="ltr" class="${module.moduleName}-module ${isIE} no-js">
<head>

<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<meta charset="UTF-8">

<title>${fn:escapeXml(title)}</title>

<!-- Layout CSS files -->
<cti:includeCss link="NORMALIZE" force="true"/>
<cti:includeCss link="YUKON_DEFAULT" force="true"/>
<cti:includeCss link="ICONS" force="true"/>
<cti:includeCss link="BUTTONS_DEFAULT" force="true"/>
<link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/CannonStyle.css"/>">
<link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/consumer/LeftMenuStyles.css"/>">
<cti:includeCss link="JQUERY_UI_MIN" force="true"/>

<%-- Include overrides.css last so that, you know, they actually override.  cascade! --%>
<cti:includeCss link="OVERRIDES" force="true"/>

<%-- Module CSS files from module_config.xml --%>
<c:forEach items="${moduleConfigCss}" var="file">
    <link rel="stylesheet" type="text/css" href="<c:url value="${file}"/>">
</c:forEach>

<%-- Individual files from includeCss tag on the request page --%>
<c:forEach items="${innerContentCss}" var="file">
    <link rel="stylesheet" type="text/css" href="<c:url value="${file}"/>">
</c:forEach>

<%-- Login Group specific style sheets (YukonRoleProperty.STD_PAGE_STYLE_SHEET) --%>
<c:forEach items="${loginGroupCss}" var="file">
    <link rel="stylesheet" type="text/css" href="<c:url value="${file}"/>">
</c:forEach>

<%-- Library Script Files --%>
<c:forEach items="${libraryScriptFiles}" var="file">
    <script type="text/javascript" src="<c:url value="${file}"/>"></script>
</c:forEach>

<tags:jsGlobals />

<%-- Yukon Script Files --%>
<c:forEach items="${yukonScriptFiles}" var="file">
    <script type="text/javascript" src="<c:url value="${file}"/>"></script>
</c:forEach>

<script>
function hideRevealSectionSetup(showElement, hideElement, clickableElement,
        section, showInitially, persistId) {

    section = $(document.getElementById(section));
    showElement = $(document.getElementById(showElement));
    hideElement = $(document.getElementById(hideElement));
    
    var doShow = function(doSlide) {
        section.show();
        hideElement.show();
        showElement.hide();
        
        if (persistId != '') {
            yukon.cookie.set('hideReveal', persistId, 'show');
        }
    };
    
    var doHide = function() {
        section.hide();
        hideElement.hide();
        showElement.show();
        if (persistId != '') {
            yukon.cookie.set('hideReveal', persistId, 'hide');
        }
    };
    
    var lastState = null;
    if (persistId != '') {
        lastState = yukon.cookie.get('hideReveal', persistId);
    }
    
    if (lastState) {
        if (lastState == 'show') {
            doShow();
        } else {
            doHide();
        }
    } else if (showInitially) {
        doShow();
    } else {
        doHide();
    }
    
    $(document.getElementById(clickableElement)).click(function(event) {
        if (section.is(":visible")) {
            doHide();
        } else {
            doShow();
        }
    });
}
</script>
</head>

<body class="<c:out value="${module.moduleName}"/>_module">
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

    <div id="Header">
        <div class="stdhdr_left">
            <div id="TopLeftLogo">
                <cti:logo key="yukon.web.layout.standard.upperleftlogo"/>
            </div>
        </div>
        <div class="stdhdr_middle">
            <div id="TopMiddleLogo">
                <cti:logo key="yukon.web.layout.standard.uppermiddlelogo"/>
            </div>
        </div>
        <div class="stdhdr_right">
            <div id="TopRightLogo">
                <cti:logo key="yukon.web.layout.standard.upperrightlogo"/>
            </div>
        </div>
        <div class="stdhdr_clear"></div>
    </div>

    <table style="width: 100%">
        <tr>
            <td colspan="2" class="leftMenuHeader">
                <cti:checkRolesAndProperties value="RESIDENTIAL_SIGN_OUT_ENABLED">
                    <a href="<cti:url value="/servlet/LoginController/logout"/>">
                        <cti:msg key="yukon.web.menu.logout"/>
                    </a>
                </cti:checkRolesAndProperties>&nbsp;
            </td>
        </tr>
        <tr>
            <td class="leftMenu"><cti:outputContent writable="${menuRenderer}"/></td>
            <td id="Content" style="vertical-align: top; width: 97%">
                <table class="contentTable">
                    <tr>
                        <td class="leftColumn"><cti:outputContent writable="${bodyContent}"/></td>
                        <td class="rightColumn">
                            <cti:customerAccountInfoTag account="${customerAccount}"/><br>
                            <cti:logo key="yukon.dr.${module.moduleName}.${info.pageName}.rightColumnLogo"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div id="CopyRight">
                    <cti:msg key="yukon.web.layout.standard.yukonVersion" arguments="${yukonVersion}"/>&nbsp;|&nbsp;
                    <jsp:useBean id="now" class="java.util.Date"/>
                    <fmt:formatDate var="year" value="${now}" pattern="yyyy"/>
                    <cti:msg key="yukon.web.layout.standard.copyright" arguments="${year}"/>&nbsp;|&nbsp;Generated at&nbsp;
                    <cti:formatDate type="FULL" value="${currentTime}"/>
                </div>
            </td>
        </tr>
    </table>

    <tags:analyticsTrackPage/>
</body>
</html>