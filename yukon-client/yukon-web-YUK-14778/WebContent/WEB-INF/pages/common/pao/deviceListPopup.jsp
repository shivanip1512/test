<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty resultsLimitedTo}">
    <div class="warning tac"><i:inline key="yukon.common.device.bulk.selectedDevicesResultsLimited" arguments="${resultsLimitedTo}"/></div>
</c:if>

<c:choose>
	<c:when test="${fn:length(deviceInfoList) == 0}">
	    <div class="error tac"><i:inline key="yukon.common.device.bulk.selectedDevicesEmpty"/></div>
	</c:when>
    <c:otherwise>
        <table class="compact-results-table">
            <thead>
                <tr>
                    <c:forEach var="th" items="${deviceInfoList[0]}"><th>${th}</th></c:forEach>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="row" items="${deviceInfoList}" begin="1">
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