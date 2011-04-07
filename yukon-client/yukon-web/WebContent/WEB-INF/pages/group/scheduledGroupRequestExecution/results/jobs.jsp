<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.pageTitle" />
<cti:msg var="filterSectionText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.section" />
<cti:msg var="filerDateFromText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.dateFrom" />
<cti:msg var="filerDateToText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.dateTo" />
<cti:msg var="filterTypeText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.type" />
<cti:msg var="filterTypeAnyText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.typeAny" />
<cti:msg var="filterButtonText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.button" />
<cti:msg var="filterClearText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.clear" />
<cti:msg var="filterEnabledText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.enabled" />
<cti:msg var="filterDisabledText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.disabled" />
<cti:msg var="filterAllWordText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.allWord" />
<cti:msg var="filterExcludePendingText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.excludePending" />
<cti:msg var="filterIncludeOnetimeText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.filter.includeOnetime" />
<cti:msg var="executionsSectionText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.section" />
<cti:msg var="scheduleNameText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.scheduleName" />
<cti:msg var="executionsTypeText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.type" />
<cti:msg var="attributeOrCommandText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.attributeOrCommand" />
<cti:msg var="attributeWord" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.attributeOrCommand.attribute" />
<cti:msg var="scheduleDescriptionText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.scheduleDescription" />
<cti:msg var="executionsLastRunText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.lastRun" />
<cti:msg var="executionsNextRunText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.nextRun" />
<cti:msg var="executionsEnabledStatusText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.enabledStatus" />
<cti:msg var="executionsUserText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.user" />  
    
