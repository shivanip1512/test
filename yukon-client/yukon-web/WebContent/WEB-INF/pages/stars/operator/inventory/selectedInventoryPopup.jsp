<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:if test="${not empty resultsLimitedTo}">
    <div class="errorRed" style="width:95%;text-align:right;">
        <cti:msg key="yukon.common.collection.inventory.selectedInventoryPopup.selectedDevicesResultsLimited" arguments="${resultsLimitedTo}" />
    </div>
    <br>
</c:if>

<table class="compactResultsTable">
    <tr>
        <th><i:inline key="yukon.common.collection.inventory.serialNumber"/></th>
        <th><i:inline key="yukon.common.collection.inventory.hardwareType"/></th>
        <th><i:inline key="yukon.common.collection.inventory.label"/></th>
    </tr>
    
    <c:forEach var="inventoryInfoRow" items="${inventoryInfoList}" varStatus="status">
    
        <tr>
            <c:forEach var="info" items="${inventoryInfoRow}">
                <td style="text-align:left;"><spring:escapeBody htmlEscape="true">${info}</spring:escapeBody></td>
            </c:forEach>
        </tr>
        
    </c:forEach>
</table>