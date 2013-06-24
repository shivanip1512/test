<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"  %>

<cti:standardPage module="operator" page="accountLog">

    <tags:boxContainer2 nameKey="accountActionLog">
        <table class="compactResultsTable">
            <tr>
                <th><i:inline key=".event" /></th>
                <th><i:inline key=".username" /></th>
                <th><i:inline key=".timeOfEvent" /></th>
            </tr>
            <c:forEach var="accountEvent" items="${accountEvents}">
                <tr>
                    <td><spring:escapeBody htmlEscape="true">${accountEvent.actionText}</spring:escapeBody></td>
                    <td><spring:escapeBody htmlEscape="true">${accountEvent.userName}</spring:escapeBody></td>
                    <td><cti:formatDate value="${accountEvent.eventBase.eventTimestamp}" type="BOTH"/></td>
                </tr>
            </c:forEach>
        </table>
    </tags:boxContainer2>
    
</cti:standardPage>
