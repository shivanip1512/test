<%@ attribute name="jobId" required="true" type="java.lang.Integer"%>
<%@ attribute name="linkedInitially" required="true" type="java.lang.Boolean"%>
<%@ attribute name="showInfoPopup" required="false" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:uniqueIdentifier var="id" prefix="scheduledGroupRequestExecutionJobLastRunDateTagId_"/>

<c:if test="${empty showInfoPopup}">
	<c:set var="showInfoPopup" value="true"/>
</c:if>

<script type="text/javascript">

	function setLastRunCreId_${id}() {
		
		return function(data) {

			var creId = data['creId'];

			var lastRunDivWithLinkName = 'lastRunDivWithLink_' + '${id}';
			var lastRunDivWithoutLinkName = 'lastRunDivWithoutLink_' + '${id}';

			if ($(lastRunDivWithLinkName) != undefined && $(lastRunDivWithoutLinkName) != undefined) { // function is called before these may exists, avoid js error
			
				if (creId > 0) {
					$(lastRunDivWithLinkName).show();
					$(lastRunDivWithoutLinkName).hide();
				} else {
					$(lastRunDivWithLinkName).hide();
					$(lastRunDivWithoutLinkName).show();
				}
			}
			
			$('lastRunCommandRequestExecutionId_${id}').value = data['creId'];
        };
	}
	function viewLastRunCommandRequestExecution_${id}() {
		$('lastRunCommandRequestExecutionForm_${id}').submit();
	}

</script>

<form id="lastRunCommandRequestExecutionForm_${id}" action="/spring/amr/commandRequestExecution/detail" method="get">
	<input type="hidden" id="lastRunCommandRequestExecutionId_${id}" name="commandRequestExecutionId" value="">
</form>

<c:choose>
	<c:when test="${jobId > 0}">
	
		<cti:dataUpdaterCallback function="setLastRunCreId_${id}()" initialize="true" creId="COMMAND_REQUEST_EXECUTION/${jobId}/LAST_CRE_ID_FOR_JOB" />
	
		<div id="lastRunDivWithLink_${id}" <c:if test="${not linkedInitially}">style="display:none;"</c:if>>
			<a href="javascript:void(0);" onclick="viewLastRunCommandRequestExecution_${id}();">
				<cti:dataUpdaterValue type="JOB" identifier="${jobId}/LAST_RUN_DATE"/>
			</a>
			
			<c:if test="${showInfoPopup}">	
			<img onclick="$('lastRunLinkInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
		
			<tags:simplePopup id="lastRunLinkInfoPopup" title="Last Run" onClose="$('lastRunLinkInfoPopup').toggle();">
			     <br>
			     View the results of the most recent execution of this job.
			     <br><br>
			     Note: The most recent execution may still be in progress. It's progress will be displayed and updated on the results page.
			     <br><br>
			</tags:simplePopup>
			</c:if>
			
		</div>
		
		<div id="lastRunDivWithoutLink_${id}" <c:if test="${linkedInitially}">style="display:none;"</c:if>>
			<cti:dataUpdaterValue type="JOB" identifier="${jobId}/LAST_RUN_DATE"/>
		</div>
		
	</c:when>
	<c:otherwise>
		N/A
	</c:otherwise>
</c:choose>