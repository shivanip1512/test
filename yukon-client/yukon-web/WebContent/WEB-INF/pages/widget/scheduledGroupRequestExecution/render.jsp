<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>

<c:url var="pencil" value="/WebConfig/yukon/Icons/pencil.gif"/>
<c:url var="pencilOver" value="/WebConfig/yukon/Icons/pencil_over.gif"/>
<c:url var="script" value="/WebConfig/yukon/Icons/script.gif"/>
<c:url var="scriptOver" value="/WebConfig/yukon/Icons/script_over.gif"/>
<c:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<c:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>

<cti:msg var="noSchedulesSetupText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.noSchedulesSetup"/>
<cti:msg var="scheduleNameText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.scheduleName"/>
<cti:msg var="attributeOrCommandText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.attributeOrCommand"/>
<cti:msg var="scheduleDescriptionText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.scheduleDescription"/>
<cti:msg var="enabledStatusText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.enabledStatus"/>
<cti:msg var="attributeWord" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.attributeOrCommand.attribute" />
<cti:msg var="editScheduleTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.editSchedule" />
<cti:msg var="viewScheduleDetailsTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.viewScheduleDetails" />
<cti:msg var="deleteTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.delete" />
<cti:msg var="deleteConfirmText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.deleteConfirm" />
<cti:msg var="createButtonText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.createButton" />
<cti:msg var="infoLink" key="yukon.common.device.scheduledGroupRequstExecutionWidget.infoLink" />
<cti:msg var="title" key="yukon.common.device.scheduledGroupRequstExecutionWidget.title" />

<ct:widgetActionPopup method="helpInfo" container="helpInfo" labelBusy="${title}" label="${title}">
	${infoLink}
</ct:widgetActionPopup>

<script type="text/javascript">

	function setTrClassByJobState(jobId) {
	    return function(data) {
	        var trEl = $('tr_' + jobId);
	        var state = data['state'];
	        if (state == 'DISABLED') {
				trEl.className = 'subtleGray';
	        } else if (state == 'RUNNING') {
	        	trEl.className = 'okGreen';
	        } else {
				trEl.className = '';
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
		<th>${scheduleNameText}</th>
		<th>${scheduleDescriptionText}</th>
		<th style="text-align:center;width:80px;">${enabledStatusText}</th>
		<c:if test="${canManage}">
			<th style="text-align:right;width:20px;"></th>
		</c:if>
	</tr>

	<c:forEach var="jobWrapper" items="${jobWrappers}">
	
		<tr id="tr_${jobWrapper.job.id}">
			
			<%-- actions --%>	
			<td>
			
				<cti:dataUpdaterCallback function="setTrClassByJobState(${jobWrapper.job.id})" initialize="true" state="JOB/${jobWrapper.job.id}/STATE"/>
			
				<%-- view details --%>
				<cti:url var="viewScheduleDetailsUrl" value="/spring/group/scheduledGroupRequestExecutionResults/detail" >
					<cti:param name="jobId" value="${jobWrapper.job.id}"/>
				</cti:url>
				
				<a href="${viewScheduleDetailsUrl}" title="${viewScheduleDetailsTitleText} (${jobWrapper.name})" style="text-decoration:none;" >
					<img src="${script}" onmouseover="javascript:this.src='${scriptOver}'" onmouseout="javascript:this.src='${script}'">
				</a>
					
			</td>
			
			<%-- name --%>	
			<td style="white-space:nowrap;">
				<c:choose>
					<c:when test="${canManage}">
						<cti:url var="editScheduleUrl" value="/spring/group/scheduledGroupRequestExecution/home">
							<cti:param name="editJobId" value="${jobWrapper.job.id}"/>
						</cti:url>
						<a href="${editScheduleUrl}" title="${editScheduleTitleText} (${jobWrapper.name})">
							${jobWrapper.name}
						</a>
					</c:when>
					<c:otherwise>
						${jobWrapper.name}
					</c:otherwise>
				</c:choose>
			</td>
			
			<%-- schedule description --%>
			<td style="white-space:nowrap;">${jobWrapper.scheduleDescription}</td>
			
			<%-- status --%>
			<td style="text-align:center;" title="${jobWrapper.job.id}">
				<cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/STATE_TEXT"/>
			</td>
			
			<%-- delete --%>
			<c:if test="${canManage}">
				<td style="text-align:right;">
					<tags:widgetActionRefreshImage jobId="${jobWrapper.job.id}" confirmText="${deleteConfirmText}" imgSrc="${delete}" imgSrcHover="${deleteOver}"  title="${deleteTitleText} (${jobWrapper.name})" method="delete"/>
				</td>
			</c:if>
				
		</tr>
	
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




