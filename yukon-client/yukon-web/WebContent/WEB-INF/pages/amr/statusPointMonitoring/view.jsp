<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="amr" page="statusPointMonitorView">

    <%-- MAIN DETAILS --%>
    
    <tags:sectionContainer2 nameKey="sectionHeader">
        <tags:nameValueContainer2 tableClass="has-actions">
            <tags:nameValue2 nameKey=".name">${fn:escapeXml(statusPointMonitor.statusPointMonitorName)}</tags:nameValue2>
            <tags:nameValue2 nameKey=".monitoring">
                <cti:dataUpdaterValue type="STATUS_POINT_MONITORING" identifier="${statusPointMonitor.statusPointMonitorId}/MONITORING_COUNT"/>
            </tags:nameValue2>
            
            <tags:nameValueGap2 gapHeight="20px"/>
            
            <tags:nameValue2 nameKey=".attribute">
                <i:inline key="${statusPointMonitor.attribute}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".stateGroup">${fn:escapeXml(statusPointMonitor.stateGroup.stateGroupName)}</tags:nameValue2>
            
            <tags:nameValueGap2 gapHeight="20px"/>
            
            <tags:nameValue2 nameKey=".deviceGroup">
                <cti:url var="deviceGroupUrl" value="/group/editor/home">
                    <cti:param name="groupName">${statusPointMonitor.groupName}</cti:param>
                </cti:url>
                <a href="${deviceGroupUrl}">${fn:escapeXml(statusPointMonitor.groupName)}</a>
                <cm:dropdown triggerClasses="vv">
                    <cti:url var="statusPointGroupReportUrl" value="/amr/reports/groupDevicesReport">
                        <cti:param name="groupName" value="${statusPointMonitor.groupName}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-folder-explore" key="yukon.web.components.button.view.label" href="${statusPointGroupReportUrl}"/>
                    <cti:url var="mapUrl" value="/tools/map/dynamic">
                        <cti:param name="collectionType" value="group"/>
                        <cti:param name="group.name" value="${statusPointMonitor.groupName}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-map-sat" key="yukon.web.components.button.map.label" href="${mapUrl}"/>
                    <cti:url var="collectionActionUrl" value="/bulk/collectionActions" htmlEscape="true">
                        <cti:param name="collectionType" value="group"/>
                        <cti:param name="group.name" value="${statusPointMonitor.groupName}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-cog-go" key="yukon.web.components.button.collectionAction.label" href="${collectionActionUrl}"/>
                </cm:dropdown>
            </tags:nameValue2>
            
            <c:if test="${statusPointMonitor.enabled}"><c:set var="clazz" value="success"/></c:if>
            <c:if test="${!statusPointMonitor.enabled}"><c:set var="clazz" value="error"/></c:if>
            <tags:nameValue2 nameKey=".status" valueClass="${clazz}">
                <i:inline key="${statusPointMonitor.evaluatorStatus}"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
    </tags:sectionContainer2>
    
    <c:if test="${not empty statusPointMonitor.processors}">
        <tags:sectionContainer2 nameKey="stateActionsTable" id="resTable">
            <div class="scroll-lg">
                <table class="compact-results-table dashed">
                    <thead>
                        <tr>
                            <th><i:inline key=".stateActionsTable.header.prevState"/></th>
                            <th><i:inline key=".stateActionsTable.header.nextState"/></th>
                            <th><i:inline key=".stateActionsTable.header.action"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach items="${statusPointMonitor.processors}" var="row" varStatus="status">
                            <tr>
                                <td>${prevStateStrings[status.index]}</td>
                                <td>${nextStateStrings[status.index]}</td>
                                <td><i:inline key="${row.actionTypeEnum}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </tags:sectionContainer2>
    </c:if>
    
    <div class="page-action-area">        
        <cti:url var="editPageUrl" value="/amr/statusPointMonitoring/editPage"/>
        <form action="${editPageUrl}" method="get">
            <input type="hidden" name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}">
            <cti:button nameKey="edit" icon="icon-pencil" type="submit" classes="js-blocker"/>
        </form>
    </div>
</cti:standardPage>