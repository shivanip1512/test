<%@ attribute name="result" required="true" type="com.cannontech.common.bulk.BulkProcessingResultHolder"%>
<%@ attribute name="headerText" required="false" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div>
	<h4>${headerText}</h4>
	
	<div>
		<input type="hidden" id="bulkProcessProgress" value="${results.complete}" />
		Status: ${(results.complete)?'Complete':'In Progress'} ${(results.processingFailed)?' - Failed':''}<br/>
		Total Processed: ${result.totalObjectsProcessedCount}<br/>
		Successful: ${result.successfulObjectsProcessedCount}<br/>
		Unsuccessful: ${result.unsuccessfulObjectsProcessedCount}<br/>
	</div>


	<c:if test="${fn:length(result.mappingExceptionList) > 0}">
		<br/>Mapping Errors:<br/>
		<c:forEach var="mappingError" items="${result.mappingExceptionList}">
			${mappingError.message}<br/>
		</c:forEach>
	</c:if>

	<c:if test="${fn:length(result.processingExceptionList) > 0}">
		<br/>Processing Errors:<br/>
		<c:forEach var="processingError" items="${result.processingExceptionList}">
			${processingError.message}<br/>
		</c:forEach>
	</c:if>
		
	<c:if test="${results.processingFailed}">
		<br/>
		<h4 style="display: inline;">Processing Failed: </h4>${results.failedMessage}
	</c:if>
	
</div>