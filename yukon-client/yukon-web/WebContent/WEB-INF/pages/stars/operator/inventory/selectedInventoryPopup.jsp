<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:if test="${not empty resultsLimitedTo}">
    <div class="error"><i:inline key="yukon.common.collection.inventory.selectedInventoryPopup.selectedDevicesResultsLimited" arguments="${resultsLimitedTo}"/></div>
</c:if>

<div class="scrollingContainer_large" style="margin-bottom: 10px;">
    <table class="compactResultsTable">
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
</div>