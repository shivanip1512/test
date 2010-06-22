<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="multispeak" page="deviceGroupSyncProgress">

	<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>

	<tags:formElementContainer nameKey="progressDetailContainer">
	
		<%-- INFO --%>
		<tags:nameValueContainer2>
		
			<%-- status --%>
			<tags:nameValue2 nameKey=".infoLabel.status">
				<cti:classUpdater type="MSP_DEVICE_GROUP_SYNC" identifier="STATUS_CLASS">
					<cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="STATUS_TEXT"/>
				</cti:classUpdater>
			</tags:nameValue2>
	
			<%-- type --%>
			<tags:nameValue2 nameKey=".infoLabel.type">
				<cti:msg key="${progress.type}"/>
			</tags:nameValue2>
			
			<%-- progress bar --%>
			<tags:nameValue2 nameKey=".infoLabel.progress">
				<tags:updateableProgressBar totalCount="${meterCount}" 
											countKey="MSP_DEVICE_GROUP_SYNC/METERS_PROCESSED_COUNT"  
											isAbortedKey="MSP_DEVICE_GROUP_SYNC/IS_ABORTED" 
											hideCount="true"/>
			</tags:nameValue2>
			
			<%-- stats --%>
			<c:set var="processorTypes" value="${progress.type.processorTypes}"/>
			<c:forEach var="processorType" items="${processorTypes}">
			
				<tags:nameValue2 nameKey=".infoLabel.statsLabel.${processorType}">
					<cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="${processorType}_CHANGE_COUNT"/> <i:inline key=".infoValue.metersAdded"/>
					<br>
					<cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="${processorType}_NO_CHANGE_COUNT"/> <i:inline key=".infoValue.noChange"/>
				</tags:nameValue2>
			
			</c:forEach>
			
		</tags:nameValueContainer2>
		
		<br>
	
		<%-- CANCEL --%>
		<div id="cancelDiv" style="display:none;">
			<form id="cancelForm" action="/spring/multispeak/setup/deviceGroupSync/cancel" method="post">
		   		<tags:slowInput2 key="cancelButton" formId="cancelForm"/>
		   	</form>
	   	</div>
	   	
	   	<%-- DONE BUTTON --%>
		<div id="doneDiv" style="display:none;">
			<form id="doneForm" action="/spring/multispeak/setup/deviceGroupSync/done" method="post">
		   		<tags:slowInput2 key="doneButton" formId="doneForm"/>
		   	</form>
	   	</div>
	
	</tags:formElementContainer>
	
	<cti:dataUpdaterCallback function="toggleElementsWhenTrue(['cancelDiv'], true)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_RUNNING" />
	<cti:dataUpdaterCallback function="toggleElementsWhenTrue(['doneDiv'], false)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_RUNNING" />
	<cti:dataUpdaterCallback function="toggleElementsWhenTrue(['cancelDiv'], false)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_NOT_RUNNING" />
	<cti:dataUpdaterCallback function="toggleElementsWhenTrue(['doneDiv'], true)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/IS_NOT_RUNNING" />

</cti:standardPage>