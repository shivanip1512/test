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
<cti:url var="sortUrl" value="page" />
<cti:msgScope paths="modules.operator.hardware.assetAvailability">

<div class="f-table-container">
    <table class="compactResultsTable sortable-table rowHighlighting">
        <thead>
            <tr>
                <th><tags:sortLink nameKey="serialNumber" baseUrl="${sortUrl}" fieldName="SERIAL_NUM" 
                                   sortParam="sortBy" styleClass="f-sortLink" isDefault="true" /></th>
                <th><tags:sortLink nameKey="type" baseUrl="${sortUrl}" fieldName="TYPE" 
                                   sortParam="sortBy" styleClass="f-sortLink" /></th>
                <th><tags:sortLink nameKey="lastCommunication" baseUrl="${sortUrl}" fieldName="LAST_COMM" 
                                   sortParam="sortBy" styleClass="f-sortLink" /></th>
                <th><tags:sortLink nameKey="lastRuntime" baseUrl="${sortUrl}" fieldName="LAST_RUN" 
                                   sortParam="sortBy" styleClass="f-sortLink" /></th>
                <th><tags:sortLink nameKey="appliances" baseUrl="${sortUrl}" fieldName="APPLIANCES" 
                                   sortParam="sortBy" styleClass="f-sortLink" /></th>
                <th><tags:sortLink nameKey="availability" baseUrl="${sortUrl}" fieldName="AVAILABILITY" 
                                   sortParam="sortBy" styleClass="f-sortLink" /></th>
            </tr>
        </thead>
        
        <tbody>
            <c:forEach items="${result.resultList}" var="row">
                <tr>
                    <td>${row.serialNumber}</td>
                    <td><i:inline key="${row.type}"/></td>
                    <td><cti:formatDate type="BOTH" value="${row.lastComm}"/></td>
                    <td><cti:formatDate type="BOTH" value="${row.lastRun}"/></td>
                    <td>
                        <tags:expandableString name="${row.serialNumber}" value="${row.appliances}" maxLength="20"/>
                    </td>
                    <c:set var="availabilityClass" value="${colorMap[row.availability]}"></c:set>
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
        <cti:param name="sortBy" value="${assetDetailsSortBy}" />
        <cti:param name="descending" value="${assetDetailsSortDesc}" />
    </cti:url>
    <tags:pagingResultsControls baseUrl="${baseUrl}" result="${result}"/>
</c:if>
</cti:msgScope>
