<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.inventory,modules.operator">
<table class="striped full-width">
    <thead>
        <tr>
            <th><i:inline key=".serialNumber"/></th>
            <th><i:inline key=".meterNumber"/></th>
            <th><i:inline key=".type"/></th>
            <th><i:inline key=".name"/></th>
            <th><i:inline key=".label"/></th>
            <th><i:inline key=".accountNumber"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="device" items="${devices.resultList}">
            <tr>
                <td>
                    <c:if test="${not empty device.serialNumber}">
                        <cti:url var="url" value="/stars/operator/inventory/view">
                            <cti:param name="inventoryId" value="${device.inventoryIdentifier.inventoryId}"/>
                        </cti:url>
                        <a href="${url}">${fn:escapeXml(device.serialNumber)}</a>
                    </c:if>
                </td>
                <td>${fn:escapeXml(device.meterNumber)}</td>
                <td>
                    <c:choose>
                        <c:when test="${device.inventoryIdentifier.hardwareType.meter}">
                            <cti:msg2 key="${device.paoIdentifier.paoType}"/>
                        </c:when>
                        <c:otherwise>
                            <cti:msg2 key="${device.inventoryIdentifier.hardwareType}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td><c:if test="${device.deviceId > 0}">${fn:escapeXml(device.name)}</c:if></td>
                <td>${fn:escapeXml(device.label)}</td>
                <td>
                    <c:if test="${device.accountId > 0}">
                        <cti:url var="url" value="/stars/operator/account/view">
                            <cti:param name="accountId" value="${device.accountId}"/>
                        </cti:url>
                        <a href="${url}">${fn:escapeXml(device.accountNo)}</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<tags:pagingResultsControls adjustPageCount="true" result="${devices}"/>
</cti:msgScope>