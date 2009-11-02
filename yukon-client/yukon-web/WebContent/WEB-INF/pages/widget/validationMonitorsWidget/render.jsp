<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:msg var="noMonitorsSetupText" key="yukon.web.modules.amr.validationMonitorsWidget.noMonitorsSetup"/>
<cti:msg var="nameText" key="yukon.web.modules.amr.validationMonitorsWidget.tableHeader.name"/>
<cti:msg var="thresholdText" key="yukon.web.modules.amr.validationMonitorsWidget.tableHeader.threshold"/>
<cti:msg var="monitoringText" key="yukon.web.modules.amr.validationMonitorsWidget.tableHeader.monitoring"/>
<cti:msg var="statusText" key="yukon.web.modules.amr.validationMonitorsWidget.tableHeader.status"/>
<cti:msg var="createNewText" key="yukon.web.modules.amr.validationMonitorsWidget.createNew"/>
<cti:msg var="thresholdUnits" key="yukon.web.modules.amr.validationMonitorsWidget.thresholdUnits"/>

<%-- CREATE NEW VALIDATION MONITOR FORM --%>
<form id="createNewValidationMonitorForm_${widgetParameters.widgetId}" action="/spring/common/vee/monitor/edit" method="get">
</form>

<%-- ERROR --%>
<c:if test="${not empty validationMonitorsWidgetError}">
    <div class="errorRed">${validationMonitorsWidgetError}</div>
</c:if>
        
<%-- TABLE --%>
<c:choose>
<c:when test="${fn:length(monitors) > 0}">

<table class="compactResultsTable">
    
    <tr>
        <th>${nameText}</th>
        <th style="text-align:right;">${thresholdText} (${thresholdUnits})</th>
        <th style="text-align:right;">${monitoringText}</th>
        <th style="text-align:center;width:80px;">${statusText}</th>
    </tr>

    <c:forEach var="monitor" items="${monitors}">
    
        <c:set var="monitorId" value="${monitor.validationMonitorId}"/>
        <c:set var="monitorName" value="${monitor.name}"/>

        <c:set var="tdClass" value=""/>
        <c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
            <c:set var="tdClass" value="subtleGray"/>
        </c:if>
        
        <tr>
            
            <td>
            
                <%-- monitor name --%>
                <cti:url var="viewValidationMonitorEditorUrl" value="/spring/common/vee/monitor/edit">
                    <cti:param name="validationMonitorId" value="${monitorId}"/>
                </cti:url>
                
                <a href="${viewValidationMonitorEditorUrl}">
                    ${monitorName}
                </a>
                &nbsp;&nbsp;
                
            </td>
            
            <%-- threshold --%>
            <td class="${tdClass}" style="text-align:right;">
                ${monitor.reasonableMaxKwhPerDay}
            </td>
            
            <%-- monitoring count --%>
            <td class="${tdClass}" style="text-align:right;">
                <cti:dataUpdaterValue type="VALIDATION_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
            </td>
            
            <%-- status --%>
            <td class="${tdClass}" style="text-align:center;">${monitor.evaluatorStatus.description}</td>
            
        </tr>
    
    </c:forEach>

</table>
</c:when>

<c:otherwise>
    ${noMonitorsSetupText}
</c:otherwise>
</c:choose>

<div style="text-align:right;padding-top:5px;">
    <tags:slowInput myFormId="createNewValidationMonitorForm_${widgetParameters.widgetId}" labelBusy="${createNewText}" label="${createNewText}"/>
</div>