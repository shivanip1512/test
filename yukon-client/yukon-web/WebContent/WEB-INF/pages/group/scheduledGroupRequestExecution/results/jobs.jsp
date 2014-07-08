<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="schedules.all">
    
<cti:includeScript link="/JavaScript/yukon.jobs.js"/>
  
<%-- FILTER POPUP --%>
<div id="filter-popup" class="dn" data-title="<cti:msg2 key=".filter.section"/>">
    <cti:flashScopeMessages/>
    <cti:url var="filterUrl" value="/group/scheduledGroupRequestExecutionResults/jobs"/>
    <form:form action="${filterUrl}" method="get" commandName="filter">

        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".filter.dateRange">
                <dt:dateRange startPath="fromDate" endPath="toDate" >
                    <span class="fl" style="margin-right: 5px;"><i:inline key=".filter.dateTo"/></span>
                </dt:dateRange>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".filter.status">
                <label><input type="radio" name="statusFilter" value="ANY"
                    <c:if test="${filter.statusFilter == 'ANY'}">checked</c:if>>
                    <i:inline key=".filter.allWord"/>
                </label>&nbsp;
                <label><input type="radio" name="statusFilter" value="ENABLED_ONLY"
                    <c:if test="${filter.statusFilter == 'ENABLED_ONLY'}">checked</c:if>>
                    <i:inline key=".filter.enabled"/>
                </label>&nbsp;
                <label><input type="radio" name="statusFilter" value="DISABLED_ONLY"
                    <c:if test="${filter.statusFilter == 'DISABLED_ONLY'}">checked</c:if>>
                    <i:inline key=".filter.disabled"/>
                </label>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".filter.excludePending">
                <form:checkbox path="excludePendingFilterBool"/>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".filter.includeOnetime">
                <form:checkbox path="includeOnetimeFilterBool"/>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".filter.type">
                <form:select path="typeFilterAsString">
                    <form:option value="ANY"><cti:msg2 key=".filter.typeAny"/></form:option>
                    <c:forEach var="requestType" items="${scheduledCommandRequestExecutionTypes}">
                        <form:option value="${requestType}" title="${requestType.description}">${requestType.shortName}</form:option>
                    </c:forEach>
                </form:select>
            </tags:nameValue2>
        </tags:nameValueContainer2>

        <div class="action-area">
            <cti:button nameKey="filter" type="submit" classes="primary action"/>
            <cti:button nameKey="clear" href="clear"/>
        </div>
    </form:form>
</div>

<c:set var="controls">
    <a href="javascript:void(0);" popup="#filter-popup" data-popup-toggle>
        <cti:icon icon="icon-filter"/>&nbsp;
        <i:inline key="yukon.common.filter"/>
    </a>
