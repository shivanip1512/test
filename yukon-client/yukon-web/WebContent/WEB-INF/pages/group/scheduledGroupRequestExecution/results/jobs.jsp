<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage page="scheduledGroupRequestAllJobs" module="amr">
    
	<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
	<cti:includeScript link="/JavaScript/scheduledJobs.js"/>

    <script type="text/javascript">
    jQuery(document).ready(function() {
        if (${hasFilterErrors}) {
            adjustDialogSizeAndPosition('filterPopup');
            jQuery('#filterPopup').show();
        }
    });
    </script>
      
    <%-- FILTER POPUP --%>
	<i:simplePopup titleKey=".filter.section" id="filterPopup" styleClass="smallSimplePopup">
        <cti:flashScopeMessages/>
        <form:form id="filterForm" action="/group/scheduledGroupRequestExecutionResults/jobs"
            method="get" commandName="backingBean">
            <tags:sortFields backingBean="${backingBean}" />

            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".filter.dateRange">
                    <dt:dateRange startPath="fromDate" endPath="toDate" >
                    	<i:inline key=".filter.dateTo"/>
                    </dt:dateRange>
                </tags:nameValue2>

                <tags:nameValue2 nameKey=".executions.tableHeader.status.linkText">
                    <label><input type="radio" name="statusFilter" value="ANY"
                        <c:if test="${backingBean.statusFilter == 'ANY'}">checked</c:if>>
                        	<i:inline key=".filter.allWord" />
                    </label>&nbsp;
		    		<label><input type="radio" name="statusFilter" value="ENABLED_ONLY"
                        <c:if test="${backingBean.statusFilter == 'ENABLED_ONLY'}">checked</c:if>>
                        <i:inline key=".filter.enabled" />
                    </label>&nbsp;
		    		<label><input type="radio" name="statusFilter" value="DISABLED_ONLY"
                        <c:if test="${backingBean.statusFilter == 'DISABLED_ONLY'}">checked</c:if>>
                        <i:inline key=".filter.disabled" />
                    </label>
                </tags:nameValue2>

                <tags:nameValue2 nameKey=".filter.excludePending">
                    <form:checkbox path="excludePendingFilterBool" />
                </tags:nameValue2>

                <tags:nameValue2 nameKey=".filter.includeOnetime">
                    <form:checkbox path="includeOnetimeFilterBool" />
                </tags:nameValue2>

                <tags:nameValue2 nameKey=".filter.type">
                    <form:select path="typeFilterAsString">
                        <form:option value="ANY">
                            <cti:msg2 key=".filter.typeAny"/>
                        </form:option>
                        <c:forEach var="requestType" items="${scheduledCommandRequestExecutionTypes}">
                            <form:option value="${requestType}" title="${requestType.description}">
                                ${requestType.shortName}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </tags:nameValue2>
            </tags:nameValueContainer2>

            <div class="actionArea">
                <cti:button nameKey="filter" type="submit" styleClass="f_blocker" />
                <cti:button nameKey="clear" href="clear" styleClass="f_blocker" />
            </div>
        </form:form>
    </i:simplePopup>
	
	<tags:pagedBox2 nameKey="tableTitle" searchResult="${filterResult}" baseUrl="jobs" filterDialog="filterPopup">
	<table id="jobsTable" class="compactResultsTable">
		<tr>
			<th><tags:sortLink nameKey="executions.tableHeader.scheduleName" baseUrl="jobs" fieldName="NAME" isDefault="false" /></th>
			<th><tags:sortLink nameKey="executions.tableHeader.deviceGroup" baseUrl="jobs" fieldName="DEVICE_GROUP" /></th>
			<th><tags:sortLink nameKey="executions.tableHeader.attributeOrCommand" baseUrl="jobs" fieldName="ATTR_OR_COMM" /></th>
			<th><tags:sortLink nameKey="executions.tableHeader.scheduleDescription" baseUrl="jobs" fieldName="SCHED_DESC" /></th>
			<th><tags:sortLink nameKey="executions.tableHeader.nextRun" baseUrl="jobs" fieldName="NEXT_RUN" isDefault="true"/></th>
			<th><tags:sortLink nameKey="executions.tableHeader.status" baseUrl="jobs" fieldName="ENABLED_STATUS" /></th>
			<th class="enabledStatus"><i:inline key=".executions.tableHeader.enabled"/></th>
		</tr>
		<c:forEach var="jobWrapper" items="${filterResult.resultList}">
	    	<c:set var="trClass" value=""/>
			<c:if test="${jobWrapper.job.disabled}">
				<c:set var="trClass" value="subtle"/>
			</c:if>
			<tr id="tr_${jobWrapper.job.id}" class="<tags:alternateRow odd="" even="altRow"/> ${trClass}"
                title="Job ID: ${jobWrapper.job.id}">
				<td>
                    <cti:url var="jobDetailUrl" value="/group/scheduledGroupRequestExecutionResults/detail">
                        <cti:param name="jobId" value="${jobWrapper.job.id}"/>
                    </cti:url>
                    <a href="${jobDetailUrl}">
                        ${jobWrapper.name}
                    </a>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${not empty jobWrapper.deviceGroupName}">
                            <cti:url var="deviceGroupUrl" value="/group/editor/home">
                                <cti:param name="groupName" value="${jobWrapper.deviceGroupName}"/>
                            </cti:url>
                            <a href="${deviceGroupUrl}">
                                ${jobWrapper.deviceGroupName}
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="errorMessage"><i:inline key=".groupDoesNotExist"/></span>
                        </c:otherwise>
                    </c:choose>
                </td>
				<td>
					<c:if test="${not empty jobWrapper.attributes}">
						${jobWrapper.attributeDescriptions} 
                        <i:inline key=".executions.tableHeader.attributeOrCommand.attribute" />
                    </c:if>
					<c:if test="${not empty jobWrapper.command}">
						${jobWrapper.command}
					</c:if>
				</td>
				<td class="runSchedule">${jobWrapper.scheduleDescription}</td>
				<td class="nextRunDate">
                    <cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/NEXT_RUN_DATE"/>
                </td>
				<td id="status_${jobWrapper.job.id}">
                    <%-- status --%>
                    <span id="jobNotRunningSpan_${jobWrapper.job.id}" <c:if test="${jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                        <cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/STATE_TEXT"/>
                    </span>
                    <span id="jobRunningSpan_${jobWrapper.job.id}" <c:if test="${not (jobWrapper.jobStatus eq 'RUNNING')}">style="display:none;"</c:if>>
                        <tags:updateableProgressBar totalCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_REQUEST_COUNT_FOR_JOB"
                            countKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_SUCCESS_RESULTS_COUNT_FOR_JOB"
                            failureCountKey="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_FAILURE_RESULTS_COUNT_FOR_JOB"
                            borderClasses="scheduledRequestProgressBarBorder" hideCount="true" hidePercent="true"/>
                    </span>
				</td>
                <cti:checkRolesAndProperties value="MANAGE_SCHEDULES">
                    <td class="tar">
                        <span id="disableSpan_${jobWrapper.job.id}" 
                            <c:if test="${jobWrapper.jobStatus eq 'DISABLED' || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                                <cti:button nameKey="disable" id="toggle_${jobWrapper.job.id}"
                                    styleClass="toggleEnabled" renderMode="image" arguments="${jobWrapper.name}" />
                        </span>
                        <span id="enableSpan_${jobWrapper.job.id}" 
                            <c:if test="${jobWrapper.jobStatus eq 'ENABLED' || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                                <cti:button nameKey="enable" id="toggle_${jobWrapper.job.id}"
                                    styleClass="toggleEnabled" renderMode="image" arguments="${jobWrapper.name}" />
                        </span>
                    </td>
                </cti:checkRolesAndProperties>
			</tr>
            <cti:dataUpdaterCallback function="buildTooltipText('status_${jobWrapper.job.id}')" initialize="true"
                tooltip="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_TOOLTIP_TEXT_FOR_JOB" />
            <cti:dataUpdaterCallback function="setTrClassByJobState(${jobWrapper.job.id})" initialize="true" state="JOB/${jobWrapper.job.id}/STATE"/>
		</c:forEach>
		
		<c:if test="${fn:length(filterResult.resultList) == 0}">
			<tr>
				<td class="noResults subtle" colspan="7">
					<c:choose>
						<c:when test="${canManage}">
                            <i:inline key=".noJobs.createAccess"/>
						</c:when>
						<c:otherwise>
                            <i:inline key=".noJobs.noCreateAccess"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:if>
	</table>
	</tags:pagedBox2>
</cti:standardPage>