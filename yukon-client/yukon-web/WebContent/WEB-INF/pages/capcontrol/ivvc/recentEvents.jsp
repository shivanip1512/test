<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<tags:standardPageFragment pageName="ivvc" module="capcontrol" fragmentName="recentEvents">
<table class="compactResultsTable ">
    <c:forEach var="ccEvent" items="${events}">
        <tr>
            <td><spring:escapeBody htmlEscape="true">${ccEvent.deviceName}</spring:escapeBody></td>
            <td><spring:escapeBody htmlEscape="true">${ccEvent.text}</spring:escapeBody></td>
            <td><cti:formatDate value="${ccEvent.dateTime}" type="BOTH" /></td>
            <td class="dn">${ccEvent.dateTime}</td>
        </tr>
    </c:forEach>
</table>
</tags:standardPageFragment>