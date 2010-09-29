<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/amr" prefix="amr" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/ext" prefix="ext" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>

<cti:standardPage module="amr" page="statusPointMonitorView">

    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        <cti:crumbLink><i:inline key=".title" /></cti:crumbLink>
    </cti:breadCrumbs>
    
    <h2><i:inline key=".title" /></h2>
    <br>
    
    <cti:flashScopeMessages/>
    
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
                ${statusPointMonitor.attribute.description}
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
                                <td nowrap="nowrap">${row.actionType}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </tags:boxContainer2>
            </c:when>
        </c:choose>
        
        <div class="pageActionArea">        
            <form id="editMonitorForm" action="/spring/amr/statusPointMonitoring/editPage" method="get">
                <input type="hidden" name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}">
                <tags:slowInput2 formId="editMonitorForm" key="edit"/>
            </form>
        </div>
    
    </tags:formElementContainer>
</cti:standardPage>