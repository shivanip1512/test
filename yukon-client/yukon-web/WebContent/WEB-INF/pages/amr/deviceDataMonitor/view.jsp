<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="amr" page="deviceDataMonitor.${mode}">

    <%@ include file="shared.jspf"%>
    <cti:msgScope paths=",yukon.web.components.button">
    <tags:sectionContainer2 nameKey="settings" styleClass="${settings_section_class}">
        <input type="hidden" id="monitor-id" value="${monitor.id}"/>
        <tags:nameValueContainer2 tableClass="has-actions">
            
            <tags:nameValue2 nameKey=".name">${fn:escapeXml(monitor.name)}</tags:nameValue2>
            <tags:nameValue2 nameKey=".violations">${violationsCount}</tags:nameValue2>
            <tags:nameValue2 nameKey=".monitoring">${monitoringCount}</tags:nameValue2>
            
            <tags:nameValueGap2 gapHeight="20px"/>
            
            <tags:nameValue2 nameKey=".supportedDevices">${supportedDevices}</tags:nameValue2>
            
            <tags:nameValueGap2 gapHeight="20px"/>
            
            <tags:nameValue2 nameKey=".deviceGroup">
                <cti:url value="/group/editor/home" var="deviceGroupUrl">
                    <cti:param name="groupName">${monitor.groupName}</cti:param>
                </cti:url>
                <cti:url value="/amr/reports/groupDevicesReport" var="deviceGrouphtmlReportUrl">
                    <cti:param name="groupName">${monitor.groupName}</cti:param>
                </cti:url>
                <span class="dn js-monitor-group"><input type="hidden" value="${fn:escapeXml(monitor.groupName)}"></span>
                <a href="${deviceGroupUrl}">${fn:escapeXml(monitor.groupName)}</a>
                <cm:dropdown triggerClasses="fr">
                    <cm:dropdownOption icon="icon-folder-explore" key=".view.label" href="${deviceGrouphtmlReportUrl}"/>
                    <cti:url var="mapUrl" value="/tools/map/dynamic">
                        <cti:param name="collectionType" value="group"/>
                        <cti:param name="group.name" value="${monitor.groupName}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-map-sat" key=".map.label" href="${mapUrl}"/>
                    <cti:url var="collectionActionUrl" value="/bulk/collectionActions" htmlEscape="true">
                        <cti:param name="collectionType" value="group"/>
                        <cti:param name="group.name" value="${monitor.groupName}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-cog-go" key=".collectionAction.label" href="${collectionActionUrl}"/>
                </cm:dropdown>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".violationsGroup">
                ${violationsGroup}
                <cm:dropdown triggerClasses="fr">
                    <cm:dropdownOption icon="icon-folder-explore" key=".view.label" href="${htmlReportUrl}"/>
                    <cti:url var="mapUrl" value="/tools/map/dynamic">
                        <cti:param name="collectionType" value="group"/>
                        <cti:param name="group.name" value="${violationsDeviceGroupPath}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-map-sat" key=".map.label" href="${mapUrl}"/>
                    <cti:url var="collectionActionUrl" value="/bulk/collectionActions" htmlEscape="true">
                        <cti:param name="collectionType" value="group"/>
                        <cti:param name="group.name" value="${violationsDeviceGroupPath}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-cog-go" key=".collectionAction.label" href="${collectionActionUrl}"/>
                </cm:dropdown>
            </tags:nameValue2>
            <c:if test="${monitor.enabled}"><c:set var="clazz" value="success"/></c:if>
            <c:if test="${!monitor.enabled}"><c:set var="clazz" value="error"/></c:if>
            <tags:nameValue2 nameKey=".status" valueClass="${clazz}">${monitoringEnabled}</tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
    </cti:msgScope>
    
    <tags:sectionContainer2 nameKey="processors" styleClass="${processors_section_class}">
        <c:choose>
            <c:when test="${fn:length(monitor.processors) == 0}">
                <span class="empty-list"><i:inline key=".noProcessors" /></span>
            </c:when>
            <c:otherwise>
                <div class="scroll-sm">
                    <table class="compact-results-table dashed with-form-controls">
                        <thead>
                            <tr>
                                <th><i:inline key=".processors.attribute" /></th>
                                <th><i:inline key=".processors.stateGroup" /></th>
                                <th><i:inline key=".processors.state" /></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="processor" items="${monitor.processors}">
                                <tr>
                                    <td><i:inline key="${processor.attribute}"/></td>
                                    <td>${processor.stateGroup}</td>
                                    <td>${processor.state.stateText}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer2>

    <%-- update / enable_disable / delete / cancel --%>
    <div class="page-action-area">
        <cti:url value="/amr/deviceDataMonitor/editPage" var="editUrl">
            <cti:param name="monitorId" value="${monitor.id}"/>
        </cti:url>
        <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
        <cti:url var="startUrl" value="/meter/start"/>
        <cti:button nameKey="back" href="${startUrl}"/>
    </div>

</cti:standardPage>
