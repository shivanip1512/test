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

<cti:msg var="noMonitorsSetupText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.noMonitorsSetup"/>
<cti:msg var="nameText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.tableHeader.name"/>
<cti:msg var="violationsText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.tableHeader.violations"/>
<cti:msg var="monitoringText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.tableHeader.monitoring"/>
<cti:msg var="statusText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.tableHeader.status"/>
<cti:msg var="deleteText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.tableHeader.delete"/>
<cti:msg var="createNewText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.createNew"/>
<cti:msg var="deleteConfirmText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.deleteConfirm"/>
<cti:msg var="editActionTitleText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.actionTitle.edit"/>
<cti:msg var="tamperFlagProcessingActionTitleText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.actionTitle.tamperFlagProcessing"/>
<cti:msg var="deleteActionTitleText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.actionTitle.delete"/>

<script type="text/javascript">

	function tamperFlagMonitorsWidget_deleteTamperFlagMonitor(id) {

		var deleteOk = confirm('${deleteConfirmText}');

		if (deleteOk) {
			$('tamperFlagMonitorsWidget_deleteTamperFlagMonitorId').value = id;
			${widgetParameters.jsWidget}.doDirectActionRefresh('delete');
		}
	}

</script>

<%-- FORMS --%>
<form id="createNewTamperFlagMonitorForm" action="/spring/amr/tamperFlagProcessing/edit" method="get">
</form>

<input type="hidden" id="tamperFlagMonitorsWidget_deleteTamperFlagMonitorId" name="tamperFlagMonitorsWidget_deleteTamperFlagMonitorId" value="">

<%-- ERROR --%>
<c:if test="${not empty tamperFlagMonitorsWidgetError}">
  	<div class="errorRed">${tamperFlagMonitorsWidgetError}</div>
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
	
		<c:set var="monitorId" value="${monitor.tamperFlagMonitorId}"/>
		<c:set var="monitorName" value="${monitor.tamperFlagMonitorName}"/>

		<c:set var="tdClass" value=""/>
		<c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
			<c:set var="tdClass" value="subtleGray"/>
		</c:if>
		
		<tr>
			
			<%-- action icons --%>
			<td>
			
				<%-- edit monitor --%>
				<cti:url var="viewTamperFlagMonitorEditorUrl" value="/spring/amr/tamperFlagProcessing/edit">
					<cti:param name="tamperFlagMonitorId" value="${monitorId}"/>
				</cti:url>
				
				<a href="${viewTamperFlagMonitorEditorUrl}" title="${editActionTitleText} (${monitorName})" style="text-decoration:none;">
					<img src="${pencil}" onmouseover="javascript:this.src='${pencilOver}'" onmouseout="javascript:this.src='${pencil}'">
				</a>
				&nbsp;&nbsp;
				
				<%-- monitor widget --%>
				<cti:url var="viewTamperFlagProcessingUrl" value="/spring/amr/tamperFlagProcessing/process/process">
					<cti:param name="tamperFlagMonitorId" value="${monitorId}"/>
				</cti:url>
				
				<a href="${viewTamperFlagProcessingUrl}" title="${tamperFlagProcessingActionTitleText} (${monitorName})" style="text-decoration:none;">
					<img src="${cog}" onmouseover="javascript:this.src='${cogOver}'" onmouseout="javascript:this.src='${cog}'">
				</a>
				
			</td>
			
			<%-- monitor name --%>
			<td class="${tdClass}">${monitorName}</td>
			
			<%-- violations count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${monitorId}/VIOLATIONS_COUNT"/>
			</td>
			
			<%-- monitoring count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
			</td>
			
			<%-- status --%>
			<td class="${tdClass}" style="text-align:center;">${monitor.evaluatorStatus.description}</td>
			
			<%-- delete --%>
			<td class="${tdClass}" style="text-align:right;">
				<img onclick="tamperFlagMonitorsWidget_deleteTamperFlagMonitor(${monitorId});" 
					title="${deleteActionTitleText} (${monitorName})" 
					src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'">
			</td>
			
		</tr>
	
	</c:forEach>

</table>
<br>
</c:when>

<c:otherwise>
	${noMonitorsSetupText}
</c:otherwise>
</c:choose>

<div style="text-align: right">
	<tags:slowInput myFormId="createNewTamperFlagMonitorForm" labelBusy="${createNewText}" label="${createNewText}"/>
</div>



