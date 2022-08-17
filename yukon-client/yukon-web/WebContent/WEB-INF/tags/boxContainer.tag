<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="escapeTitle" type="java.lang.Boolean" description="If true, html escapes the title text." %>
<%@ attribute name="helpText" description="The text to put inside of a help popup." %>
<%@ attribute name="helpUrl" description="A url used to load a help popup with content before showing." %>
<%@ attribute name="id" description="The id of the outer container element. If not provided, the id is generated." %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="showInitially" type="java.lang.Boolean" %>
<%@ attribute name="styleClass" description="CSS class names applied to the outer container element." %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="titleLinkHtml" %>
<%@ attribute name="showArrows" type="java.lang.Boolean" %>
<%@ attribute name="showHelpIcon" required="false" type="java.lang.Boolean" 
              description="Show help icon even if the helpText or helpUrl is empty. The help text has to be loaded using some js code in this case." %>
<%@ attribute name="smartNotificationsEvent" %>
<%@ attribute name="useIdForCookie" type="java.lang.Boolean" %>

<cti:default var="hideEnabled" value="${true}"/>
<cti:default var="showInitially" value="${true}"/>
<cti:default var="styleClass" value=""/>

<c:choose>
    <c:when test="${hideEnabled}">
        <c:choose>
            <c:when test="${showInitially}"><c:set var="showHide" value="show"/></c:when>
            <c:otherwise><c:set var="showHide" value="hide"/></c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${useIdForCookie}">
                <c:set var="persistId" value="${pageScope.id}"/>
            </c:when>
            <c:otherwise>
                <cti:replaceAll var="persistId" input="${pageScope.title}" replace="" pattern="[^\w]"/>
            </c:otherwise>
        </c:choose>
        <cti:yukonCookie var="showHide" scope="hideReveal" id="${persistId}" defaultValue="${showHide}"/>
        <c:set var="collapsed" value="${showHide eq 'hide' ? ' collapsed' : ''}"/>
    </c:when>
    <c:otherwise><c:set var="collapsed" value=""/></c:otherwise>
</c:choose>

<c:set var="styleClass" value="${styleClass}${collapsed}"/>

<cti:uniqueIdentifier prefix="titled-container-" var="thisId"/>
<c:if test="${!empty pageScope.id}">
    <c:set var="thisId" value="${id}"/>
</c:if>

<div class="titled-container box-container clearfix ${styleClass}" <c:if test="${!empty pageScope.id}">id="${id}"</c:if> data-use-id="${useIdForCookie}">

    <div class="title-bar clearfix">
        <c:if test="${!empty pageScope.titleLinkHtml}">${pageScope.titleLinkHtml}</c:if>
        <h3 class="title">
            <c:choose>
                <c:when test="${pageScope.escapeTitle}">${fn:escapeXml(pageScope.title)}</c:when>
                <c:otherwise>${pageScope.title}</c:otherwise>
            </c:choose>
        </h3>
        <div class="controls">
            <c:if test="${not empty pageScope.smartNotificationsEvent}">
                <cti:button renderMode="image" icon="icon-email-open" classes="widget-controls" data-popup="#smart-notifications-popup-${thisId}"/>
            </c:if>
            <c:if test="${not empty pageScope.helpText or not empty pageScope.helpUrl or showHelpIcon}">
                <cti:button renderMode="image" icon="icon-help" classes="widget-controls js-help-icon" data-popup="#box-container-info-popup-${thisId}"/>
            </c:if>
            <c:if test="${hideEnabled}">
                <cti:button renderMode="image" icon="show-hide" classes="widget-controls js-show-hide"/>
            </c:if>
            <c:if test="${showArrows}">
                <cti:button renderMode="image" icon="icon-bullet-go-up" classes="widget-controls js-move-up"/>
                <cti:button renderMode="image" icon="icon-bullet-go-down" classes="widget-controls js-move-down"/>
                <cti:button renderMode="image" icon="icon-bullet-go-left" classes="widget-controls js-move-left"/>
                <cti:button renderMode="image" icon="icon-bullet-go" classes="widget-controls js-move-right"/>
                <cti:button renderMode="image" icon="icon-cross" classes="widget-controls js-remove"/>
            </c:if>
        </div>
    </div>
    
    <div id="${thisId}_content" class="content clearfix"><jsp:doBody/></div>
</div>

<c:if test="${not empty pageScope.helpText or not empty pageScope.helpUrl or showHelpIcon}">
    <div id="box-container-info-popup-${thisId}" 
            class="dn" 
            data-title="${pageScope.title}"
            <c:if test="${not empty pageScope.helpUrl}">data-url="${helpUrl}"</c:if> 
            data-width="600"><div class="scroll-lg">${helpText}</div></div>
</c:if>
<c:if test="${not empty pageScope.smartNotificationsEvent}">
    <cti:url var="smartNotificationsUrl" value="/notifications/subscription/existingPopup/${pageScope.smartNotificationsEvent}"/>
    <div id="smart-notifications-popup-${thisId}" class="dn js-smart-notifications-popup"
            data-title="<cti:msg2 key="yukon.web.modules.smartNotifications.popup.title"/>"
            data-load-event="yukon:notifications:load"
            data-url="${smartNotificationsUrl}" 
            data-width="600"></div>
</c:if>

<c:if test="${not empty pageScope.smartNotificationsEvent}">
    <cti:includeScript link="YUKON_TIME_FORMATTER"/>
    <cti:includeScript link="/resources/js/common/yukon.ui.timeSlider.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.smart.notifications.js"/>
</c:if>