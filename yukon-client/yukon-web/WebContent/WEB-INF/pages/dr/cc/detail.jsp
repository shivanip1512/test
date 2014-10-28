<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.detail">
<cti:msgScope paths="yukon.web.modules.commercialcurtailment.ccurtSetup">

<div class="column-24">
    <div class="column one nogutter">
        <h2><i:inline key=".${eventHeadingSpecific}"/>&nbsp;${participant}</h2>
        <table class="name-value-table natural-width">
            <tr>
                <td class="name">Event Number</td>
                <td class="value">${event.displayName}</td>
            </tr>
            <c:if test="${not empty event.startTime}">
                <tr>
                    <td class="name">Start Date</td>
                    <td class="value"><cti:formatDate value="${event.startTime}" type="DATE"/>&nbsp; (all times ${tz})</td>
                </tr>
            </c:if>
            <c:if test="${not empty event.notificationTime}">
                <tr>
                    <td class="name">Notification Time</td>
                    <td class="value"><cti:formatDate value="${event.notificationTime}" type="DATEHM"/></td>
                </tr>
            </c:if>
            <tr>
                <td class="name">Start Time</td>
                <td class="value"><cti:formatDate value="${event.startTime}" type="DATEHM"/></td>
            </tr>
            <c:if test="${not empty event.stopTime}">
                <tr>
                    <td class="name">Stop Time</td>
                    <td class="value"><cti:formatDate value="${event.stopTime}" type="DATEHM"/></td>
                </tr>
            </c:if>
            <c:if test="${not empty duration}">
                <tr>
                    <td class="name">Duration</td>
                    <td class="value">${duration}</td>
                </tr>
            </c:if> 
            <c:if test="${not empty event.stateDescription}">
                <tr>
                    <td class="name">State</td>
                    <td class="value">${event.stateDescription}</td>
                </tr>
            </c:if>
            <c:if test="${not empty message}">
                <tr>
                    <td class="name">Message</td>
                    <td class="value">${message}</td>
                </tr>
            </c:if>
            <c:if test="${not empty economicDetail}">
                <tr>
                    <td class="name">Initial Event</td>
                    <td class="value">${event.initialEvent.displayName}</td>
                </tr>
                <tr>
                    <td class="name">Latest Revision</td>
                    <td class="value">Revision&nbsp;${event.latestRevision.revision}</td>
                </tr>
            </c:if>
            <c:if test="${not empty duration}">
                <tr>
                    <td class="name">Duration</td>
                    <td class="value">${duration}</td>
                </tr>
            </c:if>
            <c:if test="${not empty reason}">
                <tr>
                    <td class="name">Reason</td>
                    <td class="value">${reason}</td>
                </tr>
            </c:if>
            <tr>
                <td class="name">Operations</td>
                <td><cti:button nameKey="refresh" onClick="history.go(0)"/></td>
            </tr>
        </table>
        <c:if test="${not empty pricingTableHead}">
        <%@ include file="economicDetail.jspf" %>
        </c:if>
        <c:choose>
        <c:when test="${(empty affectedCustomers) && (empty pricingTableHead)}">
        <table class="compact-results-table" id="notifications">
            <thead>
                <tr>
                    <td><i:inline key=".ccurtEvent_heading_accounting_company_heading"/></td>
                    <td><i:inline key=".ccurtEvent_heading_reason"/></td>
                    <td><i:inline key=".ccurtEvent_heading_notif_type"/></td>
                    <td><i:inline key=".ccurtEvent_heading_time"/></td>
                    <td><i:inline key=".programState"/></td>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="notification" items="${eventNotifications}">
                <tr>
                    <td>${notification.customer.companyName}</td>
                    <td>${notification.reason}</td>
                    <td>${notification.notifType}</td>
                    <td><cti:formatDate value="${notification.notificationTime}" type="DATEHM"/></td>
                    <td>${notification.state}</td>
                </tr>
                </c:forEach>
             </tbody>
        </table>
        </c:when>
        <c:otherwise>
        <c:if test="${empty pricingTableHead}">
        <h3>${affectedCustomers}</h3>
        <table class="compact-results-table" id="notifications">
            <thead>
                <tr>
                    <td><i:inline key=".ccurtEvent_heading_accounting_company_heading"/></td>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="notification" items="${eventNotifications}">
                <tr>
                    <td>${notification.customer.companyName}</td>
                </tr>
                </c:forEach>
             </tbody>
        </table>
        </c:if>
        </c:otherwise>
        </c:choose>
    </div>
</div>

</cti:msgScope>
</cti:standardPage>