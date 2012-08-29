<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:msg var="normalPageTitle" key="yukon.common.device.scheduledGroupRequestExecution.home.pageTitle"/>
<cti:msg var="editModePageTitle" key="yukon.common.device.scheduledGroupRequestExecution.home.pageTitle.editMode"/>
<cti:msg var="requestTypeLabel" key="yukon.common.device.scheduledGroupRequestExecution.home.requestType"/>
<cti:msg var="attibuteRequestTypeLabel" key="yukon.common.device.scheduledGroupRequestExecution.home.requestType.attribute"/>
<cti:msg var="commandRequestTypeLabel" key="yukon.common.device.scheduledGroupRequestExecution.home.requestType.command"/>
<cti:msg var="groupLabel" key="yukon.common.device.scheduledGroupRequestExecution.home.group"/>
<cti:msg var="chooseGroupText" key="yukon.common.device.scheduledGroupRequestExecution.home.group.chooseGroup"/>
<cti:msg var="changeGroupText" key="yukon.common.device.scheduledGroupRequestExecution.home.group.changeGroup"/>
<cti:msg var="selectDeviceGroupText" key="yukon.common.device.scheduledGroupRequestExecution.home.group.selectDeviceGroup"/>
<cti:msg var="selectDeviceGroupChooseText" key="yukon.common.device.scheduledGroupRequestExecution.home.group.selectDeviceGroupChoose"/>
<cti:msg var="selectDeviceGroupCancelText" key="yukon.common.device.scheduledGroupRequestExecution.home.group.selectDeviceGroupCancel"/>
<cti:msg var="scheduleNameLabel" key="yukon.common.device.scheduledGroupRequestExecution.home.scheduleName"/>
<cti:msg var="timeFrequencyLabel" key="yukon.common.device.scheduledGroupRequestExecution.home.timeFrequency"/>
<cti:msg var="retryLabel" key="yukon.common.device.scheduledGroupRequestExecution.home.retry"/>
<cti:msg var="scheduleStateLabel" key="yukon.common.device.scheduledGroupRequestExecution.home.scheduleState"/>
<cti:msg var="scheduleButtonText" key="yukon.common.device.scheduledGroupRequestExecution.home.scheduleButton"/>
<cti:msg var="updateButtonText" key="yukon.common.device.scheduledGroupRequestExecution.home.updateButton"/>
<cti:msg var="enableJobButtonText" key="yukon.common.device.scheduledGroupRequestExecution.home.enableJobButton" />
<cti:msg var="disableJobButtonText" key="yukon.common.device.scheduledGroupRequestExecution.home.disableJobButton" />
<cti:msg var="disableAndDeleteJobButtonText" key="yukon.common.device.scheduledGroupRequestExecution.home.disableAndDeleteJobButton" />

<c:choose>
	<c:when test="${!editMode}">
		<c:set var="pageTitle" value="${normalPageTitle}"/>
	</c:when>
	<c:otherwise>
		<c:set var="pageTitle" value="${editModePageTitle}"/>
	</c:otherwise>
</c:choose>

