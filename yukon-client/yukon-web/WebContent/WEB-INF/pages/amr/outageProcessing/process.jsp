<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="outageProcessing">
    
    <div class="column-14-10">
        <div class="column one">
            <%-- MAIN DETAILS --%>
            <tags:sectionContainer2 nameKey="mainDetail.sectionHeader">
            
                <tags:nameValueContainer2 tableClass="has-actions" naturalWidth="false">
                    <tags:nameValue2 nameKey=".mainDetail.name">${fn:escapeXml(outageMonitor.name)}</tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".mainDetail.violations">
                        <cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${outageMonitor.outageMonitorId}/VIOLATIONS_COUNT"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".mainDetail.monitoring">
                        <cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${outageMonitor.outageMonitorId}/MONITORING_COUNT"/>
                    </tags:nameValue2>
                    
                    <tags:nameValueGap2 gapHeight="20px"/>
                    
                    <tags:nameValue2 nameKey=".mainDetail.numberOfOutages">
                        <i:inline key=".mainDetail.numberOfOutages.outages" arguments="${outageMonitor.numberOfOutages}"/> 
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".mainDetail.timePeriod">
                        <i:inline key=".mainDetail.timePeriod.days" arguments="${outageMonitor.timePeriodDays}"/>
                    </tags:nameValue2>
                    
                    <tags:nameValueGap2 gapHeight="20px"/>
                    
                    <tags:nameValue2 nameKey=".mainDetail.deviceGroup">
                        <cti:url var="deviceGroupUrl" value="/group/editor/home">
                            <cti:param name="groupName">${outageMonitor.groupName}</cti:param>
                        </cti:url>
                        <a href="${deviceGroupUrl}">${fn:escapeXml(outageMonitor.groupName)}</a>
                        <cm:dropdown triggerClasses="vv">
                            <cti:url var="outagesGroupReportUrl" value="/amr/reports/groupDevicesReport">
                                <cti:param name="groupName" value="${outageMonitor.groupName}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-folder-explore" key="yukon.web.components.button.view.label" href="${outagesGroupReportUrl}"/>
                            <cti:url var="mapUrl" value="/tools/map/dynamic">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${outageMonitor.groupName}"/>
                                <cti:param name="monitorType" value="Outage"/>
                                <cti:param name="monitorId" value="${outageMonitor.outageMonitorId}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-map-sat" key="yukon.web.components.button.map.label" href="${mapUrl}"/>
                            <cti:url var="collectionActionUrl" value="/bulk/collectionActions">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${outageMonitor.groupName}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-cog-go" key="yukon.web.components.button.collectionAction.label" href="${collectionActionUrl}"/>
                        </cm:dropdown>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".mainDetail.outagesGroup">
                        <cti:url var="outageGroupUrl" value="/group/editor/home">
                            <cti:param name="groupName">${outageGroupBase}${outageMonitor.name}</cti:param>
                        </cti:url>
                        <a href="${outageGroupUrl}">${outageGroupBase}${fn:escapeXml(outageMonitor.name)}</a>
                        <cm:dropdown triggerClasses="vv">
                            <cti:url var="outagesGroupReportUrl" value="/amr/reports/groupDevicesReport">
                                <cti:param name="groupName" value="${outageGroupBase}${outageMonitor.name}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-folder-explore" key="yukon.web.components.button.view.label" href="${outagesGroupReportUrl}"/>
                            <cti:url var="clearOutageGroupUrl" value="/amr/outageProcessing/process/clearOutagesGroup">
                                <cti:param name="outageMonitorId" value="${outageMonitor.outageMonitorId}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-folder-delete" key="yukon.web.components.button.clear.label" href="${clearOutageGroupUrl}"/>
                            <cti:url var="mapUrl" value="/tools/map/dynamic">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${outageGroupBase}${outageMonitor.name}"/>
                                <cti:param name="monitorType" value="Outage"/>
                                <cti:param name="monitorId" value="${outageMonitor.outageMonitorId}"/>
                                <cti:param name="violationsOnly" value="true"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-map-sat" key="yukon.web.components.button.map.label" href="${mapUrl}"/>
                            <cti:url var="collectionActionUrl" value="/bulk/collectionActions">
                                <cti:param name="collectionType" value="group"/>
                                <cti:param name="group.name" value="${outageGroupBase}${outageMonitor.name}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-cog-go" key="yukon.web.components.button.collectionAction.label" href="${collectionActionUrl}"/>
                        </cm:dropdown>
                    </tags:nameValue2>
                    
                    <c:if test="${outageMonitor.enabled}"><c:set var="clazz" value="success"/></c:if>
                    <c:if test="${!outageMonitor.enabled}"><c:set var="clazz" value="error"/></c:if>
                    <tags:nameValue2 nameKey=".status" valueClass="${clazz}">
                        <i:inline key="${outageMonitor.evaluatorStatus}"/>
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
                
            </tags:sectionContainer2>
            
        </div>
        <div class="column two nogutter">
            <%-- READ OUTAGE LOGS --%>
            <tags:sectionContainer2 nameKey="readOutageLogs">
                <cti:url var="readOutageLogsUrl" value="/amr/outageProcessing/process/readOutageLogs"/>
                <form action="${readOutageLogsUrl}">
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
                        <cti:button nameKey="readOutageLogs" busy="true" type="submit" icon="icon-read"/>
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
                                    <th><i:inline key=".recentReadLogsResults.detail"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="result" items="${readResults}">
                                    <tr>
                                        <td><cti:formatDate type="BOTH" value="${result.startTime}"/></td>
                                        <td>
                                            <cti:url var="readLogsDetailUrl" value="/collectionActions/progressReport/view">
                                                <cti:param name="key" value="${result.cacheKey}"/>
                                            </cti:url>
                                            <a href="${readLogsDetailUrl}"><i:inline key=".recentReadLogsResults.viewDetailLink"/></a>
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