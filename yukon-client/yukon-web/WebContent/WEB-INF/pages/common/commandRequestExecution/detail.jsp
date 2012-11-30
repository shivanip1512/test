<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/amr" prefix="amr" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.commandRequestExecution.results.detail.pageTitle" />
<cti:msg var="infoSectionText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.info.section" />
<cti:msg var="infoTypeText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.info.type" />
<cti:msg var="infoStatusText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.info.status" />
<cti:msg var="infoStartTimeText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.info.startTime" />
<cti:msg var="infoStopTimeText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.info.stopTime" />
<cti:msg var="infoUserText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.info.user" />
<cti:msg var="infoProgressText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.info.progress" />
<cti:msg var="resultsSectionText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.results.section" />
<cti:msg var="resultsSectionPopupInfoText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.results.section.popupInfo" />
<cti:msg var="resultsTotalRequestsText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.results.totalRequests" />
<cti:msg var="resultsSuccessText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.results.success" />
<cti:msg var="resultsFailText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.results.fail" />
<cti:msg var="resultsDetailsText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.results.details" />
<cti:msg var="resultsViewReportText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.results.viewReport" />
<cti:msg var="resultsHideReportText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.results.hideReport" />
<cti:msg var="resultsFailStatsReportText" key="yukon.web.modules.amr.commandRequestExecution.results.detail.results.failStatsReport" />
    
