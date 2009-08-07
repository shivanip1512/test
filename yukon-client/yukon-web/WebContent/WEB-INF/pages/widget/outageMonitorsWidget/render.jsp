<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

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

<cti:msg var="nameText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.name"/>
<cti:msg var="scheduleDescriptionText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.scheduleDescription"/>
<cti:msg var="devicesText" key="yukon.web.modules.amr.outageMonitorsWidget.tableHeader.devices"/>
<cti:msg var="createNewText" key="yukon.web.modules.amr.outageMonitorsWidget.createNew"/>
<cti:msg var="deleteConfirmText" key="yukon.web.modules.amr.outageMonitorsWidget.deleteConfirm"/>
<cti:msg var="outageProcessingActionTitleText" key="yukon.web.modules.amr.outageMonitorsWidget.actionTitle.outageProcessing"/>
<cti:msg var="scheduledReadActionTitleText" key="yukon.web.modules.amr.outageMonitorsWidget.actionTitle.scheduledRead"/>
<cti:msg var="startReadNowActionTitleText" key="yukon.web.modules.amr.outageMonitorsWidget.actionTitle.startReadNow"/>
<cti:msg var="deleteActionTitleText" key="yukon.web.modules.amr.outageMonitorsWidget.actionTitle.delete"/>


<script type="text/javascript">

	function deleteOutageMonitor(id) {

		var deleteOk = confirm('${deleteConfirmText}');

		if (deleteOk) {
			$('outageMonitorsWidget_deleteOutageMonitorId').value = id;
			${widgetParameters.jsWidget}.doDirectActionRefresh('delete');
		}
	}

</script>

<%-- FORMS --%>
<form id="createForm" action="/spring/amr/outageProcessing/monitorEditor/edit" method="get">
</form>

<input type="hidden" id="outageMonitorsWidget_deleteOutageMonitorId" name="outageMonitorsWidget_deleteOutageMonitorId" value="">

<%-- ERROR --%>
<c:if test="${not empty outageMonitorsWidgetError}">
  	<div class="errorRed">${outageMonitorsWidgetError}</div>
</c:if>
		
<%-- TABLE --%>
<table class="compactResultsTable">
	
	<tr>
		<th>&nbsp;</th>
		<th>${nameText}</th>
		<th>${devicesText}</th>
		
	</tr>

	<c:forEach var="monitor" items="${monitors}">
	
		<c:set var="monitorId" value="${monitor.outageMonitorId}"/>
		<c:set var="monitorName" value="${monitor.name}"/>

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
				&nbsp;&nbsp;
			
				<%-- delete --%>
				<img onclick="deleteOutageMonitor(${monitorId});" 
					title="${deleteActionTitleText} (${monitorName})" 
					src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'">
			</td>
			
			<%-- monitor name / edit link --%>
			<td>
				<a href="/spring/amr/outageProcessing/monitorEditor/edit?outageMonitorId=${monitorId}">
					${monitorName}
				</a>
			</td>
			
			<%-- outage count --%>
			<td>
				<cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/OUTAGE_COUNT"/>
			</td>
			
		</tr>
	
	</c:forEach>

</table>

<br>
<div style="text-align: right">
	<tags:slowInput myFormId="createForm" labelBusy="${createNewText}" label="${createNewText}"/>
</div>



