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
            <tags:sort column="${scheduleName}"/>
            <tags:sort column="${timestamp}"/>
            <tags:sort column="${fileErrorCount}" classes="js-file-import-error-count-lbl"/>
            <tags:sort column="${fileSuccessCount}" classes="js-file-import-success-count-lbl"/>
        </tr>
        <tbody>
            <c:forEach var="event" items="${events.resultList}">
                <tr class="js-event-${event.eventId}">
                    <td class="js-schedule-name-${event.eventId}">
                        <c:choose>
                            <c:when test="${event.jobGroupId eq -1}">
                                <!-- This is an event for manual import. Manual import does not have a schedule name.
                                     So there is no hyperlink on the jobName. -->
                                ${fn:escapeXml(event.jobName)}
                            </c:when>
                            <c:otherwise>
                                <cti:url var="viewUrl" value="/stars/scheduledDataImport/${event.jobGroupId}/viewHistory"/>
                                <a href="${viewUrl}">${fn:escapeXml(event.jobName)}</a>
                            </c:otherwise>
                        </c:choose>
                        
                    </td>
                    <td class="js-timestamp"><cti:formatDate value="${event.timestamp}" type="FULL"/></td>
                    <td>${event.fileErrorCount}</td>
                    <td>${event.fileSuccessCount}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${events}" adjustPageCount="true" thousands="true"/>
</div>