<cti:standardPage title="${pageTitle}" module="blank">

    <cti:standardMenu menuSelection=""/>
    
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        
        <%-- cre list --%>
        <cti:url var="creListUrl" value="/common/commandRequestExecutionResults/list">
            <cti:param name="jobId">${jobId}</cti:param>
        </cti:url>
        <cti:crumbLink url="${creListUrl}" title="Command Request Executions" />
        
        <%-- cre detail --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
	<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>

    <script type="text/javascript">

    	function switchResultsFilterType(resultsFilterType) {

			// init request
			var url = '';
			var params = $H();

			// failure stats report
			if (resultsFilterType == 'FAIL_STATS') {

				url = '/common/commandRequestExecutionResults/failureStatsReport';
				params.set('commandRequestExecutionId', ${commandRequestExecutionId});

			// detail report (success/fail/all)
			} else {

				url = '/common/commandRequestExecutionResults/detailsReport';
				params.set('commandRequestExecutionId', ${commandRequestExecutionId});
				params.set('resultsFilterType', resultsFilterType);
				
			}

			// show indicator, disable select
			$('viewHideDetailsReportButtonIndicator').show();

			// request report
			new Ajax.Updater('detailsReportDiv', url, {
			   'parameters': params,
			   'evalScripts': true,
			   'onSuccess': function(transport, json) {
					$('viewHideDetailsReportButtonIndicator').hide();
					$('detailsReportDiv').show();
			   },
			   'onException': function(e) {
				   $('viewHideDetailsReportButtonIndicator').hide();
				   $('detailsReportDiv').show();
				   $('detailsReportDiv').innerHTML = "Error getting report:" + e.responseText;
			   }
			 });
    	}

		function countUpdateCallback() {
		  //assumes data is of type Hash
			return function(data) {
				var isComplete = data.get('isComplete');
				
				if ($('creProgressBarDiv') != undefined && $('creStopTimeDiv') != undefined) { 
					if (isComplete == 'true') {
						$('creProgressBarDiv').hide();
						$('creStopTimeDiv').show();
					} else if (isComplete == 'false') {
						$('creProgressBarDiv').show();
						$('creStopTimeDiv').hide();
					}

				}
	        };
		}
		
    </script>
    
    <h2 title="ID: ${commandRequestExecutionId}">${pageTitle}</h2>
    <br>
    
    <%-- CRE INFO --%>
    <tags:sectionContainer title="${infoSectionText}" id="creInfoSection">
					
		<tags:nameValueContainer>

			<tags:nameValue name="${infoTypeText}" nameColumnWidth="160px">${cre.commandRequestExecutionType.shortName}</tags:nameValue>
			
			<tags:nameValue name="${infoStatusText}">
				<c:choose>
					<c:when test="${cre.commandRequestExecutionStatus == 'FAILED'}">
						<c:set var="statusSpanClass" value="error"/>
					</c:when>
					<c:when test="${cre.commandRequestExecutionStatus == 'IN_PROGRESS'}">
						<c:set var="statusSpanClass" value="success"/>
					</c:when>
					<c:otherwise>
						<c:set var="statusSpanClass" value=""/>
					</c:otherwise>
				</c:choose>
				<span class="${statusSpanClass}">
					<cti:msg key="${cre.commandRequestExecutionStatus.formatKey}" />
				</span>
			</tags:nameValue>
			
			<tags:nameValue name="${infoStartTimeText}"><cti:formatDate type="DATEHM" value="${cre.startTime}" nullText="N/A"/></tags:nameValue>
			
			<tags:nameValue name="${infoStopTimeText}">
				
				<c:choose>
				
					<c:when test="${cre.commandRequestExecutionStatus == 'FAILED'}">
						N/A
					</c:when>
					
					<c:otherwise>
						
						<cti:dataUpdaterCallback function="countUpdateCallback()" initialize="true" isComplete="COMMAND_REQUEST_EXECUTION/${cre.id}/IS_COMPLETE" />
						
						<div id="creStopTimeDiv" <c:if test="${not isComplete}">style="display:none;"</c:if>>
							<cti:dataUpdaterValue type="COMMAND_REQUEST_EXECUTION" identifier="${cre.id}/STOP_TIME"/>
						</div>
						
						<div id="creProgressBarDiv" <c:if test="${isComplete}">style="display:none;"</c:if>>
							<tags:updateableProgressBar totalCount="${cre.requestCount}" countKey="COMMAND_REQUEST_EXECUTION/${cre.id}/RESULTS_COUNT"/>
						</div>
						
					</c:otherwise>
				</c:choose>
				
			</tags:nameValue>
			
			<tags:nameValue name="${infoUserText}">${cre.userName}</tags:nameValue>
		
		</tags:nameValueContainer>
	
	</tags:sectionContainer>
	
	<br>
	<br>
	
	<%-- RESULTS --%>
	<tags:sectionContainer title="${resultsSectionText}" id="creReusltsSection" helpText="${resultsSectionPopupInfoText}" >
		
		<tags:nameValueContainer>

			<tags:nameValue name="${resultsTotalRequestsText}" nameColumnWidth="160px">
				<amr:commandRequestExecutionResultsCountLink commandRequestExecutionId="${commandRequestExecutionId}" commandRequestExecutionUpdaterType="REQUEST_COUNT" linkedInitially="${requestCount > 0}"/>
			</tags:nameValue>
			
			<tags:nameValue name="${resultsSuccessText}">
				<amr:commandRequestExecutionResultsCountLink commandRequestExecutionId="${commandRequestExecutionId}" commandRequestExecutionUpdaterType="SUCCESS_RESULTS_COUNT" linkedInitially="${successCount > 0}"/>
			</tags:nameValue>
			
			<tags:nameValue name="${resultsFailText}">
				<amr:commandRequestExecutionResultsCountLink commandRequestExecutionId="${commandRequestExecutionId}" commandRequestExecutionUpdaterType="FAILURE_RESULTS_COUNT" linkedInitially="${failCount > 0}"/>
			</tags:nameValue>
			
			<tags:nameValue name="${resultsViewReportText}">
			
				<a href="javascript:void(0);" onclick="switchResultsFilterType('FAIL_STATS');">${resultsFailStatsReportText}</a>
				<c:forEach var="resultsFilterType" items="${resultsFilterTypes}">
					| <a href="javascript:void(0);" onclick="switchResultsFilterType('${resultsFilterType}');">${resultsFilterType.description}</a>
				</c:forEach>
			
				<img id="viewHideDetailsReportButtonIndicator" style="display:none;" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting"> 
			
			</tags:nameValue>
			
		</tags:nameValueContainer>
			
		<div id="detailsReportDiv" style="display:none;">
		</div>
	
	</tags:sectionContainer>
					
</cti:standardPage>