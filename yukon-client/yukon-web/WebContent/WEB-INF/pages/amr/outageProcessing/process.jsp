<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="outageProcessing">
    
    <c:if test="${not empty param.processError}">
        <div class="error">${param.processError}</div>
    </c:if>
    
    <div class="column-12-12">
        <div class="column one">
            <%-- MAIN DETAILS --%>
            <cti:msg2 var="mainDetailSectionHeaderText" key=".mainDetail.sectionHeader" />
            <tags:sectionContainer title="${mainDetailSectionHeaderText}">
            
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".mainDetail.name">${fn:escapeXml(outageMonitor.outageMonitorName)}</tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".mainDetail.violations">
                        <cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${outageMonitor.outageMonitorId}/VIOLATIONS_COUNT"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".mainDetail.monitoring">
                        <cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${outageMonitor.outageMonitorId}/MONITORING_COUNT"/>
                    </tags:nameValue2>
                    
                    <tags:nameValueGap2 gapHeight="20px"/>
                    
                    <tags:nameValue2 nameKey=".mainDetail.numberOfOutages">
                        ${outageMonitor.numberOfOutages}
                        <i:inline key=".mainDetail.numberOfOutages.outages" arguments="${outageMonitor.numberOfOutages}"/> 
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".mainDetail.timePeriod">
                        ${outageMonitor.timePeriodDays} 
                        <i:inline key=".mainDetail.timePeriod.days" arguments="${outageMonitor.timePeriodDays}"/>
                    </tags:nameValue2>
                    
                    <tags:nameValueGap2 gapHeight="20px"/>
                    
                    <tags:nameValue2 nameKey=".mainDetail.deviceGroup">
                        <cti:url var="deviceGroupUrl" value="/group/editor/home">
                            <cti:param name="groupName">${fn:escapeXml(outageMonitor.groupName)}</cti:param>
                        </cti:url>
                        <a href="${deviceGroupUrl}">${fn:escapeXml(outageMonitor.groupName)}</a>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".mainDetail.outagesGroup">
                        <cti:url var="outageGroupUrl" value="/group/editor/home">
                            <cti:param name="groupName">${outageGroupBase}${fn:escapeXml(outageMonitor.outageMonitorName)}</cti:param>
                        </cti:url>
                        <div class="stacked"><a href="${outageGroupUrl}">${outageGroupBase}${fn:escapeXml(outageMonitor.outageMonitorName)}</a></div>
                        
                        <div class="clearfix">
                            
                            <cti:url var="outagesGroupReportUrl" value="/amr/reports/groupDevicesReport">
                                <cti:param name="groupName" value="${outageGroupBase}${fn:escapeXml(outageMonitor.outageMonitorName)}"/>
                            </cti:url>
                            <cti:button nameKey="view" href="${outagesGroupReportUrl}" icon="icon-folder-explore" classes="left"/>
                            
                            <cti:url var="clearOutagesGroupUrl" value="/amr/outageProcessing/process/clearOutagesGroup">
                                <cti:param name="outageMonitorId" value="${outageMonitor.outageMonitorId}"/>
                            </cti:url>
                            <cti:button nameKey="clear" href="${clearOutagesGroupUrl}" icon="icon-folder-delete" classes="middle"/>
                            <cti:url var="collectionActionUrl" value="/bulk/collectionActions">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${outageGroupBase}${fn:escapeXml(outageMonitor.outageMonitorName)}"/>
                            </cti:url>
                            <cti:button nameKey="collectionAction" href="${collectionActionUrl}" icon="icon-cog-go" classes="right"/>
                        </div>
                    </tags:nameValue2>
                    
                </tags:nameValueContainer2>
                
                <div class="page-action-area">
                    <cti:url var="editUrl" value="/amr/outageProcessing/monitorEditor/edit">
                        <cti:param name="outageMonitorId" value="${outageMonitor.outageMonitorId}"/>
                    </cti:url>
                    <cti:button nameKey="edit" href="${editUrl}" icon="icon-pencil"/>
                    <cti:formatDate var="startDate" type="DATE" value="${reportStartDate}"/>
                    <cti:url value="/analysis/Reports.jsp" var="reportUrl">
                        <cti:param name="groupType" value="METERING"/>
                        <cti:param name="type" value="METER_OUTAGE_LOG"/>
                        <cti:param name="selectedReportFilter" value="GROUPS"/>
                        <cti:param name="selectedReportFilterValues" value="${fn:escapeXml(outageMonitor.groupName)}"/>
                        <cti:param name="startDate" value="${startDate}"/>
                    </cti:url>
                    <a href="${reportUrl}" class="fr"><i:inline key=".reportingLink"/></a>
                </div>
                
            </tags:sectionContainer>
            
        </div>
        <div class="column two nogutter">
            <%-- READ OUTAGE LOGS --%>
            <cti:msg2 var="readOutageLogsSectionTitleText" key=".readOutageLogs.title" />
            <tags:sectionContainer id="readOutageLogsSection" title="${readOutageLogsSectionTitleText}">
                <cti:url var="readOutageLogsUrl" value="/amr/outageProcessing/process/readOutageLogs"/>
                <form id="readOutagesForm" action="${readOutageLogsUrl}">
                    <input type="hidden" name="outageMonitorId" value="${outageMonitor.outageMonitorId}">
                    <%-- note --%>
                    <table class="stacked">
                        <tr>
                            <td class="strong-label-small"><i:inline key=".readOutageLogs.noteLabel" /></td>
                            <td class="detail"><cti:msg2 key=".readOutageLogs.noteBody"/></td>
                        </tr>
                    </table>
                
                    <%-- remove after read checkbox --%>
                    <div class="page-action-area stacked">
                        <cti:button nameKey="readOutageLogs" busy="true" type="submit" icon="icon-bullet-go"/>
                        <label><input type="checkbox" name="removeFromOutageGroupAfterRead" checked><i:inline key=".removeAfterRead" /></label>
                    </div>
                    
                    <%-- read ok --%>
                    <c:if test="${param.readOk}">
                        <div class="success stacked"><i:inline key=".readOutageLogs.readOk" /></div>
                    </c:if>
                    
                    <%-- recent reads --%>
                    <c:if test="${fn:length(readResults) > 0}">
                        <table class="compact-results-table">
                            <thead>
                                <tr>
                                    <th><i:inline key=".recentReadLogsResults.dateTime"/></th>
                                    <th><i:inline key=".recentReadLogsResults.successCount"/></th>
                                    <th><i:inline key=".recentReadLogsResults.failureCount"/></th>
                                    <th><i:inline key=".recentReadLogsResults.unsupportedCount"/></th>
                                    <th><i:inline key=".recentReadLogsResults.detail"/></th>
                                    <th><i:inline key=".recentReadLogsResults.status"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="result" items="${readResults}">
                                    <tr>
                                    
                                        <td><cti:formatDate type="BOTH" value="${result.startTime}"/></td>
                                        <td><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/SUCCESS_COUNT"/></td>
                                        <td><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/FAILURE_COUNT"/></td>
                                        <td><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/UNSUPPORTED_COUNT"/></td>
                                        <td>
                                            <cti:url var="readLogsDetailUrl" value="/group/groupMeterRead/resultDetail">
                                                <cti:param name="resultKey" value="${result.key}"/>
                                            </cti:url>
                                            <a href="${readLogsDetailUrl}"><i:inline key=".recentReadLogsResults.viewDetailLink"/></a>
                                        </td>
                                        <td>
                                            <cti:classUpdater type="GROUP_METER_READ" identifier="${result.key}/STATUS_CLASS">
                                                <cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/STATUS_TEXT"/>
                                            </cti:classUpdater>
                                        </td>
                                    
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </form>
                
            </tags:sectionContainer>
        </div>
    </div>
    
</cti:standardPage>