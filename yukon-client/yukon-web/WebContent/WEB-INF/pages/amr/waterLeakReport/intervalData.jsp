<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="waterLeakReport.report.intervalData">

    <tags:selectedDevices deviceCollection="${filter.deviceCollection}" id="device-collection"/>
    
    <c:choose>
        <c:when test="${fn:length(leaks.resultList) > 0}">
            <cti:formatDate var="from" type="DATEHM" value="${filter.fromInstant}"/>
            <cti:formatDate var="to" type="DATEHM" value="${filter.toInstant}"/>
            <cti:url var="intervalsUrl" value="intervalData">
                <cti:mapParam value="${filter.deviceCollection.collectionParameters}"/>
                <cti:param name="includeDisabledPaos" value="${filter.includeDisabledPaos}"/>
                <cti:param name="fromInstant" value="${from}"/>
                <cti:param name="toInstant" value="${to}"/>
                <cti:param name="threshold" value="${filter.threshold}"/>
                <cti:param name="paoIds" value="${paoIds}"/>
            </cti:url>
            <cti:url var="downloadUrl" value="interval-data-csv">
                <cti:mapParam value="${filter.deviceCollection.collectionParameters}"/>
                <cti:param name="includeDisabledPaos" value="${filter.includeDisabledPaos}"/>
                <cti:param name="fromInstant" value="${from}"/>
                <cti:param name="toInstant" value="${to}"/>
                <cti:param name="threshold" value="${filter.threshold}"/>
            </cti:url>
            <div id="usage-container" data-static data-url="${intervalsUrl}">
                <div class="clearfix buffered">
                    <span class="state-box alert form-control"></span>&nbsp;<span class="form-control"><i:inline key=".legend"/></span>
                    <cti:url var="actionsUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${collectionFromReportResults.collectionParameters}"/>
                    </cti:url>
                    <cti:button href="${actionsUrl}" nameKey="collectionAction" icon="icon-cog-go" classes="fr M0"/>
                    <cti:button href="${downloadUrl}" nameKey="download" icon="icon-page-white-excel" classes="fr"/>
                </div>
                
                <table id="usage-table" class="compact-results-table has-actions has-alerts">
                    <thead>
                        <tr>
                            <tags:sort column="${nameColumn}" colspan="2"/>
                            <tags:sort column="${numberColumn}"/>
                            <tags:sort column="${typeColumn}"/>
                            <tags:sort column="${usageColumn}"/>
                            <tags:sort column="${dateColumn}"/>
                            <th><cti:icon icon="icon-cog" classes="fr"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="row" items="${leaks.resultList}">
                            <c:set var="disabledClass" value="${row.meter.disabled ? 'subtle' : ''}"/>
                            <tr class="${disabledClass}">
                                <td>
                                    <c:if test="${row.pointValueHolder.value == row.leakRate}">
                                        <span class="state-box alert"></span>
                                    </c:if>
                                </td>
                                <td><cti:paoDetailUrl yukonPao="${row.meter}">${fn:escapeXml(row.meter.name)}</cti:paoDetailUrl></td>
                                <td>${fn:escapeXml(row.meter.meterNumber)}</td>
                                <td><tags:paoType yukonPao="${row.meter}"/></td>
                                <td><cti:pointValueFormatter value="${row.pointValueHolder}" format="VALUE"/></td>
                                <td><cti:formatDate type="BOTH" value="${row.pointValueHolder.pointDataTimeStamp}"/></td>
                                <td><cm:singleDeviceMenu deviceId="${row.meter.paoIdentifier.paoId}" triggerClasses="fr"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <tags:pagingResultsControls result="${leaks}" adjustPageCount="true"/>
            </div>
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key=".noUsage"/></span>
        </c:otherwise>
    </c:choose>
    
</cti:standardPage>