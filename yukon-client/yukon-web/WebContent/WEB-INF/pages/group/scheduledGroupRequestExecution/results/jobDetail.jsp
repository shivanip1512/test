<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/amr" prefix="amr" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.pageTitle" />
<cti:msg var="infoSectionText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.section" />
<cti:msg var="scheduleNameText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.scheduleName" />
<cti:msg var="jobTypeText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.jobType" />
<cti:msg var="creTypeText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.creType" />
<cti:msg var="enabledText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.enabled" />
<cti:msg var="enabledYesText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.enabledYes" />
<cti:msg var="enabledNoText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.enabledNo" />
<cti:msg var="attributeText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.attribute" />
<cti:msg var="commandText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.command" />
<cti:msg var="scheduleDescriptionText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.scheduleDescription" />
<cti:msg var="lastRunText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.lastRun" />
<cti:msg var="lastResultsText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.lastResults" />
<cti:msg var="lastResultsSuccessText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.lastResults.success" />
<cti:msg var="lastResultsMissedText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.lastResults.missed" />
<cti:msg var="lastResultsTotalText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.lastResults.total" />
<cti:msg var="deviceGroupText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.deviceGroup" />
<cti:msg var="nextRunText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.nextRun" />
<cti:msg var="nextRunDisabledText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.nextRunDisabled" />
<cti:msg var="userText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.user" />
<cti:msg var="executionsText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.executions" />
<cti:msg var="executionsButtonText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.executionsButton" />
<cti:msg var="deviceGroupPopupInfoText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.popInfo.deviceGroup" />
<cti:msg var="executionsPopupInfoText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.popInfo.executions" />
<cti:msg var="editScheduleButtonText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.editScheduleButton" />
<cti:msg var="retryCountText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.retryCount"/>
<cti:msg var="stopRetryAfterHoursCountText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.stopRetryAfterHoursCount"/>
<cti:msg var="turnOffQueuingAfterRetryCountText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.turnOffQueuingAfterRetryCount"/>
    
