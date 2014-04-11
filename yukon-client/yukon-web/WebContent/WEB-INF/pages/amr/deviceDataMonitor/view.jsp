<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="amr" page="deviceDataMonitor.${mode}">
    <%@ include file="shared.jspf"%>

    <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="settings" styleClass="${settings_section_class}">
                    <input type="hidden" id="monitorId" value="${monitor.id}"/>
                    <tags:nameValueContainer2>
                        
                        <%-- monitor name --%>
                        <tags:nameValue2 nameKey=".name">${fn:escapeXml(monitor.name)}</tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".deviceGroup">
                            <cti:url var="deviceGroupUrl" value="/group/editor/home">
                                <cti:param name="groupName">${monitor.groupName}</cti:param>
                            </cti:url>
                            <a href="${deviceGroupUrl}">${fn:escapeXml(monitor.groupName)}</a>
                        </tags:nameValue2>
            
                        <tags:nameValue2 nameKey=".deviceGroupCount">${monitoringCount}</tags:nameValue2>
                        <tags:nameValue2 nameKey=".supportedDevices">
                            ${supportedDevices}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".violationsGroup">
                            ${violationsGroup}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".violationsCount">
                            ${violationsCount}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".monitoring">
                            ${monitoringEnabled}
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
            <div class="column two nogutter">
                <tags:sectionContainer2 nameKey="processors" styleClass="${processors_section_class}">
                    <c:choose>
                        <c:when test="${fn:length(monitor.processors) == 0}">
                            <span class="empty-list"><i:inline key=".noProcessors" /></span>
                        </c:when>
                        <c:otherwise>
                            <div class="scroll-small">
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
            </div>
    </div>

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
