<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="dr" page="cc.detail">

<div class="column-24">
    <div class="column one nogutter">
        <table class="name-value-table natural-width">
            <tr>
                <td class="name"><i:inline key=".eventNumber"/></td>
                <td class="value">${fn:escapeXml(event.displayName)}</td>
            </tr>
            <c:if test="${not empty event.startTime}">
                <tr>
                    <td class="name"><i:inline key=".startDate"/></td>
                    <td class="value"><cti:formatDate value="${event.startTime}" type="DATE"/>&nbsp; (all times ${tz})</td>
                </tr>
            </c:if>
            <c:if test="${not empty event.notificationTime}">
                <tr>
                    <td class="name"><i:inline key=".notificationTime"/></td>
                    <td class="value"><cti:formatDate value="${event.notificationTime}" type="DATEHM"/></td>
                </tr>
            </c:if>
            <tr>
                <td class="name"><i:inline key=".startTime"/></td>
                <td class="value"><cti:formatDate value="${event.startTime}" type="DATEHM"/></td>
            </tr>
            <c:if test="${not empty event.stopTime}">
                <tr>
                    <td class="name"><i:inline key=".stopTime"/></td>
                    <td class="value"><cti:formatDate value="${event.stopTime}" type="DATEHM"/></td>
                </tr>
            </c:if>
            <c:if test="${not empty duration}">
                <tr>
                    <td class="name"><i:inline key=".duration"/></td>
                    <td class="value"><i:inline key=".durationMinutes" arguments="${duration}"/></td>
                </tr>
            </c:if> 
            <c:if test="${not empty event.stateDescription}">
                <tr>
                    <td class="name"><i:inline key=".state"/></td>
                    <td class="value">${event.stateDescription}</td>
                </tr>
            </c:if>
            <c:if test="${not empty message}">
                <tr>
                    <td class="name"><i:inline key=".message"/></td>
                    <td class="value">${message}</td>
                </tr>
            </c:if>
            <c:if test="${not empty economicDetail}">
                <tr>
                    <td class="name"><i:inline key=".initialEvent"/></td>
                    <td class="value">${event.initialEvent.displayName}</td>
                </tr>
                <tr>
                    <td class="name"><i:inline key=".latestRevision"/></td>
                    <td class="value">Revision&nbsp;${event.latestRevision.revision}</td>
                </tr>
            </c:if>
            <c:if test="${not empty reason}">
                <tr>
                    <td class="name"><i:inline key=".reason"/></td>
                    <td class="value">${reason}</td>
                </tr>
            </c:if>
            <tr>
                <td class="name"><i:inline key=".operations"/></td>
                <td class="value">
                    <c:if test="${showDeleteButton}">
                        <cti:url var="deleteUrl" value="/dr/cc/program/${program.id}/event/${event.id}"/>
                        <form:form action="${deleteUrl}" method="DELETE">
                            <cti:button type="submit" nameKey="delete" />
                        </form:form>
                    </c:if>
                    <c:if test="${showCancelButton}">
                        <cti:url var="cancelUrl" value="/dr/cc/program/${program.id}/event/${event.id}/cancel"/>
                        <cti:button nameKey="cancel" href="${cancelUrl}"/>
                    </c:if>
                    <c:if test="${showSuppressButton}">
                        <cti:url var="suppressUrl" value="/dr/cc/program/${program.id}/event/${event.id}/suppress"/>
                        <cti:button nameKey="suppress" href="${suppressUrl}"/>
                    </c:if>
                    <c:if test="${showReviseButton}">
                        <cti:url var="reviseUrl" value="/dr/cc/program/${program.id}/event/${event.id}/revise"/>
                        <cti:button nameKey="revise" href="${reviseUrl}"/>
                    </c:if>
                    <c:if test="${showExtendButton}">
                        <cti:url var="extendUrl" value="/dr/cc/program/${program.id}/event/${event.id}/extend"/>
                        <cti:button nameKey="extend" href="${extendUrl}"/>
                    </c:if>
                    <c:if test="${showAdjustButton}">
                        <cti:url var="adjustUrl" value="/dr/cc/program/${program.id}/event/${event.id}/adjust"/>
                        <cti:button nameKey="adjust" href="${adjustUrl}"/>
                    </c:if>
                    <c:if test="${showRemoveButton}">
                        <cti:url var="removeUrl" value="/dr/cc/program/${program.id}/event/${event.id}/remove"/>
                        <cti:button nameKey="remove" href="${removeUrl}"/>
                    </c:if>
                    
                    <cti:button nameKey="refresh" onClick="history.go(0)"/>
                </td>
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
                    <td><i:inline key=".company"/></td>
                    <td><i:inline key=".reason"/></td>
                    <td><i:inline key=".notificationType"/></td>
                    <td><i:inline key=".notificationTime"/></td>
                    <td><i:inline key=".state"/></td>
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
	        <c:forEach var="notification" items="${eventNotifications}">
                <tr>
                    <td>${fn:escapeXml(notification.customer.companyName)}</td>
                </tr>
	        </c:forEach>
        </table>
        </c:if>
        </c:otherwise>
        </c:choose>
    </div>
</div>

</cti:standardPage>