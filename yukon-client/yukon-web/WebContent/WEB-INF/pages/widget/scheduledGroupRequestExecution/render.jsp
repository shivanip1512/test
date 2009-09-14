<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

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
<cti:msg var="toggleEnabledTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.toggleEnabled" />
<cti:msg var="toggleDisabledTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.toggleDisabled" />
<cti:msg var="toggleDisabledTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.toggleDisabled" />
<cti:msg var="deleteTitleText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.delete" />
<cti:msg var="deleteConfirmText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.actions.deleteConfirm" />
<cti:msg var="enabledText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.enabled" />
<cti:msg var="disabledText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.disabled" />
<cti:msg var="createButtonText" key="yukon.common.device.scheduledGroupRequstExecutionWidget.createButton" />

<%-- CREATE NEW SCHEDULE FORM --%>
<form id="createNewSchduleForm_${widgetParameters.widgetId}" action="/spring/group/scheduledGroupRequestExecution/home" method="get">
</form>


<%-- TABLE --%>
<c:choose>
<c:when test="${fn:length(jobWrappers) > 0}">

<table class="compactResultsTable">
	
	<tr>
		<th style="width:70px;">&nbsp;</th>
		<th>${scheduleNameText}</th>
		<th>${scheduleDescriptionText}</th>
		<th style="text-align:center;width:80px;">${enabledStatusText}</th>
		<th style="text-align:right;width:20px;"></th>
	</tr>

	<c:forEach var="jobWrapper" items="${jobWrappers}">
	
		<c:set var="tdClass" value=""/>
		<c:if test="${jobWrapper.job.disabled}">
			<c:set var="tdClass" value="subtleGray"/>
		</c:if>
				
		<tr>
			
			<%-- actions --%>	
			<td>

				<%-- edit schedule --%>
				<cti:url var="editScheduleUrl" value="/spring/group/scheduledGroupRequestExecution/home">
					<cti:param name="editJobId" value="${jobWrapper.job.id}"/>
				</cti:url>
				
				<a href="${editScheduleUrl}" title="${editScheduleTitleText} (${jobWrapper.name})" style="text-decoration:none;">
					<img src="${pencil}" onmouseover="javascript:this.src='${pencilOver}'" onmouseout="javascript:this.src='${pencil}'">
				</a>
				&nbsp;&nbsp;
			
				<%-- view details --%>
				<cti:url var="viewScheduleDetailsUrl" value="/spring/group/scheduledGroupRequestExecutionResults/detail" >
					<cti:param name="jobId" value="${jobWrapper.job.id}"/>
				</cti:url>
				
				<a href="${viewScheduleDetailsUrl}" title="${viewScheduleDetailsTitleText} (${jobWrapper.name})" style="text-decoration:none;" >
					<img src="${script}" onmouseover="javascript:this.src='${scriptOver}'" onmouseout="javascript:this.src='${script}'">
				</a>
					
			</td>
			
			<%-- name --%>	
			<td class="${tdClass}">${jobWrapper.name}</td>
			
			<%-- schedule description --%>
			<td class="${tdClass}">${jobWrapper.scheduleDescription}</td>
			
			<%-- status --%>
			<td class="${tdClass}" style="text-align:center;">
				<c:choose>
					<c:when test="${jobWrapper.job.disabled}">
						<span class="subtleGray">${disabledText}</span>
					</c:when>
					<c:otherwise>
						${enabledText}
					</c:otherwise>
				</c:choose>
			</td>
			
			<%-- delete --%>
			<td class="${tdClass}" style="text-align:right;">
				<tags:widgetActionRefreshImage jobId="${jobWrapper.job.id}" confirmText="${deleteConfirmText}" imgSrc="${delete}" imgSrcHover="${deleteOver}"  title="${deleteTitleText} (${jobWrapper.name})" method="delete"/>
			</td>
				
		</tr>
	
	</c:forEach>

</table>
</c:when>

<c:otherwise>
	${noSchedulesSetupText}
</c:otherwise>
</c:choose>

<div style="text-align:right;padding-top:5px;">
	<tags:slowInput myFormId="createNewSchduleForm_${widgetParameters.widgetId}" labelBusy="${createButtonText}" label="${createButtonText}"/>
</div>



