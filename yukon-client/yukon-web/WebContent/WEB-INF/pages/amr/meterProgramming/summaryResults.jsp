<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<table class="compact-results-table row-highlighting">
    <thead>
        <tr>
           <tags:sort column="${DEVICE_NAME}"/>
           <tags:sort column="${METER_NUMBER}"/>
           <tags:sort column="${DEVICE_TYPE}"/>
           <tags:sort column="${PROGRAM}"/>
           <tags:sort column="${STATUS}"/>
           <tags:sort column="${LAST_UPDATE}"/>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:choose>
            <c:when test="${searchResults.hitCount > 0}">
                <c:forEach var="result" items="${searchResults.resultList}">
                    <tr>                        
                        <td><cti:paoDetailUrl yukonPao="${result.device}">${fn:escapeXml(result.device.name)}</cti:paoDetailUrl></td>
                        <td>${result.meterNumber}</td>
                        <td>${result.device.paoIdentifier.paoType.paoTypeName}</td>
                        <td>${result.programInfo.name}</td>
                        <td><i:inline key="${result.status.formatKey}"/></td>
                        <td><cti:formatDate type="BOTH" value="${result.lastUpdate}"/></td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr><td colspan="6">
                    <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
                </td></tr>
            </c:otherwise>
        </c:choose>

    </tbody>
</table>
<tags:pagingResultsControls result="${searchResults}" adjustPageCount="true" hundreds="true"/>
