<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

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
<cti:msg var="timeFrequencyLabel" key="yukon.common.device.scheduledGroupRequestExecution.home.timeFrequency"/>
<cti:msg var="scheduleButtonText" key="yukon.common.device.scheduledGroupRequestExecution.home.scheduleButton"/>
<cti:msg var="updateButtonText" key="yukon.common.device.scheduledGroupRequestExecution.home.updateButton"/>

<c:choose>
	<c:when test="${editJobId <= 0}">
		<c:set var="pageTitle" value="${normalPageTitle}"/>
	</c:when>
	<c:otherwise>
		<c:set var="pageTitle" value="${editModePageTitle}"/>
	</c:otherwise>
</c:choose>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="commandRequestExecution|createSchedule"/>

		<%-- BREAD CRUMBS --%>
       	<cti:breadCrumbs>
    	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
    	    <cti:crumbLink url="/spring/meter/start" title="Metering" />
    	    <cti:crumbLink title="${pageTitle}"/>
    	</cti:breadCrumbs>
        
        <%-- JAVASCRIPT --%>
        <script type="text/javascript">

        function setSelectedGroupName(btnName, spanName, deviceGroupNameElName) {

            $(spanName).innerHTML = $(deviceGroupNameElName).value;
			$(btnName).value = '${changeGroupText}';
		}
        
        </script>
        
        <style type="text/css">
			.scheduleButtonDiv {border-top:solid 1px #cccccc;padding-top:6px;}
		</style>
        
        <h2>${pageTitle}</h2>
        <br>
        
        <%-- ERROR MSG --%>
        <c:if test="${not empty errorMsg}">
        	<div class="errorRed">${errorMsg}</div>
        	<br>
        </c:if>
        
        <%-- DEVICE GROUP JSON DATA --%>
        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" selectGroupName="${deviceGroupName}" selectedNodePathVar="selectedNodePath" />
        
        <%-- TABS --%>
		<tags:tabbedContentSelector name="requestType">
		        
			<%-- ATTRIBUTE TAB --%>
			<tags:tabbedContentSelectorContent selectorName="${attibuteRequestTypeLabel}" initiallySelected="${empty requestType || requestType == 'SCHEDULED_GROUP_ATTRIBUTE_READ'}">
        		
				<form id="scheduledGroupRequestExecutionForm_attr" action="/spring/group/scheduledGroupRequestExecution/schedule" method="post" >
        		 
        		 	<input type="hidden" name="requestType" value="SCHEDULED_GROUP_ATTRIBUTE_READ">
        		 	<cti:uniqueIdentifier var="cronTagId_attr" prefix="cronTagIdAttr_" />
        		 	<input type="hidden" name="cronTagId" value="${cronTagId_attr}">
        		 
        		 	<tags:nameValueContainer>
        		 	
        		 		<cti:msg var="selectAttributeLabel" key="yukon.common.device.commander.attributeSelector.selectAttribute"/>
        		 		<tags:nameValue name="${selectAttributeLabel}" nameColumnWidth="160px">
        		 			<amr:attributeSelector fieldName="attribute" attributes="${allAttributes}" selectedAttribute="${attribute}" includeDummyOption="true"/>
        		 			<br><br>
        		 		</tags:nameValue>
        		 	
        		 		<tags:nameValue name="${timeFrequencyLabel}">
        		 			<tags:cronExpressionData id="${cronTagId_attr}" state="${cronExpressionTagState}"/>
        		 		</tags:nameValue>
        		 		
        		 		<tags:nameValue name="${groupLabel}">
        		 			
							<span id="deviceGroupNameSpan_attr">${deviceGroupName}</span>
					        
							<c:choose>
								<c:when test="${not empty deviceGroupName}">
									<input type="button" id="selectGroupButton_attr" value="${changeGroupText}">
								</c:when>
								<c:otherwise>
									<input type="button" id="selectGroupButton_attr" value="${chooseGroupText}">
								</c:otherwise>
							</c:choose>
					        
					        <ext:nodeValueSelectingPopupTree    fieldId="deviceGroup_attr"
				                                                fieldName="deviceGroupName"
				                                                fieldValue="${deviceGroupName}"
				                                                nodeValueName="groupName"
				                                                submitButtonText="${selectDeviceGroupChooseText}"
				                                                cancelButtonText="${selectDeviceGroupCancelText}"
				                                                submitCallback="setSelectedGroupName('selectGroupButton_attr', 'deviceGroupNameSpan_attr', 'deviceGroup_attr');"
				                                                
				                                                id="selectGroupTree_attr"
				                                                treeAttributes="{}"
				                                                triggerElement="selectGroupButton_attr"
				                                                dataJson="${groupDataJson}"
				                                                title="${selectDeviceGroupText}"
				                                                width="432"
				                                                height="600" 
				                                                highlightNodePath="${selectedNodePath}"/>
		                                                
        		 		</tags:nameValue>
        		 
        			</tags:nameValueContainer>
        			
        			<br>
					<div class="scheduleButtonDiv">
			        <c:choose>
			       	 	<%-- SCHDULE MODE --%>
						<c:when test="${editJobId <= 0}">
							<tags:slowInput myFormId="scheduledGroupRequestExecutionForm_attr" labelBusy="${scheduleButtonText}" label="${scheduleButtonText}"/>
						</c:when>
						
						<%-- EDIT MODE --%>
						<c:otherwise>
							<input type="hidden" name="editJobId" value="${editJobId}">
							<tags:slowInput myFormId="scheduledGroupRequestExecutionForm_attr" labelBusy="${updateButtonText}" label="${updateButtonText}"/>
						</c:otherwise>
					</c:choose>
					</div>
        	
        		</form>
        	
        	</tags:tabbedContentSelectorContent>
        	
        	
        	
        	<%-- COMMAND TAB --%>
        	<tags:tabbedContentSelectorContent selectorName="${commandRequestTypeLabel}" initiallySelected="${requestType == 'SCHEDULED_GROUP_COMMAND'}">
        	
        		<form id="scheduledGroupRequestExecutionForm_cmd" action="/spring/group/scheduledGroupRequestExecution/schedule" method="post" >
        		 
        		 	<input type="hidden" name="requestType" value="SCHEDULED_GROUP_COMMAND">
        		 	<cti:uniqueIdentifier var="cronTagId_cmd" prefix="cronTagIdCmdr_" />
        		 	<input type="hidden" name="cronTagId" value="${cronTagId_cmd}">
        		 	
        		 	<tags:nameValueContainer>
        		 	
        		 		<cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
        		 		<tags:nameValue name="${selectCommandLabel}" nameColumnWidth="160px">
        		 			<amr:commandSelector selectName="commandSelectValue" fieldName="commandString" commands="${commands}" selectedSelectValue="${commandSelectValue}" selectedCommandString="${commandString}" includeDummyOption="true"/>  
        		 			<br><br>
        		 		</tags:nameValue>
        		 	
        		 		<tags:nameValue name="${timeFrequencyLabel}">
        		 			<tags:cronExpressionData id="${cronTagId_cmd}" state="${cronExpressionTagState}"/>
        		 		</tags:nameValue>
        		 		
        		 		<tags:nameValue name="${groupLabel}">
        		 			
							<span id="deviceGroupNameSpan_cmd">${deviceGroupName}</span>
					        
							<c:choose>
								<c:when test="${not empty deviceGroupName}">
									<input type="button" id="selectGroupButton_cmd" value="${changeGroupText}">
								</c:when>
								<c:otherwise>
									<input type="button" id="selectGroupButton_cmd" value="${chooseGroupText}">
								</c:otherwise>
							</c:choose>
					        
					        <ext:nodeValueSelectingPopupTree    fieldId="deviceGroup_cmd"
				                                                fieldName="deviceGroupName"
				                                                fieldValue="${deviceGroupName}"
				                                                nodeValueName="groupName"
				                                                submitButtonText="${selectDeviceGroupChooseText}"
				                                                cancelButtonText="${selectDeviceGroupCancelText}"
				                                                submitCallback="setSelectedGroupName('selectGroupButton_cmd', 'deviceGroupNameSpan_cmd', 'deviceGroup_cmd');"
				                                                
				                                                id="selectGroupTree_cmd"
				                                                treeAttributes="{}"
				                                                triggerElement="selectGroupButton_cmd"
				                                                dataJson="${groupDataJson}"
				                                                title="${selectDeviceGroupText}"
				                                                width="432"
				                                                height="600" 
				                                                highlightNodePath="${selectedNodePath}"/>
		                                                
        		 		</tags:nameValue>
        		 
        			</tags:nameValueContainer>
        			
        			<br>
        			<div class="scheduleButtonDiv">
			        <c:choose>
			       	 	<%-- SCHDULE MODE --%>
						<c:when test="${editJobId <= 0}">
							<tags:slowInput myFormId="scheduledGroupRequestExecutionForm_cmd" labelBusy="${scheduleButtonText}" label="${scheduleButtonText}"/>
						</c:when>
						
						<%-- EDIT MODE --%>
						<c:otherwise>
							<input type="hidden" name="editJobId" value="${editJobId}">
							<tags:slowInput myFormId="scheduledGroupRequestExecutionForm_cmd" labelBusy="${updateButtonText}" label="${updateButtonText}"/>
						</c:otherwise>
					</c:choose>
					</div>
        	
        		</form>
        	
        	</tags:tabbedContentSelectorContent>
        
        </tags:tabbedContentSelector>
            
</cti:standardPage>