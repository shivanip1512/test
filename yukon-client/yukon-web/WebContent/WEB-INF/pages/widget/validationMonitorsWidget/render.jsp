<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<c:url var="enabledImg" value="/WebConfig/yukon/Icons/green_circle.gif"/>
<c:url var="disabledImg" value="/WebConfig/yukon/Icons/gray_circle.gif"/>

<cti:msg var="noMonitorsSetupText" key="yukon.web.modules.common.vee.widget.noMonitorsSetup"/>
<cti:msg var="nameHeaderText" key="yukon.web.modules.common.vee.widget.tableHeader.name"/>
<cti:msg var="thresholdHeaderText" key="yukon.web.modules.common.vee.widget.tableHeader.threshold"/>
<cti:msg var="monitoringHeaderText" key="yukon.web.modules.common.vee.widget.tableHeader.monitoring"/>
<cti:msg var="enabledHeaderText" key="yukon.web.modules.common.vee.widget.tableHeader.enabled"/>
<cti:msg var="createNewText" key="yukon.web.modules.common.vee.widget.createNew"/>
<cti:msg var="editActionTitleText" key="yukon.web.modules.common.vee.widget.actionTitle.edit"/>
<cti:msg var="thresholdUnits" key="yukon.web.modules.common.vee.widget.thresholdUnits"/>
<cti:msg var="enableText" key="yukon.common.enable"/> 
<cti:msg var="disableText" key="yukon.common.disable"/> 

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
        <th>${nameHeaderText}</th>
        <th style="text-align:right;">${thresholdHeaderText} (${thresholdUnits})</th>
        <th style="text-align:right;">${monitoringHeaderText}</th>
        <th style="text-align:right;width:80px;">${enabledHeaderText}</th>
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
                
                <a href="${viewValidationMonitorEditorUrl}" title="${editActionTitleText}">
                    ${monitorName}
                </a>
                
            </td>
            
            <%-- threshold --%>
            <td class="${tdClass}" style="text-align:right;">
                ${monitor.reasonableMaxKwhPerDay}
            </td>
            
            <%-- monitoring count --%>
            <td class="${tdClass}" style="text-align:right;">
                <cti:dataUpdaterValue type="VALIDATION_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
            </td>
            
            <%-- enable/disable --%>
			<td class="${tdClass}" style="text-align:right;">
				<c:choose>
					<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
						<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${enabledImg}" imgSrcHover="${enabledImg}" validationMonitorId="${monitorId}" title="${disableText} (${monitorName})"/>
					</c:when>
					<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
						<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${disabledImg}" imgSrcHover="${disabledImg}" validationMonitorId="${monitorId}" title="${enableText} (${monitorName})" checked="false"/>
					</c:when>
				</c:choose>
			</td>
			
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