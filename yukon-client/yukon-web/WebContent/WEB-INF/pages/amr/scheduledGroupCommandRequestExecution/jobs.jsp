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
<cti:msg var="executionsSectionText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.section" />
<cti:msg var="executionsTypeText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.type" />
<cti:msg var="executionsExecutionCountText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.executionCount" />
<cti:msg var="executionsLastRunText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.lastRun" />
<cti:msg var="executionsNextRunText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.nextRun" />
<cti:msg var="executionsUserText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobs.executions.tableHeader.user" />
    
<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="commandRequestExecution|scheduledGroupCommands"/>
    
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- metering home --%>
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        
        <%-- jobs --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
	<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
	<cti:includeScript link="/JavaScript/extjs_cannon/extGridHelper.js"/>
	<cti:includeScript link="/JavaScript/extjs_cannon/extGridMaker.js"/>
	<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>

    <script type="text/javascript">

    function forwardToJobDetail(row, id) {
		$('jobsTable').removeClassName('activeResultsTable');
		window.location = "/spring/amr/scheduledGroupCommandRequestExecution/detail?jobId=" + id;
	}

    </script>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <%-- FILTERS --%>
	<tags:sectionContainer title="${filterSectionText}" id="filterSection">
	
		<form name="clearForm" id="clearForm" action="/spring/amr/scheduledGroupCommandRequestExecution/list" method="get">
		</form>
		
		<form name="filterForm" action="/spring/amr/scheduledGroupCommandRequestExecution/list" method="get">
		
			<c:if test="${not empty error}">
				<div class="errorRed">${error}</div><br>
			</c:if>
			
			<input type="hidden" name="commandRequestExecutionId" value="${commandRequestExecutionId}">
			<input type="hidden" name="jobId" value="${jobId}">
		
			<table width="50%">
				<tr>
					<td nowrap>
						<cti:formatDate var="fromDateStr" type="DATE" value="${fromDate}" nullText=""/>
						<span class="normalBoldLabel">${filerDateFromText}:</span> 
						<tags:dateInputCalendar fieldName="fromDate" fieldValue="${fromDateStr}"/>
					</td>
					<td nowrap>
						<cti:formatDate var="toDateStr" type="DATE" value="${toDate}" nullText=""/>
						<span class="normalBoldLabel">${filerDateToText}:</span> 
						<tags:dateInputCalendar fieldName="toDate" fieldValue="${toDateStr}"/>
					</td>
					<td nowrap>
						<span class="normalBoldLabel">${filterTypeText}:</span> 
						<select name="typeFilter">
							<option value="ANY">${filterTypeAnyText}</option>
							<c:forEach var="commandRequestExecutionType" items="${scheduledCommandRequestExecutionTypes}">
								<option value="${commandRequestExecutionType}" title="${commandRequestExecutionType.description}" <c:if test="${typeFilter eq commandRequestExecutionType}">selected</c:if>>${commandRequestExecutionType.shortName}</option>
							</c:forEach>
						</select>
					</td>
					<td>
						<tags:slowSubmit label="${filterButtonText}" labelBusy="${filterButtonText}"/>
					</td>
					<td>
						<tags:slowInput myFormId="clearForm" label="${filterClearText}" labelBusy="${filterClearText}"/>
					</td>
				</tr>
			</table>
			<br>
		
		</form>
	
	</tags:sectionContainer>
	
	
	<%-- RESULTS TABLE --%>
	<tags:sectionContainer title="${executionsSectionText}" id="executionsSection">
	
		<table id="jobsTable" class="resultsTable activeResultsTable">
		
			<tr>
				<th>${executionsTypeText}</th>
				<th>${executionsExecutionCountText}</th>
				<th>${executionsLastRunText}</th>
				<th>${executionsNextRunText}</th>
				<th>${executionsUserText}</th>
			</tr>
		
			<c:forEach var="jobWrapper" items="${jobWrappers}">
		    				
				<tr class="<tags:alternateRow odd="" even="altRow"/>" 
					onclick="forwardToJobDetail(this, ${jobWrapper.job.id})" 
					onmouseover="activeResultsTable_highLightRow(this)" 
					onmouseout="activeResultsTable_unHighLightRow(this)"
					title="ID: ${jobWrapper.job.id}">
					
					<td>${jobWrapper.commandRequestTypeShortName}</td>
					<td>${jobWrapper.creCount}</td>
					<td><cti:formatDate type="DATEHM" value="${jobWrapper.lastRun}" nullText="N/A"/></td>
					<td><cti:formatDate type="DATEHM" value="${jobWrapper.nextRun}" nullText="N/A"/></td>
					<td>${jobWrapper.job.userContext.yukonUser.username}</td>
					
				</tr>
			
			</c:forEach>
		
		</table>
	
	</tags:sectionContainer>
	
</cti:standardPage>