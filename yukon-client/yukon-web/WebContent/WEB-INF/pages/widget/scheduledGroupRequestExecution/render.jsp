<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<c:url var="edit" value="/WebConfig/yukon/Icons/edit.gif"/>
<c:url var="editOver" value="/WebConfig/yukon/Icons/edit_over.gif"/>
<c:url var="script" value="/WebConfig/yukon/Icons/script.gif"/>
<c:url var="scriptOver" value="/WebConfig/yukon/Icons/script_over.gif"/>
<c:url var="enable" value="/WebConfig/yukon/Icons/enable.gif"/>
<c:url var="enableOver" value="/WebConfig/yukon/Icons/enable_over.gif"/>
<c:url var="stop" value="/WebConfig/yukon/Icons/stop.gif"/>
<c:url var="stopOver" value="/WebConfig/yukon/Icons/stop_over.gif"/>

<cti:msg var="attributeOrCommandText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.attributeOrCommand"/>
<cti:msg var="scheduleDescriptionText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.scheduleDescription"/>
<cti:msg var="enabledStatusText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.enabledStatus"/>
<cti:msg var="attributeWord" key="yukon.common.device.scheduledGroupRequstExecutionWidget.tableHeader.attributeOrCommand.attribute" />
<cti:msg var="editScheduleTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.editSchedule" />
<cti:msg var="viewScheduleDetailsTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.viewScheduleDetails" />
<cti:msg var="toggleEnabledTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.toggleEnabled" />
<cti:msg var="toggleDisabledTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.toggleDisabled" />
<cti:msg var="toggleDisabledTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.toggleDisabled" />
<cti:msg var="enabledText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.enabled" />
<cti:msg var="disabledText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.disabled" />
<cti:msg var="createButtonText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.createButton" />


<script type="text/javascript">

	function toggleEnabled(toggleEnabledJobId) {

		$('toggleEnabledJobId').value= toggleEnabledJobId;
		${widgetParameters.jsWidget}.doDirectActionRefresh('toggleEnabled');
	}

</script>

<%-- FORMS --%>
<input type="hidden" id="toggleEnabledJobId" name="toggleEnabledJobId" value="">

<form id="createNewSchduleForm" action="/spring/group/scheduledGroupRequestExecution/home" method="get">
</form>




<%-- TABLE --%>
<table class="compactResultsTable">
	
	<tr>
		<th>&nbsp;</th>
		<th>${attributeOrCommandText}</th>
		<th>${scheduleDescriptionText}</th>
		<th style="text-align:center;">${enabledStatusText}</th>
		
	</tr>

	<c:forEach var="jobWrapper" items="${jobWrappers}">
	
		<tr>
			
			<%-- actions --%>	
			<td>

				<%-- edit schedule --%>
				<cti:url var="editScheduleUrl" value="/spring/group/scheduledGroupRequestExecution/home">
					<cti:param name="editJobId" value="${jobWrapper.job.id}"/>
				</cti:url>
				
				<a href="${editScheduleUrl}" title="${editScheduleTitleText}" style="text-decoration:none;">
					<img src="${edit}" onmouseover="javascript:this.src='${editOver}'" onmouseout="javascript:this.src='${edit}'">
				</a>
				&nbsp;&nbsp;
			
				<%-- view details --%>
				<cti:url var="viewScheduleDetailsUrl" value="/spring/group/scheduledGroupRequestExecutionResults/detail" >
					<cti:param name="jobId" value="${jobWrapper.job.id}"/>
				</cti:url>
				
				<a href="${viewScheduleDetailsUrl}" title="${viewScheduleDetailsTitleText}" style="text-decoration:none;" >
					<img src="${script}" onmouseover="javascript:this.src='${scriptOver}'" onmouseout="javascript:this.src='${script}'">
				</a>
				&nbsp;&nbsp;
				
				<%-- enable/disable --%>
				<c:choose>
				
					<c:when test="${jobWrapper.job.disabled}">
					
						<img onclick="toggleEnabled(${jobWrapper.job.id});" 
							title="${toggleEnabledTitleText}" 
							src="${enable}" 
							onmouseover="javascript:this.src='${enableOver}'" 
							onmouseout="javascript:this.src='${enable}'">
					
					</c:when>
					
					<c:otherwise>
					
						<img onclick="toggleEnabled(${jobWrapper.job.id});" 
							title="${toggleDisabledTitleText}" 
							src="${stop}" 
							onmouseover="javascript:this.src='${stopOver}'" 
							onmouseout="javascript:this.src='${stop}'">
					
					</c:otherwise>
				
				</c:choose>
					
			</td>
			
			<%-- attr/cmd --%>	
			<td>
				<c:if test="${not empty jobWrapper.attribute}">
					${jobWrapper.attribute.description} ${attributeWord}
				</c:if>
				<c:if test="${not empty jobWrapper.command}">
					${jobWrapper.command}
				</c:if>
			</td>
			
			<%-- schedule description --%>
			<td>${jobWrapper.scheduleDescription}</td>
			
			<%-- status --%>
			<td style="text-align:center;">
				<c:choose>
					<c:when test="${jobWrapper.job.disabled}">
						<span class="subtleGray">${disabledText}</span>
					</c:when>
					<c:otherwise>
						${enabledText}
					</c:otherwise>
				</c:choose>
			</td>
				
		</tr>
	
	</c:forEach>

</table>

<br>
<div style="text-align: right">
	<tags:slowInput myFormId="createNewSchduleForm" labelBusy="${createButtonText}" label="${createButtonText}"/>
</div>



