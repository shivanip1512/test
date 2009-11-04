<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<c:url var="cog" value="/WebConfig/yukon/Icons/cog.gif"/>
<c:url var="cogOver" value="/WebConfig/yukon/Icons/cog_over.gif"/>
<c:url var="enabledImg" value="/WebConfig/yukon/Icons/green_circle.gif"/>
<c:url var="disabledImg" value="/WebConfig/yukon/Icons/gray_circle.gif"/>

<cti:msg var="noMonitorsSetupText" key="yukon.web.modules.amr.outageMonitorsWidget.noMonitorsSetup"/>
<cti:msg var="nameHeaderText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.name"/>
<cti:msg var="violationsHeaderText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.violations"/>
<cti:msg var="monitoringHeaderText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.monitoring"/>
<cti:msg var="statusHeaderText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.status"/>
<cti:msg var="enabledHeaderText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.enabled"/>
<cti:msg var="createNewText" key="yukon.web.modules.amr.outageMonitorsWidget.createNew"/>
<cti:msg var="editActionTitleText" key="yukon.web.modules.amr.outageMonitorsWidget.actionTitle.edit"/>
<cti:msg var="outageProcessingActionTitleText" key="yukon.web.modules.amr.outageMonitorsWidget.actionTitle.outageProcessing"/>
<cti:msg var="enableText" key="yukon.common.enable"/> 
<cti:msg var="disableText" key="yukon.common.disable"/> 

<%-- CREATE NEW OUTAGE MONITOR FORM --%>
<form id="createNewOutageMonitorForm_${widgetParameters.widgetId}" action="/spring/amr/outageProcessing/monitorEditor/edit" method="get">
</form>

<%-- ERROR --%>
<c:if test="${not empty outageMonitorsWidgetError}">
  	<div class="errorRed">${outageMonitorsWidgetError}</div>
</c:if>
		
<%-- TABLE --%>
<c:choose>
<c:when test="${fn:length(monitors) > 0}">

<table class="compactResultsTable">
	
	<tr>
		<th style="width:20px;">&nbsp;</th>
		<th>${nameHeaderText}</th>
		<th style="text-align:right;">${violationsHeaderText}</th>
		<th style="text-align:right;">${monitoringHeaderText}</th>
		<th style="text-align:right;width:80px;">${enabledHeaderText}</th>
		
	</tr>

	<c:forEach var="monitor" items="${monitors}">
	
		<c:set var="monitorId" value="${monitor.outageMonitorId}"/>
		<c:set var="monitorName" value="${monitor.outageMonitorName}"/>

		<c:set var="tdClass" value=""/>
		<c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
			<c:set var="tdClass" value="subtleGray"/>
		</c:if>
		
		<tr>
			
			<%-- action icons --%>
			<td>
				
				<%-- monitor widget --%>
				<cti:url var="viewOutageProcessingUrl" value="/spring/amr/outageProcessing/process/process">
					<cti:param name="outageMonitorId" value="${monitorId}"/>
				</cti:url>
				
				<a href="${viewOutageProcessingUrl}" title="${outageProcessingActionTitleText} (${monitorName})" style="text-decoration:none;">
					<img src="${cog}" onmouseover="javascript:this.src='${cogOver}'" onmouseout="javascript:this.src='${cog}'">
				</a>
				
			</td>
			
			<%-- monitor name --%>
			<cti:url var="viewOutageMonitorEditorUrl" value="/spring/amr/outageProcessing/monitorEditor/edit">
				<cti:param name="outageMonitorId" value="${monitorId}"/>
			</cti:url>
				
			<td class="${tdClass}">
				<a href="${viewOutageMonitorEditorUrl}" title="${editActionTitleText}">${monitorName}</a>
			</td>
			
			<%-- violations count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/VIOLATIONS_COUNT"/>
			</td>
			
			<%-- monitoring count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
			</td>
			
			<%-- enable/disable --%>
			<td class="${tdClass}" style="text-align:right;">
				<c:choose>
					<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
						<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${enabledImg}" imgSrcHover="${enabledImg}" outageMonitorId="${monitorId}" title="${disableText} (${monitorName})"/>
					</c:when>
					<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
						<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${disabledImg}" imgSrcHover="${disabledImg}" outageMonitorId="${monitorId}" title="${enableText} (${monitorName})" checked="false"/>
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
	<tags:slowInput myFormId="createNewOutageMonitorForm_${widgetParameters.widgetId}" labelBusy="${createNewText}" label="${createNewText}"/>
</div>



