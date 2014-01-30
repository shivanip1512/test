<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags/dialog" prefix="d"%>

<cti:msgScope paths="yukon.web.modules.tools.bulk.analysis.list">
<table class="compact-results-table">
    <thead>
        <tr>
            <th><i:inline key="yukon.web.modules.tools.bulk.analysis.list.runDate"/></th>
            <th><i:inline key="yukon.web.modules.tools.bulk.analysis.attribute"/></th>
            <th><i:inline key="yukon.web.modules.tools.bulk.analysis.list.numberOfDevices"/></th>
            <th><i:inline key="yukon.web.modules.tools.bulk.analysis.list.range"/></th>
            <th><i:inline key="yukon.web.modules.tools.bulk.analysis.interval"/></th>
            <th><i:inline key="yukon.web.modules.tools.bulk.analysis.pointQuality"/></th>
            <th><i:inline key="yukon.web.modules.tools.bulk.analysis.list.status"/></th>
            <th><i:inline key="yukon.web.modules.tools.bulk.analysis.list.actions"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach items="${result.resultList}" var="analysis">
            <cti:msg2 var="attribName" key="${analysis.attribute}" htmlEscape="true"/>
            <c:if test="${analysis.status != 'DELETED'}">
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
                                <i:inline key="yukon.web.modules.tools.bulk.analysis.list.normalOnly"/>
                            </c:when>
                            <c:otherwise>
                                <i:inline key="yukon.web.modules.tools.bulk.analysis.list.allQualities"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    
                    <td>
                        <div class="f-analysis-status">
                            <c:choose>
                                <c:when test="${analysis.running}">
                                    <cti:url var="analysisProgressUrl" value="/bulk/archiveDataAnalysis/home/processing">
                                        <cti:param name="resultsId" value="${analysis.statusId}"/>
                                        <cti:param name="analysisId" value="${analysis.analysisId}"/>
                                    </cti:url>
                                    <cti:link href="${analysisProgressUrl}" key="${analysis.status.formatKey}"/>
                                </c:when>
                                <c:when test="${analysis.reading}">
                                    <cti:url var="readProgressUrl" value="/bulk/archiveDataAnalysis/read/readResults">
                                        <cti:param name="resultId" value="${analysis.statusId}"/>
                                        <cti:param name="analysisId" value="${analysis.analysisId}"/>
                                    </cti:url>
                                    <cti:link href="${readProgressUrl}" key="${analysis.status.formatKey}"/>
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
                        <div class="f-analysis-actions">
                            <c:choose>
                                <c:when test="${analysis.running}">
                                    <cti:button classes="f-results-button" nameKey="viewButtonAnalyzing" renderMode="image" href="${viewUrl}" disabled="true" icon="icon-application-view-columns"/>
                                </c:when>
                                <c:when test="${analysis.reading}">
                                    <cti:button classes="f-results-button" nameKey="viewButton" renderMode="image" href="${viewUrl}" icon="icon-application-view-columns"/>
                                </c:when>
                                <c:when test="${analysis.done}">
                                    <c:choose>
                                        <c:when test="${analysis.deviceCount == 0}">
                                            <cti:button classes="f-results-button" nameKey="viewButtonNoDevices" renderMode="image" disabled="true" icon="icon-application-view-columns"/>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:button classes="f-results-button" nameKey="viewButton" renderMode="image" href="${viewUrl}" icon="icon-application-view-columns"/>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                            </c:choose>
                            <cti:button id="deleteButton_${analysis.analysisId}" nameKey="remove" renderMode="image" icon="icon-cross" href="/bulk/archiveDataAnalysis/list/delete?analysisId=${analysis.analysisId}"/>
                            <d:confirm on="#deleteButton_${analysis.analysisId}" nameKey="deleteConfirmation" argument="${attribName}"/>
                        </div>
                    </td>
                    <cti:dataUpdaterCallback function="Yukon.DataAnalysis.changeStatus"
                                value="ARCHIVE_DATA_ANALYSIS/${analysis.analysisId}/STATUS"/>
                    
                </tr>
            </c:if>
        </c:forEach>
    </tbody>
</table>
<cti:url value="page" var="baseUrl"/>
<tags:pagingResultsControls baseUrl="${baseUrl}" result="${result}"/>
</cti:msgScope>