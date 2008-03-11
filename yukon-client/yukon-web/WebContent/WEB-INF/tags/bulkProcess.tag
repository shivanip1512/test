<%@ attribute name="process" required="true" type="com.cannontech.common.bulk.BulkProcess"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div>
    <cti:msg var="actionText" key="yukon.common.device.bulk.currentBulkProcesses.action" />
	<cti:msg var="collectionText" key="yukon.common.device.bulk.currentBulkProcesses.collection" />
	<cti:msg var="userText" key="yukon.common.device.bulk.currentBulkProcesses.user" />
	<cti:msg var="startText" key="yukon.common.device.bulk.currentBulkProcesses.start" />
	<cti:msg var="successText" key="yukon.common.device.bulk.currentBulkProcesses.success" />
	<cti:msg var="failText" key="yukon.common.device.bulk.currentBulkProcesses.fail" />
	<cti:msg var="completeText" key="yukon.common.device.bulk.currentBulkProcesses.complete" />
    
    <tags:nameValueContainer style="width: 50%;">
        
        <tags:nameValue name="${actionText}">
			<cti:msg key="${process.actionMessage}" />
        </tags:nameValue>

        <tags:nameValue name="${collectionText}">
			<cti:msg key="${process.deviceCollectionMessage}" />
        </tags:nameValue>

        <tags:nameValue name="${userText}">
			${process.userContext.yukonUser.username}
        </tags:nameValue>

        <tags:nameValue name="${startText}">
			${process.startDate}
        </tags:nameValue>

        <tags:nameValue name="${successText}">
			${process.resultHolder.successfulObjectsProcessedCount}
        </tags:nameValue>

        <tags:nameValue name="${failText}">
			${process.resultHolder.unsuccessfulObjectsProcessedCount}
        </tags:nameValue>

        <tags:nameValue name="${completeText}">
			${process.resultHolder.complete}
        </tags:nameValue>
	
	</tags:nameValueContainer>
	
</div>