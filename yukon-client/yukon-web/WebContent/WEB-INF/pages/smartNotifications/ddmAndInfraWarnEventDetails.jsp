<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

    <cti:url var="detailUrl" value="${urlPath}">
        <cti:formatDate type="LONG_DATE_TIME" value="${filter.startDate}" var="startDate"/>
        <cti:param name="startDate" value="${startDate}"/>
        <cti:formatDate type="LONG_DATE_TIME" value="${filter.endDate}" var="endDate"/>
        <cti:param name="endDate" value="${endDate}"/>
        <c:forEach var="category" items="${filter.categories}">
            <cti:param name="categories" value="${category}"/>
        </c:forEach>
    </cti:url>
    <c:set var="showTypeColumn" value="${eventType == 'INFRASTRUCTURE_WARNING'}"/>
    <div id="events-detail" data-url="${detailUrl}" data-static>
        <table class="compact-results-table has-actions row-highlighting">
            <tr>
                <tags:sort column="${deviceName}" />
                <c:if test="${showTypeColumn}">
                    <tags:sort column="${type}" />
                </c:if>
                <tags:sort column="${status}" />
                <tags:sort column="${timestamp}" />
            </tr>
            <tbody>
                <c:forEach var="event" items="${events.resultList}">
                    <tr class="js-event-${event.eventId}">
                        <td>
                            <cti:paoDetailUrl paoId="${event.deviceId}" newTab="true">
                                 ${fn:escapeXml(event.deviceName)}
                            </cti:paoDetailUrl>
                        </td>
                        <c:if test="${showTypeColumn}">
                            <td>${event.type}</td>
                        </c:if>
                        <td class="js-status-${event.eventId}">
                            <c:choose>
                                <c:when test="${eventType == 'INFRASTRUCTURE_WARNING'}">
                                    <c:set var="warningColor" value="warning"/>
                                    <c:if test="${event.severity == 'HIGH'}">
                                        <c:set var="warningColor" value="error"/>
                                    </c:if>
                                    <c:if test="${event.severity == 'CLEAR'}">
                                        <c:set var="warningColor" value="green"/>
                                    </c:if>
                                    <c:set var="arguments" value="${[event.argument1, event.argument2, event.argument3]}"/>
                                    <span class="${warningColor}"><cti:msg2 key="yukon.web.widgets.infrastructureWarnings.warningType.${event.status}.${event.severity}" arguments="${arguments}"/></span>
                                </c:when>
                                <c:otherwise>
                                     <span class="ttc"><cti:msg2 key=".${eventType}.${event.status}"/></span> 
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="js-timestamp"><cti:formatDate value="${event.timestamp}" type="FULL"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <tags:pagingResultsControls result="${events}" adjustPageCount="true" thousands="true"/>
    </div>