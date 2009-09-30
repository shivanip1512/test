<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.commandRequestExecution.results.list.pageTitle" />
<cti:msg var="filterSectionText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.section" />
<cti:msg var="filerDateFromText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.dateFrom" />
<cti:msg var="filerDateToText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.dateTo" />
<cti:msg var="filterTypeText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.type" />
<cti:msg var="filterTypeAnyText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.typeAny" />
<cti:msg var="filterButtonText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.button" />
<cti:msg var="filterClearText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.clear" />
<cti:msg var="executionsSectionText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.section" />
<cti:msg var="executionsTypeText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.type" />
<cti:msg var="executionsStartTimeText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.startTime" />
<cti:msg var="executionsStopTimeText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.stopTime" />
<cti:msg var="executionsUserText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.user" />
    
<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection=""/>
    
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        
        <%-- cres --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">

		function forwardToCreDetail(row, id) {
			$('cresTable').removeClassName('activeResultsTable');
			window.location = "/spring/common/commandRequestExecutionResults/detail?commandRequestExecutionId=" + id + "&jobId=" + ${jobId};
		}
    
    </script>
    
    <h2>${pageTitle}</h2>
    <br>
    
		
	<%-- FILTER POPUP --%>
    <tags:simplePopup id="filterPopup" title="${filterSectionText}">
	
		<form name="clearForm" id="clearForm" action="/spring/common/commandRequestExecutionResults/list" method="get">
		</form>
	
		<form name="filterForm" id="filterForm" action="/spring/common/commandRequestExecutionResults/list" method="get">
		
			<c:if test="${not empty error}">
				<div class="errorRed">${error}</div><br>
			</c:if>
			
			<input type="hidden" name="commandRequestExecutionId" value="${commandRequestExecutionId}">
			<input type="hidden" name="jobId" value="${jobId}">
			
			<tags:nameValueContainer>
			
				<tags:nameValue name="${filerDateFromText}" nameColumnWidth="100px">
					<cti:formatDate var="fromDateStr" type="DATE" value="${fromDate}" nullText=""/>
					<tags:dateInputCalendar fieldName="fromDate" fieldValue="${fromDateStr}"/>
				</tags:nameValue>
				
				<tags:nameValue name="${filerDateToText}">
					<cti:formatDate var="toDateStr" type="DATE" value="${toDate}" nullText=""/>
					<tags:dateInputCalendar fieldName="toDate" fieldValue="${toDateStr}"/>
				</tags:nameValue>
				
				<c:if test="${empty jobId || jobId == 0}">
				
					<tags:nameValue name="${filterTypeText}">
						<select name="typeFilter">
							<option value="ANY">${filterTypeAnyText}</option>
							<c:forEach var="commandRequestExecutionType" items="${commandRequestExecutionTypes}">
								<option value="${commandRequestExecutionType}" title="${commandRequestExecutionType.description}" <c:if test="${typeFilter eq commandRequestExecutionType}">selected</c:if>>${commandRequestExecutionType.shortName}</option>
							</c:forEach>
						</select>
					</tags:nameValue>
				
				</c:if>
			
			</tags:nameValueContainer>
			
			<br>
			<tags:slowInput myFormId="filterForm" label="${filterButtonText}" labelBusy="${filterButtonText}"/>
			<tags:slowInput myFormId="clearForm" label="${filterClearText}" labelBusy="${filterClearText}"/>
			<br><br>
		
		</form>
		
	</tags:simplePopup>
	
	
	<%-- RESULTS TABLE --%>
	<tags:filterLink popupId="filterPopup"/>
	
	<table id="cresTable" class="resultsTable activeResultsTable">
	
		<tr>
			<th>${executionsTypeText}</th>
			<th>${executionsStartTimeText}</th>
			<th>${executionsStopTimeText}</th>
			<th>${executionsUserText}</th>
		</tr>
	
		<c:forEach var="cre" items="${cres}" varStatus="status">
		
			<tr class="<tags:alternateRow odd="" even="altRow"/>" 
				onclick="forwardToCreDetail(this, ${cre.id})" 
				onmouseover="activeResultsTable_highLightRow(this)" 
				onmouseout="activeResultsTable_unHighLightRow(this)"
				title="${cre.commandRequestExecutionType.description} ID: ${cre.id}">
				
				<td>${cre.commandRequestExecutionType.shortName}</td>
				<td style="white-space:nowrap;"><cti:formatDate type="DATEHM" value="${cre.startTime}"/></td>
				<td style="white-space:nowrap;"><cti:formatDate type="DATEHM" value="${cre.stopTime}" nullText="In Progress"/></td>
				<td>${cre.userName}</td>
				
			</tr>
		
		</c:forEach>
	
	</table>
	
</cti:standardPage>