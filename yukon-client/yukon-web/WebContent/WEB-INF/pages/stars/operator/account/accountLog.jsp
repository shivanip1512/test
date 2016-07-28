<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="accountLog">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
    <table class="compact-results-table row-highlighting">
        <thead>
            <tr>
                <th><i:inline key=".event" /></th>
                <th><i:inline key=".username" /></th>
                <th><i:inline key=".timeOfEvent" /></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="accountEvent" items="${accountEvents}">
                <tr>
                    <td>${fn:escapeXml(accountEvent.actionText)}</td>
                    <td>${fn:escapeXml(accountEvent.userName)}</td>
                    <td><cti:formatDate value="${accountEvent.eventBase.eventTimestamp}" type="BOTH"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>
