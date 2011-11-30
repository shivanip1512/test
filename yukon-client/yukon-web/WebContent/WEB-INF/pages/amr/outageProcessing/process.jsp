<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:standardPage module="amr" page="outageProcessing">
    
    <script type="text/javascript">
    
    </script>

	<c:if test="${not empty param.processError}">
		<div class="errorMessage">${param.processError}</div>
	</c:if>
	
	<%-- MAIN DETAILS --%>
    <cti:msg2 var="mainDetailSectionHeaderText" key=".mainDetail.sectionHeader" />
	<tags:sectionContainer title="${mainDetailSectionHeaderText}">
	
	<tags:nameValueContainer2>

		<tags:nameValue2 nameKey=".mainDetail.name">
			${outageMonitor.outageMonitorName}
		</tags:nameValue2>
		
		<tags:nameValue2 nameKey=".mainDetail.violations">
			<cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${outageMonitor.outageMonitorId}/VIOLATIONS_COUNT"/>
		</tags:nameValue2>
		
		<tags:nameValue2 nameKey=".mainDetail.monitoring">
			<cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${outageMonitor.outageMonitorId}/MONITORING_COUNT"/>
		</tags:nameValue2>
		
		<tags:nameValueGap2 gapHeight="20px"/>
		
		<tags:nameValue2 nameKey=".mainDetail.numberOfOutages">
			${outageMonitor.numberOfOutages}
            <i:inline key=".mainDetail.numberOfOutages.outages" arguments="${outageMonitor.numberOfOutages}"/> 
		</tags:nameValue2>
		
		<tags:nameValue2 nameKey=".mainDetail.timePeriod">
			${outageMonitor.timePeriodDays} 
            <i:inline key=".mainDetail.timePeriod.days" arguments="${outageMonitor.timePeriodDays}"/>
		</tags:nameValue2>
		
		<tags:nameValueGap2 gapHeight="20px"/>
		
		<tags:nameValue2 nameKey=".mainDetail.deviceGroup">
			<cti:url var="deviceGroupUrl" value="/spring/group/editor/home">
				<cti:param name="groupName">${outageMonitor.groupName}</cti:param>
			</cti:url>
			<a href="${deviceGroupUrl}">${outageMonitor.groupName}</a>
			<br>
		</tags:nameValue2>
		
		<tags:nameValue2 nameKey=".mainDetail.outagesGroup">
			<cti:url var="outageGroupUrl" value="/spring/group/editor/home">
				<cti:param name="groupName">${outageGroupBase}${outageMonitor.outageMonitorName}</cti:param>
			</cti:url>
			
			<a href="${outageGroupUrl}">${outageGroupBase}${outageMonitor.outageMonitorName}</a>
			
			<br><br>
			<cti:url var="outagesGroupReportUrl" value="/spring/amr/reports/groupDevicesReport">
				<cti:param name="groupName" value="${outageGroupBase}${outageMonitor.outageMonitorName}"/>
			</cti:url>
			
            <a href="${outagesGroupReportUrl}"><i:inline key=".options.outagesGroupReport"/></a>
			
		</tags:nameValue2>
		
	</tags:nameValueContainer2>
	<br>
	<form id="editMonitorForm" action="/spring/amr/outageProcessing/monitorEditor/edit" method="get">
		<input type="hidden" name="outageMonitorId" value="${outageMonitor.outageMonitorId}">
	</form>
    <cti:msg2 var="mainDetailEditText" key=".mainDetail.edit"/>
    <tags:slowInput myFormId="editMonitorForm" label="${mainDetailEditText}" />
	</tags:sectionContainer>
	<br>

	<%-- READ OUTAGE LOGS --%>
    <cti:msg2 var="readOutageLogsSectionTitleText" key=".readOutageLogs.title" />
    <tags:sectionContainer id="readOutageLogsSection" title="${readOutageLogsSectionTitleText}">
	
		<form id="readOutagesForm" action="/spring/amr/outageProcessing/process/readOutageLogs">

			<input type="hidden" name="outageMonitorId" value="${outageMonitor.outageMonitorId}">
	
			<%-- note --%>
			<table cellpadding="2">
	            <tr>
	                <td valign="top" class="smallBoldLabel">
                        <i:inline key=".readOutageLogs.noteLabel" />
	                </td>
	                <td style="font-size:11px;">
                        <cti:msg2 key=".readOutageLogs.noteBody" htmlEscape="false"/>
	                </td>
	            </tr>
	    	</table>
	    	<br>
		
			<%-- remove after read checkbox --%>
			<span style="white-space:nowrap;">
            <cti:msg2 var="readOutageLogsButtonText" key=".readOutageLogs.button" />
            <tags:slowInput myFormId="readOutagesForm" labelBusy="${readOutageLogsButtonText}" label="${readOutageLogsButtonText}" />
			<input type="checkbox" name="removeFromOutageGroupAfterRead" checked>
			<i:inline key=".removeAfterRead" />
			</span>
			
			<%-- read ok --%>
	    	<c:if test="${param.readOk}">
	    		<br>
                <i:inline key=".readOutageLogs.readOk" />
	    	</c:if>
			
			<%-- recent reads --%>
			<c:if test="${fn:length(readResults) > 0}">
			
				<br><br>
				<div class="normalBoldLabel"><i:inline key=".recentReadLogsResults"/></div>
				<br>
				
				<table class="miniResultsTable">
					<tr>
						<th><i:inline key=".recentReadLogsResults.dateTime"/></th>
						<th><i:inline key=".recentReadLogsResults.successCount"/></th>
						<th><i:inline key=".recentReadLogsResults.failureCount"/></th>
						<th><i:inline key=".recentReadLogsResults.unsupportedCount"/></th>
						<th><i:inline key=".recentReadLogsResults.detail"/></th>
						<th><i:inline key=".recentReadLogsResults.status"/></th>
					</tr>
					
					<c:forEach var="result" items="${readResults}">
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
								<a href="${readLogsDetailUrl}"><i:inline key=".recentReadLogsResults.viewDetailLink"/></a>
							</td>
							<td>
								<cti:classUpdater type="GROUP_METER_READ" identifier="${result.key}/STATUS_CLASS">
		                        	<cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/STATUS_TEXT"/>
		                        </cti:classUpdater>
							</td>
						
						</tr>
					</c:forEach>
				</table>
			</c:if>
		</form>
		
	</tags:sectionContainer>
	<br>
	<br>
	
	<%-- OPTIONS SECTION --%>
	<cti:msg2 var="optionsSectionTitleText" key=".options.title" />
    <tags:sectionContainer id="optionsSection" title="${optionsSectionTitleText}">
	
		<%-- outages log report --%>
		<cti:formatDate var="startDate" type="DATE" value="${reportStartDate}"/>
		<cti:link href="/analysis/Reports.jsp" key=".reportingLink">
			<cti:param name="groupType" value="METERING"/>
			<cti:param name="type" value="METER_OUTAGE_LOG"/>
			<cti:param name="selectedReportFilter" value="GROUPS"/>
			<cti:param name="selectedReportFilterValues" value="${outageMonitor.groupName}"/>
			<cti:param name="startDate" value="${startDate}"/>
		</cti:link>
		<br>
		
		<%-- clear outages group --%>
		<cti:url var="clearOutagesGroupUrl" value="/spring/amr/outageProcessing/process/clearOutagesGroup">
			<cti:param name="outageMonitorId" value="${outageMonitor.outageMonitorId}"/>
		</cti:url>
		<a href="${clearOutagesGroupUrl}"><i:inline key=".options.clearOutagesGroup"/></a>
		<br>
		
		<%-- other actions --%>
		<cti:url var="otherActionsUrl" value="/spring/bulk/collectionActions">
			<cti:param name="collectionType" value="group"/>
			<cti:param name="group.name" value="${outageGroupBase}${outageMonitor.outageMonitorName}"/>
		</cti:url>
		<a href="${otherActionsUrl}"><i:inline key=".options.otherActions" /></a>
	    	
	</tags:sectionContainer>

</cti:standardPage>