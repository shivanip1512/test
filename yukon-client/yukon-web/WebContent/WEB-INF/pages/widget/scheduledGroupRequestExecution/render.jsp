<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>


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
        
            <table class="compact-results-table has-actions">
                <thead>
                    <th><i:inline key=".tableHeader.scheduleName"/></th>
                    <th><i:inline key=".tableHeader.scheduleDescription"/></th>
                    <th><i:inline key=".tableHeader.status"/></th>
                    <c:if test="${canManage}">
                        <th><!-- the cog column --></th>
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
                                </div>
                            </td>
                
                            <%-- enable/disable --%>
                            <c:if test="${canManage}">
                                <td>
                                    <cti:url var="toggleUrl" value="/group/scheduledGroupRequestExecution/toggleJob" >
                                        <cti:param name="toggleJobId" value="${jobWrapper.job.id}"/>
                                    </cti:url>
                                    <cti:url var="startUrl" value="/group/scheduledGroupRequestExecution/startJob" >
                                        <cti:param name="toggleJobId" value="${jobWrapper.job.id}"/>
                                    </cti:url>
                                    <cti:url var="startDialogUrl" value="/group/scheduledGroupRequestExecution/startDialog" >
                                        <cti:param name="toggleJobId" value="${jobWrapper.job.id}"/>
                                    </cti:url>
                                    <cti:url var="cancelUrl" value="/group/scheduledGroupRequestExecution/cancelJob" >
                                        <cti:param name="toggleJobId" value="${jobWrapper.job.id}"/>
                                    </cti:url>
<%--                                     <cti:url var="createDependentScheduleUrl" value="/group/scheduledGroupRequestExecution/home" >
                                        <cti:param name="editJobId" value="${jobWrapper.job.id}"/>
                                    </cti:url> --%>
                                    <cm:dropdown icon="icon-cog" triggerClasses="fr">
                                        <c:choose>                                        
                                            <c:when test="${jobWrapper.tagState.cronTagStyleType == 'ONETIME'}"> 
                                                <c:if test="${jobWrapper.jobStatus ne 'RUNNING'}">
                                                    <cm:dropdownOption id="startScheduleButton" key="yukon.common.start" data-job-id="${jobId}" icon="icon-bullet-go" data-popup="#startScheduleDialog-${jobId}"/>
                                                    <div class="dn" id="startScheduleDialog-${jobId}" data-dialog data-title="<cti:msg2 key="yukon.web.widgets.schedules.start"/>" data-ok-text="<cti:msg2 key="yukon.common.start"/>"
                                                    data-url="<cti:url value="/group/scheduledGroupRequestExecution/startDialog?jobId=${jobId}"/>" data-job-id="${jobId}" data-event="yukon:schedule:start" data-height="250" data-width="400"></div>
                                                </c:if>
                                            </c:when>
                                            <c:otherwise>
                                                <c:choose>
                                                    <c:when test="${jobWrapper.jobStatus eq 'ENABLED' || jobWrapper.jobStatus eq 'RUNNING'}">
                                                        <cm:dropdownOption disabled="${jobWrapper.jobStatus eq 'RUNNING'}" key="yukon.common.disable" href="${toggleUrl}" icon="icon-disabled"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <cm:dropdownOption key="yukon.common.enable" href="${toggleUrl}" icon="icon-enabled"/>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:if test="${jobWrapper.jobStatus ne 'RUNNING'}">
                                                    <cm:dropdownOption disabled="${jobWrapper.jobStatus eq 'RUNNING'}" key="yukon.common.start" href="${startUrl}" icon="icon-bullet-go"/>
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${jobWrapper.jobStatus eq 'RUNNING'}">
                                            <cm:dropdownOption id="cancel-job-btn-${jobId}" data-ok-event="yukon.schedule.cancel" data-job-id="${jobId}" key="yukon.common.cancel" icon="icon-cross"/>
                                            <d:confirm on="#cancel-job-btn-${jobId}" nameKey="cancelConfirm" argument="${jobWrapper.name}"/>
                                        </c:if>
<%--                                         <cm:dropdownOption key="yukon.web.widgets.schedules.createDependent" href="${createDependentScheduleUrl}" icon="icon-plus-green"/> --%>
                                    </cm:dropdown>
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
