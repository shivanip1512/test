<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:url var="script" value="/WebConfig/yukon/Icons/script.gif"/>
<c:url var="scriptOver" value="/WebConfig/yukon/Icons/script_over.gif"/>
<c:url var="enabledImg" value="/WebConfig/yukon/Icons/green_circle.gif"/>
<c:url var="disabledImg" value="/WebConfig/yukon/Icons/gray_circle.gif"/>

<script type="text/javascript">

	function setTrClassByJobState(jobId) {
	  //assumes data is of type Hash
	    return function(data) {
	        var trEl = $('tr_' + jobId);
	        var state = data.get('state');
	        if (state == 'DISABLED') {
				trEl.className = 'subtleGray';
                $('disableSpan_' + jobId).hide();
                $('enableSpan_' + jobId).show();

                $('jobRunningSpan_' + jobId).hide();
                $('jobNotRunningSpan_' + jobId).show();
	        } else if (state == 'RUNNING') {
	        	trEl.className = 'okGreen';
                $('disableSpan_' + jobId).hide();
                $('enableSpan_' + jobId).hide();

                $('jobRunningSpan_' + jobId).show();
                $('jobNotRunningSpan_' + jobId).hide();
	        } else if (state == 'ENABLED') {
				trEl.className = '';
                $('disableSpan_' + jobId).show();
                $('enableSpan_' + jobId).hide();

                $('jobRunningSpan_' + jobId).hide();
                $('jobNotRunningSpan_' + jobId).show();
	        }
	    };
	} 

    function buildTooltipText(elementId) {
        //assumes data is of type Hash
        return function(data) {
            var tooltipText = data.get('tooltip');
            setTooltipText(elementId, tooltipText);
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
		<th><i:inline key=".tableHeader.scheduleName"/></th>
		<th><i:inline key=".tableHeader.scheduleDescription"/></th>
		<th><i:inline key=".tableHeader.status"/></th>
		<c:if test="${canManage}">
			<th style="text-align:right;width:80px;"><i:inline key=".tableHeader.enabled"/></th>
		</c:if>
	</tr>

	<c:forEach var="jobWrapper" items="${jobWrappers}">
	
		<cti:url var="viewScheduleDetailsUrl" value="/spring/group/scheduledGroupRequestExecutionResults/detail" >
			<cti:param name="jobId" value="${jobWrapper.job.id}"/>
		</cti:url>
		
		<tr id="tr_${jobWrapper.job.id}">
			
			<%-- actions --%>
			<td>
			    <cti:msg2 var="viewScheduleDetailsTitleText" key=".actions.viewScheduleDetails" />
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
            <td id="status_${jobWrapper.job.id}">
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

            <%-- delete --%>
			<c:if test="${canManage}">
                <td style="text-align:right;">
                    <span id="disableSpan_${jobWrapper.job.id}" <c:if test="${jobWrapper.jobStatus eq 'DISABLED' || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                        <tags:widgetActionRefreshImage2 method="toggleEnabled" imgSrc="${enabledImg}" imgSrcHover="${enabledImg}" jobId="${jobWrapper.job.id}" title=".disable" titleArgument="${jobWrapper.name}"/>
                    </span>
                    <span id="enableSpan_${jobWrapper.job.id}" <c:if test="${jobWrapper.jobStatus eq 'ENABLED' || jobWrapper.jobStatus eq 'RUNNING'}">style="display:none;"</c:if>>
                        <tags:widgetActionRefreshImage2 method="toggleEnabled" imgSrc="${disabledImg}" imgSrcHover="${disabledImg}" jobId="${jobWrapper.job.id}" title=".enable" titleArgument="${jobWrapper.name}"/>
                    </span>
                </td>
			</c:if>
					
		</tr>

        <cti:dataUpdaterCallback function="buildTooltipText('status_${jobWrapper.job.id}')" initialize="true"
            tooltip="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_TOOLTIP_TEXT_FOR_JOB" />

        <cti:dataUpdaterCallback function="setTrClassByJobState(${jobWrapper.job.id})" initialize="true" state="JOB/${jobWrapper.job.id}/STATE"/>   
	</c:forEach>

</table>
<c:if test="${fn:length(jobWrappers) > 20}">
	</div>
</c:if>

</c:when>

<c:otherwise>
	<i:inline key=".noSchedulesSetup"/>
</c:otherwise>
</c:choose>

<c:if test="${canManage}">
	<div style="text-align:right;padding-top:5px;">
		<cti:msg2 var="createButtonText" key=".createButton" />
        <tags:slowInput myFormId="createNewSchduleForm_${widgetParameters.widgetId}" labelBusy="${createButtonText}" label="${createButtonText}"/>
	</div>
</c:if>




