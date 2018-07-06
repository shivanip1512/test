<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:url var="detailUrl" value="${urlPath}">
        <cti:formatDate type="LONG_DATE_TIME" value="${filter.startDate}" var="startDate"/>
        <cti:param name="startDate" value="${startDate}"/>
        <cti:formatDate type="LONG_DATE_TIME" value="${filter.endDate}" var="endDate"/>
        <cti:param name="endDate" value="${endDate}"/>
</cti:url>
   
    <div id="events-detail" data-url="${detailUrl}" data-static>
        <table class="compact-results-table has-actions row-highlighting">
            <tr>
                <tags:sort column="${warningType}" />
                <tags:sort column="${status}" />
                <tags:sort column="${timestamp}" />
            </tr>
            <tbody>
                <c:forEach var="event" items="${events.resultList}">
                    <tr class="js-event-${event.eventId}">
                        <td>
                             ${fn:escapeXml(event.warningType)}
                        </td>
                        <td class="js-status-${event.eventId}">
	                        <c:set var="warningColor" value="warning"/>
	                        <span class="${warningColor}">${fn:escapeXml(event.status)}</span>
                        </td>
                        <td class="js-timestamp"><cti:formatDate value="${event.timestamp}" type="FULL"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <tags:pagingResultsControls result="${events}" adjustPageCount="true" thousands="true"/>
    </div>