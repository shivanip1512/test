<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="amr" page="tamperFlagProcessing">
	
	<c:if test="${not empty param.processError}">
		<div class="errorMessage">${param.processError}</div>
	</c:if>
	
	<%-- MAIN DETAILS --%>
    <tags:sectionContainer2 nameKey="section.mainDetail.sectionHeader">
	
	<tags:nameValueContainer2>

        <tags:nameValue2 nameKey=".section.mainDetail.name">
			${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}
		</tags:nameValue2>
		
        <tags:nameValue2 nameKey=".section.mainDetail.violations">
			<cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${tamperFlagMonitor.tamperFlagMonitorId}/VIOLATIONS_COUNT"/>
		</tags:nameValue2>
		
        <tags:nameValue2 nameKey=".section.mainDetail.monitoring">
			<cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${tamperFlagMonitor.tamperFlagMonitorId}/MONITORING_COUNT"/>
		</tags:nameValue2>
		
		<tags:nameValueGap2 gapHeight="20px"/>
		
        <tags:nameValue2 nameKey=".section.mainDetail.deviceGroup">
			
			<cti:url var="deviceGroupUrl" value="/spring/group/editor/home">
				<cti:param name="groupName">${tamperFlagMonitor.groupName}</cti:param>
			</cti:url>
			
			<a href="${deviceGroupUrl}">${tamperFlagMonitor.groupName}</a>
			<br>
			
		</tags:nameValue2>
		<tags:nameValue2 nameKey=".section.mainDetail.tamperFlagGroup">
			
			<cti:url var="tamperFlagGroupUrl" value="/spring/group/editor/home">
				<cti:param name="groupName">${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}</cti:param>
			</cti:url>
			
			<a href="${tamperFlagGroupUrl}">${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}</a>
			
			<%-- tamper flag group report --%>
			<br><br>
			<cti:url var="tamperFlagGroupReportUrl" value="/spring/amr/reports/groupDevicesReport">
				<cti:param name="groupName" value="${tamperFlagGroupBase}${fn:escapeXml(tamperFlagMonitor.tamperFlagMonitorName)}"/>
			</cti:url>
			<a href="${tamperFlagGroupReportUrl}"><i:inline key=".section.options.tamperFlagGroupReport"/></a>
			
		</tags:nameValue2>
		
	</tags:nameValueContainer2>
	
	<form id="editMonitorForm" action="/spring/amr/tamperFlagProcessing/edit" method="get">
		<input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}">
	</form>
	<cti:msg2 var="mainDetailEditText" key=".section.mainDetail.edit"/>
    <tags:slowInput myFormId="editMonitorForm" label="${mainDetailEditText}"/>
	
	</tags:sectionContainer2>
	<br>
	
	<%-- READ INTERNAL FLAGS --%>
    <tags:sectionContainer2 id="readInternalFlagsSection" nameKey="section.readInternalFlags">
	
		<form id="readInternalFlagsForm" action="/spring/amr/tamperFlagProcessing/process/readFlags">

			<input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}">
	
			<%-- note --%>
			<table cellpadding="2">
	            <tr>
	                <td valign="top" class="smallBoldLabel">
	                	<i:inline key=".section.readInternalFlags.noteLabel"/>
	                </td>
	                <td style="font-size:11px;">
                        <cti:msg2 key=".section.readInternalFlags.noteBody" htmlEscape="false"/>
	                	<br>
	                	<cti:url var="viewPointsUrl" value="/spring/bulk/addPoints/home">
	                		<cti:mapParam value="${tamperFlagGroupDeviceCollection.collectionParameters}"/>
	                	</cti:url>
                        <i:inline key=".section.readInternalFlags.viewPointsDescription"/>
	                	<a href="${viewPointsUrl}"><i:inline key=".section.readInternalFlags.viewPointsLink"/></a>
	                </td>
	            </tr>
	    	</table>
	    	<br>
	    	
	    	<%-- read internal flags button --%>
	    	<span style="white-space:nowrap;">
			<cti:msg2 var="readInternalFlagsButtonText" key=".section.readInternalFlags.button"/>
            <tags:slowInput myFormId="readInternalFlagsForm" labelBusy="${readInternalFlagsButtonText}" label="${readInternalFlagsButtonText}"/>
			
			<%-- read ok --%>
	    	<c:if test="${param.readOk}">
	    		<i:inline key=".section.readInternalFlags.readOk"/>
	    	</c:if>
	    	</span>
			
			<%-- recent reads --%>
			<c:if test="${fn:length(readResults) > 0}">
				
				<br><br>
				<div class="normalBoldLabel"><i:inline key=".section.readFlags.recentReadFlagsResults"/></div>
				<br>	
				
				<table class="miniResultsTable">
					<tr>
						<th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.dateTime"/></th>
						<th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.successCount"/></th>
						<th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.failureCount"/></th>
						<th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.unsupportedCount"/></th>
						<th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.detail"/></th>
						<th><i:inline key=".section.readFlags.recentReadFlagsResults.tableHeader.status"/></th>
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
								<a href="${readLogsDetailUrl}"><i:inline key=".section.readFlags.recentReadFlagsResults.viewDetailLink"/></a>
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
	
	</tags:sectionContainer2>
	<br>
	
	<%-- RESET INTERNAL FLAGS --%>
    <tags:sectionContainer2 id="resetInternalFlagsSection" nameKey="section.resetInternalFlags">
	
		<form id="resetInternalFlagsForm" action="/spring/amr/tamperFlagProcessing/process/resetFlags">

			<input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}">
	
			<%-- note --%>
			<table cellpadding="2">
	            <tr>
	                <td valign="top" class="smallBoldLabel">
	                	<i:inline key=".section.resetInternalFlags.noteLabel"/>
	                </td>
	                <td style="font-size:11px;">
	                	<cti:msg2 key=".section.resetInternalFlags.noteBody" htmlEscape="false"/>
	                </td>
	            </tr>
	    	</table>
	    	<br>
	    	
	    	<%-- reset internal flags button --%>
			<cti:msg2 var="resetInternalFlagsButtonText" key=".section.resetInternalFlags.button"/>
            <tags:slowInput myFormId="resetInternalFlagsForm" labelBusy="${resetInternalFlagsButtonText}" label="${resetInternalFlagsButtonText}"/>
			
			<%-- reset ok --%>
	    	<c:if test="${param.resetOk}">
	    	    <i:inline key=".section.resetInternalFlags.resetOk"/>
	    	</c:if>
	    	
			<%-- recent resets --%>
			<c:if test="${fn:length(resetResults) > 0}">
			
				<br><br>
				<div class="normalBoldLabel"><i:inline key=".section.resetFlags.recentResetFlagsResults"/></div>
				<br>
				
				<table class="miniResultsTable">
					<tr>
						<th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.dateTime"/></th>
						<th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.successCount"/></th>
						<th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.failureCount"/></th>
						<th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.detail"/></th>
						<th><i:inline key=".section.resetFlags.recentResetFlagsResults.tableHeader.status"/></th>
					</tr>
					
					<c:forEach var="result" items="${resetResults}">
						<tr>
						
							<td>
								<cti:formatDate type="BOTH" value="${result.startTime}"/>
							</td>
							<td>
								<cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/SUCCESS_COUNT"/>
							</td>
							<td>
								<cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/FAILURE_COUNT"/>
							</td>
							<td>
								<cti:url var="resetLogsDetailUrl" value="/spring/group/commander/resultDetail">
									<cti:param name="resultKey" value="${result.key}"/>
								</cti:url>
								<a href="${resetLogsDetailUrl}"><i:inline key=".section.resetFlags.recentResetFlagsResults.viewDetailLink"/></a>
							</td>
							<td>
								<cti:classUpdater type="COMMANDER" identifier="${result.key}/STATUS_CLASS">
		                        	<cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/STATUS_TEXT"/>
		                        </cti:classUpdater>
							</td>
						
						</tr>
					</c:forEach>
				</table>
			</c:if>
	    </form>
	
	</tags:sectionContainer2>
	<br>

	<%-- OPTIONS SECTION --%>
    <tags:sectionContainer2 id="optionsSection" nameKey="section.options">
	
		<%-- clear tamper flag group --%>
		<cti:url var="clearTamperFlagGroupUrl" value="/spring/amr/tamperFlagProcessing/process/clearTamperFlagGroup">
			<cti:param name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}"/>
		</cti:url>
		<a href="${clearTamperFlagGroupUrl}"><i:inline key=".section.options.clearTamperFlagGroup"/></a>
		<br>
		
		<%-- other actions --%>
		<cti:url var="otherActionsUrl" value="/spring/bulk/collectionActions" htmlEscape="true">
			<cti:param name="collectionType" value="group"/>
			<cti:param name="group.name" value="${tamperFlagGroupBase}${tamperFlagMonitor.tamperFlagMonitorName}"/>
		</cti:url>
		<a href="${otherActionsUrl}"><i:inline key=".section.options.otherActions"/></a>
	    	
	</tags:sectionContainer2>
	
</cti:standardPage>