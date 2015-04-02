<%@ tag body-content="empty" %>

<%@ attribute name="result" required="true" type="com.cannontech.common.search.result.SearchResults" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="auditId" required="true" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:if test="${type == 'CONTROLLED'}">
    <c:set var="controlled" value="true"/>
</c:if>

<table class="full-width striped">
    <thead>
        <tr>
            <th><i:inline key=".serialNumber"/></th>
            <th><i:inline key=".deviceType"/></th>
            <th><i:inline key=".accountNumber"/></th>
            <c:if test="${controlled}"><th><i:inline key=".controlTotal"/></th></c:if>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="row" items="${result.resultList}">
            <tr>
                <td>
                    <cti:url value="/stars/operator/inventory/view" var="hardwareUrl">
                        <cti:param name="inventoryId" value="${row.hardware.identifier.inventoryId}"/>
                    </cti:url>
                    <a href="${hardwareUrl}">${fn:escapeXml(row.hardware.serialNumber)}</a>
                </td>
                <td><i:inline key="${row.hardware.identifier.hardwareType}"/></td>
                <td>
                    <c:if test="${row.hardware.accountId > 0}">
                        <cti:url value="/stars/operator/account/view" var="accountUrl">
                            <cti:param name="accountId" value="${row.hardware.accountId}"/>
                        </cti:url>
                        <a href="${accountUrl}">${fn:escapeXml(row.hardware.accountNo)}</a>
                    </c:if>
                </td>
                <c:if test="${controlled}">
                    <td><cti:formatDuration type="DHMS_REDUCED" value="${row.control}"/></td>
                </c:if>
            </tr>
        </c:forEach>
    </tbody>
</table>
<tags:pagingResultsControls result="${result}"/>