<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="commandRequestExecution|scheduledGroupCommands"/>
    
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- metering home --%>
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        
        <%-- jobs --%>
        <cti:msg var="jobsPageTitle" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.pageTitle" />
        <cti:crumbLink url="/spring/group/scheduledGroupRequestExecutionResults/jobs" title="${jobsPageTitle}" />
        
        <%-- job detail --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
	<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>

    <script type="text/javascript">

    </script>
    
    <h2 title="ID: ${jobWrapper.job.id}">${pageTitle}</h2>
    <br>
    
    <%-- JOB INFO --%>
    <tags:sectionContainer title="${infoSectionText}" id="jobInfoSection">
					
		<tags:nameValueContainer>

			<%-- name --%>
			<tags:nameValue name="${scheduleNameText}" nameColumnWidth="160px">
				${jobWrapper.name}
			</tags:nameValue>
			
			<%-- cre type --%>
			<tags:nameValue name="${creTypeText}">
				${jobWrapper.commandRequestTypeShortName}
			</tags:nameValue>
			
			<%-- attribute/command --%>
			<c:if test="${not empty jobWrapper.attributes}">
				<tags:nameValue name="${attributeText}">${jobWrapper.attributeDescriptions}</tags:nameValue>
			</c:if>
			<c:if test="${not empty jobWrapper.command}">
				<tags:nameValue name="${commandText}">${jobWrapper.command}</tags:nameValue>
			</c:if>
			
			<%-- device group --%>
			<tags:nameValue name="${deviceGroupText}">
				<c:choose>
					<c:when test="${not empty jobWrapper.deviceGroupName}">
						<cti:url var="deviceGroupUrl" value="/spring/group/editor/home">
							<cti:param name="groupName" value="${jobWrapper.deviceGroupName}"/>
						</cti:url>
						<a href="${deviceGroupUrl}">
							${jobWrapper.deviceGroupName}
						</a>
					</c:when>
					<c:otherwise>
						<span class="errorRed">Group does not exist.</span>
					</c:otherwise>
				</c:choose>
			</tags:nameValue>
			
			<%-- user --%>
			<tags:nameValue name="${userText}">
				${jobWrapper.job.userContext.yukonUser.username}
			</tags:nameValue>
			
			<%-- retry setup --%>
			<c:if test="${jobWrapper.retrySetup}">
				<tags:nameValue name="Retry Options">
					<tags:hideReveal title="View" showInitially="false">
					
						<table class="compactResultsTable">
							<tr>
								<td style="width:15px;">${jobWrapper.retryCount}</td>
								<td>${retryCountText}</td>
							</tr>
							<c:if test="${not empty jobWrapper.stopRetryAfterHoursCount}">
							<tr>
								<td>${jobWrapper.stopRetryAfterHoursCount}</td>
								<td>${stopRetryAfterHoursCountText}</td>
							</tr>
							</c:if>
							<c:if test="${not empty jobWrapper.turnOffQueuingAfterRetryCount}">
							<tr>
								<td>${jobWrapper.turnOffQueuingAfterRetryCount}</td>
								<td>${turnOffQueuingAfterRetryCountText}</td>
							</tr>
							</c:if>
						</table>
					
					</tags:hideReveal>
				</tags:nameValue>
			</c:if>
			
			
			<tags:nameValueGap gapHeight="20px"/>
			
			<%-- enabled --%>
			<tags:nameValue name="${enabledText}">
				<c:choose>
					<c:when test="${jobWrapper.job.disabled}">
						${enabledNoText}
					</c:when>
					<c:otherwise>
						${enabledYesText}
					</c:otherwise>
				</c:choose>
			</tags:nameValue>
			
			<%-- schedule description --%>
			<tags:nameValue name="${scheduleDescriptionText}">
			
				<cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/SCHEDULE_DESCRIPTION"/>
			
			</tags:nameValue>
			
			<%-- last run --%>
			<tags:nameValue name="${lastRunText}">
			
				<amr:scheduledGroupRequestExecutionJobLastRunDate jobId="${jobWrapper.job.id}" linkedInitially="${not empty lastCre}" />
			
			</tags:nameValue>
			
			<%-- last results --%>
			<tags:nameValue name="${lastResultsText}">
			
				<amr:scheduledGroupRequestExecutionJobLastRunStats jobId="${jobWrapper.job.id}" hasStatsInitially="${not empty lastCre}"/>
				
			</tags:nameValue>
			
			<%-- next run --%>
			<tags:nameValue name="${nextRunText}">
			<c:choose>
				<c:when test="${jobWrapper.job.disabled}">
					${nextRunDisabledText}
				</c:when>
				<c:otherwise>
					<amr:scheduledGroupRequestExecutionJobNextRunDate jobId="${jobWrapper.job.id}"/>
				</c:otherwise>
			</c:choose>
			</tags:nameValue>
			
			<%-- all executions button --%>
			<tags:nameValue name="${executionsText}">
				<form name="viewAllExecutionsForm" action="/spring/common/commandRequestExecutionResults/list" method="get">
				
					<cti:url var="creListUrl" value="/spring/common/commandRequestExecutionResults/list">
						<cti:param name="jobId" value="${jobWrapper.job.id}"/>
					</cti:url>
				
					<a href="${creListUrl}">${executionsButtonText}</a> 
				
					(<cti:dataUpdaterValue type="SCHEDULED_GROUP_REQUEST_EXECUTION" identifier="${jobWrapper.job.id}/CRE_COUNT_FOR_JOB" />)
					
					<img onclick="$('viewAllExecutionsInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
			
					<tags:simplePopup id="viewAllExecutionsInfoPopup" title="View All Executions">
					     ${executionsPopupInfoText}
					</tags:simplePopup>
					
				</form>
				
			</tags:nameValue>
			
		</tags:nameValueContainer>
		
		<%-- EDIT --%>
		<br>
		<form id="editScheduledGroupRequestExecutionForm" action="/spring/group/scheduledGroupRequestExecution/home" method="get">
			<input type="hidden" name="editJobId" value="${jobWrapper.job.id}">
		</form>
		
		<%-- edit button --%>
		<c:if test="${canManage}">
			<tags:slowInput myFormId="editScheduledGroupRequestExecutionForm" labelBusy="${editScheduleButtonText}" label="${editScheduleButtonText}" width="80px"/>
		</c:if>
		
	</tags:sectionContainer>
					
</cti:standardPage>