</c:set>
<tags:sectionContainer2 nameKey="tableTitle" controls="${controls}">
    <c:choose>
        <c:when test="${fn:length(filterResult.resultList) == 0}">
            <span class="empty-list">
                <c:choose>
                    <c:when test="${canManage}"><i:inline key=".noJobs.createAccess"/></c:when>
                    <c:otherwise><i:inline key=".noJobs.noCreateAccess"/></c:otherwise>
                </c:choose>
            </span>
        </c:when>
        <c:otherwise>
            <cti:url var="url" value="jobs">
                <cti:param name="fromDate" value="${filter.fromDate}"/>
                <cti:param name="toDate" value="${filter.toDate}"/>
                <cti:param name="statusFilter" value="${filter.statusFilter}"/>
                <cti:param name="excludePendingFilterBool" value="${filter.excludePendingFilterBool}"/>
                <cti:param name="includeOnetimeFilterBool" value="${filter.includeOnetimeFilterBool}"/>
                <cti:param name="typeFilterAsString" value="${filter.typeFilterAsString}"/>
            </cti:url>
            <div data-url="${url}" data-static>
                <table class="compact-results-table">
                    <thead>
                        <tags:sort column="${NAME}"/>
                        <tags:sort column="${DEVICE_GROUP}"/>
                        <tags:sort column="${ATTR_OR_COMM}"/>
                        <tags:sort column="${SCHED_DESC}"/>
                        <tags:sort column="${NEXT_RUN}"/>
                        <tags:sort column="${ENABLED_STATUS}"/>
                        <th class="enabledStatus"><i:inline key=".executions.tableHeader.enabled"/></th>
                    </thead>
                    <tfoot>
                    </tfoot>
                    <tbody>
                        <c:forEach var="jobWrapper" items="${filterResult.resultList}">
                            <cti:msg2 var="rowTitle" key=".jobID" argument="${jobWrapper.job.id}"/>
                            <tr id="tr_${jobWrapper.job.id}" title="${rowTitle}">
                                <td>
                                    <cti:url var="jobDetailUrl" value="/group/scheduledGroupRequestExecutionResults/detail">
                                        <cti:param name="jobId" value="${jobWrapper.job.id}"/>
                                    </cti:url>
                                    <a href="${jobDetailUrl}">${fn:escapeXml(jobWrapper.name)}</a>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty jobWrapper.deviceGroupName}">
                                            <cti:url var="deviceGroupUrl" value="/group/editor/home">
                                                <cti:param name="groupName" value="${jobWrapper.deviceGroupName}"/>
                                            </cti:url>
                                            <a href="${deviceGroupUrl}">${fn:escapeXml(jobWrapper.deviceGroupName)}</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="error"><i:inline key=".groupDoesNotExist"/></span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${not empty jobWrapper.attributes}">
                                        ${jobWrapper.attributeDescriptions} 
                                        <i:inline key=".executions.tableHeader.attributeOrCommand.attribute"/>
                                    </c:if>
                                    <c:if test="${not empty jobWrapper.command}">${jobWrapper.command}</c:if>
                                </td>
                                <td class="runSchedule">${jobWrapper.scheduleDescription}</td>
                                <td class="nextRunDate">
                                    <cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/NEXT_RUN_DATE"/>
                                </td>
                                <td id="status_${jobWrapper.job.id}">
                                    <%-- status --%>
                                    <span id="jobNotRunningSpan_${jobWrapper.job.id}"
                                        <c:if test="${jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                                        <cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/STATE_TEXT"/>
                                    </span>
                                    <div id="jobRunningSpan_${jobWrapper.job.id}"
                                        <c:if test="${not (jobWrapper.jobStatus eq 'RUNNING')}">style="display:none;"</c:if> class="wsnw">
                                        <tags:updateableProgressBar totalCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_REQUEST_COUNT_FOR_JOB"
                                            countKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_SUCCESS_RESULTS_COUNT_FOR_JOB"
                                            failureCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_FAILURE_RESULTS_COUNT_FOR_JOB"
                                            borderClasses="scheduled-request" hideCount="true" hidePercent="true"/>
                                        <cti:button nameKey="cancel" id="cancel_${jobWrapper.job.id}" classes="stopButton fn M0"
                                            renderMode="image" arguments="${jobWrapper.name}" icon="icon-cross"/>
                                        <d:confirm on="#cancel_${jobWrapper.job.id}" nameKey="cancelConfirm" argument="${jobWrapper.name}"/>
                                    </div>
                                </td>
                                <cti:checkRolesAndProperties value="MANAGE_SCHEDULES">
                                    <td class="tar">
                                        <span id="disableSpan_${jobWrapper.job.id}" 
                                            <c:if test="${jobWrapper.jobStatus eq 'DISABLED'
                                                    || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                                                <cti:button nameKey="disable" id="toggle_${jobWrapper.job.id}"
                                                    classes="toggleEnabled fr" renderMode="image" arguments="${jobWrapper.name}"
                                                    icon="icon-disabled"/>
                                        </span>
                                        <span id="enableSpan_${jobWrapper.job.id}" 
                                            <c:if test="${jobWrapper.jobStatus eq 'ENABLED'
                                                    || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                                                <cti:button nameKey="enable" id="toggle_${jobWrapper.job.id}"
                                                    classes="toggleEnabled fr" renderMode="image" arguments="${jobWrapper.name}"
                                                    icon="icon-enabled"/>
                                        </span>
                                    </td>
                                </cti:checkRolesAndProperties>
                            </tr>
                            <cti:dataUpdaterCallback function="buildTooltipText('status_${jobWrapper.job.id}')" initialize="true"
                                tooltip="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_TOOLTIP_TEXT_FOR_JOB"/>
                            <cti:dataUpdaterCallback function="setTrClassByJobState(${jobWrapper.job.id})"
                                initialize="true" state="JOB/${jobWrapper.job.id}/STATE"/>
                        </c:forEach>
                        
                    </tbody>
                </table>
                <tags:pagingResultsControls result="${filterResult}" hundreds="true" adjustPageCount="true"/>
            </div>
        </c:otherwise>
    </c:choose>

    <c:if test="${canManage}">
        <div class="action-area">
            <cti:url var="createUrl" value="/group/scheduledGroupRequestExecution/home"/>
            <cti:button nameKey="create" icon="icon-plus-green" href="${createUrl}"/>
        </div>
    </c:if>
</tags:sectionContainer2>

</cti:standardPage>