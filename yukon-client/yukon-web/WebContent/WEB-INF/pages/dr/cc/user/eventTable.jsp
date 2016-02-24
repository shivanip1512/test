<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:choose>
    <c:when test="${not empty events}">
        <table class="compact-results-table stacked-md">
            <thead>
                  <tr>
                      <th><i:inline key=".programType"/></th>
                      <th><i:inline key=".programName"/></th>
                      <th><i:inline key=".programEvent"/></th>
                      <th><i:inline key=".programState"/></th>
                      <th><i:inline key=".programStart"/></th>
                      <th><i:inline key=".programStop"/></th>
                  </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="event" items="${events}">
                    <tr>
                        <td>${event.program.programType.name}</td>
                        <td>${fn:escapeXml(event.program.name)}</td>
                        <cti:url var="eventDetailUrl" value="/dr/cc/user/program/${event.program.id}/event/${event.id}/detail"/>
                        <td><a href="${eventDetailUrl}">${fn:escapeXml(event.displayName)}</a></td>
                        <td>${event.stateDescription}</td>
                        <td><cti:formatDate value="${event.startTime}" type="DATEHM"/></td>
                        <td><cti:formatDate value="${event.stopTime}" type="DATEHM"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <span class="empty-list">
            <i:inline key=".noEvents"/>
        </span>
    </c:otherwise>
</c:choose>