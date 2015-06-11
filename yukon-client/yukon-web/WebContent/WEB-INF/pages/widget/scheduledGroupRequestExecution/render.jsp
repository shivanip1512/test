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

<cti:includeScript link="/resources/js/pages/yukon.jobs.js"/>

<cti:url var="submitUrl" value="/group/scheduledGroupRequestExecution/home"/>
<form action="${submitUrl}" method="get">

<cti:msgScope paths="yukon.web.widgets.schedules">

<c:choose>
    <c:when test="${fn:length(jobWrappers) > 0}">
        <div class="scroll-lg">
        
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
                        <c:set var="jobId" value="${jobWrapper.job.id}"/>
                        <tr id="tr_${jobId}" data-schedule-id="${jobId}"
                            data-schedule-name="${fn:escapeXml(jobWrapper.name)}">
                            <%-- name --%>    
                            <td>
                                <a href="${viewScheduleDetailsUrl}" title="<cti:msg2 key=".edit.hoverText"
                                    arguments="${jobWrapper.name}"/>">${fn:escapeXml(jobWrapper.name)}</a>
                            </td>
                            
                            <%-- schedule description --%>
                            <td class="wsnw">${jobWrapper.scheduleDescription}</td>
                
                            <%-- status --%>
                            <td id="status_${jobId}">
                                <div id="jobNotRunningSpan_${jobId}"
                                        <c:if test="${jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                                    <cti:dataUpdaterValue type="JOB" identifier="${jobId}/STATE_TEXT"/>
                                </div>
                                <div id="jobRunningSpan_${jobId}" class="wsnw take16"
                                        <c:if test="${not (jobWrapper.jobStatus eq 'RUNNING')}">style="display:none;"</c:if>>
                                    <tags:updateableProgressBar
                                        totalCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobId}/LAST_REQUEST_COUNT_FOR_JOB"
                                        countKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobId}/LAST_SUCCESS_RESULTS_COUNT_FOR_JOB"
                                        failureCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobId}/LAST_FAILURE_RESULTS_COUNT_FOR_JOB"
                                        containerClasses="progress-sm" 
                                        hideCount="true" 
                                        hidePercent="true"/>
                                    <cti:button id="cancel-job-btn-${jobId}" nameKey="cancel" data-job-id="${jobId}" 
                                        data-ok-event="yukon.job.cancel" classes="js-cancel-job fn M0" renderMode="image" 
                                        arguments="${jobWrapper.name}" icon="icon-cross"/>
                                </div>
                                <d:confirm on="#cancel-job-btn-${jobId}" nameKey="cancelConfirm" argument="${jobWrapper.name}"/>
                            </td>
                
                            <%-- enable/disable --%>
                            <c:if test="${canManage}">
                                <td class="remove-column">
                                    <span id="disableSpan_${jobId}" <c:if test="${jobWrapper.jobStatus eq 'DISABLED'
                                            || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                                        <tags:widgetActionRefreshImage method="toggleEnabled"
                                                                       jobId="${jobId}" 
                                                                       nameKey="enable"
                                                                       arguments="${jobWrapper.name}"
                                                                       icon="icon-disabled"/>
                                    </span>
                                    <span id="enableSpan_${jobId}" <c:if test="${jobWrapper.jobStatus eq 'ENABLED'
                                            || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                                        <tags:widgetActionRefreshImage method="toggleEnabled"
                                                                       jobId="${jobId}" 
                                                                       nameKey="disable"
                                                                       arguments="${jobWrapper.name}"
                                                                       icon="icon-enabled"/>
                                    </span>
                                </td>
                            </c:if>
                        </tr>
                        
                        <cti:dataUpdaterCallback function="yukon.jobs.buildTooltipText('status_${jobId}')" initialize="true"
                            tooltip="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobId}/LAST_TOOLTIP_TEXT_FOR_JOB" />
                        
                        <%-- Determines which styles and spans to show based on the job's current status --%>
                        <cti:dataUpdaterCallback function="yukon.jobs.setTrClassByJobState(${jobId})" initialize="true"
                            state="JOB/${jobId}/STATE"/>
                    </c:forEach>
                </tbody>
            </table>

        </div>
    </c:when>
    <c:otherwise><i:inline key=".noSchedules"/></c:otherwise>
</c:choose>

<div class="action-area">
    <a href="<cti:url value="/group/scheduledGroupRequestExecutionResults/jobs"/>" class="fl">
        <c:choose>
            <c:when test="${numAdditionalJobs != null}">
                <i:inline key=".viewDetailsWithAdditional" arguments="${numAdditionalJobs}"/>
            </c:when>
            <c:otherwise><i:inline key=".viewDetails" /></c:otherwise>
        </c:choose>
    </a>
    <c:if test="${canManage}">
        <cti:button nameKey="create" type="submit" classes="js-blocker fr" icon="icon-plus-green"/>
    </c:if>
</div>

</cti:msgScope>
    
</form>
