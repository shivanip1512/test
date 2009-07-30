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
    	    
			<cti:msg var="schedulesLink" key="yukon.web.menu.config.amr.commandRequestExecution"/>
        	<cti:crumbLink url="/spring/meter/start" title="${schedulesLink}" />
        
    	    <cti:crumbLink title="${pageTitle}"/>
    	    
    	</cti:breadCrumbs>
        
        <%-- JAVASCRIPT --%>
        <script type="text/javascript">
        
        </script>
        
        <h2>${pageTitle}</h2>
        <br>
        
        <%-- ERROR MSG --%>
        <c:if test="${not empty errorMsg}">
        	<div class="errorRed">${errorMsg}</div>
        	<br>
        </c:if>
        
        <form id="scheduledGroupRequestExecutionForm" action="/spring/group/scheduledGroupRequestExecution/schedule" method="post" >
        
        	<%-- REQUEST TYPE --%>
	        <div style="height:115px;">
	        <tags:radioContentSelectorContainer label="${requestTypeLabel}" name="requestType">
	        
	        	<%-- SELECT ATTRIBUTE --%>
	        	<tags:contentSelectorContent selectorName="${attibuteRequestTypeLabel}" selectorValue="SCHEDULED_GROUP_ATTRIBUTE_READ" initiallySelected="${empty requestType || requestType == 'SCHEDULED_GROUP_ATTRIBUTE_READ'}">
	        		<amr:attributeSelector fieldName="attribute" attributes="${allAttributes}" selectedAttribute="${attribute}"/>
	        	</tags:contentSelectorContent>
	        	
	        	<%-- SELECT COMMAND --%>
	        	<tags:contentSelectorContent selectorName="${commandRequestTypeLabel}" selectorValue="SCHEDULED_GROUP_COMMAND" initiallySelected="${requestType == 'SCHEDULED_GROUP_COMMAND'}">
					<amr:commandSelector selectName="commandSelectValue" fieldName="commandString" commands="${commands}" selectedSelectValue="${commandSelectValue}" selectedCommandString="${commandString}"/>  
	        	</tags:contentSelectorContent>
	        
	        </tags:radioContentSelectorContainer>
	        </div>
	        
	        <%-- SCHEDULE --%>
	        <tags:sectionContainer title="${timeFrequencyLabel}">
        			
       			<tags:cronExpressionData id="${cronExpressionTagId}" state="${cronExpressionTagState}"/>
	        	
	        </tags:sectionContainer>
	        
	        <%-- SELECT GROUP --%>
       		<tags:sectionContainer title="${groupLabel}">
       		
        		<cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="dataJson" selectGroupName="${groupName}" selectedNodePathVar="selectedNodePath"/>
	            <ext:nodeValueSelectingInlineTree   fieldId="groupName" 
	                                                fieldName="groupName"
	                                                fieldValue="${groupName}"
	                                                nodeValueName="groupName" 
	                                                multiSelect="false"
	                                                id="selectGroupTree" 
	                                                dataJson="${dataJson}" 
	                                                width="500"
	                                                height="400" 
	                                                highlightNodePath="${selectedNodePath}"
	                                                treeAttributes="{'border':true}" />
       		
       		</tags:sectionContainer>
	        		
	        
	        
	        
	        <c:choose>
	        
	       	 	<%-- SCHDULE MODE --%>
				<c:when test="${editJobId <= 0}">
					
					<tags:slowInput myFormId="scheduledGroupRequestExecutionForm" labelBusy="${scheduleButtonText}" label="${scheduleButtonText}"/>
					
					<%-- RECENT RESULTS --%>
					<br><br>
					<span class="largeBoldLabel">${recentResultLinkLabel}</span> 
					<a href="/spring/group/groupMeterRead/resultsList">${recentResultLink}</a>
			
				</c:when>
				
				<%-- EDIT MODE --%>
				<c:otherwise>
					
					<input type="hidden" name="editJobId" value="${editJobId}">
					
					<tags:slowInput myFormId="scheduledGroupRequestExecutionForm" labelBusy="${updateButtonText}" label="${updateButtonText}"/>
					
				</c:otherwise>
				
			</c:choose>
       
		</form>
            
</cti:standardPage>