<%@ tag body-content="empty" %>

<%@ attribute name="result" required="true" type="com.cannontech.common.search.result.SearchResults" %>
<%@ attribute name="assetId" required="true" %>
<%@ attribute name="assetTotal" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="baseUrl" value="page">
    <cti:param name="assetId" value="${assetId}"/>
    <cti:param name="assetTotal" value="${assetTotal}"/>
</cti:url>
<cti:msgScope paths="modules.operator.hardware.assetAvailability">

<div data-url="${baseUrl}">
    <table class="compact-results-table">
        <thead>
            <tr>
                <tags:sort column="${SERIAL_NUM}"/>
                <tags:sort column="${TYPE}"/>
                <tags:sort column="${LAST_COMM}"/>
                <tags:sort column="${LAST_RUN}"/>
                <tags:sort column="${APPLIANCES}"/>
                <tags:sort column="${AVAILABILITY}"/>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach items="${result.resultList}" var="row">
                <tr>
                    <td>${row.serialNumber}</td>
                    <td><i:inline key="${row.type}"/></td>
                    <td><cti:formatDate type="BOTH" value="${row.lastComm}"/></td>
                    <td><cti:formatDate type="BOTH" value="${row.lastRun}"/></td>
                    <td><tags:expandableString name="${row.serialNumber}" value="${row.appliances}" maxLength="20"/></td>
                    <c:set var="availabilityClass" value="${colorMap[row.availability]}"></c:set>
                    <td class="${availabilityClass}"><i:inline key="${row.availability}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${result}" adjustPageCount="true"/>
</div>

</cti:msgScope>