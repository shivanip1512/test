<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:if test="${not empty resultsLimitedTo}">
    <div class="warning tac"><i:inline key="yukon.common.collection.inventory.selectedInventoryPopup.selectedDevicesResultsLimited" arguments="${resultsLimitedTo}"/></div>
</c:if>

<c:choose>
    <c:when test="${fn:length(inventoryInfoList) == 0}">
        <div class="error tac"><i:inline key="yukon.common.device.bulk.selectedDevicesEmpty"/></div>
    </c:when>
    <c:otherwise>
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th class="wsnw"><i:inline key="yukon.common.collection.inventory.serialNumber"/></th>
                    <th class="wsnw"><i:inline key="yukon.common.collection.inventory.hardwareType"/></th>
                    <th class="wsnw"><i:inline key="yukon.common.collection.inventory.label"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="row" items="${inventoryInfoList}">
                    <tr>
                        <c:forEach var="info" items="${row}" varStatus="status">
                            <td class="wsnw<c:if test="${status.last}"> last</c:if>">${fn:escapeXml(info)}</td>
                        </c:forEach>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>