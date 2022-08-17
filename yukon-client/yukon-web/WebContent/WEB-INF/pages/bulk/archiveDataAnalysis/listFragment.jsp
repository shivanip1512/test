<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.bulk.analysis">

<table class="compact-results-table">
    <thead>
        <tr>
            <th><i:inline key=".list.runDate"/></th>
            <th><i:inline key=".attribute"/></th>
            <th><i:inline key=".list.numberOfDevices"/></th>
            <th><i:inline key=".list.range"/></th>
            <th><i:inline key=".interval"/></th>
            <th><i:inline key=".pointQuality"/></th>
            <th><i:inline key=".list.status"/></th>
            <th><i:inline key=".list.actions"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach items="${result.resultList}" var="analysis">
            <cti:msg2 var="attribName" key="${analysis.attribute}" htmlEscape="true"/>
                <tr data-analysis="${analysis.analysisId}" data-status="${analysis.status}">
                    <input type="hidden" value="${analysis.analysisId}"/>
                    <td>
                        <cti:formatDate value="${analysis.runDate}" type="DATEHM"/>
                    </td>
                    <td>${attribName}</td>
                    <td>
                        <cti:dataUpdaterValue type="ARCHIVE_DATA_ANALYSIS" identifier="${analysis.analysisId}/DEVICES"/>
                    </td>
                    <td>
                        <cti:formatInterval type="DATEHM" value="${analysis.dateTimeRange}"/>
                    </td>
                    <td>
                        <cti:formatPeriod value="${analysis.intervalPeriod}" type="DHMS_REDUCED"/>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${analysis.excludeBadPointQualities}">
                                <i:inline key=".list.normalOnly"/>
                            </c:when>
                            <c:otherwise>
                                <i:inline key=".list.allQualities"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    
                    <td>
                        <div class="js-analysis-status">
                            <c:choose>
                                <c:when test="${analysis.running}">
                                    <cti:url var="analysisProgressUrl" value="/bulk/archiveDataAnalysis/home/processing">
                                        <cti:param name="resultsId" value="${analysis.statusId}"/>
                                        <cti:param name="analysisId" value="${analysis.analysisId}"/>
                                    </cti:url>
                                    <a href="${analysisProgressUrl}"><i:inline key="${analysis.status.formatKey}"/></a>
                                </c:when>
                                <c:when test="${analysis.reading}">
                                    <cti:url var="readProgressUrl" value="/bulk/archiveDataAnalysis/read/readResults">
                                        <cti:param name="resultId" value="${analysis.statusId}"/>
                                        <cti:param name="analysisId" value="${analysis.analysisId}"/>
                                    </cti:url>
                                    <a href="${readProgressUrl}"><i:inline key="${analysis.status.formatKey}"/></a>
                                </c:when>
                                <c:when test="${analysis.done}">
                                    <i:inline key="${analysis.status}"/>
                                </c:when>
                            </c:choose>
                        </div>
                    </td>
                    <td>
                        <cti:url var="viewUrl" value="/bulk/archiveDataAnalysis/results/view">
                            <cti:param name="analysisId" value="${analysis.analysisId}"/>
                        </cti:url>
                        <div class="js-analysis-actions">
                            <c:choose>
                                <c:when test="${analysis.running}">
                                    <cti:button classes="js-results-button" nameKey="viewButtonAnalyzing" renderMode="image" 
                                            href="${viewUrl}" disabled="true" icon="icon-application-view-columns"/>
                                </c:when>
                                <c:when test="${analysis.reading}">
                                    <cti:button classes="js-results-button" nameKey="viewButton" renderMode="image" 
                                            href="${viewUrl}" icon="icon-application-view-columns"/>
                                </c:when>
                                <c:when test="${analysis.done}">
                                    <c:choose>
                                        <c:when test="${analysis.deviceCount == 0}">
                                            <cti:button classes="js-results-button" nameKey="viewButtonNoDevices" 
                                                    renderMode="image" disabled="true" icon="icon-application-view-columns"/>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:button classes="js-results-button" nameKey="viewButton" renderMode="image" 
                                                    href="${viewUrl}" icon="icon-application-view-columns"/>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                            </c:choose>
                            <cti:url var="deleteUrl" value="/bulk/archiveDataAnalysis/list/delete">
                                <cti:param name="analysisId" value="${analysis.analysisId}"/>
                            </cti:url>
                            <cti:button id="delete-result-${analysis.analysisId}" nameKey="remove" renderMode="image" 
                                    icon="icon-cross" href="${deleteUrl}"/>
                            <d:confirm on="#delete-result-${analysis.analysisId}" nameKey="list.deleteConfirmation" 
                                    argument="${attribName}"/>
                        </div>
                    </td>
                    <cti:dataUpdaterCallback function="yukon.dataAnalysis.changeStatus"
                                value="ARCHIVE_DATA_ANALYSIS/${analysis.analysisId}/STATUS"/>
                    
                </tr>
        </c:forEach>
    </tbody>
</table>
<tags:pagingResultsControls adjustPageCount="true" result="${result}"/>
</cti:msgScope>