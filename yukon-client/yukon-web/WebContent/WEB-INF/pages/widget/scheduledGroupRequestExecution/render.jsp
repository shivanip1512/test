<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<style>
    .take16 { height:16px; }
</style>

<cti:includeScript link="/JavaScript/scheduledJobs.js"/>

<cti:url var="submitUrl" value="/group/scheduledGroupRequestExecution/home"/>
<form action="${submitUrl}" method="get">

<cti:msgScope paths="yukon.web.widgets.schedules">

<c:choose>
    <c:when test="${fn:length(jobWrappers) > 0}">
        <div class="scroll-large">
        
<table class="compact-results-table">
    <thead>
        <th><i:inline key=".tableHeader.scheduleName"/></th>
        <th><i:inline key=".tableHeader.scheduleDescription"/></th>
        <th><i:inline key=".tableHeader.status"/></th>
        <c:if test="${canManage}">
            <th><!-- the 'enabled' column --></th>
        </c:if>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="jobWrapper" items="${jobWrappers}">
            <cti:url var="viewScheduleDetailsUrl" value="/group/scheduledGroupRequestExecutionResults/detail" >
                <cti:param name="jobId" value="${jobWrapper.job.id}"/>
            </cti:url>
            
            <tr id="tr_${jobWrapper.job.id}" data-schedule-id="${jobWrapper.job.id}"
                data-schedule-name="${fn:escapeXml(jobWrapper.name)}">
                <%-- name --%>    
                <td>
                    <a href="${viewScheduleDetailsUrl}" title="<cti:msg2 key=".edit.hoverText"
                        arguments="${jobWrapper.name}"/>">${fn:escapeXml(jobWrapper.name)}</a>
                </td>
                
                <%-- schedule description --%>
                <td>${jobWrapper.scheduleDescription}</td>
    
                <%-- status --%>
                <td id="status_${jobWrapper.job.id}">
                    <div id="jobNotRunningSpan_${jobWrapper.job.id}"
                            <c:if test="${jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                        <cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/STATE_TEXT"/>
                    </div>
                    <div id="jobRunningSpan_${jobWrapper.job.id}" class="wsnw take16"
                            <c:if test="${not (jobWrapper.jobStatus eq 'RUNNING')}">style="display:none;"</c:if>>
                        <tags:updateableProgressBar
                            totalCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_REQUEST_COUNT_FOR_JOB"
                            countKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_SUCCESS_RESULTS_COUNT_FOR_JOB"
                            failureCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_FAILURE_RESULTS_COUNT_FOR_JOB"
                            borderClasses="scheduled-request" 
                            hideCount="true" 
                            hidePercent="true"/>
                        <cti:button nameKey="cancel" id="cancel_${jobWrapper.job.id}" classes="stopButton fn M0"
                            renderMode="image" arguments="${jobWrapper.name}" icon="icon-cross"/>
                    </div>
                    <d:confirm on="#cancel_${jobWrapper.job.id}" nameKey="cancelConfirm" argument="${jobWrapper.name}" />
                </td>
    
                <%-- enable/disable --%>
                <c:if test="${canManage}">
                    <td class="remove-column">
                        <span id="disableSpan_${jobWrapper.job.id}" <c:if test="${jobWrapper.jobStatus eq 'DISABLED'
                                || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                            <tags:widgetActionRefreshImage method="toggleEnabled"
                                                           jobId="${jobWrapper.job.id}" 
                                                           nameKey="enable"
                                                           arguments="${jobWrapper.name}"
                                                           icon="icon-disabled"/>
                        </span>
                        <span id="enableSpan_${jobWrapper.job.id}" <c:if test="${jobWrapper.jobStatus eq 'ENABLED'
                                || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                            <tags:widgetActionRefreshImage method="toggleEnabled"
                                                           jobId="${jobWrapper.job.id}" 
                                                           nameKey="disable"
                                                           arguments="${jobWrapper.name}"
                                                           icon="icon-enabled"/>
                        </span>
                    </td>
                </c:if>
            </tr>
            
            <cti:dataUpdaterCallback function="buildTooltipText('status_${jobWrapper.job.id}')" initialize="true"
                tooltip="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_TOOLTIP_TEXT_FOR_JOB" />
            
            <%-- Determines which styles and spans to show based on the job's current status --%>
            <cti:dataUpdaterCallback function="setTrClassByJobState(${jobWrapper.job.id})" initialize="true"
                state="JOB/${jobWrapper.job.id}/STATE"/>   
        </c:forEach>
    </tbody>
</table>

        </div>
    </c:when>
    <c:otherwise><i:inline key=".noSchedules"/></c:otherwise>
</c:choose>

<div class="action-area">
    <a href="/group/scheduledGroupRequestExecutionResults/jobs" class="fl">
        <c:choose>
            <c:when test="${numAdditionalJobs != null}">
                <i:inline key=".viewDetailsWithAdditional" arguments="${numAdditionalJobs}"/>
            </c:when>
            <c:otherwise><i:inline key=".viewDetails" /></c:otherwise>
        </c:choose>
    </a>
    <c:if test="${canManage}">
        <cti:button nameKey="create" type="submit" classes="f-blocker fr" icon="icon-plus-green"/>
    </c:if>
</div>

</cti:msgScope>
    
</form>
