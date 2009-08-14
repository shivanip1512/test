<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.tamperFlagProcessing.pageTitle" />
<cti:msg var="mainDetailNameText" key="yukon.web.modules.amr.tamperFlagProcessing.section.mainDetail.name" />
<cti:msg var="mainDetailDeviceGroupText" key="yukon.web.modules.amr.tamperFlagProcessing.section.mainDetail.deviceGroup" />
<cti:msg var="mainDetailTamperFlagGroupText" key="yukon.web.modules.amr.tamperFlagProcessing.section.mainDetail.tamperFlagGroup" />
<cti:msg var="mainDetailViolationsText" key="yukon.web.modules.amr.tamperFlagProcessing.section.mainDetail.violations" />
<cti:msg var="mainDetailMonitoringText" key="yukon.web.modules.amr.tamperFlagProcessing.section.mainDetail.monitoring" />
<cti:msg var="readInternalFlagsSectionTitleText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readInternalFlags.title" />
<cti:msg var="readInternalFlagsButtonText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readInternalFlags.button" />
<cti:msg var="readOkText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readInternalFlags.readOk" />
<cti:msg var="readInternalFlagsSectionNoteLabelText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readInternalFlags.noteLabel" />
<cti:msg var="readInternalFlagsSectionNoteBodyText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readInternalFlags.noteBody" />
<cti:msg var="viewPointsDescriptionText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readInternalFlags.viewPointsDescription" />
<cti:msg var="viewPointsLinkText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readInternalFlags.viewPointsLink" />
<cti:msg var="resetInternalFlagsSectionTitleText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetInternalFlags.title" />
<cti:msg var="resetInternalFlagsButtonText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetInternalFlags.button" />
<cti:msg var="resetOkText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetInternalFlags.resetOk" />
<cti:msg var="resetInternalFlagsSectionNoteLabelText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetInternalFlags.noteLabel" />
<cti:msg var="resetInternalFlagsSectionNoteBodyText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetInternalFlags.noteBody" />
<cti:msg var="readFlagsSectionRecentReadLogsResultsText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readFlags.recentReadFlagsResults" />
<cti:msg var="readFlagsSectiondateTimeText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readFlags.recentReadFlagsResults.tableHeader.dateTime" />
<cti:msg var="readFlagsSectionSuccessCountText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readFlags.recentReadFlagsResults.tableHeader.successCount" />
<cti:msg var="readFlagsSectionFailureCountText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readFlags.recentReadFlagsResults.tableHeader.failureCount" />
<cti:msg var="readFlagsSectionUnsupportedCountText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readFlags.recentReadFlagsResults.tableHeader.unsupportedCount" />
<cti:msg var="readFlagsSectionDetailText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readFlags.recentReadFlagsResults.tableHeader.detail" />
<cti:msg var="readFlagsSectionStatusText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readFlags.recentReadFlagsResults.tableHeader.status" />
<cti:msg var="readFlagsSectionVieDetailLinkText" key="yukon.web.modules.amr.tamperFlagProcessing.section.readFlags.recentReadFlagsResults.viewDetailLink" />
<cti:msg var="resetFlagsSectionRecentResetLogsResultsText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetFlags.recentResetFlagsResults" />
<cti:msg var="resetFlagsSectiondateTimeText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetFlags.recentResetFlagsResults.tableHeader.dateTime" />
<cti:msg var="resetFlagsSectionSuccessCountText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetFlags.recentResetFlagsResults.tableHeader.successCount" />
<cti:msg var="resetFlagsSectionFailureCountText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetFlags.recentResetFlagsResults.tableHeader.failureCount" />
<cti:msg var="resetFlagsSectionDetailText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetFlags.recentResetFlagsResults.tableHeader.detail" />
<cti:msg var="resetFlagsSectionStatusText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetFlags.recentResetFlagsResults.tableHeader.status" />
<cti:msg var="resetFlagsSectionVieDetailLinkText" key="yukon.web.modules.amr.tamperFlagProcessing.section.resetFlags.recentResetFlagsResults.viewDetailLink" />
<cti:msg var="optionsSectionTitleText" key="yukon.web.modules.amr.tamperFlagProcessing.section.options.title" />
<cti:msg var="tamperFlagGroupReportText" key="yukon.web.modules.amr.tamperFlagProcessing.section.options.tamperFlagGroupReport" />
<cti:msg var="clearTamperFlagGroupText" key="yukon.web.modules.amr.tamperFlagProcessing.section.options.clearTamperFlagGroup" />
<cti:msg var="otherActionsText" key="yukon.web.modules.amr.tamperFlagProcessing.section.options.otherActions" />