<cti:standardPage page="scheduledGroupRequestAllJobs" module="amr">
    
    <cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
	<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
	<cti:includeScript link="/JavaScript/extjs_cannon/extGridHelper.js"/>
	<cti:includeScript link="/JavaScript/extjs_cannon/extGridMaker.js"/>
	<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>

    <script type="text/javascript">

    function forwardToJobDetail(row, id) {
		$('jobsTable').removeClassName('activeResultsTable');
		window.location = "/spring/group/scheduledGroupRequestExecutionResults/detail?jobId=" + id;
	}

    </script>
      
    <%-- FILTER POPUP --%>
    <tags:simplePopup id="filterPopup" title="${filterSectionText}" on="#filterButton">
     	
		<form name="clearForm" id="clearForm" action="/spring/group/scheduledGroupRequestExecutionResults/jobs" method="get">
   		</form>
   		
   		<form name="filterForm" id="filterForm" action="/spring/group/scheduledGroupRequestExecutionResults/jobs" method="get">
	
		    <tags:nameValueContainer>
		    
		    	<tags:nameValue name="${filerDateFromText}" nameColumnWidth="160px">
		    		<cti:formatDate var="fromDateStr" type="DATE" value="${fromDate}" nullText=""/>
		    		<tags:dateInputCalendar fieldName="fromDateFilter" fieldValue="${fromDateStr}"/>
		    	</tags:nameValue>
		    
		    	<tags:nameValue name="${filerDateToText}">
		    		<cti:formatDate var="toDateStr" type="DATE" value="${toDate}" nullText=""/>
		    		<tags:dateInputCalendar fieldName="toDateFilter" fieldValue="${toDateStr}"/>
		    	</tags:nameValue>
		    	
		    	<tags:nameValue name="${executionsEnabledStatusText}">
		    		<label><input type="radio" name="statusFilter" value="ANY" <c:if test="${statusFilter == 'ANY'}">checked</c:if>> ${filterAllWordText}</label>&nbsp;
		    		<label><input type="radio" name="statusFilter" value="ENABLED_ONLY" <c:if test="${statusFilter == 'ENABLED_ONLY'}">checked</c:if>> ${filterEnabledText}</label>&nbsp;
		    		<label><input type="radio" name="statusFilter" value="DISABLED_ONLY" <c:if test="${statusFilter == 'DISABLED_ONLY'}">checked</c:if>> ${filterDisabledText}</label>
		    	</tags:nameValue>
		    	
		    	<tags:nameValue name="${filterExcludePendingText}">
		    		<input type="checkbox" name="excludePendingFilter" <c:if test="${excludePendingFilter == 'EXECUTED_ONLY'}">checked</c:if>>
		    	</tags:nameValue>
		    	
		    	<tags:nameValue name="${filterIncludeOnetimeText}">
		    		<input type="checkbox" name="includeOnetimeFilter" <c:if test="${includeOnetimeFilter == 'INCLUDE_ONETIME'}">checked</c:if>>
		    	</tags:nameValue>
		    	
		    	<tags:nameValue name="${filterTypeText}">
		    		<select name="typeFilter">
						<option value="ANY">${filterTypeAnyText}</option>
						<c:forEach var="commandRequestExecutionType" items="${scheduledCommandRequestExecutionTypes}">
							<option value="${commandRequestExecutionType}" title="${commandRequestExecutionType.description}" <c:if test="${typeFilter eq commandRequestExecutionType}">selected</c:if>>${commandRequestExecutionType.shortName}</option>
						</c:forEach>
					</select>
		    	</tags:nameValue>
		    
		    </tags:nameValueContainer>
			
			<br>
			<div style="white-space:nowrap;">
			<tags:slowInput myFormId="filterForm" labelBusy="${filterButtonText}" label="${filterButtonText}"/>
			<tags:slowInput myFormId="clearForm" label="${filterClearText}" labelBusy="${filterClearText}"/>
			</div>
		</form>
			
	</tags:simplePopup>	
	
	
	<%-- RESULTS TABLE --%>
    <div style="padding-bottom:5px;">
        <cti:button key="filter" styleClass="navlink" renderMode="labeledImage" id="filterButton" />
    </div>

	<table id="jobsTable" class="resultsTable activeResultsTable">
	
		<tr>
			<th>${scheduleNameText}</th>
			<th>${executionsTypeText}</th>
			<th>${attributeOrCommandText}</th>
			<th>${scheduleDescriptionText}</th>
			<th>${executionsLastRunText}</th>
			<th>${executionsNextRunText}</th>
			<th>${executionsEnabledStatusText}</th>
			<th>${executionsUserText}</th>
		</tr>
		
		
	
		<c:forEach var="jobWrapper" items="${jobWrappers}">
	    				
	    	<c:set var="tdClass" value=""/>
			<c:if test="${jobWrapper.job.disabled}">
				<c:set var="tdClass" value="subtleGray"/>
			</c:if>
		
			<tr class="<tags:alternateRow odd="" even="altRow"/>" 
				onclick="forwardToJobDetail(this, ${jobWrapper.job.id})" 
				onmouseover="activeResultsTable_highLightRow(this)" 
				onmouseout="activeResultsTable_unHighLightRow(this)"
				title="ID: ${jobWrapper.job.id}">
				
				<td class="${tdClass}">${jobWrapper.name}</td>
				<td class="${tdClass}">${jobWrapper.commandRequestTypeShortName}</td>
				<td class="${tdClass}">
					<c:if test="${not empty jobWrapper.attributes}">
						${jobWrapper.attributeDescriptions} ${attributeWord}<c:if test="${fn:length(jobWrapper.attributes) > 1}">s</c:if>
					</c:if>
					<c:if test="${not empty jobWrapper.command}">
						${jobWrapper.command}
					</c:if>
				</td>
				<td class="${tdClass}">${jobWrapper.scheduleDescription}</td>
				<td class="${tdClass}" style="white-space:nowrap;"><cti:formatDate type="DATEHM" value="${jobWrapper.lastRun}" nullText="N/A"/></td>
				<td class="${tdClass}" style="white-space:nowrap;"><cti:formatDate type="DATEHM" value="${jobWrapper.nextRun}" nullText="N/A"/></td>
				<td class="${tdClass}">
					<c:choose>
						<c:when test="${jobWrapper.jobStatus == 'RUNNING'}">
							<c:set var="statusSpanClass" value="okGreen"/>
						</c:when>
						<c:otherwise>
							<c:set var="statusSpanClass" value=""/>
						</c:otherwise>
					</c:choose>
					<span class="${statusSpanClass}">
						<cti:msg key="${jobWrapper.jobStatus.formatKey}"/>
					</span>
				</td>
				<td class="${tdClass}">${jobWrapper.job.userContext.yukonUser.username}</td>
				
			</tr>
		
		</c:forEach>
		
		<c:if test="${fn:length(jobWrappers) == 0}">
			<tr>
				<td colspan="8" style="text-align:center;font-style:italic;" class="subtleGray">
					<c:choose>
						<c:when test="${canManage}">
							<cti:msg key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.noJobs.createAccess" />  
						</c:when>
						<c:otherwise>
							<cti:msg key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.noJobs.noCreateAccess" />  
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:if>
	
	</table>
	
</cti:standardPage>