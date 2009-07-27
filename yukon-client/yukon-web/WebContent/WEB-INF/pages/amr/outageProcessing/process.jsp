<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.outageProcessing.pageTitle" />
<cti:msg var="reportingLinkText" key="yukon.web.modules.amr.outageProcessing.reportingLink" />
<cti:msg var="mainDetailNameText" key="yukon.web.modules.amr.outageProcessing.section.mainDetail.name" />
<cti:msg var="mainDetailDeviceGroupText" key="yukon.web.modules.amr.outageProcessing.section.mainDetail.deviceGroup" />
<cti:msg var="mainDetailOutagesGroupText" key="yukon.web.modules.amr.outageProcessing.section.mainDetail.outagesGroup" />
<cti:msg var="mainDetailDevicesText" key="yukon.web.modules.amr.outageProcessing.section.mainDetail.devices" />
<cti:msg var="mainDetailNumberOfOutagesText" key="yukon.web.modules.amr.outageProcessing.section.mainDetail.numberOfOutages" />
<cti:msg var="mainDetailTimePeriodText" key="yukon.web.modules.amr.outageProcessing.section.mainDetail.timePeriod" />
<cti:msg var="readOutageLogsSectionTitleText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.title" />
<cti:msg var="readOutageLogsButtonText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.button" />
<cti:msg var="readOutageLogsSectionNoteLabelText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.noteLabel" />
<cti:msg var="readOutageLogsSectionNoteBodyText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.noteBody" />
<cti:msg var="readOutageLogsSectionRemoveAfterReadText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.removeAfterRead" />
<cti:msg var="readOutageLogsSectionRecentReadLogsResultsText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.recentReadLogsResults" />
<cti:msg var="readOutageLogsSectiondateTimeText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.recentReadLogsResults.tableHeader.dateTime" />
<cti:msg var="readOutageLogsSectionSuccessCountText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.recentReadLogsResults.tableHeader.successCount" />
<cti:msg var="readOutageLogsSectionFailureCountText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.recentReadLogsResults.tableHeader.failureCount" />
<cti:msg var="readOutageLogsSectionUnsupportedCountText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.recentReadLogsResults.tableHeader.unsupportedCount" />
<cti:msg var="readOutageLogsSectionDetailText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.recentReadLogsResults.tableHeader.detail" />
<cti:msg var="readOutageLogsSectionStatusText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.recentReadLogsResults.tableHeader.status" />
<cti:msg var="readOutageLogsSectionVieDetailLinkText" key="yukon.web.modules.amr.outageProcessing.section.readOutageLogs.recentReadLogsResults.viewDetailLink" />
<cti:msg var="optionsSectionTitleText" key="yukon.web.modules.amr.outageProcessing.section.options.title" />
<cti:msg var="outagesGroupReportText" key="yukon.web.modules.amr.outageProcessing.section.options.outagesGroupReport" />
<cti:msg var="clearOutagesGroupText" key="yukon.web.modules.amr.outageProcessing.section.options.clearOutagesGroup" />
<cti:msg var="otherActionsText" key="yukon.web.modules.amr.outageProcessing.section.options.otherActions" />
    
