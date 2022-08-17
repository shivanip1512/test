<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:standardPage module="tools" page="schedules.all">
    
<cti:includeScript link="/resources/js/pages/yukon.jobs.js"/>

<div id="errorMessages" class="error"></div>
  
<%-- FILTER POPUP --%>
<div id="filter-popup" class="dn" data-title="<cti:msg2 key=".filter.section"/>">
    <cti:flashScopeMessages/>
    <cti:url var="filterUrl" value="/group/scheduledGroupRequestExecutionResults/jobs"/>
    <form:form action="${filterUrl}" method="get" modelAttribute="filter">

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
    <a href="javascript:void(0);" data-popup="#filter-popup" data-popup-toggle>
        <cti:icon icon="icon-filter"/>&nbsp;
        <i:inline key="yukon.common.filter"/>
    </a>
</c:set>
<tags:sectionContainer2 nameKey="tableTitle" controls="${controls}">
    <c:choose>
        <c:when test="${empty filterResult.resultList}">
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
                <cti:param name="typeFilterAsString" value="${filter.typeFilterAsString}"/>
            </cti:url>
            <div data-url="${url}" data-static>
                <table id="jobs-table" class="compact-results-table has-actions">
                    <thead>
                        <tags:sort column="${NAME}"/>
                        <tags:sort column="${DEVICE_GROUP}"/>
                        <tags:sort column="${ATTR_OR_COMM}"/>
                        <tags:sort column="${SCHED_DESC}"/>
                        <tags:sort column="${NEXT_RUN}"/>
                        <tags:sort column="${ENABLED_STATUS}"/>
                        <th class="action-column"><cti:checkRolesAndProperties value="MANAGE_SCHEDULES"><cti:icon icon="icon-cog" classes="M0"/></cti:checkRolesAndProperties></th>
                    </thead>
                    <tfoot>
                    </tfoot>
                    <tbody>
                        <c:forEach var="jobWrapper" items="${filterResult.resultList}">
                            <c:set var="jobId" value="${jobWrapper.job.id}"/>
                            <cti:msg2 var="rowTitle" key=".jobID" argument="${jobId}"/>
                            <tr id="tr_${jobId}" title="${rowTitle}" data-job-id="${jobId}">
                                <td>
                                    <cti:url var="jobDetailUrl" value="/group/scheduledGroupRequestExecutionResults/detail">
                                        <cti:param name="jobId" value="${jobId}"/>
                                    </cti:url>
                                    <a href="${jobDetailUrl}">${fn:escapeXml(jobWrapper.shortName)}</a>
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
                                        <i:inline key=".executions.tableHeader.attributeOrCommand.attribute" arguments="${jobWrapper.attributeDescriptions}"/>
                                    </c:if>
                                    <c:if test="${not empty jobWrapper.command}">${fn:escapeXml(jobWrapper.command)}</c:if>
                                </td>
                                <td class="runSchedule">${jobWrapper.scheduleDescription}</td>
                                <td class="nextRunDate">
                                    <cti:dataUpdaterValue type="JOB" identifier="${jobId}/NEXT_RUN_DATE"/>
                                </td>
                                <td id="status_${jobId}">
                                    <%-- status --%>
                                    <span id="jobNotRunningSpan_${jobId}"
                                        <c:if test="${jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                                        <cti:dataUpdaterValue type="JOB" identifier="${jobId}/STATE_TEXT"/>
                                    </span>
                                    <div id="jobRunningSpan_${jobId}"
                                        <c:if test="${not (jobWrapper.jobStatus eq 'RUNNING')}">style="display:none;"</c:if> class="wsnw">
                                        <tags:updateableProgressBar totalCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobId}/LAST_REQUEST_COUNT_FOR_JOB"
                                            countKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobId}/LAST_SUCCESS_RESULTS_COUNT_FOR_JOB"
                                            failureCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobId}/LAST_FAILURE_RESULTS_COUNT_FOR_JOB"
                                            containerClasses="progress-sm" hideCount="true" hidePercent="true"/>
                                    </div>
                                </td>
                                <cti:checkRolesAndProperties value="MANAGE_SCHEDULES">
                                    <td class="tar">
                                        <%@ include file="../scheduleCog.jspf" %>
                                    </td>
                                </cti:checkRolesAndProperties>
                            </tr>
                            <cti:dataUpdaterCallback function="yukon.jobs.buildTooltipText('status_${jobId}')" initialize="true"
                                tooltip="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobId}/LAST_TOOLTIP_TEXT_FOR_JOB"/>
                            <cti:dataUpdaterCallback function="yukon.jobs.setTrClassByJobState(${jobId})"
                                initialize="true" state="JOB/${jobId}/STATE"/>
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