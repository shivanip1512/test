<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:includeScript link="/JavaScript/scheduledJobs.js"/>

<%-- TABLE --%>
<cti:url var="submitUrl" value="/group/scheduledGroupRequestExecution/home"/>
<form action="${submitUrl}" method="get">
<c:choose>
<c:when test="${fn:length(jobWrappers) > 0}">

<c:if test="${fn:length(jobWrappers) > 20}">
	<c:choose>
		<c:when test="${canManage}">
			<div style="overflow:auto; height:500px;">
		</c:when>
		<c:otherwise>
			<div style="overflow:auto; height:390px;">
		</c:otherwise>
	</c:choose>
</c:if>
<table id="jobsTable" class="compactResultsTable">
	<thead>
		<th style="width:20px;">&nbsp;</th>
		<th><i:inline key=".tableHeader.scheduleName"/></th>
		<th><i:inline key=".tableHeader.scheduleDescription"/></th>
		<th><i:inline key=".tableHeader.status"/></th>
		<c:if test="${canManage}">
			<th style="text-align:right;width:80px;"><i:inline key=".tableHeader.enabled"/></th>
		</c:if>
	</thead>

	<c:forEach var="jobWrapper" items="${jobWrappers}">
	
		<cti:url var="viewScheduleDetailsUrl" value="/group/scheduledGroupRequestExecutionResults/detail" >
			<cti:param name="jobId" value="${jobWrapper.job.id}"/>
		</cti:url>
		
		<tr id="tr_${jobWrapper.job.id}">
			<%-- actions --%>
			<td>
                <cti:button nameKey="edit" renderMode="image" href="${viewScheduleDetailsUrl}" arguments="${jobWrapper.name}"/>
			</td>
			
			<%-- name --%>	
			<td style="white-space:nowrap;">
				<a href="${viewScheduleDetailsUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${jobWrapper.name}"/>">
					${fn:escapeXml(jobWrapper.name)}
				</a>
			</td>
			
			<%-- schedule description --%>
			<td style="white-space:nowrap;">${jobWrapper.scheduleDescription}</td>

            <%-- status --%>
            <td id="status_${jobWrapper.job.id}">
                <span id="jobNotRunningSpan_${jobWrapper.job.id}" <c:if test="${jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                    <cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/STATE_TEXT"/>
                </span>
                <span id="jobRunningSpan_${jobWrapper.job.id}" <c:if test="${not (jobWrapper.jobStatus eq 'RUNNING')}">style="display:none;"</c:if>>
                    <tags:updateableProgressBar totalCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_REQUEST_COUNT_FOR_JOB"
                        countKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_SUCCESS_RESULTS_COUNT_FOR_JOB"
                        failureCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_FAILURE_RESULTS_COUNT_FOR_JOB"
                        borderClasses="scheduledRequestProgressBarBorder" hideCount="true" hidePercent="true"/>
                	<cti:button nameKey="cancel" id="cancel_${jobWrapper.job.id}" styleClass="stopButton" renderMode="image" arguments="${jobWrapper.name}" />
                	<d:confirm on="#cancel_${jobWrapper.job.id}" nameKey="cancelConfirm" argument="${jobWrapper.name}" />
                </span>
            </td>

            <%-- enable/disable --%>
			<c:if test="${canManage}">
                <td style="text-align:right;">
                    <span id="disableSpan_${jobWrapper.job.id}" <c:if test="${jobWrapper.jobStatus eq 'DISABLED' || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                        <tags:widgetActionRefreshImage method="toggleEnabled" jobId="${jobWrapper.job.id}"
                                                       nameKey="disable" arguments="${jobWrapper.name}"/>
                    </span>
                    <span id="enableSpan_${jobWrapper.job.id}" <c:if test="${jobWrapper.jobStatus eq 'ENABLED' || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                        <tags:widgetActionRefreshImage method="toggleEnabled" jobId="${jobWrapper.job.id}"
                                                       nameKey="enable" arguments="${jobWrapper.name}"/>
                    </span>
                </td>
			</c:if>
		</tr>

        <cti:dataUpdaterCallback function="buildTooltipText('status_${jobWrapper.job.id}')" initialize="true"
            tooltip="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_TOOLTIP_TEXT_FOR_JOB" />
		
		<%-- Determines which styles and spans to show based on the job's current status --%>
        <cti:dataUpdaterCallback function="setTrClassByJobState(${jobWrapper.job.id})" initialize="true" state="JOB/${jobWrapper.job.id}/STATE"/>   
	</c:forEach>

</table>
<c:if test="${fn:length(jobWrappers) > 20}">
	</div>
</c:if>

</c:when>

<c:otherwise>
	<i:inline key=".noSchedules"/>
	<br>
</c:otherwise>
</c:choose>

    <div class="additionalSchedulerJobs fl">
        <a href="/group/scheduledGroupRequestExecutionResults/jobs">
        	<c:choose>
				<c:when test="${numAdditionalJobs != null}">
	            	<i:inline key=".viewDetailsWithAdditional" arguments="${numAdditionalJobs}"/>
				</c:when>
				<c:otherwise>
	            	<i:inline key=".viewDetails" />
				</c:otherwise>
        	</c:choose>
        </a>
    </div>

<c:if test="${canManage}">
	<div style="text-align:right;padding-top:5px;">
        <cti:button nameKey="create" type="submit" styleClass="f_blocker"/>
	</div>
</c:if>
</form>
