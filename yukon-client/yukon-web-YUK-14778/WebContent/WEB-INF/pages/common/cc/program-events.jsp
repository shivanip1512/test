<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:includeScript link="/resources/js/pages/yukon.dr.curtailment.js"/>

<cti:standardPage module="commercialcurtailment_user" page="programEvents">

<h3>Current Events</h3>
<c:choose>
    <c:when test="${empty currentEvents}">
        <div class="empty-list"><i:inline key="yukon.common.none.choice"/></div>
    </c:when>
    <c:otherwise>
        <c:set var="events" value="${currentEvents}"/>
        <%@ include file="events-table.jspf" %>
    </c:otherwise>
</c:choose>

<h3>Pending Events</h3>
<c:choose>
    <c:when test="${empty pendingEvents}">
        <div class="empty-list"><i:inline key="yukon.common.none.choice"/></div>
    </c:when>
    <c:otherwise>
        <c:set var="events" value="${pendingEvents}"/>
        <%@ include file="events-table.jspf" %>
    </c:otherwise>
</c:choose>

<h3>Recent Events</h3>
<c:choose>
    <c:when test="${empty recentEvents}">
        <div class="empty-list"><i:inline key="yukon.common.none.choice"/></div>
    </c:when>
    <c:otherwise>
        <c:set var="events" value="${recentEvents}"/>
        <c:set var="recentEventsColl" value="${recentEventsCollection}"/>
        <%@ include file="events-table.jspf" %>
    </c:otherwise>
</c:choose>
<c:if test="${!empty recentEventsCollection}">

<div
    id="details-popup"
    data-dialog
    data-event="yukon:dr:curtailment:details:display"
    data-url="${detailUrl}"
    data-title="${detailsTitle}">
</div>
</c:if>
</cti:standardPage>