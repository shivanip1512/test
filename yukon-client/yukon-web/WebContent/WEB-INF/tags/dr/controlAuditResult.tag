<%@ tag body-content="empty" %>
<%@ attribute name="result" required="true" type="com.cannontech.common.search.SearchResult"%>
<%@ attribute name="type" required="true" type="java.lang.String"%>
<%@ attribute name="auditId" required="true" type="java.lang.String"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:if test="${type == 'CONTROLLED'}">
    <c:set var="controlled" value="true"/>
</c:if>

<c:set var="pageMe" value="${result.hitCount > 10}"/>
<div id="${type}_Content" class="columnContent">
    <table class="compactResultsTable sectionContainerCompactResults rowHighlighting">
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
            <c:forEach varStatus="status" items="${result.resultList}" var="row">
                <c:choose>
                    <c:when test="${status.first}"><c:set var="rowClass" value="first"/></c:when>
                    <c:when test="${status.last}"><c:set var="rowClass" value="last"/></c:when>
                    <c:otherwise><c:set var="rowClass" value="middle"/></c:otherwise>
                </c:choose>
                <tr class="${rowClass}">
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
                    <c:if test="${controlled}"><td><cti:formatDuration type="DHMS_REDUCED" value="${row.control}"/></td></c:if>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
<c:if test="${pageMe}">
    <cti:url value="page" var="nextUrl">
        <cti:param name="direction" value="NEXT"/>
        <cti:param name="index" value="${result.startIndex + 1}"/>
        <cti:param name="type" value="${type}"/>
        <cti:param name="auditId" value="${auditId}"/>
    </cti:url>
    <cti:url value="page" var="prevUrl">
        <cti:param name="direction" value="PREV"/>
        <cti:param name="index" value="${result.startIndex + 1}"/>
        <cti:param name="type" value="${type}"/>
        <cti:param name="auditId" value="${auditId}"/>
    </cti:url>
    <div class="compactResultsPaging pagingArea">
        <span class="fl previousLink">
            <c:choose>
                <c:when test="${result.previousNeeded}">
                    <a href="${prevUrl}" class="labeled_icon prev f_ajaxPaging">
                        <i:inline key="yukon.common.paging.previous"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <span class="labeled_icon prev_disabled"><i:inline key="yukon.common.paging.previous"/></span>
                </c:otherwise>
            </c:choose>
        </span>
        <span class="fl pageNumText"><i:inline key="yukon.common.paging.viewing" arguments="${result.startIndex + 1},${result.endIndex},${result.hitCount}" argumentSeparator=","/></span>
        <span class="fl nextLink">
            <c:choose>
                <c:when test="${result.nextNeeded}">
                    <a href="${nextUrl}" class="labeled_icon_right next f_ajaxPaging">
                        <i:inline key="yukon.common.paging.next"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <span class="labeled_icon_right next_disabled"><i:inline key="yukon.common.paging.next"/></span>
                </c:otherwise>
            </c:choose>
        </span>
    </div>
</c:if>