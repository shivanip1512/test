<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<table class="compact-results-table row-highlighting js-select-all-container">
    <thead>
        <tr>
           <th><input type="checkbox" class="js-select-all"></th>
           <tags:sort column="${deviceName}"/>
           <tags:sort column="${deviceType}"/>
           <tags:sort column="${serialNumber}"/>
           <tags:sort column="${gatewayName}"/>
           <tags:sort column="${gatewayLoading}"/>
           <tags:sort column="${attributes}"/>
           <tags:sort column="${interval}"/>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:choose>
            <c:when test="${searchResults.hitCount > 0}">
                <c:forEach var="result" items="${searchResults.resultList}">
                    <cti:url var="gatewayUrl" value="/stars/gateways/${result.gateway.paoIdentifier.paoId}"/>
                    <tr>
                        <td><input class="js-select-all-item" type="checkbox" name="selectedResult" value="${result.meter.paoIdentifier.paoId}"></td>
                        <td><cti:paoDetailUrl yukonPao="${result.meter}">${result.meter.name}</cti:paoDetailUrl></td>
                        <td>${result.meter.paoIdentifier.paoType.paoTypeName}</td>
                        <td>${result.meter.rfnIdentifier.sensorSerialNumber}</td>
                        <td>
                            <a href="${gatewayUrl}">${fn:escapeXml(result.gateway.name)}</a>
                        </td>
                        <td>${result.gateway.data.dataStreamingLoadingPercent}</td>
                        <td style="max-width:400px;">${result.config.commaDelimitedAttributes}</td>
                        <td>${result.config.selectedInterval}
                            <c:choose>
                                <c:when test="${result.config.selectedInterval > 1}">
                                    <i:inline key="yukon.web.modules.tools.dataStreaming.minutes"/>
                                </c:when>
                                <c:otherwise>
                                    <i:inline key="yukon.web.modules.tools.dataStreaming.minute"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr><td></td><td>
                    <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
                </td></tr>
            </c:otherwise>
        </c:choose>

    </tbody>
</table>
<tags:pagingResultsControls result="${searchResults}" adjustPageCount="true" hundreds="true"/>