<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- metering home --%>
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        
        <%-- outage processing --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">

		
	</script>

    <h2>${pageTitle}</h2>
    <br>
	
	<c:if test="${not empty processError}">
		<div class="errorRed">${processError}</div>
	</c:if>
	
	<%-- MAIN DETAILS --%>
	<tags:nameValueContainer>

		<tags:nameValue name="${mainDetailNameText}" nameColumnWidth="250px">
			<cti:url var="outageMonitorEditUrl" value="/spring/amr/outageProcessing/monitorEditor/edit">
				<cti:param name="outageMonitorId" value="${outageMonitor.outageMonitorId}"/>
			</cti:url>
			<a href="${outageMonitorEditUrl}">${outageMonitor.name}</a>
		</tags:nameValue>
		
		<tags:nameValue name="${mainDetailDevicesText}">
			<cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${outageMonitor.outageMonitorId}/OUTAGE_COUNT"/>
		</tags:nameValue>
		
		<tags:nameValue name="${mainDetailNumberOfOutagesText}">
			${outageMonitor.numberOfOutages}
		</tags:nameValue>
		
		<tags:nameValue name="${mainDetailTimePeriodText}">
			${outageMonitor.timePeriod}
		</tags:nameValue>
		
		<tags:nameValue name="">
			&nbsp;
		</tags:nameValue>
		
		<tags:nameValue name="${mainDetailDeviceGroupText}">
			
			<cti:url var="deviceGroupUrl" value="/spring/group/editor/home">
				<cti:param name="groupName">${outageMonitor.groupName}</cti:param>
			</cti:url>
			
			<a href="${deviceGroupUrl}">${outageMonitor.groupName}</a>
			<br>
			
		</tags:nameValue>
		
		<tags:nameValue name="${mainDetailOutagesGroupText}">
			
			<cti:url var="outageGroupUrl" value="/spring/group/editor/home">
				<cti:param name="groupName">${outageGroupBase}${outageMonitor.name}</cti:param>
			</cti:url>
			
			<a href="${outageGroupUrl}">${outageGroupBase}${outageMonitor.name}</a>
			<br>
			<cti:formatDate var="startDate" type="DATE" value="${reportStartDate}"/>
			<cti:link href="/analysis/Reports.jsp" key="yukon.web.modules.amr.outageProcessing.reportingLink">
				<cti:param name="groupType" value="METERING"/>
				<cti:param name="type" value="METER_OUTAGE_LOG"/>
				<cti:param name="selectedReportFilter" value="GROUPS"/>
				<cti:param name="selectedReportFilterValues" value="${outageGroupBase}${outageMonitor.name}"/>
				<cti:param name="startDate" value="${startDate}"/>
			</cti:link>
			
		</tags:nameValue>
		
	</tags:nameValueContainer>

	<br>
	<br>
	
	<%-- READ OUTAGE LOGS --%>
	<tags:sectionContainer id="readOutageLogsSection" title="${readOutageLogsSectionTitleText}">
	
		<form id="readOutagesForm" action="/spring/amr/outageProcessing/process/readOutageLogs">

			<input type="hidden" name="outageMonitorId" value="${outageMonitor.outageMonitorId}">
	
			<%-- note --%>
			<table cellpadding="2">
	            <tr>
	                <td valign="top" class="smallBoldLabel">
	                	${readOutageLogsSectionNoteLabelText}
	                </td>
	                <td style="font-size:11px;">
	                	${readOutageLogsSectionNoteBodyText}
	                </td>
	            </tr>
	    	</table>
	    	<br>
		
			<%-- remove after read checkbox --%>
			<span style="white-space:nowrap;">
			<tags:slowInput myFormId="readOutagesForm" labelBusy="${readOutageLogsButtonText}" label="${readOutageLogsButtonText}"/>
			<input type="checkbox" name="removeFromOutageGroupAfterRead" checked>
			${readOutageLogsSectionRemoveAfterReadText}
			</span>
			<br>
			<br>
			
			<%-- recent reads --%>
			<div class="normalBoldLabel">${readOutageLogsSectionRecentReadLogsResultsText}</div>
			<br>
			<table class="miniResultsTable">
				<tr>
					<th>${readOutageLogsSectiondateTimeText}</th>
					<th>${readOutageLogsSectionSuccessCountText}</th>
					<th>${readOutageLogsSectionFailureCountText}</th>
					<th>${readOutageLogsSectionUnsupportedCountText}</th>
					<th>${readOutageLogsSectionDetailText}</th>
					<th>${readOutageLogsSectionStatusText}</th>
				</tr>
				
				<c:forEach var="result" items="${allReads}">
					<tr>
					
						<td>
							<cti:formatDate type="BOTH" value="${result.startTime}"/>
						</td>
						<td>
							<cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/SUCCESS_COUNT"/>
						</td>
						<td>
							<cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/FAILURE_COUNT"/>
						</td>
						<td>
							<cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/UNSUPPORTED_COUNT"/>
						</td>
						<td>
							<cti:url var="readLogsDetailUrl" value="/spring/group/groupMeterRead/resultDetail">
								<cti:param name="resultKey" value="${result.key}"/>
							</cti:url>
							<a href="${readLogsDetailUrl}">${readOutageLogsSectionVieDetailLinkText}</a>
						</td>
						<td>
							<cti:classUpdater type="GROUP_METER_READ" identifier="${result.key}/STATUS_CLASS">
	                        	<cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/STATUS_TEXT"/>
	                        </cti:classUpdater>
						</td>
					
					</tr>
				</c:forEach>
				
			</table>
		
		</form>
	
	</tags:sectionContainer>
	<br>
	<br>
	
	
	<%-- OPTIONS SECTION --%>
	<tags:sectionContainer id="optionsSection" title="${optionsSectionTitleText}">
	
		<%-- outages group report --%>
		<cti:url var="outagesGroupReportUrl" value="/spring/amr/reports/groupDevicesReport">
			<cti:param name="groupName" value="${outageGroupBase}${outageMonitor.name}"/>
			<cti:param name="def" value="groupDevicesDefinition"/>
		</cti:url>
		<a href="${outagesGroupReportUrl}">${outagesGroupReportText}</a>
		<br>
		
		<%-- clear outages group --%>
		<cti:url var="clearOutagesGroupUrl" value="/spring/amr/outageProcessing/process/clearOutagesGroup">
			<cti:param name="outageMonitorId" value="${outageMonitor.outageMonitorId}"/>
		</cti:url>
		<a href="${clearOutagesGroupUrl}">${clearOutagesGroupText}</a>
		<br>
		
		<%-- other actions --%>
		<cti:url var="otherActionsUrl" value="/spring/bulk/collectionActions">
			<cti:param name="collectionType" value="group"/>
			<cti:param name="group.name" value="${outageGroupBase}${outageMonitor.name}"/>
		</cti:url>
		<a href="${otherActionsUrl}">${otherActionsText}</a>
	    	
	</tags:sectionContainer>
    

</cti:standardPage>