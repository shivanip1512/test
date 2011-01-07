<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<c:url var="script" value="/WebConfig/yukon/Icons/script.gif"/>
<c:url var="scriptOver" value="/WebConfig/yukon/Icons/script_over.gif"/>
<c:url var="enabledImg" value="/WebConfig/yukon/Icons/green_circle.gif"/>
<c:url var="disabledImg" value="/WebConfig/yukon/Icons/gray_circle.gif"/>

<cti:msg var="noSchedulesSetupText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.noSchedulesSetup"/>
<cti:msg var="scheduleNameHeaderText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.scheduleName"/>
<cti:msg var="scheduleDescriptionHeaderText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.scheduleDescription"/>
<cti:msg var="statusHeaderText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.status"/>
<cti:msg var="enabledHeaderText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.enabled"/>
<cti:msg var="editScheduleTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.editSchedule" />
<cti:msg var="viewScheduleDetailsTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.viewScheduleDetails" />
<cti:msg var="createButtonText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.createButton" />
<cti:msg var="enableText" key="yukon.common.enable"/> 
<cti:msg var="disableText" key="yukon.common.disable"/> 

<script type="text/javascript">

	function setTrClassByJobState(jobId) {
	  //assumes data is of type Hash
	    return function(data) {
	        var trEl = $('tr_' + jobId);
	        var state = data.get('state');
	        if (state == 'DISABLED') {
				trEl.className = 'subtleGray';
				$('disableTd_' + jobId).hide();
				$('enableTd_' + jobId).show();
	        } else if (state == 'RUNNING') {
	        	trEl.className = 'okGreen';
	        	$('disableTd_' + jobId).hide();
				$('enableTd_' + jobId).hide();
	        } else if (state == 'ENABLED') {
				trEl.className = '';
                $('disableTd_' + jobId).show();
				$('enableTd_' + jobId).hide();
	        }
	    };
	} 

</script>
    
    
<%-- CREATE NEW SCHEDULE FORM --%>
<form id="createNewSchduleForm_${widgetParameters.widgetId}" action="/spring/group/scheduledGroupRequestExecution/home" method="get">
</form>


<%-- TABLE --%>
<c:choose>
<c:when test="${fn:length(jobWrappers) > 0}">

<c:if test="${fn:length(jobWrappers) > 20}">
	<c:choose>
		<c:when test="${canManage}">
			<div style="overflow:auto; height:500px;">
		</c:when>
		<c:otherwise>
			<div style="overflow:auto; height:390px;">
		</c:otherwise>
	</c:choose>
</c:if>
<table class="compactResultsTable">
	
	<tr>
		<th style="width:20px;">&nbsp;</th>
		<th>${scheduleNameHeaderText}</th>
		<th>${scheduleDescriptionHeaderText}</th>
		<th>${statusHeaderText}</th>
		<c:if test="${canManage}">
			<th style="text-align:right;width:80px;">${enabledHeaderText}</th>
		</c:if>
	</tr>

	<c:forEach var="jobWrapper" items="${jobWrappers}">
	
		<cti:url var="viewScheduleDetailsUrl" value="/spring/group/scheduledGroupRequestExecutionResults/detail" >
			<cti:param name="jobId" value="${jobWrapper.job.id}"/>
		</cti:url>
		
		<tr id="tr_${jobWrapper.job.id}">
			
			<%-- actions --%>
			<td>
			
				<a href="${viewScheduleDetailsUrl}" title="${viewScheduleDetailsTitleText} (${jobWrapper.name})" style="text-decoration:none;" >
					<img src="${script}" onmouseover="javascript:this.src='${scriptOver}'" onmouseout="javascript:this.src='${script}'">
				</a>
			</td>
			
			<%-- name --%>	
			<td style="white-space:nowrap;">
			
				<a href="${viewScheduleDetailsUrl}" title="${viewScheduleDetailsTitleText} (${jobWrapper.name})" >
					${jobWrapper.name}
				</a>
				
			</td>
			
			<%-- schedule description --%>
			<td style="white-space:nowrap;">${jobWrapper.scheduleDescription}</td>
			
			<%-- status --%>
			<td title="${jobWrapper.job.id}">
				<cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/STATE_TEXT"/>
			</td>
			
			<%-- delete --%>
			<c:if test="${canManage}">
			
				<td id="disableTd_${jobWrapper.job.id}" <c:if test="${jobWrapper.jobStatus eq 'DISABLED' || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if> style="text-align:right;">
					<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${enabledImg}" imgSrcHover="${enabledImg}" jobId="${jobWrapper.job.id}" title="${disableText} (${jobWrapper.name})"/>
				</td>
				
				<td id="enableTd_${jobWrapper.job.id}" <c:if test="${jobWrapper.jobStatus eq 'ENABLED' || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if> style="text-align:right;">
					<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${disabledImg}" imgSrcHover="${disabledImg}" jobId="${jobWrapper.job.id}" title="${enableText} (${jobWrapper.name})"/>
				</td>
			</c:if>
					
		</tr>
        <cti:dataUpdaterCallback function="setTrClassByJobState(${jobWrapper.job.id})" initialize="true" state="JOB/${jobWrapper.job.id}/STATE"/>   
	</c:forEach>

</table>
<c:if test="${fn:length(jobWrappers) > 20}">
	</div>
</c:if>

</c:when>

<c:otherwise>
	${noSchedulesSetupText}
</c:otherwise>
</c:choose>

<c:if test="${canManage}">
	<div style="text-align:right;padding-top:5px;">
		<tags:slowInput myFormId="createNewSchduleForm_${widgetParameters.widgetId}" labelBusy="${createButtonText}" label="${createButtonText}"/>
	</div>
</c:if>




