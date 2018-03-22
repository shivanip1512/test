<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="tamperFlagProcessing">
    
    <%-- MAIN DETAILS --%>
    <div class="column-14-10">
        <div class="column one">
            <tags:sectionContainer2 nameKey="section.mainDetail.sectionHeader">
            
                <tags:nameValueContainer2 tableClass="has-actions" naturalWidth="false">
                    <tags:nameValue2 nameKey=".section.mainDetail.name">${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}</tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".section.mainDetail.violations">
                        <cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${tamperFlagMonitor.tamperFlagMonitorId}/VIOLATIONS_COUNT"/>
                        <cti:dataUpdaterCallback function="yukon.monitor.tamperFlag.enableDisableReadFlags" initialize="true" 
                            value="TAMPER_FLAG_PROCESSING/${tamperFlagMonitor.tamperFlagMonitorId}/VIOLATIONS_COUNT"/>
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
                        <cm:dropdown triggerClasses="vv">
                            <cti:url var="tamperFlagGroupReportUrl" value="/amr/reports/groupDevicesReport">
                                <cti:param name="groupName" value="${tamperFlagMonitor.groupName}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-folder-explore" key="yukon.web.components.button.view.label" href="${tamperFlagGroupReportUrl}"/>
                            <cti:url var="mapUrl" value="/tools/map/dynamic">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${tamperFlagMonitor.groupName}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-map-sat" key="yukon.web.components.button.map.label" href="${mapUrl}"/>
                            <cti:url var="collectionActionUrl" value="/bulk/collectionActions" htmlEscape="true">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${tamperFlagMonitor.groupName}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-cog-go" key="yukon.web.components.button.collectionAction.label" href="${collectionActionUrl}"/>
                        </cm:dropdown>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".section.mainDetail.tamperFlagGroup">
                        <cti:url var="tamperFlagGroupUrl" value="/group/editor/home">
                            <cti:param name="groupName">${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}</cti:param>
                        </cti:url>
                        <a href="${tamperFlagGroupUrl}">${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}</a>
                        <cm:dropdown triggerClasses="vv">
                            <cti:url var="tamperFlagGroupReportUrl" value="/amr/reports/groupDevicesReport">
                                <cti:param name="groupName" value="${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-folder-explore" key="yukon.web.components.button.view.label" href="${tamperFlagGroupReportUrl}"/>
                            <cti:url var="clearTamperFlagGroupUrl" value="/amr/tamperFlagProcessing/process/clearTamperFlagGroup">
                                <cti:param name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-folder-delete" key="yukon.web.components.button.clear.label" href="${clearTamperFlagGroupUrl}"/>
                            <cti:url var="mapUrl" value="/tools/map/dynamic">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-map-sat" key="yukon.web.components.button.map.label" href="${mapUrl}"/>
                            <cti:url var="collectionActionUrl" value="/bulk/collectionActions" htmlEscape="true">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-cog-go" key="yukon.web.components.button.collectionAction.label" href="${collectionActionUrl}"/>
                        </cm:dropdown>
                    </tags:nameValue2>
                    
                    <c:if test="${tamperFlagMonitor.enabled}"><c:set var="clazz" value="success"/></c:if>
                    <c:if test="${!tamperFlagMonitor.enabled}"><c:set var="clazz" value="error"/></c:if>
                    <tags:nameValue2 nameKey=".status" valueClass="${clazz}">
                        <i:inline key="${tamperFlagMonitor.evaluatorStatus}"/>
                    </tags:nameValue2>
                    
                </tags:nameValueContainer2>
                
                <div class="page-action-area">
                    <cti:url var="editUrl" value="/amr/tamperFlagProcessing/${tamperFlagMonitor.tamperFlagMonitorId}/edit"/>
                    <cti:button nameKey="edit" href="${editUrl}" icon="icon-pencil"/>
                </div>
                
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            <%-- READ INTERNAL FLAGS --%>
            <tags:sectionContainer2 id="readInternalFlagsSection" nameKey="section.readInternalFlags">
                <cti:url var="readFlagsUrl" value="/amr/tamperFlagProcessing/process/readFlags"/>
                <form id="readInternalFlagsForm" action="${readFlagsUrl}">
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
                        <tags:alertBox key=".section.readInternalFlags.disabled" type="info" classes="js-read-flags-message dn"/>
                        <cti:msg2 var="readInternalFlagsButtonText" key=".section.readInternalFlags.button"/>
                        <cti:button label="${readInternalFlagsButtonText}" busy="true" type="submit" icon="icon-flag-green" classes="js-read-flags"/>
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
                                    <th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.detail"/></th>
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
                                            <cti:url var="readLogsDetailUrl" value="/bulk/progressReport/detail">
                                                <cti:param name="key" value="${result.cacheKey}"/>
                                            </cti:url>
                                            <a href="${readLogsDetailUrl}"><i:inline key=".section.readFlags.recentReadFlagsResults.viewDetailLink"/></a>
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
                <cti:url var="resetFlagsUrl" value="/amr/tamperFlagProcessing/process/resetFlags"/>
                <form id="resetInternalFlagsForm" action="${resetFlagsUrl}">
        
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
                        <tags:alertBox key=".section.readInternalFlags.disabled" type="info" classes="js-read-flags-message dn"/>
                        <cti:msg2 var="resetInternalFlagsButtonText" key=".section.resetInternalFlags.button"/>
                        <cti:button label="${resetInternalFlagsButtonText}" type="submit" icon="icon-flag-gray" busy="true" classes="js-read-flags"/>
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
                                    <th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.detail"/></th>
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
                                            <cti:url var="resetLogsDetailUrl" value="/bulk/progressReport/detail">
                                                <cti:param name="key" value="${result.cacheKey}"/>
                                            </cti:url>
                                            <a href="${resetLogsDetailUrl}"><i:inline key=".section.resetFlags.recentResetFlagsResults.viewDetailLink"/></a>
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
    
    <cti:includeScript link="/resources/js/pages/yukon.monitor.tamperFlag.js"/>
    
</cti:standardPage>