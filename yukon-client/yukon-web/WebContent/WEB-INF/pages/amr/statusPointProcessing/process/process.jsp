<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<cti:standardPage module="amr" page="statusPointProcessing">

    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
    </cti:breadCrumbs>
    
    <h2><cti:msg2 key=".title" /></h2>
    <br>
    
    <c:if test="${not empty param.processError}">
        <div class="errorRed">${param.processError}</div>
    </c:if>
    
    <%-- MAIN DETAILS --%>
    <tags:formElementContainer nameKey="sectionHeader">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                ${statusPointMonitor.statusPointMonitorName}
            </tags:nameValue2>
                        
            <tags:nameValue2 nameKey=".monitoring">
                <cti:dataUpdaterValue type="STATUS_POINT_PROCESSING" identifier="${statusPointMonitor.statusPointMonitorId}/MONITORING_COUNT"/>
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
                ${statusPointMonitorStatus}
            </tags:nameValue2>
            
        </tags:nameValueContainer2>
        
        <br>
        
        <c:choose>
            <c:when test="${fn:length(statusPointMonitor.statusPointMonitorMessageProcessors) == 0}">
                <cti:msg2 key=".stateActionsTable.noProcessors" />
            </c:when>
            <c:otherwise>
                <tags:boxContainer2 nameKey="stateActionsTable" id="resTable" styleClass="pointMonitorContainer">
                    <table class="compactResultsTable">
                        <tr>
                            <th><cti:msg2 key=".stateActionsTable.prevState"/></th>
                            <th><cti:msg2 key=".stateActionsTable.nextState"/></th>
                            <th><cti:msg2 key=".stateActionsTable.action"/></th>
                        </tr>
                        
                        <c:forEach items="${statusPointMonitor.statusPointMonitorMessageProcessors}" var="row" varStatus="status">
                            <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
                                <td nowrap="nowrap">${prevStateStrings[status.index]}</td>
                                <td nowrap="nowrap">${nextStateStrings[status.index]}</td>
                                <td nowrap="nowrap">${row.actionType}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </tags:boxContainer2>
            </c:otherwise>
        </c:choose>
        
        <br>
        
        <form id="editMonitorForm" action="/spring/amr/statusPointProcessing/edit" method="get">
            <input type="hidden" name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}">
        </form>
        <tags:slowInput2 formId="editMonitorForm" key="edit" />
    
    </tags:formElementContainer>
</cti:standardPage>