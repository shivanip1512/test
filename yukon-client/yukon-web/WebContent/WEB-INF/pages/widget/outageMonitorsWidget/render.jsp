<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<c:url var="pencil" value="/WebConfig/yukon/Icons/pencil.gif"/>
<c:url var="pencilOver" value="/WebConfig/yukon/Icons/pencil_over.gif"/>
<c:url var="cog" value="/WebConfig/yukon/Icons/cog.gif"/>
<c:url var="cogOver" value="/WebConfig/yukon/Icons/cog_over.gif"/>
<c:url var="script" value="/WebConfig/yukon/Icons/script.gif"/>
<c:url var="scriptOver" value="/WebConfig/yukon/Icons/script_over.gif"/>
<c:url var="lightning" value="/WebConfig/yukon/Icons/lightning_go.gif"/>
<c:url var="lightningOver" value="/WebConfig/yukon/Icons/lightning_go_over.gif"/>
<c:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<c:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>
<c:url var="placeholder" value="/WebConfig/yukon/Icons/placeholder.gif"/>
<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:msg var="noMonitorsSetupText" key="yukon.web.modules.amr.outageMonitorsWidget.noMonitorsSetup"/>
<cti:msg var="nameText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.name"/>
<cti:msg var="violationsText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.violations"/>
<cti:msg var="monitoringText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.monitoring"/>
<cti:msg var="statusText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.status"/>
<cti:msg var="deleteText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.delete"/>
<cti:msg var="createNewText" key="yukon.web.modules.amr.outageMonitorsWidget.createNew"/>
<cti:msg var="deleteConfirmText" key="yukon.web.modules.amr.outageMonitorsWidget.deleteConfirm"/>
<cti:msg var="editActionTitleText" key="yukon.web.modules.amr.outageMonitorsWidget.actionTitle.edit"/>
<cti:msg var="outageProcessingActionTitleText" key="yukon.web.modules.amr.outageMonitorsWidget.actionTitle.outageProcessing"/>
<cti:msg var="deleteActionTitleText" key="yukon.web.modules.amr.outageMonitorsWidget.actionTitle.delete"/>

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
		<th style="width:70px;">&nbsp;</th>
		<th>${nameText}</th>
		<th style="text-align:right;">${violationsText}</th>
		<th style="text-align:right;">${monitoringText}</th>
		<th style="text-align:center;width:80px;">${statusText}</th>
		<th style="text-align:right;width:20px;"></th>
		
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
			
				<%-- edit monitor --%>
				<cti:url var="viewOutageMonitorEditorUrl" value="/spring/amr/outageProcessing/monitorEditor/edit">
					<cti:param name="outageMonitorId" value="${monitorId}"/>
				</cti:url>
				
				<a href="${viewOutageMonitorEditorUrl}" title="${editActionTitleText} (${monitorName})" style="text-decoration:none;">
					<img src="${pencil}" onmouseover="javascript:this.src='${pencilOver}'" onmouseout="javascript:this.src='${pencil}'">
				</a>
				&nbsp;&nbsp;
				
				<%-- monitor widget --%>
				<cti:url var="viewOutageProcessingUrl" value="/spring/amr/outageProcessing/process/process">
					<cti:param name="outageMonitorId" value="${monitorId}"/>
				</cti:url>
				
				<a href="${viewOutageProcessingUrl}" title="${outageProcessingActionTitleText} (${monitorName})" style="text-decoration:none;">
					<img src="${cog}" onmouseover="javascript:this.src='${cogOver}'" onmouseout="javascript:this.src='${cog}'">
				</a>
				
			</td>
			
			<%-- monitor name --%>
			<td class="${tdClass}">${monitorName}</td>
			
			<%-- violations count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/VIOLATIONS_COUNT"/>
			</td>
			
			<%-- monitoring count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
			</td>
			
			<%-- status --%>
			<td class="${tdClass}" style="text-align:center;">${monitor.evaluatorStatus.description}</td>
			
			<%-- delete --%>
			<td class="${tdClass}" style="text-align:right;">
				<tags:widgetActionRefreshImage outageMonitorId="${monitorId}" confirmText="${deleteConfirmText}" imgSrc="${delete}" imgSrcHover="${deleteOver}"  title="${deleteActionTitleText} (${monitorName})" method="delete"/>
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