<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
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
			<cti:url var="tamperFlagMonitorEditUrl" value="/spring/amr/tamperFlagProcessing/edit">
				<cti:param name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}"/>
			</cti:url>
			<a href="${tamperFlagMonitorEditUrl}">${tamperFlagMonitor.tamperFlagMonitorName}</a>
		</tags:nameValue>
		
		<tags:nameValue name="${mainDetailViolationsText}">
			<cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${tamperFlagMonitor.tamperFlagMonitorId}/VIOLATIONS_COUNT"/>
		</tags:nameValue>
		
		<tags:nameValue name="${mainDetailMonitoringText}">
			<cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${tamperFlagMonitor.tamperFlagMonitorId}/MONITORING_COUNT"/>
		</tags:nameValue>
		
		<tags:nameValue name="">
			&nbsp;
		</tags:nameValue>
		
		<tags:nameValue name="${mainDetailDeviceGroupText}">
			
			<cti:url var="deviceGroupUrl" value="/spring/group/editor/home">
				<cti:param name="groupName">${tamperFlagMonitor.groupName}</cti:param>
			</cti:url>
			
			<a href="${deviceGroupUrl}">${tamperFlagMonitor.groupName}</a>
			<br>
			
		</tags:nameValue>
		
		<tags:nameValue name="${mainDetailTamperFlagGroupText}">
			
			<cti:url var="tamperFlagGroupUrl" value="/spring/group/editor/home">
				<cti:param name="groupName">${tamperFlagGroupBase}${tamperFlagMonitor.tamperFlagMonitorName}</cti:param>
			</cti:url>
			
			<a href="${tamperFlagGroupUrl}">${tamperFlagGroupBase}${tamperFlagMonitor.tamperFlagMonitorName}</a>
			
		</tags:nameValue>
		
	</tags:nameValueContainer>
	<br>
	<br>
	
	<%-- READ INTERNAL FLAGS --%>
	<tags:sectionContainer id="readInternalFlagsSection" title="${readInternalFlagsSectionTitleText}">
	
		<form id="readInternalFlagsForm" action="/spring/amr/tamperFlagProcessing/process/readFlags">

			<input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}">
	
			<%-- note --%>
			<table cellpadding="2">
	            <tr>
	                <td valign="top" class="smallBoldLabel">
	                	${readInternalFlagsSectionNoteLabelText}
	                </td>
	                <td style="font-size:11px;">
	                	${readInternalFlagsSectionNoteBodyText}
	                	<br>
	                	<cti:url var="viewPointsUrl" value="/spring/bulk/addPoints/home">
	                		<cti:mapParam value="${tamperFlagGroupDeviceCollection.collectionParameters}"/>
	                	</cti:url>
	                	${viewPointsDescriptionText} <a href="${viewPointsUrl}">${viewPointsLinkText}</a>
	                </td>
	            </tr>
	    	</table>
	    	<br>
	    	
	    	<%-- read internal flags button --%>
	    	<span style="white-space:nowrap;">
			<tags:slowInput myFormId="readInternalFlagsForm" labelBusy="${readInternalFlagsButtonText}" label="${readInternalFlagsButtonText}"/>
			
			<%-- read ok --%>
	    	<c:if test="${readOk}">
	    		${readOkText}
	    	</c:if>
	    	</span>
			
			<%-- recent reads --%>
			<c:if test="${fn:length(readResults) > 0}">
				
				<br><br>
				<div class="normalBoldLabel">${readFlagsSectionRecentReadLogsResultsText}</div>
				<br>	
				
				<table class="miniResultsTable">
					<tr>
						<th>${readFlagsSectiondateTimeText}</th>
						<th>${readFlagsSectionSuccessCountText}</th>
						<th>${readFlagsSectionFailureCountText}</th>
						<th>${readFlagsSectionUnsupportedCountText}</th>
						<th>${readFlagsSectionDetailText}</th>
						<th>${readFlagsSectionStatusText}</th>
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
								<a href="${readLogsDetailUrl}">${readFlagsSectionVieDetailLinkText}</a>
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
	
	<%-- RESET INTERNAL FLAGS --%>
	<tags:sectionContainer id="resetInternalFlagsSection" title="${resetInternalFlagsSectionTitleText}">
	
		<form id="resetInternalFlagsForm" action="/spring/amr/tamperFlagProcessing/process/resetFlags">

			<input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}">
	
			<%-- note --%>
			<table cellpadding="2">
	            <tr>
	                <td valign="top" class="smallBoldLabel">
	                	${resetInternalFlagsSectionNoteLabelText}
	                </td>
	                <td style="font-size:11px;">
	                	${resetInternalFlagsSectionNoteBodyText}
	                </td>
	            </tr>
	    	</table>
	    	<br>
	    	
	    	<%-- reset internal flags button --%>
			<tags:slowInput myFormId="resetInternalFlagsForm" labelBusy="${resetInternalFlagsButtonText}" label="${resetInternalFlagsButtonText}"/>
			
			<%-- reset ok --%>
	    	<c:if test="${resetOk == true}">
	    		${resetOkText}
	    	</c:if>
	    	
			<%-- recent resets --%>
			<c:if test="${fn:length(resetResults) > 0}">
			
				<br><br>
				<div class="normalBoldLabel">${resetFlagsSectionRecentResetLogsResultsText}</div>
				<br>
				
				<table class="miniResultsTable">
					<tr>
						<th>${resetFlagsSectiondateTimeText}</th>
						<th>${resetFlagsSectionSuccessCountText}</th>
						<th>${resetFlagsSectionFailureCountText}</th>
						<th>${resetFlagsSectionDetailText}</th>
						<th>${resetFlagsSectionStatusText}</th>
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
								<a href="${resetLogsDetailUrl}">${resetFlagsSectionVieDetailLinkText}</a>
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
	
	</tags:sectionContainer>
	<br>
	<br>

	<%-- OPTIONS SECTION --%>
	<tags:sectionContainer id="optionsSection" title="${optionsSectionTitleText}">
	
		<%-- tamper flag group report --%>
		<cti:url var="tamperFlagGroupReportUrl" value="/spring/amr/reports/groupDevicesReport">
			<cti:param name="groupName" value="${tamperFlagGroupBase}${tamperFlagMonitor.tamperFlagMonitorName}"/>
			<cti:param name="def" value="groupDevicesDefinition"/>
		</cti:url>
		<a href="${tamperFlagGroupReportUrl}">${tamperFlagGroupReportText}</a>
		<br>
		
		<%-- clear tamper flag group --%>
		<cti:url var="clearTamperFlagGroupUrl" value="/spring/amr/tamperFlagProcessing/process/clearTamperFlagGroup">
			<cti:param name="tamperFlagMonitorId" value="${tamperFlagMonitor.tamperFlagMonitorId}"/>
		</cti:url>
		<a href="${clearTamperFlagGroupUrl}">${clearTamperFlagGroupText}</a>
		<br>
		
		<%-- other actions --%>
		<cti:url var="otherActionsUrl" value="/spring/bulk/collectionActions">
			<cti:param name="collectionType" value="group"/>
			<cti:param name="group.name" value="${tamperFlagGroupBase}${tamperFlagMonitor.tamperFlagMonitorName}"/>
		</cti:url>
		<a href="${otherActionsUrl}">${otherActionsText}</a>
	    	
	</tags:sectionContainer>
	
</cti:standardPage>