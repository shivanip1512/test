<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="statusPointMonitorView">
    <div class="column-12-12">
            <div class="column one">
                <%-- MAIN DETAILS --%>
                <tags:sectionContainer2 nameKey="sectionHeader">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">
                            <spring:escapeBody htmlEscape="true">${statusPointMonitor.statusPointMonitorName}</spring:escapeBody>
                        </tags:nameValue2>
                                    
                        <tags:nameValue2 nameKey=".monitoring">
                            <cti:dataUpdaterValue type="STATUS_POINT_MONITORING" identifier="${statusPointMonitor.statusPointMonitorId}/MONITORING_COUNT"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".deviceGroup">
                            <cti:url var="deviceGroupUrl" value="/group/editor/home">
                                <cti:param name="groupName">${statusPointMonitor.groupName}</cti:param>
                            </cti:url>
                            
                            <a href="${deviceGroupUrl}">${statusPointMonitor.groupName}</a>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".attribute">
                            <i:inline key="${statusPointMonitor.attribute}"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".stateGroup">
                            ${statusPointMonitor.stateGroup.stateGroupName}
                        </tags:nameValue2>
                        
                        <%-- enable/disable monitoring --%>
                        <tags:nameValue2 nameKey=".statusPointMonitoring">
                            <i:inline key="${statusPointMonitor.evaluatorStatus}"/>
                        </tags:nameValue2>
                        
                    </tags:nameValueContainer2>
                    
                    <div class="page-action-area">        
                        <form action="/amr/statusPointMonitoring/editPage" method="get">
                            <input type="hidden" name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}">
                            <cti:button nameKey="edit" icon="icon-pencil" type="submit" classes="f-blocker"/>
                        </form>
                    </div>
                
                </tags:sectionContainer2>
            </div>
            <div class="column two nogutter">
                <c:if test="${not empty statusPointMonitor.processors}">
                    <tags:boxContainer2 nameKey="stateActionsTable" id="resTable" styleClass="mediumContainer">
                        <table class="compact-results-table">
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
                                        <td nowrap="nowrap">${prevStateStrings[status.index]}</td>
                                        <td nowrap="nowrap">${nextStateStrings[status.index]}</td>
                                        <td nowrap="nowrap"><i:inline key="${row.actionTypeEnum}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </tags:boxContainer2>
                </c:if>
            </div>
    </div>
</cti:standardPage>