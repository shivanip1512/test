<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="zbProblemDevices">

    <c:if test="${fn:length(devices) > 0}">
        <div id="f-page-buttons">
            <cti:button nameKey="csv" href="/stars/operator/inventory/zbProblemDevices/csv" icon="icon-page-white-excel"/>
        </div>
    </c:if>
    
    <c:choose>
        <c:when test="${fn:length(devices) > 0}">
            <table class="compact-results-table full-width">
                <tr>
                    <th><i:inline key=".serialNumber"/></th>
                    <th><i:inline key=".accountNumber"/></th>
                    <th><i:inline key=".hardwareType"/></th>
                    <th><i:inline key=".label"/></th>
                    <th><i:inline key=".state"/></th>
                    <th><i:inline key=".timestamp"/></th>
                </tr>
                
                <c:forEach var="device" items="${devices}">
                    <tr>
                        <cti:url value="/stars/operator/hardware/view" var="hardwareUrl">
                            <cti:param name="inventoryId" value="${device.first.identifier.inventoryId}"/>
                            <cti:param name="accountId" value="${device.first.accountId}"/>
                            <cti:param name="energyCompanyId" value="${device.first.energyCompanyId}"/>
                        </cti:url>
                        <cti:url value="/stars/operator/inventory/view" var="invDetailUrl">
                            <cti:param name="inventoryId" value="${device.first.identifier.inventoryId}"/>
                        </cti:url>
                        <cti:url value="/stars/operator/account/view" var="accountUrl">
                            <cti:param name="accountId" value="${device.first.accountId}"/>
                        </cti:url>
                        
                        <c:if test="${device.first.accountId == 0}">
                            <td><a href="${invDetailUrl}">${fn:escapeXml(device.first.serialNumber)}</a></td>
                            <td><i:inline key=".noAccountNumber"/></td>
                        </c:if>
                        <c:if test="${device.first.accountId != 0}">
                            <td><a href="${hardwareUrl}">${fn:escapeXml(device.first.serialNumber)}</a></td>
                            <td><a href="${accountUrl}">${fn:escapeXml(accountIdsToAccountNumbers[device.first.accountId])}</a></td>
                        </c:if>
                        <td><i:inline key="${device.first.identifier.hardwareType}"/></td>
                        <td>${fn:escapeXml(device.first.label)}</td>
                        <td class="fwb" style="color:${stateColorMap[device.second.value]};"><cti:pointValueFormatter value="${device.second}" format="VALUE"/></td>
                        <c:if test="${empty device.second.pointDataTimeStamp}">
                            <td><i:inline key=".uninitialized"/></td>
                        </c:if>
                        <c:if test="${not empty device.second.pointDataTimeStamp}">
                            <td><cti:pointValueFormatter value="${device.second}" format="DATE"/></td>
                        </c:if>
                    </tr>
                </c:forEach>
            </table>
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key=".none"/></span>
        </c:otherwise>
    </c:choose>
</cti:standardPage>