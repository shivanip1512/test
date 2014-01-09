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
                <tr>
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
                    <c:choose>
                        <%--if analyzing, disable view button, enable delete, status links to progress page--%>
                        <c:when test="${analysis.status == 'RUNNING'}">
                            <td>
                                <cti:url var="analysisProgressUrl" value="/bulk/archiveDataAnalysis/home/processing">
                                    <cti:param name="resultsId" value="${analysis.statusId}"/>
                                    <cti:param name="analysisId" value="${analysis.analysisId}"/>
                                </cti:url>
                                <cti:link href="${analysisProgressUrl}" key="${analysis.status.formatKey}"/>
                            </td>
                            <td>
                                <cti:button nameKey="viewButtonAnalyzing" renderMode="image" disabled="true" icon="icon-application-view-columns"/>
                                <cti:button id="deleteButton_${analysis.analysisId}" nameKey="remove" renderMode="image" icon="icon-cross" href="/bulk/archiveDataAnalysis/list/delete?analysisId=${analysis.analysisId}"/>
                                <d:confirm on="#deleteButton_${analysis.analysisId}" nameKey="deleteConfirmation" argument="${attribName}"/>
                            </td>
                        </c:when>
                        <%-- if complete with some devices successfully analyzed, enable view, enable delete, status doesn't link--%>
                        <%-- if complete with 0 devices successfully analyzed, disable view, enable delete, status doesn't link--%>
                        <c:when test="${analysis.status == 'COMPLETE' || analysis.status == 'INTERRUPTED'}">
                            <td>
                                <i:inline key="${analysis.status}"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${analysis.deviceCount == 0}">
                                        <cti:button nameKey="viewButtonNoDevices" renderMode="image" disabled="true" icon="icon-application-view-columns"/>
                                        <cti:button id="deleteButton_${analysis.analysisId}" nameKey="remove" renderMode="image" icon="icon-cross" href="/bulk/archiveDataAnalysis/list/delete?analysisId=${analysis.analysisId}"/>
                                        <d:confirm on="#deleteButton_${analysis.analysisId}" nameKey="deleteConfirmation" argument="${attribName}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:url var="viewUrl" value="/bulk/archiveDataAnalysis/results/view">
                                            <cti:param name="analysisId" value="${analysis.analysisId}"/>
                                        </cti:url>
                                        <cti:button nameKey="viewButton" renderMode="image" href="${viewUrl}" icon="icon-application-view-columns"/>
                                        <cti:button id="deleteButton_${analysis.analysisId}" nameKey="remove" renderMode="image" icon="icon-cross" href="/bulk/archiveDataAnalysis/list/delete?analysisId=${analysis.analysisId}"/>
                                        <d:confirm on="#deleteButton_${analysis.analysisId}" nameKey="deleteConfirmation" argument="${attribName}"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </c:when>
                        <%--if reading, enable view, enable delete, status links to read progress--%>
                        <c:when test="${analysis.status == 'READING'}">
                            <td>
                                <cti:url var="readProgressUrl" value="/bulk/archiveDataAnalysis/read/readResults">
                                    <cti:param name="resultId" value="${analysis.statusId}"/>
                                    <cti:param name="analysisId" value="${analysis.analysisId}"/>
                                </cti:url>
                                <cti:link href="${readProgressUrl}" key="${analysis.status.formatKey}"/>
                            </td>
                            <td>
                                <cti:url var="viewUrl" value="/bulk/archiveDataAnalysis/results/view">
                                    <cti:param name="analysisId" value="${analysis.analysisId}"/>
                                </cti:url>
                                <cti:button nameKey="viewButton" renderMode="image" href="${viewUrl}" icon="icon-application-view-columns"/>
                                <cti:button id="deleteButton_${analysis.analysisId}" nameKey="remove" renderMode="image" icon="icon-cross" href="/bulk/archiveDataAnalysis/list/delete?analysisId=${analysis.analysisId}"/>
                                <d:confirm on="#deleteButton_${analysis.analysisId}" nameKey="deleteConfirmation" argument="${attribName}"/>
                            </td>
                            <cti:dataUpdaterCallback function="Yukon.ArchiveDataAnalysis.changeStatus"
                                value="ARCHIVE_DATA_ANALYSIS/${analysis.analysisId}/STATUS"/>
                        </c:when>
                    </c:choose>
                </tr>
            </c:if>
        </c:forEach>
    </tbody>
</table>
<c:if test="${pageMe}">
    <cti:url value="page" var="baseUrl"/>
    <tags:pagingResultsControls baseUrl="${baseUrl}" result="${result}"/>
</c:if>
</cti:msgScope>