<%@ attribute name="jobId" required="true" type="java.lang.Integer"%>
<%@ attribute name="hasStatsInitially" required="true" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msg var="lastResultsSuccessText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.lastResults.success" />
<cti:msg var="lastResultsMissedText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.lastResults.missed" />
<cti:msg var="lastResultsTotalText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.info.lastResults.total" />

<cti:uniqueIdentifier var="id" prefix="scheduledGroupRequestExecutionJobLastRunStatsTagId_"/>

<script type="text/javascript">

	function toggleViewStats_${id}() {

	    //assumes data is of type Hash
		return function(data) {
			var requestCount = data.get('requestCount');
			var statsDivName = 'statsDiv_' + '${id}';
			var noStatsDivName = 'noStatsDiv_' + '${id}';

			if ($(statsDivName) != undefined && $(noStatsDivName) != undefined) { // function is called before these may exists, avoid js error
				if (requestCount > 0) {
					$(statsDivName).show();
					$(noStatsDivName).hide();
				} else {
					$(statsDivName).hide();
					$(noStatsDivName).show();
				}
			}
        };
	}

</script>

<c:choose>
	<c:when test="${jobId > 0}">
	
		<cti:dataUpdaterCallback function="toggleViewStats_${id}()" initialize="true" requestCount="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobId}/LAST_REQUEST_COUNT_FOR_JOB" />
	
		<div id="statsDiv_${id}" <c:if test="${not hasStatsInitially}">style="display:none;"</c:if>>
			<cti:dataUpdaterValue type="SCHEDULED_GROUP_REQUEST_EXECUTION" identifier="${jobId}/LAST_SUCCESS_RESULTS_COUNT_FOR_JOB"/> ${lastResultsSuccessText}
			/ <cti:dataUpdaterValue type="SCHEDULED_GROUP_REQUEST_EXECUTION" identifier="${jobId}/LAST_FAILURE_RESULTS_COUNT_FOR_JOB"/> ${lastResultsMissedText}
			(<cti:dataUpdaterValue type="SCHEDULED_GROUP_REQUEST_EXECUTION" identifier="${jobId}/LAST_REQUEST_COUNT_FOR_JOB"/> ${lastResultsTotalText})
		</div>
		
		<div id="noStatsDiv_${id}" <c:if test="${hasStatsInitially}">style="display:none;"</c:if>>
			N/A
		</div>
		
	</c:when>
	<c:otherwise>
		N/A
	</c:otherwise>
</c:choose>