<cti:standardPage page="scheduledGroupRequestHome" module="amr">
        
        <%-- JAVASCRIPT --%>
        <script type="text/javascript">
        </script>
        
        <style type="text/css">
			.scheduleButtonDiv {border-top:solid 1px #cccccc;padding-top:6px;}
		</style>
        
        <%-- ERROR MSG --%>
        <c:if test="${not empty errorMsg}">
        	<div class="errorRed">${errorMsg}</div>
        	<br>
        </c:if>
        
        <%-- DEVICE GROUP JSON DATA --%>
        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" selectGroupName="${deviceGroupName}" selectedNodePathVar="selectedNodePath" />
        
        <%-- TOGGLE/DELETE FORMS --%>
        <form id="toggleJobEnabledForm" action="/spring/group/scheduledGroupRequestExecution/toggleJobEnabled" method="post">
			<input type="hidden" name="toggleJobId" value="${editJobId}">
		</form>
		
		<form id="disabledAndDeleteJobForm" action="/spring/group/scheduledGroupRequestExecution/deleteJob" method="post">
			<input type="hidden" name="deleteJobId" value="${editJobId}">
		</form>
							
        <%-- TABS --%>
		<cti:tabbedContentSelector>
		        
			<%-- ATTRIBUTE TAB --%>
			<cti:tabbedContentSelectorContent selectorName="${attibuteRequestTypeLabel}" initiallySelected="${empty requestType || requestType == 'SCHEDULED_GROUP_ATTRIBUTE_READ'}">
        		
				<form id="scheduledGroupRequestExecutionForm_attr" action="/spring/group/scheduledGroupRequestExecution/schedule" method="post" >
        		 
        		 	<input type="hidden" name="editJobId" value="${editJobId}">
        		 	<input type="hidden" name="requestType" value="SCHEDULED_GROUP_ATTRIBUTE_READ">
        		 	<cti:uniqueIdentifier var="formUniqueId" prefix="attrFormUniqueId_" />
        		 	<input type="hidden" name="formUniqueId" value="${formUniqueId}">
        		 	
        		 	<cti:uniqueIdentifier var="cronTagId_attr" prefix="cronTagIdAttr_" />
        		 	<input type="hidden" name="cronTagId" value="${cronTagId_attr}">
        		 
        		 	<tags:nameValueContainer>
        		 	
        		 		<tags:nameValue name="${scheduleNameLabel}">
        		 			<input type="text" name="scheduleName" value="${scheduleName}">
        		 		</tags:nameValue>
        		 		
        		 		<tags:nameValueGap gapHeight="12px"/>
        		 		
        		 		<cti:msg var="selectAttributeLabel" key="yukon.common.device.commander.attributeSelector.selectAttribute"/>
        		 		<tags:nameValue name="${selectAttributeLabel}" nameColumnWidth="160px">
        		 			<amr:attributeSelector fieldName="attribute" attributes="${allGroupedReadableAttributes}" 
                                selectedAttributes="${selectedAttributes}" multipleSize="8" groupItems="true"/>
        		 		</tags:nameValue>
        		 	
        		 		<tags:nameValueGap gapHeight="10px"/>
        		 		
        		 		<tags:nameValue id="attributeDeviceGroups" name="${groupLabel}">
        		 			<tags:deviceGroupNameSelector fieldName="deviceGroupName_${formUniqueId}" fieldValue="${deviceGroupName}" 
												      dataJson="${groupDataJson}" linkGroupName="true" showSelectedDevicesIcon="false"/>
        		 		</tags:nameValue>
        		 		
        		 		<tags:nameValueGap gapHeight="12px"/>
        		 		
        		 		<tags:nameValue name="${timeFrequencyLabel}">
        		 			<tags:cronExpressionData id="${formUniqueId}" state="${cronExpressionTagState}"/>
        		 		</tags:nameValue>
        		 		
        		 		<tags:nameValue name="${retryLabel}">
        		 			<tags:requestRetryOptions retryCheckbox="${retryCheckbox}" queuedRetryCount="${queuedRetryCount}" nonQueuedRetryCount="${nonQueuedRetryCount}" maxTotalRunTimeHours="${maxTotalRunTimeHours}" />
        		 		</tags:nameValue>
        		 		
        		 		<c:if test="${editMode}">
	        		 		<tags:nameValue name="${scheduleStateLabel}">
	        		 			<cti:msg key="yukon.common.device.scheduledGroupRequestExecution.state.${status}"/>
	        		 		</tags:nameValue>
	        		 	</c:if>
        		 		
        			</tags:nameValueContainer>
        			
        		</form>
        			
       			<br>
		        <c:choose>
		       	 	<%-- SCHDULE MODE --%>
					<c:when test="${!editMode}">
						<tags:slowInput myFormId="scheduledGroupRequestExecutionForm_attr" labelBusy="${scheduleButtonText}" label="${scheduleButtonText}" />
					</c:when>
					
					<%-- EDIT MODE --%>
					<c:otherwise>
						
						<tags:slowInput myFormId="scheduledGroupRequestExecutionForm_attr" labelBusy="${updateButtonText}" label="${updateButtonText}" />
						
						<c:if test="${status ne 'RUNNING'}">
						<c:choose>
							<c:when test="${disabled}">
								<tags:slowInput myFormId="toggleJobEnabledForm" labelBusy="${enableJobButtonText}" label="${enableJobButtonText}" />
							</c:when>
							<c:otherwise>
								<tags:slowInput myFormId="toggleJobEnabledForm" labelBusy="${disableJobButtonText}" label="${disableJobButtonText}" />
							</c:otherwise>
						</c:choose>
						</c:if>
						
						<tags:slowInput myFormId="disabledAndDeleteJobForm" labelBusy="${disableAndDeleteJobButtonText}" label="${disableAndDeleteJobButtonText}" />
						
					</c:otherwise>
				</c:choose>
        	
        	</cti:tabbedContentSelectorContent>
        	
        	
        	
        	<%-- COMMAND TAB --%>
        	<cti:tabbedContentSelectorContent selectorName="${commandRequestTypeLabel}" initiallySelected="${requestType == 'SCHEDULED_GROUP_COMMAND'}">
        	
        		<form id="scheduledGroupRequestExecutionForm_cmd" action="/spring/group/scheduledGroupRequestExecution/schedule" method="post" >
        		 
        		 	<input type="hidden" name="editJobId" value="${editJobId}">
        		 	<input type="hidden" name="requestType" value="SCHEDULED_GROUP_COMMAND">
        		 	<cti:uniqueIdentifier var="formUniqueId" prefix="cmdFormUniqueId_" />
        		 	<input type="hidden" name="formUniqueId" value="${formUniqueId}">
        		 	
        		 	<tags:nameValueContainer>
        		 	
        		 		<tags:nameValue name="${scheduleNameLabel}">
        		 			<input type="text" name="scheduleName" value="${scheduleName}">
        		 		</tags:nameValue>
        		 		
        		 		<tags:nameValueGap gapHeight="12px"/>
        		 		
        		 		<cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
        		 		<tags:nameValue name="${selectCommandLabel}" nameColumnWidth="160px">
        		 			<amr:commandSelector selectName="commandSelectValue" fieldName="commandString" commands="${commands}" selectedSelectValue="${commandSelectValue}" selectedCommandString="${commandString}" includeDummyOption="true"/>  
        		 		</tags:nameValue>
        		 		
        		 		<tags:nameValueGap gapHeight="10px"/>
        		 		
        		 		<tags:nameValue id="commandDeviceGroups" name="${groupLabel}">
							<tags:deviceGroupNameSelector fieldName="deviceGroupName_${formUniqueId}" fieldValue="${deviceGroupName}" 
												      dataJson="${groupDataJson}" linkGroupName="true" showSelectedDevicesIcon="false"/>
        		 		</tags:nameValue>
        		 		
        		 		<tags:nameValueGap gapHeight="12px"/>
        		 		
        		 		<tags:nameValue name="${timeFrequencyLabel}">
        		 			<tags:cronExpressionData id="${formUniqueId}" state="${cronExpressionTagState}"/>
        		 		</tags:nameValue>
        		 		
        		 		<tags:nameValue name="${retryLabel}">
        		 			<tags:requestRetryOptions retryCheckbox="${retryCheckbox}" queuedRetryCount="${queuedRetryCount}" nonQueuedRetryCount="${nonQueuedRetryCount}" maxTotalRunTimeHours="${maxTotalRunTimeHours}" />
        		 		</tags:nameValue>
        		 		
        		 		<c:if test="${editMode}">
	        		 		<tags:nameValue name="${scheduleStateLabel}">
	        		 			<cti:msg key="yukon.common.device.scheduledGroupRequestExecution.state.${status}"/>
	        		 		</tags:nameValue>
	        		 	</c:if>
        		 		
        			</tags:nameValueContainer>
        			
        		</form>
        			
       			<br>
		        <c:choose>
		       	 	<%-- SCHDULE MODE --%>
					<c:when test="${!editMode}">
						<tags:slowInput myFormId="scheduledGroupRequestExecutionForm_cmd" labelBusy="${scheduleButtonText}" label="${scheduleButtonText}" />
					</c:when>
					
					<%-- EDIT MODE --%>
					<c:otherwise>
	
						<tags:slowInput myFormId="scheduledGroupRequestExecutionForm_cmd" labelBusy="${updateButtonText}" label="${updateButtonText}" />
						
						<c:if test="${status ne 'RUNNING'}">
						<c:choose>
							<c:when test="${disabled}">
								<tags:slowInput myFormId="toggleJobEnabledForm" labelBusy="${enableJobButtonText}" label="${enableJobButtonText}" />
							</c:when>
							<c:otherwise>
								<tags:slowInput myFormId="toggleJobEnabledForm" labelBusy="${disableJobButtonText}" label="${disableJobButtonText}" />
							</c:otherwise>
						</c:choose>
						</c:if>
						
						<tags:slowInput myFormId="disabledAndDeleteJobForm" labelBusy="${disableAndDeleteJobButtonText}" label="${disableAndDeleteJobButtonText}" />
	
					</c:otherwise>
				</c:choose>
        	
        	</cti:tabbedContentSelectorContent>
        
        </cti:tabbedContentSelector>
            
</cti:standardPage>