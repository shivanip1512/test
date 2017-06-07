<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table class="compact-results-table row-highlighting has-alerts has-actions">
    <th></th>
    <tags:sort column="${deviceName}" />                
    <tags:sort column="${meterNumber}" />                
    <tags:sort column="${deviceType}" />                
    <tags:sort column="${serialNumberAddress}" />
    <tags:sort column="${delta}" />                
    <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
    <c:forEach var="device" items="${detail.resultList}">
        <c:set var="deviceId" value="${device.paoIdentifier.paoId}"/>
        <tr>
            <td></td>
            <td><cti:paoDetailUrl yukonPao="${device.paoIdentifier}" newTab="true">${device.deviceName}</cti:paoDetailUrl></td>
            <td>${device.meterNumber}</td>
            <td>${device.paoIdentifier.paoType.paoTypeName}</td>
            <td>${device.addressSerialNumber}</td>
            <td>${device.delta}</td>
            <td></td>
        </tr>
    </c:forEach>
</table>
<tags:pagingResultsControls result="${detail}" adjustPageCount="true"/>

