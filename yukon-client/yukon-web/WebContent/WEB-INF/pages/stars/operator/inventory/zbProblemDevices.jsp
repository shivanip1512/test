<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="zbProblemDevices">

    <div class="exportLink">
        <cti:button nameKey="csv" renderMode="labeledImage" href="/spring/stars/operator/inventory/zbProblemDevices/csv"/>
    </div>
    
    <table class="resultsTable" style="width:100%;">
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
                <cti:url value="/spring/stars/operator/hardware/view" var="hardwareUrl">
                    <cti:param name="inventoryId" value="${device.first.identifier.inventoryId}"/>
                    <cti:param name="accountId" value="${device.first.accountId}"/>
                    <cti:param name="energyCompanyId" value="${device.first.energyCompanyId}"/>
                </cti:url>
                <cti:url value="/operator/Hardware/InventoryDetail.jsp" var="invDetailUrl">
                    <cti:param name="src" value="Inventory"/>
                    <cti:param name="InvId" value="${device.first.identifier.inventoryId}"/>
                </cti:url>
                <cti:url value="/spring/stars/operator/account/view" var="accountUrl">
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
</cti:standardPage>