<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="tamperFlagProcessing">
    
    <c:if test="${not empty param.processError}">
        <div class="error">${param.processError}</div>
    </c:if>
    
    <%-- MAIN DETAILS --%>
    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="section.mainDetail.sectionHeader">
            
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".section.mainDetail.name">${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}</tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".section.mainDetail.violations">
                        <cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${tamperFlagMonitor.tamperFlagMonitorId}/VIOLATIONS_COUNT"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".section.mainDetail.monitoring">
                        <cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${tamperFlagMonitor.tamperFlagMonitorId}/MONITORING_COUNT"/>
                    </tags:nameValue2>
                    
                    <tags:nameValueGap2 gapHeight="20px"/>
                    
                    <tags:nameValue2 nameKey=".section.mainDetail.deviceGroup">
                        <cti:url var="deviceGroupUrl" value="/group/editor/home">
                            <cti:param name="groupName">${tamperFlagMonitor.groupName}</cti:param>
                        </cti:url>
                        <a href="${deviceGroupUrl}">${fn:escapeXml(tamperFlagMonitor.groupName)}</a>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".section.mainDetail.tamperFlagGroup">
                        <cti:url var="tamperFlagGroupUrl" value="/group/editor/home">
                            <cti:param name="groupName">${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}</cti:param>
                        </cti:url>
                        <div class="stacked"><a href="${tamperFlagGroupUrl}">${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}</a></div>
                        
                        <div class="clearfix button-group">
                            
                            <cti:url var="tamperFlagGroupReportUrl" value="/amr/reports/groupDevicesReport">
                                <cti:param name="groupName" value="${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}"/>
                            </cti:url>
                            <cti:button nameKey="view" href="${tamperFlagGroupReportUrl}" icon="icon-folder-explore"/>
                            
                            <cti:url var="clearTamperFlagGroupUrl" value="/amr/tamperFlagProcessing/process/clearTamperFlagGroup">
                                <cti:param name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}"/>
                            </cti:url>
                            <cti:button nameKey="clear" href="${clearTamperFlagGroupUrl}" icon="icon-folder-delete"/>
                            <cti:url var="collectionActionUrl" value="/bulk/collectionActions" htmlEscape="true">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${tamperFlagGroupBase}${tamperFlagMonitor.tamperFlagMonitorName}"/>
                            </cti:url>
                            <cti:button nameKey="collectionAction" href="${collectionActionUrl}" icon="icon-cog-go"/>
                        </div>
                    </tags:nameValue2>
                    
                </tags:nameValueContainer2>
                
                <div class="page-action-area">
                    <cti:url var="editUrl" value="/amr/tamperFlagProcessing/edit">
                        <cti:param name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}"/>
                    </cti:url>
                    <cti:button nameKey="edit" href="${editUrl}" icon="icon-pencil"/>
                </div>
                
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            <%-- READ INTERNAL FLAGS --%>
            <tags:sectionContainer2 id="readInternalFlagsSection" nameKey="section.readInternalFlags">
            
                <form id="readInternalFlagsForm" action="/amr/tamperFlagProcessing/process/readFlags">
                    <input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}">
                    <%-- note --%>
                    <table class="stacked">
                        <tr>
                            <td class="strong-label-small"><i:inline key=".section.readInternalFlags.noteLabel"/></td>
                            <td class="detail">
                                <cti:url var="viewPointsUrl" value="/bulk/addPoints/home">
                                    <cti:mapParam value="${tamperFlagGroupDeviceCollection.collectionParameters}"/>
                                </cti:url>
                                <i:inline key=".section.readInternalFlags.noteBody"/>
                                <i:inline key=".section.readInternalFlags.viewPointsDescription"/>
                                <a href="${viewPointsUrl}"><i:inline key=".section.readInternalFlags.viewPointsLink"/></a>
                            </td>
                        </tr>
                    </table>
                    
                    <%-- read internal flags button --%>
                    <div class="page-action-area stacked">
                        <cti:msg2 var="readInternalFlagsButtonText" key=".section.readInternalFlags.button"/>
                        <cti:button label="${readInternalFlagsButtonText}" busy="true" type="submit" icon="icon-flag-green"/>
                    </div>
                    
                    <%-- read ok --%>
                    <c:if test="${param.readOk}">
                        <div class="success stacked"><i:inline key=".section.readInternalFlags.readOk"/></div>
                    </c:if>
                    </span>
                    
                    <%-- recent reads --%>
                    <c:if test="${fn:length(readResults) > 0}">
                        <table class="compact-results-table">
                            <thead>
                                <tr>
                                    <th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.dateTime"/></th>
                                    <th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.successCount"/></th>
                                    <th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.failureCount"/></th>
                                    <th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.unsupportedCount"/></th>
                                    <th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.detail"/></th>
                                    <th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.status"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="result" items="${readResults}">
                                    <tr>
                                    
                                        <td>
                                            <cti:formatDate type="BOTH" value="${result.startTime}"/>
                                        </td>
                                        <td>
                                            <cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/SUCCESS_COUNT"/>
                                        </td>
                                        <td>
                                            <cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/FAILURE_COUNT"/>
                                        </td>
                                        <td>
                                            <cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/UNSUPPORTED_COUNT"/>
                                        </td>
                                        <td>
                                            <cti:url var="readLogsDetailUrl" value="/group/groupMeterRead/resultDetail">
                                                <cti:param name="resultKey" value="${result.key}"/>
                                            </cti:url>
                                            <a href="${readLogsDetailUrl}"><i:inline key=".section.readFlags.recentReadFlagsResults.viewDetailLink"/></a>
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
            
            </tags:sectionContainer2>
            
            <%-- RESET INTERNAL FLAGS --%>
            <tags:sectionContainer2 id="resetInternalFlagsSection" nameKey="section.resetInternalFlags">
            
                <form id="resetInternalFlagsForm" action="/amr/tamperFlagProcessing/process/resetFlags">
        
                    <input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}">
            
                    <%-- note --%>
                    <table class="stacked">
                        <tr>
                            <td class="strong-label-small"><i:inline key=".section.resetInternalFlags.noteLabel"/></td>
                            <td class="detail"><cti:msg2 key=".section.resetInternalFlags.noteBody"/></td>
                        </tr>
                    </table>
                    
                    <%-- reset internal flags button --%>
                    <div class="page-action-area stacked">
                        <cti:msg2 var="resetInternalFlagsButtonText" key=".section.resetInternalFlags.button"/>
                        <cti:button label="${resetInternalFlagsButtonText}" type="submit" icon="icon-flag-gray" busy="true"/>
                    </div>
                    
                    <%-- reset ok --%>
                    <c:if test="${param.resetOk}">
                        <div class="success stacked"><i:inline key=".section.resetInternalFlags.resetOk"/></div>
                    </c:if>
                    
                    <%-- recent resets --%>
                    <c:if test="${fn:length(resetResults) > 0}">
                        <table class="compact-results-table">
                            <thead>
                                <tr>
                                    <th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.dateTime"/></th>
                                    <th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.successCount"/></th>
                                    <th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.failureCount"/></th>
                                    <th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.detail"/></th>
                                    <th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.status"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                            
                                <c:forEach var="result" items="${resetResults}">
                                    <tr>
                                    
                                        <td>
                                            <cti:formatDate type="BOTH" value="${result.startTime}"/>
                                        </td>
                                        <td>
                                            <cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/SUCCESS_COUNT"/>
                                        </td>
                                        <td>
                                            <cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/FAILURE_COUNT"/>
                                        </td>
                                        <td>
                                            <cti:url var="resetLogsDetailUrl" value="/group/commander/resultDetail">
                                                <cti:param name="resultKey" value="${result.key}"/>
                                            </cti:url>
                                            <a href="${resetLogsDetailUrl}"><i:inline key=".section.resetFlags.recentResetFlagsResults.viewDetailLink"/></a>
                                        </td>
                                        <td>
                                            <cti:classUpdater type="COMMANDER" identifier="${result.key}/STATUS_CLASS">
                                                <cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/STATUS_TEXT"/>
                                            </cti:classUpdater>
                                        </td>
                                    
                                    </tr>
                                </c:forEach>
                                </tbody>
                        </table>
                    </c:if>
                </form>
            
            </tags:sectionContainer2>
        </div>
    </div>
    
</cti:standardPage>