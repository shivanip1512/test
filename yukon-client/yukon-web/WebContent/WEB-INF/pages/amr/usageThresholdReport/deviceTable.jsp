<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<table class="compact-results-table row-highlighting has-alerts has-actions">
    <th></th>
    <th>Device Name</th>
    <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
    <c:forEach var="device" items="${detail.resultList}">
        <c:set var="deviceId" value="${device.paoIdentifier.paoId}"/>
        <tr>
            <td></td>
            <td><cti:paoDetailUrl yukonPao="${device.paoIdentifier}" newTab="true">${device.deviceName}</cti:paoDetailUrl></td>
            <td></td>
        </tr>
    </c:forEach>
</table>
