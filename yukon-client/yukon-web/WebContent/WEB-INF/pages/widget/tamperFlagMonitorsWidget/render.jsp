<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<c:url var="cog" value="/WebConfig/yukon/Icons/cog.gif"/>
<c:url var="cogOver" value="/WebConfig/yukon/Icons/cog_over.gif"/>
<c:url var="enabledImg" value="/WebConfig/yukon/Icons/green_circle.gif"/>
<c:url var="disabledImg" value="/WebConfig/yukon/Icons/gray_circle.gif"/>

<cti:msg var="noMonitorsSetupText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.noMonitorsSetup"/>
<cti:msg var="nameHeaderText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.tableHeader.name"/>
<cti:msg var="violationsHeaderText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.tableHeader.violations"/>
<cti:msg var="monitoringHeaderText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.tableHeader.monitoring"/>
<cti:msg var="enabledHeaderText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.tableHeader.enabled"/>
<cti:msg var="createNewText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.createNew"/>
<cti:msg var="editActionTitleText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.actionTitle.edit"/>
<cti:msg var="tamperFlagProcessingActionTitleText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.actionTitle.tamperFlagProcessing"/>
<cti:msg var="enableText" key="yukon.common.enable"/> 
<cti:msg var="disableText" key="yukon.common.disable"/> 

<%-- CREATE NEW TAMPERFLAG MONITOR FORM --%>
<form id="createNewTamperFlagMonitorForm_${widgetParameters.widgetId}" action="/spring/amr/tamperFlagProcessing/edit" method="get">
</form>

<%-- ERROR --%>
<c:if test="${not empty tamperFlagMonitorsWidgetError}">
  	<div class="errorRed">${tamperFlagMonitorsWidgetError}</div>
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
	
		<c:set var="monitorId" value="${monitor.tamperFlagMonitorId}"/>
		<c:set var="monitorName" value="${monitor.tamperFlagMonitorName}"/>

		<c:set var="tdClass" value=""/>
		<c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
			<c:set var="tdClass" value="subtleGray"/>
		</c:if>
		
		<tr>
			
			<%-- action icons --%>
			<td>
			
				<%-- monitor widget --%>
				<cti:url var="viewTamperFlagProcessingUrl" value="/spring/amr/tamperFlagProcessing/process/process">
					<cti:param name="tamperFlagMonitorId" value="${monitorId}"/>
				</cti:url>
				
				<a href="${viewTamperFlagProcessingUrl}" title="${tamperFlagProcessingActionTitleText} (${monitorName})" style="text-decoration:none;">
					<img src="${cog}" onmouseover="javascript:this.src='${cogOver}'" onmouseout="javascript:this.src='${cog}'">
				</a>
				
			</td>
			
			<%-- monitor name --%>
			<cti:url var="viewTamperFlagMonitorEditorUrl" value="/spring/amr/tamperFlagProcessing/edit">
				<cti:param name="tamperFlagMonitorId" value="${monitorId}"/>
			</cti:url>
				
			<td class="${tdClass}">
				<a href="${viewTamperFlagMonitorEditorUrl}" title="${editActionTitleText}">${monitorName}</a>
			</td>
			
			<%-- violations count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${monitorId}/VIOLATIONS_COUNT"/>
			</td>
			
			<%-- monitoring count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
			</td>
			
			<%-- enable/disable --%>
			<td class="${tdClass}" style="text-align:right;">
				<c:choose>
					<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
						<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${enabledImg}" imgSrcHover="${enabledImg}" tamperFlagMonitorId="${monitorId}" title="${disableText} (${monitorName})"/>
					</c:when>
					<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
						<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${disabledImg}" imgSrcHover="${disabledImg}" tamperFlagMonitorId="${monitorId}" title="${enableText} (${monitorName})" checked="false"/>
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
	<tags:slowInput myFormId="createNewTamperFlagMonitorForm_${widgetParameters.widgetId}" labelBusy="${createNewText}" label="${createNewText}"/>
</div>



