<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="viewCall.${mode}">
<tags:setFormEditMode mode="${mode}"/>

	<script type="text/javascript">

		var combineDateAndTimeFieldsAndSubmit = function() {
			
		    combineDateAndTimeFields('dateTaken');

            $("callReportUpdateForm").submit();
		}

	</script>
	
	<form id="viewAllForm" action="/spring/stars/operator/callTracking/callList">
		<input type="hidden" name="accountId" value="${accountId}">
	</form>
	
	<form:form id="callReportUpdateForm" commandName="callReport" action="/spring/stars/operator/callTracking/updateCall">
	
		<input type="hidden" name="accountId" value="${accountId}">
	
		<tags:formElementContainer nameKey="callContainer">
	
			<tags:nameValueContainer2>
			
				<form:hidden path="callId"/>
			
				<c:choose>
					<c:when test="${shouldAutoGenerateCallNumber}">
						<form:hidden path="callNumber"/>
					</c:when>
					<c:otherwise>
						<tags:inputNameValue nameKey=".callNumber" path="callNumber"/>
					</c:otherwise>
				</c:choose>
			
				<tags:nameValue2 nameKey=".date">
					<tags:dateTimeInput path="dateTaken" inline="true" fieldValue="${callReport.dateTaken}"/>
				</tags:nameValue2>
				
				<tags:yukonListEntrySelectNameValue nameKey=".type" path="callTypeId" energyCompanyId="${energyCompanyId}" listName="CALL_TYPE"/>
				
				<tags:inputNameValue nameKey=".takenBy" path="takenBy"/>
				
				<tags:textareaNameValue nameKey=".description" path="description" rows="4" cols="35"/>
			
			</tags:nameValueContainer2>
		
		</tags:formElementContainer>
		
		<br>
		
		<%-- buttons --%>
        <cti:displayForPageEditModes modes="EDIT,CREATE">
    		<tags:slowInput2 formId="callReportUpdateForm" key="save" onsubmit="combineDateAndTimeFieldsAndSubmit"/>
        </cti:displayForPageEditModes>
		<tags:slowInput2 formId="viewAllForm" key="cancel"/>
		
	</form:form>
    
</cti:standardPage>