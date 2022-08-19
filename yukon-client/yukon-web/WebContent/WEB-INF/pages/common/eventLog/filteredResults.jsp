<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.support.eventViewer.byCategory">

    <c:choose>
        <c:when test="${empty searchResult.resultList}">
            <span class="empty-list"><i:inline key="yukon.common.events.noResults" /></span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th><i:inline key="yukon.common.events.columnHeader.event" /></th>
                        <th><i:inline key="yukon.common.events.columnHeader.dateAndTime" /></th>
                        <th><i:inline key="yukon.common.events.columnHeader.message" /></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach items="${searchResult.resultList}" var="event">
                        <tr>
                            <td>${fn:escapeXml(event.eventType)}</td>
                            <td><cti:formatDate type="BOTH" value="${event.dateTime}" /></td>
                            <td class="wbba"><i:inline key="${event.messageSourceResolvable}" htmlEscape="true" /></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    <tags:pagingResultsControls result="${searchResult}" adjustPageCount="true" hundreds="true" />
    
</cti:msgScope>
