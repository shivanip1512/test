<%@ tag body-content="empty" %>

<%@ attribute name="result" required="true" type="com.cannontech.common.search.SearchResult" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="assetId" required="true" %>
<%@ attribute name="itemsPerPage" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="pageMe" value="${result.hitCount > itemsPerPage}"/>
<div class="f-table-container">
    <table class="compactResultsTable sortable-table rowHighlighting">
        <thead>
            <tr>
                <th><a class="sorted desc"><span class="fl">Serial Number</span><i class="icon icon-bullet-arrow-down"></i></a></th>
                <th><a class=""><span class="fl">Type</span><i class="icon icon-bullet-arrow-down"></i></a></th>
                <th><a class=""><span class="fl">Last Communication</span><i class="icon icon-bullet-arrow-down"></i></a></th>
                <th><a class=""><span class="fl">Last Runtime</span><i class="icon icon-bullet-arrow-down"></i></a></th>
                <th><a class=""><span class="fl">Appliances</span><i class="icon icon-bullet-arrow-down"></i></a></th>
                <th><a class=""><span class="fl">Availability</span><i class="icon icon-bullet-arrow-down"></i></a></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${result.resultList}" var="row">
                <tr>
                    <c:set var="availabilityClass" value="${colorMap[row.availability]}"></c:set>
                    <td>${row.serialNumber}</td>
                    <td>${row.type}</td>
                    <td><cti:formatDate type="BOTH" value="${row.lastComm}"/></td>
                    <td><cti:formatDate type="BOTH" value="${row.lastRun}"/></td>
                    <td>${row.appliances}</td>
                    <td class="${availabilityClass}"><i:inline key="${row.availability}"/></td>
                </tr>
            </c:forEach>
        </tbody>
        <tfoot></tfoot>
    </table>
</div>

<c:if test="${pageMe}">
    <cti:url value="page" var="baseUrl">
        <cti:param name="type" value="${type}"/>
        <cti:param name="assetId" value="${assetId}"/>
    </cti:url>
    <tags:pagingResultsControls baseUrl="${baseUrl}" result="${result}"/>
</c:if>