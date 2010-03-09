<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"  %>

<cti:standardPage module="operator" page="accountLog">
    <cti:standardMenu />

    <tags:boxContainer2 key="accountActionLog" styleClass="boxContainer50percent">
        <table class="compactResultsTable">
            <tr>
                <th><i18n:inline key=".event" /></th>
                <th><i18n:inline key=".username" /></th>
                <th><i18n:inline key=".timeOfEvent" /></th>
            </tr>
            <c:forEach var="accountEvent" items="${accountEvents}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td><spring:escapeBody htmlEscape="true">${accountEvent.actionText}</spring:escapeBody></td>
                    <td><spring:escapeBody htmlEscape="true">${accountEvent.userName}</spring:escapeBody></td>
                    <td><cti:formatDate value="${accountEvent.eventBase.eventTimestamp}" type="BOTH"/></td>
                </tr>
            </c:forEach>
        </table>
    </tags:boxContainer2>
    
</cti:standardPage>
