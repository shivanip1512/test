<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="statusPointMonitorView">

    <%-- MAIN DETAILS --%>
    <tags:formElementContainer nameKey="sectionHeader">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <spring:escapeBody htmlEscape="true">${statusPointMonitor.statusPointMonitorName}</spring:escapeBody>
            </tags:nameValue2>
                        
            <tags:nameValue2 nameKey=".monitoring">
                <cti:dataUpdaterValue type="STATUS_POINT_MONITORING" identifier="${statusPointMonitor.statusPointMonitorId}/MONITORING_COUNT"/>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".deviceGroup">
                <cti:url var="deviceGroupUrl" value="/spring/group/editor/home">
                    <cti:param name="groupName">${statusPointMonitor.groupName}</cti:param>
                </cti:url>
                
                <a href="${deviceGroupUrl}">${statusPointMonitor.groupName}</a>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".attribute">
                <i:inline key="${statusPointMonitor.attribute.formatKey}"/>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".stateGroup">
                ${statusPointMonitor.stateGroup.stateGroupName}
            </tags:nameValue2>
            
            <%-- enable/disable monitoring --%>
            <tags:nameValue2 nameKey=".statusPointMonitoring">
                <i:inline key="${statusPointMonitor.evaluatorStatus}"/>
            </tags:nameValue2>
            
        </tags:nameValueContainer2>
        
        <c:choose>
            <c:when test="${not empty statusPointMonitor.processors}">
                <tags:boxContainer2 nameKey="stateActionsTable" id="resTable" styleClass="mediumContainer">
                    <table class="compactResultsTable">
                        <tr>
                            <th><i:inline key=".stateActionsTable.header.prevState"/></th>
                            <th><i:inline key=".stateActionsTable.header.nextState"/></th>
                            <th><i:inline key=".stateActionsTable.header.action"/></th>
                        </tr>
                        
                        <c:forEach items="${statusPointMonitor.processors}" var="row" varStatus="status">
                            <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
                                <td nowrap="nowrap">${prevStateStrings[status.index]}</td>
                                <td nowrap="nowrap">${nextStateStrings[status.index]}</td>
                                <td nowrap="nowrap"><i:inline key="${row.actionTypeEnum}"/></td>
                            </tr>
                        </c:forEach>
                    </table>
                </tags:boxContainer2>
            </c:when>
        </c:choose>
        
        <div class="pageActionArea">        
            <form action="/spring/amr/statusPointMonitoring/editPage" method="get">
                <input type="hidden" name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}">
                <cti:button nameKey="edit" type="submit" styleClass="f_blocker"/>
            </form>
        </div>
    
    </tags:formElementContainer>
</cti:standardPage>